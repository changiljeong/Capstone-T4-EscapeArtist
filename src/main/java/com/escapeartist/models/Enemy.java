package com.escapeartist.models;

public class Enemy {
  // Fields for the enemy's name, health, and attack damage
  private String name;
  private int health;
  private int attackDamage;

  // Constructor for creating a new enemy with a given name, health, and attack damage
  public Enemy(String name, int health, int attackDamage) {
    this.name = name;
    this.health = health;
    this.attackDamage = attackDamage;
  }

  public String getName() {
    // Get the enemy's name
    return name;
  }

  public int attack() {
    // Get the amount of damage the enemy can do when it attacks
    return attackDamage;
  }

  public void receiveDamage(int damage) {
    // Reduce the enemy's health by a given amount of damage
    health -= damage;
    if (health < 0) {
      health = 0;
    }
  }

  public boolean isDefeated() {
    // Check if the enemy has been defeated (i.e. its health is zero or less)
    return health <= 0;
  }


}

