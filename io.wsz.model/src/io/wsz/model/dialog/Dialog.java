package io.wsz.model.dialog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Dialog implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<Answer> answers = new ArrayList<>(0);
    private Answer startAnswer;

    public Dialog() {}

    public Dialog(List<Answer> answers, Answer startAnswer) {
        this.answers.addAll(answers);
        this.startAnswer = startAnswer;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public Answer getStartAnswer() {
        return startAnswer;
    }

    public void setStartAnswer(Answer startAnswer) {
        this.startAnswer = startAnswer;
    }

    @Override
    public String toString() {
        return startAnswer.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dialog dialog = (Dialog) o;
        if (startAnswer == null && dialog.startAnswer == null) {
            return true;
        } else if (startAnswer == null || dialog.startAnswer == null) {
            return false;
        }
        return startAnswer.getText().equals(dialog.startAnswer.getText()); //TODO equals method when dialog edit stage requires identity equals
    }

    @Override
    public int hashCode() {
        return Objects.hash(startAnswer.getText());
    }
}
