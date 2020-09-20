package game.view.world.inventory;

import game.model.GameController;
import game.model.setting.KeyAction;
import io.wsz.model.item.EquipmentMayCountable;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class CountableRelocationWindow {
    private static final Double SCROLL_HOR_PART = 0.7;
    private static final Double SCROLL_VER_PART = 0.2;
    private static final Double SCROLL_BUTTON_HOR_PART = 0.1;
    private static final Double BUTTON_HOR_PART = 0.3;
    private static final Double BUTTON_VER_PART = 0.2;
    private static final Double BUTTONS_PADDING = 0.1;

    private final GameController gameController;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Coords mousePos;

    private double posX;
    private double posY;
    private double width;
    private double height;
    private boolean isVisible;
    private boolean isOpened;
    private int maxAmount;
    private EventHandler<KeyEvent> keyEvent;
    private EventHandler<MouseEvent> mouseEvent;
    private InventoryMoveAction moveAction;
    private EquipmentMayCountable[] toLeave;
    private EquipmentMayCountable[] toMove;
    private double buttonPixelY;
    private double buttonPixelWidth;
    private double buttonPixelHeight;
    private double acceptPixelX;
    private double cancelPixelX;

    public CountableRelocationWindow(GameController gameController, Canvas canvas, GraphicsContext gc, Coords mousePos) {
        this.gameController = gameController;
        this.canvas = canvas;
        this.gc = gc;
        this.mousePos = mousePos;
        defineRemovableEvents();
    }

    private void defineRemovableEvents() {
        keyEvent = this::resolveKeyPress;
        mouseEvent = this::resolveMouseClick;
    }

    private void resolveMouseClick(MouseEvent event) {
        event.consume();
        MouseButton button = event.getButton();
        if (button.equals(MouseButton.PRIMARY)) {
            double x = event.getX();
            double y = event.getY();
            resolveClick(x, y);
        }
    }

    private void resolveClick(double x, double y) {
        if (x > acceptPixelX && x < acceptPixelX + buttonPixelWidth
                && y > buttonPixelY && y < buttonPixelY + buttonPixelHeight) {
            closeWindowWithAccept();
        } else if (x > cancelPixelX && x < cancelPixelX + buttonPixelWidth
                && y > buttonPixelY && y < buttonPixelY + buttonPixelHeight) {
            closeWindowWithCancel();
        }
    }

    private void resolveKeyPress(KeyEvent event) {
        event.consume();
        KeyCode code = event.getCode();
        KeyCode inventoryClose = gameController.getSettings().getKey(KeyAction.INVENTORY);
        if (code.equals(KeyCode.ENTER) || code.equals(KeyCode.ESCAPE) || code.equals(inventoryClose)) {
            if (!code.equals(KeyCode.ENTER)) {
                closeWindowWithCancel();
            } else {
                closeWindowWithAccept();
            }
        } else if (code.equals(KeyCode.RIGHT) || code.equals(KeyCode.LEFT)) {
            adjustMovedAmount(code);
        }
    }

    private void adjustMovedAmount(KeyCode code) {
        EquipmentMayCountable toMove = this.toMove[0];
        int amount = toMove.getAmount();
        if (code.equals(KeyCode.RIGHT)) {
            if (amount < maxAmount) {
                amount++;
                toMove.setAmount(amount);
            }
        } else {
            if (amount > 0) {
                amount--;
                toMove.setAmount(amount);
            }
        }
        this.toLeave[0].setAmount(maxAmount - amount);
    }

    private void closeWindowWithCancel() {
        toMove[0].setAmount(0);
        toLeave[0].setAmount(maxAmount);
        closeWindowWithAccept();
    }

    private void closeWindowWithAccept() {
        moveAction.perform();
        removeRemovableEvents();
        isOpened = false;
        isVisible = false;
    }

    private void hookUpRemovableEvents() {
        canvas.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent);
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent);
    }

    private void removeRemovableEvents() {
        canvas.removeEventHandler(KeyEvent.KEY_PRESSED, keyEvent);
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent);
    }

    public void refresh() {
        if (!isOpened) {
            isOpened = true;
            hookUpRemovableEvents();
        }
        int meter = Sizes.getMeter();
        clearBackground();
        drawMovedPicture(meter);
        drawScrollBar(meter);
        drawAcceptAndCancelButtons(meter);

        setMainCursor();
    }

    private void drawAcceptAndCancelButtons(int meter) {
        double padding = BUTTONS_PADDING * width;
        double buttonWidth = BUTTON_HOR_PART * width;
        buttonPixelWidth = buttonWidth * meter;
        double buttonHeight = BUTTON_VER_PART * height;
        buttonPixelHeight = buttonHeight * meter;
        double buttonY = posY + (height - padding - buttonHeight);
        buttonPixelY = buttonY * meter;
        drawAcceptButton(meter, padding, buttonWidth);
        drawCancelButton(meter, padding);
    }

    private void drawAcceptButton(int meter, double padding, double acceptWidth) {
        double acceptX = posX + (width - padding - acceptWidth);
        acceptPixelX = acceptX * meter;
        gc.setFill(Color.GREEN);
        gc.fillRect(acceptPixelX, buttonPixelY, buttonPixelWidth, buttonPixelHeight);
    }

    private void drawCancelButton(int meter, double padding) {
        double cancelX = posX + padding;
        cancelPixelX = cancelX * meter;
        gc.setFill(Color.RED);
        gc.fillRect(cancelPixelX, buttonPixelY, buttonPixelWidth, buttonPixelHeight);
    }

    private void setMainCursor() {
        boolean mouseWithinWindow = mousePos.x > posX && mousePos.x < posX + width
                && mousePos.y > posY && mousePos.y < posY + height;
        if (mouseWithinWindow) {
            ImageCursor main = gameController.getCursor().getMain();
            canvas.getScene().setCursor(main);
        }
    }

    private void drawMovedPicture(int meter) {
        EquipmentMayCountable countable = toLeave[0];
        countable.getAnimation().play(countable);
        ResolutionImage image = countable.getImage();
        if (image == null) return;
        Image fxImage = image.getFxImage();
        if (fxImage == null) return;
        double imageWidth = fxImage.getWidth();
        double windowHorCenter = (posX + width/2) * meter;
        double x = windowHorCenter - imageWidth/2;
        gc.drawImage(fxImage, x, posY * meter);
    }

    private void drawScrollBar(int meter) {
        double barWidth = SCROLL_HOR_PART * width;
        double barX = posX + (width-barWidth) / 2;
        double barHeight = SCROLL_VER_PART * height;
        double barY = posY + (height-barHeight) / 2;
        gc.setFill(Color.WHITE);
        gc.fillRect(barX * meter, barY * meter, barWidth * meter, barHeight * meter);
        drawScrollButton(barWidth, barX, barHeight, barY, meter);
        drawMovedAmount(meter, barX, barY);
        drawLeftAmount(meter, barX, barWidth, barY);
    }

    private void drawLeftAmount(int meter, double barX, double barWidth, double barY) {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.BLACK);
        String text = String.valueOf(toLeave[0].getAmount());
        double x = barX + barWidth;
        gc.fillText(text, x * meter, barY * meter);
    }

    private void drawMovedAmount(int meter, double barX, double barY) {
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.BLACK);

        String text = String.valueOf(toMove[0].getAmount());
        gc.fillText(text, barX * meter, barY * meter);
    }

    private void drawScrollButton(double barWidth, double barX, double barHeight, double barY, int meter) {
        double buttonWidth = SCROLL_BUTTON_HOR_PART * barWidth;
        int amount = toMove[0].getAmount();

        double relPosX = ((barWidth - buttonWidth) * amount) / (maxAmount);
        double buttonX = barX + relPosX;
        gc.setFill(Color.GREEN);
        gc.fillRect(buttonX * meter, barY * meter, buttonWidth * meter, barHeight * meter);
    }

    private void clearBackground() {
        gc.setFill(Color.GREENYELLOW);
        int meter = Sizes.getMeter();
        gc.fillRect(posX * meter, posY * meter, width * meter, height * meter);
    }

    public void setToLeave(EquipmentMayCountable[] toLeave) {
        this.toLeave = toLeave;
    }

    public void setToMove(EquipmentMayCountable[] toMove) {
        this.toMove = toMove;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setMoveAction(InventoryMoveAction moveAction) {
        this.moveAction = moveAction;
    }
}
