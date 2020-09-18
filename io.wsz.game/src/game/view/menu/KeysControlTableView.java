package game.view.menu;

import game.model.GameController;
import game.model.setting.Key;
import game.model.setting.KeyAction;
import io.wsz.model.asset.Asset;
import io.wsz.model.locale.LocaleKeys;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.Properties;

public class KeysControlTableView {
    private final GameController gameController;
    private final TableView<Key> table = new TableView<>();

    private boolean isEditMode;

    public KeysControlTableView(GameController gameController) {
        this.gameController = gameController;
        hookUpEvents();
    }

    private void hookUpEvents() {
        EventHandler<KeyEvent> keyTyped = e -> {
            Key selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }
            KeyCode code = e.getCode();
            if (code.equals(KeyCode.ESCAPE)) {
                escapeEditMode();
                return;
            }
            if (isEditMode) {
                selected.setCode(code);
                table.refresh();
                escapeEditMode();
                return;
            }
            if (code.equals(KeyCode.ENTER)) {
                enterEditMode();
            }
        };
        table.addEventHandler(KeyEvent.KEY_PRESSED, keyTyped);
    }

    private void escapeEditMode() {
        table.getSelectionModel().clearSelection();
        isEditMode = false;
    }

    private void enterEditMode() {
        isEditMode = true;
    }

    public void initTable() {
        ObservableList<Key> keys = FXCollections.observableArrayList();
        keys.addAll(gameController.getSettings().getKeys());
        table.setItems(keys);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        Properties locale = gameController.getController().getLocale();
        TableColumn<Key, String> actionCol = new TableColumn<>(locale.getProperty(LocaleKeys.ACTION));
        actionCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getAction().toString();
            }
        });
        actionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        table.getColumns().add(actionCol);

        TableColumn<Key, String> keyCol = new TableColumn<>(locale.getProperty(LocaleKeys.KEY));
        keyCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                KeyCode code = p.getValue().getCode();
                if (code == null) {
                    return "";
                } else {
                    return code.toString();
                }
            }
        });
        keyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        table.getColumns().add(keyCol);
    }

    public TableView<Key> getTable() {
        return table;
    }
}
