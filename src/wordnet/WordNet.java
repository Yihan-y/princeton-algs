package wordnet;

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;

public class WordNet {
    private final Digraph digraph;
    private final SAP sap;

    private final String[] synsets;
    private final int numOfId;
    private final Map<String, Bag<Integer>> noun2Ids;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        validate(synsets, hypernyms);
        In in = new In(synsets);
        this.synsets = in.readAllLines();
        numOfId = this.synsets.length;
        digraph = new Digraph(numOfId);
        initDigraph(hypernyms);
        sap = new SAP(digraph);
        // least size of nounToIds is the number of id (one noun each line)
        // if proceeded, hashmap resizes itself
        noun2Ids = new HashMap<>((int) (numOfId / 0.75 + 1));
        initDictionary();
    }

    // returns all wordnet.WordNet nouns
    public Iterable<String> nouns() {
        return noun2Ids.keySet();
    }

    // is the word a wordnet.WordNet noun?
    public boolean isNoun(String word) {
        validate(word);
        return noun2Ids.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        return sap.length(noun2Ids.get(nounA), noun2Ids.get(nounB));
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException();
        }
        int anc = sap.ancestor(noun2Ids.get(nounA), noun2Ids.get(nounB));
        return synsets[anc].split(",")[1];
    }

    // do unit testing of this class
//    public static void main(String[] args) {
//
//    }

    private void initDictionary() {
        for (int i = 0; i < numOfId; i++) {
            String[] tmp = synsets[i].split(",");
            for (String noun : tmp[1].split(" ")) {
                Bag<Integer> idBags = noun2Ids.getOrDefault(noun, new Bag<>());
                idBags.add(i);
                noun2Ids.put(noun, idBags);
            }
        }
    }

    private void initDigraph(String hypernyms) {
        In in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] tmp = in.readLine().split(",");
            int v = Integer.parseInt(tmp[0]);
            for (int i = 1; i < tmp.length; i++) {
                digraph.addEdge(v, Integer.parseInt(tmp[i]));
            }
        }
        // validate rooted DAG
        int num = 0;
        for (int i = 0; i < digraph.V(); i++) {
            if (digraph.outdegree(i) == 0 && ++num > 1) {
                throw new IllegalArgumentException();
            }
        }
        DirectedCycle directedCycle = new DirectedCycle(digraph);
        if (directedCycle.hasCycle()) {
            throw new IllegalArgumentException();
        }
    }

    private void validate(Object...o) {
        for (Object e : o) {
            if (e == null) {
                throw new IllegalArgumentException();
            }
        }
    }
}
