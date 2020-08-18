package editor.view.asset.coords;

import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CoordsPointEditStage extends CoordsShapeEditStage {

    public CoordsPointEditStage(Stage parent, PosItem item, Coords interactionPoint, Image background) {
        super(parent, item, background);
        if (interactionPoint != null) {
            coordsList.add(interactionPoint);
        }
    }

    @Override
    public void initWindow(boolean isContent, String title) {
        super.initWindow(isContent, title);
    }

    @Override
    protected void fillList() {
    }

    @Override
    protected void restoreShape() {
        if (!coordsList.isEmpty()) {
            coordsCB.setValue(coordsList.get(0));
        }
    }

    @Override
    protected void clearShape() {
        coordsList.clear();
    }

    @Override
    protected void saveShape() {
        if (!coordsList.isEmpty() && coordsList.get(0) != null) {
            item.setInteractionCoords(coordsList.get(0).clonePos());
        } else {
            item.setInteractionCoords(null);
        }
        close();
    }

    @Override
    protected void addPoint(Coords point) {
        coordsList.clear();
        coordsList.add(point);
        coordsCB.setValue(point);
        refreshShape();
    }
}
