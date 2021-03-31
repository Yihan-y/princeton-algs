package burrows;

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        LinkedList list = initList();
        while (!BinaryStdIn.isEmpty()) {
            char cur = BinaryStdIn.readChar();
            int index = getIndexOf(cur, list);
            BinaryStdOut.write((char) index);
        }
        BinaryStdOut.flush();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        LinkedList list = initList();
        while (!BinaryStdIn.isEmpty()) {
            char cur = BinaryStdIn.readChar();
            char c = getByIndex(cur, list);
            BinaryStdOut.write(c);
        }
        BinaryStdOut.flush();
    }

    private static LinkedList initList() {
        LinkedList list = new LinkedList();
        for (int i = 0; i < 256; i++) {
            list.add(i);
        }
        return list;
    }

    private static int getIndexOf(int c,  LinkedList list) {
        return list.getIndexAndMoveToFirst(c);
    }

    private static char getByIndex(int index, LinkedList list) {
        return (char) list.getByIndexAndMoveToFirst(index);
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException();
        }
        String op = args[0], encode = "-", decode = "+";
        if (op.equals(encode)) {
            encode();
        } else if (op.equals(decode)) {
            decode();
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static class LinkedList {

        private static class Node {
            private Node prev, next;
            private final int val;

            public Node(int val) {
                this.val = val;
            }
        }

        private final Node head, tail;
        private Node p;

        public LinkedList() {
            head = new Node(-1);
            tail = new Node(-1);
            head.next = tail;
            tail.prev = head;
            p = head;
        }

        public void add(int val) {
            Node cur = new Node(val);
            p.next = cur;
            cur.prev = p;
            cur.next = tail;
            tail.prev = cur;
            p = p.next;
        }

        private boolean moveToFirst(Node p) {
            if (p == null) {
                return false;
            }
            p.prev.next = p.next;
            p.next.prev = p.prev;
            p.next = head.next;
            p.prev = head;
            head.next.prev = p;
            head.next = p;
            return true;
        }

        public int getIndexAndMoveToFirst(int val) {
            int index = 0;
            Node p = head.next;
            while (p != null && p.val != val) {
                p = p.next;
                index++;
            }
            if (!moveToFirst(p)) {
                return -1;
            }
            return index;
        }

        private int getByIndexAndMoveToFirst(int index) {
            Node p = head.next;
            int cur = 0;
            while (p != null && cur++ != index) {
                p = p.next;
            }
            if (!moveToFirst(p)) {
                return -1;
            }
            return p.val;
        }

    }

}
