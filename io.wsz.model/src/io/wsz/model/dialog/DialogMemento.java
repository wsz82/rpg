package io.wsz.model.dialog;

import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class DialogMemento<A extends PosItem, B extends PosItem> implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<DialogItem> dialogs;
    private A asking;
    private B answering;
    private Answer lastAnswer;
    private boolean isFinished;
    private int curPos;

    public DialogMemento() {}

    public DialogMemento(List<DialogItem> dialogs) {
        this.dialogs = dialogs;
    }

    public List<DialogItem> getDialogs() {
        return dialogs;
    }

    public PosItem getAsking() {
        return asking;
    }

    public void setAsking(A asking) {
        this.asking = asking;
    }

    public PosItem getAnswering() {
        return answering;
    }

    public void setAnswering(B answering) {
        this.answering = answering;
    }

    public Answer getLastAnswer() {
        return lastAnswer;
    }

    public void setLastAnswer(Answer lastAnswer) {
        this.lastAnswer = lastAnswer;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        this.isFinished = finished;
    }

    public int getCurPos() {
        return curPos;
    }

    public void setCurPos(int curPos) {
        this.curPos = curPos;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(dialogs);

        out.writeObject(asking);

        out.writeObject(answering);

        out.writeObject(lastAnswer);

        out.writeBoolean(isFinished);

        out.writeInt(curPos);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        dialogs = (List<DialogItem>) in.readObject();

        asking = (A) in.readObject();

        answering = (B) in.readObject();

        lastAnswer = (Answer) in.readObject();

        isFinished = in.readBoolean();

        curPos = in.readInt();
    }
}
