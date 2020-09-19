package game.model.textures;

import io.wsz.model.stage.ResolutionImage;
import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

import java.io.File;

import static io.wsz.model.sizes.Paths.*;

public class Cursor {
    private ImageCursor active;
    private ImageCursor mainCursor;
    private ImageCursor upCursor;
    private ImageCursor rightCursor;
    private ImageCursor downCursor;
    private ImageCursor leftCursor;
    private ImageCursor leftUpCursor;
    private ImageCursor rightUpCursor;
    private ImageCursor rightDownCursor;
    private ImageCursor leftDownCursor;
    private ImageCursor openDoorCursor;
    private ImageCursor closedDoorCursor;
    private ImageCursor openContainerCursor;
    private ImageCursor closedContainerCursor;
    private ImageCursor pickCursor;
    private ImageCursor goCursor;
    private ImageCursor notGoCursor;
    private ImageCursor talkCursor;
    private ImageCursor attackCursor;

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

        ResolutionImage doorOpen = new ResolutionImage(cursorsDir + DOOR_OPEN_DIR + PNG);
        Image doorOpenFx = doorOpen.getFxImage();
        openDoorCursor = new ImageCursor(doorOpenFx, doorOpenFx.getWidth()/2, doorOpenFx.getHeight()/2);

        ResolutionImage doorClosed = new ResolutionImage(cursorsDir + DOOR_CLOSED_DIR + PNG);
        Image doorClosedFx = doorClosed.getFxImage();
        closedDoorCursor = new ImageCursor(doorClosedFx, doorClosedFx.getWidth()/2, doorClosedFx.getHeight()/2);

        ResolutionImage containerOpen = new ResolutionImage(cursorsDir + CONTAINER_OPEN_DIR + PNG);
        Image containerOpenFx = containerOpen.getFxImage();
        openContainerCursor = new ImageCursor(containerOpenFx, containerOpenFx.getWidth()/2, containerOpenFx.getHeight()/2);

        ResolutionImage containerClosed = new ResolutionImage(cursorsDir + CONTAINER_CLOSED_DIR + PNG);
        Image containerClosedFx = containerClosed.getFxImage();
        closedContainerCursor = new ImageCursor(containerClosedFx, containerClosedFx.getWidth()/2, containerClosedFx.getHeight()/2);

        ResolutionImage pick = new ResolutionImage(cursorsDir + PICK_DIR + PNG);
        Image pickFx = pick.getFxImage();
        pickCursor = new ImageCursor(pickFx, 0, pickFx.getHeight()/2);

        ResolutionImage go = new ResolutionImage(cursorsDir + GO_DIR + PNG);
        Image goFx = go.getFxImage();
        goCursor = new ImageCursor(goFx, goFx.getWidth()/2, goFx.getHeight()/2);

        ResolutionImage notGo = new ResolutionImage(cursorsDir + NOT_GO_DIR + PNG);
        Image notGoFx = notGo.getFxImage();
        notGoCursor = new ImageCursor(notGoFx, notGoFx.getWidth()/2, notGoFx.getHeight()/2);

        ResolutionImage talk = new ResolutionImage(cursorsDir + TALK_DIR + PNG);
        Image talkFx = talk.getFxImage();
        talkCursor = new ImageCursor(talkFx, talkFx.getWidth()/2, talkFx.getHeight()/2);

        ResolutionImage attack = new ResolutionImage(cursorsDir + ATTACK_DIR + PNG);
        Image attackFx = attack.getFxImage();
        attackCursor = new ImageCursor(attackFx, attackFx.getWidth()/2, attackFx.getHeight()/2);
    }

    public ImageCursor getActive() {
        return active;
    }

    public ImageCursor getMain() {
        active = mainCursor;
        return mainCursor;
    }

    public ImageCursor getUp() {
        active = upCursor;
        return upCursor;
    }

    public ImageCursor getRight() {
        active = rightCursor;
        return rightCursor;
    }

    public ImageCursor getDown() {
        active = downCursor;
        return downCursor;
    }

    public ImageCursor getLeft() {
        active = leftCursor;
        return leftCursor;
    }

    public ImageCursor getLeftUpCursor() {
        active = leftUpCursor;
        return leftUpCursor;
    }

    public ImageCursor getRightUpCursor() {
        active = rightUpCursor;
        return rightUpCursor;
    }

    public ImageCursor getRightDownCursor() {
        active = rightDownCursor;
        return rightDownCursor;
    }

    public ImageCursor getLeftDownCursor() {
        active = leftDownCursor;
        return leftDownCursor;
    }

    public ImageCursor getOpenDoorCursor() {
        active = openDoorCursor;
        return openDoorCursor;
    }

    public ImageCursor getClosedDoorCursor() {
        active = closedDoorCursor;
        return closedDoorCursor;
    }

    public ImageCursor getOpenContainerCursor() {
        active = openContainerCursor;
        return openContainerCursor;
    }

    public ImageCursor getClosedContainerCursor() {
        active = closedContainerCursor;
        return closedContainerCursor;
    }

    public ImageCursor getPickCursor() {
        active = pickCursor;
        return pickCursor;
    }

    public ImageCursor getGoCursor() {
        active = goCursor;
        return goCursor;
    }

    public ImageCursor getNotGoCursor() {
        active = notGoCursor;
        return notGoCursor;
    }

    public ImageCursor getTalkCursor() {
        active = talkCursor;
        return talkCursor;
    }

    public ImageCursor getAttackCursor() {
        active = attackCursor;
        return attackCursor;
    }
}
