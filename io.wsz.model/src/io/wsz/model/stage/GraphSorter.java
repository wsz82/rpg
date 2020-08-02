package io.wsz.model.stage;

import io.wsz.model.item.PosItem;

import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.stage.ItemsComparator.Comparison.GREAT;
import static io.wsz.model.stage.ItemsComparator.Comparison.LESS;
import static io.wsz.model.stage.ItemsComparator.compare;

public class GraphSorter<A extends PosItem> {
    private final Graph<A> graph = new Graph<>(new ArrayList<>(0));
    private final List<A> sortedItems = new ArrayList<>(0);

    public void sortItems(List<A> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        List<Node<A>> nodes = graph.getNodes();

        int dif = items.size() - nodes.size();
        if (dif > 0) {
            for (int i = 0; i < dif; i++) {
                Node<A> newNode = new Node<>(null);
                nodes.add(newNode);
            }
        }

        for (int i = 0; i < items.size(); i++) {
            A pi = items.get(i);
            Node<A> newNode = nodes.get(i);
            newNode.setItem(pi);

            for (Node<A> n : nodes) {
                if (n == newNode) continue;
                A item = n.getItem();
                if (item == null) break;
                ItemsComparator.Comparison result = compare(pi, n);

                if (result.equals(GREAT)) {
                    n.getGreater().add(newNode);
                    newNode.getLesser().add(n);
                } else if (result.equals(LESS)) {
                    n.getLesser().add(newNode);
                    newNode.getGreater().add(n);
                }
            }
        }

        sortedItems.clear();
        if (!nodes.isEmpty()) {
            Node<A> n = nodes.get(0);

            int size = 0;
            for (Node<A> node : nodes) {
                if (node.getItem() != null) size++;
            }

            while (size > 0) {
                if (n == null || n.getItem() == null) {
                    for (Node<A> node : nodes) {
                        if (node.getItem() != null) n = node;
                    }
                }
                if (n == null) {
                    break;
                }
                Node<A> min = findMin(n);

                sortedItems.add(min.getItem());
                clearNode(min);

                size = size - 1;

                n = findFirstNotEmptyGreater(min);
            }

            items.clear();
            items.addAll(sortedItems);
        }
    }

    private void clearNode(Node<A> n) {
        n.setItem(null);
        n.getGreater().clear();
        n.getLesser().clear();
    }

    private Node<A> findFirstNotEmptyGreater(Node<A> last) {
        List<Node<A>> greater = last.getGreater();
        return greater.isEmpty() ? null : greater.get(0);
    }

    private Node<A> findMin(Node<A> n) {
        List<Node<A>> lesser = n.getLesser();
        Node<A> first = null;
        Node<A> last = n;
        while (!lesser.isEmpty()) {
            n = lesser.get(0);
            if (first != null && n == first) {
                break;
            }
            if (first == null) {
                first = n;
            }
            last = n;
            lesser = last.getLesser();
        }
        for (Node<A> greater : last.getGreater()) {
            greater.getLesser().remove(last);
        }
        return last;
    }
}
