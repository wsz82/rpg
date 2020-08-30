package io.wsz.model.dialog;

import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class Question implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String text;
    private String answerID;
    private boolean isFinishingDialog;

    public Question() {}

    public Question(String text, String answerID, boolean isFinishingDialog) {
        this.text = text;
        this.answerID = answerID;
        this.isFinishingDialog = isFinishingDialog;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswerID() {
        return answerID;
    }

    public void setAnswerID(String answerID) {
        this.answerID = answerID;
    }

    public boolean isFinishingDialog() {
        return isFinishingDialog;
    }

    public void setFinishingDialog(boolean finishingDialog) {
        this.isFinishingDialog = finishingDialog;
    }

    @Override
    public String toString() {
        int max = 20;
        if (text == null) return "";
        if (text.length() > max) {
            return text.substring(0, max) + "...";
        } else {
            return text;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question question = (Question) o;
        return isFinishingDialog() == question.isFinishingDialog() &&
                Objects.equals(getText(), question.getText()) &&
                Objects.equals(getAnswerID(), question.getAnswerID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), getAnswerID(), isFinishingDialog());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(text);

        out.writeUTF(answerID);

        out.writeBoolean(isFinishingDialog);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        text = in.readUTF();

        answerID = in.readUTF();

        isFinishingDialog = in.readBoolean();
    }
}
