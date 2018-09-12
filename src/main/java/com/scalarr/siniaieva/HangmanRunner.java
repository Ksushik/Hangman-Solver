package com.scalarr.siniaieva;

import static com.scalarr.siniaieva.Game.Prisoner.Status.ALIVE;


public class HangmanRunner {
	public static void main(String[] args){

		Game game = new Game();
		HangmanGuesser intelligence = new HangmanGuesser(game.getState().length());
		while(game.getPrisoner().getStatus() == ALIVE){

			char guess = intelligence.makeGuess(game.getState());
			boolean isLetterGuessed = game.updateGameStatus(guess);

				intelligence.update(guess, isLetterGuessed);
		}

	}
}
