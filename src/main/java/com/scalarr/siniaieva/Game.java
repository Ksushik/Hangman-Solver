package com.scalarr.siniaieva;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class Game {

	private ConsoleConnector consoleConnector;
	private  int remaining;
	@Getter
	private String state;
	@Getter
	private Prisoner prisoner;

	public Game() {
		this.remaining = 7;
		this.consoleConnector = new ConsoleConnector();
		this.state = consoleConnector.initializeGame();
		prisoner = new Prisoner();
	}

	public boolean updateGameStatus(char guess) {
		String oldState = this.state;
		this.state = consoleConnector.updateStatus(guess,remaining, state);
		if (!state.contains("_")) {
			prisoner.getFree();
			finishGame();
		}
		if (remaining == 0) {
			prisoner.die();
			finishGame();
		}
		boolean isGuessed = oldState.equalsIgnoreCase(state);
		if(isGuessed) {
			remaining--;
		}
		return !isGuessed;
	}

	public void finishGame(){
		consoleConnector.cleanUp();
		System.out.println(state);
		if (prisoner.status == Prisoner.Status.DEAD) {
			System.out.println("Mission Unsuccessful!");
		} else {
			System.out.println("!!!!! I'm a winner!!! I'm a champ))!!!");
		}
	}

	@Data
	static class Prisoner {
		public int remaining;
		@Builder.Default
		private Status status = Status.ALIVE;
		private String token;

		private void die(){
			this.status = Status.DEAD;
		}

		private void getFree() {
			this.status = Status.FREE;
		}

		enum Status {
			ALIVE, FREE, DEAD
		}

	}

}
