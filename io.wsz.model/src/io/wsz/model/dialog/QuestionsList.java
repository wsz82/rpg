package io.wsz.model.dialog;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

public class QuestionsList implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private List<Question> questions;

    public QuestionsList() {}

    public QuestionsList(String ID) {
        this.ID = ID;
    }

    public QuestionsList(String ID, List<Question> questions) {
        this.ID = ID;
        this.questions = questions;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QuestionsList)) return false;
        QuestionsList that = (QuestionsList) o;
        return Objects.equals(getID(), that.getID()) &&
                Objects.equals(getQuestions(), that.getQuestions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getQuestions());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(ID);

        out.writeObject(questions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        ID = in.readUTF();

        questions = (List<Question>) in.readObject();
    }
}
