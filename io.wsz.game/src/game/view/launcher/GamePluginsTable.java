package game.view.launcher;

import game.model.GameController;
import io.wsz.model.Controller;
import io.wsz.model.plugin.Plugin;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.StringConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GamePluginsTable extends Stage {
    private final ChoiceBox<Plugin> activePluginCB = new ChoiceBox<>();
    private final ObservableList<Plugin> pluginList = FXCollections.observableArrayList();
    private final GameController gameController;
    private final Controller controller;

    public GamePluginsTable(GameController gameController) {
        super(StageStyle.UTILITY);
        this.gameController = gameController;
        controller = gameController.getController();
        pluginList.addAll(getPlugins());
        initWindow();
    }

    private void initWindow() {
        final Group root = new Group();
        final VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        final TableView<Plugin> table = new TableView<>();
        final TableColumn<Plugin, String> nameCol = new TableColumn<>("Name");
        final TableColumn<Plugin, Boolean> startCol = new TableColumn<>("Starting");

        final HBox activePluginBox = new HBox(5);
        activePluginBox.setAlignment(Pos.CENTER);
        final Label activePluginLabel = new Label("Active plugin");
        activePluginLabel.setAlignment(Pos.CENTER);
        activePluginBox.getChildren().addAll(activePluginLabel, activePluginCB);

        final HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.CENTER);
        final Button cancel = new Button("Cancel");
        cancel.setAlignment(Pos.CENTER);
        final Button ok = new Button("Accept");
        ok.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(cancel, ok);

        container.getChildren().addAll(table, activePluginBox, buttons);
        root.getChildren().add(container);
        final Scene scene = new Scene(root);
        setScene(scene);

        setUpTable(table, nameCol, startCol);
        setUpChoiceBox(activePluginCB);
        setUpButtons(cancel, ok);
    }

    private void setUpButtons(Button cancel, Button ok) {
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> close());
        ok.setDefaultButton(true);
        ok.setOnAction(event -> setActivePlugin());
    }

    private void setActivePlugin() {
        Plugin pluginToActivate = activePluginCB.getValue();
        if (pluginToActivate == null) {
            alertNoPluginToActivate();
            return;
        }
        String pluginToActivateName = pluginToActivate.getName();
        Plugin plugin = controller.loadPlugin(pluginToActivateName);
        if (plugin == null) return;
        controller.getModel().setActivePlugin(plugin);
        gameController.storeLastPlugin(plugin);
        close();
    }

    private void alertNoPluginToActivate() {
        final Alert alert = new Alert(
                Alert.AlertType.ERROR, "No starting plugin is chosen", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    private void setUpChoiceBox(ChoiceBox<Plugin> activePluginCB) {
        if (pluginList.isEmpty()) {
            return;
        }
        FilteredList<Plugin> startingPlugins = new FilteredList<>(pluginList);
        startingPlugins.setPredicate(p -> p.isStartingLocation());
        activePluginCB.setItems(startingPlugins);
        activePluginCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(Plugin p) {
                return p.getName();
            }

            @Override
            public Plugin fromString(String s) {
                List<Plugin> singlePlugin = pluginList.stream()
                        .filter(p -> p.getName().equals(s))
                        .collect(Collectors.toList());
                return singlePlugin.get(0);
            }
        });
        Plugin active = controller.getActivePlugin();
        if (active != null) {
            String name = active.getName();
            List<Plugin> singlePlugin = pluginList.stream()
                    .filter(p -> p.getName().equals(name))
                    .collect(Collectors.toList());
            activePluginCB.setValue(singlePlugin.get(0));
        }
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
        File programDir = controller.getProgramDir();
        File[] files = programDir.listFiles((dir, name) -> name.endsWith(".rpg"));
        List<Plugin> plugins = new ArrayList<>(0);
        for (File file : Objects.requireNonNull(files)) {
            Plugin p = controller.loadPluginMetadata(file.getName());
            if (p == null) continue;
            plugins.add(p);
        }
        return plugins;
    }
}
