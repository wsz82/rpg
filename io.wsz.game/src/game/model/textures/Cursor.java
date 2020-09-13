package game.model.textures;

import io.wsz.model.stage.ResolutionImage;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

import java.io.File;

import static io.wsz.model.sizes.Paths.*;

public class Cursor {
    private ImageCursor mainCursor;
    private ImageCursor upCursor;
    private ImageCursor rightCursor;
    private ImageCursor downCursor;
    private ImageCursor leftCursor;
    private ImageCursor leftUpCursor;
    private ImageCursor rightUpCursor;
    private ImageCursor rightDownCursor;
    private ImageCursor leftDownCursor;

    public void initCursorsImages(File programDir) {
        String cursorsDir = programDir + TEXTURES_DIR + CURSOR_DIR;

        ResolutionImage main = new ResolutionImage(cursorsDir + MAIN_DIR + PNG);
        Image mainFx = main.getFxImage();
        mainCursor = new ImageCursor(mainFx);

        ResolutionImage up = new ResolutionImage(cursorsDir + UP_DIR + PNG);
        Image upFx = up.getFxImage();
        upCursor = new ImageCursor(upFx, upFx.getWidth()/2, 0);

        ResolutionImage right = new ResolutionImage(cursorsDir + RIGHT_DIR + PNG);
        Image rightFx = right.getFxImage();
        rightCursor = new ImageCursor(rightFx, rightFx.getWidth(), rightFx.getHeight()/2);

        ResolutionImage down = new ResolutionImage(cursorsDir + DOWN_DIR + PNG);
        Image downFx = down.getFxImage();
        downCursor = new ImageCursor(downFx, downFx.getWidth()/2, downFx.getHeight());

        ResolutionImage left = new ResolutionImage(cursorsDir + LEFT_DIR + PNG);
        Image leftFx = left.getFxImage();
        leftCursor = new ImageCursor(leftFx, 0, leftFx.getHeight()/2);

        ResolutionImage leftUp = new ResolutionImage(cursorsDir + LEFT_UP_DIR + PNG);
        Image leftUpFx = leftUp.getFxImage();
        leftUpCursor = new ImageCursor(leftUpFx);

        ResolutionImage rightUp = new ResolutionImage(cursorsDir + RIGHT_UP_DIR + PNG);
        Image rightUpFx = rightUp.getFxImage();
        rightUpCursor = new ImageCursor(rightUpFx, rightUpFx.getWidth(), 0);

        ResolutionImage rightDown = new ResolutionImage(cursorsDir + RIGHT_DOWN_DIR + PNG);
        Image rightDownFx = rightDown.getFxImage();
        rightDownCursor = new ImageCursor(rightDownFx, rightDownFx.getWidth(), rightDownFx.getHeight());

        ResolutionImage leftDown = new ResolutionImage(cursorsDir + LEFT_DOWN_DIR + PNG);
        Image leftDownFx = leftDown.getFxImage();
        leftDownCursor = new ImageCursor(leftDownFx, 0, leftDownFx.getHeight());
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

    public ImageCursor getLeftUpCursor() {
        return leftUpCursor;
    }

    public ImageCursor getRightUpCursor() {
        return rightUpCursor;
    }

    public ImageCursor getRightDownCursor() {
        return rightDownCursor;
    }

    public ImageCursor getLeftDownCursor() {
        return leftDownCursor;
    }
}
