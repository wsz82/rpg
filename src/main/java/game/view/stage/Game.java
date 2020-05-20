package game.view.stage;

import game.view.launcher.Main;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.Controller;
import model.save.SaveMemento;

import java.io.File;

public class Game extends Stage {
    private static final KeyCodeCombination CLOSE_GAME = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);
    private final EventHandler<KeyEvent> gameMenuReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            showGameMenu();
        }
    };
    private final EventHandler<KeyEvent> mainMenuReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            showMainMenu();
        }
    };
    private EventHandler<KeyEvent> gameReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            showGame();
        }
    };
    private StackPane gameMenuRoot;
    private ScrollPane gameRoot;
    private StackPane mainMenuRoot;
    private StackPane savesRoot;
    private StackPane loadsRoot;
    private final Button cancel = new Button("Cancel");
    private ListView<String> savesView;
    private ListView<String> loadsView;
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
        setWidth(800);
        setHeight(600);
        setFullScreen(true);

        createMainMenu();
        initSavesList(Main.getDir());
        Scene scene = new Scene(mainMenuRoot);
        setScene(scene);
        show();
    }

    private void showMainMenu() {
        getScene().setRoot(mainMenuRoot);
        removeEventHandler(KeyEvent.KEY_RELEASED, mainMenuReturn);
        removeEventHandler(KeyEvent.KEY_RELEASED, gameReturn);
    }

    private void createMainMenu() {
        mainMenuRoot = new StackPane();

        VBox menu = new VBox(10);

        Button newGame = new Button("New Game");
        newGame.setOnAction(event -> startNewGame());
        Button loadGame = new Button("Load game");
        loadGame.setOnAction(event -> {
            openSaveListToLoad();
            cancel.setOnAction(e -> {
                showMainMenu();
            });
        });
        Button openSettings = new Button("Settings");
        openSettings.setOnAction(event -> openSettings(mainMenuReturn));
        Button exit = new Button("Exit");
        exit.setOnAction(event -> leaveGame());

        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(newGame, loadGame, openSettings, exit);
        mainMenuRoot.getChildren().addAll(menu);
    }

    private void openSettings(EventHandler<KeyEvent> returnEvent) {
        SettingsMenu.get().open(getScene(), returnEvent);
    }

    private void startNewGame() {
        StackPane stackPane = new StackPane();
        gameRoot = new GameScrollPane(stackPane);
        BorderPane gameEnvelope = new GameEnvelope();

        GameBoard gameBoard = GameBoard.get();
        gameEnvelope.setCenter(gameBoard);
        stackPane.getChildren().addAll(gameEnvelope);

        showGame();
    }

    private void leaveGame() {
        //TODO save
        close();
    }

    private void showGameMenu() {
        removeEventHandler(KeyEvent.KEY_RELEASED, gameMenuReturn);
        addEventHandler(KeyEvent.KEY_RELEASED, gameReturn);
        if (gameMenuRoot == null) {
            createGameMenu();
        }
        getScene().setRoot(gameMenuRoot);
    }

    private void createGameMenu() {
        gameMenuRoot = new StackPane();
        VBox menu = new VBox(10);
        menu.setAlignment(Pos.CENTER);
        Button resume = new Button("Resume");
        resume.setOnAction(event -> {
            showGame();
        });
        Button saveMenu = new Button("Save game");
        saveMenu.setOnAction(event -> {
            openSaveListToSave();
        });
        Button loadMenu = new Button("Load game");
        loadMenu.setOnAction(event -> {
            openSaveListToLoad();
            cancel.setOnAction(e -> {
                showGameMenu();
            });
        });
        Button openSettings = new Button("Settings");
        openSettings.setOnAction(event -> openSettings(gameMenuReturn));
        Button mainMenu = new Button("Main menu");
        mainMenu.setOnAction(event -> {
            showMainMenu();
        });
        menu.getChildren().addAll(resume, saveMenu, loadMenu, openSettings, mainMenu);
        gameMenuRoot.getChildren().addAll(menu);
    }

    private void openSaveListToSave() {
        addEventHandler(KeyEvent.KEY_RELEASED, gameMenuReturn);
        if (savesRoot == null) {
            initSavesView();
        }
        getScene().setRoot(savesRoot);
    }

    private void openSaveListToLoad() {
        if (getScene().getRoot() == mainMenuRoot) {
            addEventHandler(KeyEvent.KEY_RELEASED, mainMenuReturn);
        } else if (getScene().getRoot() == gameMenuRoot) {
            addEventHandler(KeyEvent.KEY_RELEASED, gameMenuReturn);
        }
        if (loadsRoot == null) {
            initLoadsView();
        }
        getScene().setRoot(loadsRoot);
    }

    private void initSavesList(File programDir) {
        Controller.get().initSavesList(programDir);
    }

    private void initLoadsView() {
        loadsRoot = new StackPane();
        BorderPane borderPane = new BorderPane();

        if (loadsView == null) {
            initLoadsListView();
        }

        borderPane.setCenter(loadsView);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        cancel.setCancelButton(true);
        Button load = new Button("Load");
        load.setOnAction(event -> loadSave());
        buttons.getChildren().addAll(cancel, load);
        borderPane.setBottom(buttons);

        loadsRoot.getChildren().addAll(borderPane);
    }

    private void initSavesView() {
        savesRoot = new StackPane();
        BorderPane borderPane = new BorderPane();

        if (savesView == null) {
            initSavesListView();
        }

        borderPane.setCenter(savesView);

        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        Button delete = new Button("Delete");
        delete.setOnAction(event -> deleteSave());
        Button cancel = new Button("Cancel");
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> showGameMenu());
        Button save = new Button("Save");
        save.setOnAction(event -> overwriteSave());
        Button newSave = new Button("New save");
        newSave.setOnAction(event -> createNewSave());

        buttons.getChildren().addAll(delete, cancel, save, newSave);

        borderPane.setBottom(buttons);

        savesRoot.getChildren().addAll(borderPane);
    }

    private void overwriteSave() {
        String name = savesView.getSelectionModel().getSelectedItem();
        saveGame(true, name);
    }

    private void showGame() {
        getScene().setRoot(gameRoot);
        removeEventHandler(KeyEvent.KEY_RELEASED, gameReturn);
        addEventHandler(KeyEvent.KEY_RELEASED, gameMenuReturn);
    }

    private void createNewSave() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter save name");
        dialog.showAndWait()
                .filter(f -> !f.equals(""))
                .ifPresent(n -> saveGame(false, n));
    }

    private void initSavesListView() {
        savesView = new ListView<>(Controller.get().getSavesList());
        savesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initLoadsListView() {
        loadsView = new ListView<>(Controller.get().getSavesList());
        loadsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void loadSave() {
        String name = loadsView.getSelectionModel().getSelectedItem();
        SaveMemento memento = Controller.get().loadGameSave(name, Main.getDir());
        gameRoot.setHvalue(memento.gethValue());
        gameRoot.setVvalue(memento.getvValue());
        showGame();
    }

    private void deleteSave() {
        String name = savesView.getSelectionModel().getSelectedItem();
        Controller.get().deleteGameSave(name, Main.getDir());
    }

    private void saveGame(boolean overwrite, String name) {
        double hvalue = gameRoot.getHvalue();
        double vvalue = gameRoot.getVvalue();
        Controller.get().saveGame(overwrite, name, hvalue, vvalue, Main.getDir());
        showGame();
    }
}
