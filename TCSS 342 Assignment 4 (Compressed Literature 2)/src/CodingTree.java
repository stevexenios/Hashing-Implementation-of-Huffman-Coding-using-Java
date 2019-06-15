import java.util.ArrayList;
import java.util.Collections;

/*
 * TCSS 342 - Data Structures
 * Assignment 4 - Compressed Literature 2
 */

/**
 * You are responsible for implementing the CodingTree class that must 
 * function according to the  following interface:  
 * 
 * ● CodingTree(String fulltext)   
 * 	○ a constructor that takes the text of an English message to be 
 *	  compressed.   
 *  ○ The constructor is responsible for calling all methods that carry
 *    out the Huffman  coding algorithm and ensuring that the following
 *    property has the correct value.  
 * 
 * ● MyHashTable<String, String> codes   
 * 	○ a hash table of words or separators used as keys to retrieve strings
 *    of 1s and 0s  as values.  
 * 
 * ● String or List<Bytes> bits  
 * 	○ a data member that is the message encoded using the Huffman codes.  
 * 
 * ● (Optional)​  String decode(String bits, Map<String, String> codes) 
 *   ­ this method will take  the output of Huffman’s encoding and produce
 *   the original text. 
 * 
 * Sources: www.techiedelight.com/huffman-coding/
 * @author Steve Mwangi
 * @version Spring 2019
 *
 */
public class CodingTree {
	// Default HashTable capacity
	public static final int SIZE = 32768;
	// HashTables
	public final MyHashTable<String, String> codes;
	// Count frequency of appearance of each string and store in map..
	public MyHashTable<String, Integer> frequencyMap;
	// Huffman Tree Root Node.
	public final HuffmanNode huffmanTree;
	// ArrayList for HuffmanNodes
	public ArrayList<HuffmanNode> nodeList = new ArrayList<HuffmanNode>();
	// Array of Bytes for compressed message.
	public byte[] bytes;
	// String for words.
	public String words = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'-"; 
	
	/**
	 *  void CodingTree(String message) ­ a constructor that takes the
	 *  text of a message to be  compressed. 
	 *  
	 *  The constructor is responsible for calling all private methods 
	 *  that carry out  the Huffman coding algorithm.  
	 */
	public CodingTree(String message) {
		frequencyMap = getFrequencyHashTable(message);
		huffmanTree = getHuffmanTree(nodeList);
		codes = encode(huffmanTree, "", new MyHashTable<String, String>(SIZE));
		reassignFrequencies(nodeList);
		encodedText(message);
	}

	/**
	 * Counting the frequency of words and separators in a text file.  
	 * 		○ for the purposes of this assignment a word will count
	 *  	  as string of characters from  the set 
	 * 		  {0,...,9,A,...,Z,a,...,z,’,­}.  
	 * 		
	 * 		Notice the ‘ and capital letters may appear in the string
	 * 	    for words like “wouldn’t’ve” and “d’Eckmuhl”.  
	 * 		
	 * 		Every other character is a  separator and will be handled 
	 * 		as a string of length one (i.e. “ “, “\n”, “!”, etc).  
	 * 		
	 * 		○ use a hashtable of your creation (see above) to store 
	 * 		  each word and separator in  the hash table with its count.  
	 */
	
	public MyHashTable<String, Integer> getFrequencyHashTable(String text){
		MyHashTable<String, Integer> frequencyMap = new MyHashTable<>(SIZE);
		StringBuilder wordsBuild = new StringBuilder();
		StringBuilder otherWordsBuild = new StringBuilder();
		for(int i = 0; i<text.length(); i++) {
			if(words.contains(text.substring(i, i+1))) {
				wordsBuild.append(text.substring(i, i+1));
			} else {
				// if we branch in here, we can clear both string 
				// builders, add values to Map and iterate again.
				otherWordsBuild.append(text.charAt(i));
				String w1 = wordsBuild.toString();
				String w2 = otherWordsBuild.toString();
				// Add words. 
				if(w1.length() != 0) {
					wordsBuild.delete(0, wordsBuild.length());
					if(frequencyMap.containsKey(w1)) {
						frequencyMap.put(w1, frequencyMap.get(w1)+1);
					} else {
						frequencyMap.put(w1, 1);
						nodeList.add(new HuffmanNode(w1, ""));
					}
				}
				
				//Add non-words.
				if(w2.length() != 0) {
					otherWordsBuild.delete(0, otherWordsBuild.length());
					if(frequencyMap.containsKey(w2)) {
						frequencyMap.put(w2, frequencyMap.get(w2)+1);
					} else {
						frequencyMap.put(w2, 1);
						nodeList.add(new HuffmanNode(w2, ""));
					}
				}
			}
		}
		return frequencyMap;
	}
	
	/**
	 * Method to return the root of our Huffman Tree.
	 * 
	 * @param nodeList
	 * @return huffmanNode
	 */
	private HuffmanNode getHuffmanTree(ArrayList<HuffmanNode> nodeList) {
		ArrayList<HuffmanNode> initial = new ArrayList<>();
		for(int i = 0; i < nodeList.size(); i++) {
			initial.add(nodeList.get(i));
		}
		while(initial.size() > 1) {
			Collections.sort(initial);
			HuffmanNode huffmanTree = new HuffmanNode(null, null);
			huffmanTree.setFrequency(initial.get(0).getFrequency()+initial.get(1).getFrequency());
			huffmanTree.left = initial.get(0);
			huffmanTree.right = initial.get(1);
			initial.remove(0);
			initial.remove(0);
			initial.add(0, huffmanTree);
		}
		return initial.get(0);
	}
	
