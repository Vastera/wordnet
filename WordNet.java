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
        depthFirstSearch dF = new depthFirstSearch(wordMap);
        dF.isRootedDAG();
    }

    public int V() {
        return V;
    }

    public int nounID(String s) {
        if (!isNoun(s))
            throw new IllegalArgumentException(s + "is not a noun in the word net!");
        return wordsBST.get(s);
    }

    private class depthFirstSearch { // to check whether the digraph is rooted DAG
        private int[] marked;
        // 0 is unvisited, 1 is visited in the current path, -1 is visited before the current path
        private Digraph digraph;
        private Digraph reDigraph;
        private int root;

        public depthFirstSearch(Digraph digraph) {

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
            DFS(root);
            for (int i = 0; i < digraph.V(); i++) {
                if (marked[i] == 0) {
                    throw new IllegalArgumentException(
                            "there are at least two roots in the Digraph!");
                }
            }
        }

        private void DFS(int v) {
            validateVertex(v);
            if (marked[v] == 1) {
                throw new IllegalArgumentException("there is directed circle in the Digraph!");
            }
            else if (marked[v] == -1) return; // the following part has been visited
            marked[v] = 1;
            for (int i : reDigraph.adj(v)) {
                DFS(i);
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
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException(
                    nounA + " or " + nounB + " is not a noun in wordnet");
        int numA = wordsBST.get(nounA);
        int numB = wordsBST.get(nounB);
        SAP sap = new SAP(wordMap);
        return sap.length(numA, numB);
    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("the input argument is null!");
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("nounA or nounB is not a noun in wordnet");
        int numA = wordsBST.get(nounA);
        int numB = wordsBST.get(nounB);
        SAP sap = new SAP(wordMap);
        return words[(sap.ancestor(numA, numB))];
    }


    // do unit testing of this class

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println(wordnet.words[1]);
        String[] wds = wordnet.words;
        System.out.println(wordnet.sap(wds[1], wds[4]));

    }
}
