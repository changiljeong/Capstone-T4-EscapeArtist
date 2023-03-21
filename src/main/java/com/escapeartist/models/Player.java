package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Player {

    @SerializedName("hp")
    private int hp;

    @SerializedName("attack")
    private int attack;

    @SerializedName("defense")
    private int defense;

    @SerializedName("inventory")
    private List<Item> inventory;

    @SerializedName("currentLocation")
    private int currentLocation;

    @SerializedName("equippedArmor")
    private Item equippedArmor;

    @SerializedName("equippedWeapon")
    private Item equippedWeapon;

    public Player(int hp, int attack, int defense, List<Item> inventory, int currentLocation, Item equippedArmor, Item equippedWeapon) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.inventory = inventory;
        this.currentLocation = currentLocation;
        this.equippedArmor = equippedArmor;
        this.equippedWeapon = equippedWeapon;
    }

    public int getHp() {
        return hp;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public int getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(int currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Item getEquippedArmor() {
        return equippedArmor;
    }

    public void setEquippedArmor(Item equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    public Item getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(Item equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }
}

