// Justin Neff - TCSS 342 Compressed Literature
// Cited Resource: https://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class CodingTree {
  Map<Character, String> map;
  byte[] bytes;
  StringBuilder sb;
  PriorityQueue<TreeNode> queue;
  TreeNode root;

  CodingTree() {}

  CodingTree(String message) {
    map = new HashMap<>();
    int[] charTable = countChars(message);
    root = buildTree(charTable);
    buildMap(root, "");
    writeFile(message);
  }

  private void writeFile(String message) {
    sb = new StringBuilder();
    StringCharacterIterator iter = new StringCharacterIterator(message);
    while (iter.next() != CharacterIterator.DONE) {
      sb.append(map.get(iter.current()));
    }
    int binLength = sb.length();
    bytes = new byte[binLength / 8];
    int i = 0;
    for (; i < binLength / 8; i++) {
      bytes[i] = (byte) Integer.parseInt(sb.substring(i * 8, i * 8 + 8), 2);
    }
  }

  private int[] countChars(String message) {
    queue = new PriorityQueue<>();
    StringCharacterIterator iter = new StringCharacterIterator(message);
    int[] charTable = new int[65536];
    charTable[(int) (iter.current())]++;
    while (iter.next() != CharacterIterator.DONE) {
      charTable[(int) (iter.current())]++;
    }
    return charTable;
  }

  private TreeNode buildTree(int[] charTable) {
    for (int i = 0; i < charTable.length; i++) {
      if (charTable[i] > 0) {
        TreeNode t = new TreeNode(charTable[i], (char) i);
        queue.offer(t);
      }
    }

    TreeNode root = null;
    if (queue.size() > 1) {
      while (!queue.isEmpty()) {
        TreeNode t = new TreeNode(queue.poll(), queue.poll());
        t.value = t.left.value + t.right.value;
        if (!queue.isEmpty()) {
          queue.offer(t);
        } else {
          root = t;
        }
      }
    } else {
      root = queue.poll();
    }
    return root;
  }

  private void buildMap(TreeNode node, String str) {
    if (node.isLeaf) {
      map.put(node.c, str);
    } else {
      buildMap(node.left, str + '0');
      buildMap(node.right, str + '1');
    }
  }

  public void readCodes(String text) {
    root = new TreeNode(0, '\0');
    root.isLeaf = false;
    TreeNode node = null;
    String[] codes = text.split(", ");
    for (int i = 0, j = 0; i < codes.length; i++) {
      char c = codes[i].charAt(0);
      String bin = codes[i].substring(2, codes[i].length());
      node = root;
      for (; j < bin.length(); j++) {
        if (bin.charAt(j) == '0') {
          if (node.left == null) {
            node.left = new TreeNode(0, '\0');
            node.left.isLeaf = false;
          }
          node = node.left;
        } else if (bin.charAt(j) == '1') {
          if (node.right == null) {
            node.right = new TreeNode(1, '\0');
            node.right.isLeaf = false;
          }
          node = node.right;
        }
      }
      node.c = c;
      node.isLeaf = true;
      j = 0;
    }

  }

  public void decode(byte[] text, Map<Character, String> codes) {
    sb = new StringBuilder();
    for (int i = 0; i < text.length; i++) {
      sb.append((String.format("%8s", Integer.toBinaryString(text[i] & 0xFF)).replace(' ', '0')));
    }
    binToAscii();
  }

  private void binToAscii() {
    String text = sb.toString();
    sb = new StringBuilder();
    TreeNode node = root;
    for (int i = 0; i < text.length();) {
      if (text.charAt(i) == '0' && node.c == '\0') {
        node = node.left;
        i++;
      } else if (text.charAt(i) == '1' && node.c == '\0') {
        node = node.right;
        i++;
      } else {
        sb.append(node.c);
        node = root;
      }
    }
  }

  // TREE NODES
  private class TreeNode implements Comparable<TreeNode> {
    int value = 0;
    char c = '\0';
    boolean isLeaf = true;
    TreeNode left = null;
    TreeNode right = null;

    TreeNode(TreeNode left, TreeNode right) {
      this.left = left;
      this.right = right;
      isLeaf = false;
    }

    TreeNode(int value, char c) {
      this.value = value;
      this.c = c;
    }

    @Override
    public int compareTo(TreeNode t) {
      if (t.value == this.value)
        return 0;
      else
        return (t.value < this.value) ? 1 : -1;
    }

    @Override
    public String toString() {
      return "[leaf: " + isLeaf + "] [has left branch: "
          + (left != null) + "] [has right branch: " + (right != null) + "]";
    }
  }
}
