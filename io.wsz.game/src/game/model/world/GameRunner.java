package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import javafx.application.Platform;

import java.util.List;

public class GameRunner {
    private final long turnDurationMillis = 16;

    public GameRunner() {
    }

    public void startGame(SaveMemento memento) {
        GameController.get().setGame(true);

        if (memento == null) {
            loadNewGame();
        } else {
            loadSave(memento);
        }

        runGameThread();
    }

    public void resumeGame() {
        GameController.get().setGame(true);
        runGameThread();
    }

    private void runGameThread() {
        showGame();
        GameController.get().focusGameScrollPane();

        Thread gameThread = new Thread(() -> {
            while (GameController.get().isGame()) {
                synchronized (this) {
                    List<PosItem> contents = Controller.get().getCurrentLocation().getItems();

                    for (PosItem pi : contents) {
                        pi.update();
                    }
                    for (Location l : Controller.get().getLocationsList()) {
                        addContent(l);
                        removeContent(l);
                    }
                    updateLocation();
                }

                try {
                    Thread.sleep(turnDurationMillis);
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

    private void addContent(Location l) {
        List<PosItem> p = l.getItemsToAdd();
        if (p.isEmpty()) {
            return;
        }
        l.getItems().get().addAll(p);
        p.clear();
    }

    private void removeContent(Location l) {
        List<PosItem> p = l.getItemsToRemove();
        if (p.isEmpty()) {
            return;
        }
        l.getItems().get().removeAll(p);
        p.clear();
    }

    private void updateLocation() {
        Location locationToUpdate = Controller.get().getUpdatedLocation();
        if (locationToUpdate != null) {
            CurrentLocation cl = Controller.get().getCurrentLocation();
            Location l = cl.getLocation();
            if (!l.getName().equals(locationToUpdate.getName())) {
                cl.setLocation(locationToUpdate);
            }
        }
    }

    private void showGame() {
        GameController.get().showGame();
    }

    private void loadSave(SaveMemento memento) {
        GameController.get().loadSaveToLists(memento);
        GameController.get().initLoadedGameSettings(memento);
    }

    private void loadNewGame() {
        GameController.get().loadGameActivePluginToLists();
        GameController.get().initNewGameSettings();
    }
}
