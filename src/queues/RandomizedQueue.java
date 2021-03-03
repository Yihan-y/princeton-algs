package queues;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RandomizedQueue<Item> implements Iterable<Item> {
    private int n;
    private Item[] items;

    // construct an empty randomized queue
    public RandomizedQueue() {
        n = 0;
        items = (Item[]) new Object[2];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return n == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return n;
    }

    // add the item
    public void enqueue(Item item) {
        validateItem(item);
        if (n >= items.length / 2) {
            items = resize(items.length * 2);
        }
        items[n++] = item;
    }

    // remove and return a random item
    public Item dequeue() {
        checkEmpty();
        int index = randomIndex();
        Item remove = items[index];
        items[index] = items[--n];
        items[n] = null;
        // shrink
        if (n < items.length / 4) {
            items = resize(items.length / 2);
        }
        return remove;
    }

    // return a random item (but do not remove it)
    public Item sample() {
        checkEmpty();
        return items[randomIndex()];
    }

    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedIterator();
    }

    // unit testing (required)
    public static void main(String[] args) {
        RandomizedQueue<Integer> randomizedQueue = new RandomizedQueue<>();
        StdOut.println("initial randomized queue size: " + randomizedQueue.size());
        StdOut.println("is randomized queue empty: " + randomizedQueue.isEmpty());
        randomizedQueue.enqueue(1);
        randomizedQueue.enqueue(2);
        randomizedQueue.enqueue(3);
        randomizedQueue.enqueue(4);
        StdOut.println("current randomized queue size: " + randomizedQueue.size());
        StdOut.println("output(random order): ");
        for (int i : randomizedQueue) {
            StdOut.print(i + "\t");
        }
        StdOut.println();
        StdOut.println("sample :" + randomizedQueue.sample());
        StdOut.println("randomly remove an element: " + randomizedQueue.dequeue());
        StdOut.println("output(random order): ");
        for (int i : randomizedQueue) {
            StdOut.print(i + "\t");
        }
    }

    private int randomIndex() {
        return StdRandom.uniform(n);
    }

    private Item[] resize(int n) {
        if (n <= 2) {
            return items;
        }
        Item[] updated = (Item[]) new Object[n];
        System.arraycopy(items, 0, updated, 0, this.n);
        return updated;
    }

    private void checkEmpty() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
    }

    private void validateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        }
    }

    private class RandomizedIterator implements Iterator<Item> {
        // to maintain iterator, create an array to keep track of shuffled index
        private final int[] shuffledIndex;
        private int p;

        public RandomizedIterator() {
            p = 0;
            shuffledIndex = new int[n];
            for (int i = 0; i < n; i++) {
                shuffledIndex[i] = i;
            }
            // shuffle
            for (int i = 0; i < n; i++) {
                int index = StdRandom.uniform(i, n);
                int tmp = shuffledIndex[i];
                shuffledIndex[i] = shuffledIndex[index];
                shuffledIndex[index] = tmp;
            }
        }

        @Override
        public boolean hasNext() {
            return p < shuffledIndex.length;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return items[shuffledIndex[p++]];
        }
    }
}
