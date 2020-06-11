package io.wsz.model.asset;

import java.util.ArrayList;
import java.util.List;

public class AssetsList {
    private static List<Asset> assets;

    public static List<Asset> get() {
        if (assets == null) {
            assets = new ArrayList<>(0);
        }
        return assets;
    }

    private AssetsList() {
    }
}
