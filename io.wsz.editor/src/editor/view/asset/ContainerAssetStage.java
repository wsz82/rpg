package editor.view.asset;

import editor.view.DoubleField;
import editor.view.IntegerField;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Container;
import io.wsz.model.item.ItemType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ContainerAssetStage extends EquipmentAssetStage<Container>{
    private static final String TITLE = "Container asset";

    private final DoubleField inputNettoWeight = new DoubleField(0.0, isContent);
    private final IntegerField inputNettoSize = new IntegerField(0, isContent);
    private final Button itemsButton = new Button("Items");

    private OpenableContainer openable;

    public ContainerAssetStage(Stage parent, Container item, boolean isContent, EditorCanvas editorCanvas) {
        super(parent, item, isContent, editorCanvas);
        initWindow();
    }

    public ContainerAssetStage(Stage parent, EditorCanvas editorCanvas) {
        super(parent, editorCanvas);
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

        container.getChildren().addAll(weightBox, sizeBox);

        if (item != null) {
            container.getChildren().add(itemsButton);
            hookupContainerEvents();
        }

        fillInputs();
    }

    private void hookupContainerEvents() {
        itemsButton.setOnAction(e -> {
            ItemsStage<Container> itemsStage = new ItemsStage<>(parent, item);
            itemsStage.show();
        });
    }

    @Override
    protected void fillInputs() {
        if (item == null) {
            item = createNewAsset();
        }
        openable = new OpenableContainer(this, item, isContent);
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
        ObservableAssets.get().getContainers().add(asset);
    }

    @Override
    protected Container createNewAsset() {
        return new Container(getType());
    }

    @Override
    protected ItemType getType() {
        return ItemType.CONTAINER;
    }
}
