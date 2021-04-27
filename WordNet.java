import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.RedBlackBST;

import java.util.ArrayList;
import java.util.Arrays;


public class WordNet {
    private RedBlackBST<String, Integer> wordsBST = new RedBlackBST<String, Integer>();
    private String[] words; // store the nodes of synsets
    private Digraph wordMap; // store the graph of wordnet
    private int V;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new IllegalArgumentException("synsets or hypernums is null!");
        }
        In inWord = new In(synsets);
        ArrayList<String> words0 = new ArrayList<String>();
        while (inWord.hasNextLine()) {
            String[] w = inWord.readLine().split(",");
            wordsBST.put(w[1], Integer.parseInt(w[0]));
            words0.add(Integer.parseInt(w[0]), w[1]);
        }
        words = words0.toArray(new String[words0.size()]);
        V = words.length;
        wordMap = new Digraph(words.length);
        In inAdjcences = new In(hypernyms);
        while (inAdjcences.hasNextLine()) {
            String[] adj = inAdjcences.readLine().split(","); // read the hypernyms
            for (int i = 1; i < adj.length; i++)
                wordMap.addEdge(Integer.parseInt(adj[0]), Integer.parseInt(adj[i]));
        }
        DepthFirstSearch dF = new DepthFirstSearch(wordMap);
        dF.isRootedDAG();
    }


    private class DepthFirstSearch { // to check whether the digraph is rooted DAG
        private int[] marked;
        // 0 is unvisited, 1 is visited in the current path, -1 is visited before the current path
        private Digraph digraph;
        private Digraph reDigraph;
        private int root;

        public DepthFirstSearch(Digraph digraph) {

            marked = new int[V];
            this.digraph = digraph;
            reDigraph = digraph.reverse();
        }

        private void isRootedDAG() {
            root = 0;
            while (digraph.outdegree(root) != 0) {
                for (int i : digraph.adj(root)) {
                    if (marked[i] == 0) {
                        marked[i] = 1;
                        root = i;
                        break;
                    }
                    else {
                        throw new IllegalArgumentException("there is at least one circle!");
                    }
                }
            }
            marked = new int[V];
            dFS(root);
            for (int i = 0; i < digraph.V(); i++) {
                if (marked[i] == 0) {
                    throw new IllegalArgumentException(
                            "there are at least two roots in the Digraph!");
                }
            }
        }

        private void dFS(int v) {
            validateVertex(v);
            if (marked[v] == 1) {
                throw new IllegalArgumentException("there is directed circle in the Digraph!");
            }
            else if (marked[v] == -1) return; // the following part has been visited
            marked[v] = 1;
            for (int i : reDigraph.adj(v)) {
                dFS(i);
            }
            marked[v] = -1;
        }
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
        return wordsBST.contains(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("the input argument is null!");
        ArrayList<Integer> nounAID = new ArrayList<Integer>();
        ArrayList<Integer> nounBID = new ArrayList<Integer>();
        boolean isNounA = false;
        boolean isNounB = false;
        for (int i = 0; i < V; i++) {
            String[] W = words[i].split(" ");
            for (String w : W) {
                if (w.contentEquals(nounA)) {
                    isNounA = true;
                    nounAID.add(i);
                    break;
                }
                if (w.contentEquals(nounB)) {
                    isNounB = true;
                    nounBID.add(i);
                    break;
                }
            }
        }
        if (!isNounA) throw new IllegalArgumentException(nounA + " is not a noun in word net!");
        if (!isNounB) throw new IllegalArgumentException(nounB + " is not a noun in word net!");
        SAP sap = new SAP(wordMap);
        return sap.length(nounAID, nounBID);

    }


    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("the input argument is null!");
        ArrayList<Integer> nounAID = new ArrayList<Integer>();
        ArrayList<Integer> nounBID = new ArrayList<Integer>();
        boolean isNounA = false;
        boolean isNounB = false;
        for (int i = 0; i < V; i++) {
            String[] W = words[i].split(" ");
            for (String w : W) {
                if (w.contentEquals(nounA)) {
                    isNounA = true;
                    nounAID.add(i);
                    break;
                }
                if (w.contentEquals(nounB)) {
                    isNounB = true;
                    nounBID.add(i);
                    break;
                }
            }
        }
        if (!isNounA) throw new IllegalArgumentException(nounA + " is not a noun in word net!");
        if (!isNounB) throw new IllegalArgumentException(nounB + " is not a noun in word net!");
        SAP sap = new SAP(wordMap);
        return words[sap.ancestor(nounAID, nounBID)];
    }


    // do unit testing of this class

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println(wordnet.words[1]);
        String[] wds = wordnet.words;
        System.out.println(wordnet.sap(wds[1], wds[4]));

    }
}
