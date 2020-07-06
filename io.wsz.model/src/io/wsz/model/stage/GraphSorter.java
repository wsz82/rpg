package io.wsz.model.stage;

import io.wsz.model.item.PosItem;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.stage.Comparator.Comparison.GREAT;
import static io.wsz.model.stage.Comparator.Comparison.LESS;
import static io.wsz.model.stage.Comparator.compare;

public class GraphSorter<A extends PosItem> {
    private final Graph<A> graph = new Graph<A>(new ArrayList<>(0));
    private final List<A> sortedItems = new ArrayList<>(0);

    public void sortItems(List<A> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        List<Node<A>> nodes = graph.getNodes();
        nodes.clear();

        for (A pi : items) {
            Node<A> newNode = new Node<A>(pi);

            for (Node<A> n : nodes) {
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
            Node<A> n = nodes.get(0);
            int size  = nodes.size();
            while (size > 0) {
                if (n == null) {
                    n = nodes.get(0);
                }
                Node<A> min = findMin(n);

                sortedItems.add(min.getItem());
                nodes.remove(min);

                size = size - 1;

                n = findFirstNotEmptyGreater(min);
            }

            items.clear();
            items.addAll(sortedItems);
        }
    }

    private Node<A> findFirstNotEmptyGreater(Node<A> last) {
        List<Node<A>> greater = last.getGreater();
        return greater.isEmpty() ? null : greater.get(0);
    }

    private Node<A> findMin(Node<A> n) {
        List<Node<A>> lesser = n.getLesser();
        while (!lesser.isEmpty()) {
            n = lesser.get(0);
            lesser = n.getLesser();
        }
        for (Node<A> greater : n.getGreater()) {
            greater.getLesser().remove(n);
        }
        return n;
    }
}
