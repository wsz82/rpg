package game.view.world.inventory;

import game.model.GameController;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.item.Equipment;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

public abstract class EquipmentViewElement extends InventoryViewElement {
    protected static final double SIZE_COLUMN_WIDTH = 0.01;
    protected static final double WEIGHT_COLUMN_WIDTH = 0.01;

    protected final List<Equipment> items = new ArrayList<>(0);
    protected final Coords curPos = new Coords();

    protected double inventoryWidth;
    protected double scrollWidth;
    protected double yScrollPos;
    protected double yScrollButtonHeight;
    protected double maxCurPosY;
    protected boolean isYScrollVisible = true;

    public EquipmentViewElement(Canvas canvas, GameController gameController, Coords mousePos) {
        super(canvas, gameController, mousePos);
    }

    @Override
    public void refresh() {
        super.refresh();
        drawBackground();

        drawVerScroll();
    }

    protected abstract void drawEquipment();

    protected abstract void drawBackground();

    public abstract List<Equipment> getItems();

    @Override
    protected void scrollScrollBar() {
        if (!isScrollDragged || isViewNotVisible()) return;
        double scrollToY = mousePos.y - viewPos.y;
        setCurPosY(scrollToY);
    }

    @Override
    public Coords getFixedDraggedPos(Coords mousePos, Coords draggedCoords,
                                     Equipment dragged, double draggedInitWidth, double draggedInitHeight) {
        Coords local = super.getFixedDraggedPos(mousePos, draggedCoords, dragged, draggedInitWidth, draggedInitHeight);
        Coords resizedImageCorrection =
                adjustCoordsForResizedImage(draggedInitWidth, draggedInitHeight, draggedCoords, dragged);
        local.subtract(resizedImageCorrection);

        Coords currentPos = getCurPos();
        if (local.x < currentPos.x) {
            local.x = currentPos.x;
        } else {
            double max = currentPos.x + getViewWidth() - dragged.getImageWidth();
            if (local.x > max) {
                local.x = max;
            }
        }
        if (local.y < currentPos.y) {
            local.y = currentPos.y;
        } else {
            double max = currentPos.y + getViewHeight() - dragged.getImageHeight();
            if (local.y > max) {
                local.y = max;
            }
        }
        return local;
    }

    private Coords adjustCoordsForResizedImage(double draggedInitWidth, double draggedInitHeight,
                                               Coords draggedCoords, Equipment dragged) {
        double draggedWidth = dragged.getImageWidth();
        double draggedInitX = draggedCoords.x;
        double resizedX = draggedWidth * draggedInitX / draggedInitWidth;

        double draggedHeight = dragged.getImageHeight();
        double draggedInitY = draggedCoords.y;
        double resizedY = draggedHeight * draggedInitY / draggedInitHeight;
        draggedCoords.x = resizedX;
        draggedCoords.y = resizedY;
        return draggedCoords;
    }

    @Override
    public Coords getLocalCoords(Coords pos) {
        TEMP_1.x = pos.x;
        TEMP_1.y = pos.y;
        TEMP_1.subtract(viewPos);
        TEMP_1.add(curPos);
        return TEMP_1;
    }

    @Override
    public Equipment lookForEquipment(double x, double y, Coords draggedCoords) {
        Coords currentPos = getCurPos();
        if (x < currentPos.x || x > currentPos.x + getViewWidth()) return null;
        if (y < currentPos.y || y > currentPos.y + getViewHeight()) return null;
        lookedEquipment.clear();
        lookedEquipment.addAll(getItems());
        Collections.reverse(lookedEquipment);
        for (Equipment eq : lookedEquipment) {
            double cX = eq.getLeft();
            double cWidth = eq.getImageWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) continue;

            double cY = eq.getTop();
            double cHeight = eq.getImageHeight();
            boolean fitY = y >= cY && y <= cY + cHeight;
            if (!fitY) continue;

            Image img = eq.getImage();
            int meter = Sizes.getMeter();
            int imgX = (int) ((x - cX) * meter);
            int imgY = (int) ((y - cY) * meter);
            Color color;
            try {
                color = img.getPixelReader().getColor(imgX, imgY);
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
            boolean isPixelTransparent = color.equals(Color.TRANSPARENT);
            if (isPixelTransparent) continue;
            draggedCoords.x = (double) imgX / meter;
            draggedCoords.y = (double) imgY / meter;
            return eq;
        }
        return null;
    }

    public void scrollUp() {
        Coords curPos = getCurPos();
        double curPosY = curPos.y;
        double minCurPosY = getMinCurPosY();
        if (curPosY <= minCurPosY) {
            return;
        }
        double newY = curPosY - getScrollSpeed();
        newY = Math.max(newY, minCurPosY);
        curPos.y = newY;
    }

    public void scrollDown() {
        Coords curPos = getCurPos();
        double y = curPos.y;
        double maxPos = getMaxCurPosY() - getViewHeight();
        if (y >= maxPos) {
            return;
        }
        double newY = y + getScrollSpeed();
        newY = Math.min(newY, maxPos);
        curPos.y = newY;
    }

    protected void drawVerScroll() {
        if (isYScrollVisible) {
            int meter = Sizes.getMeter();
            double x = (viewPos.x + viewWidth) * meter;

            clearVerScroll(x);
            drawVerScrollButton(x);
        }
    }

