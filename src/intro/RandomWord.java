package intro;

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args) {
        String res = "";
        double i = 0D;
        while (!StdIn.isEmpty()) {
            String next = StdIn.readString();
            if (StdRandom.bernoulli(1 / ++i)) {
                res = next;
            }
        }
        StdOut.println(res);
    }
}
