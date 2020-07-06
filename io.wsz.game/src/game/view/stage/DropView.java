package game.view.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class DropView extends EquipmentView{
    private final Coords creaturePos = new Coords();
    private List<Equipment> droppedEquipment;

    public DropView(GraphicsContext gc) {
        super(gc);
    }

    protected final void drawEquipment() {
        for (Equipment e : droppedEquipment) {
            final Coords pos = e.getPos();
            Coords screenCoords = translateCoordsToScreenCoords(pos);
            final double x = (screenCoords.x * Sizes.getMeter());
            final double y = (screenCoords.y * Sizes.getMeter());

            Image img = e.getImage();
            double width = img.getWidth();
            double height = img.getHeight();

            double startX = 0;
            if (x < 0) {
                startX = -x;
                width = x + width;
            }
            if (width > viewWidth * Sizes.getMeter()) {
                width = viewWidth * Sizes.getMeter();
            }

            double startY = 0;
            if (y < 0) {
                startY = -y;
                height = y + height;
            }
            if (height > viewHeight * Sizes.getMeter()) {
                height = viewHeight * Sizes.getMeter();
            }

            double destX = 0;
            if (x > 0) {
                destX = x;
            }
            double destY = 0;
            if (y > 0) {
                destY = y;
            }
            gc.drawImage(img, startX, startY, width, height, destX, destY, width, height);
        }
    }

    public void setDroppedEquipment(List<Equipment> droppedEquipment) {
        this.droppedEquipment = droppedEquipment;
    }

    @Override
    protected void drawBackground() {
        gc.setFill(Color.BROWN);
        gc.fillOval(viewPos.x * Sizes.getMeter(), viewPos.y * Sizes.getMeter(),
                viewWidth * Sizes.getMeter(), viewHeight * Sizes.getMeter());
    }

    public void setCreaturePos(Coords pos) {
        this.creaturePos.x = pos.x;
        this.creaturePos.y = pos.y;
    }

    @Override
    public void remove(Equipment e, Creature cr) {
        e.onTake(cr, 0, 0); //TODO
        droppedEquipment.remove(e);
    }

    @Override
    public void add(Equipment e, Creature cr, double x, double y) {
        e.onDrop(cr, x, y);
    }

    @Override
    public List<Equipment> getItems() {
        return droppedEquipment;
    }
}