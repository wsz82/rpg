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
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.util.converter.IntegerStringConverter;

import java.util.*;
import java.util.stream.Collectors;

public class AnswersCenterView {
    private final EditorController editorController;
    private final ObservableList<QuestionsList> questionsLists;
    private final ScrollPane answersCenter = new ScrollPane();
    private final TableView<AnswerItem> answersTableView = new TableView<>();
    private final VBox answerDetails = new VBox(5);
    private final TextArea answerTextArea = new TextArea();

    private RequirementsListView answerRequirementsListView;
    private ScriptArea scriptArea;

    public AnswersCenterView(EditorController editorController, ObservableList<QuestionsList> questionsLists) {
        this.editorController = editorController;
        this.questionsLists = questionsLists;
    }

    public void initAnswersCenter() {
        setUpAnswersTable();
        setUpAnswerDetails();
        setUpContainer();
    }

    protected void setUpContainer() {
        VBox container = new VBox(5);
        container.getChildren().addAll(answersTableView, answerDetails);
        answersCenter.setContent(container);
    }

    public void startEditNewAnswersList(AnswersList newAnswersList) {
        ObservableList<AnswerItem> answersItems = answersToAnswersItems(newAnswersList.getAnswers());
        answersTableView.setItems(answersItems);
        if (answersItems.isEmpty()) {
            disableAnswerDetails();
        }
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

        answerRequirementsListView = new RequirementsListView(editorController);
        VBox requirementsContainer = answerRequirementsListView.getContainer();

        scriptArea = new ScriptArea(editorController);
        scriptArea.init();
        final VBox scriptArea = this.scriptArea.getScriptArea();

        answerDetails.getChildren().addAll(answerTextArea, requirementsContainer, scriptArea);
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

        TableColumn<AnswerItem, QuestionsList> answerCol = new TableColumn<>("PC text");
        answerCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected QuestionsList computeValue() {
                AnswerItem item = p.getValue();
                if (item == null) return null;
                Answer answer = item.answer;
                if (answer == null) return null;
                String answersListID = answer.getQuestionsListID();
                if (answersListID == null) return null;
                return questionsLists.stream()
                        .filter(a -> {
                            if (a == null) return false;
                            String id = a.getID();
                            if (id == null) return false;
                            return id.equals(answersListID);
                        })
                        .findFirst()
                        .orElse(null);
            }
        });
        answerCol.setEditable(true);
        answerCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(questionsLists));
        answerCol.setSortable(false);
        answerCol.setOnEditCommit(t -> {
            QuestionsList newList = t.getNewValue();
            if (newList == null) return;
            AnswerItem item = answersTableView.getSelectionModel().getSelectedItem();
            if (item == null) return;
            Answer answer = item.answer;
            if (answer == null) return;
            answer.setQuestionsListID(newList.getID());
        });
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
        scriptArea.clearArea();
    }

    private void enableAnswerDetails() {
        answerDetails.setVisible(true);
    }

    private void saveAnswerItem(AnswerItem item) {
        Answer answer = item.answer;
        answer.setText(answerTextArea.getText());
        answer.setRequirements(answerRequirementsListView.getOutput());
        answer.setBeginScript(scriptArea.getScript());
        answersTableView.refresh();
    }

    private void startEditAnswerDetails(AnswerItem newAnswerItem) {
        Answer answer = newAnswerItem.answer;
        answerTextArea.setText(answer.getText());
        answerRequirementsListView.clear();
        answerRequirementsListView.populate(answer.getRequirements());
        scriptArea.restoreScript(answer.getBeginScript());
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

    public ScrollPane getAnswersCenter() {
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
