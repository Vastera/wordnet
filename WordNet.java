import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RedBlackBST;

import java.util.ArrayList;

public class WordNet {
    private RedBlackBST<String, Integer> wordsBST = new RedBlackBST<String, Integer>();
    private String[] words; // store the nodes of synsets
    private Digraph wordMap; // store the graph of wordnet

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

    private class depthFirstSearch { // to check whether the digraph is rooted DAG
        private int V;
        private int[] marked;
        // 0 is unvisited, 1 is visited in the current path, -1 is visited before the current path
        private Digraph digraph;
        private Digraph reDigraph;
        private int root;

        public depthFirstSearch(Digraph digraph) {
            V = words.length;
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
        int V = words.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    // returns all WordNet nouns
    // public Iterable<String> nouns() {
    //
    // }

    // is the word a WordNet noun?
    // public boolean isNoun(String word) {
    //
    // }

    // distance between nounA and nounB (defined below)
    // public int distance(String nounA, String nounB) {
    //
    // }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null)
            throw new IllegalArgumentException("the input argument is null!");
        int V = wordMap.V();
        // get the index of nounA and nounB
        int numA = wordsBST.get(nounA);
        int numB = wordsBST.get(nounB);
        // deep copy of digraph into unDiagraph
        Digraph unDigraph = new Digraph(V);
        for (int i = 0; i < V; i++) {
            for (int j : wordMap.adj(i)) {
                unDigraph.addEdge(i, j);
                unDigraph.addEdge(j, i);
            }
        }
        // BFS to find the shortest ancestral path
        Queue<Integer> path = new Queue<Integer>();
        boolean[] marked = new boolean[V];
        int[] edgeTo = new int[V]; // store the path for retrieve
        edgeTo[numA] = numA;
        path.enqueue(numA);
        int v;
        while (!path.isEmpty()) {
            v = path.dequeue();
            if (v == numB) {
                break;
            }
            marked[v] = true;
            for (int i : unDigraph.adj(v)) {
                if (!marked[i]) {
                    marked[i] = true;
                    path.enqueue(i);
                    edgeTo[i] = v;
                }
            }
        }
        int ancestralNum = numB;
        while (edgeTo[ancestralNum] != ancestralNum) { // before the terminal of the path
            int next = edgeTo[ancestralNum];
            // if there is path from current vertex to the next vertex,
            // then continue, or current is ancestral number
            boolean contains = false;
            for (int j : wordMap.adj(ancestralNum))
                if (j == next) {
                    contains = true;
                    break;
                }
            if (contains)
                ancestralNum = next;
            else
                break;
        }
        return words[ancestralNum];
    }

    // do unit testing of this class

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        System.out.println(wordnet.words[1]);
        String[] wds = wordnet.words;
        System.out.println(wordnet.sap(wds[1], wds[4]));

    }
}
