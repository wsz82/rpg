package io.wsz.model.stage;

import io.wsz.model.item.PosItem;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private PosItem content;
    private final List<Node> greater = new ArrayList<>(0);
    private final List<Node> lesser = new ArrayList<>(0);

    public Node(PosItem content) {
        this.content = content;
    }

    public PosItem getItem() {
        return content;
    }

    public void setContent(PosItem content) {
        this.content = content;
    }

    public List<Node> getGreater() {
        return greater;
    }

    public List<Node> getLesser() {
        return lesser;
    }

    @Override
    public String toString() {
        return getItem().getName();
    }
}
