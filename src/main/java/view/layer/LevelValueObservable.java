package view.layer;

public interface LevelValueObservable {

    void attachLevelValueListener(LevelValueListener listener);

    void removeLevelValueListener(LevelValueListener listener);

    void notifyLevelValueListeners(int oldValue, int newValue);
}
