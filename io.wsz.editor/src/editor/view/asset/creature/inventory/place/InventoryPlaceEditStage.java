package editor.view.asset.creature.inventory.place;

import editor.model.EditorController;
import editor.view.asset.coords.CoordsShapeEditStage;
import editor.view.utilities.ToStringConverter;
import io.wsz.model.item.Creature;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.stage.Coords;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class InventoryPlaceEditStage extends CoordsShapeEditStage<Creature> {
    private final EditorController editorController;
    private final Map<InventoryPlaceType, List<Coords>> initInventoryPlaces;
    private final ObservableList<List<Coords>> places = FXCollections.observableArrayList();
    private final ObservableList<InventoryPlaceType> types = FXCollections.observableArrayList();
    private final ChoiceBox<InventoryPlaceType> typesCB;
    private final MenuItem addPlace = new MenuItem("Add place");
    private final Button deletePlace = new Button("Delete place");

    private VBox placeCBVBox;
    private ChoiceBox<List<Coords>> placeCB;

    private Map<InventoryPlaceType, List<Coords>> inventoryPlaces;

    public InventoryPlaceEditStage(EditorController editorController, Stage parent, Creature item, Image background,
                                   Map<InventoryPlaceType, List<Coords>> initInventoryPlaces) {
        super(parent, item, background);
        this.editorController = editorController;
        this.initInventoryPlaces = initInventoryPlaces;
        restoreTypes();
        this.typesCB = new ChoiceBox<>(types);
    }

    private void restoreTypes() {
        types.clear();
        types.addAll(editorController.getObservableInventoryPlacesTypes());
        types.add(null);
        types.removeAll(initInventoryPlaces.keySet());
    }

    @Override
    public void initWindow(boolean isContent, String title) {
        super.initWindow(isContent, title);

        placeCBVBox = new VBox(5);
        final Label placeCBLabel = new Label("Place");
        placeCBLabel.setAlignment(Pos.CENTER);
        typesCB.setPrefWidth(100);
        final Label typeCBLabel = new Label("Set for");
        typeCBLabel.setAlignment(Pos.CENTER);
        placeCBVBox.getChildren().addAll(placeCBLabel, typeCBLabel, typesCB);

        hControls.getChildren().add(2, placeCBVBox);
        vControls.getChildren().add(deletePlace);

        setUpPlaceCB();
        setUpTypesCB();
        hookUpEvents();
    }

    private void hookUpEvents() {
        deletePlace.setOnAction(e -> {
            removePlace();
            refreshShape();
        });
    }

    private void removePlace() {
        List<Coords> place = placeCB.getValue();
        if (place == null) return;
        places.remove(place);
        placeCB.setValue(null);
        InventoryPlaceType type = getInventoryPlaceType(place);
        if (type != null) {
            types.add(type);
            inventoryPlaces.remove(type);
        }
        typesCB.setValue(null);
        coordsList.clear();
    }

    private void setUpPlaceCB() {
        if (placeCB != null) {
            placeCBVBox.getChildren().remove(placeCB);
        }
        placeCB = new ChoiceBox<>(places);
        InventoryPlaceType type = typesCB.getValue();
        if (type != null) {
            String name = type.getId();
            placeCB.setValue(getPlace(name));
        }
        placeCBVBox.getChildren().add(1, placeCB);
        placeCB.setPrefWidth(100);
        placeCB.setConverter(new ToStringConverter<>(placeCB) {
            @Override
            public String toString(List<Coords> place) {
                InventoryPlaceType type = getInventoryPlaceType(place);
                if (type == null) {
                    return "undefined";
                } else {
                    return type.getId();
                }
            }
        });

        placeCB.setOnAction(e -> {
            List<Coords> place = placeCB.getValue();
            if (place == null) return;
            coordsList.clear();
            coordsList.addAll(place);
            coordsCB.setValue(place.get(0));
        });
    }

    private void setUpTypesCB() {
        typesCB.setConverter(new ToStringConverter<>(typesCB) {
            @Override
            public String toString(InventoryPlaceType type) {
                if (type == null) {
                    return "";
                } else {
                    return type.getId();
                }
            }
        });

        typesCB.setOnAction(e -> {
            List<Coords> place = placeCB.getValue();
            if (place == null) {
                return;
            }
            InventoryPlaceType selectedType = typesCB.getValue();
            if (selectedType == null) return;
            types.remove(selectedType);
            InventoryPlaceType actualType = getInventoryPlaceType(place);
            if (actualType != null) {
                types.add(actualType);
            }
            inventoryPlaces.remove(actualType);
            inventoryPlaces.put(selectedType, place);
            setUpPlaceCB();
        });
    }

    private List<Coords> getPlace(String name) {
        for (InventoryPlaceType type : inventoryPlaces.keySet()) {
            String typeName = type.getId();
            if (name.equals(typeName)) {
                return inventoryPlaces.get(type);
            }
        }
        return null;
    }

    private InventoryPlaceType getInventoryPlaceType(List<Coords> place) {
        for (InventoryPlaceType type : inventoryPlaces.keySet()) {
            List<Coords> compared = inventoryPlaces.get(type);
            if (place.equals(compared)) {
                return type;
            }
        }
        return null;
    }

    @Override
    protected double getPointXRefreshFactor() {
        return background.getWidth();
    }

    @Override
    protected double getPointYRefreshFactor() {
        return background.getHeight();
    }

    @Override
    protected void fillList() {
        inventoryPlaces = InventoryPlaceType.cloneInventoryPlacesKeysWithValues(initInventoryPlaces);

        int size = inventoryPlaces.size();
        List<List<Coords>> initPlaces = new ArrayList<>(size);
        initPlaces.addAll(inventoryPlaces.values());
        places.addAll(initPlaces);
    }

    @Override
    protected void restoreShape() {
        if (places == null) {
            return;
        }
        if (places.isEmpty()) {
            return;
        }
        refreshShape();
    }

    @Override
    protected void clearShape() {
        inventoryPlaces.clear();
        restoreTypes();
        places.clear();
        coordsList.clear();
        refreshShape();
    }

    @Override
    protected void refreshShape() {
        super.refreshShape();
        List<Polygon> polygonsShapes = new ArrayList<>(0);
        for (List<Coords> place : places) {
            Polygon p = new Polygon();
            List<Double> points = coordsToPoints(place);
            p.getPoints().addAll(points);

            setUpPolygon(p);
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

    private void setUpPolygon(Polygon p) {
        p.setStroke(Color.RED);
        p.setStrokeWidth(2);
        p.setOpacity(0.5);
    }

    @Override
    protected void addPoint(Coords point) {
        if (placeCB.getValue() == null) {
            addNewPlace(point);
        } else {
            addPointToActualPlace(point);
        }
        coordsCB.setValue(point);
        refreshShape();
    }

    private void addPointToActualPlace(Coords point) {
        placeCB.getValue().add(point);
        coordsList.add(point);
    }

    @Override
    protected void deletePoint() {
        Coords c = coordsCB.getValue();
        if (c == null) return;
        List<Coords> place = placeCB.getValue();
        if (place == null) return;
        coordsList.remove(c);
        place.remove(c);
        if (place.isEmpty()) {
            removePlace();
        }
        super.deletePoint();
        refreshShape();
    }

    @Override
    protected void saveShape() {
        item.getInventory().setInventoryPlaces(inventoryPlaces);
        close();
    }

    @Override
    protected void setUpContextMenu() {
        super.setUpContextMenu();
        final MenuItem removePlace = new MenuItem("Remove place");
        contextMenu.getItems().addAll(addPlace, removePlace);
        removePlace.setOnAction(e -> {
            removePlace();
            refreshShape();
        });
    }

    @Override
    protected void openContextMenu(ContextMenu cm, ContextMenuEvent e) {
        super.openContextMenu(cm, e);
        double x = e.getX();
        double y = e.getY();
        addPlace.setOnAction(ev -> {
            Coords first = new Coords(x / getPointXRefreshFactor(), y / getPointYRefreshFactor());
            addNewPlace(first);
        });
    }

    private void addNewPlace(Coords first) {
        List<Coords> newPlace = new ArrayList<>(1);
        newPlace.add(first);
        InventoryPlaceType type = typesCB.getValue();
        List<Coords> actualPlace = placeCB.getValue();
        if (type != null && actualPlace == null) {
            types.remove(type);
            inventoryPlaces.put(type, newPlace);
        }
        places.add(newPlace);
        placeCB.setValue(newPlace);

        coordsList.clear();
        coordsList.add(first);
        coordsCB.setValue(first);
    }
}
