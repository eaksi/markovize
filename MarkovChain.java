package markovize;

import java.util.Random;
import java.util.Vector;

public class MarkovChain {

	private static Random rnd = new Random();
	private char[] charTable =  {'#','A','B','C','D','E','F','G','H','I','J','K',
		'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private float[][] pairTable;
	private boolean normalized = false; // has the table been normalized (occurences -> probabilities)
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
		pairTable = new float[(int)Math.round(Math.pow(charTable.length,orders))][charTable.length];
	}
	
	// learn a single pair
	private void learn(int curr, int next) {
		if (!normalized) {
			pairTable[curr][next]++;
		} else {
			System.err.println("Warning: Chain already normalized! Ignoring learn(int,int).");
		}
		
		
	}

//FIXME: orders>1
	// learn a word 
	public void learnWord(String word) {
		if (!normalized) {
			int prev = 0;
			for (int i=0; i < word.length(); i++) { //fine 
				char tmpChar = word.charAt(i); //fine
				int cur = 0; //fine
				
				if (tmpChar >= 'A' && tmpChar <= 'Z') { //fine
					cur = tmpChar-'A'+1; //fine
				}
				learn(prev, cur);
				prev = cur;
			}
			learn(prev,0); //fine
		} else {
			System.err.println("Warning: Chain already normalized! Ignoring learnWord(String).");
		}
	}
	
	public int ordersArrayToInt(int[] nrs) { //FIXME: set to private after debugging
		int combined = 0;
		int multiplier = 1;
		for (int i=0; i < nrs.length; i++) {
			if (nrs.length-i == 0) {
				combined += nrs[i];
			} else {
				combined += multiplier * nrs[i];
			}
			
			multiplier *= charTable.length;
		}
		return combined;
	}
	
	
	// learn all words from a Vector<String>
	public void learnWords(Vector<String> wordList) {
		for (int i=0; i < wordList.size(); i++) {
			learnWord(wordList.get(i));
		}
	}
	
//FIXME: orders>1
	// debug method, dumps the current chain
	public void printChain() {
		System.out.println("Normalized: "+ normalized);
		for (int i=0; i<pairTable.length; i++) {
			for (int j=0; j<pairTable[i].length; j++) {
				System.out.print(charTable[i]+""+charTable[j]+": "+pairTable[i][j]+"; ");
			}
			System.out.print("\n");
		}
	}
	
	// replace pairTable occurences with probabilities
	public void normalize() {
		if (!normalized) {
			normalized = true;
			for (int i=0; i<pairTable.length; i++) {
				float sum = 0;
				for (int j=0; j<pairTable[i].length; j++) {
					sum += pairTable[i][j];
				}
				for (int j=0; j<pairTable[i].length; j++) {
					pairTable[i][j] /= sum;
				}
			}
		} else {
			System.err.println("Warning: Chain already normalized! Ignoring second normalization.");
		}
	}
	
	// get next element of the chain
	private int next(int curpos) {
		float randomNr = rnd.nextFloat();
		for (int i=0; i<pairTable[curpos].length; i++) {
			if (pairTable[curpos][i] > randomNr) {
				return i; // next letter
			}
			randomNr -= pairTable[curpos][i];
		}
		return 0; // end of word
	}
	
	// get the randomized output of current Markov chain
	public String getOutput() {
		if (normalized) {
			String res = "";
			int cur = 0; // every word starts at 0
			do {
				cur = next(cur);
				if (cur > 0) {
					if (res == "") { // capitalization
							res += (char)(cur+'A'-1);
					} else {
						res += (char)(cur+'a'-1);
					}
				}				
			} while(cur != 0);
			return res;
		} else {
			System.err.println("Warning: Chain not yet normalized! returning output 'foobar'");
			return "foobar";
		}
	}
	
}
