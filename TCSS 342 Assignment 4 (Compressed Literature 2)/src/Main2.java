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
public class Main2 {
	
	public static void main(String[] args) throws IOException {
		StringBuilder text = new StringBuilder();
		File uncompressedFile = new File("WarAndPeace.txt");
		BufferedReader myBr = new BufferedReader(new FileReader(uncompressedFile));
		long start = System.currentTimeMillis();
		
		// Read and pass contents of file as string to CodingTree
		while(myBr.ready()) {
			String line = myBr.readLine();
			text.append(line + System.lineSeparator());
		}
		CodingTree codingTree = new CodingTree(text.toString());
		
		// Output MyHashTable<String, String>()codes to a text file
		File codes = new File("codes.txt");
		codes.createNewFile();
		new FileOutputStream(codes, true).close();  
			
		BufferedWriter codesDirectory = new BufferedWriter(new FileWriter(codes.getAbsoluteFile()));
		codesDirectory.write(codingTree.codes.toString());
				
		// Output the compressed message to a binary file
		File compressedFile = new File("compressed.txt");
		compressedFile.createNewFile();
		FileOutputStream compressed = new FileOutputStream(compressedFile.getAbsoluteFile(), false);
		compressed.write(codingTree.bytes);
				
		// Close resources
		myBr.close();
		codesDirectory.close();
		compressed.close();
		
		//codingTree.codes.stats();
		//codingTree.frequencyMap.stats();
		//System.out.println(codingTree.codes);
		
		long end = System.currentTimeMillis();
		long runtime = end - start;
		System.out.println("Runtime: " + runtime + " milliseconds." );
		System.out.println("Original File Size: " + uncompressedFile.length() + " bytes.");
		System.out.println("Final File Size: " + compressedFile.length() + " bytes.");

	}
}
