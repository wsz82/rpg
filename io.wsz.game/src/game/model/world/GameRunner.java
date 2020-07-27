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
            while (true) {
                if (!gameController.isGame()) {
                    continue;
                }
                synchronized (this) {
                    updateModel();
                }

                try {
                    Thread.sleep(TURN_DURATION_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(() -> {
                    synchronized (this) {
                        updateView();
                    }
                });

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
        showGame();
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

    private void showGame() {
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
    }

    private class Loader extends Task<String> {
        @Override
        protected String call() throws Exception {
            List<PosItem> items = controller.getCurrentLocation().getItems();
            Set<Asset> assets = getAssets(items);
            int total = assets.size() - 1;
            updateProgress(0, total);

            int i = 0;
            for (Asset a : assets) {
                reloadAssetImages(a);
                updateProgress(i, total);
                i++;
            }

            return "Completed";
        }

        private Set<Asset> getAssets(List<PosItem> items) {
            Set<Asset> assets = new HashSet<>(1);
            Set<Asset> locationAssets = items.stream()
                    .map(pi -> (Asset) pi.getPrototype())
                    .collect(Collectors.toSet());
            assets.addAll(locationAssets);

            List<Containable> locCons = items.stream()
                    .filter(pi -> pi instanceof Containable)
                    .map(pi -> (Containable) pi)
                    .filter(c -> !c.getItems().isEmpty())
                    .collect(Collectors.toList());
            addInnerAssets(assets, locCons);
            return assets;
        }

        private void addInnerAssets(Set<Asset> assets, List<Containable> locCons) {
            for (Containable cons : locCons) {
                List<Equipment> equipment = cons.getItems();
                Set<Asset> equipmentAssets = equipment.stream()
                        .map(pi -> (Asset) pi.getPrototype())
                        .collect(Collectors.toSet());
                assets.addAll(equipmentAssets);
                List<Containable> containables = equipment.stream()
                        .filter(pi -> pi instanceof Containable)
                        .map(pi -> (Containable) pi)
                        .filter(c -> !c.getItems().isEmpty())
                        .collect(Collectors.toList());
                addInnerAssets(assets, containables);
            }
        }

        private void reloadAssetImages(Asset a) {
            a.setImage(null);
            a.getInitialImage();
            if (a instanceof Openable) {
                Openable o = (Openable) a;
                o.setOpenImage(null);
                o.getOpenImage();
            }
        }
    }
}
