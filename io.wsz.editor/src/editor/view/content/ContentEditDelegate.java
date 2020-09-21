package editor.view.content;

import editor.model.EditorController;
import editor.view.asset.AssetStage;
import editor.view.asset.cover.CoverAssetStage;
import editor.view.asset.creature.CreatureAssetStage;
import editor.view.asset.equipment.container.ContainerAssetStage;
import editor.view.asset.equipment.countable.misc.MiscAssetStage;
import editor.view.asset.equipment.countable.weapon.WeaponAssetStage;
import editor.view.asset.indoor.InDoorAssetStage;
import editor.view.asset.landscape.LandscapeAssetStage;
import editor.view.asset.outdoor.OutDoorAssetStage;
import editor.view.asset.teleport.TeleportAssetStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.*;
import javafx.stage.Stage;

public class ContentEditDelegate {

    public void openEditWindow(Stage parent, PosItem pi, EditorCanvas editorCanvas, EditorController editorController) {
        if (pi == null) {
            return;
        }
        ItemType type = pi.getType();
        AssetStage itemStage = switch (type) {
            case CREATURE -> new CreatureAssetStage(
                    parent, (Creature) pi, true, editorCanvas, editorController);
            case TELEPORT -> new TeleportAssetStage(
                    parent, (Teleport) pi, true, editorCanvas, editorController);
            case LANDSCAPE -> new LandscapeAssetStage(
                    parent, (Landscape) pi, true, editorCanvas, editorController);
            case COVER -> new CoverAssetStage(
                    parent, (Cover) pi, true, editorCanvas, editorController);
            case WEAPON -> new WeaponAssetStage(
                    parent, (Weapon) pi, true, editorCanvas, editorController);
            case CONTAINER -> new ContainerAssetStage(
                    parent, (Container) pi, true, editorCanvas, editorController);
            case INDOOR -> new InDoorAssetStage(
                    parent, (InDoor) pi, true, editorCanvas, editorController);
            case OUTDOOR -> new OutDoorAssetStage(
                    parent, (OutDoor) pi, true, editorCanvas, editorController);
            case MISC -> new MiscAssetStage(
                    parent, (Misc) pi, true, editorCanvas, editorController);
        };
        itemStage.show();
    }
}
