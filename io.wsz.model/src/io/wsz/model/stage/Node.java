package io.wsz.model.stage;

import io.wsz.model.content.Content;

import java.util.LinkedList;

public class Node {
    private Content content;
    private LinkedList<Node> greater;
    private LinkedList<Node> lesser;

    public Node(Content content, LinkedList<Node> greater, LinkedList<Node> lesser) {
        this.content = content;
        this.greater = greater;
        this.lesser = lesser;
    }

    public Content getContent() {
        return content;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public LinkedList<Node> getGreater() {
        return greater;
    }

    public void setGreater(LinkedList<Node> greater) {
        this.greater = greater;
    }

    public LinkedList<Node> getLesser() {
        return lesser;
    }

    public void setLesser(LinkedList<Node> lesser) {
        this.lesser = lesser;
    }

    @Override
    public String toString() {
        return getContent().getItem().getName();
    }
}
