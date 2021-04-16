import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.Stack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;

public class WordNet {
    private RedBlackBST<String, Integer> wordsBST = new RedBlackBST<String, Integer>();
    private String[] words; // store the nodes of synsets
    private Digraph wordMap; // store the graph of wordnet
    private int V;
    HashMap<Integer, Integer> topoHash;

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
        V = wordMap.V();
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
        // get the index of nounA and nounB
        int numA = wordsBST.get(nounA);
        int numB = wordsBST.get(nounB);
        // int[] edgeTo = new int[V]; // store the path for retrieve
        // edgeTo[numA] = numA; // the first terminal node

        // BFS to find the shortest ancestral path
        // Queue<Integer> path = new Queue<Integer>(); // store the ancestral path
        // store the words by a hash table in the topological order
        topoHash = new HashMap<Integer, Integer>();
        Integer[] topoArray = topologicalOrder();
        int i = 0;
        for (int word : topologicalOrder()) {
            topoHash.put(word, i);
            i++;
        }
        // set X as the smaller one of numA and numB, while set Y as the bigger one
        int X = Math.min(numA, numB);
        int Y = Math.max(numA, numB);
        // find all the reachable nodes of X by using depth first search staring from X
        // initialize a minheap to store the reachable nodes of X
        int[] xConntedComponent = new reachability(X).XReachVertexes();
        // store the reachable nodes of X as an array and a hashtable (XPathHash) for searching, respectively
        HashMap<Integer, Integer> XPathHash = new HashMap<Integer, Integer>(
                xConntedComponent.length);
        for (int j = 0; j < xConntedComponent.length; j++) {
            XPathHash.put(xConntedComponent[j], j);
        }
        // initialize a stack to store the path from Y to the common ancestral
        // Stack<Integer> YUpward = new Stack<Integer>();
        // transverse the XPathHash to find out whether there is Y, and Y is the common ancestor
        do {
            // if Y exists in the XPathHash, then combine the path to common ancestor from X and the original Y
            if (XPathHash.containsKey(Y)) {
                // find the shortest path from X to current Y using Breadth first search
                // shortestPath sp = new shortestPath(X, Y);
                break;
            }
            else {
                // if Y doesn't exist in the XPathHash, then make the nearest adjacences of Y in the topological order and repeat searching
                int yIndex = topoArray.length - 1;
                for (int j : wordMap.adj(Y)) {
                    if (topoHash.get(j) < yIndex)
                        yIndex = topoHash.get(j);
                }
                Y = topoArray[yIndex];
            }
        } while (topoHash.get(Y) < topoArray.length);
        return Integer.toString(Y);
    }

    // find the shortest path between two vertex
    // private class shortestPath {
    //     Stack<Integer> SP; // record the shortest path
    //     boolean[] marker; // visiting status marker
    //     Queue<Integer> q;
    //     int[] edgeTo; // record the shorest path
    //     int start;
    //     int end;
    //
    //     public shortestPath(int start, int end) {
    //         this.start = start;
    //         this.end = end;
    //         SP = new Stack<Integer>();
    //         marker = new boolean[V];
    //         q = new Queue<Integer>();
    //         edgeTo = new int[V];
    //
    //         q.enqueue(start);
    //         marker[start] = true;
    //         while (!q.isEmpty()) {
    //             int v = q.dequeue();
    //             for (int w : wordMap.adj(v)) {
    //                 if (!marker[w]) {
    //                     marker[v] = true;
    //                     q.enqueue(w);
    //                     edgeTo[w] = v;
    //                 }
    //             }
    //         }
    //     }
    //
    //     public Iterable<Integer> path() {
    //         while (start != end) {
    //             SP.push(end);
    //             end = edgeTo[end];
    //         }
    //         return SP;
    //     }
    // }

    private class reachability {
        private boolean[] marked; // marker of visited status
        private PriorityQueue<Integer> XMinHeap;
        private int[] XAdj;

        public reachability(int X) {
            // X is the source vertex
            // define a min heap for the reachable vertexes of X according to the topological order
            XMinHeap = new PriorityQueue<Integer>(V, new Comparator<Integer>() {
                public int compare(Integer a, Integer b) {
                    return topoHash.get(b) - topoHash.get(a);
                }
            });
            XAdj = new int[V];
            marked = new boolean[V];
            dfs(X);
        }

        private void dfs(int v) {
            marked[v] = true;
            for (int i : wordMap.adj(v)) {
                if (!marked[i]) {
                    XMinHeap.add(i);
                    dfs(i);
                }
            }
        }

        public int[] XReachVertexes() {
            int i = 0;
            while (!XMinHeap.isEmpty()) {
                XAdj[i] = XMinHeap.poll();
                i++;
            }
            return XAdj;
        }
    }

    // find the topological order of the whole wordnet
    private class topological {
        boolean[] marked; // markers for depth first search
        Stack<Integer> topoStack;

        public topological() {
            marked = new boolean[V];
            topoStack = new Stack<Integer>();
            for (int i = 0; i < V; i++)
                if (!marked[i]) topoDfs(i);
        }

        private void topoDfs(int v) {
            marked[v] = true;
            for (int i : wordMap.adj(v))
                if (!marked[i]) topoDfs(i);
            topoStack.push(v);
        }

        public Iterable<Integer> order() {
            return topoStack;
        }

    }

    public Integer[] topologicalOrder() {
        topological topo = new topological();
        ArrayList<Integer> ret = new ArrayList<Integer>();
        for (int i : topo.order()) {
            ret.add(i);
        }
        // int[] ans = new int[ret.size()];
        int size = ret.size();
        return ret.toArray(new Integer[size]);
    }
    // do unit testing of this class

    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        // System.out.println(wordnet.words[1]);
        String[] wds = wordnet.words;
        // for (int i : wordnet.topologicalOrder()) {
        //     System.out.println(i);
        // }
        System.out.println(wordnet.sap(wds[0], wds[2]));

    }
}
