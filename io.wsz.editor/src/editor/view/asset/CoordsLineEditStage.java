package editor.view.asset;

import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;

public class CoordsLineEditStage extends CoordsShapeEditStage {
    private final Polyline polyline = new Polyline();
    private final List<Coords> line;

    public CoordsLineEditStage(Stage parent, PosItem item, List<Coords> line, boolean isContent) {
        super(parent, item, isContent);
        this.line = line;
    }

    @Override
    public void initWindow(boolean isContent) {
        super.initWindow(isContent);
        setTitle("Cover edit");

        scrollPane.setContent(pointsPane);
        polyline.setStroke(Color.RED);
        polyline.setStrokeWidth(1);
        pointsPane.getChildren().add(polyline);
    }

    @Override
    protected void fillList(PosItem item) {
        List<Coords> clone = Coords.cloneCoordsList(line);
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
