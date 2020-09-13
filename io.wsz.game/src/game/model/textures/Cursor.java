package game.model.textures;

import io.wsz.model.stage.ResolutionImage;
import javafx.scene.ImageCursor;

import java.io.File;

import static io.wsz.model.sizes.Paths.*;

public class Cursor {
    private ImageCursor mainCursor;
    private ImageCursor upCursor;
    private ImageCursor rightCursor;
    private ImageCursor downCursor;
    private ImageCursor leftCursor;

    public void initCursorsImages(File programDir) {
        String cursorsDir = programDir + TEXTURES_DIR + CURSOR_DIR;

        ResolutionImage main = new ResolutionImage(cursorsDir + MAIN_DIR + PNG);
        mainCursor = new ImageCursor(main.getFxImage());

        ResolutionImage up = new ResolutionImage(cursorsDir + UP_DIR + PNG);
        upCursor = new ImageCursor(up.getFxImage());

        ResolutionImage right = new ResolutionImage(cursorsDir + RIGHT_DIR + PNG);
        rightCursor = new ImageCursor(right.getFxImage());

        ResolutionImage down = new ResolutionImage(cursorsDir + DOWN_DIR + PNG);
        downCursor = new ImageCursor(down.getFxImage());

        ResolutionImage left = new ResolutionImage(cursorsDir + LEFT_DIR + PNG);
        leftCursor = new ImageCursor(left.getFxImage());
    }

    public ImageCursor getMain() {
        return mainCursor;
    }

    public ImageCursor getUp() {
        return upCursor;
    }

    public ImageCursor getRight() {
        return rightCursor;
    }

    public ImageCursor getDown() {
        return downCursor;
    }

    public ImageCursor getLeft() {
        return leftCursor;
    }
}
