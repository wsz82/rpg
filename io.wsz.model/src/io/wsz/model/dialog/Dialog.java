package io.wsz.model.dialog;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

public class Dialog implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private List<Answer> answers;
    private List<QuestionsList> questionsLists;
    private String greetingAnswerID;

    public Dialog() {}

    public Dialog(String ID) {
        this.ID = ID;
    }

    public Dialog(String ID, List<Answer> answers, List<QuestionsList> questionsLists, String greetingAnswerID) {
        this.ID = ID;
        this.answers = answers;
        this.questionsLists = questionsLists;
        this.greetingAnswerID = greetingAnswerID;
    }

    public QuestionsList getQuestionsListByID(String ID) {
        for (QuestionsList questionsList : questionsLists) {
            String id = questionsList.getID();
            if (id.equals(ID)) {
                return questionsList;
            }
        }
        return null;
    }

    public Answer getAnswerByID(String ID) {
        for (Answer answer : answers) {
            String id = answer.getID();
            if (id.equals(ID)) {
                return answer;
            }
        }
        return null;
    }

    public Answer getGreeting() {
        for (Answer answer : answers) {
            String id = answer.getID();
            if (id.equals(greetingAnswerID)) {
                return answer;
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

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public List<QuestionsList> getQuestionsLists() {
        return questionsLists;
    }

    public void setQuestionsLists(List<QuestionsList> questionsLists) {
        this.questionsLists = questionsLists;
    }

    public String getGreetingAnswerID() {
        return greetingAnswerID;
    }

    public void setGreetingAnswerID(String greetingAnswerID) {
        this.greetingAnswerID = greetingAnswerID;
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
        return getGreetingAnswerID() == dialog.getGreetingAnswerID() &&
                Objects.equals(getID(), dialog.getID()) &&
                Objects.equals(getAnswers(), dialog.getAnswers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getAnswers(), getGreetingAnswerID());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(ID);

        out.writeObject(answers);

        out.writeObject(questionsLists);

        out.writeUTF(greetingAnswerID);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        ID = in.readUTF();

        answers = (List<Answer>) in.readObject();

        questionsLists = (List<QuestionsList>) in.readObject();

        greetingAnswerID = in.readUTF();
    }
}
