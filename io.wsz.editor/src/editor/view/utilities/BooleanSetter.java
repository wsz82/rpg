package editor.view.utilities;

@FunctionalInterface
public interface BooleanSetter<O> {

    void set(O object, boolean value);

}
