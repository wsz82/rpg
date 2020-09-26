package game.view.world;

import game.model.GameController;
import game.model.textures.Cursor;
import game.model.world.ItemsSorter;
import io.wsz.model.Controller;
import io.wsz.model.animation.creature.CreatureBaseAnimationType;
import io.wsz.model.animation.cursor.CursorType;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.item.*;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.*;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.item.ItemType.*;

public abstract class CanvasView {
    protected static final ItemType[] CURSOR_TYPES =
            new ItemType[] {CREATURE, CONTAINER, WEAPON, MISC, INDOOR, OUTDOOR};
    protected static final ItemType[] OBSTACLE_TYPES =
            new ItemType[] {LANDSCAPE, COVER, TELEPORT};

    protected final Canvas canvas;
    protected final GraphicsContext gc;
    protected final GameController gameController;
    protected final Controller controller;
    protected final Board board;
    protected final List<Creature> visibleControllables = new ArrayList<>(0);
    protected final CursorSetter cursorSetter;

    protected Coords mousePos;
    protected boolean isCursorOnCountable;
    protected int countableAmount;

    public CanvasView(Canvas canvas, GameController gameController) {
        this.canvas = canvas;
        this.gameController = gameController;
        controller = gameController.getController();
        board = controller.getBoard();
        gc = canvas.getGraphicsContext2D();
        cursorSetter = type -> {
            gameController.getCursor().setCursor(type);
            if (type.isShowAmount()) {
                isCursorOnCountable = true;
                countableAmount = type.getAmount();
            }
        };
    }

    protected void adjustCoverOpacity(Creature cr, PosItem pi) {
        visibleControllables.clear();
        visibleControllables.add(cr);
        adjustCoverOpacity(visibleControllables, pi);
    }

    protected void adjustCoverOpacity(List<Creature> visibleControllables, PosItem pi) {
        if (pi.getActualCoverLine().isEmpty()) return;
        for (Creature cr : visibleControllables) {
            if (pi.getPos().level != cr.getPos().level) {
                continue;
            }
            double crLeft = cr.getLeft();
            double crRight = cr.getRight();
            double crTop = cr.getTop();
            double crBottom = cr.getBottom();

            double piLeft = pi.getLeft();
            double piRight = pi.getRight();
            double piTop = pi.getTop();
            double piBottom = pi.getBottom();
            boolean overlap = Geometry.doOverlap(
                    crLeft, crTop, crRight, crBottom,
                    piLeft, piTop, piRight, piBottom);
            if (!overlap) continue;
            ItemsComparator.Comparison comparison = ItemsComparator.isCovered(cr, pi);
            if (comparison == ItemsComparator.Comparison.LESS) {
                gc.setGlobalAlpha(Sizes.COVER_OPACITY);
                break;
            }
        }
    }

    protected void cutImageAndDraw(double x, double y, Image img, double viewX, double viewY,
                                   double viewWidth, double viewHeight) {
        double width = img.getWidth();
        double height = img.getHeight();
        double startX = 0;
        if (x < 0) {
            startX = -x;
            width = x + width;
        }
        if (x + width > viewWidth) {
            width = viewWidth - x;
        } else if (width > viewWidth) {
            width = viewWidth;
        }

        double startY = 0;
        if (y < 0) {
            startY = -y;
            height = y + height;
        }
        if (y + height > viewHeight) {
            height = viewHeight - y;
        } else if (height > viewHeight) {
            height = viewHeight;
        }

        double destX = 0;
        if (x > 0) {
            destX = x;
        }
        double destY = 0;
        if (y > 0) {
            destY = y;
        }
        gc.drawImage(img, startX, startY, width, height, destX + viewX, destY + viewY, width, height);
    }

    protected void drawCreatureBase(double x, double y, ResolutionImage base) {
        if (base == null) return;
        Image img = base.getFxImage();
        int meter = Sizes.getMeter();
        x = x*meter - img.getWidth()/2.0;
        y = y*meter - img.getHeight()/2.0;
        gc.drawImage(img, x, y);
    }

    protected void setDropAnimationPos(Equipment e) {
        EquipmentAnimationPos animationPos = e.getAnimationPos();
        animationPos.setCurAnimation(EquipmentAnimationType.DROP);
    }

    protected <A extends PosItem> void setAppropriateCursor(Creature selected, Coords pos,
                                                            double minX, double minY,
                                                            double maxX, double maxY,
                                                            List<A> items) {
        if (pos.x < minX || pos.y < minY || pos.x > maxX || pos.y > maxY) {
            return;
        }
        Cursor cursor = gameController.getCursor();
        PosItem item = board.lookForItem(items, pos.x, pos.y, pos.level, CURSOR_TYPES, false);

        if (selected == null) {
            cursor.setCursor(CursorType.MAIN);
            if (item instanceof Creature) {
                Creature cr = (Creature) item;
                setCursorForControllableCreature(cursor, cr);
            }
            return;
        }

        if (item == null) {
            item = board.getObstacle(pos, selected, OBSTACLE_TYPES, items);
            CursorType type;
            if (item == null) {
                if (controller.isInventory()) {
                    type = CursorType.MAIN;
                } else {
                    type = CursorType.GO;
                }
            } else {
                type = CursorType.NOT_GO;
            }
            cursor.setCursor(type);
        } else {
            item.setCursor(cursorSetter);
        }
    }

    private void setCursorForControllableCreature(Cursor cursor, Creature cr) {
        CreatureControl control = cr.getControl();
        if (control == CreatureControl.CONTROLLABLE) {
            cursor.setCursor(CursorType.MAIN);
            cr.getBaseAnimationPos().setBaseAnimationType(CreatureBaseAnimationType.ACTION);
        }
    }

    protected void drawCountableText() {
        isCursorOnCountable = false;
        gc.setFill(Color.BLANCHEDALMOND);
        String text = String.valueOf(countableAmount);
        int meter = Sizes.getMeter();
        gc.setTextBaseline(VPos.CENTER);
        gc.setTextAlign(TextAlignment.RIGHT);
        gc.fillText(text, mousePos.x * meter, mousePos.y * meter);
    }

    public double getWidth() {
        return canvas.getWidth() / Sizes.getMeter();
    }

    public double getHeight() {
        return canvas.getHeight() / Sizes.getMeter();
    }

    public void sortItems(ItemsSorter itemsSorter) {
        itemsSorter.sort();
    }
}
