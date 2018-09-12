package com.scalarr.siniaieva;

import java.util.Scanner;


public class ConsoleConnector {
    private Scanner sc;

    public ConsoleConnector() {
        sc = new Scanner(System.in);
    }

    public String initializeGame() {
        System.out.println("Let's start! Please selects a secret word, use `_` for closed letters");
        String initialState = sc.nextLine();
        return initialState;
    }

    public String updateStatus(char guess, int remaining, String state) {
        System.out.println("Current state: " + state + " (length = " + state.length() + ")");
        System.out.println("Show must go on, I still have " + remaining + " tries!");
        System.out.println("\n     THE NEXT LETTER IS ***  " + guess + " *** \n");
        System.out.println("Please, enter the current state in the form of a mask of a secret word with open guessed letters");
        return sc.nextLine();

    }

    public void cleanUp() {
        sc.close();
    }

}
