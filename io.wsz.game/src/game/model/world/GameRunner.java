package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import javafx.application.Platform;

import java.util.List;

import static io.wsz.model.Constants.TURN_DURATION_MILLIS;

public class GameRunner {
    private final GameController gameController = GameController.get();
    private final Controller controller = Controller.get();

    public GameRunner() {
    }

    public void startGame(SaveMemento memento) {
        gameController.setGame(true);

        if (memento == null) {
            loadNewGame();
        } else {
            loadSave(memento);
        }

        runGameThread();
    }

    public void resumeGame() {
        gameController.setGame(true);
        runGameThread();
    }

    private void runGameThread() {
        showGame();

        Thread gameThread = new Thread(() -> {
            while (gameController.isGame()) {
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
                }

                try {
                    Thread.sleep(TURN_DURATION_MILLIS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    synchronized (this) {
                        showGame();
                    }
                });

            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
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
            CurrentLocation cl = controller.getCurrentLocation();
            Location l = cl.getLocation();
            if (!l.getName().equals(locationToUpdate.getName())) {
                cl.setLocation(locationToUpdate);
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
    }
}
