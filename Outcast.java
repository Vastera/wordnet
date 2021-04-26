import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class Outcast {
    private WordNet wordnet;
    private int V;
    private Integer[] allNounsID;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        // corner case test
        if (wordnet == null)
            throw new IllegalArgumentException("input argument is null~");
        this.wordnet = wordnet;
        this.V = wordnet.V();
    }

    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {
        // corner case test
        if (nouns == null)
            throw new IllegalArgumentException("input argument 'nouns' is null!");
        // find all nouns in the word net
        int len = nouns.length;
        ArrayList<Integer>[] allNounsID = new ArrayList[len];

        for (int i = 0; i < len; i++) {
            allNounsID[i] = new ArrayList<Integer>();
            int j = 0;
            for (String sequence : wordnet.nouns()) {
                String[] words = sequence.split(" ");
                for (String w : words)
                    if (w.contentEquals(nouns[i])) {
                        allNounsID[i].add(j);
                        break;
                    }
                j++;
            }
        }
        // for (int i = 0; i < len; i++)
        int[] lenSum = new int[len];
        int dist;
        for (int i = 0; i < len - 1; i++)
            for (int j = i + 1; j < len; j++) {
                dist = wordnet.distance(allNounsID[i], allNounsID[j]);
                lenSum[i] += dist;
                lenSum[j] += dist;
            }
        int lenSumMax = lenSum[0];
        int lenSumMaxIndex = 0;
        for (int i = 1; i < len; i++)
            if (lenSum[i] > lenSumMax) {
                lenSumMax = lenSum[i];
                lenSumMaxIndex = i;
            }
        return nouns[lenSumMaxIndex];
    }

    // see test client below
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}

