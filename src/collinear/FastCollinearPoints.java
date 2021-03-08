package collinear;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FastCollinearPoints {
    private final Point[] points;
    private int segments;
    private final List<LineSegment> list;

    /*
     * restore <slope, lastPoint>
     * when slopes are identical, check lastPoints identical or not
     * maintain the list, since hashmap is forbidden
     * more alternatives, to check out avoidSub
     * hashmap<double,hashset<>> will do better since without hash collision
     * search o(1), put o(1), validate o(1)
     * */
    private List<MyPair> pairs;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        this.points = points.clone();
//        validateAndSort();
        validate();
        segments = 0;
        list = new ArrayList<>();
        pairs = new ArrayList<>();
        fastSearch();
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments;
    }

    // the line segments
    public LineSegment[] segments() {
        LineSegment[] res = new LineSegment[list.size()];
        return list.toArray(res);
    }

    private void validate() {
        for (Point p : points) {
            if (p == null) {
                throw new IllegalArgumentException();
            }
        }
        BST<Point, Integer> bst = new BST<>();
        for (Point p : points) {
            if (bst.get(p) != null) {
                throw new IllegalArgumentException();
            }
            bst.put(p, 0);
        }
    }

    private void validateAndSort() {
        for (Point p : points) {
            if (p == null) {
                throw new IllegalArgumentException();
            }
        }
        Arrays.sort(points);
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].compareTo(points[i + 1]) == 0) {
                throw new IllegalArgumentException();
            }
        }
    }

    /*
    * to maintain the initial sequent order, that is, stability (by point itself)
    * sorting alg should consider this situation
    * while Arrays.sort() for primitive types applies quick sort
    * and Arrays.sort(T[], Comparator<? super T>) -->
    * System.setProperty("java.util.Arrays.useLegacyMergeSort", "true") --> merge sort (stable)
    * comparator == null --> quick sort (unstable)
    * otherwise --> tim sort after jdk 1.7
    * tim sort combines merge sort and insertion sort so stable
    * */
    private void fastSearch() {
        int n = points.length;
        BST<Double, BST<Point, Integer>> bst = new BST<>();
        for (int i = 0; i < n - 3; i++) {
            Point first = points[i];
            Point[] tmp = Arrays.copyOfRange(points, i + 1, n);
//            Arrays.sort(tmp, first.slopeOrder());
            mergeSort(tmp, 0, tmp.length - 1, first.slopeOrder());
            int cnt = 1;
            double slope = Double.NaN;
            for (int j = 0; j < tmp.length - 1; j++) {
                if ((slope = first.slopeTo(tmp[j])) == first.slopeTo(tmp[j + 1])) {
                    cnt++;
                }else {
                    // in case sub-segments occur
                    avoidSub(cnt, first, tmp[j], slope, bst);
                    cnt = 1;
                }
            }
            // last element
            avoidSub(cnt, first, tmp[tmp.length - 1], slope, bst);
        }
        pairs = null;
    }

    /*
    * insert element into list of pairs,
    * outer search o(logN), insert worst case o(N), inner validate worst case o(N)
    * */
    private void avoidSub(int cnt, Point first, Point cur, double slope) {
        if (Double.isNaN(slope) || cnt < 3) {
            return;
        }
        if (insertPairs(slope, cur)) {
            list.add(new LineSegment(first, cur));
            segments++;
        }
    }

    /*
    * since efficiency of bst depends on height of tree
    * initial sort to find duplicate is unnecessary and stability of sorting alg no longer matters
    * outer search o(logN), insert o(logN), inner validate o(logN)
    * */
    private void avoidSub(int cnt, Point first, Point cur, double slope, BST<Double, BST<Point, Integer>> bst) {
        if (Double.isNaN(slope) || cnt < 3) {
            return;
        }
        BST<Point, Integer> helper;
        if ((helper = bst.get(slope)) == null) {
            helper = new BST<>();
            helper.put(cur, 0);
            bst.put(slope, helper);
        }else if (helper.get(cur) == null) {
            helper.put(cur, 0);
        }else {
            return;
        }
        list.add(new LineSegment(first, cur));
        segments++;
    }

    private boolean insertPairs(double slope, Point last) {
        int index = lessThanAndEqual(slope);
        if (index < 0 || pairs.get(index).slope != slope) {
            List<Point> cur = new ArrayList<>();
            cur.add(last);
            pairs.add(Math.max(index + 1, 0), new MyPair(slope, cur));
            return true;
        }
        List<Point> lastPoints = pairs.get(index).lastPoints;
        for (Point p : lastPoints) {
            if (p == last) {
                return false;
            }
        }
        lastPoints.add(last);
        return true;
    }

    // binary search index of list by slope o(logN)
    private int lessThanAndEqual(double slope) {
        int low = 0, hi = pairs.size() - 1;
        while (low <= hi) {
            int mid = low + (hi - low) / 2;
            double cur = pairs.get(mid).slope;
            int diff = Double.compare(slope, cur);
            if (diff < 0) {
                hi = mid - 1;
            }else if (diff > 0) {
                low = mid + 1;
            }else {
                return mid;
            }
        }
        return hi;
    }

    private void mergeSort(Point[] arr, int low, int hi, Comparator<Point> comparator) {
        if (low >= hi) {
            return;
        }
        int mid = low + (hi - low) / 2;
        mergeSort(arr, low , mid, comparator);
        mergeSort(arr, mid + 1, hi, comparator);
        Point[] tmp = new Point[hi - low + 1];
        int index = 0, i = low, j = mid + 1;
        while (i <= mid && j <= hi) {
            if (comparator.compare(arr[i], arr[j]) <= 0) {
                tmp[index] = arr[i++];
            }else {
                tmp[index] = arr[j++];
            }
            index++;
        }
        while (i <= mid) {
            tmp[index++] = arr[i++];
        }
        while (j <= hi) {
            tmp[index++] = arr[j++];
        }
        i = 0;
        while (i < index) {
            arr[i + low] = tmp[i++];
        }
    }

    // binary search
    private class MyPair {
        private final double slope;
        private final List<Point> lastPoints;

        public MyPair(double slope, List<Point> lastPoints) {
            this.slope = slope;
            this.lastPoints = lastPoints;
        }
    }

    // binary search tree
    private class BST<Key extends Comparable<? super Key>, Value> {
        private class MyNode {
            private final Key key;
            private Value value;
            private MyNode left, right;

            public MyNode(Key key, Value value) {
                this.key = key;
                this.value = value;
            }
        }

        private MyNode root;
        public BST() {
        }

        public void put(Key key, Value value) {
            assert value != null;
            root = put(root, key, value);
        }

        private MyNode put(MyNode node, Key key, Value value) {
            if (node == null) {
                return new MyNode(key, value);
            }
            int diff = key.compareTo(node.key);
            if (diff < 0) {
                node.left = put(node.left, key, value);
            }else if (diff > 0) {
                node.right = put(node.right, key, value);
            }else {
                node.value = value;
            }
            return node;
        }

        public Value get(Key key) {
            return get(root, key);
        }

        private Value get(MyNode node, Key key) {
            if (node == null) {
                return null;
            }
            int diff = key.compareTo(node.key);
            Value value;
            if (diff < 0) {
                value = get(node.left, key);
            }else if (diff > 0) {
                value = get(node.right, key);
            }else {
                value = node.value;
            }
            return value;
        }
    }

    // unit testing
    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // print and draw the line segments
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
        }
    }

}
