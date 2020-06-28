package editor.view.asset;

import editor.view.stage.ChildStage;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polyline;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CoverEditStage extends ChildStage {
    private final Polyline polyline = new Polyline();
    private final Pane pane = new Pane();
    private final ImageView iv = new ImageView();
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final Asset item;

    public CoverEditStage(Stage parent, Asset item, boolean isContent) {
        super(parent);
        this.item = item;
        initWindow(isContent);
    }

    private void initWindow(boolean isContent) {
        setTitle("Cover edit");
        final StackPane r = new StackPane();

        final VBox c = new VBox(5);
        final Image img = item.getImage();
        iv.setImage(img);
        pane.getChildren().add(iv);
        final ScrollPane sc = new ScrollPane();
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        sc.setMaxSize(bounds.getWidth() - bounds.getWidth()/10,
                bounds.getHeight() - bounds.getHeight()/5);
        sc.setContent(pane);
        polyline.setStroke(Color.RED);
        polyline.setStrokeWidth(1);
        pane.getChildren().add(polyline);

        final HBox btns = new HBox(5);
        btns.getChildren().addAll(cancel, save);

        c.getChildren().addAll(sc, btns);
        r.getChildren().add(c);

        final Scene scene = new Scene(r);
        setScene(scene);
        hookupEvents();
        restoreLine();

        if (isContent) {
            pane.setDisable(true);
            save.setDisable(true);
        }
    }

    private void restoreLine() {
        PosItem item = (PosItem) this.item;
        List<Coords> coverLine = item.getCoverLine();
        if (coverLine == null) {
            return;
        }
        if (coverLine.size() < 2) {
            return;
        }
        int size = coverLine.size() * 2;
        List<Double> points = new ArrayList<>(size);
        for (Coords pos : coverLine) {
            double x = pos.x * 100;
            double y = pos.y * 100;
            points.add(x);
            points.add(y);
        }
        polyline.getPoints().addAll(points);
    }

    private void hookupEvents() {
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> close());
        save.setDefaultButton(true);
        save.setOnAction(e -> saveLine());
        final ContextMenu cm = new ContextMenu();
        final MenuItem addPoint = new MenuItem("Add point");
        final MenuItem clear = new MenuItem("Clear");
        clear.setOnAction(ev -> clearPolyline());
        cm.getItems().addAll(addPoint, clear);
        pane.setOnContextMenuRequested(e -> openContextMenu(cm, addPoint, e));
        pane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)
                && e.getClickCount() == 2) {
                addPoint(e.getX(), e.getY());
            }
        });
    }

    private void openContextMenu(ContextMenu cm, MenuItem addPoint, ContextMenuEvent e) {
        cm.show(iv, e.getScreenX(), e.getScreenY());
        addPoint.setOnAction(ev -> {
            double x = e.getX();
            double y = e.getY();
            addPoint(x, y);
        });
    }

    private void clearPolyline() {
        polyline.getPoints().clear();
    }

    private void addPoint(double x, double y) {
        if (xAlreadyExists(x)) {
            return;
        }
        polyline.getPoints().addAll(x, y);

        List<Double> points = polyline.getPoints();
        List<Coords> coordsList = pointsToCoords(points);
        coordsList.sort(Comparator.comparingDouble(c -> c.x));

        List<Double> doubles = coordsToPoints(coordsList);
        points.clear();
        points.addAll(doubles);
    }

    private boolean xAlreadyExists(double x) {
        List<Double> doubles = polyline.getPoints();
        List<Coords> coords = pointsToCoords(doubles);
        return coords.stream()
                .anyMatch(c ->
                        x/Sizes.getMeter() > c.x - 1.0/Sizes.getMeter()
                        && x/Sizes.getMeter() < c.x + 1.0/Sizes.getMeter()
                );
    }

    private List<Coords> pointsToCoords(List<Double> points) {
        if (points.isEmpty()) {
            return new ArrayList<>(0);
        }
        int size = points.size();
        List<Coords> coordsList = new ArrayList<>(size / 2);
        for (int i = 0; i < size - 1; i = i + 2) {
            double px = points.get(i) / Sizes.getMeter();
            double py = points.get(i + 1) / Sizes.getMeter();
            Coords point = new Coords(px, py);
            coordsList.add(point);
        }
        return coordsList;
    }

    private List<Double> coordsToPoints(List<Coords> coordsList) {
        int size = coordsList.size() * 2;
        List<Double> doubles = new ArrayList<>(size);
        for (Coords pos : coordsList) {
            double px = pos.x * Sizes.getMeter();
            double py = pos.y * Sizes.getMeter();
            doubles.add(px);
            doubles.add(py);
        }
        return doubles;
    }

    private void saveLine() {
        List<Double> points = polyline.getPoints();
        List<Coords> coverLine = pointsToCoords(points);
        PosItem i = (PosItem) item;
        i.setCoverLine(coverLine);
        close();
    }
}
