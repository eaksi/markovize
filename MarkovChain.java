package markovize;

import java.util.Random;
import java.util.ArrayList;

/**
 *	MarkovChain is an object that takes a list of words for text generation using Markov chains.
 *  Also handles the output of generated words.
 */
public class MarkovChain {

	private static Random rnd = new Random();
	private char[] charTable =  {'#','A','B','C','D','E','F','G','H','I','J','K',
		'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private int[][] hitsTable; // non-normalized table, instances of hits
	private float[][] normTable; // normalized table, probabilities
	private boolean normalized = false; // has the table been normalized recently
	private boolean normalizedAtLeastOnce = false; // has the table been normalized (occurences -> probabilities)
	private int orders;
	
	// FIXME: currently orders=2 only
	/**
	 *	Generates a new MarkovChain object with a number or orders (letters preceeding).
	 */
	public MarkovChain(int ord) {
		if (ord == 2) {
			orders = ord;
		} else {
			System.err.println("Warning: current version only allows orders=2.");
			orders = 2;
		}
		
		// FIXME: more elegant solution, for-loop?
		// Math.pow turns the int to Double, must be rounded back to int
		hitsTable = new int[(int)Math.round(Math.pow(charTable.length,orders))][charTable.length];
		normTable = new float[(int)Math.round(Math.pow(charTable.length,orders))][charTable.length];
	}
	
	/**
	 * Learns a single hit. Mostly a convenience method.
	 */
	private void learn(int curr, int next) {
		hitsTable[curr][next]++;
	}

	// FIXME: currently orders=2 only
	/**
	 * Takes a word as parameter, processes and learns it.
	 */
	public void learnWord(String word) {
		if (normalized) {
			System.err.println("Warning: Chain already normalized. Added the world and set normalized to false");
			normalized = false;
		}	

		int prevs[] = new int[orders];

		// initialize temp array
		for (int i = 0; i < orders; i++) {
			prevs[i] = 0;
		}

		for (int i = 0; i < word.length(); i++) {
			char tmpChar = word.charAt(i);
			int cur = 0;
			
			if (tmpChar >= 'A' && tmpChar <= 'Z') {
				cur = tmpChar-'A'+1;
			}
			
			learn(ordersArrayToInt(prevs), cur);
			/*************************/
			prevs[0] = prevs[1]; //FIXME
			prevs[1] = cur; // FIXME
			/***************************/
			//learn(ordersArrayToInt(prevs),0);
		}	

		if (word.length() != 0) { 	// test to prevent 0 letter words from happening
			learn(ordersArrayToInt(prevs),0); // end of word
		}
	}
	
	/**
	 * Learns all words from the word list (ArrayList)
	 */
	public void learnWords(ArrayList<String> wordList) {
		for (int i=0; i < wordList.size(); i++) {
			learnWord(wordList.get(i));
		}
		System.out.println("*Words learnt!*");
	}
	
	// FIXME: set to private after debugging orders>2
	/**
	 *	Turn the array of ints to one int (when multiple orders).
	 */
	private int ordersArrayToInt(int[] nrs) {
		int sum = 0;
		for (int i = 0; i < nrs.length; i++) {
			 sum = (sum * charTable.length) + nrs[i];
		}
		return sum;
	}
	 
	// FIXME: set to private after debugging orders>2, set ord to orders
	/**
	 * Turns the int into an array of ints (when multiple orders)
	 */
	public int[] intToOrdersArray(int nr, int ord) {
		int[] separated = new int[ord];
		int multiplier = charTable.length;
		for (int i = ord - 1; i >= 0; i--) {
			separated[i] = nr % multiplier;
			nr /= multiplier;
		}
		return separated;
	}

	public int[] intToOrdersArray(int nr) {
		return intToOrdersArray(nr,orders);
	}
	
	/**
	 * Normalizes the chain, needs to be called once before getting a markov output.
	 * Calculates hits from hitsTable into probabilities for normTable.
	 */ 
	public void normalize() {
		System.out.println("*Start Normalize*");
		if (!normalized) {
			normalizedAtLeastOnce = true;
			normalized = true;
			for (int i=0; i<hitsTable.length; i++) {
				float sum = 0;
				for (int j=0; j<hitsTable[i].length; j++) {
					sum += hitsTable[i][j];
				}
				for (int j=0; j<hitsTable[i].length; j++) {
					normTable[i][j] = hitsTable[i][j] / sum;
				}
			}
		} else {
			System.err.println("Warning: Chain already normalized with current word list");
		}
		System.out.println("*End Normalize*");
	}
	
	/**
	 * Gets the next element of the chain (output from normalized chain),
	 * called only from getOutput().
	 */
	private int next(int curpos) {
		float randomNr = rnd.nextFloat();
		for (int i=0; i<normTable[curpos].length; i++) {
			if (normTable[curpos][i] > randomNr) {
				return i; // next letter
			}
			randomNr -= normTable[curpos][i];
		}
		return 0; // end of word
	}
		 
	// FIXME: currently orders=2 only
	// TODO: set possible character limit for words and test 
	/**
	 * Gets the randomized output of current Markov chain.
	 */ 
	public String getOutput() {
		if (normalizedAtLeastOnce) {
			if (!normalized) { 
				System.err.println("Warning: words added since last normalization");
			}
			String markovWord = "";
			int cur = 0; // every word starts at 0
			int beforeCur = 0; // FIXME: bad, refactor to orders != 2 
			int nxt = 0;
			do {
				nxt = next(beforeCur*charTable.length + cur);
				beforeCur = cur;
				cur = nxt;
				
				if (nxt > 0) {
					markovWord += (char)(nxt+'a'-1);
				}
				
			} while (nxt != 0);

			// Capitalize string
			return Character.toUpperCase(markovWord.charAt(0)) + markovWord.substring(1);

		} else {
			System.err.println("Error: Chain not yet normalized! returning output 'FOOBAR'");
			return "FOOBAR";
		}
	}
}
