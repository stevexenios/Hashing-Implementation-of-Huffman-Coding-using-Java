import java.util.Arrays;

/*
 * TCSS 342 - Data Structures
 * Assignment 4 - Compressed Literature 2
 */

/**
 * @author Steve Mwangi
 * @version Spring 2019
 */
public class MyHashTable<K, V> {
	public int[] counts = new int[1000];
	public K[] Keys;
	public V[] Values;
	public int capacity;
	public int entries = 0;
	public int maxCount = 0;
	
	/**
	 * MyHashTable<K, V>(int capacity) constructor.   
	 * 
	 * ○ creates a hash table with capacity number of buckets 
	 * (for this assignment you  will use capacity  2^15 = 32768)
	 * 
	 * ○ K is the type of the keys  ○ V is the type of the values
	 *   
	 * @param capacity
	 */
	@SuppressWarnings("unchecked")
	MyHashTable(int size){
		this.capacity = size;
		Keys = (K[])new Object[size];
		Values = (V[])new Object[size];
		Arrays.fill(counts, 0);
	}
	
	/**
	 * Update or add the newValue to the bucket hash(searchKey)
	 *   ○ if hash(key) is full use linear probing to find the
	 *     next available bucket
	 * 
	 * @param key
	 * @param newValue
	 */
	public void put(K key, V newValue) {
		int i = 0;
		int count = 0;
		for(i = hash(key); Keys[i] != null; i = (i+1) % capacity) {
			if(Keys[i].equals(key)) {
				Values[i] = newValue;
				return;
			}
			count++;
		}
		Keys[i] = key;
		Values[i] = newValue;
		if(count > maxCount) { 
			maxCount = count;
		}
		counts[count]++;
		entries++;
	}
	
	/**
	 * Return a value for the specified key from the bucket hash(searchKey)  
	 * 
	 * ○ if hash(searchKey) doesn’t contain the value use linear probing 
	 *   to find the  appropriate value 
	 *   
	 * @param key
	 * @return key, or null if no key found
	 */
	public V get(K key) {
		int i = 0;
		if(key == null) {
			return null;
		}
		for(i = hash(key); Keys[i] != null; i = (i+1) % capacity) {
			if(Keys[i].equals(key)) {
				return Values[i];
			}
		}
		return null;
	}
	
	/**
	 * Return true if there is a value stored for searchKey 
	 * 
	 * @param findKey
	 * @return boolean true/false
	 */
	public boolean containsKey(K key) {
		return get(key)!= null;
	}
	
	/**
	 * A function that displays the following stat block for the data in your hash table:
	 * Hash Table Stats 
	 * ================  
	 * Number of Entries: 22690  
	 * Number of Buckets: 32768  
	 * Histogram of Probes: 
	 * 
	 * [14591, 3419, 1510, 859, 479, 337, 238, 169, 166, 100, 
	 * 	90,  78, 53, 42, 51, 54, 29, 28, 18, 17, 21, 20, 17, 15,
	 * 	12, 10, 12, 11, 6, 4, 12, 4, 9, 13,  5, 4, 7, 0, 0, 3, 2,
	 *  3, 2, 3, 5, 1, 3, 2, 1, 2, 6, 2, 1, 3, 1, 1, 2, 3, 3, 0,
	 *  2, 2, 1, 1, 1, 1,  2, 4, 3, 2, 1, 0, 2, 1, 3, 0, 0, 2, 2,
	 *  .........................0, 0, 0, 0, 0,............etc. ]  
	 * 
	 * Fill Percentage: 69.244385%  
	 * Max Linear Prob: 533  
	 * Average Linear Prob: 3.434773    
	 * 
	 * ○ A histogram of probes shows how many keys are found after a certain
	 * number of  probes.  In this example data there are 22690 words.  
	 * 14591 of them can be  found in the bucket they belong in.  
	 * 3419 of them can be found after one linear  probe.  
	 * 1510 of them can be found after two linear probes and so on.  
	 * The worst  key is found after 533 probes in my implementation so my 
	 * histogram has 533  entries. 
	 */
	public void stats() {
		StringBuilder stats = new StringBuilder("Hash Table Stats" + "\n================\n");
		stats.append("Number of Entries: " + entries + "\n");
		stats.append("Number of Buckets: " + capacity + "\n");
		stats.append("Histogram of Probes: ");
		for(int i = 0; i <= maxCount; i++) {
			if(i==0) {
				stats.append("\n[");
			}
			stats.append(counts[i] + ", ");
			if(i % 20 == 0 && i > 0) {
				stats.append("\n");
			}
			
			if(i==maxCount) {
				stats.append("]\n");
			}
		}
		stats.append("Fill Percentage: " + ((float) entries/capacity) * 100 + "%" + "\n");
		stats.append("Max Linear Probe: " + maxCount + "\n");
		float sum = 0;
		for(int i = 0; i<= maxCount; i++) {
			sum += counts[i]*i;
		}
		stats.append("Average Linear Probe: " + sum/entries + "\n");
		System.out.println(stats.toString());
	}
	
	/**
	 * A method that converts the hash table contents to a String. 
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder("String: Code" + System.lineSeparator());
		for(int i =0; i<Keys.length; i++) {
			if(Keys[i] != null) {
				sb.append(Keys[i] + ": " + Values[i] + System.lineSeparator());
			}
		}
		return sb.toString();
	}
	
	/**
	 * A​ private method that takes a key and returns an int in the range [0…capacity].  
	 * @param key
	 * @return int in the range [0...capacity]
	 */
	private int hash(K key) {
		return (Math.abs(key.hashCode() % capacity));
	}
}
