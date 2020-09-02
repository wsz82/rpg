package editor.view.asset.coords;

import io.wsz.model.stage.Coords;

import java.util.List;

@FunctionalInterface
public interface PolygonsSetter {

    void set(List<List<Coords>> polygons);

}
