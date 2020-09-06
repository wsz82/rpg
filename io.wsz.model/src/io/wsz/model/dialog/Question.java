package io.wsz.model.dialog;

import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
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
    private Requirements requirements;

    public Question() {}

    public Question(String text, String answerID) {
        this.text = text;
        this.answerID = answerID;
    }

    public boolean doMatchRequirements(Creature pc, PosItem npc) {
        if (requirements == null) {
            return true;
        }
        return requirements.doMatch(pc, npc);
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

    public Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
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
        return Objects.equals(getText(), question.getText()) &&
                Objects.equals(getAnswerID(), question.getAnswerID()) &&
                Objects.equals(getRequirements(), question.getRequirements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), getAnswerID(), getRequirements());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(text);

        out.writeObject(answerID);

        out.writeObject(requirements);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        text = (String) in.readObject();

        answerID = (String) in.readObject();

        requirements = (Requirements) in.readObject();
    }
}
