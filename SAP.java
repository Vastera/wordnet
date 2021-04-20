/* *****************************************************************************
 *  Name: Vastera Ma
 *  Date: 20210416
 *  Description: Shortest ancestral path in assignment wordnet in Algorithm II: week 1
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

import java.util.HashMap;

public class SAP {
    private Digraph G;
    private int V;
    private Digraph ReverseG;
    private boolean[] marked;


    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = G;
        this.V = G.V();
        this.ReverseG = G.reverse();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v == 0 && w == 0)
            throw new IllegalArgumentException("input v and w are 0 at the same time!");
        if (v == w) // corner case where v == w
            return 0;
        int pathLen = -1;
        int[] layer = new int[V];
        marked = new boolean[V];
        Queue<Integer> q = new Queue<Integer>();
        // DFS to find the adjacency of v and sort them in the topological order
        Stack<Integer> topoOrder = new Stack<Integer>();
        HashMap<Integer, Integer> topoHash = new HashMap<Integer, Integer>(V);
        Operation reversePost = topoOrder::push;
        marked[v] = true;
        for (int i : G.adj(v)) {
            if (!marked[i]) {
                marked[i] = true;
                dfs(i, reversePost);
            }
        }
        reversePost.operation(v);
        int j = 0;
        for (int i : topoOrder)
            topoHash.put(i, j++);
        // BFS to find the least common ancestor and return the length
        q.enqueue(w);
        marked = new boolean[V]; // reset the markers as false
        marked[v] = true;
        if (topoHash.containsKey(w))
            return topoHash.get(w) + layer[w];

        while (!q.isEmpty()) {
            int x = q.dequeue();
            for (int i : G.adj(x)) {
                if (topoHash.containsKey(i)) {
                    pathLen = topoHash.get(i) + layer[x] + 1;
                    break;
                }
                if (!marked[i]) {
                    marked[i] = true;
                    q.enqueue(i);
                    layer[i] = layer[x] + 1;
                }
            }
        }
        return pathLen;
    }

    interface Operation {
        void operation(int p);
    }

    private void dfs(int v, Operation op) {
        for (int i : G.adj(v)) {
            if (!marked[i]) {
                marked[i] = true;
                dfs(i, op);
            }
        }
        op.operation(v);
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        if (v == 0 && w == 0)
            throw new IllegalArgumentException("v and w are 0 at the same time!");
        return -1;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    // public int length(Iterable<Integer> v, Iterable<Integer> w)

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    // public int ancestor(Iterable<Integer> v, Iterable<Integer> w)

    // do unit testing of this class
    public static void main(String[] args) {
        In inGraph = new In(args[0]);
        Digraph G = new Digraph(inGraph);
        SAP sap = new SAP(G);
        System.out.println(sap.length(13, 7));
    }

}
