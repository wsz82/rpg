package game.view.world;

import game.model.GameController;
import game.model.textures.CreatureBase;
import game.model.textures.Cursor;
import io.wsz.model.Controller;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.item.*;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.ItemsComparator;
import javafx.scene.ImageCursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public CanvasView(Canvas canvas, GameController gameController) {
        this.canvas = canvas;
        this.gameController = gameController;
        controller = gameController.getController();
        board = controller.getBoard();
        gc = canvas.getGraphicsContext2D();
    }

    protected void sortItems(Location location, double left, double top, double width, double height, List<PosItem> items, int level) {
        double right = left + width;
        double bottom = top + height;

        items.clear();
        location.getItems().stream()
                .filter(PosItem::getIsVisible)
                .filter(pi -> {
                    double piLeft = pi.getLeft();
                    double piRight = pi.getRight();
                    double piTop = pi.getTop();
                    double piBottom = pi.getBottom();
                    return Geometry.doOverlap(
                            left, top, right, bottom,
                            piLeft, piTop, piRight, piBottom);
                })
                .filter(pi -> pi.getPos().level <= level)
                .collect(Collectors.toCollection(() -> items));

        board.sortPosItems(items);
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

    protected void drawCreatureBase(double x, double y, CreatureSize size, CreatureControl control) {
        int meter = Sizes.getMeter();
        x -= size.getWidth() / 2.0;
        y -= size.getHeight() / 2.0;
        CreatureBase base = CreatureBase.getCreatureBase(size, control);
        if (base == null) return;
        File programDir = controller.getProgramDir();
        Image img = base.getImage(programDir).getFxImage();
        gc.drawImage(img, x * meter, y * meter);
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
        if (selected == null) {
            ImageCursor cursorImg = cursor.getMain();
            setCursor(cursorImg);
            return;
        }
        PosItem item = board.lookForItem(items, pos.x, pos.y, pos.level, CURSOR_TYPES, false);

        if (item == null) {
            item = board.getObstacle(pos, selected, OBSTACLE_TYPES, items);
            ImageCursor cursorImg;
            if (item == null) {
                if (controller.isInventory()) {
                    cursorImg = cursor.getMain();
                } else {
                    cursorImg = cursor.getGoCursor();
                }
            } else {
                cursorImg = cursor.getNotGoCursor();
            }
            setCursor(cursorImg);
        } else if (item instanceof Creature) {
            setCursorForCreature((Creature) item);
        } else if (item instanceof InDoor || item instanceof OutDoor) {
            setCursorForDoor((Openable) item);
        } else if (item instanceof Container) {
            setCursorForContainer((Openable) item);
        } else if (item instanceof Equipment) {
            ImageCursor cursorImg = cursor.getPickCursor();
            setCursor(cursorImg);
        } else {
            ImageCursor cursorImg = cursor.getMain();
            setCursor(cursorImg);
        }
    }

    private void setCursorForContainer(Openable item) {
        Openable container = item;
        ImageCursor imageCursor;
        Cursor cursor = gameController.getCursor();
        if (container.isOpen()) {
            imageCursor = cursor.getOpenContainerCursor();
        } else {
            imageCursor = cursor.getClosedContainerCursor();
        }
        setCursor(imageCursor);
    }

    private void setCursorForDoor(Openable item) {
        Openable door = item;
        ImageCursor imageCursor;
        Cursor cursor = gameController.getCursor();
        if (door.isOpen()) {
            imageCursor = cursor.getOpenDoorCursor();
        } else {
            imageCursor = cursor.getClosedDoorCursor();
        }
        setCursor(imageCursor);
    }

    private void setCursorForCreature(Creature item) {
        Creature creature = item;
        CreatureControl control = creature.getControl();
        ImageCursor cursor;
        if (control == CreatureControl.NEUTRAL) {
            cursor = gameController.getCursor().getTalkCursor();
        } else if (control == CreatureControl.ENEMY) {
            cursor = gameController.getCursor().getAttackCursor();
        } else {
            cursor = gameController.getCursor().getMain();
        }
        setCursor(cursor);
    }

    protected void setCursor(ImageCursor main) {
        canvas.getScene().setCursor(main);
    }
}
