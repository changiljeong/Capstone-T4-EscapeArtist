package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;

public class NPC {
    @SerializedName("name")
    private String name;

    @SerializedName("health")
    private int health;

    @SerializedName("attackPower")
    private int attackPower;

    @SerializedName("defense")
    private int defense;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }
}
