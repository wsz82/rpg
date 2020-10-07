package editor.view.asset.equipment.container;

import editor.model.EditorController;
import editor.view.asset.items.ItemsStage;
import editor.view.asset.items.TableItem;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Container;
import io.wsz.model.item.Equipment;
import javafx.stage.Stage;

public class ContainerItemsStage extends ItemsStage<Container, TableItem> {

    public ContainerItemsStage(Stage parent, Container containable, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, containable, editorCanvas, editorController);
    }

    @Override
    protected TableItem getNewEquipment(Equipment<?,?> equipment) {
        int count = equipment.getAmount();
        return new TableItem(equipment, count);
    }
}
