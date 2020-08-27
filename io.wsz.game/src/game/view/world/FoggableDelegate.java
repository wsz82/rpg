package game.view.world;

import game.model.GameController;
import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.textures.Fog;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FoggableDelegate implements Foggable{
    private final GameController gameController;
    private final Controller controller;
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Coords curPos;
    private final Coords nextPiecePos = new Coords();
    private final Coords temp1 = new Coords();
    private final Coords temp2 = new Coords();

    private Coords viewPos;

    public FoggableDelegate(GameController gameController, Canvas canvas, Coords curPos) {
        this.gameController = gameController;
        this.controller = gameController.getController();
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.curPos = curPos;
        this.viewPos = new Coords(0, 0);
    }

    public FoggableDelegate(GameController gameController, Canvas canvas, Coords curPos, Coords viewPos) {
        this(gameController, canvas, curPos);
        this.viewPos = viewPos;
    }

    public void drawFog(List<Creature> heroes, double width, double height) {
        Location loc = controller.getCurrentLocation().getLocation();
        List<List<Boolean>> discoveredFog = loc.getDiscoveredFog();
        int meter = Sizes.getMeter();
        Image piece = getFogImage();
        double fogSize = piece.getWidth() / meter;
        if (discoveredFog == null) {
            discoveredFog = initFogPiecesList(loc, fogSize);
        }

        gc.setImageSmoothing(false);

        int heightPieces = discoveredFog.size();
        int widthPieces = discoveredFog.get(0).size();

        double horizontalVisionRangeFactor = Sizes.HORIZONTAL_VISION_RANGE_FACTOR;
        double verticalVisionRangeFactor = Sizes.VERTICAL_VISION_RANGE_FACTOR;
        double y = 0;
        for (int i = 0; i < heightPieces; i++) {
            if (i != 0) {
                y += fogSize;
            }
            double x = 0;
            for (int j = 0; j < widthPieces; j++) {
                if (j != 0) {
                    x += fogSize;
                }
                nextPiecePos.x = x;
                nextPiecePos.y = y;
                Coords translatedPiecePos = translateCoordsToScreenCoords1(nextPiecePos);
                boolean isPieceWithinHeroView = false;
                for (Creature cr : heroes) {
                    double visionRange = cr.getVisionRange();
                    Coords translatedHeroCenterPos = translateCoordsToScreenCoords2(cr.getCenter());
                    double visWidth = visionRange * horizontalVisionRangeFactor;
                    double visHeight = visionRange * verticalVisionRangeFactor;
                    isPieceWithinHeroView = Geometry.isPointWithinOval(translatedPiecePos, translatedHeroCenterPos, visWidth, visHeight);
                    if (isPieceWithinHeroView) break;
                }
                if (isPieceWithinHeroView) {
                    discoveredFog.get(i).set(j, true);
                } else {
                    boolean isPieceWithinView =
                            nextPiecePos.x + fogSize >= curPos.x && nextPiecePos.y + fogSize >= curPos.y
                                    && nextPiecePos.x <= curPos.x + width && nextPiecePos.y <= curPos.y + height;
                    if (isPieceWithinView) {
                        boolean isPieceDiscovered = discoveredFog.get(i).get(j);
                        if (isPieceDiscovered) {
                            gc.setGlobalAlpha(0.3);
                            drawFogPiece(translatedPiecePos, piece);
                            gc.setGlobalAlpha(1);
                        } else {
                            drawFogPiece(translatedPiecePos, piece);
                        }
                    }
                }
            }
        }
        gc.setImageSmoothing(true);
    }

    private Image getFogImage() {
        File programDir = controller.getProgramDir();
        Fog fog = gameController.getFog();
        return fog.getImage(programDir).getFxImage();
    }

    private List<List<Boolean>> initFogPiecesList(Location loc, double fogSize) {
        List<List<Boolean>> discoveredFog;
        int maxPiecesHeight = (int) Math.ceil(loc.getHeight() / fogSize);
        int maxPiecesWidth = (int) Math.ceil(loc.getWidth() / fogSize);
        discoveredFog = new ArrayList<>(maxPiecesHeight);
        for (int i = 0; i < maxPiecesHeight; i++) {
            ArrayList<Boolean> horList = new ArrayList<>(maxPiecesWidth);
            for (int j = 0; j < maxPiecesWidth; j++) {
                horList.add(false);
            }
            discoveredFog.add(i, horList);
        }
        loc.setDiscoveredFog(discoveredFog);
        return discoveredFog;
    }

    private void drawFogPiece(Coords pos, Image fog) {
        int meter = Sizes.getMeter();
        pos.x *= meter;
        pos.y *= meter;
        gc.drawImage(fog, pos.x, pos.y);
    }

    private Coords translateCoordsToScreenCoords1(Coords pos) {
        temp1.x = pos.x;
        temp1.y = pos.y;
        temp1.subtract(curPos);
        temp1.add(viewPos);
        return temp1;
    }

    private Coords translateCoordsToScreenCoords2(Coords pos) {
        temp2.x = pos.x;
        temp2.y = pos.y;
        temp2.subtract(curPos);
        temp2.add(viewPos);
        return temp2;
    }
}
