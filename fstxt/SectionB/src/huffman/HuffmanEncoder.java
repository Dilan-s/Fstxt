package huffman;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.TreeMap;

public class HuffmanEncoder {

  final HuffmanNode root;
  final Map<String, String> word2bitsequence;

  private HuffmanEncoder(HuffmanNode root,
      Map<String, String> word2bitSequence) {
    this.root = root;
    this.word2bitsequence = word2bitSequence;
  }

  public static HuffmanEncoder buildEncoder(Map<String, Integer> wordCounts) {

    if (wordCounts == null) {
      throw new HuffmanEncoderException("wordCounts cannot be null");
    }
    if (wordCounts.size() < 2) {
      throw new HuffmanEncoderException("This encoder requires at least two different words");
    }

    // fixing the order in which words will be processed: this determinize the execution and makes
    // tests reproducible.
    TreeMap<String, Integer> sortedWords = new TreeMap<String, Integer>(wordCounts);
    PriorityQueue<HuffmanNode> queue = new PriorityQueue<>(sortedWords.size());

    //YOUR IMPLEMENTATION HERE...
    HuffmanNode root = null;
    for (Map.Entry<String, Integer> entry : sortedWords.entrySet()) {
      queue.offer(new HuffmanLeaf(entry.getValue(), entry.getKey()));
    }
    while (queue.size() > 1) {
      HuffmanNode fst = queue.poll();
      HuffmanNode snd = queue.poll();
      HuffmanNode parent = new HuffmanInternalNode(fst, snd);
      queue.offer(parent);
    }
    root = queue.poll();
    Map<String, String> word2bitSequence = new HashMap<>();
    traverse(root, word2bitSequence, "");

    return new HuffmanEncoder(root, word2bitSequence);
  }

  private static void traverse(HuffmanNode root, Map<String, String> mapping, String prefix) {
    if (root instanceof HuffmanLeaf) {
      mapping.put(((HuffmanLeaf) root).word, prefix);
    } else {
      HuffmanInternalNode newRoot = (HuffmanInternalNode) root;
      HuffmanNode left = newRoot.left;
      HuffmanNode right = newRoot.right;
      traverse(left, mapping, prefix + '0');
      traverse(right, mapping, prefix + '1');
    }
  }


  public String compress(List<String> text) {
    assert text != null && text.size() > 0;
    StringBuilder sb = new StringBuilder();
    for (String s : text) {
      String res = word2bitsequence.get(s);
      if (res == null){
        throw new HuffmanEncoderException();
      }
      sb.append(res);
    }
    return sb.toString();
  }


  public List<String> decompress(String compressedText) {
    assert compressedText != null && compressedText.length() > 0;
    List<String> res = new ArrayList<>();
    char[] input = compressedText.toCharArray();
    HuffmanNode traverse = root;
    for (char c : input) {
      traverse = getWord(traverse, c);
      if (traverse instanceof HuffmanLeaf) {
        res.add(((HuffmanLeaf) traverse).word);
        traverse = root;
      }
    }
    if (!traverse.equals(root)){
      throw new HuffmanEncoderException();
    }

    return res;
  }

  private static HuffmanNode getWord(HuffmanNode node, char direction)
      throws HuffmanEncoderException {
    HuffmanInternalNode internalNode = (HuffmanInternalNode) node;
    if (internalNode.left == null || internalNode.right == null) {
      throw new HuffmanEncoderException("Invalid Compressed Text");
    }
    if (direction == '0') {
      return internalNode.left;
    } else {
      return internalNode.right;
    }
  }

  // Below the classes representing the tree's nodes. There should be no need to modify them, but
  // feel free to do it if you see it fit

  private static abstract class HuffmanNode implements Comparable<HuffmanNode> {

    private final int count;

    public HuffmanNode(int count) {
      this.count = count;
    }

    @Override
    public int compareTo(HuffmanNode otherNode) {
      return count - otherNode.count;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof HuffmanEncoder)) {
      return false;
    }
    HuffmanEncoder that = (HuffmanEncoder) o;
    return Objects.equals(root, that.root) &&
        Objects.equals(word2bitsequence, that.word2bitsequence);
  }

  @Override
  public int hashCode() {
    return Objects.hash(root, word2bitsequence);
  }

  private static class HuffmanLeaf extends HuffmanNode {

    private final String word;

    public HuffmanLeaf(int frequency, String word) {
      super(frequency);
      this.word = word;
    }
  }


  private static class HuffmanInternalNode extends HuffmanNode {

    private final HuffmanNode left;
    private final HuffmanNode right;

    public HuffmanInternalNode(HuffmanNode left, HuffmanNode right) {
      super(left.count + right.count);
      this.left = left;
      this.right = right;
    }
  }
}
