package game.model.save;

import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.Creature;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.world.World;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;

public class SaveMemento implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Coords lastPos;
    private LinkedList<Creature> heroes;
    private World world;
    private DialogMemento dialogMemento;

    public SaveMemento() {}

    public SaveMemento(String name, Coords lastPos, LinkedList<Creature> heroes, DialogMemento dialogMemento) {
        this.name = name;
        this.lastPos = lastPos;
        this.heroes = heroes;
        this.dialogMemento = dialogMemento;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coords getLastPos() {
        return lastPos;
    }

    public void setLastPos(Coords lastPos) {
        this.lastPos = lastPos;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public LinkedList<Creature> getHeroes() {
        return heroes;
    }

    public void setHeroes(LinkedList<Creature> heroes) {
        this.heroes = heroes;
    }

    public DialogMemento getDialogMemento() {
        return dialogMemento;
    }

    public void setDialogMemento(DialogMemento dialogMemento) {
        this.dialogMemento = dialogMemento;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(name);

        out.writeObject(lastPos);

        out.writeObject(heroes);

        out.writeObject(world);

        out.writeObject(dialogMemento);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        name = in.readUTF();

        lastPos = (Coords) in.readObject();

        heroes = (LinkedList<Creature>) in.readObject();

        world = (World) in.readObject();

        dialogMemento = (DialogMemento) in.readObject();
    }
}
