package editor.view.dialog;

import editor.model.EditorController;
import editor.view.dialog.requirement.RequirementsListView;
import editor.view.dialog.script.ScriptArea;
import io.wsz.model.dialog.AnswersList;
import io.wsz.model.dialog.Question;
import io.wsz.model.dialog.QuestionsList;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.*;
import java.util.stream.Collectors;

public class QuestionsCenterView {
    private final EditorController editorController;
    private final ObservableList<AnswersList> answersLists;
    private final VBox questionsCenter = new VBox(5);
    private final TableView<QuestionItem> questionsTableView = new TableView<>();
    private final VBox questionDetails = new VBox(5);
    private final HBox questionAnswersListBox = new HBox(5);
    private final TextArea questionText = new TextArea();

    private ChoiceBox<AnswersList> questionAnswersListCB;
    private RequirementsListView questionRequirementsListView;
    private ScriptArea scriptArea;

    public QuestionsCenterView(EditorController editorController, ObservableList<AnswersList> answersLists) {
        this.editorController = editorController;
        this.answersLists = answersLists;
    }

    public void initQuestionsCenter() {
        setUpQuestionsTableView();
        setUpQuestionDetails();
        questionsCenter.getChildren().addAll(questionsTableView, questionDetails);
    }

    public void startEditQuestionsList(QuestionsList questions) {
        ObservableList<QuestionItem> questionItems = questionsToQuestionItems(questions.getQuestions());
        questionsTableView.setItems(questionItems);
        if (questionItems.isEmpty()) {
            disableQuestionDetails();
        }
    }

    public void saveQuestionsList(QuestionsList questionsList) {
        saveCurrentQuestionItem();

        ObservableList<QuestionItem> questionItems = questionsTableView.getItems();
        List<Question> questions = questionItemsToQuestions(questionItems);
        questionsList.setQuestions(questions);
    }

    public void refreshQuestionAnswerCB() {
        boolean wasCreated = questionAnswersListCB != null;
        AnswersList temp = null;
        if (wasCreated) {
            temp = questionAnswersListCB.getValue();
        }
        questionAnswersListBox.getChildren().remove(questionAnswersListCB);
        questionAnswersListCB = new ChoiceBox<>(answersLists);
        questionAnswersListCB.setMaxWidth(100);
        questionAnswersListBox.getChildren().add(questionAnswersListCB);
        if (wasCreated) {
            questionAnswersListCB.setValue(temp);
        }
        hookUpQuestionAnswerCBEvents();
    }

    public void saveCurrentQuestionsList(QuestionsList selectedQuestionsList) {
        List<Question> questions = questionItemsToQuestions(questionsTableView.getItems());
        if (selectedQuestionsList == null) return;
        selectedQuestionsList.setQuestions(questions);
    }

    private void setUpQuestionsTableView() {
        setUpQuestionsTable();
        hookUpQuestionsTableViewEvents();
        setUpQuestionsTableContextMenu();
    }

    private void hookUpQuestionsTableViewEvents() {
        ChangeListener<QuestionItem> questionTableItemListener = (observable, oldQuestionItem, newQuestionItem) -> {
            if (newQuestionItem == null) {
                disableQuestionDetails();
                return;
            }
            if (questionsTableView.getItems().size() < 1) {
                disableQuestionDetails();
                return;
            }
            if (oldQuestionItem != null) {
                saveQuestionItem(oldQuestionItem);
            }
            startEditQuestionDetails(newQuestionItem);
            enableQuestionsDetails();
        };
        questionsTableView.getSelectionModel().selectedItemProperty().addListener(questionTableItemListener);
    }

    private void enableQuestionsDetails() {
        questionDetails.setVisible(true);
    }

    private void startEditQuestionDetails(QuestionItem newQuestionItem) {
        Question question = newQuestionItem.question;
        questionText.setText(question.getText());
        String answerID = question.getAnswersListID();
        AnswersList answersList = getAnswersListWithID(answerID);
        questionAnswersListCB.setValue(answersList);
        questionRequirementsListView.clear();
        questionRequirementsListView.populate(question.getRequirements());
        scriptArea.restoreScript(question.getBeginScript());
    }

    private AnswersList getAnswersListWithID(String answersListID) {
        Optional<AnswersList> optAnswer = answersLists.stream()
                .filter(Objects::nonNull)
                .filter(a -> a.getID().equals(answersListID))
                .findFirst();
        return optAnswer.orElse(null);
    }

    private void setUpQuestionDetails() {
        questionDetails.setVisible(false);
        final Label answerLabel = new Label("NPC text");
        questionAnswersListBox.getChildren().addAll(answerLabel);
        final HBox questionProperties = new HBox(5);
        refreshQuestionAnswerCB();
        questionProperties.getChildren().addAll(questionAnswersListBox);

        questionRequirementsListView = new RequirementsListView(editorController);
        final ScrollPane requirementsScrollPane = questionRequirementsListView.getListScrollPane();

        scriptArea = new ScriptArea(editorController);
        scriptArea.init();
        final VBox scriptArea = this.scriptArea.getScriptArea();

        questionDetails.getChildren().addAll(questionText, questionProperties, requirementsScrollPane, scriptArea);
    }

