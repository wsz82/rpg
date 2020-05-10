import javafx.beans.property.*;

public class Layer {
    private IntegerProperty level;
    private StringProperty name;
    private BooleanProperty visible;

    public void setLevel(int value) {
        levelProperty().set(value);
    }

    public int getLevel() {
        return levelProperty().get();
    }

    public IntegerProperty levelProperty() {
        if (level == null) level = new SimpleIntegerProperty(this, "level");
        return level;
    }

    public void setName(String value) {
        nameProperty().set(value);
    }

    public String getName() {
        return nameProperty().get();
    }

    public StringProperty nameProperty() {
        if (name == null) name = new SimpleStringProperty(this, "name");
        return name;
    }

    public void setVisible(boolean value) {
        visibleProperty().set(value);
    }

    public boolean getVisible() {
        return visibleProperty().get();
    }

    public BooleanProperty visibleProperty() {
        if (visible == null) visible = new SimpleBooleanProperty(this, "visible");
        return visible;
    }
}
