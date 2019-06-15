import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * TCSS 342 - Data Structures
 * Assignment 4 - Compressed Literature 2
 */

/**
 * 
 * You are also responsible for implementing a Main controller 
 * that uses the CodingTree class to  compress a file.  
 * The main must:  
 * 
 * ● Read the contents of a text file into a String.  
 * ● Pass the String into the CodingTree in order to 
 *   initiate Huffman’s encoding procedure  and  generate
 *   a map of codes.  
 * ● Output the codes to a text file.  
 * ● Output the compressed message to a binary file.  
 * ● Display compression and run time statistics.
 *  
 * @author Steve Mwangi
 * @version Spring 2019
 *
 */
public class Main {
	
	public static void main(String[] args) throws Exception {
		StringBuilder text = new StringBuilder();
		File uncompressedFile = new File("WarAndPeace.txt");
		BufferedReader myBr = new BufferedReader(new FileReader(uncompressedFile));
		long start = System.currentTimeMillis();
		
		// Read and pass contents of file as string to CodingTree
		while(myBr.ready()) {
			String line = myBr.readLine();
			text.append(line);
			text.append(System.lineSeparator());
		}
		CodingTree codingTree = new CodingTree(text.toString());
		
		// Output MyHashTable<String, String>()codes to a text file
		File codes = new File("codes.txt");
		codes.createNewFile();
		BufferedWriter codesDirectory = new BufferedWriter(new FileWriter(codes.getAbsoluteFile()));
		codesDirectory.write(codingTree.codes.toString());
		
		//Output the compressed message to a binary file
		File compressedFile = new File("compressed.txt");
		compressedFile.createNewFile();
		FileOutputStream compressed = new FileOutputStream(compressedFile.getAbsoluteFile());
		compressed.write(codingTree.bytes);
		
		// Hash Table Stats
		codingTree.codes.stats();
		
		// Close resources
		myBr.close();
		codesDirectory.close();
		compressed.close();
		
		long end = System.currentTimeMillis();
		long runtime = end - start;
		System.out.println("Runtime: " + runtime + " milliseconds." );
		System.out.println("Original File Size: " + uncompressedFile.length()/1024 + " kilobytes.");
		System.out.println("Final File Size: " + compressedFile.length()/1024 + " kilobytes.");
		
		// Test Methods
		System.out.println("\n" + "\n" + "The Test for the Coding Tree: " + "\n");
		testCodingTree();
		System.out.println("\n" + "\n" + "The Test for MyHashTable: " + "\n");
		testMyHashTable();
		
//		// Decoding our text files
//		codingTree.decode(compressedFile, codes);
//		
		// Printing out to trace.txt, the stats of MyHashTable.
//		File trace = new File("trace.txt");
//		trace.createNewFile();
//		PrintStream traceS = new PrintStream(trace.getAbsoluteFile());
//		System.setOut(traceS);
//		codingTree.codes.stats();
//		traceS.close();
	}
	
	
	
	/**
	 * This method tests the coding tree on a short simple phrase 
	 * so you can verify its  correctness.  
	 * @return void
	 * @throws IOException 
	 */
	public static void testCodingTree() throws Exception{
		String concise = "Once upon a time there was a dear little girl "
				+ "who was loved by everyone who looked at her, but most "
				+ "of all by her grandmother, and there was nothing that "
				+ "she would not have given to the child. Once she gave "
				+ "her a little cap of red velvet, which suited her so "
				+ "well that she would never wear anything else; so she "
				+ "was always called 'Little Red- Cap.' ";
		CodingTree ct = new CodingTree(concise);
		System.out.println("String used is: " + concise);
		System.out.println("Encoded String: " + ct.encoded);
		System.out.println("The Coding Tree codes hash table is: " + ct.codes.toString());
		System.out.println("The Hash Table stats are: ");
		ct.codes.stats();	
	}
	
	/**
	 * This method tests the hash table. 
	 * @param <K>
	 * @param <V>
	 * @return void
	 */
	public static <K, V> void testMyHashTable(){
		MyHashTable<String, Integer> ht = new MyHashTable<String, Integer>(20);
		int i = 0;
		int s = 65;
		for(i = 0; i< ht.capacity-1; i++) {
			ht.put(Character.toString((char) s), i);
			s++;
		}
		System.out.println("The Table as a string: " + "\n" + ht.toString());
		System.out.println("The maxCount of the table: " + ht.maxCount);
		System.out.println("The key b is linked to value: " + ht.get("b"));
		ht.put("z", 20);
		System.out.println("After Putting z-key, for value-20, the value is: " + ht.get("z"));
		System.out.println();
		ht.stats();
	}
	// Decoding method
	//(Optional) different hashing implementation
}
