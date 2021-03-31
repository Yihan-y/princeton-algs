package burrows;

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {
    private final int length;
    private final int[] indexes;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        validateNull(s);
        length = s.length();
        indexes = new int[length];
        if (length > 0) {
            buildMyNodes(s);
        }
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        validateIndex(i);
        return indexes[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        String test = "ABRACADABRA!";
        CircularSuffixArray circularSuffixArray = new CircularSuffixArray(test);
        int len = circularSuffixArray.length();
        StdOut.println("length of circular suffix array: " + len);
        for (int i = 0; i < len; i++) {
            StdOut.println("index[" + i + "] = " + circularSuffixArray.index(i));
        }
    }

    private void buildMyNodes(String s) {
        MyNode[] nodes = new MyNode[length];
        nodes[0] = new MyNode(s, 0);
        for (int i = 1; i < length; i++) {
            s = s.substring(1) + s.substring(0, 1);
            nodes[i] = new MyNode(s, i);
        }
        Arrays.sort(nodes);
        for (int i = 0; i < length; i++) {
            indexes[i] = nodes[i].index;
        }
    }

    private void validateNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }

    private void validateIndex(int index) {
        if (index < 0 || index >= length) {
            throw new IllegalArgumentException();
        }
    }

    private static class MyNode implements Comparable<MyNode> {
        private final String s;
        private final int index;

        public MyNode(String s, int index) {
            this.s = s;
            this.index = index;
        }

        @Override
        public int compareTo(MyNode that) {
            return this.s.compareTo(that.s);
        }
    }
}
