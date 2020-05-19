package game.view.stage;

import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
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
    private StackPane gameMenu;
    private ScrollPane gameRoot;
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
//        setWidth(1000);
//        setHeight(700);
        showMainMenu();
    }

    private void showMainMenu() {
        if (menuRoot == null) {
            createMainMenu();
        }
        Scene scene = new Scene(menuRoot);
        setScene(scene);
        show();
    }

    private void createMainMenu() {
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
    }

    private void startNewGame() {
        StackPane stackPane = new StackPane();
        gameRoot = new GameScrollPane(stackPane);
        BorderPane gameEnvelope = new GameEnvelope();

        addEventHandler(KeyEvent.KEY_RELEASED, menuReturn);

        GameBoard gameBoard = GameBoard.get();
        gameEnvelope.setCenter(gameBoard);
        stackPane.getChildren().addAll(gameEnvelope);

        getScene().setRoot(gameRoot);
    }

    private void leaveGame() {
        //TODO save
        close();
    }

    private void showGameMenu() {
        removeEventHandler(KeyEvent.KEY_RELEASED, menuReturn);
        if (gameMenu == null) {
            createGameMenu();
        }
        getScene().setRoot(gameMenu);
    }

    private void createGameMenu() {
        gameMenu = new StackPane();
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
    }
}
