import java.io.IOException;
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
	public final MyHashTable<String, String> codes = new MyHashTable<String, String>(SIZE);
	// Huffman Tree Root Node.
	public final HuffmanNode huffmanTree;
	// String for words.
	public final String words = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'-"; 
	// ArrayList for HuffmanNodes
	public ArrayList<HuffmanNode> nodeList = new ArrayList<HuffmanNode>();
	// ArrayList for words alternating with non words.
	public ArrayList<String> wordList = new ArrayList<String>(SIZE);
	// Count frequency of appearance of each string and store in map..
	public MyHashTable<String, Integer> frequencyMap;
	// Array of Bytes for compressed message.
	public byte[] bytes;
	public String encoded;
		
	/**
	 *  void CodingTree(String message) ­ a constructor that takes the
	 *  text of a message to be  compressed. 
	 *  
	 *  The constructor is responsible for calling all private methods 
	 *  that carry out  the Huffman coding algorithm.  
	 * @throws IOException 
	 */
	public CodingTree(String message) throws IOException {
		initializeFrequencyHashTable(message, frequencyMap);
		huffmanTree = getHuffmanTree(nodeList);
		encode(huffmanTree, "", codes);
		encodedText(wordList);
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
	
	public void initializeFrequencyHashTable(String text, MyHashTable<String, Integer> frequencyMap){
		frequencyMap = new MyHashTable<>(SIZE);
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
					wordList.add(w1);
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
					wordList.add(w2);
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
		for(HuffmanNode node: nodeList){
			node.setFrequency(frequencyMap.get(node.getKey()));
		}
	}
	
	/**
	 * Method to return the root of our Huffman Tree.
	 * 
	 * @param nodeList
	 * @return huffmanNode
	 */
	private HuffmanNode getHuffmanTree(ArrayList<HuffmanNode> nodeList) {
		ArrayList<HuffmanNode> initial = new ArrayList<>();
		for(HuffmanNode n: nodeList) {
			initial.add(n);
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
	public void encode(HuffmanNode root, String string, MyHashTable<String, String> codes){ 
		if(!root.hasNoChild()) {
			encode(root.left, string + "0", codes);
			encode(root.right, string + "1", codes);
		} else {
			codes.put(root.getKey(), string);
		}
	}
	
	/**
	 * Initialize bytes[] and encoded String
	 * 
	 * @param text
	 * @throws IOException 
	 */
	public void encodedText(ArrayList<String> list) throws IOException {
		StringBuilder encodedText = new StringBuilder();
		for(String s: list) {
			encodedText.append(codes.get(s));
		}
		
		encoded = encodedText.toString();
//		StringBuilder encodedText = new StringBuilder();
//		StringBuilder temp = new StringBuilder();
//		for(int i = 0; i < text.length(); i++){
//			if(words.contains(text.substring(i, i+1))) {
//				temp.append(text.substring(i, i+1));
//			}
//			else {
//				// Encoding the words in temp.
//				if(temp.length() != 0) {
//					encodedText.append(codes.get(temp.toString()));
//					temp.delete(0, temp.length());
//				}
//				// Encoding the non-words.
//				encodedText.append(codes.get(text.substring(i, i+1)));
//			}
//		}
		int bits = 8;
		bytes = new byte[encodedText.length()/bits];
		for(int i = 0; i<bytes.length; i++) {
			bytes[i] = (byte) Integer.parseUnsignedInt(encodedText.substring(i*bits, (i*bits)+bits), 2);
		}
		//bytes = compress(bytes);
	}
	
//	/**
//	 * Decoding method.
//	 * 
//	 * @param compressed
//	 * @param codes
//	 * @throws IOException
//	 */
//	public void decode(File compressed, File codes) throws IOException {
//		BufferedReader brCompressed = new BufferedReader(new FileReader(compressed));
//		BufferedReader brCodes = new BufferedReader(new FileReader(codes));
//		StringBuilder sbCompressed = new StringBuilder();
//		StringBuilder decoded = new StringBuilder();
//		ArrayList<String> codesList = new ArrayList<String>();
//		while(brCompressed.ready()) {
//			sbCompressed.append(brCompressed.readLine() + "\n");
//		}
//		
//		// Add Codes String to an ArrayList.
//		while(brCodes.ready()) {
//			codesList.add(brCodes.readLine());
//		}
//		
//		// Add each substring delimited by " = " to the Huffman Node/Tree
//		HuffmanNode ht = new HuffmanNode(null, null);
//		for(String s: codesList) {
//			String[] sa = s.split(" = ");
//			if(sa.length == 2) {
//				String a = sa[0];
//				String b = sa[1];
//				add(a, b, ht);
//			}
//		}
//		
//		// Traverse Tree to Decode Message.
//		for(int i = 0; i < sbCompressed.length()-1; i++) {
//			decode(ht, sbCompressed.toString(), i, decoded);
//		}
//		
////		System.out.println(decoded.toString());
////		File decodedText = new File("decoded.txt");
////		decodedText.createNewFile();
////		BufferedWriter d = new BufferedWriter(new FileWriter(decodedText.getAbsoluteFile()));
////		d.write(decoded.toString());
//		
//		brCompressed.close();
//		brCodes.close();
////		d.close();
//		
//	}
//	
//	public void decode(HuffmanNode root, String s, int i, StringBuilder sb){ 
//		if(!root.hasNoChild()) {
//			if(s.charAt(i) == '0') {
//				decode(root.left, s, i+1, sb);
//			} else {
//				decode(root.right, s, i+1, sb);
//			}
//			
//		} else {
//			sb.append(root.key);
//		}
//	}
//	
//	/**
//	 * Helper Method that recreates the HuffmanTree.
//	 * @param data
//	 * @param sequence
//	 * @param node
//	 */
//	public void add(String data, String sequence, HuffmanNode node){
//        int i = sequence.length()-1;
//        while(i>=0) {
//        	if(sequence.charAt(i)=='0') {
//        		if(node.left==null) {
//        			if(i==0) {
//        				node.left = new HuffmanNode(data, sequence);
//        			} else {
//        				node.left = new HuffmanNode(null, null);
//        			}
//        			
//        		}   		
//        	} else if(sequence.charAt(i)=='1') {
//        		if(node.right==null) {
//        			if(i==0) {
//        				node.right = new HuffmanNode(data, sequence);
//        			} else {
//        				node.right = new HuffmanNode(null, null);
//        			}
//        		} 
//        	}
//        	i--;
//        }
//        
//        
//    }
	
//	/**
//	 * This method helps compress the byte array above using the in built
//	 * deflater.
//	 * 
//	 * Sources: https://dzone.com/articles/how-compress-and-uncompress
//	 * @param data
//	 * @return
//	 * @throws IOException
//	 */
//	private byte[] compress(byte[] data) throws IOException {
//		Deflater deflater = new Deflater();  
//	    deflater.setInput(data);  
//	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);   
//	    deflater.finish();  
//	    byte[] buffer = new byte[1024];   
//	    while (!deflater.finished()) {  
//	    	int count = deflater.deflate(buffer); // returns the generated code... index  
//	    	outputStream.write(buffer, 0, count);   
//	    }  
//	    outputStream.close();  
//	    byte[] output = outputStream.toByteArray();  
//	    return output;
//	}  
	
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

	