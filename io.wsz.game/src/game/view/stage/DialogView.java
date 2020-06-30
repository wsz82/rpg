package game.view.stage;

import game.model.GameController;
import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.dialog.Answer;
import io.wsz.model.dialog.Question;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.awt.*;
import java.util.List;
import java.util.*;

public class DialogView {
    private static final double MAX_HEIGHT = 0.5;
    private static final double TEXT_FIELD_WIDTH = 0.6;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final double offset;
    private final List<DialogItem> dialogs = new ArrayList<>(0);
    private final Controller controller = Controller.get();
    private final Map<Question, VerticalPos> questionsPos = new HashMap<>(0);

    private Answer lastAnswer;
    private Question activeQuestion;
    private double fontSize;
    private double dialogRectangleTop;
    private double textWidth;
    private double dialogTextLeft;
    private double caretPos;
    private double scrollPos;
    private double dialogHeight;
    private boolean finished;
    private boolean isToRefresh = true;

    private final EventHandler<MouseEvent> clickEvent = e -> {
        synchronized (GameController.get().getGameRunner()) {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                if (activeQuestion != null) {
                    addQuestionAndAnswer();
                }
            } else if (button.equals(MouseButton.SECONDARY)) {
                e.consume();
                if (finished) {
                    endDialog();
                }
            }
        }
    };

    public DialogView(Canvas canvas, double offset) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.offset = offset;
        hookupEvents();
    }

    private void hookupEvents() {
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
    }

    private void addQuestionAndAnswer() {
        PosItem asker = controller.getAsking();
        addDialogItem(asker, activeQuestion.getText());

        if (activeQuestion.getAnswer() != null) {
            PosItem speaker = controller.getAnswering();
            Answer answer = activeQuestion.getAnswer();
            addDialogItem(speaker, answer.getText());

            lastAnswer = answer;
        }

        isToRefresh = true;

        if (activeQuestion.isFinish()) {
            finished = true;
        }
    }

    private void endDialog() {
        canvas.removeEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);
        GameController.get().endDialog();
    }

    public void refresh() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();
        fontSize = width / Sizes.getFontSize().getSize();
        dialogRectangleTop = height - height * MAX_HEIGHT;
        textWidth = TEXT_FIELD_WIDTH * width;
        dialogTextLeft = (width - textWidth) / 2;
        caretPos = 0;

        updatePos();

        if (!isToRefresh) {
            return;
        } else {
            isToRefresh = false;
        }

        clear();

        if (dialogs.isEmpty()) {
            PosItem speaker = controller.getAnswering();
            Answer answer = speaker.getDialog().getStartAnswer();
            addDialogItem(speaker, answer.getText());
            lastAnswer = answer;
        }

        double lastPos = drawDialogs();
        drawQuestions(lastPos);
    }

    private void addDialogItem(PosItem pi, String s) {
        DialogItem di = new DialogItem(pi, s);
        TextFlow tf = getDialogTextFlow(di);
        di.picture = getTextImage(tf);
        dialogs.add(di);
    }

    private double drawDialogs() {
        double lastPos = 0;
        for (DialogItem di : dialogs) {

            lastPos = drawText(lastPos, di.picture);

            int lastIndex = dialogs.size() - 1;
            if (di == dialogs.get(lastIndex)) {
                dialogHeight = lastPos;
            }
        }
        return lastPos;
    }

    private WritableImage getTextImage(TextFlow tf) {
        Group s = new Group(tf);
        SnapshotParameters sp = new SnapshotParameters();
        sp.setFill(Color.TRANSPARENT);
        return s.snapshot(sp, null);
    }

    private TextFlow getDialogTextFlow(DialogItem di) {
        String ownerText = di.speaker.getName() + " - ";
        Text owner = new Text(ownerText);
        owner.setFont(new Font(fontSize));
        owner.setFill(Color.RED);

        String answerText = di.text;
        Text answer = new Text();
        answer.setFont(new Font(fontSize));
        answer.setText(answerText);

        TextFlow tf = new TextFlow();
        tf.getChildren().addAll(owner, answer);
        tf.setTextAlignment(TextAlignment.JUSTIFY);
        tf.setMaxWidth(textWidth);
        return tf;
    }

    private double drawText(double lastPos, Image img) {
        double height = img.getHeight();
        double width = img.getWidth();

        if (lastPos + height >= scrollPos) {
            double imgStartY = 0;
            double imgCutHeight = height;
            if (lastPos < scrollPos) {
                imgStartY = scrollPos - lastPos;
                imgCutHeight = height - imgStartY;
            }
            gc.drawImage(img, 0, imgStartY, width, imgCutHeight, dialogTextLeft, caretPos + dialogRectangleTop, width, imgCutHeight);
            caretPos += imgCutHeight;
        }

        return lastPos + height;
    }

    private void drawQuestions(double lastPos) {
        questionsPos.clear();
        List<Question> questions = lastAnswer.getQuestions();

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
            tf.setMaxWidth(textWidth);

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
        int left = (int) b.getMinX() + (int) dialogTextLeft;
        int top = (int) b.getMinY() + (int) dialogRectangleTop;
        int right = (int) b.getMaxX() - (int) dialogTextLeft;
        int bottom = (int) b.getMaxY();

        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;

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
        double translatedMin = minY - scrollPos + top;
        double maxY = optMax.orElse(0);
        double translatedMax = maxY - scrollPos + top;

        return y > translatedMin && y < translatedMax;
    }


    private void updateActiveQuestion(int top, int y) {
        Set<Question> keySet = questionsPos.keySet();
        for (Question q : keySet) {
            VerticalPos vp = questionsPos.get(q);
            double topQ = vp.top;
            double translatedTopQ = topQ - scrollPos + top;
            double translatedBottomQ = translatedTopQ + vp.height;

            if (y > translatedTopQ && y < translatedBottomQ) {
                if (activeQuestion != q) {
                    isToRefresh = true;
                }
                activeQuestion = q;
            }
        }
    }

    private void scrollDown() {
        double newY = scrollPos + getScroll();
        double maxPos = getMaxPos();
        if (scrollPos >= maxPos) {
            return;
        }
        scrollPos = Math.min(newY, maxPos);
        isToRefresh = true;
    }

    private double getMaxPos() {
        return dialogHeight - canvas.getHeight()*MAX_HEIGHT;
    }

    private void scrollUp() {
        if (scrollPos == 0) {
            return;
        }
        double newY = scrollPos - getScroll();
        scrollPos = Math.max(newY, 0);
        isToRefresh = true;
    }

    private void clear() {
        gc.setFill(Color.LIGHTGREY);
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.fillRect(0, dialogRectangleTop, width, height*MAX_HEIGHT);
    }

    private double getScroll() {
        return Settings.getDialogScrollSpeed() * canvas.getWidth() / Sizes.getMeter() * 3;
    }

    private class DialogItem {
        private final PosItem speaker;
        private final String text;
        private Image picture;

        public DialogItem(PosItem speaker, String text) {
            this.speaker = speaker;
            this.text = text;
        }
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