	/**
	 * This method is for traversing the huffman tree and storing the
	 * huffman codes in a hash table.
	 *
	 * @param root
	 * @param string
	 * @param codes
	 */
	public MyHashTable<String, String> encode(HuffmanNode root, String string, MyHashTable<String, String> codes){ 
		if(!root.hasNoChild()) {
			encode(root.left, string + "0", codes);
			encode(root.right, string + "1", codes);
		} else {
			codes.put(root.getKey(), string);
		}
		return codes;
	}
	
	/**
	 * Initialize bytes[] and encoded String
	 * 
	 * @param text
	 */
	public void encodedText(String text) {
		StringBuilder encodedText = new StringBuilder();
		StringBuilder temp = new StringBuilder();
		for(int i = 0; i < text.length(); i++){
			if(words.contains(text.substring(i, i+1))) {
				temp.append(text.substring(i, i+1));
			}
			else {
				if(temp.length() != 0) {
					encodedText.append(codes.get(temp.toString()));
					temp.delete(0, temp.length());
				}
				encodedText.append(codes.get(text.substring(i, i+1)));
			}
		}
		bytes = new byte[encodedText.length()/8];
		for(int i = 0; i<bytes.length; i++) {
			bytes[i] = (byte) Integer.parseUnsignedInt(encodedText.substring(i*8, (i*8)+8), 2);
		}
	}
	
	/**
	 * Private Helper method to fill array list of nodes with the right frequency.
	 * @param nodeList2
	 */
	private void reassignFrequencies(ArrayList<HuffmanNode> nodeList2) {
		for(int i = 0; i< nodeList.size()-1; i++){
			nodeList.get(i).setFrequency(frequencyMap.get(nodeList.get(i).getKey()));
		}
	}
	
	/**
	 * Inner class for Huffman tree nodes.
	 * @author Steve Mwangi
	 * 
	 * Sources: www.techiedelight.com/huffman-coding/
	 */
	public class HuffmanNode implements Comparable<HuffmanNode>{
		// Strings for creating our HuffmanNodes
		private String key, code;
		// String Frequency
		int frequency;
		//Left and Right nodes
		HuffmanNode left, right;
		
		/**
		 * Non Default Constructor.
		 * @param s
		 * @param c
		 */
		HuffmanNode(String s, String c){
			this.key = s;
			this.code = c;
			this.frequency = 1;
		}
		
		public String getKey() {return key;}
		public String getCode() {return code;}
		public void setCode(String s) {code = s;}
		public int getFrequency() {return frequency;}
		public void setFrequency(int i) {frequency = i;}
		
		@Override
		public int compareTo(HuffmanNode other) {
			return this.frequency - other.frequency;
		}
		
		public boolean isEquals(String s) {
			return this.key.equals(s);
		}
		
		public boolean hasNoChild() {
			return this.left == null && this.right == null;
		}
	}
	
}


///**
//*  (Optional)  String decode(List<byte[]>, Map<Character, String> codes) ­ this
//*   method will  take the output of Huffman’s encoding and produce the original text.
//*/
//public String decode(List<String> bits, Map<Character, String> codes) {
//	StringBuilder decodedBits = new StringBuilder();
//	for(int i = 0; i< bits.size(); i++ ) {
//		for(Map.Entry<Character, String> entry: codes.entrySet()) {
//			if(entry.getValue().equals(bits.get(i))) {
//				decodedBits.append(entry.getKey());
//			}
//		}
//	}
//	return decodedBits.toString();
//}

//public StringBuilder getCodeDirectory(MyHashTable<String, String> hc, StringBuilder sb) {
//	for(Map.Entry<String, String> entry: hc.entrySet()) {
//		sb.append("\n" + entry.getKey() + ":" + entry.getValue());
//	}
//	return sb;
//}
//
//public List<String> getBits(MyHashTable<String, String> hc, String m) {
//	List<String> b = new ArrayList<String>();
//	for(int i = 0; i < m.length(); i++) { 
//		b.add(hc.get(m.charAt(i)));
//	}
//	return b;
//}

///**
// * Part of this method I saw on Github.
// * @author David Foster & Zeeshan Karim
// * @author Steve Mwangi
// * @param hc
// * @param m
// * @param bytes
// * @return String
// */
//private byte[] getCompressed(Map<Character, String> hc, String m, byte[] bytes) {
//	StringBuilder compressedBits = new StringBuilder();
//	for(int i = 0; i<m.length(); i++) {
//		compressedBits.append(hc.get(m.charAt(i)));
//	}
//	
//	bytes = new byte[compressedBits.length()/8];
//	for(int i = 0; i<bytes.length; i++) {
//		bytes[i] = (byte) Integer.parseUnsignedInt(compressedBits.substring(i*8, (i*8)+8), 2);
//	}
//	encoded = compressedBits.toString();
//	return bytes;
//}
