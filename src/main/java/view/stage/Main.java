package view.stage;

import javafx.application.Application;
import javafx.stage.Stage;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        initLocation();
        MainView view = new MainView(stage);
        view.show();
    }

    private void initLocation() {
        Location location;
        location = new Location("new location", 800, 600);
        CurrentLocation.get().setLocation(location);
        LocationsList.get().add(location);
    }

    public static void main(String[] args) {
        launch();
    }

}