package io.wsz.model.dialog;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.Script;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class Question implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String text;
    private String answersListID;
    private Requirements requirements;
    private Script beginScript;

    public Question() {}

    public Question(String text, String answersListID) {
        this.text = text;
        this.answersListID = answersListID;
    }

    public boolean doMatchRequirements(Controller controller, Creature pc, PosItem npc) {
        if (requirements == null) {
            return true;
        }
        return requirements.doMatch(controller, pc, npc);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnswersListID() {
        return answersListID;
    }

    public void setAnswersListID(String answersListID) {
        this.answersListID = answersListID;
    }

    public Requirements getRequirements() {
        return requirements;
    }

    public void setRequirements(Requirements requirements) {
        this.requirements = requirements;
    }

    public Script getBeginScript() {
        return beginScript;
    }

    public void setBeginScript(Script beginScript) {
        this.beginScript = beginScript;
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
                Objects.equals(getAnswersListID(), question.getAnswersListID()) &&
                Objects.equals(getRequirements(), question.getRequirements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), getAnswersListID(), getRequirements());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(text);

        out.writeObject(answersListID);

        out.writeObject(requirements);

        out.writeObject(beginScript);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        text = (String) in.readObject();

        answersListID = (String) in.readObject();

        requirements = (Requirements) in.readObject();

        beginScript = (Script) in.readObject();
    }
}
