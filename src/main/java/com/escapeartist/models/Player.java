package com.escapeartist.models;

public class Player {
    private int hp;
    private int attack;
    private int defense;

    public Player(int hp, int attack, int defense) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
    }

    public void takeDamage(int damage) {
        int actualDamage = Math.max(damage - defense, 0);
        hp -= actualDamage;
    }

    public int attackEnemy() {
        return attack;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getAttack() {
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }


}
