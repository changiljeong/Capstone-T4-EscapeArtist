package com.escapeartist.controllers;

import com.escapeartist.models.*;
import com.escapeartist.util.Clear;
import com.escapeartist.util.GameMusic;
import com.escapeartist.util.GsonDeserializer;
import com.escapeartist.util.MusicController;
import com.escapeartist.views.MainView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class MainController {
    private MainView mainView;
    private GameController gameController;
    private GameMusic gameMusic;
    private MusicController musicController;

    public MainController() {
        mainView = new MainView();
        gameMusic = new GameMusic();
        musicController = new MusicController(gameMusic);

        GsonDeserializer deserializer = new GsonDeserializer();
        JsonObject gameData = new JsonObject();
        gameData.add("dialogue", new Gson().toJsonTree(deserializer.deserializeGameDialogue()));
        gameData.add("locations", new Gson().toJsonTree(deserializer.deserializeLocations()));
        gameData.add("items", new Gson().toJsonTree(deserializer.deserializeItems()));
        gameData.add("npcs", new Gson().toJsonTree(deserializer.deserializeNPCs()));
        gameController = new GameController(gameData);
    }

    public void startMenu() {
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
}
