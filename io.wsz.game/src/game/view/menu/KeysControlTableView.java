package game.view.menu;

import game.model.GameController;
import game.model.setting.Key;
import io.wsz.model.locale.LocaleKeys;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

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
                boolean isCodeAlreadyAssigned = table.getItems().stream()
                        .anyMatch(k -> k.getCode().equals(code));
                if (isCodeAlreadyAssigned) return;
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

        Properties locale = gameController.getLocale();
        TableColumn<Key, String> actionCol = new TableColumn<>(locale.getProperty(LocaleKeys.ACTION));
        actionCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                String localeCode = p.getValue().getAction().getLocaleCode();
                return locale.getProperty(localeCode);
            }
        });
        actionCol.setCellFactory(TextFieldTableCell.forTableColumn());
        setColumnParams(actionCol);
        table.getColumns().add(actionCol);

        TableColumn<Key, String> keyCol = new TableColumn<>(locale.getProperty(LocaleKeys.KEY));
        keyCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                KeyCode code = p.getValue().getCode();
                if (code == null) {
                    return "";
                } else {
                    return code.getName();
                }
            }
        });
        keyCol.setCellFactory(TextFieldTableCell.forTableColumn());
        setColumnParams(keyCol);
        table.getColumns().add(keyCol);

        table.widthProperty().addListener((observable, oldWidth, newWidth) -> {
            double keyColWidth = keyCol.getWidth();
            double newActionColWidth = newWidth.doubleValue() - keyColWidth;
            actionCol.setPrefWidth(newActionColWidth);
        });
    }

    private void setColumnParams(TableColumn<Key, String> actionCol) {
        actionCol.setSortable(false);
        actionCol.setReorderable(false);
        actionCol.setResizable(false);
    }

    public TableView<Key> getTable() {
        return table;
    }
}
