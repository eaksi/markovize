package markovize;

import java.util.Random;
import java.util.Vector;

public class MarkovChain {

	private static Random rnd = new Random();
	private char[] charTable =  {'#','A','B','C','D','E','F','G','H','I','J','K',
		'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private float[][] hitsTable; // non-normalized table, instances of pair hits //FIXME: change to int array
	private float[][] normTable; // normalized table, probabilities
	private boolean normalized = false; // has the table been normalized recently
	private boolean normalizedAtLeastOnce = false; // has the table been normalized (occurences -> probabilities)
	private int orders;
	
	// generate a new MarkovChain object with a number or orders (letters preceeding)
	public MarkovChain(int ord) {
		if (ord >= 1) {
			orders = ord;
		} else {
			System.err.println("Warning: number of orders < 1, defaulting to 1.");
			orders = 1;
		}
		
		// Math.pow turns the int to Double, must be rounded back to int //FIXME: more elegant solution, for-loop?
		hitsTable = new float[(int)Math.round(Math.pow(charTable.length,orders))][charTable.length];
		normTable = new float[(int)Math.round(Math.pow(charTable.length,orders))][charTable.length];
	}
	
	// learn a single hit
	private void learn(int curr, int next) {
		if (normalized) {
			System.err.println("Warning: Chain already normalized, but adding to hits table.");
		}	
		hitsTable[curr][next]++;
	}

	// FIXME: currently orders=2 only
	// learn a word
	public void learnWord(String word) {
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
			prevs[0] = prevs[1]; //FIXME
			prevs[1] = cur; // FIXME
			//learn(ordersArrayToInt(prevs),0);
		}	

		if (word.length() != 0) { 	// test to prevent 0 letter words from happening
			learn(ordersArrayToInt(prevs),0); // end of word
		}
	}
	
	// learn all words from a Vector<String>
	public void learnWords(Vector<String> wordList) {
		for (int i=0; i < wordList.size(); i++) {
			learnWord(wordList.get(i));
		}
		System.out.println("Words learnt!");
	}
	
	// FIXME: set to private after debugging orders>2
	// turn the array of ints to one int (when multiple orders)
	public int ordersArrayToInt(int[] nrs) {
		int sum = 0;
		for (int i=0; i < nrs.length; i++) {
			 sum = (sum * charTable.length) + nrs[i];
		}
		return sum;
	}
	 
	// FIXME: set to private after debugging orders>2, set ord to orders
	// turn the int into an array of ints (when multiple orders)
	public int[] intToOrdersArray(int nr, int ord) {
		int[] separated = new int[ord];
		int multiplier = charTable.length;
		for (int i = ord-1; i >= 0; i--) {
			separated[i] = nr % multiplier;
			nr /= multiplier;
		}
		return separated;
	}

	public int[] intToOrdersArray(int nr) {
		return intToOrdersArray(nr,orders);
	}
	
	// FIXME: rewrite entirely
	// debug method, dumps the current chain
	public void printChain() {
		System.out.println("Normalized: "+ normalized);
/*		for (int i=0; i<normTable.length; i++) {
			for (int j=0; j<normTable[i].length; j++) {
				System.out.print(charTable[i]+""+charTable[j]+": "+normTable[i][j]+"; ");
			}
			System.out.print("\n");
		}*/

	}
	
	// calculate hits from hitsTable into probabilities for normTable
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
	
	// get next element of the chain (output from normalized chain), called only from getOutput()
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
	// get the randomized output of current Markov chain
	public String getOutput() {
		if (normalizedAtLeastOnce) {
			if (!normalized) { 
				System.err.println("Warning: words added since last normalization");
			}
			String res = "";
			int cur = 0; // every word starts at 0
			int beforeCur = 0;
			int nxt = 0;
			do {
				nxt = next(beforeCur*charTable.length + cur);
				//System.out.println("sttr " + charTable[beforeCur] +","+ charTable[cur] + " -> "+charTable[nxt]);
				beforeCur = cur;
				cur = nxt;
				
				if (nxt > 0) {
					if (res == "") { // capitalization
							res += (char)(nxt+'A'-1);
					} else {
						res += (char)(nxt+'a'-1);
					}
				}				
			} while(nxt != 0 /*&& res.length() < 20*/); //TODO: character limits
			return res;
		} else {
			System.err.println("Error: Chain not yet normalized! returning output 'foobar'");
			return "foobar";
		}
	}
}
