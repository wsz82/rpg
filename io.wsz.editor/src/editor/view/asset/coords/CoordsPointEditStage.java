package editor.view.asset.coords;

import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class CoordsPointEditStage<A extends PosItem<?,?>> extends CoordsShapeEditStage<A> {
    private final PointSetter pointSetter;

    public CoordsPointEditStage(Stage parent, A item, Coords point, Image background, PointSetter pointSetter) {
        super(parent, item, background);
        this.pointSetter = pointSetter;
        coordsList.add(point);
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
            Coords point = coordsList.get(0);
            pointSetter.set(point);
        } else {
            pointSetter.set(null);
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
