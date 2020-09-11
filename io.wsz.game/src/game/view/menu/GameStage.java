package game.view.menu;

import game.model.GameController;
import game.model.save.SaveMemento;
import game.model.setting.SettingMemento;
import game.view.world.board.GameView;
import io.wsz.model.Controller;
import io.wsz.model.locale.LocaleKeys;
import io.wsz.model.sizes.Sizes;
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
import java.util.Properties;

public class GameStage extends Stage {
    private static final KeyCodeCombination CLOSE_GAME = new KeyCodeCombination(KeyCode.F4, KeyCombination.ALT_DOWN);

    private final GameController gameController;
    private final Controller controller;
    private final BorderPane root;
    private final GameView gameView;

    private Node parentToReturn;
    private StackPane mainMenu;
    private StackPane gameMenu;
    private StackPane saves;
    private StackPane loads;
    private ListView<String> savesView;
    private ListView<String> loadsView;
    private Button cancel;
    private Button newGame;
    private Button loadGame;
    private Button openSettings;
    private Button exit;

    public GameStage(GameController gameController) {
        super(StageStyle.DECORATED);
        this.gameController = gameController;
        this.controller = gameController.getController();
        this.root = new BorderPane();
        this.gameView = new GameView(this, gameController);
        gameController.setGameStage(this);
        gameController.setGameView(gameView);
    }

    public void init() {
        Properties locale = controller.getLocale();
        cancel = new Button(locale.getProperty(LocaleKeys.CANCEL));
        setFullScreenExitHint("");
        setFullScreenExitKeyCombination(CLOSE_GAME);
        setWidth(1280);
        setHeight(720);
        setMinWidth(300);
        setMinHeight(300);

        File programDir = controller.getProgramDir();
        initSavesList(programDir);
        restoreSettings();

        root.setFocusTraversable(false);
        setMainMenuForCenter();
        final Scene scene = new Scene(root);
        setScene(scene);

        hookUpCloseEvent();
        hookUpReturnEvent();
    }

    private void hookUpCloseEvent() {
        setOnCloseRequest(e -> {
            storeSettings();
        });
    }

