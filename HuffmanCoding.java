package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
        int[] occurences = new int[128];
        int count = 0;
        sortedCharFreqList = new ArrayList<>();
        while (StdIn.hasNextChar()) {
            char character = StdIn.readChar();
            occurences[character] = occurences[character] + 1;
            count ++;
        }
        int iterator = 0;
        for (int j = 0; j < occurences.length; j++){
            if (occurences[j] != 0) {
                break;
            }
            if (occurences[j] == 0) {
                iterator++;
            }
        }
        int countSize = 0;
        for (int j = 0; j < occurences.length; j++){
            if (occurences[j]!= 0) {
                countSize++;
            }
        }
        if (countSize > 1){
            for (int i = iterator; i < 127; i++) {
            if (occurences[i]!= 0) {
                char character = (char) i;
                double probOcc = (double) occurences[i] / (double) count;
                CharFreq CharLet = new CharFreq(character, probOcc);
                sortedCharFreqList.add(CharLet);
                }    
            }
        } else {
            char character = (char) iterator;
            double probOcc = (double) occurences[character] / (double)count;
            char bigchar;

            if (iterator + 1 > 127) {
                bigchar = (char) 0;
            } else {
                bigchar = (char) (iterator + 1);
            }
            CharFreq CharLet = new CharFreq(character,probOcc);
            CharFreq CharBigLet = new CharFreq(bigchar, 0);
            sortedCharFreqList.add(CharLet);
            sortedCharFreqList.add(CharBigLet);
        }
        
        Collections.sort(sortedCharFreqList);
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
    /* Your code goes here */
    Queue<TreeNode> tempQueue = new Queue<TreeNode>();
    Queue<TreeNode> Source = new Queue<>(); 
    for (int i = 0; i < sortedCharFreqList.size(); i++) {   
            TreeNode SourceNode = new TreeNode(sortedCharFreqList.get(i), null, null);
            Source.enqueue(SourceNode);
    }
    Queue<TreeNode> Target = new Queue<>();
    
       while (!Source.isEmpty()||Target.size() != 1) {
        while(tempQueue.size() < 2) {
            if (Target.isEmpty()){
                tempQueue.enqueue(Source.dequeue()); 
            } else if (!Source.isEmpty() && Target.peek().getData().getProbOcc() >= Source.peek().getData().getProbOcc()) {
                  //check if source is not empty and the source probability is smaller than target probability
                    tempQueue.enqueue(Source.dequeue());
                } else {
                    tempQueue.enqueue(Target.dequeue());
                }
            }   
        TreeNode tempNode1 = tempQueue.dequeue();
        TreeNode tempNode2 = tempQueue.dequeue();
        double tempSum = tempNode1.getData().getProbOcc() + tempNode2.getData().getProbOcc();
        TreeNode tempRoot = new TreeNode(new CharFreq(null,tempSum), tempNode1, tempNode2);
        Target.enqueue(tempRoot); 
        }
        huffmanRoot = Target.peek();
    }
    


    public void traverseBST(String bitEncoding, TreeNode currentNode) {
        // base case: when Node's CharFreq's 1st argument is not null, meaning there is no character
        
        if (currentNode.getData().getCharacter() != null) {
            encodings[currentNode.getData().getCharacter()] = bitEncoding;
            return;
        }
            traverseBST(bitEncoding + "0", currentNode.getLeft());
            traverseBST(bitEncoding + "1", currentNode.getRight());
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {
    /* Your code goes here */
    encodings = new String[128];
    String bitEncoding = "";
    traverseBST(bitEncoding, getHuffmanRoot());
    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);
    /* Your code goes here */
        String bitString = "";
        while (StdIn.hasNextChar()){ 
            char character = StdIn.readChar();
            bitString = bitString + encodings[character];
        }
        writeBitString(encodedFile, bitString);
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);
    /* Your code goes here */
        String readBitString = readBitString(encodedFile);
        TreeNode pointer = huffmanRoot;
        for (int i = 0; i < readBitString.length(); i++) {
            if (pointer.getData().getCharacter() != null) {
                StdOut.print(pointer.getData().getCharacter());
                pointer = huffmanRoot;
            } 
                char tempChar = readBitString.charAt(i);
                if (tempChar == '0') {
                    pointer = pointer.getLeft();
                } else {
                    pointer = pointer.getRight();
                }
        }
        StdOut.print(pointer.getData().getCharacter());
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}