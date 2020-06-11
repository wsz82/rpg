package editor.view.plugin;

import editor.model.EditorController;
import editor.view.stage.Main;
import io.wsz.model.Controller;
import io.wsz.model.plugin.Plugin;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditorPluginsTable extends Stage {
    private final ObservableList<Plugin> pluginList = FXCollections.observableArrayList();
    private final TableView<Plugin> table = new TableView<>();
    private final PluginSettingsStage pss;

    public EditorPluginsTable(PluginSettingsStage pss) {
        super(StageStyle.UTILITY);
        this.pss = pss;
        pluginList.addAll(getPlugins());
        initWindow();
    }

    private void initWindow() {
        final Group root = new Group();
        final VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        final TableColumn<Plugin, String> nameCol = new TableColumn<>("Name");
        final TableColumn<Plugin, Boolean> startCol = new TableColumn<>("Starting");

        final HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.CENTER);
        final Button cancel = new Button("Cancel");
        cancel.setAlignment(Pos.CENTER);
        final Button load = new Button("Load");
        load.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(cancel, load);
        final HBox activePluginBox = new HBox(5);
        final Label activePluginLabel = new Label("Active plugin: ");
        final Label activePluginName = new Label("");
        activePluginBox.getChildren().addAll(activePluginLabel, activePluginName);

        container.getChildren().addAll(table, activePluginBox, buttons);
        root.getChildren().add(container);
        final Scene scene = new Scene(root);
        setScene(scene);

        setUpTable(table, nameCol, startCol);
        setUpButtons(cancel, load);
        setUpActivePluginText(activePluginName);
    }

    private void setUpActivePluginText(Label activePluginName) {
        Plugin active = Controller.get().getActivePlugin();
        if (active != null) {
            activePluginName.setText(active.getName());
        }
    }

    private void setUpButtons(Button cancel, Button load) {
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> close());
        load.setDefaultButton(true);
        load.setOnAction(event -> loadPlugin());
    }

    private void loadPlugin() {
        Plugin plugin = table.getSelectionModel().getSelectedItem();
        if (plugin == null) {
            alertNoPluginChosen();
            return;
        }
        EditorController.get().loadAndRestorePlugin(plugin.getName(), pss);
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
            TableView<Plugin> table, TableColumn<Plugin, String> nameCol, TableColumn<Plugin, Boolean> startCol) {

        if (pluginList.isEmpty()) {
            return;
        }
        table.setItems(pluginList);

        nameCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getName();
            }
        });

        startCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Boolean computeValue() {
                return param.getValue().isStartingLocation();
            }
        });

        ObservableList<TableColumn<Plugin, ?>> columns = table.getColumns();
        columns.add(0, nameCol);
        columns.add(1, startCol);
    }

    public List<Plugin> getPlugins() {
        File[] files = Main.getDir().listFiles((dir, name) -> name.endsWith(".rpg"));
        List<Plugin> plugins = new ArrayList<>(0);
        for (File file : Objects.requireNonNull(files)) {
            Plugin p = Controller.get().loadPluginMetadata(file.getName());
            plugins.add(p);
        }
        return plugins;
    }
}
