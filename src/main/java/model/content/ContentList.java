package model.content;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.location.CurrentLocation;
import view.layers.LevelValueListener;
import view.layers.VisibleValueListener;

public class ContentList implements LevelValueListener, VisibleValueListener {
    private final ObservableList<Content> contents;

    public ContentList() {
        this.contents = FXCollections.observableArrayList();
    }

    public ObservableList<Content> get() {
        return contents;
    }

    @Override
    public void onLevelValueChanged(int oldValue, int newValue) {
        for (Content content:
                CurrentLocation.get().getContent()) {
            if (content.getLevel() == oldValue) {
                content.setLevel(newValue);
            }
        }
    }

    @Override
    public void onVisibleValueChanged(int level, boolean newValue) {
        for (Content content:
                CurrentLocation.get().getContent()) {
            if (content.getLevel() == level) {
                content.setVisible(newValue);
            }
        }
    }
}
