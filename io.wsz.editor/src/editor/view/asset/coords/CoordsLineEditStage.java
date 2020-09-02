package editor.view.asset.coords;

import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class CoordsLineEditStage<A extends PosItem<?,?>> extends CoordsShapeEditStage<A> {
    private final Polyline polyline = new Polyline();
    private final List<Coords> line;

    public CoordsLineEditStage(Stage parent, A item, List<Coords> line, Image background) {
        super(parent, item, background);
        this.line = line;
    }

    @Override
    public void initWindow(boolean isContent, String title) {
        super.initWindow(isContent, title);

        polyline.setStroke(Color.RED);
        polyline.setStrokeWidth(1);
        pointsPane.getChildren().add(polyline);
    }

    @Override
    protected void fillList() {
        List<Coords> clone = Geometry.cloneCoordsList(line);
        coordsList.addAll(clone);
    }

    @Override
    protected void restoreShape() {
        if (line == null) {
            return;
        }
        if (line.size() < 2) {
            return;
        }
        List<Double> points = coordsToPoints(line);
        polyline.getPoints().addAll(points);
    }

    @Override
    protected void clearShape() {
        polyline.getPoints().clear();
    }

    @Override
    protected void refreshShape() {
        super.refreshShape();
        coordsList.sort(Comparator.comparingDouble(c -> c.x));
        List<Double> doubles = coordsToPoints(coordsList);
        List<Double> points = polyline.getPoints();
        points.clear();
        points.addAll(doubles);
    }

    @Override
    protected void saveShape() {
        line.clear();
        line.addAll(coordsList);
        close();
    }
}
