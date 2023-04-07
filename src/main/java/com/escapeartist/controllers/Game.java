package com.escapeartist.controllers;

import com.escapeartist.models.Item;
import com.escapeartist.models.NPC;
import com.escapeartist.models.Player;
import com.escapeartist.models.Riddle;
import com.escapeartist.models.Room;
import com.escapeartist.models.Trivia;
import com.escapeartist.util.Deserializer;
import com.escapeartist.util.AudioPlayer;
import com.escapeartist.models.Boss;
import com.escapeartist.views.GUI;


import java.util.List;
import java.util.Random;

public class Game {
  private Room currentRoom;
  private Player player= new Player();
  private NPC npc;
  private AudioPlayer backgroundMusic;
  private AudioPlayer bossMusic;
  private Boss boss;



  private final List<String> triviaRooms = List.of(
      new String[]{"Shinto Exhibit", "Middle Ages Exhibit", "Middle Kingdom Exhibit"});

  private final List<String> riddleRooms = List.of(
      new String[]{"Old Kingdom Exhibit", "Dark Ages Exhibit", "Koto Exhibit"});

  private final List<String> chestRooms = List.of(
      new String[]{"Homeopathy Exhibit", "Weapons Exhibit", "Armor Exhibit"});

  Deserializer jsonData;
  List<Room> roomJSON;
  List<Trivia> triviaJSON;
  List<Riddle> riddleJSON;


  public Game() {
    jsonData = new Deserializer();
    roomJSON = jsonData.deserializeLocations();
    triviaJSON = jsonData.deserializeTrivia();
    riddleJSON = jsonData.deserializeRiddles();
    currentRoom = roomJSON.get(0);
    boss = new Boss();

    backgroundMusic = new AudioPlayer("soft-piano.wav");
    bossMusic = new AudioPlayer("boss_music.wav");
    backgroundMusic.play();
  }

  private boolean isBossInCurrentRoom() {
    return currentRoom.getName().equals(getCurrentRoom());
  }

  public void bossEnters() {
    backgroundMusic.stop();
    bossMusic.play();
  }

  public void checkForBoss() {
    if (isBossInCurrentRoom() && !boss.isActive()) {
      boss.setActive(true);
      bossEnters();
    }
  }

  public void moveNorth() {
    if(currentRoom.getExits().containsKey("north")){
      String roomSet=currentRoom.getExits().get("north");
      for(Room room : roomJSON){
        if(room.getName().equalsIgnoreCase(roomSet)){
          currentRoom=room;
        }
      }
    } else{
      System.out.println("You cannot go that way.");
    }
    checkForBoss();
  }

  public void moveSouth() {
    if(currentRoom.getExits().containsKey("south")){
      String roomSet=currentRoom.getExits().get("south");
      for(Room room : roomJSON){
        if(room.getName().equalsIgnoreCase(roomSet)){
          currentRoom=room;
        }
      }
    } else{
      System.out.println("You cannot go that way.");
    }
    checkForBoss();
  }

  public void moveEast() {
    if(currentRoom.getExits().containsKey("east")){
      String roomSet=currentRoom.getExits().get("east");
      for(Room room : roomJSON){
        if(room.getName().equalsIgnoreCase(roomSet)){
          currentRoom=room;
        }
      }
    } else{
      System.out.println("You cannot go that way.");
    }
    checkForBoss();
  }

  public void moveWest() {
    if(currentRoom.getExits().containsKey("west")){
      String roomSet=currentRoom.getExits().get("west");
      for(Room room : roomJSON){
        if(room.getName().equalsIgnoreCase(roomSet)){
          currentRoom=room;
        }
      }
    } else{
      System.out.println("You cannot go that way.");
    }
    checkForBoss();
  }

  public String getNPCQuestion() {
    String question = "";
    if (!currentRoom.getNpc().isEmpty()) {
      if (triviaRooms.contains(currentRoom.getName())) {
        question = getTriviaQuestion();
      } else if (riddleRooms.contains(currentRoom.getName())) {
        question = getRiddleQuestion();
      }
    } else {
      question = "No NPC to talk to in this room.";
    }
    return question;
  }

  public String talkNPC(String question, String playerAnswer) {
    String result = "";
    if (!currentRoom.getNpc().isEmpty()) {
      if (triviaRooms.contains(currentRoom.getName())) {
        result = handleTriviaAnswer(question, playerAnswer);
      } else if (riddleRooms.contains(currentRoom.getName())) {
        result = handleRiddleAnswer(question, playerAnswer);
      }
    } else {
      result = "No NPC to talk to in this room.";
    }
    return result;
  }


