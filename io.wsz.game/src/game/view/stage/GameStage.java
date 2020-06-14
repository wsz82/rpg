package game.view.stage;

import game.model.GameController;
import game.model.save.SaveMemento;
import game.model.setting.SettingMemento;
import game.view.launcher.Main;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

public class GameStage extends Stage {
    private static final KeyCodeCombination CLOSE_GAME = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);
    private final EventHandler<KeyEvent> gameMenuReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            showGameMenu();
            GameController.get().setGame(false);
        }
    };
    private final EventHandler<KeyEvent> mainMenuReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            showMainMenu();
        }
    };
    private final EventHandler<KeyEvent> gameReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            resumeGame();
        }
    };
    private final GameCanvas gameCanvas;
    private final Button cancel = new Button("Cancel");
    private StackPane mainMenuRoot;
    private StackPane gameMenuRoot;
    private Group gameRoot;
    private StackPane savesRoot;
    private StackPane loadsRoot;
    private ListView<String> savesView;
    private ListView<String> loadsView;

    public GameStage() {
        super(StageStyle.DECORATED);
        this.gameCanvas = new GameCanvas();
        GameController.get().setGameCanvas(gameCanvas);
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
        exit.setOnAction(event -> close());

        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(newGame, loadGame, openSettings, exit);
        mainMenuRoot.getChildren().addAll(menu);
    }

    private void openSettings(EventHandler<KeyEvent> returnEvent) {
        SettingsMenu settingsMenu = new SettingsMenu(this);
        settingsMenu.open(getScene(), returnEvent);
    }

    private void startNewGame() {
        GameController.get().restoreLastPlugin();
        startGame(null);
    }

    private void initGameRoot() {
        gameRoot = new Group();
        gameRoot.getChildren().addAll(gameCanvas);
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
            resumeGame();
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
        GameController.get().initSavesList(programDir);
    }

    private void initLoadsView() {
        loadsRoot = new StackPane();
        BorderPane borderPane = new BorderPane();
        borderPane.setMaxWidth(300);
        borderPane.setMaxHeight(500);

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
        borderPane.setMaxWidth(300);
        borderPane.setMaxHeight(500);

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

    private void resumeGame() {
        if (gameRoot == null) {
            initGameRoot();
        }
        setRootToGame();
        GameController.get().resumeGame();
    }

    private void startGame(SaveMemento memento) {
        if (gameRoot == null) {
            initGameRoot();
        }
        boolean gameStarted = GameController.get().startGame(memento);
        if (!gameStarted) {
            alertNoGame();
            return;
        }
        setRootToGame();
    }

    private void alertNoGame() {
        Node root = getScene().getRoot();
        VBox alert = new VBox(5);
        alert.setAlignment(Pos.CENTER);
        alert.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        Label message = new Label("No game is chosen");
        message.setAlignment(Pos.CENTER);
        Button returnButton = new Button("Return");
        returnButton.setAlignment(Pos.CENTER);
        returnButton.setOnAction(e -> {
            removeAlert(alert, root);
        });

        alert.getChildren().addAll(message, returnButton);

        if (root == mainMenuRoot) {
            mainMenuRoot.getChildren().add(alert);
        } else if (root == loadsRoot) {
            loadsRoot.getChildren().add(alert);
        }
    }

    private void removeAlert(VBox alert, Node root) {
        if (root == mainMenuRoot) {
            mainMenuRoot.getChildren().remove(alert);
        } else if (root == loadsRoot) {
            loadsRoot.getChildren().remove(alert);
        }
    }

    private void createNewSave() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(this);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.UNDECORATED);
        dialog.setHeaderText(null);
        dialog.setGraphic(null);
        dialog.showAndWait()
                .filter(f -> !f.equals(""))
                .ifPresent(n -> saveGame(false, n));
    }

    private void initSavesListView() {
        savesView = new ListView<>(GameController.get().getSavesList());
        savesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initLoadsListView() {
        loadsView = new ListView<>(GameController.get().getSavesList());
        loadsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void loadSave() {
        String name = loadsView.getSelectionModel().getSelectedItem();
        SaveMemento memento = GameController.get().loadSaveMemento(name, Main.getDir());
        startGame(memento);
    }

    private void deleteSave() {
        String name = savesView.getSelectionModel().getSelectedItem();
        GameController.get().deleteGameSave(name, Main.getDir());
    }

    private void saveGame(boolean overwrite, String name) {
        Coords currentPos = gameCanvas.getCurrentPos();
        GameController.get().saveGame(overwrite, name, currentPos, Main.getDir());
        resumeGame();
    }

    private void setRootToGame() {
        removeEventHandler(KeyEvent.KEY_RELEASED, gameReturn);
        addEventHandler(KeyEvent.KEY_RELEASED, gameMenuReturn);
        getScene().setRoot(gameRoot);
    }

    private void restoreSettings(File programDir) {
        SettingMemento memento = GameController.get().loadSettings(programDir);

        setFullScreen(memento.isFullScreen());
    }

    private void storeSettings() {
        SettingMemento memento = new SettingMemento();
        memento.setFullScreen(isFullScreen());

        GameController.get().saveSettings(Main.getDir(), memento);
    }

    public void open() {
        setFullScreenExitHint("");
        setFullScreenExitKeyCombination(CLOSE_GAME);
        setWidth(800);
        setHeight(600);
        setFullScreen(true);

        createMainMenu();
        File programDir = Main.getDir();
        initSavesList(programDir);
        restoreSettings(programDir);

        Scene scene = new Scene(mainMenuRoot);
        setScene(scene);
        show();
    }

    @Override
    public void close() {
        super.close();
        storeSettings();
    }
}
