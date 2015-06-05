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
	
	// FIXME: change to dual arrays
	// learn a single pair
	private void learn(int curr, int next) {
		if (!normalized) {
			pairTable[curr][next]++;
		} else {
			System.err.println("Warning: Chain already normalized! Ignoring learn(int,int).");
		}
		
		
	}


	// learn a word //FIXME: orders=2 only
	public void learnWord(String word) {
		int prevs[] = new int[orders];
		
		if (!normalized) {
			
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
				//System.out.println("["+charTable[prevs[0]]+","+charTable[prevs[1]]+"]->"+charTable[cur]); // debug
				prevs[0] = prevs[1];
				prevs[1] = cur;
				//learn(ordersArrayToInt(prevs),0);

			}
			learn(ordersArrayToInt(prevs),0); // end of word
			//System.out.println("end:["+charTable[prevs[0]]+","+charTable[prevs[1]]+"]->#"); // debug end of word

			
		} else {
			System.err.println("Warning: Chain already normalized! Ignoring learnWord(String).");
		}
	}
	
	// turn the array of ints to one int (when multiple orders)
	public int ordersArrayToInt(int[] nrs) { //FIXME: set to private after debugging
		int sum = 0;
		for (int i=0; i < nrs.length; i++) {
			 sum = (sum * charTable.length) + nrs[i];
		}
		return sum;
	}
	
	// turn the int into an array of ints (when multiple orders)
	public int[] intToOrdersArray(int nr, int ord) { //FIXME: set to private after debugging, set ord to orders
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
	
	
	
	// learn all words from a Vector<String>
	public void learnWords(Vector<String> wordList) {
		for (int i=0; i < wordList.size(); i++) {
			learnWord(wordList.get(i));
		}
		System.out.println("All learned!");
	}
	
	// FIXME: change to dual arrays
	// FIXME: orders>1
	// debug method, dumps the current chain
	public void printChain() {
		System.out.println("Normalized: "+ normalized);
/*		for (int i=0; i<pairTable.length; i++) {
			for (int j=0; j<pairTable[i].length; j++) {
				System.out.print(charTable[i]+""+charTable[j]+": "+pairTable[i][j]+"; ");
			}
			System.out.print("\n");
		}*/

	}
	
	// FIXME: change to dual arrays
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
	
	// get next element of the chain (output from normalized chain)
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
	
	
	 
	// FIXME: currently orders=2 only
	// FIXME: change to dual arrays
	// get the randomized output of current Markov chain
	public String getOutput() {
		if (normalized) {
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
			} while(nxt != 0 /*&& res.length() < 20*/);
			return res;
		} else {
			System.err.println("Warning: Chain not yet normalized! returning output 'foobar'");
			return "foobar";
		}
	}
	
}
