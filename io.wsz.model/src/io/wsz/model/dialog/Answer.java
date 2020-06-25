package io.wsz.model.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Answer implements Serializable {
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
}
