package editor.view.asset.outdoor;

import editor.model.EditorController;
import editor.view.asset.AssetStage;
import editor.view.asset.coords.CoordsEdit;
import editor.view.stage.EditorCanvas;
import io.wsz.model.Controller;
import io.wsz.model.item.OutDoor;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Optional;

public class OutDoorAssetStage extends AssetStage<OutDoor> {
    private static final String TITLE = "OutDoor asset";

    private final ChoiceBox<OutDoor> connectionCB = new ChoiceBox<>();

    private OpenableOutDoor openable;
    private CoordsEdit coordsEdit;

    public OutDoorAssetStage(Stage parent, OutDoor item, boolean isContent,
                             EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, item, isContent, editorCanvas, editorController);
        initWindow();
    }

    public OutDoorAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, editorCanvas, editorController);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        final HBox connectionBox = new HBox(5);
        final Label connectionLabel = new Label("Connection");
        connectionBox.getChildren().addAll(connectionLabel, connectionCB);
        container.getChildren().add(connectionBox);

        hookUpOutDoorEvents();

        fillInputs();
    }

    private void hookUpOutDoorEvents() {
        setUpConnectionCB();
    }

    private void setUpConnectionCB() {
        connectionCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(OutDoor o) {
                if (o == null) {
                    return "";
                }
                return o.getAssetId() + "|" + o.getPos().toString();
            }

            @Override
            public OutDoor fromString(String s) {
                if (s == null || s.isEmpty()) {
                    return null;
                }
                return getConnection(s, controller);
            }
        });
    }

    private void setConnectionCBitems(Location exitLocation) {
        if (exitLocation == null) return;
        List<OutDoor> outDoors = exitLocation.getItemsList().getOutDoors();
        ObservableList<OutDoor> outDoorsObservable = FXCollections.observableArrayList(outDoors);
        connectionCB.setItems(outDoorsObservable);
        outDoorsObservable.add(null);
    }

    private OutDoor getConnection(String s, Controller controller) {
        String[] nameAndPos = s.split("/|");
        String name = "";
        String posText = "";
        if (nameAndPos.length > 0) {
            name = nameAndPos[0];
        }
        if (nameAndPos.length > 1) {
            posText = nameAndPos[1];
        }
        String outDoorName = name;
        Coords pos = Coords.parseCoords(posText, controller);
        Optional<OutDoor> optConnection = connectionCB.getItems().stream()
                .filter(o -> o.getAssetId().equals(outDoorName))
                .filter(o -> o.getPos().equals(pos))
                .findFirst();
        return optConnection.orElse(null);
    }

    @Override
    protected void fillInputs() {
        openable = new OpenableOutDoor(this, item, item.getOpenableItem(), isContent);
        openable.initOpenable(container);
        coordsEdit = new CoordsEdit(item.getIndividualExit(), isContent, editorCanvas, controller);
        coordsEdit.initCoords(container);

        super.fillInputs();
        openable.fillOpenableInputs();

        Location exitLoc = coordsEdit.getLocationChoice().getValue();
        setConnectionCBitems(exitLoc);
        OutDoor connection = item.getIndividualConnection();
        connectionCB.setValue(connection);
        addLocationExitListener();
    }

    private void addLocationExitListener() {
        ChoiceBox<Location> exitLocationCB = coordsEdit.getLocationChoice();
        exitLocationCB.setOnAction(e -> {
            Location exitLocation = exitLocationCB.getValue();
            setConnectionCBitems(exitLocation);
        });
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();
        openable.defineOpenable();
        Coords exit = item.getIndividualExit();
        item.setExit(coordsEdit.defineCoords(exit));
        item.setConnection(connectionCB.getValue());
    }

    @Override
    protected void addAssetToList(OutDoor asset) {
        controller.getObservableAssets().getOutDoors().add(asset);
    }

    @Override
    protected OutDoor getNewAsset() {
        return new OutDoor(controller);
    }

}