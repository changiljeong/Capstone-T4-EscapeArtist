package com.escapeartist.controllers;

import com.escapeartist.models.*;
import com.escapeartist.util.GsonDeserializer;
import com.escapeartist.views.GameView;
import com.escapeartist.util.TextParser;
import com.escapeartist.views.MainView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class GameController {
    private JsonObject gameData;
    private TextParser textParser;
    private Player player;
    private GameView gameView;
    private int currentLocationId;
    private List<Location> locations;
    private GameDialogue gameDialogue;

    public GameController(JsonObject gameData) {
        this.gameData = gameData;
    }

    public void loadGameData() {
        textParser = new TextParser(gameData);
        gameView = new GameView(gameData);
        GsonDeserializer deserializer = new GsonDeserializer();
        locations = deserializer.deserializeLocations();
        List<Item> items = deserializer.deserializeItems();
        List<NPC> npcs = deserializer.deserializeNPCs();
        gameDialogue = deserializer.deserializeGameDialogue();
        JsonObject playerJson = deserializer.deserializePlayerJson();
        player = deserializer.deserializePlayer(playerJson); // Deserialize the player using the JsonObject

        gameData.add("player", new Gson().toJsonTree(player));
        gameData.add("dialogue", new Gson().toJsonTree(gameDialogue));
        gameData.add("locations", new Gson().toJsonTree(locations));
        gameData.add("items", new Gson().toJsonTree(items));
        gameData.add("npcs", new Gson().toJsonTree(npcs));

        this.currentLocationId = player.getCurrentLocation();

    }

    public void run() {
        loadGameData();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            player.playerStatus(gameData);
            Location currentLocation = getLocationById(currentLocationId);
            gameView.displayLocation(new Gson().toJsonTree(currentLocation).getAsJsonObject());
            System.out.print(gameDialogue.getCommandPrompt());
            String userInput = scanner.nextLine();
            String cleanedInput = textParser.cleanUserInput(userInput);
            JsonElement inputElement = new Gson().toJsonTree(cleanedInput);

            if (textParser.isQuitCommand(inputElement)) {
                boolean confirmQuit = textParser.getConfirmation(gameData.getAsJsonObject("dialogue").get("quit_confirm").getAsString());

                if (confirmQuit) {
                    System.out.println(gameData.getAsJsonObject("dialogue").get("goodbye_message").getAsString());
                    running = false;
                }
            } else if (textParser.isHelpCommand(inputElement)) {
                System.out.println(gameData.getAsJsonObject("dialogue").get("help_menu").getAsString());
            } else if (textParser.isGoCommand(inputElement)) {
                moveLocation(userInput, currentLocation);

            } else if (textParser.isLookCommand(inputElement)){
                String secondWord = textParser.getSecondWord(userInput);
                if(currentLocation.getNpcs().stream().anyMatch(npc -> npc.getName().equalsIgnoreCase(secondWord))){
                    lookNpc(userInput, gameData);
                } else if(currentLocation.getItems().stream().anyMatch(item -> item.getName().equalsIgnoreCase(secondWord))){
                    lookItem(userInput, gameData);
                } else{
                    System.out.println(gameDialogue.getInvalidInput());
                }
            }  else if(textParser.isTalkCommand(inputElement)) {
                talkNpc(userInput, gameData);
            }else if(textParser.isGetCommand(inputElement)){
                getItem(userInput, gameData);
            } else {
                if (!textParser.isValidInput(inputElement)) {
                    System.out.println(gameData.getAsJsonObject("dialogue").get("invalid_input").getAsString());
                }
                // TODO: Add game logic here
            }
        }
    }

    private Location getLocationById(int locationId) {
        for (Location location : locations) {
            if (location.getId() == locationId) {
                return location;
            }
        }
        return null;
    }

    public void moveLocation(String userInput, Location currentLocation) {
        String direction = textParser.getSecondWord(userInput); // Assumes the second word is the direction
        Integer newLocationId = currentLocation.getExits().get(direction);
        // Check if the direction is a valid exit from the current location
        if (newLocationId != null) {
            setCurrentLocationId(newLocationId);
            gameView.displayLocation(new Gson().toJsonTree(getLocationById(newLocationId)).getAsJsonObject()); // Update the game view with the new location
        } else {
            System.out.println(gameDialogue.getInvalidExit());
        }
    }

    public void talkNpc(String userInput, JsonObject gameData) {
        String talkWord = textParser.getSecondWord(userInput);
        List<NPC> npcs = new Gson().fromJson(gameData.getAsJsonArray("npcs"), new TypeToken<List<NPC>>() {}.getType());
        for (NPC npc : npcs) {
            if (npc.getName().equalsIgnoreCase(talkWord)) {
                System.out.println(npc.getReply());
                toContinue();
                // ask if want to play... add method to go into mini game
            }
        }
    }

     public void lookItem(String userInput, JsonObject gameData){
        String itemWord = textParser.getSecondWord(userInput);
         List<Item> items = new Gson().fromJson(gameData.getAsJsonArray("items"), new TypeToken<List<Item>>() {}.getType());
         boolean itemFound = false;

        for(Item item : items) {
            if (item.getName().equalsIgnoreCase(itemWord)) {
                itemFound = true;
                System.out.println(item.getDescription());
            }
        }
        if (!itemFound) {
            System.out.println(gameDialogue.getInvalidInput());
        }
     }

     public void lookNpc(String userInput, JsonObject gameData){
        String npcWord = textParser.getSecondWord(userInput);
        List<NPC> npcs = new Gson().fromJson(gameData.getAsJsonArray("npcs"), new TypeToken<List<NPC>>() {}.getType());
        boolean npcFound = false;

        for (NPC npc : npcs){
            if(npc.getName().equalsIgnoreCase(npcWord)){
                npcFound = true;
                System.out.println(npc.getDescription());
            }
        }
        if(!npcFound){
            System.out.println(gameDialogue.getInvalidInput());
        }
     }

    public void getItem(String userInput, JsonObject gameData){
        String itemWord = textParser.getSecondWord(userInput);
        List<Item> items = new Gson().fromJson(gameData.getAsJsonArray("items"), new TypeToken<List<Item>>() {}.getType());
        boolean itemFound = false;

        for(Item item : items){
            if(item.getName().equalsIgnoreCase(itemWord)){
                itemFound = true;
                List<Item> inventory = player.getInventory();
                inventory.add(item);
//                gameData.getAsJsonObject("locations").getAsJsonArray("items");
                System.out.println(player.getInventory());
            }
        }
        if(!itemFound){
            System.out.println(gameDialogue.getInvalidInput());
        }
    }

    public void setCurrentLocationId(int currentLocationId) {
        this.currentLocationId = currentLocationId;
        gameView.displayLocation(new Gson().toJsonTree(getLocationById(currentLocationId)).getAsJsonObject()); // Update the game view with the new location
    }

    public void toContinue() {
        System.out.println("Press enter to continue...");
        try {
            System.in.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
