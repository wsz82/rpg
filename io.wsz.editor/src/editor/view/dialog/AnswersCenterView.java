package editor.view.dialog;

import editor.model.EditorController;
import editor.view.dialog.requirement.RequirementsListView;
import editor.view.dialog.script.ScriptArea;
import io.wsz.model.dialog.Answer;
import io.wsz.model.dialog.AnswersList;
import io.wsz.model.dialog.QuestionsList;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.*;
import java.util.stream.Collectors;

public class AnswersCenterView {
    private final EditorController editorController;
    private final ObservableList<QuestionsList> questionsLists;
    private final VBox answersCenter = new VBox(5);
    private final TableView<AnswerItem> answersTableView = new TableView<>();
    private final VBox answerDetails = new VBox(5);
    private final TextArea answerTextArea = new TextArea();
    private final HBox questionsListForAnswerBox = new HBox(5);

    private ChoiceBox<QuestionsList> questionsListForAnswerCB;
    private RequirementsListView answerRequirementsListView;
    private ScriptArea scriptArea;

    public AnswersCenterView(EditorController editorController, ObservableList<QuestionsList> questionsLists) {
        this.editorController = editorController;
        this.questionsLists = questionsLists;
    }

    public void initAnswersCenter() {
        setUpAnswersTable();
        setUpAnswerDetails();
        answersCenter.getChildren().addAll(answersTableView, answerDetails);
    }

    public void startEditNewAnswersList(AnswersList newAnswersList) {
        ObservableList<AnswerItem> answersItems = answersToAnswersItems(newAnswersList.getAnswers());
        answersTableView.setItems(answersItems);
        if (answersItems.isEmpty()) {
            disableAnswerDetails();
        }
    }

    public void refreshQuestionsListForAnswerCB() {
        QuestionsList temp = null;
        boolean wasCreated = questionsListForAnswerCB != null;
        if (wasCreated) {
            temp = questionsListForAnswerCB.getValue();
        }
        ObservableList<Node> container = questionsListForAnswerBox.getChildren();
        container.remove(questionsListForAnswerCB);
        questionsListForAnswerCB = new ChoiceBox<>(questionsLists);
        container.add(questionsListForAnswerCB);
        questionsListForAnswerCB.setMaxWidth(100);
        if (wasCreated) {
            questionsListForAnswerCB.setValue(temp);
        }

        hookUpQuestionsListForAnswerCBEvents();
    }

    public void saveAnswersList(AnswersList answersList) {
        saveCurrentAnswerItem();

        ObservableList<AnswerItem> answerItems = answersTableView.getItems();
        List<Answer> answers = answerItemsToAnswers(answerItems);
        answersList.setAnswers(answers);
    }

    private void setUpAnswerDetails() {
        answerDetails.setVisible(false);

        setUpAnswerTextArea();
        setUpQuestionsListForAnswerCB();

        answerRequirementsListView = new RequirementsListView(editorController);
        ScrollPane listScrollPane = answerRequirementsListView.getListScrollPane();

        scriptArea = new ScriptArea(editorController);
        scriptArea.init();
        final VBox scriptArea = this.scriptArea.getScriptArea();

        answerDetails.getChildren().addAll(answerTextArea, questionsListForAnswerBox, listScrollPane, scriptArea);
    }

    private void setUpQuestionsListForAnswerCB() {
        final Label questionsListForAnswerLabel = new Label("PC texts list");
        questionsListForAnswerBox.getChildren().addAll(questionsListForAnswerLabel);
        refreshQuestionsListForAnswerCB();
    }

