package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import game.view.stage.GameBoard;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import model.Controller;

public class World {
    private final GameBoard board;
    private final long turnDurationMillis = 1000;

    public World() {
        this.board = GameController.get().getBoard();
    }

    public void startGame(SaveMemento memento) {

        if (memento == null) {
            loadNewGame();
        } else {
            loadSave(memento);
        }

        Service<Void> gameThread = new Service<>() {
            @Override
            protected Task<Void> createTask() {

                showGame();

                while (isRunning()) {
                    try {
                        Thread.sleep(turnDurationMillis);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Platform.runLater(() -> {
                        showGame();
                    });
                }
                return null;
            }
        };
        gameThread.setOnReady(event -> gameThread.start());
    }

    private void showGame() {
        board.redraw();
    }

    private void loadSave(SaveMemento memento) {
        GameController.get().loadSaveToLists(memento);
    }

    private void loadNewGame() {
        Controller.get().loadActivePluginToLists();
    }
}
