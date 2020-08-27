package game.model.world;

import game.model.GameController;
import game.model.logger.Logger;
import game.model.save.SaveMemento;
import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.*;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.textures.CreatureBase;
import io.wsz.model.textures.Fog;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.wsz.model.sizes.Sizes.TURN_DURATION_MILLIS;

public class GameRunner {
    private static final ArrayDeque<Runnable> LATER_RUN_BUFFER = new ArrayDeque<>(0);

    private final Logger logger = new Logger();
    private final GameController gameController;
    private final Controller controller;
    private final Set<Location> heroesLocations = new HashSet<>(1);

    private Thread gameThread;

    public static void runLater(Runnable laterRunner) {
        LATER_RUN_BUFFER.addLast(laterRunner);
    }

    public GameRunner(GameController gameController) {
        this.gameController = gameController;
        controller = gameController.getController();
    }

    public void startGame(SaveMemento memento) {
        gameController.setGame(false);

        if (memento == null) {
            loadNewGame();
        } else {
            loadSave(memento);
        }

        reloadImages();
    }

    public void resumeGame() {
        gameController.setGame(true);
    }

    private void reloadImages() {
        Sizes.setReloadImages(false);
        gameController.setGame(false);
        Task<String> loader = new Loader();
        gameController.showLoaderView(loader);
        new Thread(loader).start();
        loader.setOnSucceeded(e -> {
            gameController.setGame(true);
            if (gameThread == null) {
                runGameThread();
            }
        });
    }

