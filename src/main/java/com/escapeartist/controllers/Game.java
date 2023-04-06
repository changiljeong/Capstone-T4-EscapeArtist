package com.escapeartist.controllers;

import com.escapeartist.models.Item;
import com.escapeartist.models.NPC;
import com.escapeartist.models.Player;
import com.escapeartist.models.Riddle;
import com.escapeartist.models.Room;
import com.escapeartist.models.Trivia;
import com.escapeartist.util.Deserializer;
import java.util.List;
import java.util.Random;

public class Game {
  private Room currentRoom;
  private Player player= new Player();
  private NPC npc;

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
  }

  public String talkNPC(String playerAnswer){
    if(!currentRoom.getNpc().isEmpty()){
      if(triviaRooms.contains(currentRoom.getName())){
        System.out.println("answer my trivia question and you will get a key!");
        askTrivia();
      } else if(riddleRooms.contains(currentRoom.getName())){
        System.out.println("solve my riddle and you will get a key!");
        askRiddle();
      }
    } else{
      System.out.println("No NPC to talk to in this room.");
    }
    return playerAnswer;
  }

  public void fightNPC(){
    if(!currentRoom.getNpc().isEmpty()){
      Random rand = new Random();
      int playerAttackChance = rand.nextInt(10);
      int enemyAttackChance = rand.nextInt(10);
      NPC enemy = currentRoom.getNpc().get(0);

      if(playerAttackChance>5){
        System.out.println("You hit " + enemy.getName() + " and do " + player.getAttack() + " points of damage.");
        enemy.setHealth(enemy.getHealth()-player.getAttack());
        if(enemy.getHealth()<1){
          System.out.println("You defeat the " + enemy.getName());
          currentRoom.getNpc().remove(enemy);
          currentRoom.addKey();
          System.out.println("A small key drops to the floor as the " + enemy.getName() + "dissolves into thin air.");
        }
      } else{
        System.out.println("You try to attack " + enemy.getName() + " but miss!");
      }

      if(enemyAttackChance>6 && enemy.getHealth()>0){
        System.out.println("The " + enemy.getName() + " attacks you back for " + enemy.getAttackPower() + " points of damage.");
      } else{
        System.out.println("The " + enemy.getName() + " tries to hit you back but misses.");
      }

    } else{
      System.out.println("No NPC to fight in this room.");
    }
  }

  public void openChest(){
    if(chestRooms.contains(currentRoom.getName())){
      System.out.println("Chest room");
    }else{
      System.out.println("No chests in this room");
    }
  }

  public String getNPCQuestion() {
    // Return the NPC's question
    return null;
  }

  public void askRiddle(){
    Riddle riddle = new Riddle();
    riddle = riddle.getRiddle(riddleJSON);
    System.out.println(riddle.getRiddle());
    String userInput = "testing- REMOVE THIS WHEN YOU FIGURE OUT INPUT";
    if(userInput.equalsIgnoreCase(riddle.getAnswer())){
      System.out.println("Correct!");
      System.out.println("A key drops to the ground as the " + npc.getName() + " begins to disappear.");
      currentRoom.addKey();
      System.out.println("The final boss has trouble hitting chain mail armor.....");
      currentRoom.setNpc(null);
      riddleJSON.remove(riddle);
    } else{
      System.out.println("Incorrect. You will have to try again.");
    }
  }

  public void askTrivia(){
    Trivia trivia = new Trivia();
    trivia = trivia.getQuestion(triviaJSON);
    System.out.println(trivia.getQuestion());
    String userInput = "testing- REMOVE THIS WHEN YOU FIGURE OUT INPUT";
    if(userInput.equalsIgnoreCase(trivia.getAnswer())){
      System.out.println("Correct!");
      System.out.println("A key drops to the ground as the " + npc.getName() + " begins to disappear.");
      currentRoom.addKey();
      System.out.println("The final boss has trouble dodging the scimitar.....");
      currentRoom.setNpc(null);
      triviaJSON.remove(trivia);
    } else{
      System.out.println("Incorrect. You will have to try again.");
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
  }
