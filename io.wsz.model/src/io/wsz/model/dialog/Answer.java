package io.wsz.model.dialog;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class Answer implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String text;
    private final List<Question> questions = new ArrayList<>(0);

    public Answer() {}

    public Answer(String text, List<Question> questions) {
        this.text = text;
        this.questions.addAll(questions);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(text);

        out.writeObject(questions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        text = in.readUTF();

        List<Question> input = (List<Question>) in.readObject();
        questions.addAll(input);
    }
}
