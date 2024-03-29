package com.escapeartist.models;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.io.FileReader;
import java.io.IOException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;

public class Boss {
  private int health;
  private int baseAttack;
  private int baseDefense;
  private int[] attackDamageRange;
  private boolean isActive;
  private List<String> taunts;

  public Boss(Player player) {
    this.health = 200;
    this.baseAttack = 5;
    this.health = 40;
    this.baseAttack = 30;
    this.baseDefense = 40;
    this.attackDamageRange = new int[]{5,10,15,20,25,30,35,40,45,50,55,60,65,70,75,80};
    this.isActive = false;
    loadTaunts();
  }

  public void setActive(boolean active) {
    isActive = active;
  }

  public int getHealth() {
    return health;
  }

  public int getRandomAttackDamage() {
    Random rand = new Random();
    return rand.nextInt(attackDamageRange[1] - attackDamageRange[0]) + attackDamageRange[0];
  }

  public String attackPlayer(Player player) {
    String bossTaunt = getRandomTaunt();
    System.out.println(bossTaunt);

    Random rand = new Random();
    int attackType = rand.nextInt(100);

    int damage = getRandomAttackDamage();
    int probability = 0;

    if (attackType < 10) {
      probability = 1;
      damage *= .5;
    } else if (attackType < 50) {
      probability = 2;
      damage *= 1;
    } else if (attackType < 80) {
      probability = 2;
      damage *= 1.5;
    } else if (attackType < 80) {
      probability = 3;
      damage *= 2;
    } else {
      probability = 4;
      damage *= 3;
    }

    System.out.println("The boss attacks with a level " + probability + " attack and deals " + damage + " damage.");
    player.takeDamage(damage);
    return bossTaunt + "\nThe boss attacks with a level " + probability + " attack and deals " + damage + " damage.";
  }

  public void takeDamage(int damage) {
    this.health -= damage;
    if (this.health < 0) {
      this.health = 0;
    }
  }

  private void loadTaunts() {
    try {
      Gson gson = new Gson();
      Type type = new TypeToken<List<String>>() {}.getType();
      InputStream inputStream = Boss.class.getClassLoader().getResourceAsStream("taunts.json");
      if (inputStream == null) {
        throw new FileNotFoundException("taunts.json not found in the resources folder.");
      }
      InputStreamReader reader = new InputStreamReader(inputStream);
      taunts = gson.fromJson(reader, type);
      reader.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String getRandomTaunt() {
    Random rand = new Random();
    return taunts.get(rand.nextInt(taunts.size()));
  }

  public boolean isActive() {
    return isActive;
  }

}
