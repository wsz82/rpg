package game.view.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

public class ContainerView extends EquipmentView {

    public ContainerView(GraphicsContext gc) {
        super(gc);
    }

    protected final void drawEquipment() {
        for (Equipment e : equipment) {
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

    @Override
    protected void drawBackground() {
        gc.setFill(Color.BROWN);
        gc.fillRect(viewPos.x * Sizes.getMeter(), viewPos.y * Sizes.getMeter(),
                viewWidth * Sizes.getMeter(), viewHeight * Sizes.getMeter());
    }

    @Override
    public void remove(Equipment e, Creature cr) {
        getItems().remove(e);
    }

    @Override
    public void add(Equipment e, Creature cr, double x, double y) {
        e.setPos(x, y, null);
        getItems().add(e);
    }
}
