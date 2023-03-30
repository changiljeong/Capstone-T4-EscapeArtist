package com.escapeartist.models;

public class Enemy {
  private String name;
  private int health;
  private int attackDamage;

  public Enemy(String name, int health, int attackDamage) {
    this.name = name;
    this.health = health;
    this.attackDamage = attackDamage;
  }

  public String getName() {
    return name;
  }

  public int attack() {
    // attack logic like how much damage
    return attackDamage;
  }

  public void receiveDamage(int damage) {
    health -= damage;
    if (health < 0) {
      health = 0;
    }
  }

  public boolean isDefeated() {
    return health <= 0;
  }
}

