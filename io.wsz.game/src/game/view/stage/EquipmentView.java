package game.view.stage;

import game.model.GameController;
import io.wsz.model.item.Creature;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class EquipmentView extends Group {
    private final Creature active;
    private final EventHandler<KeyEvent> inventoryClose = e -> {
        e.consume();
        if (e.getCode().equals(KeyCode.I)) {
            GameController.get().resumeGame();
            removeInventoryCloseEvent();
        }
    };

    private final Stage parent;

    public EquipmentView(Creature active, Stage parent) {
        this.active = active;
        this.parent = parent;
        initWindow();
    }

    private void initWindow() {

        hookupEvents();
    }

    private void removeInventoryCloseEvent() {
        parent.removeEventHandler(KeyEvent.KEY_RELEASED, inventoryClose);
    }

    private void hookupEvents() {
        parent.addEventHandler(KeyEvent.KEY_RELEASED, inventoryClose);
    }
}
