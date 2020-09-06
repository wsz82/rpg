package editor.view.dialog;

import editor.model.EditorController;
import editor.view.stage.ChildStage;
import io.wsz.model.dialog.Answer;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.dialog.Question;
import io.wsz.model.dialog.QuestionsList;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.*;
import java.util.stream.Collectors;

public class DialogsEditStage extends ChildStage {
    private final EditorController editorController;
    private final ObservableList<Answer> observableAnswers = FXCollections.observableArrayList();
    private final ObservableList<QuestionsList> observableQuestionsLists = FXCollections.observableArrayList();

    private ListView<Dialog> dialogsListView;
    private ListView<Answer> answersListView;
    private ChoiceBox<QuestionsList> questionsListForAnswerCB;
    private HBox questionsListForAnswerBox;
    private ListView<QuestionsList> questionsListsView;
    private VBox center;
    private VBox questionsCenter;
    private HBox startAnswerBox;
    private ChoiceBox<Answer> startAnswerCB;
    private TextArea answerTextArea;
    private TableView<QuestionItem> questionsTableView;
    private VBox questionDetails;
    private ChoiceBox<Answer> questionAnswerCB;
    private HBox questionAnswerBox;
    private TextArea questionText;
    private VBox answersVBox;
    private VBox questionsListsVBox;
    private VBox answerCenter;
    private RequirementsListView requirementsListView;

    public DialogsEditStage(Stage parent, EditorController editorController) {
        super(parent);
        this.editorController = editorController;
        observableAnswers.add(null);
        observableQuestionsLists.add(null);
        hookUpCloseEvent();
    }

    private void hookUpCloseEvent() {
        setOnCloseRequest(e -> {
            Dialog selectedDialog = dialogsListView.getSelectionModel().getSelectedItem();
            if (selectedDialog == null) return;
            saveDialog(selectedDialog);
        });
    }

    public void initWindow() {
        setTitle("Dialogs edit");
        setHeight(800);
        final BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        setUpLeft(root);

        setUpCenter(root);

        setUpStartSettings();

        final Scene scene = new Scene(root);
        setScene(scene);
    }

    private void setUpStartSettings() {
        setIsDialogChosenSettings(false);
        center.setVisible(false);
    }

    private void setIsDialogChosenSettings(boolean isDialogChosen) {
        answersVBox.setVisible(isDialogChosen);
        questionsListsVBox.setVisible(isDialogChosen);
    }

    private void setUpCenter(BorderPane root) {
        center = new VBox(10);
        center.setPrefWidth(800);

        setUpAnswerCenter();
        setUpQuestionCenter();

        center.getChildren().add(answerCenter);
        root.setCenter(center);
    }

    private void setUpQuestionCenter() {
        questionsCenter = new VBox(5);

        setUpQuestionsTableView();
        setUpQuestionDetails();

        questionsCenter.getChildren().addAll(questionsTableView, questionDetails);
    }

