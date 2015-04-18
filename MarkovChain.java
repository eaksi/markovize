package markovize;

import java.util.Random;

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
	
	public void learn(int curpos, int next) {
		if (!normalized) {
			pairTable[curpos][next]++;
		} else {
			System.err.println("Warning: ");
		}
		
	}
	
	// debug method, dumps the current chain
	public void printChain() {
		for (int i=0; i<pairTable.length; i++) {
			for (int j=0; j<pairTable[i].length; j++) {
				System.out.println(pairTable[i][j]);
			}
		}
	}
	
	// replace pairTable occurences with probabilities
	public void normalize() {
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
	}
	
	public int next(int curpos) {
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
}







/*	TODO

public class MarkovChain {	
	
	
	public MarkovChain(int alphabetSize, int rememberNChars) {
		
	}
	
	public void learn(int curpos, int next) {
		this.table[curpos][next]++;
	}
	public void normalize() {

	}
	
	public int next(int curpos) {
		return 0;
	}
}
*/

