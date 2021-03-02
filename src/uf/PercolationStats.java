package uf;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONFIDENCE = 1.96D;

    private final int n;
    private final int trials;
    private final double[] stats;
    private double mean;
    private double stddev;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        this.trials = trials;
        this.stats = new double[trials];
    }

    // sample mean of percolation threshold
    public double mean() {
        return mean;
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return stddev;
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean - CONFIDENCE * stddev / Math.sqrt(trials);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean + CONFIDENCE * stddev / Math.sqrt(trials);
    }

    // test client (see below)
    public static void main(String[] args) {
        int[] params = PercolationStats.validate(args);
        PercolationStats stats = new PercolationStats(params[0], params[1]);
        stats.start();
        StdOut.println("mean = " + stats.mean());
        StdOut.println("stddev = " + stats.stddev());
        StdOut.println("95% confidence interval = [" + stats.confidenceLo() +
                ", " + stats.confidenceHi() + "]");
    }

    private void start() {
        for (int i = 0; i < trials; i++) {
            Percolation percolation = new Percolation(n);
            while (!percolation.percolates()) {
                percolation.open(StdRandom.uniform(n) + 1, StdRandom.uniform(n) + 1);
            }
            stats[i] = percolation.numberOfOpenSites() / (n * n * 1D);
        }
        this.mean = StdStats.mean(stats);
        this.stddev = StdStats.stddev(stats);
    }

    private static int[] validate(String[] args) {
        if (args.length != 2) {
            throw new IllegalArgumentException();
        }
        int[] res = new int[args.length];
        int index = 0;
        for (String s : args) {
            res[index++] = Integer.parseInt(s);
        }
        return res;
    }
}
