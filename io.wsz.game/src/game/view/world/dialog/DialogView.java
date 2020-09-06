package game.view.world.dialog;

import game.model.GameController;
import game.model.setting.Settings;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.dialog.*;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.util.List;
import java.util.*;

public class DialogView {
    private static final double MAX_HEIGHT = 1.0/2;
    private static final double TEXT_FIELD_WIDTH = 3.0/5;
    private static final double SCROLL_BAR_PART = 1.0/50;

    private final Canvas canvas;
    private final GameController gameController;
    private final GraphicsContext gc;
    private final double offset;
    private final DialogMemento dialogMemento;
    private final Map<Question, VerticalPos> questionsPos = new HashMap<>(0);

    private Question activeQuestion;
    private EventHandler<MouseEvent> clickEvent;
    private EventHandler<MouseEvent> scrollBarStart;
    private EventHandler<MouseEvent> scrollBarStop;
    private EventHandler<ScrollEvent> wheelScroll;
    private double fontSize;
    private int dialogLeft;
    private int dialogTop;
    private int dialogWidth;
    private double caretPos;
    private int dialogHeight;
    private boolean isToRefresh = true;
    private int scrollPos;
    private int scrollButtonHeight;
    private boolean scrollWithButton;

    public DialogView(Canvas canvas, GameController gameController, double offset, DialogMemento dialogMemento) {
        this.canvas = canvas;
        this.gameController = gameController;
        this.offset = offset;
        this.dialogMemento = dialogMemento;
        gc = canvas.getGraphicsContext2D();
        hookupEvents();
    }

    public void refresh() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        fontSize = width / Sizes.getFontSize().getSize();
        dialogTop = (int) height - getViewHeight();
        dialogWidth = (int) (TEXT_FIELD_WIDTH * width);
        dialogLeft = (int) ((width - dialogWidth) / 2);
        caretPos = 0;

        if (dialogMemento == null) {
            endDialog();
        }

        List<DialogItem> dialogs = dialogMemento.getDialogs();
        if (Sizes.isReloadDialogImages()) {
            reloadDialogPictures(dialogs);
            return;
        }

        updatePos();

        if (!isToRefresh) {
            return;
        } else {
            isToRefresh = false;
        }

        clear();

        if (dialogs.isEmpty()) {
            initConversation();
        }

        int lastPos = drawDialogs();
        drawQuestions(lastPos);

