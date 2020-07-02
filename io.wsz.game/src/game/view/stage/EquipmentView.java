package game.view.stage;

import game.model.GameController;
import io.wsz.model.item.*;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class EquipmentView extends Pane {
    private static final double MAX_WIDTH = 40;
    private final List<Equipment> equipmentToHold = new ArrayList<>(0);
    private final List<Equipment> equipmentToDrop = new ArrayList<>(0);
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
    private final List<Weapon> weaponToEquip = new ArrayList<>(1);
    private final List<Equipment> draggedEquipment = new ArrayList<>(1);
    private final Container container;
    private final Stage parent;
    private double unit;
    private GridPane holdGP;
    private GridPane dropGP;
    private Pane equippedWeaponPane;

    public EquipmentView(Creature cr, Container container, Stage parent) {
        this.cr = cr;
        this.container = container;
        this.parent = parent;
        this.equipmentToHold.addAll(cr.getItems());
        if (container == null) {
            List<Equipment> equipmentWithinRange = cr.getEquipmentWithinRange();
            this.equipmentToDrop.addAll(equipmentWithinRange);
        } else {
            this.equipmentToDrop.addAll(container.getItems());
        }
        Weapon weapon = cr.getIndividualInventory().getEquippedWeapon();
        this.weaponToEquip.add(null);
        this.weaponToEquip.set(0, weapon);
        this.draggedEquipment.add(null);
    }

    public void initWindow() {
        refresh();
        hookupEvents();
    }

    private void resolveInventory() {
        Inventory actualHold = cr.getIndividualInventory();
        for (Equipment e : equipmentToHold) {
            boolean notContainThis = actualHold.getItems().stream()
                    .noneMatch(eq -> eq == e);
            if (notContainThis) {
                if (actualHold.add(e)) {
                    if (container == null) {
                        e.onTake(cr);
                    } else {
                        container.remove(e);
                    }
                }
            }
        }

        List<Equipment> actualDrop;
        if (container == null) {
            actualDrop = cr.getEquipmentWithinRange();
        } else {
            actualDrop = container.getItems();
        }
        for (Equipment e : equipmentToDrop) {
            boolean notContainThis = actualDrop.stream()
                    .noneMatch(eq -> eq == e);
            if (notContainThis) {
                actualHold.remove(e);
                if (container == null) {
                    e.onDrop(cr);
                } else {
                    if (!container.add(e)) {
                        e.onDrop(cr);
                    }
                }
            }
        }

        Weapon w = weaponToEquip.get(0);
        cr.getInventory().setEquippedWeapon(w);
        if (actualHold.getItems().stream().anyMatch(e -> e == w)) {
            actualHold.remove(w);
        }
        if (actualDrop.stream().anyMatch(e -> e == w)) {
            if (container == null) {
                w.onTake(cr);
            } else {
                container.remove(w);
            }
        }
    }

    private void refresh() {
        getChildren().clear();
        unit = getWidth() / MAX_WIDTH;

        initItemsScrollPane();
        initWeightBox();
        initSizeBox();
        initDropScrollPane();
        initCreatureView();
        initEquippedWeaponView();
        initDropImage();
    }

    private void initDropImage() {
        if (container != null) {
            final ImageView iv = new ImageView(container.getImage());
            iv.setPreserveRatio(true);
            iv.setFitWidth(unit);
            iv.setFitHeight(unit);

            iv.setX(0.8 * getWidth());
            iv.setY(0.1 * getHeight());

            getChildren().add(iv);
        }
    }

    private void initEquippedWeaponView() {
        final ImageView iv = new ImageView();
        equippedWeaponPane = new Pane(iv);
        iv.setFitWidth(unit);
        iv.setFitHeight(unit);
        equippedWeaponPane.setBackground(new Background(new BackgroundFill(Color.BROWN, null, null)));

        equippedWeaponPane.setLayoutX(0.4 * getWidth());
        equippedWeaponPane.setLayoutY(0.4 * getHeight());

        getChildren().add(equippedWeaponPane);

        Weapon w = weaponToEquip.get(0);
        if (w != null) {
            Image img = w.getImage();
            iv.setImage(img);

            addDragAndDropEventsForEquipment(iv, w);
        }

        addDragAndDropEventsForEquippedWeapon(equippedWeaponPane);
    }

    private void addDragAndDropEventsForEquippedWeapon(Node node) {
        node.setOnDragOver(e -> {
            if (e.getGestureSource() != node
                    && e.getDragboard().hasImage()
                    && draggedEquipment.get(0) != null
                    && draggedEquipment.get(0) instanceof Weapon) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });

        node.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()
                    && draggedEquipment.get(0) != null
                    && draggedEquipment.get(0) instanceof Weapon) {
                Weapon dragged = (Weapon) draggedEquipment.get(0);
                Weapon actual = weaponToEquip.get(0);
                if (dragged == actual) {
                    success = false;
                } else {
                    if (actual != null) {
                        equipmentToHold.add(actual);
                    }
                    weaponToEquip.set(0, dragged);
                    success = true;
                }
            }
            e.setDropCompleted(success);

            e.consume();
        });
    }

    private void initSizeBox() {
        final VBox sizeBox = new VBox(10);
        Inventory inventory = cr.getIndividualInventory();
        int maxSize = inventory.getMaxSize();
        Label maxSizeLabel = new Label(String.valueOf(maxSize));
        int sumSize = equipmentToHold.stream()
                .mapToInt(Equipment::getSize)
                .sum();
        Label sumSizeLabel = new Label(String.valueOf(sumSize));

        sizeBox.getChildren().addAll(sumSizeLabel, maxSizeLabel);

        sizeBox.setPrefSize(unit, 2*unit);

        sizeBox.setLayoutX(0.1 * getWidth());
        sizeBox.setLayoutY(0.7 * getHeight());

        getChildren().add(sizeBox);
    }

    private void initWeightBox() {
        final VBox weightBox = new VBox(10);
        Inventory inventory = cr.getIndividualInventory();
        double maxWeight = inventory.getMaxWeight();
        Label maxWeightLabel = new Label(String.format("%.1f", maxWeight));
        double sumWeight = equipmentToHold.stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
        Label sumWeightLabel = new Label(String.format("%.1f", sumWeight));

        weightBox.getChildren().addAll(sumWeightLabel, maxWeightLabel);

        weightBox.setPrefSize(unit, 2*unit);

        weightBox.setLayoutX(0.2 * getWidth());
        weightBox.setLayoutY(0.7 * getHeight());

        getChildren().add(weightBox);
    }

    private void initItemsScrollPane() {
        final ScrollPane holdSP = new ScrollPane();
        holdSP.setPrefSize(21 * unit, 2.5 * unit);
        holdGP = new GridPane();
        holdGP.setHgap(unit/10);
        holdGP.setVgap(unit/10);

        int maxColumnNumber = 10;
        fillGridPane(holdGP, maxColumnNumber, equipmentToHold);

        holdSP.setContent(holdGP);

        holdSP.setLayoutX(0.3 * getWidth());
        holdSP.setLayoutY(0.7 * getHeight());

        getChildren().add(holdSP);

        addDragAndDropEventsForHold(holdSP);
    }

    private void addDragAndDropEventsForHold(ScrollPane holdSP) {
        holdSP.setOnDragOver(e -> {
            if (e.getGestureSource() != holdSP
                    && e.getDragboard().hasImage()
                    && draggedEquipment.get(0) != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });

        holdSP.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()
                    && draggedEquipment.get(0) != null) {
                equipmentToHold.add(draggedEquipment.get(0));
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
        fillGridPane(dropGP, maxColumnNumber, equipmentToDrop);

        dropSP.setContent(dropGP);

        dropSP.setLayoutX(0.8 * getWidth());
        dropSP.setLayoutY(0.3 * getHeight());

        getChildren().add(dropSP);

        addDragAndDropEventsForGround(dropSP);
    }

    private void addDragAndDropEventsForGround(ScrollPane dropSP) {
        dropSP.setOnDragOver(e -> {
            if (e.getGestureSource() != dropSP
                    && e.getDragboard().hasImage()
                    && draggedEquipment.get(0) != null) {
                e.acceptTransferModes(TransferMode.MOVE);
            }

            e.consume();
        });

        dropSP.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()
                    && draggedEquipment.get(0) != null) {
                equipmentToDrop.add(draggedEquipment.get(0));
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

            draggedEquipment.set(0, equipment);

            e.consume();
        });

        iv.setOnDragDone(e -> {
            if (e.getTransferMode() == TransferMode.MOVE) {
                Node parent = iv.getParent();
                if (parent == holdGP) {
                    equipmentToHold.remove(equipment);
                } else if (parent == dropGP) {
                    equipmentToDrop.remove(equipment);
                } else if (parent == equippedWeaponPane) {
                    weaponToEquip.set(0, null);
                }
                draggedEquipment.set(0, null);
                refresh();
            }
            e.consume();
        });
    }

    private void initCreatureView() {
        final ImageView iv = new ImageView(cr.getImage());
        iv.setPreserveRatio(true);
        iv.setFitWidth(1.5 * unit);

        iv.setX(0.1 * getWidth());
        iv.setY(0.2 * getHeight());

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
