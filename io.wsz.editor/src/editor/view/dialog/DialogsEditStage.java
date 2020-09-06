package editor.view.dialog;

import editor.model.EditorController;
import editor.view.dialog.requirement.RequirementsListView;
import editor.view.stage.ChildStage;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.dialog.*;
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
    private final ObservableList<AnswersList> observableAnswersList = FXCollections.observableArrayList();
    private final ObservableList<QuestionsList> observableQuestionsLists = FXCollections.observableArrayList();

    private ListView<Dialog> dialogsListView;
    private ListView<AnswersList> answersListView;
    private ListView<QuestionsList> questionsListsView;
    private VBox center;
    private VBox questionsCenter;
    private HBox startAnswersListBox;
    private ChoiceBox<AnswersList> startAnswersListCB;
    private TableView<QuestionItem> questionsTableView;
    private VBox questionDetails;
    private ChoiceBox<AnswersList> questionAnswersListCB;
    private HBox questionAnswerBox;
    private TextArea questionText;
    private VBox answersVBox;
    private VBox questionsListsVBox;
    private RequirementsListView questionRequirementsListView;
    private AnswersCenterView answerCenter;

    public DialogsEditStage(Stage parent, EditorController editorController) {
        super(parent);
        this.editorController = editorController;
        observableAnswersList.add(null);
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
        setHeight(750);
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

        setUpAnswersCenter();
        setUpQuestionCenter();

        center.getChildren().add(answerCenter.getAnswersCenter());
        root.setCenter(center);
    }

    private void setUpAnswersCenter() {
        answerCenter = new AnswersCenterView(editorController, answersListView, observableQuestionsLists);
        answerCenter.initAnswersCenter();
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
    }

    private AnswersList getAnswersListWithID(String answersListID) {
        Optional<AnswersList> optAnswer = observableAnswersList.stream()
                .filter(Objects::nonNull)
                .filter(a -> a.getID().equals(answersListID))
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

        questionRequirementsListView = new RequirementsListView(editorController);
        ScrollPane listScrollPane = questionRequirementsListView.getListScrollPane();

        questionDetails.getChildren().addAll(questionText, questionProperties, listScrollPane);
    }

    private void setUpLeft(BorderPane root) {
        dialogsListView = new ListView<>();
        VBox dialogsVBox = new VBox(5);
        setUpDialogsVBox(dialogsVBox);

        FilteredList<AnswersList> answersWithoutNull = new FilteredList<>(observableAnswersList, Objects::nonNull);
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
            String newID = tempQuestionList.getID();
            QuestionsList selectedQuestionList = questionsListsView.getSelectionModel().getSelectedItem();
            if (selectedQuestionList == null) return;
            String oldID = selectedQuestionList.getID();
            if (oldID.equals(newID)) {
                return;
            }
            newID = getUniqueQuestionsListID(newID);
            selectedQuestionList.setID(newID);
            updateAnswersQuestionsListID(oldID, newID);
            answerCenter.refreshQuestionsListForAnswerCB();
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

    private void updateAnswersQuestionsListID(String oldID, String newID) {
        for (AnswersList answersList : observableAnswersList) {
            if (answersList == null) continue;
            List<Answer> answers = answersList.getAnswers();
            for (Answer answer : answers) {
                if (answer.getQuestionsListID().equals(oldID)){
                    answer.setQuestionsListID(newID);
                }
            }
        }
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
                .filter(Objects::nonNull)
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
        final Label answersLabel = new Label("NPC texts list");
        startAnswersListBox = new HBox(5);
        final Label startAnswersListLabel = new Label("NPC starting texts list");
        startAnswersListBox.setAlignment(Pos.CENTER);
        startAnswersListCB = new ChoiceBox<>(observableAnswersList);
        startAnswersListCB.setMaxWidth(100);
        startAnswersListBox.getChildren().addAll(startAnswersListLabel, startAnswersListCB);
        answersVBox.getChildren().addAll(answersLabel, answersListView, startAnswersListBox);
        setUpAnswerListView();
    }

    private void setUpAnswerListView() {
        answersListView.setMaxWidth(200);
        answersListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        answersListView.setEditable(true);
        answersListView.setOnEditCommit(d -> {
            AnswersList tempAnswersList = d.getNewValue();
            String newID = tempAnswersList.getID();
            AnswersList selectedAnswersList = answersListView.getSelectionModel().getSelectedItem();
            if (selectedAnswersList == null) return;
            String oldID = selectedAnswersList.getID();
            if (oldID.equals(newID)) {
                return;
            }
            newID = getUniqueAnswersListID(newID);
            selectedAnswersList.setID(newID);
            updateQuestionsAnswersListID(oldID, newID);
            refreshQuestionAnswerCB();
            refreshStartAnswerCB();
        });
        answersListView.setCellFactory(param -> new TextFieldListCell<>(new StringConverter<>() {
            @Override
            public String toString(AnswersList answersList) {
                if (answersList == null) {
                    return "";
                } else {
                    return answersList.getID();
                }
            }

            @Override
            public AnswersList fromString(String id) {
                return new AnswersList(id);
            }
        }));
        setUpAnswersListContextMenu();
        hookUpAnswersListEvents();
    }

    private void updateQuestionsAnswersListID(String oldID, String newID) {
        for (QuestionsList questionsList : observableQuestionsLists) {
            if (questionsList == null) continue;
            List<Question> questions = questionsList.getQuestions();
            for (Question question : questions) {
                if (question.getAnswersListID().equals(oldID)){
                    question.setAnswersListID(newID);
                }
            }
        }
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
        List<AnswersList> answersLists = new ArrayList<>(observableAnswersList);
        answersLists.remove(null);
        dialog.setAnswersLists(answersLists);
        saveCurrentQuestionsList();
        List<QuestionsList> questionsLists = new ArrayList<>(observableQuestionsLists);
        questionsLists.remove(null);
        dialog.setQuestionsLists(questionsLists);
        AnswersList startAnswersList = startAnswersListCB.getValue();
        if (startAnswersList != null) {
            String id = startAnswersList.getID();
            dialog.setGreetingAnswersListID(id);
        }
    }

    private void saveCurrentQuestionsList() {
        List<Question> questions = questionItemsToQuestions(questionsTableView.getItems());
        QuestionsList selectedQuestionsList = questionsListsView.getSelectionModel().getSelectedItem();
        if (selectedQuestionsList == null) return;
        selectedQuestionsList.setQuestions(questions);
    }

    private void startEditNewDialog(Dialog newDialog) {
        observableAnswersList.clear();
        observableAnswersList.add(null);
        observableAnswersList.addAll(newDialog.getAnswersLists());
        observableQuestionsLists.clear();
        observableQuestionsLists.add(null);
        observableQuestionsLists.addAll(newDialog.getQuestionsLists());

        if (newDialog.getAnswersLists().isEmpty()) return;
        AnswersList startAnswersList = newDialog.getGreetingList();
        if (startAnswersList == null) return;
        startAnswersListCB.setValue(startAnswersList);
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
        List<AnswersList> answersList = new ArrayList<>(1);
        List<QuestionsList> questionsLists = new ArrayList<>(1);
        Dialog dialog = new Dialog(name, answersList, questionsLists, "");
        editorController.getObservableDialogs().add(dialog);
    }

    private void removeDialogs() {
        ObservableList<Dialog> selectedDialogs = dialogsListView.getSelectionModel().getSelectedItems();
        if (selectedDialogs == null || selectedDialogs.isEmpty()) return;
        editorController.getObservableDialogs().removeAll(selectedDialogs);
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
        questionsTableView.refresh();
    }

    private void hookUpAnswersListEvents() {
        answersListView.setOnMouseClicked(e -> {
            ObservableList<Node> centerChildren = center.getChildren();
            if (!centerChildren.contains(answerCenter)) {
                changeCenterToAnswerEdit(centerChildren);
            }
        });
        answersListView.getSelectionModel().selectedItemProperty().addListener((observable, oldAnswersList, newAnswersList) -> {
            if (oldAnswersList != null) {
                answerCenter.saveAnswersList(oldAnswersList);
            }

            boolean isAnswersListNotNull = newAnswersList != null;
            if (isAnswersListNotNull) {
                answerCenter.startEditNewAnswersList(newAnswersList);
            }
            center.setVisible(isAnswersListNotNull);
        });
    }

    private void refreshQuestionAnswerCB() {
        boolean wasCreated = questionAnswersListCB != null;
        AnswersList temp = null;
        if (wasCreated) {
            temp = questionAnswersListCB.getValue();
        }
        questionAnswerBox.getChildren().remove(questionAnswersListCB);
        questionAnswersListCB = new ChoiceBox<>(observableAnswersList);
        questionAnswersListCB.setMaxWidth(100);
        questionAnswerBox.getChildren().add(questionAnswersListCB);
        if (wasCreated) {
            questionAnswersListCB.setValue(temp);
        }
        hookUpQuestionAnswerCBEvents();
    }

    private void refreshStartAnswerCB() {
        boolean wasCreated = startAnswersListCB != null;
        AnswersList temp = null;
        if (wasCreated) {
            temp = startAnswersListCB.getValue();
        }
        startAnswersListBox.getChildren().remove(startAnswersListCB);
        startAnswersListCB = new ChoiceBox<>(observableAnswersList);
        startAnswersListCB.setMaxWidth(100);
        startAnswersListBox.getChildren().add(startAnswersListCB);
        if (wasCreated) {
            startAnswersListCB.setValue(temp);
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
        centerChildren.add(answerCenter.getAnswersCenter());
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

    private void setUpAnswersListContextMenu() {
        MenuItem addAnswersList = new MenuItem("Add NPC texts list");
        addAnswersList.setOnAction(e -> addAnswersList());
        MenuItem removeAnswersLists = new MenuItem("Remove NPC texts list/s");
        removeAnswersLists.setOnAction(e -> removeAnswersLists());
        ContextMenu cm = new ContextMenu(addAnswersList, removeAnswersLists);
        answersListView.setContextMenu(cm);
    }

    private void addAnswersList() {
        String newAnswersListID = "new";
        newAnswersListID = getUniqueAnswersListID(newAnswersListID);
        ArrayList<Answer> answers = new ArrayList<>(0);
        AnswersList answer = new AnswersList(newAnswersListID, answers);
        observableAnswersList.add(answer);
    }

    private String getUniqueAnswersListID(String newAnswerID) {
        List<String> answersIDs = observableAnswersList.stream()
                .filter(Objects::nonNull)
                .map(AnswersList::getID)
                .collect(Collectors.toList());
        newAnswerID = getUniqueID(newAnswerID, answersIDs);
        return newAnswerID;
    }

    private void removeAnswersLists() {
        List<AnswersList> selectedAnswers = answersListView.getSelectionModel().getSelectedItems();
        observableAnswersList.removeAll(selectedAnswers);
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
