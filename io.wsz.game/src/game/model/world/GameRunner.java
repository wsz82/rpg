package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import io.wsz.model.Controller;
import io.wsz.model.item.Container;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.List;

import static io.wsz.model.sizes.Sizes.TURN_DURATION_MILLIS;

public class GameRunner {
    private final GameController gameController = GameController.get();
    private final Controller controller = Controller.get();
    private Thread gameThread;

    public GameRunner() {}

    public void startGame(SaveMemento memento) {
        gameController.setGame(true);

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
                    List<PosItem> items = controller.getCurrentLocation().getItems();

                    for (PosItem pi : items) {
                        pi.update();
                    }
                    for (Location l : controller.getLocationsList()) {
                        addItems(l);
                        removeItems(l);
                    }
                    updateLocation();
                    tryToStartDialog();
                }

                try {
                    Thread.sleep(TURN_DURATION_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    synchronized (this) {
                        if (Sizes.isReloadImages()) {
                            clearImagesAndReload();
                            Sizes.setReloadImages(false);
                        }
                        showGame();
                        tryToOpenInventory();
                    }
                });

            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void clearImagesAndReload() {
        List<Location> locations = Controller.get().getLocationsList();
        for (Location l : locations) {
            List<PosItem> items = l.getItems().get();
            for (PosItem pi : items) {
                pi.setImage(null);
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

    private void tryToOpenInventory() {
        Creature creatureToOpenContainer = controller.getCreatureToOpenContainer();
        Container containerToOpen = controller.getContainerToOpen();
        if (creatureToOpenContainer != null && containerToOpen != null) {
            gameController.openInventory(creatureToOpenContainer, containerToOpen);
            controller.setCreatureToOpenContainer(null);
            controller.setContainerToOpen(null);
        }
    }

    private void addItems(Location l) {
        List<PosItem> p = l.getItemsToAdd();

        if (p.isEmpty()) {
            return;
        }
        l.getItems().get().addAll(p);
        p.clear();
    }

    private void removeItems(Location l) {
        List<PosItem> p = l.getItemsToRemove();
        if (p.isEmpty()) {
            return;
        }
        l.getItems().get().removeAll(p);
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
                Platform.runLater(this::loadImages);
            }
        }
    }

    private void showGame() {
        gameController.refreshGame();
    }

    private void loadSave(SaveMemento memento) {
        gameController.loadSaveToLists(memento);
        gameController.initLoadedGameSettings(memento);
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
                updateProgress(i, total);
            }
            return "Completed";
        }
    }
}
