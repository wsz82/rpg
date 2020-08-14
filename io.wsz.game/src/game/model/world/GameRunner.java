package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.*;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.wsz.model.sizes.Sizes.TURN_DURATION_MILLIS;

public class GameRunner {
    private static final ArrayDeque<Runnable> laterRunBuffer = new ArrayDeque<>(0);

    private final GameController gameController = GameController.get();
    private final Controller controller = Controller.get();
    private final Set<Location> heroesLocations = new HashSet<>(1);

    private Thread gameThread;

    private final long[] viewStart = new long[1];

    public static void runLater(Runnable laterRunner) {
        laterRunBuffer.addLast(laterRunner);
    }

    public GameRunner() {}

    public void startGame(SaveMemento memento) {
        gameController.setGame(false);

        if (memento == null) {
            loadNewGame();
        } else {
            loadSave(memento);
        }

        loadImages();
    }

    public void resumeGame() {
        gameController.setGame(true);
    }

    private void loadImages() {
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
            long startGameLoop = System.currentTimeMillis();
            while (true) {
                long startGameLoopNext = System.currentTimeMillis();
                long loopDif = startGameLoopNext - startGameLoop;
                if (loopDif > 20) {
                    System.out.println("Loop dif: " + loopDif);
                }
                startGameLoop = startGameLoopNext;

                if (!gameController.isGame()) {
                    continue;
                }
                synchronized (this) {
                    updateModel();
                }

                Platform.runLater(() -> {
                    synchronized (this) {
                        long startNext = System.currentTimeMillis();
                        long viewLoopDif = startNext - viewStart[0];
                        if (viewLoopDif > 30) {
                            System.out.println("View loop dif: " + viewLoopDif);
                        }
                        viewStart[0] = startNext;

                        updateView();

                        long end = System.currentTimeMillis();
                        long dif = end - viewStart[0];
                        if (dif > 20) {
                            System.out.println("View: " + dif);
                        }
                    }
                });

                long endGameLoop = System.currentTimeMillis();
                long dif = endGameLoop - startGameLoop;
                if (dif > 20) {
                    System.out.println("Game loop: " + dif);
                }

                if (dif < TURN_DURATION_MILLIS) {
                    try {
                        Thread.sleep(TURN_DURATION_MILLIS - dif);
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

        for (Location l : controller.getLocationsList()) {
            addItems(l);
            removeItems(l);
        }

        if (!laterRunBuffer.isEmpty()) {
            laterRunBuffer.pop().run();
        }

        if (controller.isInventory() && Settings.isPauseOnInventory()) {
            return;
        }

        Location currentLocation = controller.getCurrentLocation().getLocation();
        List<PosItem> items = currentLocation.getItems().get();

        for (PosItem pi : items) {
            pi.update();
        }

        heroesLocations.clear();
        controller.getHeroes().stream()
                .map(h -> h.getPos().getLocation())
                .collect(Collectors.toCollection(() -> heroesLocations));
        heroesLocations.remove(currentLocation);

        for (Location l : heroesLocations) {
            if (l == null) continue;
            for (PosItem pi : l.getItems().get()) {
                pi.update();
            }
        }

        updateLocation();
        tryToStartDialog();
    }

    private void updateView() {
        if (Sizes.isReloadImages()) {
            loadImages();
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
        gameController.setDialog(true);
    }

    private void addItems(Location l) {
        List<PosItem> p = l.getItemsToAdd();

        if (p.isEmpty()) {
            return;
        }
        l.getItems().get().addAll(p);
        for (PosItem pi : p) {
            pi.getPos().setLocation(l);
            pi.setVisible(true);
        }
        p.clear();
    }

    private void removeItems(Location l) {
        List<PosItem> p = l.getItemsToRemove();
        if (p.isEmpty()) {
            return;
        }
        l.getItems().get().removeAll(p);
        for (PosItem pi : p) {
            pi.setVisible(true);
        }
        p.clear();
    }

    private void updateLocation() {
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
        gameController.loadSaveToLists(memento);
        gameController.initLoadedGameSettings(memento);
        controller.initLoadGameHeroes(memento.getHeroes());
    }

    private void loadNewGame() {
        gameController.loadGameActivePluginToLists();
        gameController.initNewGameSettings();
        controller.initNewGameHeroes();
        controller.setDialogMemento(null);
    }

    private class Loader extends Task<String> {
        @Override
        protected String call() throws Exception {
            List<PosItem> items = controller.getCurrentLocation().getItems();
            Set<Asset> assets = getAssets(items);
            CreatureBase[] bases = CreatureBase.getBases();
            int total = assets.size() + bases.length;
            int i = 0;
            updateProgress(0, total);

            Sizes.FOG.setImage(null);
            Sizes.FOG.getInitialImage();
            i++;
            updateProgress(i, total);

            for (CreatureBase base : bases) {
                base.setImg(null);
                base.getImage();
                i++;
                updateProgress(i, total);
            }

            for (Asset a : assets) {
                reloadAssetImages(a);
                updateProgress(i, total);
                i++;
            }

            return "Completed";
        }

        private Set<Asset> getAssets(List<PosItem> items) {
            Set<Asset> prototypes = items.stream()
                    .map(pi -> (Asset) pi.getPrototype())
                    .collect(Collectors.toSet());

            List<Containable> locationContainers = items.stream()
                    .filter(pi -> pi instanceof Containable)
                    .map(pi -> (Containable) pi)
                    .filter(c -> !c.getItems().isEmpty())
                    .collect(Collectors.toList());
            addInnerAssets(prototypes, locationContainers);
            return prototypes;
        }

        private void addInnerAssets(Set<Asset> prototypes, List<Containable> locationContainers) {
            for (Containable cons : locationContainers) {
                List<Equipment> equipment = cons.getItems();
                Set<Asset> equipmentAssets = equipment.stream()
                        .map(pi -> (Asset) pi.getPrototype())
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

        private void reloadAssetImages(Asset a) {
            a.setImage(null);
            if (a instanceof Creature) {
                Creature c = (Creature) a;
                c.getAnimation().initAllFrames();
            } else {
                a.getInitialImage();
            }
            if (a instanceof Openable) {
                Openable o = (Openable) a;
                o.setOpenImage(null);
                o.getOpenImage();
            }
        }
    }
}
