package game.view.world.inventory;

import game.model.GameController;
import io.wsz.model.animation.creature.CreatureAnimation;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.Inventory;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;

import java.util.List;
import java.util.Map;

public class CreatureViewElement extends InventoryViewElement {
    private Creature drawnCreature;

    public CreatureViewElement(Canvas canvas, GameController gameController, Coords mousePos) {
        super(canvas, gameController, mousePos);
    }

    @Override
    public void refresh() {
        super.refresh();
        int meter = Sizes.getMeter();
        int width = (int) viewWidth * meter;
        int height = (int) viewHeight * meter;
        CreatureAnimation animation = drawnCreature.getAnimation();

        drawCreatureBasicImage(meter, width, height, animation);

        drawEquippedItems(meter, width, height, animation);
    }

    private void drawCreatureBasicImage(int meter, int width, int height, CreatureAnimation animation) {
        ResolutionImage creatureInventoryImage = animation.getCreatureInventoryImage(Paths.BASIC, width, height);
        Image img = creatureInventoryImage.getFxImage();
        gc.drawImage(img, viewPos.x * meter, viewPos.y * meter);
    }

    private void drawEquippedItems(int meter, int width, int height, CreatureAnimation animation) {
        Map<InventoryPlaceType, Equipment> equippedItems = drawnCreature.getInventory().getEquippedItems();
        for (InventoryPlaceType type : equippedItems.keySet()) {
            Equipment equipment = equippedItems.get(type);
            if (equipment == null) continue;
            String equipmentTypeName = equipment.getEquipmentType().getId();
            ResolutionImage creatureInventoryImage = animation.getCreatureInventoryImage(equipmentTypeName, width, height);
            if (creatureInventoryImage == null) continue;
            Image creatureInventoryFxImage = creatureInventoryImage.getFxImage();
            if (creatureInventoryFxImage == null) continue;
            gc.drawImage(creatureInventoryFxImage, viewPos.x * meter, viewPos.y * meter);
        }
    }

    @Override
    protected void scrollScrollBar() {

    }

    @Override
    public boolean tryRemove(Equipment toRemove, Creature cr) {
        Inventory inventory = cr.getInventory();
        if (inventory.tryTakeOff(toRemove)) {
            System.out.println(toRemove.getAssetId() + " unequipped from " + toRemove.getOccupiedPlace().getId());
            return true;
        } else {
            System.out.println(toRemove.getAssetId() + " could not be unequipped from " + toRemove.getOccupiedPlace().getId());
            return false;
        }
    }

    @Override
    public boolean tryAdd(Equipment e, Creature cr, double x, double y) {
        if (cr.getInventory().tryWear(e, x, y)) {
            System.out.println(e.getAssetId() + " equipped on place " + e.getOccupiedPlace().getId());
            return true;
        } else {
            System.out.println(e.getAssetId() + " does not fit this place");
            return false;
        }
    }

    @Override
    public Coords getExtremePos(Coords mousePos, Coords draggedCoords, Equipment e) {
        return null;
    }

    @Override
    public Equipment lookForEquipment(double x, double y, Coords draggedCoords) {
        Map<InventoryPlaceType, List<Coords>> inventoryPlaces = drawnCreature.getInventoryPlaces();
        for (InventoryPlaceType type : inventoryPlaces.keySet()) {
            List<Coords> place = inventoryPlaces.get(type);
            Inventory inventory = drawnCreature.getInventory();
            boolean isPointToPlace = inventory.fitsPlace(x, y, drawnCreature, TEMP_2, place);
            if (isPointToPlace) {
                return inventory.getEquippedItems().get(type);
            }
        }
        return null;
    }

    @Override
    public void scrollUp() {

    }

    @Override
    public void scrollDown() {

    }

    @Override
    public boolean tryStartDragScroll(double x, double y) {
        return false;
    }

    @Override
    public void setMovedToHeroEquipmentPos(Coords pos) {
        pos.x = 0;
        pos.y = 0;
    }

    public void setDrawnCreature(Creature drawnCreature) {
        this.drawnCreature = drawnCreature;
    }
}
