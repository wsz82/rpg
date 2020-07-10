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
import javafx.geometry.Insets;
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
    protected final ContextMenu contextMenu = new ContextMenu();

    protected ChoiceBox<Coords> coordsCB = new ChoiceBox<>(coordsList);

    private final ImageView iv = new ImageView();
    private final TextField xPosField = new DoubleField(0.0, false);
    private final TextField yPosField = new DoubleField(0.0, false);
    private final Button deletePoint = new Button("Delete point");
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final HBox coordsCBBox = new HBox(5);
    private final MenuItem addPoint = new MenuItem("Add point");

    private final EventHandler<ActionEvent> coordsChosen = e -> {
        e.consume();
        Coords c = coordsCB.getValue();
        String xVal;
        String yVal;
        if (c != null) {
            xVal = String.valueOf(c.x);
            yVal = String.valueOf(c.y);
        } else {
            xVal = "0.0";
            yVal = "0.0";
        }
        xPosField.setText(xVal);
        yPosField.setText(yVal);
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

    public CoordsShapeEditStage(Stage parent, PosItem item) {
        super(parent);
        this.item = item;
    }

    public void initWindow(boolean isContent, String title) {
        setTitle(title);

        final StackPane r = new StackPane();
        r.setPadding(new Insets(10));

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
        fillList();
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

    protected abstract void fillList();

    protected abstract void restoreShape();

    protected abstract void clearShape();

    protected abstract void refreshShape();

    protected abstract void saveShape();

    protected void setUpContextMenu() {
        final MenuItem clear = new MenuItem("Clear");
        contextMenu.getItems().addAll(addPoint, clear);
        clear.setOnAction(e -> clearShape());
        EventHandler<ContextMenuEvent> onContextMenuRequested = e -> openContextMenu(contextMenu, e);
        pointsPane.setOnContextMenuRequested(onContextMenuRequested);
    }

    protected void openContextMenu(ContextMenu cm, ContextMenuEvent e) {
        cm.show(iv, e.getScreenX(), e.getScreenY());
        addPoint.setOnAction(ev -> {
            Coords point = new Coords(e.getX() / Sizes.getMeter(), e.getY() / Sizes.getMeter());
            addPoint(point);
        });
    }

    private void hookupEvents() {
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> close());
        save.setDefaultButton(true);
        save.setOnAction(e -> saveShape());
        deletePoint.setOnAction(e -> deletePoint());
        pointsPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)
                    && e.getClickCount() == 2) {
                Coords point = new Coords(e.getX() / Sizes.getMeter(), e.getY() / Sizes.getMeter());
                addPoint(point);
            }
        });
        refreshCoordsCB();
        xPosField.textProperty().addListener(xValueListener);
        yPosField.textProperty().addListener(yValueListener);

        setUpContextMenu();
    }

    protected void deletePoint() {
        Coords c = coordsCB.getValue();
        coordsList.remove(c);
        if (!coordsList.isEmpty()) {
            coordsCB.setValue(coordsList.get(0));
        }
        refreshShape();
    }

    protected void refreshCoordsCB() {
        coordsCBBox.getChildren().remove(coordsCB);
        coordsCB = new ChoiceBox<>(coordsList);
        coordsCB.setMaxWidth(100);
        coordsCBBox.getChildren().add(coordsCB);

        coordsCB.removeEventHandler(ActionEvent.ACTION, coordsChosen);
        coordsCB.setOnAction(coordsChosen);
    }

    protected void addPoint(Coords point) {
        coordsList.add(point);
        coordsCB.setValue(point);
        refreshShape();
    }
}
