package editor.view.asset;

import io.wsz.model.item.Container;
import io.wsz.model.item.ItemType;
import javafx.stage.Stage;

import java.util.ArrayList;

public class ContainerAssetStage extends EquipmentAssetStage<Container>{
    private static final String TITLE = "Container asset";

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

        fillInputs();
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
