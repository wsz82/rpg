package model.stage;

import view.stage.CurrentLayerBox;

public class CurrentLayer {
    private static int currentLayer = 0;

    public static int getCurrentLayer() {
        return currentLayer;
    }

    public static void setCurrentLayer(int currentLayer) {
        CurrentLayer.currentLayer = currentLayer;
        CurrentLayerBox.setCurrentLayerNumber(currentLayer);
    }
}