        drawScrollBar(dialogTop);
    }

    private void reloadDialogPictures(List<DialogItem> dialogs) {
        Sizes.setReloadDialogImages(false);
        isToRefresh = true;
        setCurPos(0);
        for (DialogItem di : dialogs) {
            loadDialogItemPicture(di);
        }
    }

    private void initConversation() {
        PosItem speaker = dialogMemento.getAnswering();
        Dialog dialog = speaker.getDialog();
        Answer answer = dialog.getGreeting();
        addDialogItem(speaker, answer.getText());
        dialogMemento.setLastAnswer(answer);
    }

    private void hookupEvents() {
        clickEvent = e -> {
            synchronized (gameController.getGameRunner()) {
                MouseButton button = e.getButton();
                if (button.equals(MouseButton.PRIMARY)) {
                    e.consume();
                    if (activeQuestion != null) {
                        addQuestionAndAnswer();
                    }
                } else if (button.equals(MouseButton.SECONDARY)) {
                    e.consume();
                    if (dialogMemento.isFinished()) {
                        endDialog();
                    }
                }
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);

        scrollBarStart = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                startScrollWithButton(e.getX(), e.getY());
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_PRESSED, scrollBarStart);

        scrollBarStop = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                stopScrollWithButton();
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, scrollBarStop);

        wheelScroll = e -> {
            e.consume();
            scrollWithWheel(e);
        };
        canvas.addEventHandler(ScrollEvent.SCROLL, wheelScroll);
    }

    private void scrollWithWheel(ScrollEvent e) {
        double dY = e.getDeltaY();
        if (dY < 0) {
            scrollDown();
        } else {
            scrollUp();
        }
    }

    private void startScrollWithButton(double x, double y) {
        if (dialogHeight <= getViewHeight()) return;
        int scrollWidth = getScrollWidth();
        int buttonX = dialogLeft + dialogWidth;
        int buttonY = dialogTop + scrollPos;
        double dialogBottom = canvas.getHeight();

        if (x > buttonX && x < buttonX + scrollWidth) {
            if (y > buttonY && y < buttonY + scrollButtonHeight) {
                scrollWithButton = true;
            } else if (y > dialogTop && y < dialogBottom) {
                y -= dialogTop;
                setCurPos((int) y);
            }
        }
    }

    private void setCurPos(int y) {
        y -= scrollButtonHeight/2;
        int scrollHeight = getViewHeight();
        int maxY;
        if (y < 0) {
            dialogMemento.setCurPos(0);
        } else if (y > (maxY = scrollHeight - scrollButtonHeight)) {
            dialogMemento.setCurPos(maxY * dialogHeight / scrollHeight);
        } else {
            dialogMemento.setCurPos(y * dialogHeight / scrollHeight);
        }
        isToRefresh = true;
    }

    private void stopScrollWithButton() {
        scrollWithButton = false;
    }

    private void addQuestionAndAnswer() {
        PosItem asking = dialogMemento.getPc();
        addDialogItem(asking, activeQuestion.getText());

        PosItem answering = dialogMemento.getAnswering();
        Dialog dialog = dialogMemento.getAnswering().getDialog();
        String answerID = activeQuestion.getAnswerID();
        Answer answer = dialog.getAnswerByID(answerID);

        if (answer != null) {
            addDialogItem(answering, answer.getText());
        }
        dialogMemento.setLastAnswer(answer);

        isToRefresh = true;
    }

    private void endDialog() {
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
        canvas.removeEventHandler(MouseEvent.MOUSE_PRESSED, scrollBarStart);
        canvas.removeEventHandler(MouseEvent.MOUSE_RELEASED, scrollBarStop);
        canvas.removeEventHandler(ScrollEvent.SCROLL, wheelScroll);
        gameController.endDialog();
    }

    private int getViewHeight() {
        return (int) (canvas.getHeight() * MAX_HEIGHT);
    }

    private void drawScrollBar(int dialogTop) {
        if (dialogHeight <= getViewHeight()) return;
        int right = dialogLeft + dialogWidth;
        int scrollWidth = getScrollWidth();
        int scrollHeight = getViewHeight();

        clearScrollBar(dialogTop, right, scrollWidth, scrollHeight);

        drawScrollButton(dialogTop, right, scrollWidth, scrollHeight);
    }

    private int getScrollWidth() {
        return (int) (canvas.getWidth() * SCROLL_BAR_PART);
    }

    private void drawScrollButton(int dialogTop, int right, int scrollWidth, int scrollHeight) {
        int dialogHeight = Math.max(this.dialogHeight, scrollHeight);
        scrollPos = dialogMemento.getCurPos() * scrollHeight / dialogHeight;
        scrollButtonHeight = scrollHeight * scrollHeight / dialogHeight;
        if (scrollPos + scrollButtonHeight > scrollHeight) {
            scrollPos = scrollHeight - scrollButtonHeight;
        }
        double y = dialogTop + scrollPos;

        gc.setFill(Color.GREEN);
        gc.fillRect(right, y, scrollWidth, scrollButtonHeight);
    }

    private void clearScrollBar(double dialogRectangleTop, double right, int scrollWidth, int scrollHeight) {
        gc.setFill(Color.BLUE);
        gc.fillRect(right, dialogRectangleTop, scrollWidth, scrollHeight);
    }

    private void addDialogItem(PosItem pi, String text) {
        String speakerName = pi.getAssetId();
        SpeakerMark speakerMark = SpeakerMark.NPC;
        if (pi == dialogMemento.getPc()) {
            speakerMark = SpeakerMark.PC;
        }
        DialogItem di = new DialogItem(speakerMark, speakerName, text);
        dialogMemento.getDialogs().add(di);
    }

    private int drawDialogs() {
        int lastPos = 0;
        List<DialogItem> dialogs = dialogMemento.getDialogs();
        for (DialogItem di : dialogs) {

            lastPos = drawText(lastPos, di);

            int lastIndex = dialogs.size() - 1;
            if (di == dialogs.get(lastIndex)) {
                dialogHeight = lastPos;
            }
        }
        return lastPos;
    }

    private Image getTextImage(TextFlow tf) {
        Group s = new Group(tf);
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        Image snapshot = s.snapshot(sp, null); //TODO readable decreased quality pictures
        return snapshot;
    }

    private TextFlow getDialogTextFlow(DialogItem di) {
        String speakerName = di.getSpeakerName();
        String ownerText = speakerName + " - ";
        Text owner = new Text(ownerText);
        owner.setFont(new Font(fontSize));
        Color speakerNameColor = Color.RED;
        if (di.getSpeakerMark() == SpeakerMark.PC) {
            speakerNameColor = Color.BLUE;
        }
        owner.setFill(speakerNameColor);

        String answerText = di.getText();
        Text answer = new Text();
        answer.setFont(new Font(fontSize));
        answer.setText(answerText);

        TextFlow tf = new TextFlow();
        tf.getChildren().addAll(owner, answer);
        tf.setTextAlignment(TextAlignment.JUSTIFY);
        tf.setMaxWidth(dialogWidth);
        return tf;
    }

    private int drawText(int lastPos, Image img) {
        double height = img.getHeight();
        double width = img.getWidth();

        int curPos = dialogMemento.getCurPos();
        if (lastPos + height >= curPos) {
            double imgStartY = 0;
            double imgCutHeight = height;
            if (lastPos < curPos) {
                imgStartY = curPos - lastPos;
                imgCutHeight = height - imgStartY;
            }
            gc.drawImage(img, 0, imgStartY, width, imgCutHeight, dialogLeft, caretPos + dialogTop, width, imgCutHeight);
            caretPos += imgCutHeight;
        }

        return lastPos + (int) height;
    }

    private int drawText(int lastPos, DialogItem di) {
        Image img = di.getPicture();
        if (img == null) {
            loadDialogItemPicture(di);
            img = di.getPicture();
        }
        return drawText(lastPos, img);
    }

    private void loadDialogItemPicture(DialogItem di) {
        TextFlow tf = getDialogTextFlow(di);
        Image image = getTextImage(tf);
        di.setPicture(image);
    }

    private void drawQuestions(int lastPos) {
        questionsPos.clear();
        Answer lastAnswer = dialogMemento.getLastAnswer();
        if (lastAnswer == null) {
            dialogMemento.setFinished(true);
            return;
        }
        String questionsID = lastAnswer.getQuestionsID();
        if (questionsID == null) {
            dialogMemento.setFinished(true);
            return;
        }
        PosItem answering = dialogMemento.getAnswering();
        Dialog dialog = answering.getDialog();
        Creature asking = dialogMemento.getPc();
        List<Question> questions = dialog.getQuestionsListByID(questionsID, asking, answering);
        if (questions == null || questions.isEmpty()) {
            dialogMemento.setFinished(true);
            return;
        }

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);

            String text = (i + 1) + ". " + q.getText();
            Text question = new Text();
            question.setFont(new Font(fontSize));
            if (q == activeQuestion) {
                question.setFill(Color.DARKBLUE);
            } else {
                question.setFill(Color.DARKRED);
            }
            question.setText(text);

            TextFlow tf = new TextFlow();
            tf.getChildren().add(question);
            tf.setTextAlignment(TextAlignment.JUSTIFY);
            tf.setMaxWidth(dialogWidth);

            Image img = getTextImage(tf);

            double height = img.getHeight();
            VerticalPos vp = new VerticalPos(lastPos, height);
            questionsPos.put(q, vp);

            lastPos = drawText(lastPos, img);

            if (i == questions.size() - 1) {
                dialogHeight = lastPos;
            }
        }
    }

    private void updatePos() {
        Bounds b = canvas.localToScreen(canvas.getBoundsInLocal());
        if (b == null) {
            return;
        }
        int left = (int) b.getMinX() + dialogLeft;
        int top = (int) b.getMinY() + dialogTop;
        int right = (int) b.getMaxX() - dialogLeft;
        int bottom = (int) b.getMaxY();

        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;

        if (scrollWithButton) {
            setCurPos(y - top);
        }

        if (x < left
                || x > right
                || y < top
                || y > bottom) {
            if (activeQuestion != null) {
                isToRefresh = true;
            }
            activeQuestion = null;
            return;
        }
        if (!pointWithinQuestions(top, y)) {
            if (activeQuestion != null) {
                isToRefresh = true;
            }
            activeQuestion = null;
        }

        updateActiveQuestion(top, y);

        if (y < top+offset && y >= top) {
            scrollUp();
        } else
        if (y > bottom-offset && y <= bottom) {
            scrollDown();
        }
    }

    private boolean pointWithinQuestions(int top, int y) {
        Collection<VerticalPos> values = questionsPos.values();
        OptionalDouble optMin = values.stream()
                .mapToDouble(q -> q.top)
                .min();
        OptionalDouble optMax = values.stream()
                .mapToDouble(q -> q.top + q.height)
                .max();
        double minY = optMin.orElse(0);
        int curPos = dialogMemento.getCurPos();
        double translatedMin = minY - curPos + top;
        double maxY = optMax.orElse(0);
        double translatedMax = maxY - curPos + top;

        return y > translatedMin && y < translatedMax;
    }


    private void updateActiveQuestion(int top, int y) {
        Set<Question> keySet = questionsPos.keySet();
        for (Question q : keySet) {
            VerticalPos vp = questionsPos.get(q);
            double topQ = vp.top;
            double translatedTopQ = topQ - dialogMemento.getCurPos() + top;
            double translatedBottomQ = translatedTopQ + vp.height;

            if (y > translatedTopQ && y < translatedBottomQ) {
                if (activeQuestion != q) {
                    isToRefresh = true;
                }
                if (!scrollWithButton) {
                    activeQuestion = q;
                }
            }
        }
    }

    private void scrollDown() {
        int curPos = dialogMemento.getCurPos();
        int newY = curPos + getScrollSpeed();
        int maxPos = getMaxPos();
        if (curPos >= maxPos) {
            return;
        }
        dialogMemento.setCurPos(Math.min(newY, maxPos));
        isToRefresh = true;
    }

    private int getMaxPos() {
        return dialogHeight - getViewHeight();
    }

    private void scrollUp() {
        int curPos = dialogMemento.getCurPos();
        if (curPos == 0) {
            return;
        }
        int newY = curPos - getScrollSpeed();
        dialogMemento.setCurPos(Math.max(newY, 0));
        isToRefresh = true;
    }

    private void clear() {
        gc.setFill(Color.LIGHTGREY);
        double width = canvas.getWidth();
        gc.fillRect(0, dialogTop, width, getViewHeight());
    }

    private int getScrollSpeed() {
        return (int) (Settings.getDialogScrollSpeed() * canvas.getWidth() / Sizes.getMeter() * 3);
    }

    private class VerticalPos {
        private final double top;
        private final double height;

        public VerticalPos(double top, double height) {
            this.top = top;
            this.height = height;
        }
    }
}
