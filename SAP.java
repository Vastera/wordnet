/* *****************************************************************************
 *  Name: Vastera Ma
 *  Date: 20210416
 *  Description: Shortest ancestral path in assignment wordnet in Algorithm II: week 1
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;

public class SAP {
    private Digraph G;
    private int V;
    private Digraph ReverseG;

    private class Vertex {
        int value;
        boolean direct;

        public Vertex(int v, boolean d) {
            value = v;
            boolean direct = d;
        }
    }

    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {
        this.G = G;
        this.V = G.V();
        this.ReverseG = G.reverse();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        int[] edgeTo = new int[V];
        int pathLen = 1;
        boolean[] marked = new boolean[V];
        Queue<Vertex> q = new Queue<Vertex>();
        q.enqueue(new Vertex(v, true));
        q.enqueue(new Vertex(v, false));
        while (!q.isEmpty()) {
            Vertex x = q.dequeue();
            if (!marked[x.value]) {
                marked[x.value] = true;
                if (x.direct) { // positive direction
                    for (int i : G.adj(x.value)) {
                        if (i == w) { // reach the terminal vertex
                            int y = x.value;
                            while (edgeTo[y] != v) {
                                pathLen++;
                                y = edgeTo[y];
                            }
                            return pathLen;
                        }
                        q.enqueue(new Vertex(i, true));
                        edgeTo[i] = x.value;
                    }
                }
                // negative direction and positive direction
                for (int i : ReverseG.adj(x.value)) {
                    if (i == w) { // reach the terminal vertex
                        int y = x.value;
                        while (edgeTo[y] != v) {
                            pathLen++;
                            y = edgeTo[y];
                        }
                        return pathLen;
                    }
                    q.enqueue(new Vertex(i, false));
                    edgeTo[i] = x.value;
                }
            }
        }
        return -1;
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    // public int ancestor(int v, int w)

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    // public int length(Iterable<Integer> v, Iterable<Integer> w)

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    // public int ancestor(Iterable<Integer> v, Iterable<Integer> w)

    // do unit testing of this class
    // public static void main(String[] args){
    //
    //     SAP sap = new SAP(G);
    // }

}
