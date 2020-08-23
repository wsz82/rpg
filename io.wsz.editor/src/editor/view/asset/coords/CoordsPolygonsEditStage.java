package editor.view.asset.coords;

import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CoordsPolygonsEditStage extends CoordsShapeEditStage {
    private final ObservableList<List<Coords>> polygons = FXCollections.observableArrayList();
    private final ChoiceBox<List<Coords>> polygonsCB = new ChoiceBox<>(polygons);
    private final List<List<Coords>> itemPolygons;
    private final MenuItem addPolygon = new MenuItem("Add polygon");
    private final Button deletePolygon = new Button("Delete polygon");

    public CoordsPolygonsEditStage(Stage parent, List<List<Coords>> itemPolygons, PosItem item, Image background) {
        super(parent, item, background);
        this.itemPolygons = itemPolygons;
    }

    @Override
    public void initWindow(boolean isContent, String title) {
        super.initWindow(isContent, title);

        final VBox polygonsCBVBox = new VBox(5);
        final Label polygonsCBLabel = new Label("Polygon");
        polygonsCBLabel.setAlignment(Pos.CENTER);
        polygonsCB.setPrefWidth(100);
        polygonsCBVBox.getChildren().addAll(polygonsCBLabel, polygonsCB);
        hControls.getChildren().add(2, polygonsCBVBox);
        vControls.getChildren().add(deletePolygon);

        setUpPolygonsCB();
        hookUpEvents();

        if (isContent) {
            polygonsCB.setDisable(true);
        }
    }

    private void hookUpEvents() {
        deletePolygon.setOnAction(e -> removePolygon());
    }

    @Override
    protected void fillList() {
        List<List<Coords>> clone = Geometry.cloneCoordsPolygons(itemPolygons);
        polygons.addAll(clone);
    }

    @Override
    protected void restoreShape() {
        if (itemPolygons == null) {
            return;
        }
        if (itemPolygons.isEmpty()) {
            return;
        }
        refreshShape();
    }

    @Override
    protected void clearShape() {
        polygons.clear();
        coordsList.clear();
        refreshShape();
    }

    @Override
    protected void refreshShape() {
        super.refreshShape();
        List<Polygon> polygonsShapes = new ArrayList<>(0);
        for (List<Coords> poss : polygons) {
            Polygon p = new Polygon();
            List<Double> points = coordsToPoints(poss);
            p.getPoints().addAll(points);

            setUpPolygon(poss.get(0), p);
            polygonsShapes.add(p);
        }

        pointsPane.getChildren().addAll(polygonsShapes);
        List<Polygon> pol = pointsPane.getChildren().stream()
                .filter(n -> n instanceof Polygon)
                .map(n -> (Polygon) n)
                .collect(Collectors.toList());
        pointsPane.getChildren().removeAll(pol);
        pointsPane.getChildren().addAll(polygonsShapes);
    }

    @Override
    protected void saveShape() {
        itemPolygons.clear();
        itemPolygons.addAll(polygons);
        close();
    }

    private void setUpPolygonsCB() {
        polygonsCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(List<Coords> p) {
                Coords first = p.get(0);
                return first.toString();
            }

            @Override
            public List<Coords> fromString(String s) {
                Optional<List<Coords>> optPolygon = polygons.stream()
                        .filter(l -> l.toString().equals(s))
                        .findFirst();
                return optPolygon.orElse(null);
            }
        });

        polygonsCB.setOnAction(e -> {
            List<Coords> newList = polygonsCB.getValue();
            if (newList == null) return;
            coordsList.clear();
            coordsList.addAll(newList);
            coordsCB.setValue(newList.get(0));
        });
    }

    @Override
    protected void setUpContextMenu() {
        super.setUpContextMenu();
        final MenuItem removePolygon = new MenuItem("Remove polygon");
        contextMenu.getItems().addAll(addPolygon, removePolygon);
        removePolygon.setOnAction(e -> removePolygon());
    }

    @Override
    protected void openContextMenu(ContextMenu cm, ContextMenuEvent e) {
        super.openContextMenu(cm, e);
        double x = e.getX();
        double y = e.getY();
        addPolygon.setOnAction(ev -> {
            Coords first = new Coords(x / Sizes.getMeter(), y / Sizes.getMeter());
            addNewPolygon(first);
        });
    }

    @Override
    protected void addPoint(Coords point) {
        if (polygonsCB.getValue() == null) {
            addNewPolygon(point);
        } else {
            addPointToActualList(point);
        }
        coordsCB.setValue(point);
        refreshShape();
    }

    @Override
    protected void deletePoint() {
        Coords c = coordsCB.getValue();
        if (c == null) return;
        List<Coords> list = polygonsCB.getValue();
        if (list == null) return;
        coordsList.remove(c);
        list.remove(c);
        if (list.isEmpty()) {
            polygons.remove(list);
            polygonsCB.setValue(null);
        }
        super.deletePoint();
    }

    private void addNewPolygon(Coords first) {
        List<Coords> newPolygon = new ArrayList<>(1);
        newPolygon.add(first);
        polygons.add(newPolygon);
        polygonsCB.setValue(newPolygon);
        coordsList.clear();
        coordsList.add(first);
        coordsCB.setValue(first);
    }

    private void addPointToActualList(Coords point) {
        polygonsCB.getValue().add(point);
        coordsList.add(point);
    }

    private void removePolygon() {
        polygons.remove(polygonsCB.getValue());
        polygonsCB.setValue(null);
        coordsList.clear();
        refreshShape();
    }

    private void setUpPolygon(Coords first, Polygon p) {
        p.setStroke(Color.RED);
        p.setStrokeWidth(1);
        p.setOpacity(0.5);
        p.setId(String.format("%.2f", first.x) + "; " + String.format("%.2f", first.y));
    }
}