    protected void drawVerScrollButton(double x) {
        OptionalDouble optMaxHeight = getItems().stream()
                .mapToDouble(i -> i.getPos().y)
                .max();
        if (dragged != null) {
            maxCurPosY = Math.max(optMaxHeight.orElse(0), dragged.getPos().y);
            maxCurPosY = Math.max(viewHeight, maxCurPosY);
        } else {
            maxCurPosY = Math.max(viewHeight, optMaxHeight.orElse(0));
        }
        maxCurPosY += 1;
        yScrollPos = curPos.y * viewHeight / maxCurPosY;
        yScrollButtonHeight = viewHeight * viewHeight / maxCurPosY;
        if (yScrollPos + yScrollButtonHeight > viewHeight) {
            yScrollPos = viewHeight - yScrollButtonHeight;
        }
        double y = viewPos.y + yScrollPos;

        gc.setFill(Color.GREEN);
        int meter = Sizes.getMeter();
        gc.fillRect(x, y * meter, scrollWidth * meter, yScrollButtonHeight * meter);
    }

    protected void clearVerScroll(double x) {
        gc.setFill(Color.BLUE);
        int meter = Sizes.getMeter();
        gc.fillRect(x, viewPos.y * meter, scrollWidth * meter, viewHeight * meter);
    }

    protected void sortEquipment() {
        double left = curPos.x;
        double right = left + viewWidth;
        double top = curPos.y;
        double bottom = top + viewHeight;

        items.clear();
        getItems().stream()
                .filter(e -> {
                    double eLeft = e.getLeft();
                    double eRight = e.getRight();
                    double eTop = e.getTop();
                    double eBottom = e.getBottom();
                    return Geometry.doOverlap(
                            left, top, right, bottom,
                            eLeft, eTop, eRight, eBottom);
                })
                .collect(Collectors.toCollection(() -> items));
    }

    protected void playInventoryAnimation(Equipment e) {
        EquipmentAnimationPos animationPos = e.getAnimationPos();
        animationPos.setCurAnimation(EquipmentAnimationType.INVENTORY);
        e.getAnimation().play(e);
    }

    protected void playDropAnimation(Equipment e) {
        setDropAnimationPos(e);
        e.getAnimation().play(e);
    }

    protected void drawColumn(double filled, double max, double columnWidth, double columnHeight,
                              double columnX, double columnY, Color backgroundColor) {
        drawColumn(columnWidth, columnHeight, columnX, columnY, backgroundColor);
        double columnFilledHeight = columnHeight * filled / max;
        columnFilledHeight = Math.min(columnFilledHeight, columnHeight);
        double columnFilledY = columnY + columnHeight - columnFilledHeight;
        Color filledColor;
        if (filled < 1.0/3 * max) {
            filledColor = Color.GREEN;
        } else if (filled >= 1.0/3 * max && filled < 2.0/3 * max) {
            filledColor = Color.YELLOW;
        } else {
            filledColor = Color.RED;
        }
        drawColumn(columnWidth, columnFilledHeight, columnX, columnFilledY, filledColor);
    }

    private void drawColumn(double columnWidth, double columnHeight, double columnX, double columnY,
                            Color backgroundColor) {
        gc.setFill(backgroundColor);
        int meter = Sizes.getMeter();
        gc.fillRect(columnX * meter, columnY * meter, columnWidth * meter, columnHeight * meter);
    }

    @Override
    public boolean tryStartDragScroll(double x, double y) {
        double left = viewPos.x;
        double right = left + viewWidth;
        double top = viewPos.y;
        double bottom = top + viewHeight;

        if (!isYScrollVisible) return false;

        boolean isPointWithinScrollBar = x > right && x < right + scrollWidth && y > top && y < bottom;
        if (isPointWithinScrollBar) {
            setCurPosY(y - top);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Coords getExtremePos(Coords mousePos, Coords draggedCoords, Equipment e) {
        if (mousePos.x < viewPos.x) {
            mousePos.x = curPos.x;
        } else {
            double width = viewWidth - e.getImageWidth();
            if (mousePos.x > viewPos.x + width) {
                mousePos.x = curPos.x + width;
            } else {
                mousePos.x = mousePos.x - viewPos.x + curPos.x - draggedCoords.x;
            }
        }
        if (mousePos.y < viewPos.y) {
            mousePos.y = curPos.y;
        } else {
            double height = viewHeight - e.getImageHeight();
            if (mousePos.y > viewPos.y + height) {
                mousePos.y = curPos.y + height;
            } else {
                mousePos.y = mousePos.y - viewPos.y + curPos.y - draggedCoords.y;
            }
        }
        return mousePos;
    }

    public void setCurPosY(double y) {
        y -= yScrollButtonHeight/2;
        double maxY;
        if (y < 0) {
            curPos.y = 0;
        } else if (y > (maxY = viewHeight - yScrollButtonHeight)) {
            curPos.y = maxY / viewHeight * maxCurPosY;
        } else {
            curPos.y = y / viewHeight * maxCurPosY;
        }
    }

    public Coords getCurPos() {
        return curPos;
    }

    public void setScrollWidth(double scrollWidth) {
        this.scrollWidth = scrollWidth;
    }

    public double getMaxCurPosY() {
        return maxCurPosY;
    }

    public double getMinCurPosY() {
        return 0;
    }

    public void setInventoryWidth(double inventoryWidth) {
        this.inventoryWidth = inventoryWidth;
    }

    protected Coords currentPosCorrection(Coords pos) {
        TEMP_1.x = pos.x;
        TEMP_1.y = pos.y;
        TEMP_1.subtract(curPos);
        return TEMP_1;
    }
}
