package editor.view.asset.coords;

import editor.view.stage.ChildStage;
import editor.view.utilities.DoubleField;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class CoordsShapeEditStage<A extends PosItem<?,?>> extends ChildStage {
    protected final ObservableList<Coords> coordsList = FXCollections.observableArrayList();
    protected final A item;
    protected final Image background;
    protected final ScrollPane scrollPane = new ScrollPane();
    protected final Pane pointsPane = new Pane();
    protected final HBox hControls = new HBox(5);
    protected final VBox vControls = new VBox(5);
    protected final ContextMenu contextMenu = new ContextMenu();

    protected ChoiceBox<Coords> coordsCB = new ChoiceBox<>(coordsList);

    private final Circle circle = new Circle(5, Color.TRANSPARENT);
    private final ImageView iv = new ImageView();
    private final TextField xPosField = new DoubleField(0.00, false);
    private final TextField yPosField = new DoubleField(0.00, false);
    private final Button deletePoint = new Button("Delete point");
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final HBox coordsCBBox = new HBox(5);
    private final MenuItem addPoint = new MenuItem("Add point");
    private final EventHandler<ActionEvent> coordsChosen;
    private final ChangeListener<String> xValueListener;
    private final ChangeListener<String> yValueListener;

    private Button xLeftArrow;
    private Button xRightArrow;
    private Button yLeftArrow;
    private Button yRightArrow;

    public CoordsShapeEditStage(Stage parent, A item, Image background) {
        super(parent);
        this.item = item;
        this.background = background;
        this.coordsChosen = e -> {
            e.consume();
            Coords point = coordsCB.getValue();
            String xVal;
            String yVal;
            if (point != null) {
                xVal = String.valueOf(point.x);
                yVal = String.valueOf(point.y);
            } else {
                xVal = "0.0";
                yVal = "0.0";
            }
            xPosField.setText(xVal);
            yPosField.setText(yVal);
        };
        this.xValueListener = (observable, oldValue, newValue) -> {
            Coords c = coordsCB.getValue();
            if (c == null) return;
            c.x = Double.parseDouble(newValue);
            refreshCoordsCB();
            coordsCB.setValue(c);
            refreshShape();
        };
        this.yValueListener = (observable, oldValue, newValue) -> {
            Coords c = coordsCB.getValue();
            if (c == null) return;
            c.y = Double.parseDouble(newValue);
            refreshCoordsCB();
            coordsCB.setValue(c);
            refreshShape();
        };
    }

    public void initWindow(boolean isContent, String title) {
        setTitle(title);

        final StackPane r = new StackPane();
        r.setPadding(new Insets(10));

        final VBox c = new VBox(5);
        iv.setImage(background);
        pointsPane.getChildren().add(iv);

        Rectangle2D bounds = Screen.getPrimary().getBounds();
        scrollPane.setMaxSize(bounds.getWidth() - bounds.getWidth()/10,
                bounds.getHeight() - bounds.getHeight()/5);
        scrollPane.setContent(pointsPane);

        final VBox coords = new VBox(5);

        final VBox xVBox = new VBox(5);
        final HBox xButtons = new HBox(5);
        xLeftArrow = new Button("", new ImageView(getLeftArrow()));
        xRightArrow = new Button("", new ImageView(getRightArrow()));
        xButtons.getChildren().addAll(xLeftArrow, xRightArrow);
        final HBox xBox = new HBox(5);
        final Label xLabel = new Label("X:");
        xLabel.setAlignment(Pos.CENTER);
        xVBox.getChildren().addAll(xBox, xButtons);
        xBox.getChildren().addAll(xLabel, xPosField);
        xPosField.setPrefWidth(50);

        final VBox yVBox = new VBox(5);
        final HBox yButtons = new HBox(5);
        yLeftArrow = new Button("", new ImageView(getLeftArrow()));
        yRightArrow = new Button("", new ImageView(getRightArrow()));
        yButtons.getChildren().addAll(yLeftArrow, yRightArrow);
        final HBox yBox = new HBox(5);
        final Label yLabel = new Label("Y:");
        yLabel.setAlignment(Pos.CENTER);
        yVBox.getChildren().addAll(yBox, yButtons);
        yBox.getChildren().addAll(yLabel, yPosField);
        coords.getChildren().addAll(xVBox, yVBox);
        yPosField.setPrefWidth(50);

        final VBox coordsCBVBox = new VBox(5);
        final Label coordsCBLabel = new Label("Point");
        coordsCBLabel.setAlignment(Pos.CENTER);
        coordsCBVBox.getChildren().addAll(coordsCBLabel, coordsCBBox);

        vControls.getChildren().add(deletePoint);
        hControls.getChildren().addAll(cancel, save, coordsCBVBox, coords, vControls);

        c.getChildren().addAll(scrollPane, hControls);
        r.getChildren().add(c);

        circle.setVisible(false);
        circle.setStroke(Color.GREEN);
        circle.setStrokeWidth(2);
        pointsPane.getChildren().add(circle);

        final Scene scene = new Scene(r);
        setScene(scene);

        setUpCoordsCB();
        hookUpShapeEvents();
        fillList();
        restoreShape();

        if (isContent) {
            pointsPane.setDisable(true);
            save.setDisable(true);
        }
    }

    private void setUpCoordsCB() {
        coordsCBBox.setSpacing(5);
        coordsCBBox.setPrefWidth(100);
        coordsCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(Coords point) {
                if (point == null) {
                    return "";
                } else {
                    return point.toShortString();
                }
            }

            @Override
            public Coords fromString(String string) {
                Coords parsed = Coords.parseShortCoords(string);
                for (Coords point : coordsList) {
                    if (point.equals(parsed)) {
                        return point;
                    }
                }
                return null;
            }
        });
    }

    protected List<Double> coordsToPoints(List<Coords> coordsList) {
        int size = coordsList.size() * 2;
        List<Double> doubles = new ArrayList<>(size);
        for (Coords pos : coordsList) {
            double px = pos.x * getPointXRefreshFactor();
            double py = pos.y * getPointYRefreshFactor();
            doubles.add(px);
            doubles.add(py);
        }
        return doubles;
    }

    protected double getPointXRefreshFactor() {
        return Sizes.getMeter();
    }

    protected double getPointYRefreshFactor() {
        return Sizes.getMeter();
    }

    protected abstract void fillList();

    protected abstract void restoreShape();

    protected abstract void clearShape();

    protected void refreshShape() {
        Coords c = coordsCB.getValue();
        if (c == null) {
            circle.setVisible(false);
        } else {
            circle.setCenterX(c.x * getPointXRefreshFactor());
            circle.setCenterY(c.y * getPointYRefreshFactor());
            circle.setVisible(true);
        }
    }

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
            double x = e.getX() / getPointXRefreshFactor();
            double y = e.getY() / getPointYRefreshFactor();
            Coords point = new Coords(x, y);
            addPoint(point);
        });
    }

    private void hookUpShapeEvents() {
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> close());
        save.setDefaultButton(true);
        save.setOnAction(e -> saveShape());
        deletePoint.setOnAction(e -> {
            deletePoint();
            refreshShape();
        });
        pointsPane.setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)
                    && e.getClickCount() == 2) {
                double x = e.getX() / getPointXRefreshFactor();
                double y = e.getY() / getPointYRefreshFactor();
                Coords point = new Coords(x, y);
                addPoint(point);
            }
        });
        refreshCoordsCB();
        xPosField.textProperty().addListener(xValueListener);
        yPosField.textProperty().addListener(yValueListener);

        setUpContextMenu();

        xLeftArrow.setOnAction(e -> {
            Coords c = coordsCB.getValue();
            if (c == null) return;
            double val = getRounded2DecVal(c.x - 0.01);
            setNewPosXVal(c, val);
        });
        xRightArrow.setOnAction(e -> {
            Coords c = coordsCB.getValue();
            if (c == null) return;
            double val = getRounded2DecVal(c.x + 0.01);
            setNewPosXVal(c, val);
        });
        yLeftArrow.setOnAction(e -> {
            Coords c = coordsCB.getValue();
            if (c == null) return;
            double val = getRounded2DecVal(c.y - 0.01);
            setNewPosYVal(c, val);
        });
        yRightArrow.setOnAction(e -> {
            Coords c = coordsCB.getValue();
            if (c == null) return;
            double val = getRounded2DecVal(c.y + 0.01);
            setNewPosYVal(c, val);
        });
    }

    private double getRounded2DecVal(double rawVal) {
        return (int) (Math.round(rawVal * 100)) / 100.0;
    }

    private void setNewPosXVal(Coords c, double v) {
        c.x = v;
        coordsCB.setValue(null);
        coordsCB.setValue(c);
    }

    private void setNewPosYVal(Coords c, double v) {
        c.y = v;
        coordsCB.setValue(null);
        coordsCB.setValue(c);
    }

    protected void deletePoint() {
        Coords c = coordsCB.getValue();
        coordsList.remove(c);
        if (!coordsList.isEmpty()) {
            coordsCB.setValue(coordsList.get(0));
        }
    }

    protected void refreshCoordsCB() {
        coordsCBBox.getChildren().remove(coordsCB);
        coordsCB = new ChoiceBox<>(coordsList);
        setUpCoordsCB();
        coordsCBBox.getChildren().add(coordsCB);

        coordsCB.removeEventHandler(ActionEvent.ACTION, coordsChosen);
        coordsCB.setOnAction(coordsChosen);
    }

    protected void addPoint(Coords point) {
        coordsList.add(point);
        coordsCB.setValue(point);
        refreshShape();
    }

    private Image getRightArrow() {
        return new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(Paths.ARROW_RIGHT_PNG)));
    }

    private Image getLeftArrow() {
        return new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(Paths.ARROW_LEFT_PNG)));
    }
}
