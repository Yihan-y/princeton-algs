package wordnet;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class Outcast {
    private final WordNet wordNet;

    // constructor takes a wordnet.WordNet object
    public Outcast(WordNet wordnet) {
        validate(wordnet);
        this.wordNet = wordnet;
    }

    // given an array of wordnet.WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        validate(nouns);
        int n = nouns.length, max = -n;
        if (n == 0) {
            throw new IllegalArgumentException();
        }
        int[][] dist = new int[n][n];
        for (int i = 0; i < n; i++) {
            Arrays.fill(dist[i], -2);
        }
        String res = "";
        for (int i = 0; i < n; i++) {
            int cur = 0;
            for (int j = 0; j < n; j++) {
                dist[i][j] = dist[j][i] == -2 ? wordNet.distance(nouns[i], nouns[j]) : dist[j][i];
                cur += dist[i][j];
            }
            if (cur > max) {
                max = cur;
                res = nouns[i];
            }
        }
        return res;
    }

    private void validate(Object o) {
        if (o == null) {
            throw new IllegalArgumentException();
        }
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
