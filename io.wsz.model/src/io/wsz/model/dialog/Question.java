package io.wsz.model.dialog;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Question implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String text;
    private int answerIndex;
    private boolean finish;

    public Question() {}

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int answerIndex) {
        this.answerIndex = answerIndex;
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);

        out.writeUTF(text);

        out.writeInt(answerIndex);

        out.writeBoolean(finish);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        text = in.readUTF();

        answerIndex = in.readInt();

        finish = in.readBoolean();
    }
}
