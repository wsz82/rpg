package model.content;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.layer.LevelValueListener;

public class ContentList implements LevelValueListener {
    private ObservableList<Content> contents;
    private static ContentList contentList;

    public static ContentList getInstance() {
        if (contentList == null) {
            contentList = new ContentList();
        }
        return contentList;
    }

    public ObservableList<Content> get() {
        if (contents == null) {
            contents = FXCollections.observableArrayList();
        }
        return contents;
    }

    private ContentList() {
    }

    @Override
    public void onLevelValueChanged(int oldValue, int newValue) {
        for (Content content:
                contents) {
            if (content.getLevel() == oldValue) {
                content.setLevel(newValue);
            }
        }
    }
}
