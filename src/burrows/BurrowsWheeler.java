package burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.Arrays;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String line = "";
        while (!BinaryStdIn.isEmpty()) {
            line = BinaryStdIn.readString();
        }
        CircularSuffixArray csa = new CircularSuffixArray(line);
        int first = -1, len = line.length();
        char[] t = new char[len];
        for (int i = 0; i < len; i++) {
            int index = csa.index(i);
            if (index == 0) {
                first = i;
                t[i] = line.charAt(len - 1);
            }else {
                t[i] = line.charAt(index - 1);
            }
        }
        BinaryStdOut.write(first);
        for (int i = 0; i < len; i++) {
            BinaryStdOut.write(t[i]);
        }
        BinaryStdOut.flush();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = -1;
        String line = "";
        while (!BinaryStdIn.isEmpty()) {
            if (first < 0) {
                first = BinaryStdIn.readInt();
            }
            line = BinaryStdIn.readString();
        }
        char[] t = line.toCharArray();
        int len = t.length;
        MyNode[] nodes = new MyNode[len];
        for (int i = 0; i < len; i++) {
            nodes[i] = new MyNode(t[i], i);
        }
        Arrays.sort(nodes);
        int index = 0;
        while (index++ < len) {
            MyNode cur = nodes[first];
            BinaryStdOut.write(cur.c);
            first = cur.index;
        }
        BinaryStdOut.flush();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException();
        }
        String op = args[0], transform = "-", inverse = "+";
        if (op.equals(transform)) {
            transform();
        } else if (op.equals(inverse)) {
            inverseTransform();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static class MyNode implements Comparable<MyNode> {
        private final char c;
        private final int index;

        public MyNode(char c, int index) {
            this.c = c;
            this.index = index;
        }

        @Override
        public int compareTo(MyNode that) {
            return Character.compare(this.c, that.c);
        }
    }
}