    private void hookUpQuestionsListForAnswerCBEvents() {
        questionsListForAnswerCB.valueProperty().addListener((observable, oldQuestionsList, newQuestionsList) -> {
            if (newQuestionsList == null) {
                return;
            }
            AnswerItem selectedItem = answersTableView.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                return;
            }
            Answer answer = selectedItem.answer;
            if (answer == null) return;
            String questionsListID = newQuestionsList.getID();
            answer.setQuestionsListID(questionsListID);
        });
    }

    private void setUpAnswerTextArea() {
        answerTextArea.setPrefSize(300, 200);
        answerTextArea.setWrapText(true);
    }

    private void setUpAnswersTable() {
        setUpAnswersTableView();
        hookUpAnswersTableViewEvents();
        setUpAnswersTableContextMenu();
    }

    private void setUpAnswersTableView() {
        answersTableView.setEditable(true);
        TableColumn<AnswerItem, Integer> posCol = new TableColumn<>("Position");
        posCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return p.getValue().pos;
            }
        });
        posCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        posCol.setOnEditCommit(t -> {
            int newPos = t.getNewValue();
            ObservableList<AnswerItem> answerItems = answersTableView.getItems();
            AnswerItem qi = answerItems.get(t.getTablePosition().getRow());
            boolean newPosExists = answerItems.stream()
                    .anyMatch(q -> q.pos == newPos);
            if (!newPosExists) {
                qi.pos = newPos;
            }
            answersTableView.refresh();
        });
        posCol.setSortable(false);
        answersTableView.getColumns().add(posCol);

        TableColumn<AnswerItem, String> textCol = new TableColumn<>("Text");
        textCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                AnswerItem answerItem = p.getValue();
                if (answerItem == null) return "";
                Answer answer = answerItem.answer;
                if (answer == null) return "";
                return answer.getText();
            }
        });
        textCol.setCellFactory(TextFieldTableCell.forTableColumn());
        textCol.setEditable(false);
        textCol.setSortable(false);
        answersTableView.getColumns().add(textCol);

        TableColumn<AnswerItem, String> answerCol = new TableColumn<>("PC text");
        answerCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                AnswerItem answerItem = p.getValue();
                if (answerItem == null) return "";
                Answer answer = answerItem.answer;
                if (answer == null) return "";
                return answer.getQuestionsListID();
            }
        });
        answerCol.setCellFactory(TextFieldTableCell.forTableColumn());
        answerCol.setEditable(false);
        answerCol.setSortable(false);
        answersTableView.getColumns().add(answerCol);
    }

    private void hookUpAnswersTableViewEvents() {
        ChangeListener<AnswerItem> questionTableItemListener = (observable, oldAnswerItem, newAnswerItem) -> {
            if (newAnswerItem == null) {
                disableAnswerDetails();
                return;
            }
            if (answersTableView.getItems().size() < 1) {
                disableAnswerDetails();
                return;
            }
            if (oldAnswerItem != null) {
                saveAnswerItem(oldAnswerItem);
            }
            startEditAnswerDetails(newAnswerItem);
            enableAnswerDetails();
        };
        answersTableView.getSelectionModel().selectedItemProperty().addListener(questionTableItemListener);
    }

    private void disableAnswerDetails() {
        answerDetails.setVisible(false);
        answerTextArea.setText(null);
        questionsListForAnswerCB.setValue(null);
        scriptArea.clearArea();
    }

    private void enableAnswerDetails() {
        answerDetails.setVisible(true);
    }

    private void saveAnswerItem(AnswerItem item) {
        Answer answer = item.answer;
        answer.setText(answerTextArea.getText());
        answer.setRequirements(answerRequirementsListView.getOutput());
        QuestionsList questionsList = questionsListForAnswerCB.getValue();
        if (questionsList != null) {
            String questionsListID = questionsList.getID();
            answer.setQuestionsListID(questionsListID);
        }
        answer.setBeginScript(scriptArea.getScript());
        answersTableView.refresh();
    }

    private void startEditAnswerDetails(AnswerItem newAnswerItem) {
        Answer answer = newAnswerItem.answer;
        answerTextArea.setText(answer.getText());
        String questionsListID = answer.getQuestionsListID();
        QuestionsList questionsList = getAnswerWithID(questionsListID);
        questionsListForAnswerCB.setValue(questionsList);
        answerRequirementsListView.clear();
        answerRequirementsListView.populate(answer.getRequirements());
        scriptArea.restoreScript(answer.getBeginScript());
    }

    private QuestionsList getAnswerWithID(String questionsListID) {
        Optional<QuestionsList> optQuestionsList = questionsLists.stream()
                .filter(Objects::nonNull)
                .filter(a -> a.getID().equals(questionsListID))
                .findFirst();
        return optQuestionsList.orElse(null);
    }

    private void setUpAnswersTableContextMenu() {
        MenuItem addText = new MenuItem("Add text");
        addText.setOnAction(e -> addText());
        MenuItem removeText = new MenuItem("Remove text/s");
        removeText.setOnAction(e -> removeText());
        ContextMenu cm = new ContextMenu(addText, removeText);
        answersTableView.setContextMenu(cm);
    }

    private void addText() {
        ObservableList<AnswerItem> items = answersTableView.getItems();
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

        Answer answer = new Answer("", "");
        AnswerItem questionItem = new AnswerItem(nextPos, answer);
        items.add(questionItem);
    }

    private void removeText() {
        AnswerItem items = answersTableView.getSelectionModel().getSelectedItem();
        answersTableView.getItems().remove(items);
    }

    private List<Answer> answerItemsToAnswers(ObservableList<AnswerItem> items) {
        List<AnswerItem> input = new ArrayList<>(items);
        input.sort(Comparator.comparingInt(q -> q.pos));
        return input.stream()
                .map(i -> i.answer)
                .collect(Collectors.toList());
    }

    private void saveCurrentAnswerItem() {
        AnswerItem item = answersTableView.getSelectionModel().getSelectedItem();
        if (item != null) {
            saveAnswerItem(item);
        }
    }

    private ObservableList<AnswerItem> answersToAnswersItems(List<Answer> answers) {
        ObservableList<AnswerItem> output = FXCollections.observableArrayList();
        for (int i = 0; i < answers.size(); i++) {
            Answer answer = answers.get(i);
            AnswerItem item = new AnswerItem(i, answer);
            output.add(item);
        }
        return output;
    }

    public VBox getAnswersCenter() {
        return answersCenter;
    }

    private class AnswerItem {

        private int pos;
        private final Answer answer;

        private AnswerItem(int pos, Answer answer) {
            this.pos = pos;
            this.answer = answer;
        }

        @Override
        public String toString() {
            return answer.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnswerItem)) return false;
            AnswerItem that = (AnswerItem) o;
            return pos == that.pos &&
                    Objects.equals(answer, that.answer);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, answer);
        }
    }
}
