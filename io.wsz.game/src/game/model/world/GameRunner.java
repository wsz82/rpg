package game.model.world;

import game.model.GameController;
import game.model.save.SaveMemento;
import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.content.ContentComparator;
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
                List<Content> contents = Controller.get().getCurrentLocation().getContent();
                contents.sort(new ContentComparator() {
                    @Override
                    public int compare(Content o1, Content o2) {
                        return super.compare(o1, o2);
                    }
                });
                for (Content content : contents) {
                    content.getItem().update();
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
