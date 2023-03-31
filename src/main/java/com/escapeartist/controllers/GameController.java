package com.escapeartist.controllers;

import com.escapeartist.models.*;
import com.escapeartist.util.Clear;
import com.escapeartist.util.GameMusic;
import com.escapeartist.util.GsonDeserializer;
import com.escapeartist.views.GUI;
import com.escapeartist.views.GameView;
import com.escapeartist.util.TextParser;
import com.escapeartist.views.MainView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;


import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
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
  private List<Riddle> riddles;
  private Unscramble unscramble;
  private List<Trivia> trivias;
  private GUI gui;


  // A constructor for the GameController class that takes a JsonObject representing game data
// and a GUI object as arguments and initializes the corresponding fields of the GameController
  public GameController(JsonObject gameData, GUI gui) {
    this.gameData = gameData;
    this.gui = gui;
  }

  // A method to load game data into the GameController object
  public void loadGameData() {
    // Create a TextParser object using the gameData field
    textParser = new TextParser(gameData);

    // Create a GameView object using the gameData field
    gameView = new GameView(gameData);

    // Create a GsonDeserializer object
    GsonDeserializer deserializer = new GsonDeserializer();

    // Deserialize the locations data using the GsonDeserializer object
    locations = deserializer.deserializeLocations();


    // Deserialize the NPCs data using the GsonDeserializer object
    List<NPC> npcs = deserializer.deserializeNPCs();

    // Deserialize the game dialogue data using the GsonDeserializer object
    gameDialogue = deserializer.deserializeGameDialogue();

    // Deserialize the player data using the GsonDeserializer object
    JsonObject playerJson = deserializer.deserializePlayerJson();
    unscramble = deserializer.deserializeUnscramble();
    player = deserializer.deserializePlayer(playerJson); // Deserialize the player using the JsonObject

    // Deserialize the riddles data using the GsonDeserializer object
    riddles = deserializer.deserializeRiddles();

    // Deserialize the trivia data using the GsonDeserializer object
    trivias = deserializer.deserializeTrivia();

    // Add various deserialized data as JsonElements to the gameData JsonObject
    gameData.add("player", new Gson().toJsonTree(player));
    gameData.add("dialogue", new Gson().toJsonTree(gameDialogue));
    gameData.add("locations", new Gson().toJsonTree(locations));
    gameData.add("npcs", new Gson().toJsonTree(npcs));
    gameData.add("unscramble", new Gson().toJsonTree(unscramble));
    gameData.add("riddle", new Gson().toJsonTree(riddles));
    gameData.add("trivia", new Gson().toJsonTree(trivias));

    // Set the currentLocationId field to the id of the player's current location
    this.currentLocationId = player.getCurrentLocation();
  }


  // A method to set the current location ID field of the GameController object
  public void setCurrentLocationId(int currentLocationId) {
    this.currentLocationId = currentLocationId;
  }

  // The main method that runs the game
  public void run() {
    // Load game data into the GameController object
    loadGameData();

    // Create a Scanner object to read user input from the console
    Scanner scanner = new Scanner(System.in);

    // Initialize the running flag to true
    boolean running = true;

    // Keep running the game while the running flag is true
    while (running) {
      // Display the player's current status
      player.playerStatus(gameData);

      // Get the current location object based on the current location ID field
      Location currentLocation = getLocationById(currentLocationId);

      // Display the current location object using the gameView object
      gameView.displayLocation(new Gson().toJsonTree(currentLocation).getAsJsonObject());

      // Display the command prompt and read user input from the console
      System.out.print(gameDialogue.getCommandPrompt());
      String userInput = scanner.nextLine();

      // Clean the user input using the textParser object
      String cleanedInput = textParser.cleanUserInput(userInput);

      // Convert the cleaned user input to a JsonElement
      JsonElement inputElement = new Gson().toJsonTree(cleanedInput);

// Check if the user input is a quit command
      if (textParser.isQuitCommand(inputElement)) {
        Clear.clearConsole();

        // Display a confirmation prompt to the user
        boolean confirmQuit = textParser.getConfirmation(gameData.getAsJsonObject("dialogue").get("quit_confirm").getAsString());

        // Quit the game if the user confirms
        if (confirmQuit) {
          System.out.println(gameData.getAsJsonObject("dialogue").get("goodbye_message").getAsString());
          running = false;
        }
      }
      // Check if the user input is a help command
      else if (textParser.isHelpCommand(inputElement)) {
        Clear.clearConsole();

        // Display the help menu to the user
        System.out.println(gameData.getAsJsonObject("dialogue").get("help_menu").getAsString());
      }
      // Check if the user input is a go command
      else if (textParser.isGoCommand(inputElement)) {
        Clear.clearConsole();

        // Move the player to a new location based on the user input
        moveLocation(userInput, currentLocation);
      }
      // Check if the user input is a look command
      else if (textParser.isLookCommand(inputElement)) {
        Clear.clearConsole();

        // Get the second word of the user input
        String secondWord = textParser.getSecondWord(userInput);

        // Check if the second word matches an NPC name
        if (currentLocation.getNpcs().stream().anyMatch(npc -> npc.getName().equalsIgnoreCase(secondWord))) {
          lookNpc(userInput, gameData);
        }
        // Check if the second word matches an item name
        else if (currentLocation.getItems().stream().anyMatch(item -> item.getName().equalsIgnoreCase(secondWord))) {
          lookItem(userInput, gameData);
        }
        // Check if the second word matches an item in the player's inventory
        else if (player.getInventory().stream().anyMatch(item -> item.getName().equalsIgnoreCase(secondWord))) {

          // Check if the second word of the user input is "map"
          if(secondWord.equalsIgnoreCase("map")){
            // Create a new MapFrame object and read the map
            MapFrame frame = new MapFrame();
            frame.readMap();
          } else {
            // Otherwise, look at the item in the player's inventory
            lookItemInInventory(userInput);
          }
        }
        // If the user input is not recognized as any of the above commands, print an error message
        else {
          System.out.println(gameDialogue.getInvalidInput());
        }

        // Check if the user input is a talk command
      } else if (textParser.isTalkCommand(inputElement)) {
        Clear.clearConsole();

        // Talk to the NPC specified in the user input
        talkNpc(userInput, gameData);

        // Check if the user input is a get command
      } else if (textParser.isGetCommand(inputElement)) {
        Clear.clearConsole();
        // Get the item specified in the user input from the current location
        getItem(userInput, gameData, currentLocation);
        // Check if the user input is a drop command
      } else if(textParser.isUseCommand(inputElement)) {
        useItem(userInput);
      } else if(textParser.isEquipCommand(inputElement)){
        equipItem(userInput);
      } else if (textParser.isDropCommand(inputElement)) {
        Clear.clearConsole();

        // Drop the specified item from the player's inventory into the current location
        dropItem(userInput, currentLocation);

        // If none of the above conditions are met, check if the user input is valid
      } else {
        if (!textParser.isValidInput(inputElement)) {
          Clear.clearConsole();
          System.out.println(
              gameData.getAsJsonObject("dialogue").get("invalid_input").getAsString());
        }
      }
    }
  }

  // A private helper method to get a Location object from the locations list based on its ID
  private Location getLocationById(int locationId) {
    // Iterate through the locations list and check if any location's ID matches the given location ID
    for (Location location : locations) {
      if (location.getId() == locationId) {
        return location;
      }
    }
    // If no location is found, return null
    return null;
  }

  // A method to move the player to a new location based on the user input and the current location
  public void moveLocation(String userInput, Location currentLocation) {
    // Get the second word of the user input, assuming it is the direction to move in
    String direction = textParser.getSecondWord(userInput);

    // Get the ID of the new location based on the current location and the direction to move in
    Integer newLocationId = currentLocation.getExits().get(direction);

    // If the new location ID is not null, update the game view with the new location
    if (newLocationId != null) {
      setCurrentLocationId(newLocationId);

      // Get the name of the new location and print a message to the console
      String currentLocationName = getLocationById(currentLocationId).getName();
      System.out.println(gameData.getAsJsonObject("dialogue").get("player_moved_location").getAsString() + currentLocationName);
    }
    // If the new location ID is null, print an error message to the GUI text area
    else {
      gui.textArea.append(gameDialogue.getInvalidExit() + "\n");
    }
  }


  // A method to talk to an NPC in the game based on user input and game data
  public void talkNpc(String userInput, JsonObject gameData) {
    // Get the second word of the user input, assuming it is the NPC to talk to
    String talkWord = textParser.getSecondWord(userInput);

    // Deserialize the list of NPCs from the game data
    List<NPC> npcs = new Gson().fromJson(gameData.getAsJsonArray("npcs"), new TypeToken<List<NPC>>() {}.getType());

    // Initialize variables to store specific NPCs (if present)
    NPC ghost = null;
    NPC knight = null;
    NPC samurai = null;

    // Check if the NPC is in the current location before talking to them
    Location currentLocation = getLocationById(currentLocationId);
    if (currentLocation.getNpcs().stream().noneMatch(npc -> npc.getName().equalsIgnoreCase(talkWord))) {
      System.out.println(gameDialogue.getInvalidInput());
      return;
    }

    // Iterate through the list of NPCs and find the specified NPC
    for (NPC npc : npcs) {
      if (npc.getName().equalsIgnoreCase(talkWord)) {
        // Print the NPC's reply
        System.out.println(npc.getReply());

        // Store the NPC in a specific variable if it matches a certain name
        if (npc.getName().equalsIgnoreCase("ghost")) {
          ghost = npc;
        } else if (npc.getName().equalsIgnoreCase("samurai")) {
          samurai = npc;
        } else if (npc.getName().equalsIgnoreCase("knight")) {
          knight = npc;
          break;
        }
      }
    }

    // If the NPC is a ghost, prompt the user to play a riddle mini-game
    if (ghost != null) {
      System.out.println(ghost.getGameInvitation());
      System.out.print(gameDialogue.getCommandPrompt());

      // Read user input to determine whether to play the game
      Scanner scanner = new Scanner(System.in);
      String choice = scanner.nextLine();

      // If the user enters "yes", play the riddle mini-game
      if (gameDialogue.getValidInputs().get("yes").contains(choice.toLowerCase())) {

        // Get the riddle data from the game data
        int riddleId = 1;
        JsonArray riddlesJsonArray = gameData.getAsJsonArray("riddle");
        Type listType = new TypeToken<List<Riddle>>() {}.getType();
        List<Riddle> riddlesList = new Gson().fromJson(riddlesJsonArray, listType);
        Riddle riddle = Riddle.getRiddleById(riddlesList, riddleId);

        // play the riddle mini-game
        boolean riddleSolved = playRiddle(riddle.getId());

        // If the riddle is solved, remove the ghost NPC from the current location and print a message
        if (riddleSolved) {
          currentLocation.removeNPC("Ghost");
          System.out.println(ghost.getGoodbyeMessage2());
          gamesCompleted(riddleSolved);
        }
      } // If the user enters "no", print a message and continue the game
      else if (gameDialogue.getValidInputs().get("no").contains(choice.toLowerCase())) {
        System.out.println(ghost.getGoodbyeMessage());

        // If the user enters an invalid input, print an error message
      } else {
        System.out.println(gameDialogue.getInvalidInput());
      }
    } else if (samurai != null) {
      // Display samurai's game invitation
      System.out.print(samurai.getGameInvitation());

      // Read user's input
      Scanner scanner = new Scanner(System.in);
      String choice = scanner.nextLine();

      // Check if the user inputs a valid response
      if (gameDialogue.getValidInputs().get("yes").contains(choice.toLowerCase())) {
        // Get a list of words to unscramble from the game data
        JsonArray wordsJsonArray = gameData.getAsJsonObject("unscramble").getAsJsonArray("words");
        Type listType = new TypeToken<List<String>>() {}.getType();
        List<String> wordsList = new Gson().fromJson(wordsJsonArray, listType);

        // Play the unscramble mini-game
        playUnscramble(wordsList, currentLocation);
      } else if (gameDialogue.getValidInputs().get("no").contains(choice.toLowerCase())) {
        // If the user inputs no, display samurai's goodbye message
        System.out.println(samurai.getGoodbyeMessage());
      } else {
        // If the user inputs an invalid response, display an error message
        System.out.println(gameDialogue.getInvalidInput());
      }
    } else if (knight != null) {
      // Display knight's game invitation
      System.out.println(knight.getGameInvitation());
      System.out.print(gameDialogue.getCommandPrompt());

      // Read user's input
      Scanner scanner = new Scanner(System.in);
      String choice = scanner.next();

      // Check if the user inputs a valid response
      if (gameDialogue.getValidInputs().get("yes").contains((choice.toLowerCase()))) {
        // Get a trivia question from the game data, hardcoding to id 1
        int triviaId = 1;
        JsonArray triviasJsonArray = gameData.getAsJsonArray("trivia");
        Type listType = new TypeToken<List<Trivia>>() {}.getType();
        List<Trivia> triviaList = new Gson().fromJson(triviasJsonArray, listType);
        Trivia trivia = Trivia.getTriviaByID(triviaList, triviaId);

        // Play the trivia mini-game for the knight
        playTrivia(trivia.getId(), currentLocation);
      } else if (gameDialogue.getValidInputs().get("no").contains(choice.toLowerCase())) {
        // If the user inputs no, display knight's goodbye message
        System.out.println(knight.getGoodbyeMessage());
      } else {
        // If the user inputs an invalid response, display an error message
        System.out.println(gameDialogue.getInvalidInput());
      }
    }
  }

  // This method takes a user input string and a game data JSON object as input parameters.
  public void lookItem(String userInput, JsonObject gameData) {
    // Extract the second word from the user input string using a text parser object.
    String itemWord = textParser.getSecondWord(userInput);
    // Parse the 'items' JSON array from the game data JSON object into a list of Item objects using Gson library.
    List<Item> items = new Gson().fromJson(gameData.getAsJsonArray("items"),
        new TypeToken<List<Item>>() {
        }.getType());

    // Create a boolean flag to keep track of whether an item was found or not.
    boolean itemFound = false;

    // Loop through each item in the list of items.
    for (Item item : items) {
      // Check if the item name matches the item word extracted from user input (ignoring case).
      if (item.getName().equalsIgnoreCase(itemWord)) {
        // If a matching item is found, set the itemFound flag to true and print its description.
        itemFound = true;
        System.out.println(item.getDescription());
      }
    }
    // If no matching item was found, print an error message using a gameDialogue object.
    if (!itemFound) {
      System.out.println(gameDialogue.getInvalidInput());
    }
  }

  // This method takes a user input string and a game data JSON object as input parameters.
  public void lookNpc(String userInput, JsonObject gameData) {
    // Extract the second word from the user input string using a text parser object.
    String npcWord = textParser.getSecondWord(userInput);
    // Parse the 'npcs' JSON array from the game data JSON object into a list of NPC objects using Gson library.
    List<NPC> npcs = new Gson().fromJson(gameData.getAsJsonArray("npcs"), new TypeToken<List<NPC>>() {
    }.getType());

    // Create a boolean flag to keep track of whether an NPC was found or not.
    boolean npcFound = false;

    // Loop through each NPC in the list of NPCs.
    for (NPC npc : npcs) {
      // Check if the NPC name matches the NPC word extracted from user input (ignoring case).
      if (npc.getName().equalsIgnoreCase(npcWord)) {
        // If a matching NPC is found, set the npcFound flag to true and print its description.
        npcFound = true;
        System.out.println(npc.getDescription());
      }
    }
    // If no matching NPC was found, print an error message using a gameDialogue object.
    if (!npcFound) {
      System.out.println(gameDialogue.getInvalidInput());
    }
  }

  // This method takes a user input string as an input parameter.
  public void lookMap(String userInput) {
    // Extract the second word from the user input string using a text parser object.
    String mapWord = textParser.getSecondWord(userInput);
    // Get the list of maps from the player's inventory.
    List<Item> maps = player.getInventory();

    // Create a boolean flag to keep track of whether a map was found or not.
    boolean mapFound = false;

    // Loop through each map in the list of maps.
    for (Item map : maps){
      // Check if the map name matches the map word extracted from user input (ignoring case).
      if(map.getName().equalsIgnoreCase(mapWord)){
        // If a matching map is found, set the mapFound flag to true and display the map.
        mapFound = true;
        MapFrame frame = new MapFrame();
        frame.readMap();
      }
    }
    // If no matching map was found, print an error message using a gameDialogue object.
    if(!mapFound){
      System.out.println(gameDialogue.getInvalidInput());
      System.out.println("Map not found in inventory");
    }
  }

  public void lookItemInInventory(String userInput){
    String itemWord = textParser.getSecondWord(userInput);
    List<Item> items = player.getInventory();
    boolean itemFound = false;

    for (Item item : items) {
      if (item.getName().equalsIgnoreCase(itemWord)) {
        itemFound = true;
        System.out.println(item.getDescription());
      }
    }
    if (!itemFound) {
      System.out.println(gameDialogue.getInvalidInput());
    }
  }

  public void getItem(String userInput, JsonObject gameData, Location currentLocation) {
    String itemWord = textParser.getSecondWord(userInput);
    List<Item> itemsLocation = currentLocation.getItems();
    boolean itemFound = false;

    for (Item item : itemsLocation) {
      if (item.getName().equalsIgnoreCase(itemWord)) {
        itemFound = true;
        //Item itemToAdd = new Item(item.getId(), item.getName(), item.getDescription());
        player.addItem(item);
        currentLocation.removeItem(item);
        System.out.println(
            gameData.getAsJsonObject("dialogue").get("player_picked_up_item").getAsString());
        break;
      }
    }
    if (!itemFound) {
      System.out.println(gameDialogue.getInvalidInput());
    }
  }

  public void equipItem(String userInput){
    String itemUse = textParser.getSecondWord(userInput);
    List<Item> inventory = player.getInventory();
    Item itemToEquip = null;
    for(Item item : inventory){
      if(item.getName().equalsIgnoreCase(itemUse)){
        itemToEquip=item;
        break;
      }
    }
    if(itemToEquip==null){
      System.out.println("that item is not in your inventory.");
    }
    else if(!itemToEquip.getEquippable()){
      System.out.println("that item cannot be equipped.");
    }
    else if(itemToEquip.getEquippable()){
      if(itemToEquip.getType().equals("weapon")){
        player.setEquippedWeapon(itemToEquip);
        player.setAttack(player.getAttack()+itemToEquip.getValue());
        System.out.println("You have equipped the " + itemToEquip.getName());
      }
      else if(itemToEquip.getType().equals("armor")){
        player.setEquippedArmor(itemToEquip);
        player.setDefense(player.getDefense()+itemToEquip.getValue());
        System.out.println("You have equipped the " + itemToEquip.getName());
      }
    }
  }

  public void useItem(String userInput){
    String itemUse = textParser.getSecondWord(userInput);
    List<Item> inventory = player.getInventory();
    Item itemToUse = null;
    for(Item item : inventory){
      if(item.getName().equalsIgnoreCase(itemUse)){
        itemToUse=item;
        break;
      }
    }
    if(itemToUse == null){
      System.out.println("That item is not in your inventory.");
    }
    else if(itemToUse.getType().equalsIgnoreCase("heal")){
      player.setHp(player.getHp()+itemToUse.getValue());
      System.out.println("You used the " + itemToUse.getName() + " and heal yourself by " + itemToUse.getValue() + " points!");
    }
    else if(itemToUse.getType().equalsIgnoreCase("map")){
      //TODO link logic to james' map feature
      System.out.println("map function");
    }
  }

  public void dropItem(String userInput, Location currentLocation) {
    String dropWord = textParser.getSecondWord(userInput);
    List<Item> inventory = player.getInventory();
    Item itemToRemove = null;

    for (Item item : inventory) {
      if (item.getName().equalsIgnoreCase(dropWord)) {
        itemToRemove = item;
        break;
      }
    }
    if (itemToRemove == null) {
      System.out.print(gameData.getAsJsonObject("dialogue").get("items_dropped").getAsString());
      toContinue();
    } else {
      player.dropItem(itemToRemove);
      currentLocation.addItemToLocation(itemToRemove);

    }
  }

  public void toContinue() {
    System.out.println("Press enter to continue...");
    try {
      System.in.read();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

    public boolean playRiddle(int riddleId) {
        JsonArray riddlesJsonArray = gameData.getAsJsonArray("riddle");
        Type listType = new TypeToken<List<Riddle>>() {
        }.getType();
        List<Riddle> riddlesList = new Gson().fromJson(riddlesJsonArray, listType);

        Riddle riddle = Riddle.getRiddleById(riddlesList, riddleId);

        if (riddle == null) {
            System.out.println("Invalid riddle ID");
            return false;
        }

        Scanner scanner = new Scanner(System.in);

        int numTries = 0;
        while (numTries < 3) {
            System.out.println(riddle.getQuestion());
            System.out.print(gameDialogue.getUserPrompt());
            String answer = scanner.nextLine().toLowerCase();

            if (answer.equals(riddle.getAnswer())) {
                System.out.println(gameDialogue.getRiddleSolved());
                return true;
            } else {
                System.out.println(gameDialogue.getRiddleIncorrect());
                numTries++;
            }
        }

        System.out.println(gameDialogue.getRiddleNotSolved());

        System.out.print(gameDialogue.getUserPrompt());
        String choice = scanner.nextLine().toLowerCase();

        if (gameDialogue.getValidInputs().get("yes").contains(choice)) {
            return playRiddle(riddleId);
        } else {
            return false;
        }
    }


  public void playTrivia(int triviaID, Location currentLocation) {
    JsonArray triviaJsonArray = gameData.getAsJsonArray("trivia");
    Type listType = new TypeToken<List<Trivia>>() {
    }.getType();
    List<Trivia> triviasList = new Gson().fromJson(triviaJsonArray, listType);
    Trivia trivia = Trivia.getTriviaByID(triviasList, triviaID);
  boolean won = false;
    if (trivia == null) {
      System.out.println("Invalid Trivia ID");
      return;
    }
    Scanner scanner = new Scanner(System.in);

        int numAttempts = 0;
        while (numAttempts <= 2) {
            System.out.println(trivia.getQuestion());
            System.out.print(gameDialogue.getCommandPrompt());
            String answer = scanner.nextLine().toLowerCase();

        if (answer.equalsIgnoreCase(trivia.getAnswer())) {
          System.out.println(gameDialogue.getPlayerSolvedTrivia());
          won = true;
          currentLocation.getNpcs().remove(0);
          break;
        } else if (numAttempts == 2) {
          System.out.println(gameDialogue.getFinalIncorrectAnswer());
          break;
        }
        System.out.println(gameDialogue.getPlayerGaveIncorrectAnswer());
        numAttempts++;
      }
      gamesCompleted(won);
  }

  public void gamesCompleted(boolean completed) {
      if (completed){
        System.out.println("You now have access to the key.");
        //TODO: fix this back
        //Item item = new Item(1, "Key", "A key. Maybe I can use it to unlock something.");
        //player.getInventory().add(item);
      } else{
        System.out.println("You need to complete the challenge before you can access this key.");
      }
  }

  public void playUnscramble(List<String> wordsList, Location currentLocation) {
    int guessCount = 0;
    boolean won = false;
    boolean quit = false;
    Scanner scanner = new Scanner(System.in);
    unscramble.randomizeWord(wordsList);
    System.out.println(
        gameData.getAsJsonObject("unscramble").get("scrambled") + unscramble.scrambleWord());
    while (guessCount < 5 && !won) {
      System.out.print(unscramble.getAnswer());
      String guess = scanner.nextLine();
      unscramble.guesses(guess);

      if (!unscramble.guesses(guess) && !guess.isEmpty()) {
        System.out.println(unscramble.getTryAgain());
        guessCount++;
        if (gameDialogue.getValidInputs().get("quit").contains(guess.toLowerCase())) {
          System.out.println(unscramble.getGiveUp());
          quit = true;
          break;
        }
      } else if (unscramble.guesses(guess)) {
        System.out.println(unscramble.getWin());
        won = true;
        currentLocation.getNpcs().remove(0);
      } else {
        System.out.println(unscramble.getNoAnswer());
      }
    }
    if (!won && !quit) {
      System.out.print(unscramble.getLose());
      String answer = scanner.nextLine();
      if (gameDialogue.getValidInputs().get("yes").contains(answer.toLowerCase())) {
        JsonArray wordsJsonArray = gameData.getAsJsonObject("unscramble").getAsJsonArray("words");
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        List<String> anotherList = new Gson().fromJson(wordsJsonArray, listType);
        playUnscramble(anotherList, currentLocation);
      } else if (gameDialogue.getValidInputs().get("no").contains(answer.toLowerCase())) {
        System.out.println(unscramble.getNoPlayAgain());
      } else {
        System.out.println(gameDialogue.getInvalidInput());
      }
    }
    gamesCompleted(won);
  }
}

