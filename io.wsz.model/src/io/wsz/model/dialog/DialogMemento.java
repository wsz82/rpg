package io.wsz.model.dialog;

import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class DialogMemento implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<DialogItem> dialogs;
    private Creature pc;
    private PosItem<?,?> npc;
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

    public Creature getPc() {
        return pc;
    }

    public void setPc(Creature pc) {
        this.pc = pc;
    }

    public PosItem<?,?> getNpc() {
        return npc;
    }

    public void setNpc(PosItem<?,?> npc) {
        this.npc = npc;
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

        out.writeObject(pc);

        out.writeObject(npc);

        out.writeObject(lastAnswer);

        out.writeBoolean(isFinished);

        out.writeInt(curPos);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        dialogs = (List<DialogItem>) in.readObject();

        pc = (Creature) in.readObject();

        npc = (PosItem<?, ?>) in.readObject();

        lastAnswer = (Answer) in.readObject();

        isFinished = in.readBoolean();

        curPos = in.readInt();
    }
}
