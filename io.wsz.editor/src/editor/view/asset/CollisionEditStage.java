package editor.view.asset;

import editor.view.stage.ChildStage;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CollisionEditStage extends ChildStage {
    private final ObservableList<Polygon> polygons = FXCollections.observableArrayList();
    private final ChoiceBox<Polygon> polygonsCB = new ChoiceBox<>(polygons);
    private final ImageView iv = new ImageView();
    private final Pane ivPane = new Pane();
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final Asset asset;

    public CollisionEditStage(Stage parent, Asset asset) {
        super(parent);
        this.asset = asset;
        initOwner(parent);
        initWindow();
    }

    private void initWindow() {
        setTitle("Collision edit");
        final StackPane r = new StackPane();

        final VBox c = new VBox(5);
        final Image img = asset.getImage();
        iv.setImage(img);
        ivPane.getChildren().add(iv);
        final ScrollPane sc = new ScrollPane();
        sc.setContent(ivPane);

        final HBox btns = new HBox(5);
        btns.getChildren().addAll(cancel, save, polygonsCB);

        c.getChildren().addAll(sc, btns);
        r.getChildren().add(c);

        final Scene scene = new Scene(r);
        setScene(scene);

        setUpChoiceBox();
        hookupEvents();
        restorePolygons();
    }

    private void setUpChoiceBox() {
        polygonsCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(Polygon p) {
                return p.getId();
            }

            @Override
            public Polygon fromString(String s) {
                List<Polygon> singlePolygon = polygons.stream()
                        .filter(p -> p.getId().equals(s))
                        .collect(Collectors.toList());
                if (singlePolygon.isEmpty()) {
                    return null;
                } else {
                    return singlePolygon.get(0);
                }
            }
        });
    }

    private void restorePolygons() {
        PosItem item = (PosItem) asset;
        List<List<Coords>> collisionPolygons = item.getCollisionPolygons();
        if (collisionPolygons == null) {
            return;
        }
        if (collisionPolygons.isEmpty()) {
            return;
        }
        for (List<Coords> poss : collisionPolygons) {
            Polygon p = new Polygon();
            Coords first = poss.get(0);
            int firstX = first.x;
            int firstY = first.y;
            for (Coords pos : poss) {
                int x = pos.x;
                int y = pos.y;
                p.getPoints().add((double) x);
                p.getPoints().add((double) y);
            }
            setUpPolygon(firstX, firstY, p);
            polygons.add(p);
        }
        refreshPolygons();
    }

    private void hookupEvents() {
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> close());
        save.setDefaultButton(true);
        save.setOnAction(e -> savePolygons());
        final ContextMenu cm = new ContextMenu();
        final MenuItem addPoint = new MenuItem("Add point");
        final MenuItem addPolygon = new MenuItem("Add polygon");
        final MenuItem removePolygon = new MenuItem("Remove polygon");
        final MenuItem clear = new MenuItem("Clear all");
        removePolygon.setOnAction(e -> removePolygon());
        clear.setOnAction(e -> clearAll());
        cm.getItems().addAll(addPoint, addPolygon, removePolygon, clear);
        ivPane.setOnContextMenuRequested(e -> openContextMenu(cm, addPoint, addPolygon, e));
        ivPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)
                    && e.getClickCount() == 2) {
                addPoint((int) e.getX(), (int) e.getY());
            }
        });
    }

    private void refreshPolygons() {
        List<Polygon> pol = ivPane.getChildren().stream()
                .filter(n -> n instanceof Polygon)
                .map(n -> (Polygon) n)
                .collect(Collectors.toList());
        ivPane.getChildren().removeAll(pol);
        ivPane.getChildren().addAll(polygons);
    }

    private void openContextMenu(ContextMenu cm, MenuItem addPoint, MenuItem addPolygon, ContextMenuEvent e) {
        cm.show(ivPane, e.getScreenX(), e.getScreenY());
        int x = (int) e.getX();
        int y = (int) e.getY();
        addPoint.setOnAction(ev -> addPoint(x, y));
        addPolygon.setOnAction(ev -> addNewPolygon(x, y));
    }

    private void removePolygon() {
        polygons.remove(polygonsCB.getValue());
        refreshPolygons();
    }

    private void clearAll() {
        polygons.forEach(p -> p.getPoints().clear());
        refreshPolygons();
    }

    private void addPoint(int x, int y) {
        Polygon p = polygonsCB.getValue();
        if (p == null) {
            return;
        }
        p.getPoints().addAll((double)x, (double)y);
        refreshPolygons();
    }

    private void addNewPolygon(int x, int y) {
        Polygon p = new Polygon();
        setUpPolygon(x, y, p);
        polygons.add(p);
        polygonsCB.setValue(p);

        addPoint(x, y);
        refreshPolygons();
    }

    private void setUpPolygon(int x, int y, Polygon p) {
        p.setStroke(Color.RED);
        p.setStrokeWidth(1);
        p.setOpacity(0.5);
        p.setId(x + ", " + y);
    }

    private List<Coords> doublesToCoords(List<Double> points) {
        int size = points.size();
        List<Coords> coordsList = new ArrayList<>(size / 2);
        for (int i = 0; i < size - 1; i = i + 2) {
            int px = points.get(i).intValue();
            int py = points.get(i + 1).intValue();
            Coords point = new Coords(px, py);
            coordsList.add(point);
        }
        return coordsList;
    }

    private void savePolygons() {
        if (!polygons.isEmpty()) {
            List<List<Coords>> cp = polygonsToCoords(polygons);
            PosItem i = (PosItem) asset;
            i.setCollisionPolygons(cp);
        }
        close();
    }

    private List<List<Coords>> polygonsToCoords(List<Polygon> ps) {
        List<List<Coords>> cp = new ArrayList<>(ps.size()/2);
        for (Polygon p : ps) {
            List<Double> doubles = p.getPoints();
            List<Coords> poss = doublesToCoords(doubles);
            cp.add(poss);
        }
        return cp;
    }
}
