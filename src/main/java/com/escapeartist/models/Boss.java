package com.escapeartist.models;

import java.util.Random;

public class Boss {
  private int health;
  private int baseAttack;
  private int baseDefense;
  private int[] attackDamageRange;
  private boolean isActive;

  public Boss() {
    this.health = 300;
    this.baseAttack = 30;
    this.baseDefense = 40;
    this.attackDamageRange = new int[]{10, 50};
    this.isActive = false;
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

  public boolean isActive() {
    return true;
  }
}
