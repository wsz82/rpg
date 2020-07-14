package game.model.save;

import io.wsz.model.item.Creature;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;

public class SaveMemento implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Coords lastPos;
    private LinkedList<Creature> heroes;
    private List<Location> locations;

    public SaveMemento() {}

    public SaveMemento(String name, Coords lastPos, LinkedList<Creature> heroes) {
        this.name = name;
        this.lastPos = lastPos;
        this.heroes = heroes;
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

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public LinkedList<Creature> getHeroes() {
        return heroes;
    }

    public void setHeroes(LinkedList<Creature> heroes) {
        this.heroes = heroes;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(name);

        out.writeObject(lastPos);

        out.writeObject(heroes);

        out.writeObject(locations);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        name = in.readUTF();

        lastPos = (Coords) in.readObject();

        heroes = (LinkedList<Creature>) in.readObject();

        locations = (List<Location>) in.readObject();
    }
}
