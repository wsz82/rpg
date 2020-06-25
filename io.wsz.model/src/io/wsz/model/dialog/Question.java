package io.wsz.model.dialog;

import java.io.Serializable;

public class Question implements Serializable {
    private static final long serialVersionUID = 1L;

    private String text;
    private Answer answer;
    private boolean finish;

    public Question() {}

    public Question(String text, Answer answer, boolean finish) {
        this.text = text;
        this.answer = answer;
        this.finish = finish;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    @Override
    public String toString() {
        return text;
    }
}
