package io.wsz.model.stage;

import io.wsz.model.item.PosItem;

import java.util.ArrayList;
import java.util.List;

public class Node<A extends PosItem> {
    private A content;
    private final List<Node<A>> greater = new ArrayList<>(0);
    private final List<Node<A>> lesser = new ArrayList<>(0);

    public Node(A content) {
        this.content = content;
    }

    public A getItem() {
        return content;
    }

    public void setContent(A content) {
        this.content = content;
    }

    public List<Node<A>> getGreater() {
        return greater;
    }

    public List<Node<A>> getLesser() {
        return lesser;
    }

    @Override
    public String toString() {
        return getItem().getName();
    }
}
