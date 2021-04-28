import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class WordNet {
    private HashMap<String, ArrayList<Integer>> nounsHash
            = new HashMap<String, ArrayList<Integer>>();
    // store all pairs of nouns and iterable ID
    private final String[] words; // store the nodes of nouns
    private final String[] syns;
    private Digraph wordMap; // store the graph of wordnet
    private int V;
    private final SAP sap;
    private int[] marked;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("synsets or hypernums is null!");
        }
        In inWord = new In(synsets);
        ArrayList<String> words0 = new ArrayList<String>();
        ArrayList<String> syns0 = new ArrayList<String>();
        ArrayList<Integer> ws;
        while (inWord.hasNextLine()) {
            V++;
            String[] sequence = inWord.readLine().split(",");
            int ID = Integer.parseInt(sequence[0]);
            String[] syn = sequence[1].split(" ");
            syns0.add(sequence[1]);
            for (String word : syn) {
                if (nounsHash.containsKey(word))
                    ws = nounsHash.get(word);
                else {
                    ws = new ArrayList<Integer>();
                    words0.add(word);
                }
                ws.add(ID);
                nounsHash.put(word, ws);
            }
        }
        words = words0.toArray(new String[0]);
        syns = syns0.toArray(new String[0]);
        wordMap = new Digraph(V);
        In inAdjcences = new In(hypernyms);
        while (inAdjcences.hasNextLine()) {
            String[] adj = inAdjcences.readLine().split(","); // read the hypernyms
            for (int i = 1; i < adj.length; i++)
                wordMap.addEdge(Integer.parseInt(adj[0]), Integer.parseInt(adj[i]));
        }
        // check whether the wordmap is a rooted DAG
        // marked status: 0=untouched ; 1=on the path; -1=all its adjacencies have been checked
        marked = new int[V];
        int j = 0; // the root number: root is the vertex that has none outdegree
        for (int v = 0; v < V; v++) {
            if (wordMap.outdegree(v) == 0)
                j++;
            if (marked[v] == 0) {
                marked[v] = 1;
                for (int i : wordMap.adj(v)) {
                    dFS(i);
                }
                marked[v] = -1;
            }
        }
        // check whether there is just one root?
        if (j > 1) throw new IllegalArgumentException("there are " + j + " roots!");
        // initialize the sap
        sap = new SAP(wordMap);
    }


    private void dFS(int v) {
        if (marked[v] == 1) {
            throw new IllegalArgumentException("there is directed circle in the Digraph!");
        }
        else if (marked[v] == -1) return; // the following part has been visited
        marked[v] = 1;
        for (int i : wordMap.adj(v)) {
            dFS(i);
        }
        marked[v] = -1;
    }


    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return Arrays.asList(words);
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null)
            throw new IllegalArgumentException("input argument is null!");
        return (nounsHash.containsKey(word));
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("the input argument is null!");
        if (!isNoun(nounA)) {
            throw new IllegalArgumentException(nounA + " is not a noun in word net!");
        }
        if (!isNoun(nounB)) {
            throw new IllegalArgumentException(nounB + " is not a noun in word net!");
        }
        ArrayList<Integer> nounAID = nounsHash.get(nounA);
        ArrayList<Integer> nounBID = nounsHash.get(nounB);
        return sap.length(nounAID, nounBID);

    }


    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("the input argument is null!");
        if (!isNoun(nounA)) {
            throw new IllegalArgumentException(nounA + " is not a noun in word net!");
        }
        if (!isNoun(nounB)) {
            throw new IllegalArgumentException(nounB + " is not a noun in word net!");
        }
        ArrayList<Integer> nounAID = nounsHash.get(nounA);
        ArrayList<Integer> nounBID = nounsHash.get(nounB);
        return syns[sap.ancestor(nounAID, nounBID)];
    }


    // do unit testing of this class

    public static void main(String[] args) {

        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println(wordnet.distance(args[2], args[3]));
        System.out.println(wordnet.sap(args[2], args[3]));
        // System.out.println(wordnet.words[1]);
        // String[] wds = wordnet.words;
        // System.out.println(wordnet.sap(wds[1], wds[4]));

    }
}
