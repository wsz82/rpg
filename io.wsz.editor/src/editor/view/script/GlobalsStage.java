package editor.view.script;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import io.wsz.model.script.variable.*;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GlobalsStage extends ChildStage {
    private static final String GLOBALS = "Global variables";

    private final StackPane root = new StackPane();
    private final TabPane tabPane = new TabPane();
    private final EditorController editorController;

    public GlobalsStage(Stage parent, EditorController editorController) {
        super(parent);
        this.editorController = editorController;
    }

    public void initWindow() {
        Scene scene = new Scene(root);
        setTitle(GLOBALS);
        setScene(scene);
        createTabs();
    }

    private void createTabs() {
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        GlobalsTableView<VariableString> stringsTableView = new GlobalsStringsTableView(this, editorController);
        ObservableList<VariableString> globalStrings = editorController.getObservableGlobalStrings();
        stringsTableView.initGlobalsTable(globalStrings);
        setUpTab(stringsTableView, VariableType.STRING.toString());

        GlobalsTableView<VariableBoolean> booleanTableView = new GlobalBooleanTableView(this, editorController);
        ObservableList<VariableBoolean> globalBooleans = editorController.getObservableGlobalBooleans();
        booleanTableView.initGlobalsTable(globalBooleans);
        setUpTab(booleanTableView, VariableType.BOOLEAN.toString());

        GlobalsTableView<VariableInteger> integerTableView = new GlobalIntegerTableView(this, editorController);
        ObservableList<VariableInteger> globalIntegers = editorController.getObservableGlobalIntegers();
        integerTableView.initGlobalsTable(globalIntegers);
        setUpTab(integerTableView, VariableType.INTEGER.toString());

        GlobalsTableView<VariableDecimal> decimalTableView = new GlobalDecimalTableView(this, editorController);
        ObservableList<VariableDecimal> globalDecimals = editorController.getObservableGlobalDecimals();
        decimalTableView.initGlobalsTable(globalDecimals);
        setUpTab(decimalTableView, VariableType.DECIMAL.toString());

        root.getChildren().add(tabPane);
    }

    private <A extends Variable<?>> void setUpTab(GlobalsTableView<A> tableView, String tabName) {
        Tab tab = new Tab();
        tab.setText(tabName);
        tab.setContent(tableView);
        tabPane.getTabs().add(tab);
    }
}
