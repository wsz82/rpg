package editor.view.script;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import io.wsz.model.script.Script;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class ScriptsEditStage extends ChildStage {
    private final EditorController editorController;
    private ListView<Script> scriptListView;
    private TextArea textArea;

    public ScriptsEditStage(Stage mainStage, EditorController editorController) {
        super(mainStage);
        this.editorController = editorController;
    }

    public void initStage() {
        final BorderPane root = new BorderPane();

        initTextArea();
        root.setCenter(textArea);

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
                textArea.setVisible(true);
                textArea.setText(newScript.getInitialText());
            } else {
                textArea.setVisible(false);
                textArea.setText(null);
            }
        });
    }

    private void initTextArea() {
        textArea = new TextArea();
        textArea.setVisible(false);
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
        Script script = new Script();
        String id = "_new";
        script.setId(getUniqueID(id, getIds()));
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
        Script selectedItem = scriptListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) return;
        saveScript(selectedItem);
    }

    private void saveScript(Script script) {
        script.fillScript(textArea.getText(), editorController.getController());
    }
}