    private void hookUpReturnEvent() {
        EventHandler<KeyEvent> returnEvent = e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                e.consume();
                if (parentToReturn == null) {
                    return;
                }
                root.setCenter(parentToReturn);
                if (parentToReturn == gameMenu) {
                    gameController.setGame(false);
                    Sizes.setTimeOfMenuOpen(System.currentTimeMillis());
                    setGameMenuForCenter();
                } else if (parentToReturn == gameView.getCanvas()) {
                    gameController.resumeGame();
                }
            }
        };
        addEventHandler(KeyEvent.KEY_RELEASED, returnEvent);
    }

    public void setMainMenuForCenter() {
        parentToReturn = null;
        final StackPane mainMenu = getMainMenu();
        root.setCenter(mainMenu);
    }

    public void setGameMenuForCenter() {
        final StackPane gameMenu = getGameMenu();
        parentToReturn = gameView.getCanvas();
        root.setCenter(gameMenu);
    }

    public void setGameViewForCenter() {
        gameMenu = getGameMenu();
        parentToReturn = gameMenu;
        if (root.getCenter() != gameView.getCanvas()) {
            root.setCenter(gameView.getCanvas());
        }
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

    private StackPane getMainMenu() {
        Properties locale = controller.getLocale();
        mainMenu = new StackPane();

        final VBox menu = new VBox(10);

        newGame = new Button(locale.getProperty(LocaleKeys.NEW_GAME));
        newGame.setOnAction(event -> startNewGame());
        loadGame = new Button(locale.getProperty(LocaleKeys.LOAD_GAME));
        loadGame.setOnAction(event -> {
            openSaveListToLoad();
            cancel.setOnAction(e -> setMainMenuForCenter());
        });
        openSettings = new Button(locale.getProperty(LocaleKeys.SETTINGS));
        openSettings.setOnAction(event -> openSettings());
        exit = new Button(locale.getProperty(LocaleKeys.EXIT));
        exit.setOnAction(event -> close());

        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(newGame, loadGame, openSettings, exit);
        mainMenu.getChildren().addAll(menu);
        return mainMenu;
    }

    private void openSettings() {
        parentToReturn = root.getCenter();
        SettingsParent parent;
        if (root.getCenter() == mainMenu) {
            parent = SettingsParent.MAIN_MENU;
        } else {
            parent = SettingsParent.GAME_MENU;
        }
        final SettingsMenu settingsMenu = new SettingsMenu(this, gameController, parent);
        root.setCenter(settingsMenu);
        settingsMenu.open(root);
    }

    private void startNewGame() {
        gameController.restoreLastPluginMetadata();
        startGame(null);
    }

    private StackPane getGameMenu() {
        Properties locale = controller.getLocale();
        gameMenu = new StackPane();
        final VBox menu = new VBox(10);
        menu.setAlignment(Pos.CENTER);
        final Button resume = new Button(locale.getProperty(LocaleKeys.RESUME));
        resume.setOnAction(event -> gameController.resumeGame());
        final Button saveMenu = new Button(locale.getProperty(LocaleKeys.SAVE_GAME));
        saveMenu.setOnAction(event -> openSaveListToSave());
        final Button loadMenu = new Button(locale.getProperty(LocaleKeys.LOAD_GAME));
        loadMenu.setOnAction(event -> {
            openSaveListToLoad();
            cancel.setOnAction(e -> setGameMenuForCenter());
        });
        final Button openSettingsFromGameMenu = new Button(locale.getProperty(LocaleKeys.SETTINGS));
        openSettingsFromGameMenu.setOnAction(event -> openSettings());
        final Button openMainMenuFromGameMenu = new Button(locale.getProperty(LocaleKeys.MAIN_MENU));
        openMainMenuFromGameMenu.setOnAction(event -> setMainMenuForCenter());
        menu.getChildren().addAll(resume, saveMenu, loadMenu, openSettingsFromGameMenu, openMainMenuFromGameMenu);
        gameMenu.getChildren().addAll(menu);
        return gameMenu;
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
        final BorderPane borderPane = new BorderPane();
        borderPane.setMaxWidth(300);
        borderPane.setMaxHeight(500);

        if (loadsView == null) {
            initLoadsListView();
        }

        borderPane.setCenter(loadsView);

        final HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);

        cancel.setCancelButton(true);
        Properties locale = controller.getLocale();
        final Button load = new Button(locale.getProperty(LocaleKeys.LOAD));
        load.setOnAction(event -> loadSave());
        buttons.getChildren().addAll(cancel, load);
        borderPane.setBottom(buttons);

        loads.getChildren().addAll(borderPane);
    }

    private void initSavesView() {
        saves = new StackPane();
        final BorderPane borderPane = new BorderPane();
        borderPane.setMaxWidth(300);
        borderPane.setMaxHeight(500);

        if (savesView == null) {
            initSavesListView();
        }

        borderPane.setCenter(savesView);

        Properties locale = controller.getLocale();
        final HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        final Button delete = new Button(locale.getProperty(LocaleKeys.DELETE));
        delete.setOnAction(event -> deleteSave());
        final Button cancelSave = new Button(locale.getProperty(LocaleKeys.CANCEL));
        cancelSave.setCancelButton(true);
        cancelSave.setOnAction(event -> setGameMenuForCenter());
        final Button save = new Button(locale.getProperty(LocaleKeys.SAVE));
        save.setOnAction(event -> overwriteSave());
        final Button newSave = new Button(locale.getProperty(LocaleKeys.NEW_SAVE));
        newSave.setOnAction(event -> createNewSave());

        buttons.getChildren().addAll(delete, cancelSave, save, newSave);

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
        final VBox alert = new VBox(5);
        alert.setAlignment(Pos.CENTER);
        alert.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));

        Properties locale = controller.getLocale();
        final Label messageNoGameChosen = new Label(locale.getProperty(LocaleKeys.NO_GAME_CHOSEN));
        messageNoGameChosen.setAlignment(Pos.CENTER);
        final Button returnButton = new Button(locale.getProperty(LocaleKeys.RETURN));
        returnButton.setAlignment(Pos.CENTER);
        returnButton.setOnAction(e -> removeAlert(alert));

        alert.getChildren().addAll(messageNoGameChosen, returnButton);

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

    private void restoreSettings() {
        boolean isFullScreen = gameController.getSettings().isFullScreen();
        setFullScreen(isFullScreen);
    }

    private void storeSettings() {
        SettingMemento memento = new SettingMemento();
        memento.setFullScreen(isFullScreen());
        gameController.saveSettings(controller.getProgramDir(), memento);
    }

    @Override
    public void close() {
        super.close();
        storeSettings();
    }
}
