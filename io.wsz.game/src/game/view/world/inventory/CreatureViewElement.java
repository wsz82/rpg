package game.view.world.inventory;

import game.model.GameController;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.Inventory;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
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
        Image img = drawnCreature.getAnimation().getCreatureInventoryImage(drawnCreature, width, height);
        gc.drawImage(img, viewPos.x * meter, viewPos.y * meter);
    }

    @Override
    protected void scrollScrollBar() {

    }

    @Override
    public boolean tryRemove(Equipment toRemove, Creature cr) {
        Inventory inventory = cr.getInventory();
        if (inventory.tryTakeOff(toRemove)) {
            System.out.println(toRemove.getName() + " unequipped from " + toRemove.getOccupiedPlace().getName());
            return true;
        } else {
            System.out.println(toRemove.getName() + " could not be unequipped from " + toRemove.getOccupiedPlace().getName());
            return false;
        }
    }

    @Override
    public boolean tryAdd(Equipment e, Creature cr, double x, double y) {
        if (cr.getInventory().tryWear(e, x, y)) {
            System.out.println(e.getName() + " equipped on place " + e.getOccupiedPlace().getName());
            return true;
        } else {
            System.out.println(e.getName() + " does not fit this place");
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
