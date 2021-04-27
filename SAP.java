/* *****************************************************************************
 *  Name: Vastera Ma
 *  Date: 20210416
 *  Description: Shortest ancestral path in assignment wordnet in Algorithm II: week 1
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;
import java.util.HashSet;


public class SAP {
    private Digraph G;
    private int V;
    private boolean[] marked;
    private int[] layer;
    private int lastLengh;
    private int lastAncestor;
    private Iterable<Integer> lastV;
    private Iterable<Integer> lastW;


    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.V = G.V();
        this.G = new Digraph(V);
        for (int v = 0; v < V; v++)
            for (int w : G.adj(v))
                this.G.addEdge(v, w);
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        Queue<Integer> qv = new Queue<Integer>();
        Queue<Integer> qw = new Queue<Integer>();
        qv.enqueue(v);
        qw.enqueue(w);
        return length(qv, qw);

    }


    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        Queue<Integer> qv = new Queue<Integer>();
        Queue<Integer> qw = new Queue<Integer>();
        qv.enqueue(v);
        qw.enqueue(w);
        return ancestor(qv, qw);

    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if ((v.equals(lastV) && w.equals(lastW)) || v.equals(lastW) && w.equals(lastV))
            return lastLengh;
        else {
            lastV = v;
            lastW = w;
        }
        if (v == null || w == null)
            throw new IllegalArgumentException("input v or w are null!");
        for (int i : v)
            validateVertex(i);
        for (int i : w)
            validateVertex(i);
        int ancestor = -1;
        marked = new boolean[V]; // visiting status
        layer = new int[V];
        Queue<Integer> qv = new Queue<Integer>(); // queue for breadth first search
        Queue<Integer> qw = new Queue<Integer>(); // queue for breadth first search
        HashSet<Integer> hashV = new HashSet<Integer>(
                V); // accelerate the search using hashset storing the vertex in queue v
        HashSet<Integer> hashW = new HashSet<Integer>(
                V); // accelerate the search using hashset storing the vertex in queue w
        for (int i : v) {
            qv.enqueue(i);
            hashV.add(i);
            layer[i] = 0;
        }
        for (int i : w) {
            if (hashV.contains(i)) {
                lastAncestor = i;
                return 0; // v and w have the same vertex
            }
            qw.enqueue(i);
            hashW.add(i);
        }
        while (!qv.isEmpty() && !qw.isEmpty()) {
            int xv = qv.dequeue();
            hashV.remove(xv);
            for (int i : G.adj(xv))
                if (hashW.contains(i)) {
                    lastAncestor = i;
                    return layer[xv] + 1 + layer[i];
                }
                else {
                    layer[i] = layer[xv] + 1;
                    qv.enqueue(i);
                    hashV.add(i);
                }

            int xw = qw.dequeue();
            hashW.remove(xw);
            for (int i : G.adj(xw)) {
                if (hashV.contains(i)) {
                    lastAncestor = i;
                    return layer[xw] + 1 + layer[i];
                }
                else {
                    layer[i] = layer[xw] + 1;
                    qw.enqueue(i);
                    hashW.add(i);
                }
            }
        }
        lastAncestor = -1;
        return ancestor;
    }


    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if ((v.equals(lastV) && w.equals(lastW)) || v.equals(lastW) && w.equals(lastV))
            return lastAncestor;
        else {
            lastV = v;
            lastW = w;
        }
        if (v == null || w == null)
            throw new IllegalArgumentException("input v or w are null!");
        for (int i : v)
            validateVertex(i);
        for (int i : w)
            validateVertex(i);
        int ancestor = -1;
        marked = new boolean[V]; // visiting status
        layer = new int[V];
        Queue<Integer> qv = new Queue<Integer>(); // queue for breadth first search
        Queue<Integer> qw = new Queue<Integer>(); // queue for breadth first search
        HashSet<Integer> hashV = new HashSet<Integer>(
                V); // accelerate the search using hashset storing the vertex in queue v
        HashSet<Integer> hashW = new HashSet<Integer>(
                V); // accelerate the search using hashset storing the vertex in queue w
        for (int i : v) {
            qv.enqueue(i);
            hashV.add(i);
            layer[i] = 0;
        }

        for (int i : w) {
            if (hashV.contains(i)) {
                lastLengh = 0;
                return i; // v and w have the same vertex
            }
            qw.enqueue(i);
            hashW.add(i);
        }
        while (!qv.isEmpty() && !qw.isEmpty()) {
            int xv = qv.dequeue();
            hashV.remove(xv);
            for (int i : G.adj(xv))
                if (hashW.contains(i)) {
                    lastLengh = layer[xv] + 1 + layer[i];
                    return i;
                }
                else {
                    layer[i] = layer[xv] + 1;
                    qv.enqueue(i);
                    hashV.add(i);
                }

            int xw = qw.dequeue();
            hashW.remove(xw);
            for (int i : G.adj(xw)) {
                if (hashV.contains(i)) {
                    lastLengh = layer[xw] + 1 + layer[i];
                    return i;
                }
                else {
                    layer[i] = layer[xw] + 1;
                    qw.enqueue(i);
                    hashW.add(i);
                }
            }
        }
        lastLengh = -1;
        return ancestor;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In inGraph = new In(args[0]);
        Digraph G = new Digraph(inGraph);
        SAP sap = new SAP(G);
        System.out.println("length = " + sap.length(1, 5));
        System.out.println("common ancestor : " + sap.ancestor(1, 5));

        Iterable<Integer> V = Arrays.asList(new Integer[] { 13, 23, 24 });
        Iterable<Integer> W = Arrays.asList(new Integer[] { 6, 16, 17 });
        System.out.println(
                "length = " + sap.length(V, W));
        System.out.println(
                "common ancestor = " + sap.ancestor(V, W));
    }

}
