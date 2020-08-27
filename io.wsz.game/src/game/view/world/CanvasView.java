package game.view.world;

import game.model.GameController;
import io.wsz.model.Controller;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.EquipmentAnimationType;
import io.wsz.model.item.*;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.ItemsComparator;
import io.wsz.model.textures.CreatureBase;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class CanvasView {
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
                .filter(PosItem::getVisible)
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
        if (!pi.getActualCoverLine().isEmpty()) {
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
}
