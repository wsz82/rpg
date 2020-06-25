package editor.view.asset;

import editor.view.stage.ChildStage;
import io.wsz.model.dialog.Answer;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.dialog.Question;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class DialogEditStage extends ChildStage {
    private final Dialog dialog;
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final ObservableList<Answer> answersList = FXCollections.observableArrayList();
    private final CheckBox finishCB = new CheckBox("Finish dialog");
    private TextArea answerText;
    private TableView<QuestionItem> questionsTableView;
    private ListView<Answer> answersListView;
    private VBox questionDetails;
    private ChoiceBox<Answer> answerCB;
    private ChoiceBox<Answer> startAnswerCB;
    private HBox answerBox;
    private HBox startAnswerBox;
    private TextArea questionText;
    private final ChangeListener<QuestionItem> questionTableItemListener = (observable, oldValue, newValue) -> {
        if (questionsTableView.getItems().size() < 1) {
            disableQuestionDetails();
            return;
        }
        if (oldValue != null) {
            saveQuestionItem(oldValue);
        }
        if (newValue != null) {
            questionDetails.setVisible(true);
            Question q = newValue.question;
            questionText.setText(q.getText());
            answerCB.setValue(q.getAnswer());
            finishCB.setSelected(q.isFinish());
        } else {
            disableQuestionDetails();
        }
    };
    private VBox center;

    public DialogEditStage(Stage parent, Dialog dialog) {
        super(parent);
        this.dialog = dialog;
        this.answersList.addAll(dialog.getAnswers());
        initWindow();
    }

    private void initWindow() {
        setTitle("Dialog edit");
        setHeight(800);
        final BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        final VBox left = new VBox(5);
        answersListView = new ListView<>(answersList);

        startAnswerBox = new HBox(5);
        final Label startAnswerLabel = new Label("Start answer");
        startAnswerBox.setAlignment(Pos.CENTER);
        startAnswerCB = new ChoiceBox<>(answersList);
        startAnswerCB.setMaxWidth(100);
        startAnswerCB.setValue(dialog.getStartAnswer());
        startAnswerBox.getChildren().addAll(startAnswerLabel, startAnswerCB);

        left.getChildren().addAll(answersListView, startAnswerBox);
        root.setLeft(left);

        center = new VBox(10);
        center.setPrefWidth(800);
        root.setCenter(center);
        answerText = new TextArea();
        questionsTableView = new TableView<>();

        questionDetails = new VBox(5);
        questionDetails.setVisible(false);
        questionText = new TextArea();
        answerCB = new ChoiceBox<>(answersList);
        answerCB.setMaxWidth(100);
        answerBox = new HBox(5);
        final Label answerLabel = new Label("Answer");
        answerBox.getChildren().addAll(answerLabel, answerCB);
        final HBox boxes = new HBox(5);
        boxes.getChildren().addAll(answerBox, finishCB);
        questionDetails.getChildren().addAll(questionText, boxes);

        final HBox btns = new HBox(5);
        btns.setAlignment(Pos.CENTER_RIGHT);
        btns.getChildren().addAll(cancel, save);
        center.getChildren().addAll(answerText, questionsTableView, questionDetails, btns);

        final Scene scene = new Scene(root);
        setScene(scene);

        initAnswerListView();
        initCenter();
        hookupEvents();
    }

    private void initAnswerListView() {
        answersListView.setMaxWidth(200);
        setUpAnswersListContextMenu();
        hookUpAnswersListEvents();
    }

    private void initCenter() {
        answerText.setPrefSize(300, 200);
        answerText.setWrapText(true);
        initQuestionsTable();
        initQuestionDetails();
    }

    private void initQuestionDetails() {
        hookUpQuestionDetailsEvents();
    }

    private void hookUpQuestionDetailsEvents() {
        answerCB.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (!questionDetails.isVisible()) {
                return;
            }
            QuestionItem qi = questionsTableView.getSelectionModel().getSelectedItem();
            if (qi == null) {
                return;
            }
            qi.question.setAnswer(newValue);
            questionsTableView.refresh();
        });
        finishCB.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!questionDetails.isVisible()) {
                return;
            }
            QuestionItem qi = questionsTableView.getSelectionModel().getSelectedItem();
            qi.question.setFinish(newValue);
            questionsTableView.refresh();
        });
    }

    private void initQuestionsTable() {
        questionsTableView.setEditable(true);
        TableColumn<QuestionItem, Integer> posCol = new TableColumn<>("Position");
        posCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return p.getValue().pos;
            }
        });
        posCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        posCol.setOnEditCommit(t -> {
            int newValue = t.getNewValue();
            QuestionItem qi = questionsTableView.getItems().get(t.getTablePosition().getRow());
            qi.pos = newValue;
            questionsTableView.refresh();
        });
        posCol.setSortable(false);
        questionsTableView.getColumns().add(posCol);

        TableColumn<QuestionItem, String> textCol = new TableColumn<>("Text");
        textCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().question.getText();
            }
        });
        textCol.setCellFactory(TextFieldTableCell.forTableColumn());
        textCol.setEditable(false);
        textCol.setSortable(false);
        questionsTableView.getColumns().add(textCol);

        TableColumn<QuestionItem, String> answerCol = new TableColumn<>("Answer");
        answerCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                Answer answer = p.getValue().question.getAnswer();
                if (answer == null) {
                    return null;
                } else {
                    return answer.toString();
                }
            }
        });
        answerCol.setCellFactory(TextFieldTableCell.forTableColumn());
        answerCol.setEditable(false);
        answerCol.setSortable(false);
        questionsTableView.getColumns().add(answerCol);

        TableColumn<QuestionItem, String> finishCol = new TableColumn<>("Finish");
        finishCol.setCellFactory(TextFieldTableCell.forTableColumn());
        finishCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                boolean finish = p.getValue().question.isFinish();
                return finish ? "yes" : "";
            }
        });
        finishCol.setEditable(false);
        finishCol.setSortable(false);
        questionsTableView.getColumns().add(finishCol);

        setUpQuestionsTableContextMenu();

        questionsTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                disableQuestionDetails();
            }
        });

        addQuestionTableIndexListener();
    }

    private void addQuestionTableIndexListener() {
        questionsTableView.getSelectionModel().selectedItemProperty().addListener(questionTableItemListener);
    }

    private void removeQuestionTableIndexListener() {
        questionsTableView.getSelectionModel().selectedItemProperty().removeListener(questionTableItemListener);
    }

    private void disableQuestionDetails() {
        questionDetails.setVisible(false);
        questionText.setText(null);
        answerCB.setValue(null);
        finishCB.setSelected(false);
    }

    private void saveQuestionItem(QuestionItem qi) {
        qi.question.setText(questionText.getText());
        qi.question.setAnswer(answerCB.getValue());
        qi.question.setFinish(finishCB.isSelected());
        questionsTableView.refresh();
    }

    private void hookUpAnswersListEvents() {
        answersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            removeQuestionTableIndexListener();

            if (oldValue != null) {
                QuestionItem qi = questionsTableView.getSelectionModel().getSelectedItem();
                if (qi != null) {
                    saveQuestionItem(qi);
                }

                saveAnswer(oldValue);
                refreshAnswerChoiceBox();
                refreshStartAnswerChoiceBox();
            }

            if (newValue != null) {
                refreshCenter(newValue);
                center.setVisible(true);
            } else {
                center.setVisible(false);
            }

            addQuestionTableIndexListener();
        });
    }

    private void refreshAnswerChoiceBox() {
        answerBox.getChildren().remove(answerCB);
        answerCB = new ChoiceBox<>(answersList);
        answerCB.setMaxWidth(100);
        answerBox.getChildren().add(answerCB);
        hookUpQuestionDetailsEvents();
    }

    private void refreshStartAnswerChoiceBox() {
        Answer temp = startAnswerCB.getValue();
        startAnswerBox.getChildren().remove(startAnswerCB);
        startAnswerCB = new ChoiceBox<>(answersList);
        startAnswerCB.setMaxWidth(100);
        startAnswerBox.getChildren().add(startAnswerCB);
        startAnswerCB.setValue(temp);
    }

    private void saveAnswer(Answer answer) {
        if (answer == null) {
            return;
        }
        String text = answerText.getText();
        answer.setText(text);
        List<Question> questions = questionItemsToQuestions(questionsTableView.getItems());
        if (questions == null || questions.isEmpty()) {
            answer.getQuestions().clear();
        } else {
            answer.getQuestions().clear();
            answer.getQuestions().addAll(questions);
        }
    }

    private List<Question> questionItemsToQuestions(List<QuestionItem> items) {
        List<QuestionItem> input = new ArrayList<>(items);
        input.sort(Comparator.comparingInt(q -> q.pos));
        List<Question> output = input.stream()
                .map(qi -> {
                    Question q = qi.question;
                    return q;
                })
                .collect(Collectors.toList());
        return output;
    }

    private void refreshCenter(Answer answer) {
        answerText.setText(answer.getText());
        ObservableList<QuestionItem> questionItems = questionsToQuestionItems(answer.getQuestions());
        questionsTableView.setItems(questionItems);
        if (questionItems.isEmpty()) {
            disableQuestionDetails();
        }
    }

    private ObservableList<QuestionItem> questionsToQuestionItems(List<Question> questions) {
        ObservableList<QuestionItem> output = FXCollections.observableArrayList();
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            QuestionItem qi = new QuestionItem(i, q);
            output.add(qi);
        }
        return output;
    }

    private void hookupEvents() {
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> close());
        save.setDefaultButton(true);
        save.setOnAction(e -> saveDialog());
    }

    private void setUpQuestionsTableContextMenu() {
        MenuItem add = new MenuItem("Add");
        add.setOnAction(e -> addNewQuestion());
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(e -> removeQuestion());
        ContextMenu cm = new ContextMenu(add, remove);
        questionsTableView.setContextMenu(cm);
    }

    private void removeQuestion() {
        QuestionItem q = questionsTableView.getSelectionModel().getSelectedItem();
        questionsTableView.getItems().remove(q);
    }

    private void addNewQuestion() {
        ObservableList<QuestionItem> items = questionsTableView.getItems();
        OptionalInt optionalNext = items.stream()
                .mapToInt(q -> q.pos)
                .max();
        int next;
        if (optionalNext.isPresent()) {
            next = optionalNext.getAsInt();
        } else {
            next = 0;
        }
        next++;

        items.add(new QuestionItem(next, new Question()));
    }

    private void setUpAnswersListContextMenu() {
        MenuItem add = new MenuItem("Add");
        add.setOnAction(e -> addNewAnswer());
        MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(e -> removeAnswer());
        ContextMenu cm = new ContextMenu(add, remove);
        answersListView.setContextMenu(cm);
    }

    private void removeAnswer() {
        Answer answer = answersListView.getSelectionModel().getSelectedItem();
        answersList.remove(answer);
    }

    private void addNewAnswer() {
        Answer answer = new Answer();
        answer.setText("new answer");
        answersList.add(answer);
    }

    private void saveDialog() {
        Answer startAnswer = startAnswerCB.getValue();
        dialog.setStartAnswer(startAnswer);

        QuestionItem qi = questionsTableView.getSelectionModel().getSelectedItem();
        if (qi != null) {
            saveQuestionItem(qi);
        }

        Answer answer = answersListView.getSelectionModel().getSelectedItem();
        saveAnswer(answer);

        List<Answer> answers = new ArrayList<>(answersList);
        List<Answer> dialogAnswers = dialog.getAnswers();
        dialogAnswers.clear();
        dialogAnswers.addAll(answers);
        close();
    }

    private class QuestionItem {
        private int pos;
        private final Question question;

        private QuestionItem(int pos, Question question) {
            this.pos = pos;
            this.question = question;
        }
    }
}
