package view.stage;

import javafx.application.Application;
import javafx.stage.Stage;
import model.layer.Layer;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;
import model.stage.CurrentLayer;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        initLocation();
        MainView view = new MainView(stage);
        view.show();
    }

    private void initLocation() {
        Location location;
        location = new Location("new", 800, 600);
        Layer layer = new Layer("new");
        location.getLayers().get().add(layer);
        CurrentLayer.get().setCurrentLayer(layer);
        LocationsList.get().add(location);
        CurrentLocation.get().setLocation(location);
    }

    public static void main(String[] args) {
        launch();
    }

}