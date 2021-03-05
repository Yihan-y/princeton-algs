package collinear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BruteCollinearPoints {
    private final Point[] points;
    private int segments;
    private final List<LineSegment> list;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        this.points = points.clone();
        validateAndSort();
        segments = 0;
        list = new ArrayList<>();
        bruteSearch();
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

    private void bruteSearch() {
        int n = points.length;
        for (int a = 0; a < n - 3; a++) {
            Point first = points[a];
            for (int b = a + 1; b < n - 2; b++) {
                for (int c = b + 1; c < n - 1; c++) {
                    for (int d = c + 1; d < n; d++) {
                        if (first.slopeTo(points[b]) == first.slopeTo(points[c])
                        && first.slopeTo(points[b]) == first.slopeTo(points[d])) {
                            list.add(new LineSegment(first, points[d]));
                            segments++;
                        }
                    }
                }
            }
        }
    }

    // unit testing
//    public static void main(String[] args) {
//        // read the n points from a file
//        In in = new In(args[0]);
//        int n = in.readInt();
//        collinear.Point[] points = new collinear.Point[n];
//        for (int i = 0; i < n; i++) {
//            int x = in.readInt();
//            int y = in.readInt();
//            points[i] = new collinear.Point(x, y);
//        }
//
//        // print and draw the line segments
//        collinear.BruteCollinearPoints collinear = new collinear.BruteCollinearPoints(points);
//        for (LineSegment segment : collinear.segments()) {
//            StdOut.println(segment);
//        }
//    }

}
