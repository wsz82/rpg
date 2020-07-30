package game.view.stage;

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

    public ContainerView(Canvas canvas) {
        super(canvas);
    }

    @Override
    public void refresh() {
        super.refresh();

        drawEquipment();
    }

    @Override
    protected final void drawEquipment() {

        selectEquipment();

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

        drawContainerSize();
    }

    private void drawContainerSize() {
        double x = (viewPos.x + 4.0/5*viewWidth) * Sizes.getMeter();
        double y = viewPos.y*Sizes.getMeter() - gc.getFont().getSize()*3;
        drawSize(container.getFilledSpace(), container.getSize() - container.getNettoSize(), x, y);
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
