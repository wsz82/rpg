package editor.view.plugin;

import editor.model.EditorController;
import io.wsz.model.plugin.PluginMetadata;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class EditorPluginsTable extends Stage {
    private final ObservableList<PluginMetadata> metadatas = FXCollections.observableArrayList();
    private final TableView<PluginMetadata> table = new TableView<>();
    private final PluginSettingsStage pss;
    private final EditorController controller;
    private Label activePluginName;

    public EditorPluginsTable(PluginSettingsStage pss, EditorController controller) {
        super(StageStyle.UTILITY);
        this.pss = pss;
        this.controller = controller;
        List<PluginMetadata> pluginMetadata = controller.getPluginMetadatas();
        metadatas.addAll(pluginMetadata);
    }

    public void initWindow() {
        final Group root = new Group();
        final VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        final TableColumn<PluginMetadata, String> nameCol = new TableColumn<>("Name");
        final TableColumn<PluginMetadata, Boolean> startCol = new TableColumn<>("Starting");

        final HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.CENTER);
        final Button cancel = new Button("Cancel");
        cancel.setAlignment(Pos.CENTER);
        final Button load = new Button("Load");
        load.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(cancel, load);
        final HBox activePluginBox = new HBox(5);
        final Label activePluginLabel = new Label("Active plugin: ");
        activePluginName = new Label("");
        activePluginBox.getChildren().addAll(activePluginLabel, activePluginName);

        container.getChildren().addAll(table, activePluginBox, buttons);
        root.getChildren().add(container);
        final Scene scene = new Scene(root);
        setScene(scene);

        setUpTable(table, nameCol, startCol);
        setUpButtons(cancel, load);
    }

    public void setUpActivePluginText() {
        PluginMetadata active = controller.getModel().getActivePluginMetadata();
        if (active != null) {
            activePluginName.setText(active.getPluginName());
        }
    }

    private void setUpButtons(Button cancel, Button load) {
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> close());
        load.setDefaultButton(true);
        load.setOnAction(event -> loadPlugin());
    }

    private void loadPlugin() {
        PluginMetadata metadata = table.getSelectionModel().getSelectedItem();
        if (metadata == null) {
            alertNoPluginChosen();
            return;
        }
        controller.loadAndRestorePlugin(metadata, pss);
        close();
    }

    private void alertNoPluginChosen() {
        final Alert alert = new Alert(
                Alert.AlertType.ERROR, "No plugin is chosen", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    private void setUpTable(
            TableView<PluginMetadata> table,
            TableColumn<PluginMetadata, String> nameCol,
            TableColumn<PluginMetadata, Boolean> startCol) {

        if (metadatas.isEmpty()) {
            return;
        }
        table.setItems(metadatas);

        nameCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getPluginName();
            }
        });

        startCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Boolean computeValue() {
                return param.getValue().isStartingLocation();
            }
        });

        ObservableList<TableColumn<PluginMetadata, ?>> columns = table.getColumns();
        columns.add(0, nameCol);
        columns.add(1, startCol);
    }
}
