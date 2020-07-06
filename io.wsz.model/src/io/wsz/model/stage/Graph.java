package io.wsz.model.stage;

import io.wsz.model.item.PosItem;

import java.util.List;

public class Graph<A extends PosItem> {
    private List<Node<A>> nodes;

    public Graph(List<Node<A>> nodes) {
        this.nodes = nodes;
    }

    public List<Node<A>> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node<A>> nodes) {
        this.nodes = nodes;
    }
}