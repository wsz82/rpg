package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import game.view.stage.GameBoard;
import game.view.stage.GameScrollPane;
import javafx.application.Platform;
import model.Controller;

public class World {
    private final GameBoard board;
    private final GameScrollPane scrollPane;
    private final long turnDurationMillis = 1000;
    private final long highFreqDurationMillis = 8;
    private static Thread gameThread;
    private static Thread highFreqThread;

    public World() {
        this.board = GameController.get().getBoard();
        this.scrollPane = GameController.get().getScrollPane();
    }

    public void startGame(SaveMemento memento) {
        GameController.get().setGame(true);

        if (memento == null) {
            loadNewGame();
        } else {
            loadSave(memento);
        }

        showGame();

        gameThread = new Thread(() -> {
            while (GameController.get().isGame()) {
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

        highFreqThread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(highFreqDurationMillis);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Platform.runLater(() -> {
                    update();
                });
            }
        });
        highFreqThread.setDaemon(true);
        highFreqThread.start();
    }

    private void showGame() {
        board.refresh();
    }

    private void update() {
        scrollPane.updatePos();
    }

    private void loadSave(SaveMemento memento) {
        GameController.get().loadSaveToLists(memento);
    }

    private void loadNewGame() {
        Controller.get().loadActivePluginToLists();
    }

    public static Thread getGameThread() {
        return gameThread;
    }

    public static Thread getHighFreqThread() {
        return highFreqThread;
    }
}
