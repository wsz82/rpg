package editor.view.asset;

import editor.view.DoubleField;
import editor.view.stage.ChildStage;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
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
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public abstract class CoordsShapeEditStage extends ChildStage {
    protected final ObservableList<Coords> coordsList = FXCollections.observableArrayList();
    protected final PosItem item;
    protected final ScrollPane scrollPane = new ScrollPane();
    protected final Pane pointsPane = new Pane();
    protected final HBox controls = new HBox(5);

    private final ImageView iv = new ImageView();
    private final TextField xPosField = new DoubleField(0.0, false);
    private final TextField yPosField = new DoubleField(0.0, false);
    private final Button deletePoint = new Button("Delete point");
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final HBox coordsCBBox = new HBox(5);

    private ChoiceBox<Coords> coordsCB = new ChoiceBox<>(coordsList);

    private final EventHandler<ActionEvent> coordsChosen = e -> {
        Coords c = coordsCB.getValue();
        if (c == null) return;
        xPosField.setText(String.valueOf(c.x));
        yPosField.setText(String.valueOf(c.y));
    };
    private final ChangeListener<String> xValueListener = (observable, oldValue, newValue) -> {
        Coords c = coordsCB.getValue();
        if (c == null) return;
        c.x = Double.parseDouble(newValue);
        refreshCoordsCB();
        coordsCB.setValue(c);
        refreshShape();
    };
    private final ChangeListener<String> yValueListener = (observable, oldValue, newValue) -> {
        Coords c = coordsCB.getValue();
        if (c == null) return;
        c.y = Double.parseDouble(newValue);
        refreshCoordsCB();
        coordsCB.setValue(c);
        refreshShape();
    };

    public CoordsShapeEditStage(Stage parent, PosItem item, boolean isContent) {
        super(parent);
        this.item = item;
    }

    public void initWindow(boolean isContent) {
        final StackPane r = new StackPane();

        final VBox c = new VBox(5);
        final Image img = item.getImage();
        iv.setImage(img);
        pointsPane.getChildren().add(iv);

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        scrollPane.setMaxSize(bounds.getWidth() - bounds.getWidth()/10,
                bounds.getHeight() - bounds.getHeight()/5);
        scrollPane.setContent(pointsPane);

        final HBox xBox = new HBox(5);
        final Label xLabel = new Label("X:");
        xBox.getChildren().addAll(xLabel, xPosField);
        xPosField.setPrefWidth(50);
        final HBox yBox = new HBox(5);
        final Label yLabel = new Label("Y:");
        yBox.getChildren().addAll(yLabel, yPosField);
        yPosField.setPrefWidth(50);
        coordsCBBox.setPrefWidth(Sizes.getMeter());

        controls.getChildren().addAll(cancel, save, coordsCBBox, xBox, yBox, deletePoint);

        c.getChildren().addAll(scrollPane, controls);
        r.getChildren().add(c);

        final Scene scene = new Scene(r);
        setScene(scene);

        hookupEvents();
        fillList(item);
        restoreShape();

        if (isContent) {
            pointsPane.setDisable(true);
            save.setDisable(true);
        }
    }

    protected List<Double> coordsToPoints(List<Coords> coordsList) {
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

    protected abstract void fillList(PosItem item);

    protected abstract void restoreShape();

    protected abstract void clearShape();

    protected abstract void refreshShape();

    private void hookupEvents() {
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> close());
        save.setDefaultButton(true);
        save.setOnAction(e -> saveShape());
        deletePoint.setOnAction(e -> deletePoint());
        final ContextMenu cm = new ContextMenu();
        final MenuItem addPoint = new MenuItem("Add point");
        final MenuItem clear = new MenuItem("Clear");
        clear.setOnAction(ev -> clearShape());
        cm.getItems().addAll(addPoint, clear);
        pointsPane.setOnContextMenuRequested(e -> openContextMenu(cm, addPoint, e));
        pointsPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)
                    && e.getClickCount() == 2) {
                addPoint(e.getX(), e.getY());
            }
        });
        refreshCoordsCB();
        xPosField.textProperty().addListener(xValueListener);
        yPosField.textProperty().addListener(yValueListener);
    }

    private void deletePoint() {
        Coords c = coordsCB.getValue();
        coordsList.remove(c);
        refreshShape();
    }

    private void refreshCoordsCB() {
        coordsCBBox.getChildren().remove(coordsCB);
        coordsCB = new ChoiceBox<>(coordsList);
        coordsCB.setMaxWidth(100);
        coordsCBBox.getChildren().add(coordsCB);

        coordsCB.removeEventHandler(ActionEvent.ACTION, coordsChosen);
        coordsCB.setOnAction(coordsChosen);
    }

    private void openContextMenu(ContextMenu cm, MenuItem addPoint, ContextMenuEvent e) {
        cm.show(iv, e.getScreenX(), e.getScreenY());
        addPoint.setOnAction(ev -> {
            double x = e.getX();
            double y = e.getY();
            addPoint(x, y);
        });
    }

    private void addPoint(double x, double y) {
        x /= Sizes.getMeter();
        y /= Sizes.getMeter();
        Coords point = new Coords(x, y);
        coordsList.add(point);

        coordsCB.setValue(point);

        refreshShape();
    }

    private boolean xAlreadyExists(double x) {
        return coordsList.stream()
                .anyMatch(c ->
                        x > c.x - 0.01
                                && x < c.x + 0.01);
    }

    protected abstract void saveShape();
}
