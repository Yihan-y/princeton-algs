package uf;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    private final int n;
    private int numOfOpenSites;
    private final int top;
    private final int bottom;
    private final boolean[][] openOrClose;
    private final WeightedQuickUnionUF ufModel;
    // rows above virtual bottom are all considered full even if only one of them is connected to the virtual top
    // to fix it, create a new ufModel to keep track of sites without applying virtual bottom
    private final WeightedQuickUnionUF ufModelWithoutBottom;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.numOfOpenSites = 0;
        this.top = 0;
        this.bottom = n * n + 1;
        this.openOrClose = new boolean[n + 1][n + 1];
        this.ufModel = new WeightedQuickUnionUF( bottom + 1);
        this.ufModelWithoutBottom = new WeightedQuickUnionUF(bottom);
        connectTopAndBottom();
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);
        if (openOrClose[row][col]) {
            return;
        }
        openOrClose[row][col] = true;
        numOfOpenSites++;
        int curIndex = getIndex(row, col);
        connectNearby(curIndex, row, col + 1);
        connectNearby(curIndex, row, col - 1);
        connectNearby(curIndex, row + 1, col);
        connectNearby(curIndex, row - 1, col);
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return openOrClose[row][col];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        if (!isOpen(row, col)) {
            return false;
        }
        return ufModelWithoutBottom.find(top) == ufModelWithoutBottom.find(getIndex(row, col));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return numOfOpenSites;
    }

    // does the system percolate?
    public boolean percolates() {
        if (n == 1) {
            return openOrClose[1][1];
        }
        return ufModel.find(top) == ufModel.find(bottom);
    }

    // test client (optional)
//    public static void main(String[] args) {
//
//
//    }

    private void connectTopAndBottom() {
        for (int i = 1; i <= n; i++) {
            ufModel.union(top, i);
            ufModelWithoutBottom.union(top, i);
        }
        for (int i = n * n; i > n * (n - 1); i--) {
            ufModel.union(bottom, i);
        }
    }

    private void validate(int...args) {
        for (int arg : args) {
            if (arg < 1 || arg > n) {
                throw new IllegalArgumentException();
            }
        }
    }

    private int getIndex(int row, int col) {
        return (row - 1) * n + col;
    }

    private void connectNearby(int curIndex, int row, int col) {
        try {
            validate(row, col);
        } catch (IllegalArgumentException e) {
            return;
        }
        if (openOrClose[row][col]) {
            ufModel.union(curIndex, getIndex(row, col));
            ufModelWithoutBottom.union(curIndex, getIndex(row, col));
        }
    }
}
