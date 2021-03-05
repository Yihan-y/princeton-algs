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
    private List<Point> lastPoints;

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        this.points = points.clone();
        validateAndSort();
        segments = 0;
        list = new ArrayList<>();
        lastPoints = new ArrayList<>();
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

    private void fastSearch() {
        // maintain the initial sequent order, that is, stability (by point itself)
        // setProperty is forbidden
//        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        int n = points.length;
        for (int i = 0; i < n - 3; i++) {
            Point first = points[i];
            Point[] tmp = Arrays.copyOfRange(points, i + 1, n);
//            Arrays.sort(tmp, first.slopeOrder());
            mergeSort(tmp, 0, tmp.length - 1, first.slopeOrder());
            int cnt = 1;
            for (int j = 0; j < tmp.length - 1; j++) {
                if (first.slopeTo(tmp[j]) == first.slopeTo(tmp[j + 1])) {
                    cnt++;
                }else {
                    // in case sub-segments occur
                    avoidSub(cnt, first, tmp[j]);
                    cnt = 1;
                }
            }
            // last element
            avoidSub(cnt, first, tmp[tmp.length - 1]);
        }
        lastPoints = null;
    }

    private void avoidSub(int cnt, Point first, Point cur) {
        if (cnt >= 3 &&
                (lastPoints.isEmpty() || lastPoints.get(lastPoints.size() - 1) != cur)) {
            lastPoints.add(cur);
            list.add(new LineSegment(first, cur));
            segments++;
        }
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
