package puzzle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private static final boolean MANHATTAN = true;
    private final MinPQ<MyBoard> gameTree;
    private MyBoard last;
    private int moves;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        validate(initial);
        gameTree = new MinPQ<>();
        gameTree.insert(new MyBoard(initial, null, 0, MANHATTAN));
        moves = -1;
        solve();
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return moves != -1;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        }
        Stack<Board> iterable = new Stack<>();
        while (last != null) {
            iterable.push(last.board);
            last = last.prev;
        }
        return iterable;
    }

    private void validate(Board board) {
        if (board == null) {
            throw new IllegalArgumentException();
        }
    }

    private int hammingPriority(Board board, int moves) {
        return board.hamming() + moves;
    }

    private int manhattanPriority(Board board, int moves) {
        return board.manhattan() + moves;
    }

    private boolean solve() {
        Board twin = gameTree.min().board.twin();
        MinPQ<MyBoard> twinTree = new MinPQ<>();
        twinTree.insert(new MyBoard(twin, null, 0, MANHATTAN));
        MyBoard peek, twinPeek = null;
        assert !gameTree.isEmpty();
        while (!(peek = gameTree.delMin()).board.isGoal() && !(twinPeek = twinTree.delMin()).board.isGoal()) {
            for (Board b : peek.board.neighbors()) {
                if (peek.prev != null && b == peek.prev.board) {
                    continue;
                }
                gameTree.insert(new MyBoard(b, peek, peek.moves + 1, MANHATTAN));
            }
            for (Board b : twinPeek.board.neighbors()) {
                if (twinPeek.prev != null && b == twinPeek.prev.board) {
                    continue;
                }
                twinTree.insert(new MyBoard(b, twinPeek, twinPeek.moves + 1, MANHATTAN));
            }
        }
        assert twinPeek != null;
        if (twinPeek.board.isGoal()) {
            return false;
        }
        last = peek;
        this.moves = peek.moves;
        return true;
    }

    private class MyBoard implements Comparable<MyBoard> {
        private Board board;
        private MyBoard prev;
        private int priority;
        private int moves;

        public MyBoard(Board board, MyBoard prev, int moves, boolean manhattan) {
            this.board = board;
            this.prev = prev;
            this.moves = moves;
            priority = manhattan ? manhattanPriority(board, moves) : hammingPriority(board, moves);
        }

        @Override
        public int compareTo(MyBoard o) {
            int diff = this.priority - o.priority;
            return Integer.compare(diff, 0);
        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}
