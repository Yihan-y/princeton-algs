package seam;

import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
    private Picture picture;
    private double[][] energy;
    private static final double DEFAULT_GRADIENT = 1000D;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        validateNull(picture);
        this.picture = new Picture(picture);
        buildEnergy();
    }

    // current picture
    public Picture picture() {
        return picture;
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validateRange(x, y);
        double res = DEFAULT_GRADIENT;
        if (!violateRange(x + 1, y) && !violateRange(x - 1, y)
                && !violateRange(x, y + 1) && !violateRange(x, y - 1)) {
            res = Math.sqrt(calculateGradient(x, y));
        }
        return res;
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        double[][] horBasedEnergy = new double[height()][width()];
        for (int i = 0; i < height(); i++) {
            for (int j = 0; j < width(); j++) {
                energy[i][j] = energy(j, i);
            }
        }
        return findSP(horBasedEnergy);
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        double[][] verBasedEnergy = energy;
        return findSP(verBasedEnergy);
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, height());
        rebuild(seam, width(), height() - 1);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, width());
        rebuild(seam, width() - 1, height());
    }

    //  unit testing (optional)
    public static void main(String[] args) {

    }

    private int[] findSP(double[][] energy) {
        int x = energy.length, y = energy[0].length;
        int[] res = new int[x];
        double[][] distTo = new double[x][y];
        Arrays.fill(distTo[0], DEFAULT_GRADIENT);
        for (int i = 1; i < x; i++) {
            Arrays.fill(distTo[i], Double.POSITIVE_INFINITY);
        }
        int[][] edgeTo = new int[x][y];
        for (int i = 0; i < x - 1; i++) {
            for (int j = 0; j < y; j++) {
                relax(energy, distTo, edgeTo, i, j);
            }
        }
        int minIndex = 0, index = x - 1;
        for (int j = 0; j < y; j++) {
            if (distTo[x - 1][j] < distTo[x - 1][minIndex]) {
                minIndex = j;
            }
        }
        res[index] = minIndex;
        while (index-- > 0) {
            res[index] = edgeTo[index][minIndex];
            minIndex = res[index];
        }
        return res;
    }

    private void rebuild(int[] seam, int x, int y) {
        Picture picture = new Picture(x, y);
        if (x == width()) {
            for (int i = 0; i < seam.length; i++) {
                for (int j = 0; j < seam[i]; j++) {
                    picture.setRGB(i, j, this.picture.getRGB(i, j));
                }
                for (int j = seam[i] + 1; j < y + 1; j++) {
                    picture.setRGB(i, j - 1, this.picture.getRGB(i, j));
                }
            }
        }else {
            for (int j = 0; j < seam.length; j++) {
                for (int i = 0; i < seam[j]; i++) {
                    picture.setRGB(i, j, this.picture.getRGB(i, j));
                }
                for (int i = seam[j] + 1; i < x + 1; i++) {
                    picture.setRGB(i - 1, j, this.picture.getRGB(i, j));
                }
            }
        }
        this.picture = picture;
        buildEnergy();
    }

    private void buildEnergy() {
        energy = new double[width()][height()];
        for (int i = 0; i < width();i ++) {
            for (int j = 0; j < height(); j++) {
                energy[i][j] = energy(i, j);
            }
        }
    }

    private void relax(double[][] energy, double[][] distTo, int[][] edgeTo, int i, int j) {
        assert i < energy.length - 1;
        double cur;
        if ((cur = energy[i + 1][j] + distTo[i][j]) < distTo[i + 1][j]) {
            distTo[i + 1][j] = cur;
            edgeTo[i + 1][j] = j;
        }
        if (j >= 1 && (cur = energy[i + 1][j - 1] + distTo[i][j]) < distTo[i + 1][j - 1]) {
            distTo[i + 1][j - 1] = cur;
            edgeTo[i + 1][j - 1] = j;
        }
        if (j < energy.length - 1 && (cur = energy[i + 1][j + 1] + distTo[i][j]) < distTo[i + 1][j + 1]) {
            distTo[i + 1][j + 1] = cur;
            edgeTo[i + 1][j + 1] = j;
        }
    }

    private double calculateGradient(int x, int y) {
        Color x1 = picture.get(x - 1, y), x2 = picture.get(x + 1, y);
        Color y1 = picture.get(x, y - 1), y2 = picture.get(x, y + 1);
        double gradientX = Math.pow(x1.getRed() - x2.getRed(), 2) + Math.pow(x1.getGreen() - x2.getGreen(), 2) + Math.pow(x1.getBlue() - x2.getBlue(), 2);
        double gradientY = Math.pow(y1.getRed() - y2.getRed(), 2) + Math.pow(y1.getGreen() - y2.getGreen(), 2) + Math.pow(y1.getBlue() - y2.getBlue(), 2);
        return gradientX + gradientY;
    }

    private void validateNull(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
    }

    private void validateRange(int x, int y) {
        if (violateRange(x, y)) {
            throw new IllegalArgumentException();
        }
    }

    private void validateSeam(int[] seam, int size) {
        if (size <= 1 || seam.length != size) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < size - 1; i++) {
            if (Math.abs(seam[i] - seam[i + 1]) > 1) {
                throw new IllegalArgumentException();
            }
        }
    }

    private boolean violateRange(int x, int y) {
        return x < 0 || x >= width() || y < 0 || y >= height();
    }

}
