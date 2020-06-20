package editor.view.asset;

import io.wsz.model.item.Container;
import io.wsz.model.item.ItemType;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ContainerAssetStage extends EquipmentAssetStage<Container>{
    private static final String TITLE = "Container asset";
    private final Button itemsButton = new Button("Items");

    public ContainerAssetStage(Stage parent, Container item, boolean isContent) {
        super(parent, item, isContent);
        initWindow();
    }

    public ContainerAssetStage(Stage parent) {
        super(parent);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

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
        super.fillInputs();
    }

    @Override
    protected void defineAsset() {
        super.defineAsset();
    }

    @Override
    protected void addAssetToList(Container asset) {
        ObservableAssets.get().getContainers().add(asset);
    }

    @Override
    protected Container createNewAsset(String name, String relativePath) {
        return new Container(
                null, name, getType(), relativePath,
                true, null, null,
                new ArrayList<>(0), new ArrayList<>(0));
    }

    @Override
    protected ItemType getType() {
        return ItemType.CONTAINER;
    }
}
