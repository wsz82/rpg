package io.wsz.model.dialog;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dialog implements Externalizable {
    private static final long serialVersionUID = 1L;

    private final List<Answer> answers = new ArrayList<>(0);
    private int startAnswerIndex;

    public Dialog() {}

    public Dialog(List<Answer> answers, int startAnswerIndex) {
        this.answers.addAll(answers);
        this.startAnswerIndex = startAnswerIndex;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public int getStartAnswerIndex() {
        return startAnswerIndex;
    }

    public void setStartAnswerIndex(int startAnswerIndex) {
        this.startAnswerIndex = startAnswerIndex;
    }

    public Answer getStartAnswer() {
        if (startAnswerIndex < 0 || startAnswerIndex >= answers.size()) {
            return null;
        } else {
            return answers.get(startAnswerIndex);
        }
    }

    @Override
    public String toString() {
        return answers.get(startAnswerIndex).toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dialog)) return false;
        Dialog dialog = (Dialog) o;
        return getStartAnswerIndex() == dialog.getStartAnswerIndex() &&
                Objects.equals(getAnswers(), dialog.getAnswers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnswers(), getStartAnswerIndex());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);

        out.writeObject(answers);

        out.writeInt(startAnswerIndex);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        List<Answer> input = (List<Answer>) in.readObject();
        answers.addAll(input);

        startAnswerIndex = in.readInt();
    }
}
