package markovize;

import java.util.Random;
import java.util.Vector;

public class MarkovChain {	
	/* SYNTAX
		#A = begins with A
		A# = ends with A
		AC = C follows A
		TODO: ABC = C follows AB (if 2 last characters)
	*/
	private static Random rnd = new Random();
	private char[] charTable =  {'#','A','B','C','D','E','F','G','H','I','J','K',
		'L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
	private float[][] pairTable;
	private boolean normalized = false;
	
	public MarkovChain() {		
		pairTable = new float[charTable.length][charTable.length];
	}
	
	// learn a single pair
	private void learn(int curpos, int next) {
		if (!normalized) {
			pairTable[curpos][next]++;
		} else {
			System.err.println("Warning: Chain already normalized! Ignoring learn(int,int).");
		}
	}
	
	// learn a word
	public void learnWord(String word) {
		if (!normalized) {
			int prev = 0;
			for (int i=0; i < word.length(); i++) {
				char tmpChar = word.charAt(i);
				int cur = 0;
				if (tmpChar >= 'A' && tmpChar <= 'Z') {
					cur = tmpChar-'A'+1;
				}
				learn(prev, cur);
				prev = cur;
			}
			learn(prev,0);
		} else {
			System.err.println("Warning: Chain already normalized! Ignoring learnWord(String).");
		}
	}
	
	// learn all words from a Vector<String>
	public void learnWords(Vector<String> wordList) {
		for (int i=0; i<wordList.size(); i++) {
			learnWord(wordList.get(i));
		}
	}
	
	// debug method, dumps the current chain
	public void printChain() {
		System.out.println("Normalized: "+ normalized);
		for (int i=0; i<pairTable.length; i++) {
			for (int j=0; j<pairTable[i].length; j++) {
				System.out.println(pairTable[i][j]);
			}
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
//		System.out.println("nextloop: "+i+" "+randomNr+" "+pairTable[curpos][i]); //debug
			if (pairTable[curpos][i] > randomNr) {
				return i;
			}
			randomNr -= pairTable[curpos][i];
		}
		return 0;
	}
	
	// get the randomized output of current Markov chain
	public String getOutput() {
		if (normalized) {
			String res = "";
			int cur = 0;
			do {
				cur = next(cur);
				if (cur > 0) {
					if (res == "") { // Capitalize the name
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
