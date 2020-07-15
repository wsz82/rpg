package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static io.wsz.model.sizes.Sizes.TURN_DURATION_MILLIS;

public class GameRunner {
    private final GameController gameController = GameController.get();
    private final Controller controller = Controller.get();
    private final Set<Location> heroesLocations = new HashSet<>(1);
    private Thread gameThread;

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
            clearImagesAndReload();
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

    private void clearImagesAndReload() {
        gameController.setGame(false);
        List<Location> locations = Controller.get().getLocationsList();
        for (Location l : locations) {
            List<PosItem> items = l.getItems().get();
            for (PosItem pi : items) {
                pi.setImage(null);
                if (pi instanceof Containable) {
                    Containable c = (Containable) pi;
                    List<Equipment> equipment = c.getItems();
                    if (!equipment.isEmpty()) {
                        for (Equipment e : equipment) {
                            e.setImage(null);
                        }
                    }
                }
            }
        }
        loadImages();
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
            int total = items.size() - 1;
            updateProgress(0, total);

            for (int i = 0; i <= total; i++) {
                PosItem item = items.get(i);
                item.getImage();
                if (item instanceof Containable) {
                    Containable c = (Containable) item;
                    List<Equipment> equipment = c.getItems();
                    if (!equipment.isEmpty()) {
                        for (Equipment e : equipment) {
                            e.getImage();
                        }
                    }
                }
                updateProgress(i, total);
            }
            return "Completed";
        }
    }
}
