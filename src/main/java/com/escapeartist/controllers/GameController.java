package com.escapeartist.controllers;

import com.escapeartist.models.*;
import com.escapeartist.util.Clear;
import com.escapeartist.util.GameMusic;
import com.escapeartist.util.GameTimer;
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
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

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
  private GameTimer gameTimer;
  private Boss boss;

  public GameController(JsonObject gameData, GUI gui, int difficulty) {
    this.gameData = gameData;
    this.gui = gui;

    this.gameTimer = new GameTimer(1);
    this.boss= new Boss();
  }

  public GameController(JsonObject gameData, GUI gui) {
  }
  // A method to load game data into the GameController object
  public void loadGameData() {
    textParser = new TextParser(gameData);
    gameView = new GameView(gameData);
    GsonDeserializer deserializer = new GsonDeserializer();
    locations = deserializer.deserializeLocations();
    List<NPC> npcs = deserializer.deserializeNPCs();
    gameDialogue = deserializer.deserializeGameDialogue();
    JsonObject playerJson = deserializer.deserializePlayerJson();
    unscramble = deserializer.deserializeUnscramble();
    player = deserializer.deserializePlayer(playerJson); // Deserialize the player using the JsonObject

    riddles = deserializer.deserializeRiddles();
    trivias = deserializer.deserializeTrivia();

    gameData.add("player", new Gson().toJsonTree(player));
    gameData.add("dialogue", new Gson().toJsonTree(gameDialogue));
    gameData.add("locations", new Gson().toJsonTree(locations));
    gameData.add("npcs", new Gson().toJsonTree(npcs));
    gameData.add("unscramble", new Gson().toJsonTree(unscramble));
    gameData.add("riddle", new Gson().toJsonTree(riddles));
    gameData.add("trivia", new Gson().toJsonTree(trivias));

    this.currentLocationId = player.getCurrentLocation();
  }

  public void setCurrentLocationId(int currentLocationId) {
    this.currentLocationId = currentLocationId;
  }

  public void run() {
    loadGameData();
    Scanner scanner = new Scanner(System.in);
    boolean running = true;
    while (running) {
      if (gameTimer.hasTimeExpired()) {
        System.out.println("Time's up! The main boss appears.");
        boss.setActive(true);
        break;
      }
      player.playerStatus(gameData);
      Location currentLocation = getLocationById(currentLocationId);
      gameView.displayLocation(new Gson().toJsonTree(currentLocation).getAsJsonObject());
      System.out.print(gameDialogue.getCommandPrompt());
      String userInput = scanner.nextLine();
      String cleanedInput = textParser.cleanUserInput(userInput);
      JsonElement inputElement = new Gson().toJsonTree(cleanedInput);
      if (textParser.isQuitCommand(inputElement)) {
        Clear.clearConsole();
        boolean confirmQuit = textParser.getConfirmation(
            gameData.getAsJsonObject("dialogue").get("quit_confirm").getAsString());
        if (confirmQuit) {
          System.out.println(
              gameData.getAsJsonObject("dialogue").get("goodbye_message").getAsString());
          running = false;
        }
      } else if (textParser.isHelpCommand(inputElement)) {
        Clear.clearConsole();
        System.out.println(gameData.getAsJsonObject("dialogue").get("help_menu").getAsString());
      } else if (textParser.isGoCommand(inputElement)) {
        Clear.clearConsole();
        moveLocation(userInput, currentLocation);
      } else if (textParser.isLookCommand(inputElement)) {
        Clear.clearConsole();
        String secondWord = textParser.getSecondWord(userInput);
        if (currentLocation.getNpcs().stream()
            .anyMatch(npc -> npc.getName().equalsIgnoreCase(secondWord))) {
          lookNpc(userInput, gameData);
        } else if (currentLocation.getItems().stream()
            .anyMatch(item -> item.getName().equalsIgnoreCase(secondWord))) {
          lookItem(userInput, gameData);
        } else if (player.getInventory().stream()
            .anyMatch(item -> item.getName().equalsIgnoreCase(secondWord))) {
          if (secondWord.equalsIgnoreCase("map")) {
            MapFrame frame = new MapFrame();
            frame.readMap();
          } else {
            lookItemInInventory(userInput);
          }
        } else {
          System.out.println(gameDialogue.getInvalidInput());
        }
      } else if (textParser.isTalkCommand(inputElement)) {
        Clear.clearConsole();
        talkNpc(userInput, gameData);
      } else if (textParser.isGetCommand(inputElement)) {
        Clear.clearConsole();
        getItem(userInput, gameData, currentLocation);
      } else if (textParser.isUseCommand(inputElement)) {
        useItem(userInput);
      } else if (textParser.isEquipCommand(inputElement)) {
        equipItem(userInput);
      } else if (textParser.isDropCommand(inputElement)) {
        Clear.clearConsole();
        dropItem(userInput, currentLocation);
      } else {
        if (!textParser.isValidInput(inputElement)) {
          Clear.clearConsole();
          System.out.println(
              gameData.getAsJsonObject("dialogue").get("invalid_input").getAsString());
        }
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
    String direction = textParser.getSecondWord(userInput);
    Integer newLocationId = currentLocation.getExits().get(direction);
    if (newLocationId != null) {
      setCurrentLocationId(newLocationId);
      String currentLocationName = getLocationById(currentLocationId).getName();
      System.out.println(gameData.getAsJsonObject("dialogue").get("player_moved_location").getAsString() + currentLocationName);
    }
    else {
      gui.textArea.append(gameDialogue.getInvalidExit() + "\n");
    }
  }

  public void talkNpc(String userInput, JsonObject gameData) {
    String talkWord = textParser.getSecondWord(userInput);
    List<NPC> npcs = new Gson().fromJson(gameData.getAsJsonArray("npcs"), new TypeToken<List<NPC>>() {}.getType());
    NPC ghost = null;
    NPC knight = null;
    NPC samurai = null;
    Location currentLocation = getLocationById(currentLocationId);
    if (currentLocation.getNpcs().stream()
        .noneMatch(npc -> npc.getName().equalsIgnoreCase(talkWord))) {
      System.out.println(gameDialogue.getInvalidInput());
      return;
    }

    // Find the specified NPC
    NPC targetNPC = null;
    for (NPC npc : npcs) {
      if (npc.getName().equalsIgnoreCase(talkWord)) {
        targetNPC = npc;
        break;
      }
    }

    // If the target NPC is found, handle the mini-games and responses
    if (targetNPC != null) {
      // Display the target NPC's game invitation
      System.out.println(targetNPC.getGameInvitation());
      System.out.print(gameDialogue.getCommandPrompt());

      // Read user's input
      Scanner scanner = new Scanner(System.in);
      String choice = scanner.next();

      // If the user enters "yes", play the riddle mini-game
      if (gameDialogue.getValidInputs().get("yes")
          .contains(choice.toLowerCase())) {

        // Get the riddle data from the game data
        int riddleId = 1;
        JsonArray riddlesJsonArray = gameData.getAsJsonArray("riddle");
        Type listType = new TypeToken<List<Riddle>>() {
        }.getType();
        List<Riddle> riddlesList = new Gson().fromJson(riddlesJsonArray,
            listType);
        Riddle riddle = Riddle.getRiddle(riddlesList, riddleId);

      // play the riddle mini-game
      boolean riddleSolved = playRiddle(riddle.getId());

        // If the riddle is solved, remove the ghost NPC from the current location and print a message
        if (riddleSolved) {
          currentLocation.removeNPC("Ghost");
          System.out.println(ghost.getGoodbyeMessage2());
          gamesCompleted(riddleSolved);
        }
      } // If the user enters "no", print a message and continue the game
      else if (gameDialogue.getValidInputs().get("no")
          .contains(choice.toLowerCase())) {
        System.out.println(ghost.getGoodbyeMessage());
      } else {
        System.out.println(gameDialogue.getInvalidInput());
      }
    } else if (samurai != null) {
      System.out.print(samurai.getGameInvitation());
      Scanner scanner = new Scanner(System.in);
      String choice = scanner.nextLine();
      // Check if the user inputs a valid response
      if (gameDialogue.getValidInputs().get("yes")
          .contains(choice.toLowerCase())) {
        // Get a list of words to unscramble from the game data
        JsonArray wordsJsonArray = gameData.getAsJsonObject("unscramble")
            .getAsJsonArray("words");
        Type listType = new TypeToken<List<String>>() {
        }.getType();

        List<String> wordsList = new Gson().fromJson(wordsJsonArray, listType);
        playUnscramble(wordsList, currentLocation);
      } else if (gameDialogue.getValidInputs().get("no")
          .contains(choice.toLowerCase())) {
        // If the user inputs no, display samurai's goodbye message
        System.out.println(samurai.getGoodbyeMessage());
      } else {
        System.out.println(gameDialogue.getInvalidInput());
      }
    } else if (knight != null) {
      System.out.println(knight.getGameInvitation());
      System.out.print(gameDialogue.getCommandPrompt());
      Scanner scanner = new Scanner(System.in);
      String choice = scanner.next();
      // Check if the user inputs a valid response
      if (gameDialogue.getValidInputs().get("yes")
          .contains(choice.toLowerCase())) {
        JsonArray triviasJsonArray = gameData.getAsJsonArray("trivia");
        Type listType = new TypeToken<List<Trivia>>() {
        }.getType();
        List<Trivia> triviaList = new Gson().fromJson(triviasJsonArray,
            listType);
        Collections.shuffle(triviaList);
        Trivia trivia = triviaList.get(
            0); // Get the first (randomized) trivia question

        // Play the trivia mini-game for the knight
        if (trivia != null) {
          playTrivia(trivia.getId(), currentLocation);
        } else {
          // Unable to retrieve trivia question
          System.out.println("Unable to retrieve trivia question.");
        }

      } else if (gameDialogue.getValidInputs().get("no")
          .contains(choice.toLowerCase())) {
        // If the user inputs no, display knight's goodbye message

        System.out.println(knight.getGoodbyeMessage());
      } else {
        System.out.println(gameDialogue.getInvalidInput());
      }
    }
  }

      // This method takes a user input string and a game data JSON object as input parameters.
  public void lookItem(String userInput, JsonObject gameData) {
    String itemWord = textParser.getSecondWord(userInput);
    List<Item> items = new Gson().fromJson(gameData.getAsJsonArray("items"),
        new TypeToken<List<Item>>() {
        }.getType());
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

  public void lookNpc(String userInput, JsonObject gameData) {
    String npcWord = textParser.getSecondWord(userInput);
    List<NPC> npcs = new Gson().fromJson(gameData.getAsJsonArray("npcs"), new TypeToken<List<NPC>>() {
    }.getType());
    boolean npcFound = false;

    for (NPC npc : npcs) {
      if (npc.getName().equalsIgnoreCase(npcWord)) {
        npcFound = true;
        System.out.println(npc.getDescription());
      }
    }
    if (!npcFound) {
      System.out.println(gameDialogue.getInvalidInput());
    }
  }

  public void lookMap(String userInput) {
    String mapWord = textParser.getSecondWord(userInput);
    List<Item> maps = player.getInventory();
    boolean mapFound = false;

    for (Item map : maps){
      if(map.getName().equalsIgnoreCase(mapWord)){
        mapFound = true;
        MapFrame frame = new MapFrame();
        frame.readMap();
      }
    }
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
        if(player.getEquippedWeapon()!=null){
          player.setAttack(player.getAttack()-player.getEquippedWeapon().getValue());
        }
        player.setEquippedWeapon(itemToEquip);
        player.setAttack(player.getAttack()+itemToEquip.getValue());
        System.out.println("You have equipped the " + itemToEquip.getName());
      }
      else if(itemToEquip.getType().equals("armor")){
        if(player.getEquippedArmor()!=null){
          player.setDefense(player.getAttack()-player.getEquippedWeapon().getValue());
        }
        player.setEquippedArmor(itemToEquip);
        player.setDefense(player.getDefense()+itemToEquip.getValue());
        System.out.println("You have equipped the " + itemToEquip.getName());
      }
    }
  }

  public void useItem(String userInput){
    String itemUse = textParser.getSecondWord(userInput);
    List<Item> inventory = player.getInventory();
    System.out.println(itemUse);
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
    else if(itemToUse.getType().equalsIgnoreCase("potionkey")){
      openPotionChest();
    }
    else if(itemToUse.getType().equalsIgnoreCase("armorkey")){
      openArmorChest();
    }
    else if(itemToUse.getType().equalsIgnoreCase("weaponkey")){
      openWeaponChest();
    }
  }

  public void openArmorChest() {
    Location currentLocation = getLocationById(currentLocationId);
    if(currentLocationId!=15){
      System.out.println("You cannot use the armor key here");
    }
    else{
      Scanner scanner = new Scanner(System.in);
      while(true) {
        System.out.println("Which chest do you want to open? (open left, open middle, open right)");
        String userInput = scanner.nextLine();
        if(userInput.equalsIgnoreCase("open left")){
          if(isItemIdInRoom(currentLocation, 23)){
            Item jazerant = new Item(6,10, "Jazerant", "A medieval light coat of armour consisting of mail between layers of fabric or leather.", "armor", true);
            player.addItem(jazerant);
            System.out.println("Acquired Jazerant!");
            removeChest(currentLocation, 23);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else if(userInput.equalsIgnoreCase("open middle")){
          if(isItemIdInRoom(currentLocation, 24)){
            Item chainmail = new Item(7,12, "Chainmail", "A armor consisting of small metal rings linked together in a pattern to form a mesh", "armor", true);
            player.addItem(chainmail);
            System.out.println("Acquired Chainmail!");
            removeChest(currentLocation, 24);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else if(userInput.equalsIgnoreCase("open right")){
          if(isItemIdInRoom(currentLocation, 25)){
            Item katana = new Item(8,22, "Yoroi", "Armor made of small pieces of lacquered metal that were connected with silk laces and cords", "armor", true);
            player.addItem(katana);
            System.out.println("Acquired Yoroi!");
            removeChest(currentLocation, 25);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else{
          System.out.println("Invalid input.");
        }
      }
    }
  }

  public void openWeaponChest() {
    Location currentLocation = getLocationById(currentLocationId);
    if(currentLocationId!=11){
      System.out.println("You cannot use the weapons key here");
    }
    else{
      Scanner scanner = new Scanner(System.in);
      while(true) {
        System.out.println("Which chest do you want to open? (open left, open middle, open right)");
        String userInput = scanner.nextLine();
        if(userInput.equalsIgnoreCase("open left")){
          if(isItemIdInRoom(currentLocation, 20)){
            Item scimitar = new Item(3,10, "Scimitar", "A sword with a curved blade.", "weapon", true);
            player.addItem(scimitar);
            System.out.println("Acquired Scimitar!");
            removeChest(currentLocation, 20);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else if(userInput.equalsIgnoreCase("open middle")){
          if(isItemIdInRoom(currentLocation, 21)){
            Item longsword = new Item(4,13, "Longsword", "A blade with long reach", "weapon", true);
            player.addItem(longsword);
            System.out.println("Acquired Longsword!");
            removeChest(currentLocation, 21);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else if(userInput.equalsIgnoreCase("open right")){
          if(isItemIdInRoom(currentLocation, 22)){
            Item katana = new Item(5,20, "Katana", "A weapon specialized in mid-range combat and are capable of inflicting slash and thrust", "weapon", true);
            player.addItem(katana);
            System.out.println("Acquired Katana!");
            removeChest(currentLocation, 22);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else{
          System.out.println("Invalid input.");
        }
      }
    }
  }

  public void openPotionChest() {
    Location currentLocation = getLocationById(currentLocationId);
    if(currentLocationId!=6){
      System.out.println("You cannot use the potions key here");
    }
    else{
      Scanner scanner = new Scanner(System.in);
      while(true) {
        System.out.println("Which chest do you want to open? (open left, open middle, open right)");
        String userInput = scanner.nextLine();
        if(userInput.equalsIgnoreCase("open left")){
          if(isItemIdInRoom(currentLocation, 26)){
            Item smallPotion = new Item(30,8, "Small Potion", "A small healing potion is a potion that can be healed by a player.", "heal", false);
            player.addItem(smallPotion);
            System.out.println("Acquired small potion!");
            removeChest(currentLocation, 26);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else if(userInput.equalsIgnoreCase("open middle")){
          if(isItemIdInRoom(currentLocation, 27)){
            Item potion = new Item(31,14, "Potion", "A healing potion is a potion that can be healed by a player.", "heal", false);
            player.addItem(potion);
            System.out.println("Acquired potion!");
            removeChest(currentLocation, 27);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else if(userInput.equalsIgnoreCase("open right")){
          if(isItemIdInRoom(currentLocation, 28)){
            Item bigPotion = new Item(32,20, "Big Potion", "A big healing potion is a potion that can be healed by a player.", "heal", false);
            player.addItem(bigPotion);
            System.out.println("Acquired big potion!");
            removeChest(currentLocation, 28);
          }
          else{
            System.out.println("that chest has already been opened.");
          }
          break;
        }
        else{
          System.out.println("Invalid input.");
        }
      }
    }
  }

  public Boolean isItemIdInRoom(Location location, int test){
    for(Item item : location.getItems()){
      if(item.getId()==test){
        return true;
      }
    }
    return false;
  }

  public void removeChest(Location location, int test){
    for(Item item : location.getItems()){
      if(item.getId()==test){
        location.removeItem(item);
        break;
      }
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

        Riddle riddle = Riddle.getRiddle(riddlesList, riddleId);

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
    Type listType = new TypeToken<List<Trivia>>() {}.getType();
    List<Trivia> triviasList = new Gson().fromJson(triviaJsonArray, listType);
    List<Trivia> filteredTrivias = triviasList.stream().filter(t -> t.getId() == triviaID).collect(
        Collectors.toList());
    if (filteredTrivias.isEmpty()) {
      System.out.println("Invalid Trivia ID");
      return;
    }
    Collections.shuffle(filteredTrivias);
    Trivia trivia = filteredTrivias.get(0);

    boolean won = false;
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

