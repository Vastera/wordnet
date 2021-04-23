import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
    private WordNet wordnet;
    private int V;

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
        int len = nouns.length;
        int[] lenSum = new int[len];
        int dist;
        int i = 0;
        for (i = 0; i < len - 1; i++)
            for (int j = i + 1; j < len; j++) {
                dist = wordnet.distance(nouns[i], nouns[j]);
                lenSum[i] += dist;
                lenSum[j] += dist;
            }
        int lenSumMax = lenSum[0];
        int lenSumMaxIndex = 0;
        for (i = 1; i < len; i++)
            if (lenSum[i] < lenSumMax) {
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
