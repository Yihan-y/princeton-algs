package puzzle;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private final int[][] tiles;
    private final int n;
    private final List<Board> iterable;
    private int blankX, blankY;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        this.tiles = copyArray(tiles);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    blankX = i;
                    blankY = j;
                    break;
                }
            }
        }
        iterable = initIterable();
    }

    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n).append("\n");
        for (int[] cur : tiles) {
            sb.append(" ");
            for (int i : cur) {
                sb.append(i).append("  ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return n;
    }

    // number of tiles out of place
    public int hamming() {
        int res = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((i * n + j + 1) != tiles[i][j]) {
                    res++;
                }
            }
        }
        return res - 1;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int res = 0;
        int cur;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if ((cur = tiles[i][j]) == 0) {
                    continue;
                }
                int x = (cur - 1) / n;
                int y = (cur - 1) % n;
                res += Math.abs(x - i) + Math.abs(y - j);
            }
        }
        return res;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        if (!(y instanceof Board)) {
            return false;
        }
        Board o = (Board) y;
        return tilesEqual(this.tiles, o.tiles);
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return iterable;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] clone = copyArray(tiles);
        int blank = blankX * n + blankY;
//        int random1 = -1, random2 = -1;
//        while (random1 == random2 || random1 == blank || random2 == blank) {
//            random1 = StdRandom.uniform(n * n);
//            random2 = StdRandom.uniform(n * n);
//        }
//        int a, b;
//        int x, y;
//        clone[(a = random1 / n)][(b = random1 % n)] = clone[(x = random2 / n)][(y = random2 % n)];
//        clone[x][y] = tiles[a][b];
//        return new Board(clone);
        /*
        * check that Board is immutable by testing whether methods
        * return the same value, regardless of order in which called
        * since immutable, random is forbidden
        * */
        for (int i = 0; i < n * n - 1; i++) {
            if (i != blank && i + 1 != blank) {
                clone[i / n][i % n] = clone[(i + 1) / n][(i + 1) % n];
                clone[(i + 1) / n][(i + 1) % n] = tiles[i / n][i % n];
                break;
            }
        }
        return new Board(clone);
    }

    private List<Board> initIterable() {
        List<Board> iterable = new ArrayList<>();
        insertNeighbor(blankX + 1, blankY, iterable);
        insertNeighbor(blankX - 1, blankY, iterable);
        insertNeighbor(blankX, blankY + 1, iterable);
        insertNeighbor(blankX, blankY - 1, iterable);
        return iterable;
    }

    private int[][] copyArray(int[][] tiles) {
        int n = tiles.length;
        int[][] clone = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tiles[i], 0, clone[i], 0, n);
        }
        return clone;
    }

    private boolean insertNeighbor(int x, int y, List<Board> iterable) {
        if (x < 0 || x > n - 1 || y < 0 || y > n - 1) {
            return false;
        }
        int[][] cur = copyArray(tiles);
        cur[blankX][blankY] = cur[x][y];
        cur[x][y] = 0;
        return iterable.add(new Board(cur));
    }

    private void validate(int[][] t) {
        if (t == null || t.length == 0 || t.length != t[0].length) {
            throw new IllegalArgumentException();
        }
    }

    private boolean tilesEqual(int[][] t1, int[][] t2) {
        validate(t1);
        validate(t2);
        if (t1 == t2) {
            return true;
        }
        int l1 = t1.length, l2 = t2.length;
        if (l1 != l2) {
            return false;
        }
        for (int i = 0; i < l1; i++) {
            for (int j = 0; j < l1; j++) {
                if (t1[i][j] != t2[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    // unit testing (not graded)
//    public static void main(String[] args) {
//        int[][] test = new int[3][3];
//        test[0] = new int[] {8, 1, 3};
//        test[1] = new int[] {4, 0, 2};
//        test[2] = new int[] {7, 6, 5};
//        Board board = new Board(test);
//        StdOut.println("input board: " + board.toString());
//        StdOut.println("dimension: " + board.dimension());
//        StdOut.println("hamming: " + board.hamming());
//        StdOut.println("manhattan: " + board.manhattan());
//        StdOut.println("neighbors: ");
//        for (Board b : board.neighbors()) {
//            StdOut.println(b.toString());
//        }
//        StdOut.println("random twin: " + board.twin().toString());
//    }

}
