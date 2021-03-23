package boggle;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.HashSet;
import java.util.Set;


public class BoggleSolver {
    /*
    * hash with backtracking is not a good idea
    * cuz redundant branches cannot be pruned as soon as possible
    * while with trie, those unnecessary computes can be avoided
    * */
    private final TrieScore trieScore;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        validateNull(dictionary);
        trieScore = new TrieScore();
        buildTrieScore(dictionary);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        validateNull(board);
        Set<String> iterable = new HashSet<>();
        int m = board.rows(), n = board.cols();
        boolean[][] visited = new boolean[m][n];
        StringBuilder prefix = new StringBuilder();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                dfs(i, j, visited, board, m, n, iterable, prefix);
            }
        }
        return iterable;
    }

    private void dfs(int i, int j, boolean[][] visited, BoggleBoard board,
                     int m, int n, Set<String> valid, StringBuilder prefix) {
        if (i < 0 || i >= m || j < 0 || j >= n) {
            return;
        }
        if (visited[i][j]) {
            return;
        }
        char cur = board.getLetter(i, j);
        visited[i][j] = true;
        prefix.append(cur);
        boolean containsQ = cur == 'Q';
        if (containsQ) {
            prefix.append('U');
        }
        String curWord = prefix.toString();
        // if there is no such path in trie, then it is unnecessary to recurse
        if (!trieScore.containsPath(curWord)) {
            backtracking(containsQ, prefix, i, j, visited);
            return;
        }
        if (trieScore.get(curWord) > 0) {
            valid.add(curWord);
        }
        dfs(i + 1, j, visited, board, m, n, valid, prefix);
        dfs(i - 1, j, visited, board, m, n, valid, prefix);
        dfs(i, j + 1, visited, board, m, n, valid, prefix);
        dfs(i, j - 1, visited, board, m, n, valid, prefix);
        dfs(i + 1, j + 1, visited, board, m, n, valid, prefix);
        dfs(i + 1, j - 1, visited, board, m, n, valid, prefix);
        dfs(i - 1, j + 1, visited, board, m, n, valid, prefix);
        dfs(i - 1, j - 1, visited, board, m, n, valid, prefix);
        backtracking(containsQ, prefix, i, j, visited);
    }

    private void backtracking(boolean containsQ, StringBuilder prefix,
                              int i, int j, boolean[][] visited) {
        visited[i][j] = false;
        int deletable = containsQ ? 2 : 1;
        prefix.delete(prefix.length() - deletable, prefix.length());
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        validateNull(word);
        return trieScore.get(word);
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }

    private int score(int length) {
        assert length >= 3;
        switch (length) {
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }


    private void buildTrieScore(String[] dictionary) {
        for (String word : dictionary) {
            validateNull(word);
            int length = word.length();
            if (length < 3) {
                continue;
            }
            trieScore.put(word, score(length));
        }
    }

    private void validateNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException();
        }
    }

    private class TrieScore {
        private static final int R = 26;

        private Node root;

        private class Node {
            private int score;
            private Node[] next = new Node[R];
        }

        public TrieScore() {

        }

        public boolean containsPath(String prefix) {
            return get(root, prefix, 0) != null;
        }

        public void put(String key, int score) {
            root = put(root, key, score, 0);
        }

        private Node put(Node node, String key, int score, int d) {
            if (node == null) {
                node = new Node();
            }
            if (key.length() == d) {
                node.score = score;
                return node;
            }
            char cur = key.charAt(d);
            node.next[cur - 'A'] = put(node.next[cur - 'A'], key, score, d + 1);
            return node;
        }

        public int get(String key) {
            Node node = get(root, key, 0);
            if (node == null) {
                return 0;
            } else {
                return node.score;
            }
        }

        private Node get(Node node, String key, int d) {
            if (node == null) {
                return null;
            }
            if (key.length() == d) {
                return node;
            }
            char cur = key.charAt(d);
            return get(node.next[cur - 'A'], key, d + 1);
        }
    }

}
