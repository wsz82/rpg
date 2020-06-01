package game.model.save;

import io.wsz.model.plugin.LocationSerializable;

import java.io.Serializable;
import java.util.List;

public class SaveMemento implements Serializable {
    private String name;
    private double hValue;
    private double vValue;
    private List<LocationSerializable> locations;
    private String currentLocationName;
    private int currentLayer;

    public SaveMemento(){}

    public SaveMemento(String name, double hValue, double vValue, String currentLocationName, int currentLayer) {
        this.name = name;
        this.hValue = hValue;
        this.vValue = vValue;
        this.currentLocationName = currentLocationName;
        this.currentLayer = currentLayer;
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
}
