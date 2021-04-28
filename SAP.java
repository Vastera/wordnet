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
    private final Digraph G;
    private final int V;
    private int lastLength;
    private int lastAncestor;
    private Iterable<Integer> lastV;
    private Iterable<Integer> lastW;
    // final private int inf = Integer.MAX_VALUE;


    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.V = G.V();
        this.G = new Digraph(G);
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
        if (v == null || w == null)
            throw new IllegalArgumentException(
                    "the input argument " + v + " or " + w + " is null!");
        for (Integer i : v) {
            if (i == null)
                throw new IllegalArgumentException(i + " is null in the v!");
            validateVertex(i);
        }
        for (Integer i : w) {
            if (i == null)
                throw new IllegalArgumentException(i + " is null in the w!");
            validateVertex(i);
        }
        if ((v.equals(lastV) && w.equals(lastW)) || v.equals(lastW) && w.equals(lastV)) {
            return lastLength;
        }
        else {
            lastV = v;
            lastW = w;
        }
        shortestAncestorPath(v, w);
        return lastLength;
    }


    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null)
            throw new IllegalArgumentException(
                    "the input argument " + v + " or " + w + " is null!");
        for (Integer i : v) {
            if (i == null || i < 0 || i >= V)
                throw new IllegalArgumentException(i + " is invalid in the v!");
        }
        for (Integer i : w) {
            if (i == null || i < 0 || i >= V)
                throw new IllegalArgumentException(i + " is invalid in the w!");
        }
        if ((v.equals(lastV) && w.equals(lastW)) || v.equals(lastW) && w.equals(lastV)) {
            return lastAncestor;
        }
        else {
            lastV = v;
            lastW = w;
        }
        shortestAncestorPath(v, w);
        return lastAncestor;
    }

    // throw an IllegalArgumentException unless {@code 0 <= v < V}
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException(
                    "vertex " + v + " is not between 0 and " + (V - 1));
    }

    private void shortestAncestorPath(Iterable<Integer> v, Iterable<Integer> w) {
        lastLength = V;
        lastAncestor = -1;
        boolean[] markedV = new boolean[V]; // visiting status
        boolean[] markedW = new boolean[V];
        int[] layerV = new int[V];
        int[] layerW = new int[V];
        Queue<Integer> qv = new Queue<Integer>(); // queue for breadth first search
        Queue<Integer> qw = new Queue<Integer>(); // queue for breadth first search
        HashSet<Integer> hashV = new HashSet<Integer>(
                V); // accelerate the search using hashset storing the vertex in queue v
        HashSet<Integer> hashW = new HashSet<Integer>(
                V); // accelerate the search using hashset storing the vertex in queue w
        for (int i : v) {
            qv.enqueue(i);
            hashV.add(i);
            markedV[i] = true;
        }

        for (int i : w) {
            if (hashV.contains(i)) {
                lastLength = 0;
                lastAncestor = i; // v and w have the same vertex
                return;
            }
            qw.enqueue(i);
            hashW.add(i);
            markedW[i] = true;
        }
        int xv, xw;
        int l = 0;
        while ((!qv.isEmpty() || !qw.isEmpty()) && l < lastLength) {
            while (!qv.isEmpty() && layerV[qv.peek()] == l) {
                xv = qv.dequeue();
                for (int i : G.adj(xv)) {
                    if (!markedV[i]) {
                        if (hashW.contains(i) && layerV[xv] + 1 + layerW[i] < lastLength) {
                            lastLength = layerV[xv] + 1 + layerW[i];
                            lastAncestor = i;
                        }
                        layerV[i] = layerV[xv] + 1;
                        qv.enqueue(i);
                        hashV.add(i);
                        markedV[i] = true;
                    }
                }
            }

            while (!qw.isEmpty() && layerW[qw.peek()] == l) {
                xw = qw.dequeue();
                for (int i : G.adj(xw)) {
                    if (!markedW[i]) {
                        if (hashV.contains(i) && layerW[xw] + 1 + layerV[i] < lastLength) {
                            lastLength = layerW[xw] + 1 + layerV[i];
                            lastAncestor = i;
                        }
                        layerW[i] = layerW[xw] + 1;
                        qw.enqueue(i);
                        hashW.add(i);
                        markedW[i] = true;
                    }
                }
            }
            l++;
        }

        lastLength = lastLength == V ? -1 : lastLength;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In inGraph = new In(args[0]);
        Digraph G = new Digraph(inGraph);
        SAP sap = new SAP(G);
        // int v = 2;
        // int w = 5;
        // System.out.println("length = " + sap.length(v, w));
        // System.out.println("common ancestor : " + sap.ancestor(v, w));

        Iterable<Integer> V = Arrays.asList(new Integer[] { 0, 7, 9, 12 });
        Iterable<Integer> W = Arrays.asList(new Integer[] { 1, 2, null, 4, 5, 10 });

        // System.out.println(
        //         "length = " + sap.length(V, W));
        System.out.println(
                "common ancestor = " + sap.ancestor(V, W));

    }

}
