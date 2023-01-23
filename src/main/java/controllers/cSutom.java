package controllers;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import models.Sutom;

public class cSutom implements Initializable {

	// Var
	private int attemp, maxAttemps;
	private int posLetter, maxLetters;
	private Sutom sutom;
	private String defaultStyleTextField;
	private boolean canPlay = false;

	// FXML
	@FXML
	Text error, wonOrLost, infoGame;
	@FXML
	GridPane grid;
	@FXML
	Pane fade;
	@FXML
	Button playButton;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// Save style text fields
		defaultStyleTextField = getTextFieldAt(0).getStyle();

		// Set max value
		maxAttemps = grid.getRowCount();
		maxLetters = grid.getColumnCount();

		// Create sutom
		try {
			sutom = new Sutom("src/main/java/models/dico.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	// PLAY
	@FXML
	private void play(ActionEvent playEvent) throws FileNotFoundException {
		// Visibility FALSE
		playButton.setVisible(false);
		fade.setVisible(false);
		wonOrLost.setVisible(false);
		infoGame.setVisible(false);

		// Reset grid
		grid.getChildren().forEach(tf -> {
			((TextField) tf).setText("");
			((TextField) tf).setStyle(defaultStyleTextField);
		});

		// Reset var
		attemp = 0;
		posLetter = 0;
		canPlay = true;

		// Select a word
		sutom.pickUpRandomWord();

		// Display first line
		displayLine(sutom.getEmptySpots());

		// Global key pressed
		grid.getScene().setOnKeyPressed((KeyEvent keyEvent) -> {
			keyPressed(keyEvent);
		});
	}

	// Global key pressed
	private void keyPressed(KeyEvent event) {		
		// Cannot Play
		if (!canPlay)
			return;

		// Get the key code
		KeyCode key = event.getCode();

		// Check if [A-Z] || BACKSPACE || ENTER
		if (key != KeyCode.BACK_SPACE && key != KeyCode.ENTER
				&& (key.ordinal() < KeyCode.A.ordinal() || key.ordinal() > KeyCode.Z.ordinal()))
			return;

		// Operations
		switch (key) {
		case ENTER:
			checkWord();
			break;

		case BACK_SPACE:
			deleteLastLetter();
			break;

		default: // Letter
			addLetter(key);
			break;
		}
	}

	// Check if the word is correct
	private void checkWord() {

		// Number of letter too small
		if (posLetter < maxLetters) {
			error.setText("!!! The word must have " + maxLetters + " letters !!!");
			return;
		}

		// Get data for knowing if OK
		List<Integer> spots = sutom.checkGuess(getGuess());

		// Not Found
		if (spots == null) {
			error.setText("!!! The word is not in my dictionnary !!!");
			return;
		}
		
		// NO ERROR
		error.setText("");

		// Spots + setStyle
		for (int i = 0; i < maxLetters; i++) {
			switch (spots.get(i)) {
			case 1: // Wrong Place
				getTextFieldAt(i).setStyle("-fx-background-color:gold");
				break;

			case 2: // OK
				getTextFieldAt(i).setStyle("-fx-background-color:red");
				break;
			}
		}

		// WON----
		if (getGuess().equals(sutom.getWord())) {
			won();
			return;
		}

		// Attemps
		attemp++;
		posLetter = 0;

		// LOST---
		if (attemp == maxAttemps) {
			lost();
			return;
		}

		// Display new line
		displayLine(sutom.getSpotsOK());
	}

	// Delete the last letter
	private void deleteLastLetter() {
		// Check is beginning line
		if (posLetter == 0)
			return;
		
		// Decrement
		posLetter--;

		// Reset style
		if (posLetter == 0)
			displayLine(sutom.getSpotsOK());

		// Set default text
		else
			getTextFieldAt(posLetter).setText(".");
	}

	// Add a letter
	private void addLetter(KeyCode key) {
		String letter = key.name();

		// Check guess.length == max
		if (posLetter == maxLetters)
			return;

		// Reset style if first letter
		else if (posLetter == 0)
			displayLine(sutom.getEmptySpots());

		// Display Letter
		getTextFieldAt(posLetter).setText(letter);

		// Increment
		posLetter++;
	}

	// Won
	private void won() {
		// Visibility TRUE
		fade.setVisible(true);
		wonOrLost.setVisible(true);
		infoGame.setVisible(true);
		playButton.setVisible(true);

		// Edit Text
		wonOrLost.setText("WON");
		infoGame.setText("You found '" + sutom.getWord() + "' in " + (attemp + 1) + " attemps");
		playButton.setText("RESTART");

		// Style
		wonOrLost.setFill(Paint.valueOf("green"));

		// Cannot keep going
		canPlay = false;
	}

	// Lost
	private void lost() {
		// Visibility TRUE
		fade.setVisible(true);
		wonOrLost.setVisible(true);
		infoGame.setVisible(true);
		playButton.setVisible(true);

		// Edit Text
		wonOrLost.setText("LOST");
		infoGame.setText("The word to found was '" + sutom.getWord() + "'");
		playButton.setText("RESTART");

		// Style
		wonOrLost.setFill(Paint.valueOf("red"));

		// Cannot keep going
		canPlay = false;
	}

	/**********************************************************/
	/**********************************************************/

	// Get the guess
	private String getGuess() {
		StringBuilder sb = new StringBuilder(maxLetters);

		// Get all the line of the grid
		for (int i = 0; i < maxLetters; i++)
			sb.append(getTextFieldAt(i).getText());
		return sb.toString();
	}

	// Return the i TextField from grid
	private TextField getTextFieldAt(int x) {
		return (TextField) grid.getChildren().get(attemp * maxLetters + x);
	}

	// Display line
	private void displayLine(List<Integer> spots) {

		for (int i = 0; i < maxLetters; i++) {
			// OK
			if (spots.get(i) == 2) {
				getTextFieldAt(i).setStyle("-fx-background-color:red");
				getTextFieldAt(i).setText(sutom.getWord().charAt(i) + "");
			}

			// Wrong places
			else if (spots.get(i) == 1) {
				getTextFieldAt(i).setStyle("-fx-background-color:gold");
				getTextFieldAt(i).setText(sutom.getWord().charAt(i) + "");
			}

			// Always show first letter
			else if (i == 0) {
				getTextFieldAt(i).setStyle(defaultStyleTextField);
				getTextFieldAt(i).setText(sutom.getWord().charAt(0) + "");
			}

			// Wrong
			else {
				getTextFieldAt(i).setStyle(defaultStyleTextField);
				getTextFieldAt(i).setText(".");
			}
		}
	}
}
