package game.model.save;

import model.plugin.LocationSerializable;

import java.io.Serializable;
import java.util.List;

public class SaveMemento implements Serializable {
    private String name;
    private double hValue;
    private double vValue;
    private List<LocationSerializable> locations;

    public SaveMemento(){}

    public SaveMemento(String name, double hValue, double vValue){
        this.name = name;
        this.hValue = hValue;
        this.vValue = vValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double gethValue() {
        return hValue;
    }

    public void sethValue(double hValue) {
        this.hValue = hValue;
    }

    public double getvValue() {
        return vValue;
    }

    public void setvValue(double vValue) {
        this.vValue = vValue;
    }

    public List<LocationSerializable> getLocations() {
        return locations;
    }

    public void setLocations(List<LocationSerializable> locations) {
        this.locations = locations;
    }
}
