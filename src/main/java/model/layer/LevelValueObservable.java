package model.layer;

public interface LevelValueObservable {

    void attach(LevelValueListener listener);

    void remove(LevelValueListener listener);

    void notify(int oldValue, int newValue);
}
