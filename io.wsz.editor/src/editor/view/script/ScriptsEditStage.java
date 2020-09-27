package editor.view.script;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import io.wsz.model.script.Script;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class ScriptsEditStage extends ChildStage {
    private final EditorController editorController;
    private ListView<Script> scriptListView;
    private VBox center;
    private Button validate;
    private TextArea textArea;
    private Label invalidInfo;

    public ScriptsEditStage(Stage mainStage, EditorController editorController) {
        super(mainStage);
        this.editorController = editorController;
    }

    public void initStage() {
        final BorderPane root = new BorderPane();

        initCenter();
        root.setCenter(center);

        initScriptsListView();
        root.setLeft(scriptListView);

        Scene scene = new Scene(root);
        setTitle("Scripts");
        setScene(scene);

        hookUpCloseEvent();
    }

    private void initScriptsListView() {
        ObservableList<Script> observableScripts = editorController.getObservableScripts();
        scriptListView = new ListView<>(observableScripts);
        scriptListView.setEditable(true);
        scriptListView.setOnEditCommit(e -> {
            Script selectedItem = scriptListView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) return;
            String oldId = selectedItem.getId();
            Script newScript = e.getNewValue();
            String newId = newScript.getId();
            if (newId.equals(oldId) || newId.isEmpty()) return;
            String uniqueID = getUniqueID(newId, getIds());
            selectedItem.setId(uniqueID);
        });
        scriptListView.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(Script script) {
                if (script == null) {
                    return "";
                } else {
                    return script.getId();
                }
            }

            @Override
            public Script fromString(String id) {
                return new Script(id);
            }
        }));
        setUpContextMenu();
        hookUpScriptsListEvents();
    }

    private void hookUpScriptsListEvents() {
        scriptListView.getSelectionModel().selectedItemProperty().addListener((observable, oldScript, newScript) -> {
            if (oldScript != null) {
                saveScript(oldScript);
            }
            if (newScript != null) {
                center.setVisible(true);
                textArea.setText(newScript.getInitialText());
                invalidInfo.setText(newScript.getValidatorMessage());
            } else {
                center.setVisible(false);
                textArea.setText(null);
                invalidInfo.setText(null);
            }
        });
    }

    private void initCenter() {
        center = new VBox(5);
        initValidateButton();
        HBox topBar = new HBox(5);
        topBar.getChildren().add(validate);
        initTextArea();
        HBox bottomInfo = new HBox(5);
        invalidInfo = new Label();
        setUpInvalidInfoLabel();
        bottomInfo.getChildren().add(invalidInfo);
        center.getChildren().addAll(topBar, textArea, bottomInfo);
        center.setVisible(false);
    }

    private void initValidateButton() {
        validate = new Button("Validate");
        validate.setOnAction(e -> {
            saveSelectedScript();
        });
    }

    private void initTextArea() {
        textArea = new TextArea();
    }

    private void setUpContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem addScript = new MenuItem("Add script");
        final MenuItem removeScripts = new MenuItem("Remove script/s");
        addScript.setOnAction(event -> addScript());
        removeScripts.setOnAction(event -> removeScripts());
        contextMenu.getItems().addAll(addScript, removeScripts);
        scriptListView.setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void addScript() {
        String id = "_new";
        String uniqueID = getUniqueID(id, getIds());
        Script script = new Script(uniqueID);
        scriptListView.getItems().add(script);
    }

    private String getUniqueID(String newID, List<String> IDs) {
        boolean IDsContainsNewID = IDs.contains(newID);
        if (IDsContainsNewID) {
            newID += "New";
            return getUniqueID(newID, IDs);
        } else {
            return newID;
        }
    }

    private List<String> getIds() {
        return scriptListView.getItems().stream()
                .map(Script::getId)
                .collect(Collectors.toList());
    }

    private void removeScripts() {
        List<Script> selectedItems = scriptListView.getSelectionModel().getSelectedItems();
        scriptListView.getItems().removeAll(selectedItems);
    }

    private void hookUpCloseEvent() {
        setOnCloseRequest(e -> {
            saveSelectedScript();
        });
    }

    private void saveSelectedScript() {
        Script selected = scriptListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        saveScript(selected);
        invalidInfo.setText(selected.getValidatorMessage());
    }

    private void saveScript(Script script) {
        script.fillScript(textArea.getText(), editorController.getController());
    }

    private void setUpInvalidInfoLabel() {
        invalidInfo.setTextFill(Color.RED);
    }
}
