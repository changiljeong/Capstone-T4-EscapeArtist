package com.escapeartist.controllers;

import com.escapeartist.models.*;
import com.escapeartist.util.Clear;
import com.escapeartist.util.GameMusic;
import com.escapeartist.util.GsonDeserializer;
import com.escapeartist.util.MusicController;
import com.escapeartist.views.GUI;
import com.escapeartist.views.MainView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MainController {
    private MainView mainView;
    private GameController gameController;
    private GameMusic gameMusic;
    private MusicController musicController;
    private GUI gui;

    public MainController() {
        mainView = new MainView();
        gameMusic = new GameMusic();
        musicController = new MusicController(gameMusic);
        gui = new GUI();

        GsonDeserializer deserializer = new GsonDeserializer();
        JsonObject gameData = new JsonObject();
        gameData.add("dialogue", new Gson().toJsonTree(deserializer.deserializeGameDialogue()));
        gameData.add("locations", new Gson().toJsonTree(deserializer.deserializeLocations()));
        gameData.add("items", new Gson().toJsonTree(deserializer.deserializeItems()));
        gameData.add("npcs", new Gson().toJsonTree(deserializer.deserializeNPCs()));
        gameController = new GameController(gameData, gui);
        displayGameIntro();
        startMenu();
    }

    public void startMenu() {
        displayGameIntro();
        boolean validInput = false;
        mainView.showGameInfo();
        gameMusic.playMusic();

        while (!validInput) {
            mainView.showWelcomeMessage();

            String userInput = String.valueOf(mainView.getUserInput());

            if (userInput.trim().isEmpty()) {
                Clear.clearConsole(); // Clear the console when "Return" key is pressed.
                continue;
            }

            String validCommand = mainView.isValidInput(userInput);
            if (!validCommand.isEmpty()) {
                switch (validCommand) {
                    case "new game":
                    case "new":
                        newGame();
                        validInput = true;
                        break;
                    case "music":
                        musicController.showMenu();
                        break;
                    case "quit":
                    case "exit":
                        quitGame();
                        validInput = true;
                        break;
                    default:
                        break;
                }
            } else {
                mainView.clear();
                mainView.printMessage("invalid_option");
            }
        }
    }

    private void quitGame() {
        gameMusic.stopMusic();
        mainView.printMessage("goodbye_message");
        System.exit(0);
    }

    private void newGame() {
        mainView.clear();
        mainView.printMessage("new_game_start");
        gameController.run();
    }

    public void displayGameIntro() {
        String introText = mainView.readTitleScreenFile() +"\n\n" +
            "Welcome to the Escape Artist! \n\n" +
            "Here are the instructions:\n" +
            "1. Use the buttons to navigate through the game.\n" +
            "2. Interact with NPCs and items using the text input.\n" +
            "3. Solve puzzles, fight enemies and complete tasks to progress.\n\n" +
            "Good luck and have fun!";
        gui.showGameIntro(introText);
    }
}
