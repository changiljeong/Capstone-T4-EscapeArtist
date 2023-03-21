package com.escapeartist.controllers;

import com.escapeartist.models.GameDialogue;
import com.escapeartist.models.Player;
import com.escapeartist.views.GameView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Scanner;
import com.escapeartist.models.TextParser;

public class GameController {

    private JsonObject gameData;
    private TextParser textParser;
    private Player player;
    private GameView gameView;
    private int currentLocationId;

    public GameController() {
        loadGameData();
        textParser = new TextParser(gameData);
        gameView = new GameView(gameData);
        player = new Player(100, 10,
            5); // Initialize player with starting HP, attack, and defense values
        currentLocationId = 1; // Start in Museum Lobby
    }

    private void loadGameData() {
        //noinspection DataFlowIssue
//        try(Reader reader = new InputStreamReader(getClass().getClassLoader().getResourceAsStream("game_dialogue.json"))) {
//            Gson gson = new Gson();
//            GameDialogue dialogue = gson.fromJson(reader, GameDialogue.class);
//            System.out.println();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        Gson gson = new Gson();
        InputStream dialogueInputStream = getClass().getClassLoader()
            .getResourceAsStream("game_dialogue.json");
        InputStream locationsInputStream = getClass().getClassLoader()
            .getResourceAsStream("locations.json");
        JsonObject dialogueData = gson.fromJson(new InputStreamReader(dialogueInputStream),
            JsonObject.class);
        JsonObject locationsData = gson.fromJson(new InputStreamReader(locationsInputStream),
            JsonObject.class);

        gameData = new JsonObject();
        gameData.add("dialogue", dialogueData);
        gameData.add("locations", locationsData.getAsJsonArray("locations"));
    }


    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            JsonObject currentLocation = getLocationById(currentLocationId);
            gameView.displayLocation(currentLocation);
            System.out.print(
                gameData.getAsJsonObject("dialogue").get("command_prompt").getAsString());
            String userInput = scanner.nextLine();
            String cleanedInput = textParser.cleanUserInput(userInput);
            JsonElement inputElement = new Gson().toJsonTree(cleanedInput);

            if (textParser.isQuitCommand(inputElement)) {
                boolean confirmQuit = textParser.getConfirmation(
                    gameData.getAsJsonObject("dialogue").get("quit_confirm").getAsString());

                if (confirmQuit) {
                    System.out.println(
                        gameData.getAsJsonObject("dialogue").get("goodbye_message").getAsString());
                    running = false;
                }
            } else if (textParser.isHelpCommand(inputElement)) {
                System.out.println(
                    gameData.getAsJsonObject("dialogue").get("help_menu").getAsString());
            } else if (textParser.isGoCommand(inputElement)) {
                moveLocation(userInput, currentLocationId);

            } else if (textParser.isLookCommand(inputElement)){
                lookItem(userInput, currentLocationId);
                
            }else {
            if (!textParser.isValidInput(inputElement)) {
                    System.out.println(
                        gameData.getAsJsonObject("dialogue").get("invalid_input").getAsString());
                }

                // TODO: Add game logic here
            }
        }
    }

    private JsonObject getLocationById(int locationId) {
        for (JsonElement locationElement : gameData.getAsJsonArray("locations")) {
            JsonObject location = locationElement.getAsJsonObject();
            if (location.get("id").getAsInt() == locationId) {
                return location;
            }
        }
        return null;
    }

    public void moveLocation(String userInput, int currentLocationId) {
        String direction = textParser.getSecondWord(userInput); // Assumes the second word is the direction
        JsonObject currentLocation = gameData.getAsJsonArray("locations").get(currentLocationId - 1).getAsJsonObject();
        JsonObject exits = currentLocation.getAsJsonObject("exits");
        // Check if the direction is a valid exit from the current location
        if (exits.has(direction)) {
            int newLocationId = exits.get(direction).getAsInt();
            JsonObject newLocation = gameData.getAsJsonArray("locations").get(newLocationId - 1).getAsJsonObject();
            setCurrentLocationId(newLocationId);
        } else {
            System.out.println(gameData.getAsJsonObject("dialogue").get("invalid_exit").getAsString());
        }
    }

    public void lookItem(String userInput, int currentLocationId){
        String itemWord = textParser.getSecondWord(userInput); //second word will be an item
        JsonObject currentLocation = getLocationById(currentLocationId);
        JsonArray items = currentLocation.getAsJsonArray("items");
        for(JsonElement item : items) {
            JsonObject itemJson = item.getAsJsonObject();

            if (itemJson.get("item_name").getAsString().equalsIgnoreCase(itemWord)) {
                System.out.println(itemJson.get("item_description").getAsString());
            } else {
                System.out.println(
                    gameData.getAsJsonObject("dialogue").get("invalid_input").getAsString());
            }
        }
    }


    public void setCurrentLocationId(int currentLocationId) {
        this.currentLocationId = currentLocationId;
    }
}
