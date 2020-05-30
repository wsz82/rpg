package game.model.save;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class SavesList {
    private final ObservableList<String> saves = FXCollections.observableArrayList();
    private static SavesList singleton;

    public static SavesList get() {
        if (singleton == null) {
            singleton = new SavesList();
        }
        return singleton;
    }

    private SavesList(){}

    public ObservableList<String> getSaves() {
        return saves;
    }
}
