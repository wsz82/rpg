package game.model.save;

import io.wsz.model.item.Creature;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class SaveMemento implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Coords lastPos;
    private List<Location> locations;
    private String currentLocationName;
    private int currentLayer;
    private LinkedList<Creature> heroes;

    public SaveMemento() {}

    public SaveMemento(String name, Coords lastPos, String currentLocationName, int currentLayer,
                       LinkedList<Creature> heroes) {
        this.name = name;
        this.lastPos = lastPos;
        this.currentLocationName = currentLocationName;
        this.currentLayer = currentLayer;
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

    public String getCurrentLocationName() {
        return currentLocationName;
    }

    public void setCurrentLocationName(String currentLocationName) {
        this.currentLocationName = currentLocationName;
    }

    public int getCurrentLayer() {
        return currentLayer;
    }

    public void setCurrentLayer(int currentLayer) {
        this.currentLayer = currentLayer;
    }

    public LinkedList<Creature> getHeroes() {
        return heroes;
    }

    public void setHeroes(LinkedList<Creature> heroes) {
        this.heroes = heroes;
    }
}
