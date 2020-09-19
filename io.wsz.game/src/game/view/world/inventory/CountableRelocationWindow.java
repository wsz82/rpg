package game.view.world.inventory;

import io.wsz.model.item.EquipmentMayCountable;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

public class CountableRelocationWindow {
    private static final Double SCROLL_HOR_PART = 0.7;
    private static final Double SCROLL_VER_PART = 0.3;
    private static final Double SCROLL_BUTTON_HOR_PART = 0.1;

    private final Canvas canvas;
    private final GraphicsContext gc;

    private double posX;
    private double posY;
    private double width;
    private double height;
    private boolean isVisible;
    private boolean isOpened;
    private int maxAmount;
    private EventHandler<KeyEvent> keyEvent;
    private InventoryMoveAction moveAction;
    private EquipmentMayCountable[] toLeave;
    private EquipmentMayCountable[] toMove;

    public CountableRelocationWindow(Canvas canvas, GraphicsContext gc) {
        this.canvas = canvas;
        this.gc = gc;
        defineRemovableEvents();
    }

    private void defineRemovableEvents() {
        keyEvent = this::resolveKeyPress;
    }

    private void resolveKeyPress(KeyEvent event) {
        event.consume();
        KeyCode code = event.getCode();
        if (code.equals(KeyCode.ENTER)) {
            leaveWindow();
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

    private void leaveWindow() {
        moveAction.perform();
        removeRemovableEvents();
        isOpened = false;
        isVisible = false;
    }

    private void hookUpRemovableEvents() {
        canvas.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent);
    }

    private void removeRemovableEvents() {
        canvas.removeEventHandler(KeyEvent.KEY_PRESSED, keyEvent);
    }

    public void refresh() {
        if (!isOpened) {
            isOpened = true;
            hookUpRemovableEvents();
        }
        int meter = Sizes.getMeter();
        clearBackground();
        drawMovedPicture(meter);
        drawScrollBar();
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

    private void drawScrollBar() {
        double barWidth = SCROLL_HOR_PART * width;
        double barX = posX + (width-barWidth) / 2;
        double barHeight = SCROLL_VER_PART * height;
        double barY = posY + (height-barHeight) / 2;
        int meter = Sizes.getMeter();
        gc.setFill(Color.WHITE);
        gc.fillRect(barX * meter, barY * meter, barWidth * meter, barHeight * meter);
        drawScrollButton(barWidth, barX, barHeight, barY);
        drawLeftAmount(meter, barX, barY);
        drawMovedAmount(meter, barX, barWidth, barY);
    }

    private void drawMovedAmount(int meter, double barX, double barWidth, double barY) {
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.BLACK);
        String text = String.valueOf(toMove[0].getAmount());
        double x = barX + barWidth;
        gc.fillText(text, x * meter, barY * meter);
    }

    private void drawLeftAmount(int meter, double barX, double barY) {
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.setTextBaseline(VPos.TOP);
        gc.setFill(Color.BLACK);
        String text = String.valueOf(toLeave[0].getAmount());
        gc.fillText(text, barX * meter, barY * meter);
    }

    private void drawScrollButton(double barWidth, double barX, double barHeight, double barY) {
        double buttonWidth = SCROLL_BUTTON_HOR_PART * barWidth;
        int amount = toMove[0].getAmount();

        System.out.println("Amount: " + amount);
        System.out.println("Max amount: " + maxAmount + "\n");

        double relPosX = ((barWidth - buttonWidth) * amount) / (maxAmount);
        double buttonX = barX + relPosX;
        int meter = Sizes.getMeter();
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

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public void setMoveAction(InventoryMoveAction moveAction) {
        this.moveAction = moveAction;
    }
}
