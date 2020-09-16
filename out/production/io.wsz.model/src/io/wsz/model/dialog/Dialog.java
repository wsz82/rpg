package io.wsz.model.dialog;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dialog implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final List<Question> FILTERED_QUESTIONS = new ArrayList<>(0);

    private String ID;
    private List<AnswersList> answersLists;
    private List<QuestionsList> questionsLists;
    private String greetingAnswersListID;

    public Dialog() {}

    public Dialog(String ID) {
        this.ID = ID;
    }

    public Dialog(String ID, List<AnswersList> answersLists, List<QuestionsList> questionsLists, String greetingAnswersListID) {
        this.ID = ID;
        this.answersLists = answersLists;
        this.questionsLists = questionsLists;
        this.greetingAnswersListID = greetingAnswersListID;
    }

    public List<Question> getQuestionsListByID(String ID, Controller controller, Creature pc, PosItem npc) {
        for (QuestionsList questionsList : questionsLists) {
            String id = questionsList.getID();
            if (id.equals(ID)) {
                List<Question> questions = questionsList.getQuestions();
                return geFilteredQuestions(questions, controller, pc, npc);
            }
        }
        return null;
    }

    private List<Question> geFilteredQuestions(List<Question> questions, Controller controller, Creature pc, PosItem npc) {
        FILTERED_QUESTIONS.clear();
        for (Question question : questions) {
            if (question.doMatchRequirements(controller, pc, npc)) {
                FILTERED_QUESTIONS.add(question);
            }
        }
        return FILTERED_QUESTIONS;
    }

    public Answer getAnswerByID(String ID, Controller controller, Creature pc, PosItem npc) {
        for (AnswersList answersList : answersLists) {
            String id = answersList.getID();
            if (id.equals(ID)) {
                List<Answer> answers = answersList.getAnswers();
                return getFirstMatchedAnswer(answers, controller, pc, npc);
            }
        }
        return null;
    }

    private Answer getFirstMatchedAnswer(List<Answer> answers, Controller controller, Creature pc, PosItem npc) {
        for (Answer answer : answers) {
            if (answer.doMatchRequirements(controller, pc, npc)) {
                return answer;
            }
        }
        return null;
    }

    public AnswersList getGreetingList() {
        for (AnswersList answersList : answersLists) {
            String id = answersList.getID();
            if (id.equals(greetingAnswersListID)) {
                return answersList;
            }
        }
        return null;
    }

    public Answer getGreeting(Controller controller, Creature pc, PosItem npc) {
        for (AnswersList answersList : answersLists) {
            String id = answersList.getID();
            if (id.equals(greetingAnswersListID)) {
                List<Answer> answers = answersList.getAnswers();
                return getFirstMatchedAnswer(answers, controller, pc, npc);
            }
        }
        return null;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<AnswersList> getAnswersLists() {
        return answersLists;
    }

    public void setAnswersLists(List<AnswersList> answersLists) {
        this.answersLists = answersLists;
    }

    public List<QuestionsList> getQuestionsLists() {
        return questionsLists;
    }

    public void setQuestionsLists(List<QuestionsList> questionsLists) {
        this.questionsLists = questionsLists;
    }

    public String getGreetingAnswersListID() {
        return greetingAnswersListID;
    }

    public void setGreetingAnswersListID(String greetingAnswersListID) {
        this.greetingAnswersListID = greetingAnswersListID;
    }

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dialog)) return false;
        Dialog dialog = (Dialog) o;
        return Objects.equals(getID(), dialog.getID()) &&
                Objects.equals(getAnswersLists(), dialog.getAnswersLists()) &&
                Objects.equals(getQuestionsLists(), dialog.getQuestionsLists()) &&
                Objects.equals(getGreetingAnswersListID(), dialog.getGreetingAnswersListID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getAnswersLists(), getQuestionsLists(), getGreetingAnswersListID());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(ID);

        out.writeObject(answersLists);

        out.writeObject(questionsLists);

        out.writeObject(greetingAnswersListID);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        ID = (String) in.readObject();

        answersLists = (List<AnswersList>) in.readObject();

        questionsLists = (List<QuestionsList>) in.readObject();

        greetingAnswersListID = (String) in.readObject();
    }
}
