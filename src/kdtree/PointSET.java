package kdtree;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public class PointSET {
    private final TreeSet<Point2D> set;

    // construct an empty set of points
    public PointSET() {
        set = new TreeSet<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return set.isEmpty();
    }

    // number of points in the set
    public int size() {
        return set.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        validate(p);
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        validate(p);
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : set) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);
        List<Point2D> iterable = new ArrayList<>();
        for (Point2D p : set) {
            if (rect.contains(p)) {
                iterable.add(p);
            }
        }
        return iterable;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        validate(p);
        double dist = 1.5D, tmp;
        Point2D res = null;
        for (Point2D cur : set) {
            if ((tmp = p.distanceTo(cur)) < dist) {
                res = cur;
                dist = tmp;
            }
        }
        return res;
    }

    private void validate(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException();
        }
    }

    // unit testing of the methods (optional)
//    public static void main(String[] args) {
//
//    }
}
