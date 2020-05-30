package game.view.stage;

import javafx.scene.layout.BorderPane;

class GameFrame extends BorderPane {
    private static GameFrame singleton;

    public static GameFrame get() {
        if (singleton == null) {
            singleton = new GameFrame();
        }
        return singleton;
    }

    private GameFrame() {
        initBoard();
    }

    void initBoard(){

    }
}