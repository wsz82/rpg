package game.view.stage;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Game extends Stage {
    private static final KeyCodeCombination CLOSE_GAME = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);
    private final EventHandler<KeyEvent> menuReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            showGameMenu();
        }
    };
    private StackPane gameRoot;
    private StackPane menuRoot;
    private static Game game;

    public static Game get() {
        if (game == null) {
            game = new Game();
        }
        return game;
    }

    private Game(){
        super(StageStyle.UNDECORATED);
        setFullScreenExitHint("");
        setFullScreenExitKeyCombination(
                CLOSE_GAME);
        setFullScreen(true);
        showMainMenu();
    }

    private void showMainMenu() {
        menuRoot = new StackPane();
        VBox menu = new VBox(10);
        Button newGame = new Button("New Game");
        newGame.setOnAction(event -> startNewGame());
        Button loadGame = new Button("Load game");

        Button exit = new Button("Leave game");
        exit.setOnAction(event -> leaveGame());
        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(newGame, loadGame, exit);
        menuRoot.getChildren().addAll(menu);

        Scene scene = new Scene(menuRoot);
        setScene(scene);
        show();
    }

    private void startNewGame() {
        gameRoot = new StackPane();
        BorderPane gameEnvelope = new GameEnvelope();

        Game.get().addEventHandler(KeyEvent.KEY_RELEASED, menuReturn);

        GameBoard gameBoard = new GameBoard();
        gameEnvelope.setCenter(gameBoard);
        gameRoot.getChildren().addAll(gameEnvelope);

        getScene().setRoot(gameRoot);
    }

    private void leaveGame() {
        //TODO save
        close();
    }

    private void showGameMenu() {
        removeEventHandler(KeyEvent.KEY_RELEASED, menuReturn);

        StackPane gameMenu = new StackPane();
        VBox menu = new VBox(10);
        menu.setAlignment(Pos.CENTER);
        Button resume = new Button("Resume");
        resume.setOnAction(event -> {
            getScene().setRoot(gameRoot);
            addEventHandler(KeyEvent.KEY_RELEASED, menuReturn);
        });
        Button save = new Button("Save game");

        Button load = new Button("Load game");

        Button mainMenu = new Button("Main menu");
        mainMenu.setOnAction(event -> {
            getScene().setRoot(menuRoot);
        });
        menu.getChildren().addAll(resume, save, load, mainMenu);
        gameMenu.getChildren().addAll(menu);

        getScene().setRoot(gameMenu);
    }
}
