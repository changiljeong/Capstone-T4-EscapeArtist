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

  public Boss() {
    this.health = 300;
    this.baseAttack = 30;
    this.baseDefense = 40;
    this.attackDamageRange = new int[]{10, 50};
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

  public void attackPlayer(Player player) {
    System.out.println(getRandomTaunt());

    Random rand = new Random();
    int attackType = rand.nextInt(100);

    int damage = getRandomAttackDamage();
    int probability = 0;

    if (attackType < 50) {
      probability = 1;
      damage *= 1;
    } else if (attackType < 80) {
      probability = 2;
      damage *= 2;
    } else {
      probability = 3;
      damage *= 3;
    }

    System.out.println("The boss attacks with a level " + probability + " attack and deals " + damage + " damage.");
    player.setHealth(player.getHealth() - damage);
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
