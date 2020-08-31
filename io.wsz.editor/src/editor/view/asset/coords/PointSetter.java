package editor.view.asset.coords;

import io.wsz.model.stage.Coords;

@FunctionalInterface
public interface PointSetter {

    void set(Coords point);
}
