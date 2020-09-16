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

public class Answer implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String text;
    private String questionsListID;
    private Requirements requirements;
    private Script beginScript;

    public Answer() {}

    public Answer(String text, String questionsListID) {
        this.text = text;
        this.questionsListID = questionsListID;
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

    public String getQuestionsListID() {
        return questionsListID;
    }

    public void setQuestionsListID(String questionsListID) {
        this.questionsListID = questionsListID;
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
        if (!(o instanceof Answer)) return false;
        Answer answer = (Answer) o;
        return Objects.equals(getText(), answer.getText()) &&
                Objects.equals(getQuestionsListID(), answer.getQuestionsListID()) &&
                Objects.equals(getRequirements(), answer.getRequirements());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getText(), getQuestionsListID(), getRequirements());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(text);

        out.writeObject(questionsListID);

        out.writeObject(requirements);

        out.writeObject(beginScript);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        text = (String) in.readObject();

        questionsListID = (String) in.readObject();

        requirements = (Requirements) in.readObject();

        beginScript = (Script) in.readObject();
    }
}
