package game.view.stage;

import game.model.GameController;
import io.wsz.model.item.Container;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class ContainerView extends EquipmentView {
    private Container container;

    public ContainerView(Canvas canvas, GameController gameController) {
        super(canvas, gameController);
    }

    @Override
    public void refresh() {
        super.refresh();

        drawEquipment();
    }

    @Override
    protected final void drawEquipment() {

        selectEquipment();

        drawContainerEquipment();

        drawContainerSize();
    }

    private void drawContainerSize() {
        int filledSpace = container.getFilledSpace();
        int maxSize = container.getSize() - container.getNettoSize();
        double columnWidth = SIZE_COLUMN_WIDTH * inventoryWidth;
        double viewHeight = this.viewHeight;
        double columnX = viewPos.x - columnWidth;
        double columnY = viewPos.y;
        Color backgroundColor = Color.WHITE;
        drawColumn(filledSpace, maxSize, columnWidth, viewHeight, columnX, columnY, backgroundColor);
    }

    private void drawContainerEquipment() {
        for (Equipment e : items) {
            Coords pos = e.getPos();
            Coords corrected = currentPosCorrection(pos);
            int meter = Sizes.getMeter();
            double x = corrected.x * meter;
            double y = corrected.y * meter;
            Image img = e.getImage();
            double viewX = viewPos.x * meter;
            double viewY = viewPos.y * meter;
            double viewWidth = this.viewWidth * meter;
            double viewHeight = this.viewHeight * meter;

            cutImageAndDraw(x, y, img, viewX, viewY, viewWidth, viewHeight);
        }
    }

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    @Override
    protected void drawBackground() {
        gc.setFill(Color.BROWN);
        gc.fillRect(viewPos.x * Sizes.getMeter(), viewPos.y * Sizes.getMeter(),
                viewWidth * Sizes.getMeter(), viewHeight * Sizes.getMeter());
    }

    @Override
    public boolean remove(Equipment e, Creature cr) {
        getItems().remove(e);
        System.out.println(e.getName() + " removed from " + container.getName());
        return true;
    }

    @Override
    public void add(Equipment e, Creature cr, double x, double y) {
        if (!container.add(e)) {
            System.out.println(e.getName() + " does not fit " + container.getName());
            Coords bottom = cr.getCenter();
            double dropX = bottom.x - e.getImageWidth()/2;
            double dropY = bottom.y - e.getImageHeight()/2;
            if (!e.onDrop(cr, dropX, dropY)) {
                cr.getItems().add(e);
            }
        } else {
            e.setPos(x, y, null);
            System.out.println(e.getName() + " added to " + container.getName());
        }
    }

    @Override
    public List<Equipment> getItems() {
        return container.getItems();
    }
}
