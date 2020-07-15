package io.wsz.model.dialog;

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

    private final List<Answer> answers = new ArrayList<>(0);
    private int greetingIndex;

    public Dialog() {}

    public Dialog(List<Answer> answers, int greetingIndex) {
        this.answers.addAll(answers);
        this.greetingIndex = greetingIndex;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public int getGreetingIndex() {
        return greetingIndex;
    }

    public void setGreetingIndex(int greetingIndex) {
        this.greetingIndex = greetingIndex;
    }

    public Answer getGreeting() {
        if (greetingIndex < 0 || greetingIndex >= answers.size()) {
            return null;
        } else {
            return answers.get(greetingIndex);
        }
    }

    @Override
    public String toString() {
        if (answers.isEmpty()) {
            return "Empty dialog";
        } else {
            return getGreeting().toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dialog)) return false;
        Dialog dialog = (Dialog) o;
        return getGreetingIndex() == dialog.getGreetingIndex() &&
                Objects.equals(getAnswers(), dialog.getAnswers());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAnswers(), getGreetingIndex());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(answers);

        out.writeInt(greetingIndex);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        List<Answer> input = (List<Answer>) in.readObject();
        answers.addAll(input);

        greetingIndex = in.readInt();
    }
}
