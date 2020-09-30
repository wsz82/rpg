package game.view.world;

import game.model.GameController;
import io.wsz.model.Controller;
import io.wsz.model.location.FogStatus;
import io.wsz.model.location.FogStatusWithImage;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.textures.Fog;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

import static io.wsz.model.location.FogStatus.CLEAR;
import static io.wsz.model.location.FogStatus.VISITED;

public class FoggableDelegate implements Foggable{
    private static final Coords TEMP = new Coords();

    private final Controller controller;
    private final GraphicsContext gc;
    private final Coords curPos;
    private final Coords nextPieceCenterPos = new Coords();

    private Coords viewPos;

    public FoggableDelegate(GameController controller, Canvas canvas, Coords curPos) {
        this.controller = controller;
        this.gc = canvas.getGraphicsContext2D();
        this.curPos = curPos;
        this.viewPos = new Coords(0, 0);
    }

    public FoggableDelegate(GameController gameController, Canvas canvas, Coords curPos, Coords viewPos) {
        this(gameController, canvas, curPos);
        this.viewPos = viewPos;
    }

    public void drawFog(double width, double height) {
        Location loc = controller.getCurrentLocation().getLocation();
        Fog fog = controller.getFog();
        double fogSize = fog.getFogSize();
        double half = fog.getHalfFogSize();
        List<List<FogStatusWithImage>> discoveredFog = loc.getDiscoveredFog();
        if (discoveredFog == null) return;
        List<FogStatusWithImage> firstRow = discoveredFog.get(0);
        int widthPieces = firstRow.size();
        int heightPieces = discoveredFog.size();

        gc.setImageSmoothing(false);

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
                Coords translatedPieceCenterPos = translateCoordsToScreenCoords(nextPieceCenterPos);
                FogStatusWithImage statusWithImage = horStatuses.get(j);
                FogStatus status = statusWithImage.getStatus();

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

    private void drawFogPiece(Coords pos, double half, Image piece) {
        int meter = Sizes.getMeter();
        double x = (pos.x - half) * meter;
        double y = (pos.y - half) * meter;
        gc.drawImage(piece, x, y);
    }

    private Coords translateCoordsToScreenCoords(Coords pos) {
        TEMP.x = pos.x;
        TEMP.y = pos.y;
        TEMP.subtract(curPos);
        TEMP.add(viewPos);
        return TEMP;
    }
}
