package markovize;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 *	Generate random names with Markov chains, with base name lists from 1990 US census data:
 *	http://www.census.gov/topics/population/genealogy/data/1990_census/1990_census_namefiles.html
 */
public class Markovize {

	private static ArrayList<String> wordList = new ArrayList<String>();
	private static int minLetters = 3;
	private static int nrResults = 20;
	
	public static void main(String args[]) {
		
		System.err.println("***Start***");
		openFile("dist.female.first_trimmed"," ",true);
		
		MarkovChain chain = new MarkovChain(2);
		
		chain.learnWords(wordList);
		
		chain.normalize();
			
		String tmpOutput;
		for (int i = 0; i < nrResults; i++) {
			tmpOutput = chain.getOutput();
			
			while (tmpOutput.length() < minLetters) {
					tmpOutput = chain.getOutput();
			}
			System.out.println(tmpOutput);
		}
		
		System.out.println("***End***");	
	}

	public static void printList() {
		for (int i=0; i<wordList.size(); i++) {
			System.err.println(wordList.get(i));
		}
	}
	
	// FIXME: move to another class, refactor 
	/**
	 *	This method opens a word list file and stores words in wordList.
	 *  1. Trims lines at (trimAt) String
	 *  2. Trims whitespace from lines
	 *  3. converts to uppercase (optional),
	 *	4. adds the result line to wordList.
	 */
	public static void openFile(String sourcePath, String trimAt, boolean uppercase) {
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(sourcePath));
			line = br.readLine();	// read first line of file
			
			while (line != null) {
				
				int firstTrimChar = line.indexOf(trimAt);
				
				if (firstTrimChar != -1) {	//if trimAt string found
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
	}
	
	// FIXME: move to another class
	/**
	 *	Writes the current wordList to file
	 */
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

