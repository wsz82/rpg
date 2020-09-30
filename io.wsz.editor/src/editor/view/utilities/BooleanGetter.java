package editor.view.utilities;

@FunctionalInterface
public interface BooleanGetter<O> {

    boolean get(O object);

}
