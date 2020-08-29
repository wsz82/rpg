package game.view.world;

import game.model.GameController;
import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.location.FogStatus;
import io.wsz.model.location.FogStatusWithImage;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.ResolutionImage;
import io.wsz.model.textures.Fog;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.location.FogStatus.*;

public class FoggableDelegate implements Foggable{
    private final GameController gameController;
    private final Controller controller;
    private final GraphicsContext gc;
    private final Coords curPos;
    private final Coords nextPieceCenterPos = new Coords();
    private final Coords temp1 = new Coords();
    private final Coords temp2 = new Coords();

    private Coords viewPos;

    public FoggableDelegate(GameController gameController, Canvas canvas, Coords curPos) {
        this.gameController = gameController;
        this.controller = gameController.getController();
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
        List<List<FogStatusWithImage>> discoveredFog = loc.getDiscoveredFog();
        int meter = Sizes.getMeter();
        Fog fog = gameController.getFog();
        ResolutionImage piece = fog.getRandomFog();
        double fogSize = piece.getWidth() / meter;
        double half = fogSize / 2;
        if (discoveredFog == null) {
            discoveredFog = initFogPiecesList(loc, fog, half);
        }
        List<FogStatusWithImage> firstRow = discoveredFog.get(0);
        int widthPieces = firstRow.size();
        int heightPieces = discoveredFog.size();

        if (firstRow.get(0).getImage() == null) {
            initFogPiecesImages(discoveredFog, fog, widthPieces);
        }
        gc.setImageSmoothing(false);

        double horizontalVisionRangeFactor = Sizes.HORIZONTAL_VISION_RANGE_FACTOR;
        double verticalVisionRangeFactor = Sizes.VERTICAL_VISION_RANGE_FACTOR;
        double y = -fogSize;
        for (int i = 0; i < heightPieces; i++) {
            if (i != 0) {
                y += half;
            }
            double x = -fogSize;
            List<FogStatusWithImage> horStatuses = discoveredFog.get(i);
            for (int j = 0; j < widthPieces; j++) {
                if (j != 0) {
                    x += half;
                }
                nextPieceCenterPos.x = x + half;
                nextPieceCenterPos.y = y + half;
                Coords translatedPieceCenterPos = translateCoordsToScreenCoords1(nextPieceCenterPos);
                boolean isPieceWithinHeroView = false;
                for (Creature cr : heroes) {
                    double visionRange = cr.getVisionRange();
                    Coords translatedHeroCenterPos = translateCoordsToScreenCoords2(cr.getCenter());
                    double visWidth = visionRange * horizontalVisionRangeFactor;
                    double visHeight = visionRange * verticalVisionRangeFactor;
                    isPieceWithinHeroView = Geometry.isPointWithinOval(translatedPieceCenterPos, translatedHeroCenterPos, visWidth, visHeight);
                    if (isPieceWithinHeroView) break;
                }
                FogStatusWithImage statusWithImage = horStatuses.get(j);
                FogStatus status = statusWithImage.getStatus();
                if (isPieceWithinHeroView) {
                    statusWithImage.setStatus(CLEAR);
                } else if (status == CLEAR) {
                    statusWithImage.setStatus(VISITED);
                }

                boolean isPieceWithinView = nextPieceCenterPos.x >= curPos.x - fogSize
                                && nextPieceCenterPos.y >= curPos.y - fogSize
                                && nextPieceCenterPos.x <= curPos.x + width + fogSize
                                && nextPieceCenterPos.y <= curPos.y + height + fogSize;
                if (isPieceWithinView) {
                    Image fxImage = statusWithImage.getImage().getFxImage();
                    if (status == VISITED) {
                        gc.setGlobalAlpha(0.3);
                    }
                    if (status != CLEAR) {
                        drawFogPiece(translatedPieceCenterPos, half, fxImage);
                    }
                    if (status == VISITED) {
                        gc.setGlobalAlpha(1);
                    }
                }
            }
        }
        gc.setImageSmoothing(true);
    }

    void initFogPiecesImages(List<List<FogStatusWithImage>> discoveredFog, Fog fog, int widthPieces) {
        for (List<FogStatusWithImage> fogStatusesWithImages : discoveredFog) {
            for (int j = 0; j < widthPieces; j++) {
                ResolutionImage randomFog = fog.getRandomFog();
                fogStatusesWithImages.get(j).setImage(randomFog);
            }
        }
    }


    private void drawFogPiece(Coords pos, double half, Image piece) {
        int meter = Sizes.getMeter();
        double x = (pos.x - half) * meter;
        double y = (pos.y - half) * meter;
        gc.drawImage(piece, x, y);
    }

    private List<List<FogStatusWithImage>> initFogPiecesList(Location loc, Fog fog, double fogSize) {
        int maxPiecesHeight = (int) Math.ceil(loc.getHeight() / fogSize) + 2;
        int maxPiecesWidth = (int) Math.ceil(loc.getWidth() / fogSize) + 2;
        List<List<FogStatusWithImage>> discoveredFog = new ArrayList<>(maxPiecesHeight);
        for (int i = 0; i < maxPiecesHeight; i++) {
            ArrayList<FogStatusWithImage> horList = new ArrayList<>(maxPiecesWidth);
            for (int j = 0; j < maxPiecesWidth; j++) {
                ResolutionImage randomFog = fog.getRandomFog();
                FogStatusWithImage statusWithImage = new FogStatusWithImage(UNVISITED, randomFog);
                horList.add(statusWithImage);
            }
            discoveredFog.add(i, horList);
        }
        loc.setDiscoveredFog(discoveredFog);
        return discoveredFog;
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
