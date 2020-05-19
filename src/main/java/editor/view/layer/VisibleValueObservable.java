package editor.view.layer;

public interface VisibleValueObservable {

    void attachVisibleValueListener(VisibleValueListener listener);

    void removeVisibleValueListener(VisibleValueListener listener);

    void notifyVisibleValueListeners(int level, boolean newValue);
}
