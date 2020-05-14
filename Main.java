// Justin Neff - TCSS 342 Compressed Literature

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;

public class Main {

  public static void main(String[] args) {
    // testCodingTree();
    compressFile();
    //decompressFile();

  }

  private static void compressFile() {

    Long start = System.currentTimeMillis();
    byte bytes[] = null;
    String fileLoc = "./src/WarAndPeace.txt";
    //String fileLoc = "./src/Dracula.txt";
    String text;
    CodingTree tree = null;
    // READ TEXT TO STRING
    Path path = Paths.get(fileLoc);
    try {
      bytes = Files.readAllBytes(path);
      text = new String(bytes, StandardCharsets.UTF_8);
      tree = new CodingTree(text);
    } catch (IOException e) {
    }

    // WRITE COMPRESSED TO FILE
    File file = new File("./src/compressed.txt");
    try {
      if (file.exists()) {
        file.delete();
      }
      file.createNewFile();
      DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
      os.write(tree.bytes);
      os.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // WRITE HUFFMAN CODES TO FILE
    file = new File("./src/codes.txt");
    try {
      PrintWriter pw = new PrintWriter(new FileOutputStream(file));
      pw.write(tree.map.toString());
      pw.close();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
    System.out.println("--------------------------------------------------");
    System.out.println("Execution Time: " + ((new Date()).getTime() - start) + " ms");
    System.out.println("Original Size: " + bytes.length + " bytes");
    System.out.println("Compressed Size: " + tree.bytes.length + " bytes");
    System.out
        .println("Compression: " + (100 - (float) 100 * tree.bytes.length / bytes.length) + "%");
  }

  private static void decompressFile() {

    String text = "";
    CodingTree tree = new CodingTree();
    // READ CODES FROM FILE
    Path path = Paths.get("./src/codes.txt");
    try {
      text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
      tree.readCodes(text.substring(1, text.length() - 2));
    } catch (IOException e) {
    }

    // READ COMPRESSED
    path = Paths.get("./src/compressed.txt");
    try {
      byte[] byteArray = Files.readAllBytes(path);
      tree.decode(byteArray, new HashMap<Character, String>());
    } catch (IOException e) {
    }

    // WRITE DECOMPRESSED
    File file = new File("./src/decompressed.txt");
    try {
      PrintWriter pw = new PrintWriter(new FileOutputStream(file));
      pw.write(tree.sb.toString());
      pw.close();
    } catch (FileNotFoundException e1) {
      e1.printStackTrace();
    }
  }

  private static void testCodingTree() {
    String text = "Huffman";
    CodingTree tree = null;
    // READ TEXT TO STRING
    Path path = Paths.get("./src/Dracula.txt");
    // text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
    tree = new CodingTree(text);

    System.out.println("COMPRESSING");
    System.out.println("Map: " + tree.map);
    System.out.println("Tree Build: " + (tree.root != null));
    System.out.println("Tree Root: " + tree.root);
    System.out.println("Byte Array Created: " + (tree.bytes.length > 0));

    text = "";
    tree = new CodingTree();
    // READ CODES FROM FILE
    path = Paths.get("./src/codes.txt");
    try {
      text = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
      tree.readCodes(text.substring(1, text.length() - 2));
    } catch (IOException e) {
    }

    // READ COMPRESSED
    path = Paths.get("./src/compressed.txt");
    try {
      byte[] byteArray = Files.readAllBytes(path);
      tree.decode(byteArray, new HashMap<>());
    } catch (IOException e) {
    }

    System.out.println("\n\nDECOMPRESSING");
    System.out.println("Tree Built: " + (tree.root != null));
    System.out.println("Tree Root: " + tree.root);
    System.out.println("First few words: \n\n" + tree.sb.substring(0, 100));

  }

}
