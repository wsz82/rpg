package game.view.launcher;

import game.model.GameController;
import io.wsz.model.locale.LocaleKeys;
import io.wsz.model.plugin.PluginMetadata;
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

import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class GamePluginsTable extends Stage {
    private final ChoiceBox<PluginMetadata> activePluginCB = new ChoiceBox<>();
    private final ObservableList<PluginMetadata> metadatas = FXCollections.observableArrayList();
    private final GameController controller;

    public GamePluginsTable(GameController controller) {
        super(StageStyle.UTILITY);
        this.controller = controller;
        List<PluginMetadata> pluginMetadatas = controller.getPluginMetadatas();
        metadatas.addAll(pluginMetadatas);
        initWindow();
    }

    private void initWindow() {
        final Group root = new Group();
        final VBox container = new VBox(10);
        container.setPadding(new Insets(10));

        Properties locale = controller.getLocale();

        final TableView<PluginMetadata> table = new TableView<>();
        final TableColumn<PluginMetadata, String> nameCol = new TableColumn<>(locale.getProperty(LocaleKeys.NAME));
        final TableColumn<PluginMetadata, Boolean> startCol = new TableColumn<>(locale.getProperty(LocaleKeys.STARTING));

        final HBox activePluginBox = new HBox(5);
        activePluginBox.setAlignment(Pos.CENTER);
        final Label activePluginLabel = new Label(locale.getProperty(LocaleKeys.ACTIVE_PLUGIN));
        activePluginLabel.setAlignment(Pos.CENTER);
        activePluginBox.getChildren().addAll(activePluginLabel, activePluginCB);

        final HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.CENTER);
        final Button cancel = new Button(locale.getProperty(LocaleKeys.CANCEL));
        cancel.setAlignment(Pos.CENTER);
        final Button ok = new Button(locale.getProperty(LocaleKeys.ACCEPT));
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
        ok.setOnAction(event -> setActivePluginMetadata());
    }

    private void setActivePluginMetadata() {
        PluginMetadata pluginMetadataToActivate = activePluginCB.getValue();
        if (pluginMetadataToActivate == null) {
            alertNoPluginToActivate();
            return;
        }
        controller.getModel().setActivePluginMetadata(pluginMetadataToActivate);
        controller.storeLastPluginName(pluginMetadataToActivate);
        close();
    }

    private void alertNoPluginToActivate() {
        Properties locale = controller.getLocale();
        String message = locale.getProperty(LocaleKeys.NO_PLUGIN_CHOSEN);
        final Alert alert = new Alert(
                Alert.AlertType.ERROR, message, ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    private void setUpChoiceBox(ChoiceBox<PluginMetadata> activePluginMetadataCB) {
        if (metadatas.isEmpty()) {
            return;
        }
        FilteredList<PluginMetadata> startingPlugins = new FilteredList<>(metadatas);
        startingPlugins.setPredicate(PluginMetadata::isStartingLocation);
        activePluginMetadataCB.setItems(startingPlugins);
        activePluginMetadataCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(PluginMetadata metadata) {
                if (metadata == null) {
                    return null;
                } else {
                    return metadata.getPluginName();
                }
            }

            @Override
            public PluginMetadata fromString(String s) {
                return getPluginMetadata(s);
            }
        });
        PluginMetadata active = controller.getModel().getActivePluginMetadata();
        if (active != null) {
            String name = active.getPluginName();
            activePluginMetadataCB.setValue(getPluginMetadata(name));
        }
    }

    private PluginMetadata getPluginMetadata(String s) {
        Optional<PluginMetadata> optMetadata = metadatas.stream()
                .filter(p -> p.getPluginName().equals(s))
                .findFirst();
        return optMetadata.orElse(null);
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
