package queues;

import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private int n;
    private final Node front;
    private final Node tail;

    private class Node {
        Item item;
        Node next;
        Node prev;
        Node() {

        }
        Node(Item item) {
            this.item = item;
        }
    }

    // construct an empty deque
    public Deque() {
        n = 0;
        front = new Node();
        tail = new Node();
        front.next = tail;
        tail.prev = front;
    }

    // is the deque empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the deque
    public int size() {
        return n;
    }

    // add the item to the front
    public void addFirst(Item item) {
        validateItem(item);
        insert(new Node(item), front, front.next);
        n++;
    }

    // add the item to the back
    public void addLast(Item item) {
        validateItem(item);
        insert(new Node(item), tail.prev, tail);
        n++;
    }

    // remove and return the item from the front
    public Item removeFirst() {
        emptyCheck();
        Item res = front.next.item;
        remove(front.next);
        return res;
    }

    // remove and return the item from the back
    public Item removeLast() {
        emptyCheck();
        Item res = tail.prev.item;
        remove(tail.prev);
        return res;
    }

    // return an iterator over items in order from front to back
    public Iterator<Item> iterator() {
        return new Iterator<Item>() {
            private Node cur = front;

            @Override
            public boolean hasNext() {
                return cur.next.item != null;
            }

            @Override
            public Item next() {
                Item res = cur.next.item;
                if (res == null) {
                    throw new NoSuchElementException();
                }
                cur = cur.next;
                return res;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    // unit testing (required)
    public static void main(String[] args) {
        Deque<Integer> deque = new Deque<>();
        StdOut.println("initial deque size: " + deque.size());
        StdOut.println("is deque empty: " + deque.isEmpty());
//        deque.removeFirst();
//        deque.removeLast();
        deque.addFirst(5);
        deque.addFirst(4);
        deque.addFirst(3);
        deque.addFirst(2);
        deque.addFirst(1);
        deque.addLast(6);
        deque.addLast(7);
        StdOut.println("current deque size: " + deque.size());
        StdOut.println("output(front to back order): ");
        for (int i : deque) {
            StdOut.print(i + "\t");
        }
        StdOut.println();
        StdOut.println("remove last element: " + deque.removeLast());
        deque.sout(deque);
        StdOut.println("remove last element: " + deque.removeLast());
        deque.sout(deque);
        StdOut.println("remove first element: " + deque.removeFirst());
        deque.sout(deque);
    }

    private void sout(Deque<Item> deque) {
        StdOut.println("output(by front to back order): ");
        for (Item i : deque) {
            StdOut.print(i + "\t");
        }
        StdOut.println();
        
    }

    private void insert(Node insert, Node p, Node q) {
        insert.next = q;
        insert.prev = p;
        p.next = insert;
        q.prev = insert;
    }

    private void remove(Node remove) {
        remove.prev.next = remove.next;
        remove.next.prev = remove.prev;
        remove = null;
        n--;
    }

    private void emptyCheck() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    private void validateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
    }
}
