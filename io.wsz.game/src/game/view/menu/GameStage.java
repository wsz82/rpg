package game.view.menu;

import game.model.GameController;
import game.model.save.SaveMemento;
import game.model.setting.SettingMemento;
import game.view.world.board.GameView;
import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
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

    private GameController gameController;
    private final Controller controller;
    private GameView gameView;
    private Node parentToReturn;
    private StackPane mainMenu;
    private StackPane gameMenu;
    private StackPane saves;
    private StackPane loads;
    private ListView<String> savesView;
    private ListView<String> loadsView;

    private final BorderPane root = new BorderPane();
    private final Button cancel = new Button("Cancel");
    private final EventHandler<KeyEvent> returnEvent = e -> {
        if (e.getCode() == KeyCode.ESCAPE) {
            e.consume();
            if (parentToReturn == null) {
                return;
            }
            root.setCenter(parentToReturn);
            if (parentToReturn == gameMenu) {
                gameController.setGame(false);
                setGameMenuForCenter();
            } else
            if (parentToReturn == gameView.getCanvas()) {
                gameController.resumeGame();
            }
        }
    };

    public GameStage(GameController gameController) {
        super(StageStyle.DECORATED);
        this.gameController = gameController;
        this.controller = gameController.getController();
        gameView = new GameView(this, gameController);
        gameController.setGameStage(this);
        gameController.setGameView(gameView);
    }

    private void showMainMenu() {
        parentToReturn = null;
        root.setCenter(mainMenu);
    }

    private void createMainMenu() {
        mainMenu = new StackPane();

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
        openSettings.setOnAction(event -> openSettings());
        Button exit = new Button("Exit");
        exit.setOnAction(event -> close());

        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(newGame, loadGame, openSettings, exit);
        mainMenu.getChildren().addAll(menu);
    }

    private void openSettings() {
        SettingsMenu settingsMenu = new SettingsMenu(this, controller);
        parentToReturn = root.getCenter();
        root.setCenter(settingsMenu);
        settingsMenu.open(root, parentToReturn);
    }

    private void startNewGame() {
        gameController.restoreLastPlugin();
        startGame(null);
    }

    private void setGameMenuForCenter() {
        if (gameMenu == null) {
            createGameMenu();
        }
        parentToReturn = gameView.getCanvas();
        root.setCenter(gameMenu);
    }

    private void createGameMenu() {
        gameMenu = new StackPane();
        VBox menu = new VBox(10);
        menu.setAlignment(Pos.CENTER);
        Button resume = new Button("Resume");
        resume.setOnAction(event -> {
            gameController.resumeGame();
        });
        Button saveMenu = new Button("Save game");
        saveMenu.setOnAction(event -> {
            openSaveListToSave();
        });
        Button loadMenu = new Button("Load game");
        loadMenu.setOnAction(event -> {
            openSaveListToLoad();
            cancel.setOnAction(e -> {
                setGameMenuForCenter();
            });
        });
        Button openSettings = new Button("Settings");
        openSettings.setOnAction(event -> openSettings());
        Button mainMenu = new Button("Main menu");
        mainMenu.setOnAction(event -> {
            showMainMenu();
        });
        menu.getChildren().addAll(resume, saveMenu, loadMenu, openSettings, mainMenu);
        gameMenu.getChildren().addAll(menu);
    }

    private void openSaveListToSave() {
        if (saves == null) {
            initSavesView();
        }
        parentToReturn = root.getCenter();
        root.setCenter(saves);
    }

    private void openSaveListToLoad() {
        parentToReturn = root.getCenter();
        if (loads == null) {
            initLoadsView();
        }
        parentToReturn = root.getCenter();
        root.setCenter(loads);
    }

    private void initSavesList(File programDir) {
        gameController.initSavesList(programDir);
    }

    private void initLoadsView() {
        loads = new StackPane();
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

        loads.getChildren().addAll(borderPane);
    }

    private void initSavesView() {
        saves = new StackPane();
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
        cancel.setOnAction(event -> setGameMenuForCenter());
        Button save = new Button("Save");
        save.setOnAction(event -> overwriteSave());
        Button newSave = new Button("New save");
        newSave.setOnAction(event -> createNewSave());

        buttons.getChildren().addAll(delete, cancel, save, newSave);

        borderPane.setBottom(buttons);

        saves.getChildren().addAll(borderPane);
    }

    private void overwriteSave() {
        String name = savesView.getSelectionModel().getSelectedItem();
        saveGame(true, name);
    }

    private void startGame(SaveMemento memento) {
        boolean gameStarted = gameController.startGame(memento);
        if (!gameStarted) {
            alertNoGame();
        }
    }

    private void alertNoGame() {
        VBox alert = new VBox(5);
        alert.setAlignment(Pos.CENTER);
        alert.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        Label message = new Label("No game is chosen");
        message.setAlignment(Pos.CENTER);
        Button returnButton = new Button("Return");
        returnButton.setAlignment(Pos.CENTER);
        returnButton.setOnAction(e -> {
            removeAlert(alert);
        });

        alert.getChildren().addAll(message, returnButton);

        if (root.getCenter() == mainMenu) {
            mainMenu.getChildren().add(alert);
        } else
        if (root.getCenter() == loads) {
            loads.getChildren().add(alert);
        }
    }

    private void removeAlert(VBox alert) {
        if (root.getCenter() == mainMenu) {
            mainMenu.getChildren().remove(alert);
        } else
        if (root.getCenter() == loads) {
            loads.getChildren().remove(alert);
        }
    }

    private void createNewSave() {
        final TextInputDialog dialog = new TextInputDialog();
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
        savesView = new ListView<>(gameController.getSavesList());
        savesView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void initLoadsListView() {
        loadsView = new ListView<>(gameController.getSavesList());
        loadsView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void loadSave() {
        String name = loadsView.getSelectionModel().getSelectedItem();
        SaveMemento memento = gameController.loadSaveMemento(name, controller.getProgramDir());
        startGame(memento);
    }

    private void deleteSave() {
        String name = savesView.getSelectionModel().getSelectedItem();
        gameController.deleteGameSave(name, controller.getProgramDir());
    }

    private void saveGame(boolean overwrite, String name) {
        Coords curPos = controller.getCurPos();
        File programDir = controller.getProgramDir();
        gameController.saveGame(overwrite, name, curPos, programDir);
        gameController.resumeGame();
    }

    public void setGameViewForCenter() {
        if (gameMenu == null) {
            createGameMenu();
        }
        parentToReturn = gameMenu;
        if (root.getCenter() != gameView.getCanvas()) {
            root.setCenter(gameView.getCanvas());
        }
    }

    private void restoreSettings(File programDir) {
        SettingMemento memento = gameController.loadSettings(programDir);
        setFullScreen(memento.isFullScreen());
    }

    private void storeSettings() {
        SettingMemento memento = new SettingMemento();
        memento.setFullScreen(isFullScreen());
        gameController.saveSettings(controller.getProgramDir(), memento);
    }

    public void setLoaderViewToCenter(Task<String> loader) {
        final StackPane sp = new StackPane();
        final ProgressBar pb = new ProgressBar();
        sp.getChildren().addAll(pb);
        root.setCenter(sp);

        pb.progressProperty().bind(loader.progressProperty());
        pb.progressProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() >= 1) {
                setGameViewForCenter();
            }
        });
    }

    public void open() {
        setFullScreenExitHint("");
        setFullScreenExitKeyCombination(CLOSE_GAME);
        setWidth(1280);
        setHeight(720);
        setMinWidth(300);
        setMinHeight(300);

        createMainMenu();
        File programDir = controller.getProgramDir();
        initSavesList(programDir);
        restoreSettings(programDir);
        addEventHandler(KeyEvent.KEY_RELEASED, returnEvent);

        root.setFocusTraversable(false);
        root.setCenter(mainMenu);
        Scene scene = new Scene(root);
        setScene(scene);
        show();
    }

    @Override
    public void close() {
        super.close();
        storeSettings();
    }
}
