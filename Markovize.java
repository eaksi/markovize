package markovize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;


/* Generate random names with Markov chains, with base name lists from 1990 US census data:
	http://www.census.gov/topics/population/genealogy/data/1990_census/1990_census_namefiles.html
*/
public class Markovize {

	private static Vector<String> wordList = new Vector<String>();
	
	public static void main(String args[]) {
				
		openFile("dist.male.first_trimmed"," ",true);
		
		MarkovChain chain = new MarkovChain();
		
		for (int i=0; i<wordList.size(); i++) {
			String word = wordList.get(i);
			int prev = 0;
			for (int j=0; j < word.length(); j++) {
				char tmpChar = word.charAt(j);
				int cur = 0;
				if (tmpChar >= 'A' && tmpChar <= 'Z') {
					cur = tmpChar-'A'+1;
				}
				chain.learn(prev, cur);
				prev = cur;
			}
			chain.learn(prev,0);
		}
		
		chain.normalize();
				
		for (int i=0; i<20; i++) {
			System.out.println(chain.getOutput());
		}
		
	} // end main

	public static void printList(){
		for (int i=0; i<wordList.size(); i++) {
			System.out.println(wordList.get(i));
		}
	}
	
	// trim lines at trimAt character/string, trim whitespace, to uppercase (optional), add to wordList
	public static void openFile(String sourcePath, String trimAt, boolean uppercase) {
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(sourcePath));
			line = br.readLine();
			while (line != null) {
				int firstTrimChar = line.indexOf(trimAt);
				if (firstTrimChar != -1) { //if trimAt string found
					line = line.substring(0,line.indexOf(trimAt));
				}
				line = line.trim(); // trim whitespace
				if (uppercase) {
					line = line.toUpperCase();
				}
				if (line.length() != 0) {
					wordList.add(line); // add line to wordList
				}
				
				line = br.readLine(); // read the next line
			}
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException at trimFile: "+sourcePath);
		} catch (IOException ioe) {
			System.err.println("IOException at trimFile: "+sourcePath);
		}
		// finally statement not needed in JDK 7+, autoclose		
	}
	
	// write current wordList to file
	public static void writeListToFile(String destPath) {
		try {
			if (wordList.size() > 0) {

				FileWriter writer = new FileWriter(new File(destPath));
				for (int i=0; i < wordList.size(); i++) {
					// if last line, do not change line (no '\n')
					if (i == wordList.size()-1) {
						writer.write(wordList.get(i));
					} else {
						writer.write(wordList.get(i) + '\n');
					}
				}
				writer.close();
			}
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException at writeListToFile: "+destPath);
		} catch (IOException ioe) {
			System.err.println("IOException at writeListToFile: "+destPath);
		}
	}
	
} // end class Markovize

