package io.wsz.model.dialog;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class Answer implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private String text;
    private String questionsID;

    public Answer() {}

    public Answer(String ID) {
        this.ID = ID;
    }

    public Answer(String ID, String text, String questionsID) {
        this.ID = ID;
        this.text = text;
        this.questionsID = questionsID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getQuestionsID() {
        return questionsID;
    }

    public void setQuestionsID(String questionsID) {
        this.questionsID = questionsID;
    }

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;
        Answer answer = (Answer) o;
        return Objects.equals(getID(), answer.getID()) &&
                Objects.equals(getText(), answer.getText()) &&
                Objects.equals(getQuestionsID(), answer.getQuestionsID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getText(), getQuestionsID());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(ID);

        out.writeUTF(text);

        out.writeUTF(questionsID);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        ID = in.readUTF();

        text = in.readUTF();

        questionsID = in.readUTF();
    }
}