    private void saveCurrentQuestionItem() {
        QuestionItem qi = questionsTableView.getSelectionModel().getSelectedItem();
        if (qi != null) {
            saveQuestionItem(qi);
        }
    }

    private void hookUpQuestionAnswerCBEvents() {
        questionAnswersListCB.valueProperty().addListener((observable, oldAnswer, newAnswer) -> {
            QuestionItem qi = questionsTableView.getSelectionModel().getSelectedItem();
            if (qi == null) {
                return;
            }
            String answerID;
            if (newAnswer == null) {
                answerID = null;
            } else {
                answerID = newAnswer.getID();
            }
            qi.question.setAnswersListID(answerID);
            questionsTableView.refresh();
        });
    }

    private void setUpQuestionsTable() {
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
            int newPos = t.getNewValue();
            ObservableList<QuestionItem> questionsItems = questionsTableView.getItems();
            QuestionItem qi = questionsItems.get(t.getTablePosition().getRow());
            boolean newPosExists = questionsItems.stream()
                    .anyMatch(q -> q.pos == newPos);
            if (!newPosExists) {
                qi.pos = newPos;
            }
            questionsTableView.refresh();
        });
        posCol.setSortable(false);
        questionsTableView.getColumns().add(posCol);

        TableColumn<QuestionItem, String> textCol = new TableColumn<>("Text");
        textCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                QuestionItem questionItem = p.getValue();
                if (questionItem == null) return "";
                Question question = questionItem.question;
                if (question == null) return "";
                return question.getText();
            }
        });
        textCol.setCellFactory(TextFieldTableCell.forTableColumn());
        textCol.setEditable(false);
        textCol.setSortable(false);
        questionsTableView.getColumns().add(textCol);

        TableColumn<QuestionItem, String> answerCol = new TableColumn<>("NPC text");
        answerCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                QuestionItem questionItem = p.getValue();
                if (questionItem == null) return "";
                Question question = questionItem.question;
                if (question == null) return "";
                return question.getAnswersListID();
            }
        });
        answerCol.setCellFactory(TextFieldTableCell.forTableColumn());
        answerCol.setEditable(false);
        answerCol.setSortable(false);
        questionsTableView.getColumns().add(answerCol);
    }

    private void disableQuestionDetails() {
        questionDetails.setVisible(false);
        questionText.setText(null);
        questionAnswersListCB.setValue(null);
        scriptArea.clearArea();
    }

    private void saveQuestionItem(QuestionItem qi) {
        Question question = qi.question;
        question.setText(questionText.getText());
        question.setRequirements(questionRequirementsListView.getOutput());
        AnswersList answersList = questionAnswersListCB.getValue();
        if (answersList != null) {
            String answerID = answersList.getID();
            question.setAnswersListID(answerID);
        }
        question.setBeginScript(scriptArea.getScript());
        questionsTableView.refresh();
    }

    private List<Question> questionItemsToQuestions(List<QuestionItem> items) {
        List<QuestionItem> input = new ArrayList<>(items);
        input.sort(Comparator.comparingInt(q -> q.pos));
        return input.stream()
                .map(qi -> qi.question)
                .collect(Collectors.toList());
    }

    private ObservableList<QuestionItem> questionsToQuestionItems(List<Question> questions) {
        ObservableList<QuestionItem> output = FXCollections.observableArrayList();
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            QuestionItem qi = new QuestionItem(i, question);
            output.add(qi);
        }
        return output;
    }

    private void setUpQuestionsTableContextMenu() {
        MenuItem addQuestion = new MenuItem("Add text");
        addQuestion.setOnAction(e -> addQuestion());
        MenuItem removeQuestions = new MenuItem("Remove text/s");
        removeQuestions.setOnAction(e -> removeQuestion());
        ContextMenu cm = new ContextMenu(addQuestion, removeQuestions);
        questionsTableView.setContextMenu(cm);
    }

    private void addQuestion() {
        ObservableList<QuestionItem> items = questionsTableView.getItems();
        OptionalInt optionalNext = items.stream()
                .mapToInt(q -> q.pos)
                .max();
        int nextPos;
        if (optionalNext.isPresent()) {
            nextPos = optionalNext.getAsInt();
            nextPos++;
        } else {
            nextPos = 0;
        }

        Question question = new Question("", "");
        QuestionItem questionItem = new QuestionItem(nextPos, question);
        items.add(questionItem);
    }

    private void removeQuestion() {
        QuestionItem q = questionsTableView.getSelectionModel().getSelectedItem();
        questionsTableView.getItems().remove(q);
    }

    public VBox getQuestionsCenter() {
        return questionsCenter;
    }

    private class QuestionItem {
        private int pos;
        private final Question question;

        private QuestionItem(int pos, Question question) {
            this.pos = pos;
            this.question = question;
        }

        @Override
        public String toString() {
            return question.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof QuestionItem)) return false;
            QuestionItem that = (QuestionItem) o;
            return pos == that.pos &&
                    Objects.equals(question, that.question);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, question);
        }
    }
}
