package wordnet;

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class SAP {
    private final Digraph G;

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        validate(G);
        // immutable
        this.G = new Digraph(G);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validate(v, w);
        validateVertex(v, w);
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int anc = searchAncestor(bfsV, bfsW);
        return anc == -1 ? -1 : bfsV.distTo(anc) + bfsW.distTo(anc);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validate(v, w);
        validateVertex(v, w);
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        return searchAncestor(bfsV, bfsW);
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (violateIterables(v, w)) {
            return -1;
        }
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        int anc = searchAncestor(bfsV, bfsW);
        return anc == -1 ? -1 : bfsV.distTo(anc) + bfsW.distTo(anc);
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (violateIterables(v, w)) {
            return -1;
        }
        BreadthFirstDirectedPaths bfsV = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths bfsW = new BreadthFirstDirectedPaths(G, w);
        return searchAncestor(bfsV, bfsW);
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }

    private int searchAncestor(BreadthFirstDirectedPaths bfsV, BreadthFirstDirectedPaths bfsW) {
        int anc = -1, len = Integer.MAX_VALUE;
        for (int i = 0; i < G.V(); i++) {
            if (bfsV.hasPathTo(i) && bfsW.hasPathTo(i)) {
                int cur = bfsV.distTo(i) + bfsW.distTo(i);
                if (len > cur) {
                    anc = i;
                    len = cur;
                }
            }
        }
        return anc;
    }

    private boolean violateIterables(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v, w);
        validateIterable(v);
        validateIterable(w);
        return isLengthOfIterableZero(v) || isLengthOfIterableZero(w);
    }

    private void validate(Object...o) {
        for (Object e : o) {
            if (e == null) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void validateVertex(int...vertices) {
        for (int vertex : vertices) {
            if (vertex < 0 || vertex >= G.V()) {
                throw new IllegalArgumentException();
            }
        }
    }

    private void validateIterable(Iterable<Integer> iterable) {
        for (Integer i : iterable) {
            if (i == null) {
                throw new IllegalArgumentException();
            }
            validateVertex(i);
        }
    }

    private boolean isLengthOfIterableZero(Iterable<Integer> iterable) {
        return !iterable.iterator().hasNext();
    }
}

