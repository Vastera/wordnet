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
import java.util.HashSet;

public class SAP {
    private Digraph G;
    private int V;
    private boolean[] marked;


    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = G;
        this.V = G.V();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        if (v == 0 && w == 0)
            throw new IllegalArgumentException("input v and w are 0 at the same time!");
        if (v == w) // corner case where v == w
            return 0;
        int pathLen = -1; // final shortest  path length
        int[] layer = new int[V]; // distance from the vertex w's perspective
        marked = new boolean[V]; // visiting status
        Queue<Integer> q = new Queue<Integer>(); // queue for breadth first search
        // DFS to find the adjacency of v and sort them in the topological order
        Stack<Integer> topoOrder
                = new Stack<Integer>(); // reverse post order for topological sorting
        HashMap<Integer, Integer> topoHash = new HashMap<Integer, Integer>(
                V); // accelerate the search using hashmap storing the topological order of all adjacency of vertext v
        Operation reversePost
                = topoOrder::push; // method reference (function pointer) for flexibility of DFS
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
        marked[w] = true;
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
        if (v == w)
            return v; // corner case where v equals w
        marked = new boolean[V]; // visiting status
        Queue<Integer> q = new Queue<Integer>(); // queue for breadth first search
        // DFS to find the adjacency of v and sort them in the topological order
        Stack<Integer> topoOrder
                = new Stack<Integer>(); // reverse post order for topological sorting
        HashSet<Integer> topoHash = new HashSet<Integer>(
                V); // accelerate the search using hashset storing all adjacency of vertext v
        Operation reversePost
                = topoOrder::push; // method reference (function pointer) for flexibility of DFS
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
            topoHash.add(i);
        // BFS to find the least common ancestor
        q.enqueue(w);
        if (topoHash.contains(w))
            return w;
        marked = new boolean[V];
        marked[w] = true;
        while (!q.isEmpty()) {
            int x = q.dequeue();
            for (int i : G.adj(w)) {
                if (!marked[i]) {
                    if (topoHash.contains(i))
                        return i;
                    marked[i] = true;
                    q.enqueue(i);
                }
            }
        }
        return -1;
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException("input v or w are null!");
        // if (v == w) // corner case where v == w
        //     return 0;
        int pathLen = -1; // final shortest  path length
        int[] layer = new int[V]; // distance from the vertex w's perspective
        marked = new boolean[V]; // visiting status
        Queue<Integer> q = new Queue<Integer>(); // queue for breadth first search
        // DFS to find the adjacency of v and sort them in the topological order
        Stack<Integer> topoOrder
                = new Stack<Integer>(); // reverse post order for topological sorting
        HashMap<Integer, Integer> topoHash = new HashMap<Integer, Integer>(
                V); // accelerate the search using hashmap storing the topological order of all adjacency of vertext v
        Operation reversePost
                = topoOrder::push; // method reference (function pointer) for flexibility of DFS
        for (int i : v)
            marked[i] = true;
        for (int i : v) {
            for (int j : G.adj(i)) {
                if (!marked[j]) {
                    marked[j] = true;
                    dfs(j, reversePost);
                }
            }
            reversePost.operation(i);
        }
        int j = 0;
        for (int i : topoOrder)
            topoHash.put(i, j++);
        // BFS to find the least common ancestor and return the length
        marked = new boolean[V]; // reset the markers as false
        for (int i : w) {
            q.enqueue(i);
            marked[i] = true;
            if (topoHash.containsKey(i))
                return topoHash.get(i) + layer[w];
        }

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


    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    // public int ancestor(Iterable<Integer> v, Iterable<Integer> w)

    // do unit testing of this class
    public static void main(String[] args) {
        In inGraph = new In(args[0]);
        Digraph G = new Digraph(inGraph);
        SAP sap = new SAP(G);
        System.out.println("length = " + sap.length(13, 7));
        System.out.println("common ancesttor : " + sap.ancestor(13, 7));
    }

}
