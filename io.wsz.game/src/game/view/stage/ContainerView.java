package game.view.stage;

import io.wsz.model.item.Container;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.List;

public class ContainerView extends EquipmentView {
    private Container container;

    public ContainerView(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public void refresh() {
        super.refresh();

        drawEquipment();
    }

    @Override
    protected final void drawEquipment() {

        selectItems();

        for (Equipment e : items) {
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
    public void remove(Equipment e, Creature cr) {
        getItems().remove(e);
        System.out.println(e.getName() + " removed from " + container.getName());
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