  public String handleTriviaAnswer(String question, String playerAnswer) {
    Trivia trivia = triviaJSON.stream()
        .filter(t -> t.getQuestion().equals(question))
        .findFirst()
        .orElse(null);

    if (trivia != null && playerAnswer.equalsIgnoreCase(trivia.getAnswer())) {
      currentRoom.addKey();
      currentRoom.getNpc().remove(currentRoom.getNpc().get(0));
      triviaJSON.remove(trivia);
      return "Correct! A key drops to the ground as the NPC begins to disappear.";
    } else {
      return "Incorrect. You will have to try again.";
    }
  }

  public String handleRiddleAnswer(String question, String playerAnswer) {
    Riddle riddle = riddleJSON.stream()
        .filter(r -> r.getRiddle().equals(question))
        .findFirst()
        .orElse(null);

    if (riddle != null && playerAnswer.equalsIgnoreCase(riddle.getAnswer())) {
      currentRoom.addKey();
      currentRoom.getNpc().remove(currentRoom.getNpc().get(0));
      riddleJSON.remove(riddle);
      return "Correct! A key drops to the ground as the NPC begins to disappear.";
    } else {
      return "Incorrect. You will have to try again.";
    }
  }

  public String getTriviaQuestion() {
    Trivia trivia = new Trivia();
    trivia = trivia.getQuestion(triviaJSON);
    return trivia.getQuestion();
  }

  public String getRiddleQuestion() {
    Riddle riddle = new Riddle();
    riddle = riddle.getRiddle(riddleJSON);
    return riddle.getRiddle();
  }


  public void fightNPC(){
    if(!currentRoom.getNpc().isEmpty()){
      Random rand = new Random();
      int playerAttackChance = rand.nextInt(10);
      int enemyAttackChance = rand.nextInt(10);
      NPC enemy = currentRoom.getNpc().get(0);

      if(playerAttackChance>5){
        GUI.getGameTextDisplayArea().append("You hit " + enemy.getName() + " and do " + player.getAttack() + " points of damage.\n");
        enemy.setHealth(enemy.getHealth()-player.getAttack());
        if(enemy.getHealth()<1){
          GUI.getGameTextDisplayArea().append("You defeat the " + enemy.getName() + "\n");
          currentRoom.getNpc().remove(enemy);
          currentRoom.addKey();
          GUI.getGameTextDisplayArea().append("A small key drops to the floor as the " + enemy.getName() + " dissolves into thin air.\n");
        }
      } else{
        GUI.getGameTextDisplayArea().append("You try to attack " + enemy.getName() + " but miss!\n");
      }

      if(enemyAttackChance>6 & !currentRoom.getNpc().isEmpty()){
        GUI.getGameTextDisplayArea().append("The " + enemy.getName() + " attacks you back for " + enemy.getAttackPower() + " points of damage.\n");
      } else if(enemyAttackChance<6 & !currentRoom.getNpc().isEmpty()){
        GUI.getGameTextDisplayArea().append("The " + enemy.getName() + " tries to hit you back but misses.\n");
      }

    } else{
      GUI.getGameTextDisplayArea().append("No NPC to fight in this room.\n");
    }
  }

  public boolean askRiddle(String playerAnswer){
    Riddle riddle = new Riddle();
    riddle = riddle.getRiddle(riddleJSON);
    System.out.println(riddle.getRiddle());
    if(playerAnswer.equalsIgnoreCase(riddle.getAnswer())){
      System.out.println("Correct!");
      System.out.println("A key drops to the ground as the " + npc.getName() + " begins to disappear.");
      currentRoom.addKey();
      System.out.println("The final boss has trouble hitting chain mail armor.....");
      currentRoom.setNpc(null);
      riddleJSON.remove(riddle);
      return true;
    } else{
      System.out.println("Incorrect. You will have to try again.");
      return false;
    }
  }

  public boolean askTrivia(String playerAnswer){
    Trivia trivia = new Trivia();
    trivia = trivia.getQuestion(triviaJSON);
    System.out.println(trivia.getQuestion());
    if(playerAnswer.equalsIgnoreCase(trivia.getAnswer())){
      System.out.println("Correct!");
      System.out.println("A key drops to the ground as the " + npc.getName() + " begins to disappear.");
      currentRoom.addKey();
      System.out.println("The final boss has trouble dodging the scimitar.....");
      currentRoom.setNpc(null);
      triviaJSON.remove(trivia);
      return true;
    } else{
      System.out.println("Incorrect. You will have to try again.");
      return false;
    }
  }


  public void pickUpItem(){
    if(currentRoom.getItems().isEmpty()){
      System.out.println("there are no items in this room");
    }
    Item addItem = currentRoom.getItems().get(0);
    player.getInventory().add(addItem);
    currentRoom.removeItem(addItem);
  }


  public Room getCurrentRoom() {
    return currentRoom;
  }

  public void setCurrentRoom(Room currentRoom) {
    this.currentRoom = currentRoom;
  }

  public Player getPlayer() {
    return player;
  }

  public List<Room> getRoomJSON() {
    return roomJSON;
  }

  public List<String> getChestRooms() {
    return chestRooms;
  }
}