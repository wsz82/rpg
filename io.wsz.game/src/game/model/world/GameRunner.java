package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import javafx.application.Platform;

import java.util.ArrayList;
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
                synchronized (Controller.get().getCurrentLocation().getContent()) { //TODO make the list Concurrent
                    List<Content> contents = new ArrayList<>(Controller.get().getCurrentLocation().getContent());

                    for (Content c : contents) {
                        c.getItem().update();
                    }
                }

                try {
                    Thread.sleep(turnDurationMillis);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    showGame();
                });

            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
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
