package puzzle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {
    private static final boolean MANHATTAN = true;
    // to make Solver immutable
    private final Stack<Board> iterable;
    private MyBoard root;
    private int moves;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        validate(initial);
        moves = -1;
        solve(initial);
        iterable = initIterable();
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
    
    private MinPQ<MyBoard> initMinPQ(Board board) {
        MinPQ<MyBoard> gameTree = new MinPQ<>();
        gameTree.insert(new MyBoard(board, null, 0, MANHATTAN));
        return gameTree;
    }

    private Stack<Board> initIterable() {
        if (!isSolvable()) {
            return null;
        }
        Stack<Board> iterable = new Stack<>();
        while (root != null) {
            iterable.push(root.board);
            root = root.prev;
        }
        return iterable;
    }

    /*
     * (i) those that lead to the goal board and
     * (ii) those that lead to the goal board if we modify the initial board by swapping any pair of blocks (the blank square is not a block).
     * if root turns out unsolvable, swapped twin can be solved
     * */
    private boolean solve(Board initial) {
        MinPQ<MyBoard> gameTree = initMinPQ(initial);
        MinPQ<MyBoard> twinTree = initMinPQ(initial.twin());
        MyBoard root, twinRoot = null;
        assert !gameTree.isEmpty() && !twinTree.isEmpty();
        while (!(root = gameTree.delMin()).board.isGoal() && !(twinRoot = twinTree.delMin()).board.isGoal()) {
            for (Board b : root.board.neighbors()) {
                if (root.prev != null && b.equals(root.prev.board)) {
                    continue;
                }
                gameTree.insert(new MyBoard(b, root, root.moves + 1, MANHATTAN));
            }
            for (Board b : twinRoot.board.neighbors()) {
                if (twinRoot.prev != null && b.equals(twinRoot.prev.board)) {
                    continue;
                }
                twinTree.insert(new MyBoard(b, twinRoot, twinRoot.moves + 1, MANHATTAN));
            }
        }
        if (twinRoot != null && twinRoot.board.isGoal()) {
            return false;
        }
        this.root = root;
        this.moves = root.moves;
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