    private void setUpQuestionsTableView() {
        questionsTableView = new TableView<>();
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
            if (newQuestionItem != null) {
                startEditQuestionDetails(newQuestionItem);
                enableQuestionsDetails();
            } else {
                disableQuestionDetails();
            }
        };
        questionsTableView.getSelectionModel().selectedItemProperty().addListener(questionTableItemListener);
    }

    private void enableQuestionsDetails() {
        questionDetails.setVisible(true);
    }

    private void startEditQuestionDetails(QuestionItem newQuestionItem) {
        Question question = newQuestionItem.question;
        questionText.setText(question.getText());
        String answerID = question.getAnswerID();
        Answer answer = getAnswerWithID(answerID);
        questionAnswerCB.setValue(answer);
        requirementsListView.clear();
        requirementsListView.populate(question.getRequirements());
    }

    private Answer getAnswerWithID(String answerID) {
        Optional<Answer> optAnswer = observableAnswers.stream()
                .filter(Objects::nonNull)
                .filter(a -> a.getID().equals(answerID))
                .findFirst();
        return optAnswer.orElse(null);
    }

    private void setUpQuestionDetails() {
        questionDetails = new VBox(5);
        questionDetails.setVisible(false);
        questionText = new TextArea();
        questionAnswerBox = new HBox(5);
        final Label answerLabel = new Label("NPC text");
        questionAnswerBox.getChildren().addAll(answerLabel);
        final HBox questionProperties = new HBox(5);
        refreshQuestionAnswerCB();
        questionProperties.getChildren().addAll(questionAnswerBox);

        requirementsListView = new RequirementsListView(editorController);
        ScrollPane listScrollPane = requirementsListView.getListScrollPane();

        questionDetails.getChildren().addAll(questionText, questionProperties, listScrollPane);
    }

    private void setUpAnswerCenter() {
        answerCenter = new VBox(5);
        answerTextArea = new TextArea();
        answerTextArea.setPrefSize(300, 200);
        answerTextArea.setWrapText(true);

        final Label questionsListForAnswerLabel = new Label("PC texts list");
        questionsListForAnswerBox = new HBox(5);
        questionsListForAnswerBox.getChildren().addAll(questionsListForAnswerLabel);
        refreshQuestionsListForAnswerCB();
        answerCenter.getChildren().addAll(answerTextArea, questionsListForAnswerBox);
    }

    private void refreshQuestionsListForAnswerCB() {
        QuestionsList temp = null;
        boolean wasCreated = questionsListForAnswerCB != null;
        if (wasCreated) {
            temp = questionsListForAnswerCB.getValue();
        }
        ObservableList<Node> container = questionsListForAnswerBox.getChildren();
        container.remove(questionsListForAnswerCB);
        questionsListForAnswerCB = new ChoiceBox<>(observableQuestionsLists);
        container.add(questionsListForAnswerCB);
        questionsListForAnswerCB.setMaxWidth(100);
        if (wasCreated) {
            questionsListForAnswerCB.setValue(temp);
        }

        hookUpQuestionsListForAnswerCBEvents();
    }

    private void setUpLeft(BorderPane root) {
        dialogsListView = new ListView<>();
        VBox dialogsVBox = new VBox(5);
        setUpDialogsVBox(dialogsVBox);

        FilteredList<Answer> answersWithoutNull = new FilteredList<>(observableAnswers, Objects::nonNull);
        answersListView = new ListView<>(answersWithoutNull);
        answersVBox = new VBox(5);
        setUpAnswersVBox(answersVBox);

        FilteredList<QuestionsList> questionsListsWithoutNull = new FilteredList<>(observableQuestionsLists, Objects::nonNull);
        questionsListsView = new ListView<>(questionsListsWithoutNull);
        questionsListsVBox = new VBox(5);
        setUpQuestionsVBox(questionsListsVBox);

        final HBox dialogsAnswersQuestions = new HBox();
        dialogsAnswersQuestions.getChildren().addAll(dialogsVBox, answersVBox, questionsListsVBox);
        root.setLeft(dialogsAnswersQuestions);
    }

    private void setUpQuestionsVBox(VBox questionsListsVBox) {
        final Label questionsLabel = new Label("PC texts lists");
        questionsListsVBox.getChildren().addAll(questionsLabel, questionsListsView);
        setUpQuestionsListsView();
    }

    private void setUpQuestionsListsView() {
        questionsListsView.setMaxWidth(200);
        questionsListsView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        questionsListsView.setEditable(true);
        questionsListsView.setOnEditCommit(d -> {
            QuestionsList tempQuestionList = d.getNewValue();
            String newName = tempQuestionList.getID();
            QuestionsList selectedQuestionList = questionsListsView.getSelectionModel().getSelectedItem();
            if (selectedQuestionList == null) return;
            if (selectedQuestionList.getID().equals(newName)) {
                return;
            }
            newName = getUniqueQuestionsListID(newName);
            selectedQuestionList.setID(newName);
            refreshQuestionsListForAnswerCB();
        });
        questionsListsView.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(QuestionsList questionsList) {
                if (questionsList == null) {
                    return "";
                } else {
                    return questionsList.getID();
                }
            }

            @Override
            public QuestionsList fromString(String string) {
                return new QuestionsList(string);
            }
        }));
        setUpQuestionsListsContextMenu();
        hookUpQuestionsListsEvents();
    }

    private void setUpQuestionsListsContextMenu() {
        MenuItem addQuestionsList = new MenuItem("Add PC texts");
        addQuestionsList.setOnAction(e -> addQuestionsList());
        MenuItem removeQuestionsLists = new MenuItem("Remove PC texts list/s");
        removeQuestionsLists.setOnAction(e -> removeQuestionsLists());
        ContextMenu cm = new ContextMenu(addQuestionsList, removeQuestionsLists);
        questionsListsView.setContextMenu(cm);
    }

    private void removeQuestionsLists() {
        List<QuestionsList> selectedQuestionsLists = questionsListsView.getSelectionModel().getSelectedItems();
        observableQuestionsLists.removeAll(selectedQuestionsLists);
    }

    private void addQuestionsList() {
        String name = "new";
        name = getUniqueQuestionsListID(name);
        ArrayList<Question> questions = new ArrayList<>(1);
        QuestionsList questionsList = new QuestionsList(name, questions);
        observableQuestionsLists.add(questionsList);
    }

    private String getUniqueQuestionsListID(String ID) {
        List<String> IDs = observableQuestionsLists.stream()
                .map(QuestionsList::getID)
                .collect(Collectors.toList());
        ID = getUniqueID(ID, IDs);
        return ID;
    }

    private void hookUpQuestionsListsEvents() {
        questionsListsView.setOnMouseClicked(e -> {
            ObservableList<Node> centerChildren = center.getChildren();
            if (!centerChildren.contains(questionsCenter)) {
                changeCenterToQuestionsEdit(centerChildren);
            }
        });
        questionsListsView.getSelectionModel().selectedItemProperty().addListener((observable, oldQuestionsList, newQuestionsList) -> {
            if (oldQuestionsList != null) {
                saveQuestionsList(oldQuestionsList);
            }

            boolean isQuestionsListNotNull = newQuestionsList != null;
            if (isQuestionsListNotNull) {
                startEditQuestionsList(newQuestionsList);
            }
            center.setVisible(isQuestionsListNotNull);
        });
    }

    private void saveQuestionsList(QuestionsList questionsList) {
        saveCurrentQuestionItem();

        ObservableList<QuestionItem> questionItems = questionsTableView.getItems();
        List<Question> questions = questionItemsToQuestions(questionItems);
        questionsList.setQuestions(questions);
    }

    private void saveCurrentQuestionItem() {
        QuestionItem qi = questionsTableView.getSelectionModel().getSelectedItem();
        if (qi != null) {
            saveQuestionItem(qi);
        }
    }

    private void setUpAnswersVBox(VBox answersVBox) {
        final Label answersLabel = new Label("NPC texts");
        startAnswerBox = new HBox(5);
        final Label startAnswerLabel = new Label("NPC starting text");
        startAnswerBox.setAlignment(Pos.CENTER);
        startAnswerCB = new ChoiceBox<>(observableAnswers);
        startAnswerCB.setMaxWidth(100);
        startAnswerBox.getChildren().addAll(startAnswerLabel, startAnswerCB);
        answersVBox.getChildren().addAll(answersLabel, answersListView, startAnswerBox);
        setUpAnswerListView();
    }

    private void setUpAnswerListView() {
        answersListView.setMaxWidth(200);
        answersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        answersListView.setEditable(true);
        answersListView.setOnEditCommit(d -> {
            Answer tempAnswer = d.getNewValue();
            String newID = tempAnswer.getID();
            Answer selectedAnswer = answersListView.getSelectionModel().getSelectedItem();
            if (selectedAnswer == null) return;
            if (selectedAnswer.getID().equals(newID)) {
                return;
            }
            newID = getUniqueAnswerID(newID);
            selectedAnswer.setID(newID);
            refreshQuestionAnswerCB();
            refreshStartAnswerCB();
        });
        answersListView.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(Answer answer) {
                if (answer == null) {
                    return "";
                } else {
                    return answer.getID();
                }
            }

            @Override
            public Answer fromString(String string) {
                return new Answer(string);
            }
        }));
        setUpAnswersListContextMenu();
        hookUpAnswersListEvents();
    }

    private void setUpDialogsVBox(VBox dialogsVBox) {
        final Label dialogsLabel = new Label("Dialogs");
        dialogsVBox.getChildren().addAll(dialogsLabel, dialogsListView);
        setUpDialogsList();
    }

    private void setUpDialogsList() {
        dialogsListView.setItems(editorController.getObservableDialogs());
        dialogsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        dialogsListView.setEditable(true);
        dialogsListView.setOnEditCommit(d -> {
            Dialog tempDialog = d.getNewValue();
            String newID = tempDialog.getID();
            Dialog selectedDialog = dialogsListView.getSelectionModel().getSelectedItem();
            if (selectedDialog.getID().equals(newID)) {
                return;
            }
            newID = getUniqueDialogID(newID);
            selectedDialog.setID(newID);
        });
        dialogsListView.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(Dialog dialog) {
                if (dialog == null) {
                    return "";
                } else {
                    return dialog.getID();
                }
            }

            @Override
            public Dialog fromString(String string) {
                return new Dialog(string);
            }
        }));
        setUpDialogsEvents();
    }

    private String getUniqueDialogID(String ID) {
        List<String> IDs = editorController.getObservableDialogs().stream()
                .map(Dialog::getID)
                .collect(Collectors.toList());
        ID = getUniqueID(ID, IDs);
        return ID;
    }

    private String getUniqueID(String newID, List<String> IDs) {
        boolean IDsContainsNewID = IDs.contains(newID);
        if (IDsContainsNewID) {
            newID += "New";
            return getUniqueID(newID, IDs);
        } else {
            return newID;
        }
    }

    private void setUpDialogsEvents() {
        dialogsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldDialog, newDialog) -> {
            if (oldDialog != null) {
                saveDialog(oldDialog);
            }
            boolean isDialogNotNull = newDialog != null;
            if (isDialogNotNull) {
                startEditNewDialog(newDialog);
            }
            setIsDialogChosenSettings(isDialogNotNull);
        });
        ContextMenu contextMenu = new ContextMenu();
        setUpDialogListViewContextMenu(contextMenu);
        dialogsListView.setOnContextMenuRequested(e -> {
            contextMenu.show(this, e.getScreenX(), e.getScreenY());
        });
    }

    private void saveDialog(Dialog dialog) {
        ArrayList<Answer> answers = new ArrayList<>(observableAnswers);
        answers.remove(null);
        dialog.setAnswers(answers);
        saveCurrentQuestionsList();
        List<QuestionsList> questionsLists = new ArrayList<>(observableQuestionsLists);
        questionsLists.remove(null);
        dialog.setQuestionsLists(questionsLists);
        Answer startAnswer = startAnswerCB.getValue();
        if (startAnswer != null) {
            String id = startAnswer.getID();
            dialog.setGreetingAnswerID(id);
        }
    }

    private void saveCurrentQuestionsList() {
        List<Question> questions = questionItemsToQuestions(questionsTableView.getItems());
        QuestionsList selectedQuestionsList = questionsListsView.getSelectionModel().getSelectedItem();
        if (selectedQuestionsList == null) return;
        selectedQuestionsList.setQuestions(questions);
    }

    private void startEditNewDialog(Dialog newDialog) {
        observableAnswers.clear();
        observableAnswers.add(null);
        observableAnswers.addAll(newDialog.getAnswers());
        answersListView.getSelectionModel().selectFirst();
        observableQuestionsLists.clear();
        observableQuestionsLists.add(null);
        observableQuestionsLists.addAll(newDialog.getQuestionsLists());

        if (newDialog.getAnswers().isEmpty()) return;
        Answer startAnswer = newDialog.getGreeting();
        if (startAnswer == null) return;
        startAnswerCB.setValue(startAnswer);
    }

    private void setUpDialogListViewContextMenu(ContextMenu contextMenu) {
        final MenuItem addDialog = new MenuItem("Add dialog");
        final MenuItem removeDialogs = new MenuItem("Remove dialog/s");
        contextMenu.getItems().addAll(addDialog, removeDialogs);
        addDialog.setOnAction(event -> addDialog());
        removeDialogs.setOnAction(event -> removeDialogs());
    }

    private void addDialog() {
        String name = "new";
        name = getUniqueDialogID(name);
        List<Answer> answers = new ArrayList<>(1);
        List<QuestionsList> questionsLists = new ArrayList<>(1);
        Dialog dialog = new Dialog(name, answers, questionsLists, "");
        editorController.getObservableDialogs().add(dialog);
    }

    private void removeDialogs() {
        ObservableList<Dialog> selectedDialogs = dialogsListView.getSelectionModel().getSelectedItems();
        if (selectedDialogs == null || selectedDialogs.isEmpty()) return;
        editorController.getObservableDialogs().removeAll(selectedDialogs);
    }

    private void hookUpQuestionAnswerCBEvents() {
        questionAnswerCB.valueProperty().addListener((observable, oldAnswer, newAnswer) -> {
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
            qi.question.setAnswerID(answerID);
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

        TableColumn<QuestionItem, String> answerCol = new TableColumn<>("PC text");
        answerCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                QuestionItem questionItem = p.getValue();
                if (questionItem == null) return "";
                Question question = questionItem.question;
                if (question == null) return "";
                return question.getAnswerID();
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
        questionAnswerCB.setValue(null);
    }

    private void saveQuestionItem(QuestionItem qi) {
        Question question = qi.question;
        question.setText(questionText.getText());
        question.setRequirements(requirementsListView.getOutput());
        Answer answer = questionAnswerCB.getValue();
        if (answer != null) {
            String answerID = answer.getID();
            question.setAnswerID(answerID);
        }
        questionsTableView.refresh();
    }

    private void hookUpAnswersListEvents() {
        answersListView.setOnMouseClicked(e -> {
            ObservableList<Node> centerChildren = center.getChildren();
            if (!centerChildren.contains(answerCenter)) {
                changeCenterToAnswerEdit(centerChildren);
            }
        });
        answersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldAnswer, newAnswer) -> {
            changeAnswer(oldAnswer, newAnswer);
        });
    }

    private void changeAnswer(Answer oldAnswer, Answer newAnswer) {
        if (oldAnswer != null) {
            saveOldAnswer(oldAnswer);
        }

        boolean newAnswerIsNotNull = newAnswer != null;
        if (newAnswerIsNotNull) {
            startEditNewAnswer(newAnswer);
        }
        center.setVisible(newAnswerIsNotNull);
    }

    private void saveOldAnswer(Answer oldAnswer) {
        saveAnswerTo(oldAnswer);
    }

    private void refreshQuestionAnswerCB() {
        boolean wasCreated = questionAnswerCB != null;
        Answer temp = null;
        if (wasCreated) {
            temp = questionAnswerCB.getValue();
        }
        questionAnswerBox.getChildren().remove(questionAnswerCB);
        questionAnswerCB = new ChoiceBox<>(observableAnswers);
        questionAnswerCB.setMaxWidth(100);
        questionAnswerBox.getChildren().add(questionAnswerCB);
        if (wasCreated) {
            questionAnswerCB.setValue(temp);
        }
        hookUpQuestionAnswerCBEvents();
    }

    private void hookUpQuestionsListForAnswerCBEvents() {
        questionsListForAnswerCB.valueProperty().addListener((observable, oldQuestionsList, newQuestionsList) -> {
            if (newQuestionsList == null) {
                return;
            }
            Answer answer = answersListView.getSelectionModel().getSelectedItem();
            if (answer == null) {
                return;
            }
            String questionsListID = newQuestionsList.getID();
            answer.setQuestionsID(questionsListID);
        });
    }

    private void refreshStartAnswerCB() {
        boolean wasCreated = startAnswerCB != null;
        Answer temp = null;
        if (wasCreated) {
            temp = startAnswerCB.getValue();
        }
        startAnswerBox.getChildren().remove(startAnswerCB);
        startAnswerCB = new ChoiceBox<>(observableAnswers);
        startAnswerCB.setMaxWidth(100);
        startAnswerBox.getChildren().add(startAnswerCB);
        if (wasCreated) {
            startAnswerCB.setValue(temp);
        }
    }

    private void saveAnswerTo(Answer answer) {
        if (answer == null) {
            return;
        }
        String text = answerTextArea.getText();
        answer.setText(text);
        QuestionsList questionsList = questionsListForAnswerCB.getValue();
        if (questionsList != null) {
            String id = questionsList.getID();
            answer.setQuestionsID(id);
        }
    }

    private List<Question> questionItemsToQuestions(List<QuestionItem> items) {
        List<QuestionItem> input = new ArrayList<>(items);
        input.sort(Comparator.comparingInt(q -> q.pos));
        List<Question> output = input.stream()
                .map(qi -> qi.question)
                .collect(Collectors.toList());
        return output;
    }

    private void startEditNewAnswer(Answer answer) {
        answerTextArea.setText(answer.getText());
        String questionsID = answer.getQuestionsID();
        QuestionsList questionsList = getQuestionsList(questionsID);
        questionsListForAnswerCB.setValue(questionsList);
    }

    private QuestionsList getQuestionsList(String questionsID) {
        Optional<QuestionsList> optQuestionsList = observableQuestionsLists.stream()
                .filter(Objects::nonNull)
                .filter(q -> q.getID().equals(questionsID))
                .findFirst();
        return optQuestionsList.orElse(null);
    }

    private void startEditQuestionsList(QuestionsList questions) {
        ObservableList<QuestionItem> questionItems = questionsToQuestionItems(questions.getQuestions());
        questionsTableView.setItems(questionItems);
        if (questionItems.isEmpty()) {
            disableQuestionDetails();
        }
    }

    private void changeCenterToQuestionsEdit(ObservableList<Node> centerChildren) {
        centerChildren.clear();
        centerChildren.add(questionsCenter);
    }

    private void changeCenterToAnswerEdit(ObservableList<Node> centerChildren) {
        centerChildren.clear();
        centerChildren.add(answerCenter);
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

    private void removeQuestion() {
        QuestionItem q = questionsTableView.getSelectionModel().getSelectedItem();
        questionsTableView.getItems().remove(q);
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

    private void setUpAnswersListContextMenu() {
        MenuItem addAnswer = new MenuItem("Add NPC text");
        addAnswer.setOnAction(e -> addAnswer());
        MenuItem removeAnswers = new MenuItem("Remove NPC text/s");
        removeAnswers.setOnAction(e -> removeAnswer());
        ContextMenu cm = new ContextMenu(addAnswer, removeAnswers);
        answersListView.setContextMenu(cm);
    }

    private void addAnswer() {
        String newAnswerID = "new";
        newAnswerID = getUniqueAnswerID(newAnswerID);
        Answer answer = new Answer(newAnswerID, "", "");
        observableAnswers.add(answer);
    }

    private String getUniqueAnswerID(String newAnswerID) {
        List<String> answersIDs = observableAnswers.stream()
                .filter(Objects::nonNull)
                .map(Answer::getID)
                .collect(Collectors.toList());
        newAnswerID = getUniqueID(newAnswerID, answersIDs);
        return newAnswerID;
    }

    private void removeAnswer() {
        List<Answer> selectedAnswers = answersListView.getSelectionModel().getSelectedItems();
        observableAnswers.removeAll(selectedAnswers);
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
