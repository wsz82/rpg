package io.wsz.model.stage;

import io.wsz.model.item.PosItem;

import java.util.ArrayList;
import java.util.List;

public class Node<A extends PosItem> {
    private A item;
    private final List<Node<A>> greater = new ArrayList<>(0);
    private final List<Node<A>> lesser = new ArrayList<>(0);

    public Node(A item) {
        this.item = item;
    }

    public A getItem() {
        return item;
    }

    public void setItem(A item) {
        this.item = item;
    }

    public List<Node<A>> getGreater() {
        return greater;
    }

    public List<Node<A>> getLesser() {
        return lesser;
    }

    @Override
    public String toString() {
        return getItem().getAssetId();
    }
}
