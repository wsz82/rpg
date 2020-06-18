package game.view.stage;

import game.model.GameController;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.Inventory;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EquipmentView extends AnchorPane {
    private static final double MAX_WIDTH = 40;
    private final List<Equipment> inventoryEquipment = new ArrayList<>(0);
    private final List<Equipment> groundEquipment = new ArrayList<>(0);
    private final Creature cr;
    private final EventHandler<KeyEvent> inventoryClose = e -> {
        if (e.getCode().equals(KeyCode.I)
                || e.getCode().equals(KeyCode.ESCAPE)) {
            e.consume();

            resolveInventory();

            GameController.get().resumeGame();

            removeInventoryCloseEvent();
        }
    };

    private final Stage parent;

    private double unit;
    private GridPane inventoryGP;
    private GridPane dropGP;
    public EquipmentView(Creature cr, Stage parent) {
        this.cr = cr;
        this.parent = parent;
        this.inventoryEquipment.addAll(cr.getIndividualInventory().getItems());
        this.groundEquipment.addAll(cr.getEquipmentWithinRange());
    }

    public void initWindow() {
        refresh();
        hookupEvents();
    }

    private void refresh() {
        getChildren().clear();
        unit = getWidth() / MAX_WIDTH;

        initItemsScrollPane();
        initDropScrollPane();
        initCreatureView();
    }

    private void resolveInventory() {
        Inventory actualInventory = cr.getIndividualInventory();
        for (Equipment e : inventoryEquipment) {
            boolean notContainsThis = actualInventory.getItems().stream()
                    .noneMatch(eq -> eq == e);
            if (notContainsThis) {
                if (actualInventory.add(e)) {
                    e.onTake(cr);
                }
            }
        }
        for (Equipment e : groundEquipment) {
            List<Equipment> actual = cr.getEquipmentWithinRange();
            boolean notContainsThis = actual.stream()
                    .noneMatch(eq -> eq == e);
            if (notContainsThis) {
                actualInventory.remove(e);
                e.onDrop(cr);
            }
        }
    }

    private void initItemsScrollPane() {
        final ScrollPane inventorySP = new ScrollPane();
        inventorySP.setPrefSize(21 * unit, 2.5 * unit);
        inventoryGP = new GridPane();
        inventoryGP.setHgap(unit/10);
        inventoryGP.setVgap(unit/10);

        int maxColumnNumber = 10;
        fillGridPane(inventoryGP, maxColumnNumber, inventoryEquipment);

        inventorySP.setContent(inventoryGP);

        setLeftAnchor(inventorySP, 0.3 * getWidth());
        setTopAnchor(inventorySP, 0.7 * getHeight());

        getChildren().add(inventorySP);

        addDragAndDropEventsForInventory(inventorySP);
    }

    private void addDragAndDropEventsForInventory(ScrollPane inventorySP) {
        inventorySP.setOnDragOver(e -> {
            if (e.getGestureSource() != inventorySP &&
                    e.getDragboard().hasImage()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });

        inventorySP.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                success = true;
            }
            e.setDropCompleted(success);

            e.consume();
        });
    }

    private void initDropScrollPane() {
        final ScrollPane dropSP = new ScrollPane();
        dropSP.setPrefSize(2.5 * unit, 7 * unit);
        dropGP = new GridPane();
        dropGP.setHgap(unit/10);
        dropGP.setVgap(unit/10);

        int maxColumnNumber = 2;
        fillGridPane(dropGP, maxColumnNumber, groundEquipment);

        dropSP.setContent(dropGP);

        setLeftAnchor(dropSP, 0.8 * getWidth());
        setTopAnchor(dropSP, 0.3 * getHeight());

        getChildren().add(dropSP);

        addDragAndDropEventsForGround(dropSP);
    }

    private void addDragAndDropEventsForGround(ScrollPane dropSP) {
        dropSP.setOnDragOver(e -> {
            if (e.getGestureSource() != dropSP &&
                    e.getDragboard().hasImage()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });

        dropSP.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                success = true;
            }
            e.setDropCompleted(success);

            e.consume();
        });
    }

    private void fillGridPane(GridPane gp, int maxColumnNumber, List<Equipment> groundEquipment) {
        gp.getChildren().clear();
        int column = 0, row = 0;
        for (Equipment e : groundEquipment) {
            if (column == maxColumnNumber) {
                column = 0;
                row++;
            }
            final ImageView iv = new ImageView(e.getImage());
            iv.setFitWidth(unit);
            iv.setFitHeight(unit);

            gp.add(iv, column, row);

            addDragAndDropEventsForEquipment(iv, e);

            column++;
        }
    }

    private void addDragAndDropEventsForEquipment(ImageView iv, Equipment equipment) {
        iv.setOnDragDetected(e -> {
            Dragboard db = iv.startDragAndDrop(TransferMode.MOVE);

            ClipboardContent content = new ClipboardContent();
            content.putImage(iv.getImage());
            db.setContent(content);

            e.consume();
        });

        iv.setOnDragDone(e -> {
            if (e.getTransferMode() == TransferMode.MOVE) {
                GridPane target = (GridPane) iv.getParent();
                if (target.equals(inventoryGP)) {
                    dropGP.getChildren().remove(iv);
                    inventoryEquipment.remove(equipment);
                    groundEquipment.add(equipment);
                } else if (target.equals(dropGP)) {
                    inventoryGP.getChildren().remove(iv);
                    groundEquipment.remove(equipment);
                    inventoryEquipment.add(equipment);
                }
                refresh();
            }
            e.consume();
        });
    }

    private void initCreatureView() {
        final ImageView iv = new ImageView(cr.getImage());
        iv.setPreserveRatio(true);
        iv.setFitWidth(1.5 * unit);

        setLeftAnchor(iv, 0.1 * getWidth());
        setTopAnchor(iv, 0.2 * getHeight());

        getChildren().add(iv);
    }

    private void removeInventoryCloseEvent() {
        parent.removeEventHandler(KeyEvent.KEY_RELEASED, inventoryClose);
    }

    private void hookupEvents() {
        parent.addEventHandler(KeyEvent.KEY_RELEASED, inventoryClose);

        widthProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
        heightProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
    }
}
