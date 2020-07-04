package io.wsz.model.stage;

import io.wsz.model.item.PosItem;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.stage.Comparator.Comparison.GREAT;
import static io.wsz.model.stage.Comparator.Comparison.LESS;
import static io.wsz.model.stage.Comparator.compare;

public class GraphSorter {
    private final Graph graph = new Graph(new ArrayList<>(0));
    private final List<PosItem> sortedItems = new ArrayList<>(0);

    public void sortItems(List<PosItem> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        List<Node> nodes = graph.getNodes();
        nodes.clear();

        for (PosItem pi : items) {
            Node newNode = new Node(pi);

            for (Node n : nodes) {
                Comparator.Comparison result = compare(pi, n);

                if (result.equals(GREAT)) {
                    n.getGreater().add(newNode);
                    newNode.getLesser().add(n);
                } else if (result.equals(LESS)) {
                    n.getLesser().add(newNode);
                    newNode.getGreater().add(n);
                }
            }
            nodes.add(newNode);
        }

        sortedItems.clear();
        if (!nodes.isEmpty()) {
            Node n = nodes.get(0);
            int size  = nodes.size();
            while (size > 0) {
                if (n == null) {
                    n = nodes.get(0);
                }
                Node min = findMin(n);

                sortedItems.add(min.getItem());
                nodes.remove(min);

                size = size - 1;

                n = findFirstNotEmptyGreater(min);
            }

            items.clear();
            items.addAll(sortedItems);
        }
    }

    private Node findFirstNotEmptyGreater(Node last) {
        List<Node> greater = last.getGreater();
        return greater.isEmpty() ? null : greater.get(0);
    }

    private Node findMin(Node n) {
        List<Node> lesser = n.getLesser();
        while (!lesser.isEmpty()) {
            n = lesser.get(0);
            lesser = n.getLesser();
        }
        for (Node greater : n.getGreater()) {
            greater.getLesser().remove(n);
        }
        return n;
    }
}
