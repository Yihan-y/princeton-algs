package kdtree;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.List;

public class KdTree {
    private MyNode root;
    private int size;

    // construct an empty set of points
    public KdTree() {
        size = 0;
    }

    // is the set empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points in the set
    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        validate(p);
        root = insert(root, p, 0, 0, 0, 1, 1);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        validate(p);
        return get(root, p, 0) != null;
    }

    // draw all points to standard draw
    public void draw() {
        dfsAndDraw(root);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        validate(rect);
        List<Point2D> iterable = new ArrayList<>();
        searchRange(root, rect, iterable);
        return iterable;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        validate(p);
        if (isEmpty()) {
            return null;
        }
        MyNode nearestNode = searchNearest(root, p, 0, root);
        return nearestNode.p;
    }

    private void validate(Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException();
        }
    }

    private MyNode get(MyNode node, Point2D p, int layer) {
        if (node == null) {
            return null;
        }
        if (p.equals(node.p)) {
            return node;
        }
        double diff = (layer & 1) == 0 ? p.x() - node.p.x() : p.y() - node.p.y();
        if (diff < 0) {
            return get(node.left, p, layer + 1);
        }else {
            return get(node.right, p, layer + 1);
        }
    }

    private MyNode insert(MyNode node, Point2D p, int layer,
                          double xmin, double ymin, double xmax, double ymax) {
        if (node == null) {
            size++;
            return new MyNode(p, xmin, ymin, xmax, ymax);
        }
        // check duplicate
        if (p.equals(node.p)) {
            return node;
        }
        double diff;
        if ((layer & 1) == 0) {
            diff = p.x() - node.p.x();
            if (diff < 0) {
                xmax = node.p.x();
            }else {
                xmin = node.p.x();
            }
        }else {
            diff = p.y() - node.p.y();
            if (diff < 0) {
                ymax = node.p.y();
            }else {
                ymin = node.p.y();
            }
        }
        if (diff < 0) {
            node.left = insert(node.left, p, layer + 1, xmin, ymin, xmax, ymax);
        }else {
            node.right = insert(node.right, p, layer + 1, xmin, ymin, xmax, ymax);
        }
        return node;
    }

    private void dfsAndDraw(MyNode node) {
        if (node == null) {
            return;
        }
        node.p.draw();
        dfsAndDraw(node.left);
        dfsAndDraw(node.right);
    }

    private void searchRange(MyNode node, RectHV rectHV, List<Point2D> iterable) {
        if (node == null || !rectHV.intersects(node.rect)) {
            return;
        }
        if (rectHV.contains(node.p)) {
            iterable.add(node.p);
        }
        searchRange(node.left, rectHV, iterable);
        searchRange(node.right, rectHV, iterable);
    }

    private MyNode searchNearest(MyNode node, Point2D p, int layer, MyNode nearest) {
        double dist;
        if (node == null || (dist = p.distanceTo(nearest.p)) < node.rect.distanceTo(p)) {
            return nearest;
        }
        if (p.distanceTo(node.p) < dist) {
            nearest = node;
        }
        double diff = (layer & 1) == 0 ? p.x() - node.p.x() : p.y() - node.p.y();
        if (diff < 0) {
            nearest = searchNearest(node.left, p, layer + 1, nearest);
            nearest = searchNearest(node.right, p, layer + 1, nearest);
        }else {
            nearest = searchNearest(node.right, p, layer + 1, nearest);
            nearest = searchNearest(node.left, p, layer + 1, nearest);
        }
        return nearest;
    }


    private class MyNode {
        private Point2D p;
        private RectHV rect;
        private MyNode left, right;

        public MyNode(Point2D p, double xmin, double ymin, double xmax, double ymax) {
            this.p = p;
            rect = new RectHV(xmin, ymin, xmax, ymax);
        }
    }
}
