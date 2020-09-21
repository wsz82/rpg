package editor.view.asset.equipment.container;

import editor.model.EditorController;
import editor.view.asset.items.ItemsStage;
import editor.view.asset.items.TableItem;
import io.wsz.model.item.Container;
import io.wsz.model.item.Equipment;
import javafx.stage.Stage;

public class ContainerItemsStage extends ItemsStage<Container, TableItem> {

    public ContainerItemsStage(Stage parent, Container containable, EditorController editorController) {
        super(parent, containable, editorController);
    }

    @Override
    protected TableItem getNewEquipment(Equipment e) {
        int count = getCount(e);
        return new TableItem(e, count);
    }
}
