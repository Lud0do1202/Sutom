package models;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Sutom {

	private ArrayList<String> dico;
	private ArrayList<Integer> spotsOK;
	private String word;
	private int wordLength;

	public Sutom(String pathDico) throws FileNotFoundException {
		// Read the dico and fill the ArrayList
		File file = new File(pathDico);
		Scanner scanner = new Scanner(file);
		dico = new ArrayList<>();

		while (scanner.hasNext())
			dico.add(scanner.next());
		scanner.close();
	}

	public Sutom(URL resource) throws FileNotFoundException {
		this(resource.toString());
	}

	// Pick up randomly a word in the dico
	public void pickUpRandomWord() {
		// Pick up a word
		word = dico.get(new Random().nextInt(dico.size()));
		word = word.toUpperCase();
		wordLength = word.length();

		// Spots OK
		spotsOK = getEmptySpots();
	}

	// Check if the word is correct
	// 0 -> Wrong
	// 1 -> Wrong place
	// 2 -> OK
	// Example : 
	// 		word	SUCCESS
	//		guess	ACCOUNT
	//		spots  [0120100]
	public ArrayList<Integer> checkGuess(String guess) {

		String saveWord = word;

		// Check if the word exists
		if (!dico.contains(guess.toLowerCase()))
			return null;

		// Init spots (all WRONG)
		ArrayList<Integer> spots = getEmptySpots();

		// OK
		for (int i = 0; i < wordLength; i++) {
			if (saveWord.charAt(i) == guess.charAt(i)) {
				// Set in spot
				spots.set(i, 2);
				spotsOK.set(i, 2);

				// Update guess
				saveWord = PersoString.setCharAt(saveWord, i, '.');
				guess = PersoString.setCharAt(guess, i, '.');
			}
		}

		// Wrong Place
		for (int i = 0; i < wordLength; i++) {
			if (saveWord.charAt(i) != '.') {
				if (saveWord.contains(guess.charAt(i) + "")) {
					// Set in spot
					spots.set(i, 1);

					// Update guess
					saveWord = PersoString.setCharAt(saveWord, saveWord.indexOf(guess.charAt(i)), '.');
					guess = PersoString.setCharAt(guess, i, '.');
				}
			}
		}

		return spots;
	}

	// All spots wrong
	public ArrayList<Integer> getEmptySpots() {
		ArrayList<Integer> spots = new ArrayList<>(wordLength);
		for (int i = 0; i < wordLength; i++)
			spots.add(0);
		return spots;
	}

	// GETTER
	public String getWord() {
		return word;
	}

	public ArrayList<Integer> getSpotsOK() {
		return spotsOK;
	}
}
