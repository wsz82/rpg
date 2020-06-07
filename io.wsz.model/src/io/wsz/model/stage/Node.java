package io.wsz.model.stage;

import io.wsz.model.content.Content;

import java.util.List;

public class Node {
    private Content content;
    private List<Node> greater;
    private List<Node> lesser;

    public Node(Content content, List<Node> greater, List<Node> lesser) {
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

    public List<Node> getGreater() {
        return greater;
    }

    public void setGreater(List<Node> greater) {
        this.greater = greater;
    }

    public List<Node> getLesser() {
        return lesser;
    }

    public void setLesser(List<Node> lesser) {
        this.lesser = lesser;
    }

    @Override
    public String toString() {
        return getContent().getItem().getName();
    }
}
