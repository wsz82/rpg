package editor.view.asset.equipment.container;

import editor.model.EditorController;
import editor.view.DoubleField;
import editor.view.IntegerField;
import editor.view.asset.ItemsStage;
import editor.view.asset.equipment.EquipmentAssetStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Container;
import io.wsz.model.item.OpenableItem;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ContainerAssetStage extends EquipmentAssetStage<Container> {
    private static final String TITLE = "Container asset";

    private final DoubleField inputNettoWeight = new DoubleField(0.0, isContent);
    private final IntegerField inputNettoSize = new IntegerField(0, isContent);
    private final Button itemsButton = new Button("Items");

    private OpenableContainer openable;

    public ContainerAssetStage(Stage parent, Container item, boolean isContent,
                               EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, item, isContent, editorCanvas, editorController);
        initWindow();
    }

    public ContainerAssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, editorCanvas, editorController);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        container.getChildren().remove(weightBox);

        final HBox weightBox = new HBox(10);
        final Label weightLabel = new Label("Netto weight");
        weightBox.getChildren().addAll(weightLabel, inputNettoWeight);

        final HBox sizeBox = new HBox(10);
        final Label sizeLabel = new Label("Netto size");
        sizeBox.getChildren().addAll(sizeLabel, inputNettoSize);

        container.getChildren().addAll(weightBox, sizeBox, itemsButton);

        hookupContainerEvents();

        fillInputs();
    }

    private void hookupContainerEvents() {
        itemsButton.setOnAction(e -> {
            ItemsStage<Container> itemsStage = new ItemsStage<>(parent, item, editorController);
            itemsStage.show();
        });
    }

    @Override
    protected void fillInputs() {
        OpenableItem openableItem = item.getIndividualOpenableItem();
        openable = new OpenableContainer(this, item, openableItem, isContent);
        openable.initOpenable(container);

        super.fillInputs();
        openable.fillOpenableInputs();

        Double nettoWeight = item.getIndividualNettoWeight();
        if (nettoWeight == null) {
            inputNettoWeight.setText(null);
        } else {
            inputNettoWeight.setText(String.valueOf(nettoWeight));
        }

        Integer nettoSize = item.getIndividualNettoSize();
        if (nettoSize == null) {
            inputNettoSize.setText(null);
        } else {
            inputNettoSize.setText(String.valueOf(nettoSize));
        }
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();
        openable.defineOpenable();

        String nettoWeight = inputNettoWeight.getText();
        if (nettoWeight.isEmpty()) {
            if (isContent) {
                item.setNettoWeight(null);
            } else {
                item.setNettoWeight(0.0);
            }
        } else {
            item.setNettoWeight(Double.parseDouble(nettoWeight));
        }

        String nettoSize = inputNettoSize.getText();
        if (nettoSize.isEmpty()) {
            if (isContent) {
                item.setNettoSize(null);
            } else {
                item.setNettoSize(0);
            }
        } else {
            item.setNettoSize(Integer.parseInt(nettoSize));
        }
    }

    @Override
    protected void addAssetToList(Container asset) {
        editorController.getObservableAssets().getContainers().add(asset);
    }

    @Override
    protected Container getNewAsset() {
        return new Container(controller);
    }

}
