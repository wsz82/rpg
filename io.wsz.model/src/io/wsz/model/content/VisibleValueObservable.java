package io.wsz.model.content;

public interface VisibleValueObservable {

    void attachVisibleValueListener(VisibleValueListener listener);

    void removeVisibleValueListener(VisibleValueListener listener);

    void notifyVisibleValueListeners(int level, boolean newValue);
}
