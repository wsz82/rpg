package io.wsz.model.asset;

import io.wsz.model.item.ItemType;

public class Fog extends Asset {
    public Fog() {
        setName("fog");
        setType(ItemType.FOG);
        setRelativePath("fog.png");
    }
}
