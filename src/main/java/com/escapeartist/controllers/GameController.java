package com.escapeartist.controllers;

import com.escapeartist.models.TextParser;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class GameController {
    private JsonObject gameData;
    private final TextParser parser;

    public GameController() {
        parser = new TextParser(gameData);
        loadGameData();
    }

    private void loadGameData() {
        Gson gson = new Gson();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("game_dialogue.json");
        gameData = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.print(gameData.get("command_prompt").getAsString());
            String userInput = scanner.nextLine();
            JsonElement inputElement = parser.cleanInput(userInput);

            JsonObject validInputs = gameData.getAsJsonObject("valid_inputs");

            if (parser.handleQuit(validInputs, inputElement, gameData.get("quit_confirm").getAsString(), gameData.get("command_prompt").getAsString(), gameData.get("invalid_input").getAsString(), scanner)) {
                System.out.println(gameData.get("goodbye_message").getAsString());
                running = false;

            } else {
                if (!parser.isValidInput(validInputs, inputElement)) {
                    System.out.println(gameData.get("invalid_input").getAsString());
                }
            }
            scanner = new Scanner(System.in);
        }
    }

    private boolean getConfirmation(String prompt) {
        Scanner scanner = new Scanner(System.in);
        JsonObject validInputs = gameData.getAsJsonObject("valid_inputs");

        while (true) {
            System.out.println(prompt);
            System.out.print(gameData.get("command_prompt").getAsString());
            String userInput = scanner.nextLine().trim().toLowerCase();
            JsonElement inputElement = new Gson().toJsonTree(userInput);

            if (validInputs.getAsJsonArray("yes").contains(inputElement)) {
                return true;
            } else if (validInputs.getAsJsonArray("no").contains(inputElement)) {
                return false;
            } else {
                System.out.println(gameData.get("invalid_input").getAsString());
            }
        }
    }

    private boolean isValidInput(JsonObject validInputs, JsonElement inputElement) {
        for (String key : validInputs.keySet()) {
            if (validInputs.getAsJsonArray(key).contains(inputElement)) {
                return true;
            }
        }
        return false;
    }
}
