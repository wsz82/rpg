package io.wsz.model.dialog;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Objects;

public class AnswersList implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private List<Answer> answers;

    public AnswersList() {}

    public AnswersList(String ID) {
        this.ID = ID;
    }

    public AnswersList(String ID, List<Answer> answers) {
        this.ID = ID;
        this.answers = answers;
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

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnswersList)) return false;
        AnswersList that = (AnswersList) o;
        return Objects.equals(getID(), that.getID()) &&
                Objects.equals(getAnswers(), that.getAnswers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getAnswers());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(ID);

        out.writeObject(answers);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        ID = (String) in.readObject();

        answers = (List<Answer>) in.readObject();
    }
}
