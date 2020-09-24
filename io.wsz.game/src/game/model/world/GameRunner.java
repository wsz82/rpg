package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import io.wsz.model.Controller;
import io.wsz.model.animation.creature.CreatureBaseAnimationType;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.*;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.FogStatus;
import io.wsz.model.location.FogStatusWithImage;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.textures.CreatureBase;
import io.wsz.model.textures.Fog;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static io.wsz.model.sizes.Sizes.TURN_DURATION_MILLIS;

public class GameRunner {
    private static final ArrayDeque<Runnable> LATER_RUN_BUFFER = new ArrayDeque<>(0);

    private final List<PosItem> tempItemsToAdd = new ArrayList<>(0);

    private final GameController gameController;
    private final Controller controller;
    private final Set<Location> heroesLocations = new HashSet<>(1);
    private final AtomicBoolean areImagesReloaded = new AtomicBoolean(false);

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
                controller.getLogger().logTimeBetweenModelStarts(modelStart);

                if (!gameController.isGame()) {
                    continue;
                }
                synchronized (this) {
                    updateModel();
                }

                Platform.runLater(() -> {
                    synchronized (this) {
                        controller.getLogger().logTimeBetweenViewStarts();
                        updateView();
                        controller.getLogger().logTimeOfViewLoopDuration();
                    }
                });

                long modelEnd = System.currentTimeMillis();
                long modelDif = modelEnd - modelStart;
                controller.getLogger().logTimeOfModelLoopDuration(modelDif);

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

        updateCreaturesControls();

        for (Location l : controller.getLocations()) { //TODO update only locations of a current locations group
            addItems(l);
            removeItems(l);
        }

        if (!LATER_RUN_BUFFER.isEmpty()) {
            LATER_RUN_BUFFER.pop().run();
        }

        if (controller.isInventory() && gameController.getSettings().isPauseOnInventory()) {
            return;
        }

        heroesLocations.clear();
        controller.getHeroes().stream()
                .map(h -> h.getPos().getLocation())
                .collect(Collectors.toCollection(() -> heroesLocations));
        Location currentLocation = controller.getCurrentLocation().getLocation();
        heroesLocations.add(currentLocation);

        for (Location l : heroesLocations) {
            if (l == null) continue;
            List<List<FogStatusWithImage>> discoveredFog = l.getDiscoveredFog();
            if (discoveredFog == null) continue;
            discoveredFog.forEach(r -> r.forEach(f -> {
                            if (f.getStatus() == FogStatus.CLEAR) {
                                f.setStatus(FogStatus.VISITED);
                            }
                        }));
        }

        for (Location l : heroesLocations) { //TODO update locations of a current locations group
            if (l == null) continue;
            l.getItems().forEach(PosItem::update);
        }

        updateHoveredHeroBaseAnimation();

        updateCurrentLocation();
        tryToStartDialog();
    }

    private void updateHoveredHeroBaseAnimation() {
        Creature hoveredHero = gameController.getHoveredHero();
        if (hoveredHero != null) {
            hoveredHero.getBaseAnimationPos().setBaseAnimationType(CreatureBaseAnimationType.ACTION);
        }
    }

    private void updateView() {
        if (Sizes.isReloadImages()) {
            reloadImages();
            areImagesReloaded.set(false);
            Sizes.setReloadImages(false);
        }
        refreshGame();
    }

    private void updateCreaturesControls() {
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
        Creature pc = controller.getDialogPc();
        if (pc == null) {
            return;
        }
        PosItem npc = controller.getAnswering();
        if (npc == null) {
            return;
        }
        Dialog dialog = npc.getDialog();
        if (dialog == null || dialog.getGreetingList() == null) {
            controller.setDialogMemento(null);
            controller.getLogger().logNoAnsweringResponse(npc.getName());
            return;
        }

        gameController.setDialog(true);
    }

    private void addItems(Location location) {
        List<PosItem> itemsToAdd = location.getItemsToAdd();

        if (itemsToAdd.isEmpty()) {
            return;
        }
        List<PosItem> items = location.getItems();
        tempItemsToAdd.clear();
        for (PosItem item : itemsToAdd) {
            boolean itemWillCollide = item.getCollision(location) != null;
            if (itemWillCollide) continue;
            tempItemsToAdd.add(item);
            item.onChangeLocationAction(location);
        }
        items.addAll(tempItemsToAdd);
        itemsToAdd.removeAll(tempItemsToAdd);
    }

    private void removeItems(Location location) {
        List<PosItem> itemsToRemove = location.getItemsToRemove();
        if (itemsToRemove.isEmpty()) {
            return;
        }
        location.getItems().removeAll(itemsToRemove);
        itemsToRemove.clear();
    }

    private void updateCurrentLocation() {
        Location locationToUpdate = controller.getLocationToUpdate();
        if (locationToUpdate != null) {
            controller.setLocationToUpdate(null);
            CurrentLocation cl = controller.getCurrentLocation();
            Location l = cl.getLocation();
            if (!l.getId().equals(locationToUpdate.getId())) {
                cl.setLocation(locationToUpdate);

                Sizes.setReloadImages(true);
            }
        }
    }

    private void refreshGame() {
        if (!areImagesReloaded.get()) return;
        gameController.refreshGame();
    }

    private void loadSave(SaveMemento memento) {
        gameController.restoreSaveMemento(memento);
        gameController.initLoadedGameSettings(memento);
        controller.initLoadGameHeroes(memento.getHeroes());
    }

    private void loadNewGame() {
        gameController.restoreActivePlugin();
        gameController.initNewGameSettings();
        controller.initNewGameHeroes();
        controller.setDialogMemento(null);
        gameController.setDialog(false);
    }

    private class Loader extends Task<String> {
        @Override
        protected String call() throws Exception {
            CurrentLocation currentLocation = controller.getCurrentLocation();
            List<PosItem> items = currentLocation.getItems();
            Set<PosItem> assets = getAssets(items);

            CreatureBase[] bases = CreatureBase.getBases();
            int total = assets.size() + bases.length;
            int i = 0;
            updateProgress(0, total);

            File programDir = controller.getProgramDir();
            gameController.getCursor().initCursorsImages(programDir);

            Fog fog = controller.getFog();
            fog.initAllFogs(programDir);
            i++;
            updateProgress(i, total);

            Location location = currentLocation.getLocation();
            location.initDiscoveredFog(fog, fog.getHalfFogSize());

            for (CreatureBase base : bases) {
                base.initAnimation(programDir);
                i++;
                updateProgress(i, total);
            }

            for (PosItem pi : assets) {
                try {
                    reloadAssetImages(pi, programDir);
                } catch (Exception e) {
                    controller.getLogger().logAssetReloadImagesError(pi.getAssetId());
                    continue;
                }
                updateProgress(i, total);
                i++;
            }

            areImagesReloaded.set(true);
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
            if (pi instanceof Creature) {
                Creature cr = (Creature) pi;
                cr.getPortraitAnimation().initAllAnimations(programDir);
            }
        }
    }
}
