package com.escapeartist.controllers;

import com.escapeartist.views.MainView;

public class MainController {
    // Hold an instance of MainView
    private MainView mainView;

    // Constructor initializes the MainView instance
    public MainController() {
        mainView = new MainView();
    }

    // Start the game loop, prompting the user for input until a valid input is provided
    public void startMenu() {
        boolean validInput = false;

        while (!validInput) {
            // Show the welcome message and options
            mainView.showWelcomeMessage();
            String userInput = String.valueOf(mainView.getUserInput());

            // Check if the user input is valid
            if (mainView.isValidInput(userInput)) {
                // Start a new game
                newGame();
                // Exit the loop
                validInput = true;
            } else {
                mainView.clear();
                // Show the invalid option message
                mainView.printMessage("invalid_option");
            }
        }
    }

    // Starts a new game
    private void newGame() {
        mainView.clear();
        mainView.printMessage("new_game_start");
    }
}