    private void runGameThread() {
        gameThread = new Thread(() -> {
            while (true) {
                long modelStart = System.currentTimeMillis();
                logger.logTimeBetweenModelStarts(modelStart);

                if (!gameController.isGame()) {
                    continue;
                }
                synchronized (this) {
                    updateModel();
                }

                Platform.runLater(() -> {
                    synchronized (this) {
                        logger.logTimeBetweenViewStarts();
                        updateView();
                        logger.logTimeOfViewLoopDuration();
                    }
                });

                long modelEnd = System.currentTimeMillis();
                long modelDif = modelEnd - modelStart;
                logger.logTimeOfModelLoopDuration(modelDif);

                if (modelDif < TURN_DURATION_MILLIS) {
                    try {
                        Thread.sleep(TURN_DURATION_MILLIS - modelDif);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void updateModel() {
        if (gameController.isDialog()) {
            return;
        }

        updateControls();

        for (Location l : controller.getLocations()) { //TODO update only locations of a current locations group
            addItems(l);
            removeItems(l);
        }

        if (!LATER_RUN_BUFFER.isEmpty()) {
            LATER_RUN_BUFFER.pop().run();
        }

        if (controller.isInventory() && Settings.isPauseOnInventory()) {
            return;
        }

        Location currentLocation = controller.getCurrentLocation().getLocation();
        List<PosItem> items = currentLocation.getItems();

        for (PosItem pi : items) {
            pi.update();
        }

        heroesLocations.clear();
        controller.getHeroes().stream()
                .map(h -> h.getPos().getLocation())
                .collect(Collectors.toCollection(() -> heroesLocations));
        heroesLocations.remove(currentLocation);

        for (Location l : heroesLocations) { //TODO update locations of a current locations group
            if (l == null) continue;
            for (PosItem pi : l.getItems()) {
                pi.update();
            }
        }

        updateCurrentLocation();
        tryToStartDialog();
    }

    private void updateView() {
        if (Sizes.isReloadImages()) {
            reloadImages();
            Sizes.setReloadImages(false);
        }
        refreshGame();
    }

    private void updateControls() {
        List<Creature> creaturesToLooseControl = controller.getCreaturesToLooseControl();
        if (!creaturesToLooseControl.isEmpty()) {
            for (Creature cr : creaturesToLooseControl) {
                cr.setControl(CreatureControl.CONTROLLABLE);
            }
            creaturesToLooseControl.clear();
        }
        List<Creature> creaturesToControl = controller.getCreaturesToControl();
        if (!creaturesToControl.isEmpty()) {
            for (Creature cr : creaturesToControl) {
                cr.setControl(CreatureControl.CONTROL);
            }
            creaturesToControl.clear();
        }
    }

    private void tryToStartDialog() {
        PosItem asking = controller.getAsking();
        if (asking == null) {
            return;
        }
        PosItem answering = controller.getAnswering();
        if (answering == null) {
            return;
        }
        Dialog dialog = answering.getDialog();
        if (dialog == null || dialog.getGreeting() == null) {
            controller.setAsking(null);
            controller.setAnswering(null);
            logger.logNoAnsweringResponse(answering);
            return;
        }

        gameController.setDialog(true);
    }

    private void addItems(Location location) {
        List<PosItem> itemsToAdd = location.getItemsToAdd();

        if (itemsToAdd.isEmpty()) {
            return;
        }
        location.getItems().addAll(itemsToAdd);
        for (PosItem pi : itemsToAdd) {
            pi.getPos().setLocation(location);
            pi.setVisible(true);
        }
        itemsToAdd.clear();
    }

    private void removeItems(Location location) {
        List<PosItem> itemsToRemove = location.getItemsToRemove();
        if (itemsToRemove.isEmpty()) {
            return;
        }
        location.getItems().removeAll(itemsToRemove);
        for (PosItem pi : itemsToRemove) {
            pi.setVisible(true);
        }
        itemsToRemove.clear();
    }

    private void updateCurrentLocation() {
        Location locationToUpdate = controller.getLocationToUpdate();
        if (locationToUpdate != null) {
            controller.setLocationToUpdate(null);
            CurrentLocation cl = controller.getCurrentLocation();
            Location l = cl.getLocation();
            if (!l.getName().equals(locationToUpdate.getName())) {
                cl.setLocation(locationToUpdate);

                Sizes.setReloadImages(true);
            }
        }
    }

    private void refreshGame() {
        gameController.refreshGame();
    }

    private void loadSave(SaveMemento memento) {
        gameController.restoreMemento(memento);
        gameController.initLoadedGameSettings(memento);
        controller.initLoadGameHeroes(memento.getHeroes());
    }

    private void loadNewGame() {
        gameController.restoreActivePlugin();
        gameController.initNewGameSettings();
        controller.initNewGameHeroes();
        controller.setDialogMemento(null);
    }

    private class Loader extends Task<String> {
        @Override
        protected String call() throws Exception {
            List<PosItem> items = controller.getCurrentLocation().getItems();
            Set<PosItem> assets = getAssets(items);
            CreatureBase[] bases = CreatureBase.getBases();
            int total = assets.size() + bases.length;
            int i = 0;
            updateProgress(0, total);

            Fog fog = gameController.getFog();
            fog.setImage(null);
            File programDir = controller.getProgramDir();
            fog.getImage(programDir);
            i++;
            updateProgress(i, total);

            for (CreatureBase base : bases) {
                base.setImg(null);
                base.getImage(programDir);
                i++;
                updateProgress(i, total);
            }

            for (PosItem pi : assets) {
                try {
                    reloadAssetImages(pi, programDir);
                } catch (Exception e) {
                    System.out.println("Error with " + pi.getName());
                    continue;
                }
                updateProgress(i, total);
                i++;
            }

            return "Completed";
        }

        private Set<PosItem> getAssets(List<PosItem> items) {
            Set<PosItem> prototypes = new HashSet<>();
            for (PosItem item : items) {
                PosItem prototype = item.getPrototype();
                prototypes.add(prototype);
            }

            List<Containable> locationContainers = items.stream()
                    .filter(pi -> pi instanceof Containable)
                    .map(pi -> (Containable) pi)
                    .filter(c -> !c.getItems().isEmpty())
                    .collect(Collectors.toList());
            addInnerAssets(prototypes, locationContainers);
            return prototypes;
        }

        private void addInnerAssets(Set<PosItem> prototypes, List<Containable> locationContainers) {
            for (Containable cons : locationContainers) {
                List<Equipment> equipment = cons.getItems();
                Set<PosItem> equipmentAssets = equipment.stream()
                        .map(pi -> pi.getPrototype())
                        .collect(Collectors.toSet());
                prototypes.addAll(equipmentAssets);
                List<Containable> containables = equipment.stream()
                        .filter(pi -> pi instanceof Containable)
                        .map(pi -> (Containable) pi)
                        .filter(c -> !c.getItems().isEmpty())
                        .collect(Collectors.toList());
                addInnerAssets(prototypes, containables);
            }
        }

        private void reloadAssetImages(PosItem pi, File programDir) {
            pi.getAnimation().initAllAnimations(programDir);
        }
    }
}
