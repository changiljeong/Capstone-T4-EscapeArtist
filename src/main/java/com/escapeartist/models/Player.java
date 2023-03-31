package com.escapeartist.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.stream.Collectors;

public class Player {

    // Fields for the player's stats and inventory
    @SerializedName("hp")
    private int hp;

    @SerializedName("attack")
    private int attack;

    @SerializedName("defense")
    private int defense;

    @SerializedName("inventory")
    private List<Item> inventory;

    // Field for the player's name
    @SerializedName("name")
    private String name;

    // Field for the player's current location
    @SerializedName("currentLocation")
    private int currentLocation;

    // Fields for the player's equipped armor and weapon
    @SerializedName("equippedArmor")
    private Item equippedArmor;

    @SerializedName("equippedWeapon")
    private Item equippedWeapon;

    // Constructor for creating a new player
    public Player(int hp, int attack, int defense, List<Item> inventory,
        int currentLocation, Item equippedArmor, Item equippedWeapon) {
        this.hp = hp;
        this.attack = attack;
        this.defense = defense;
        this.inventory = inventory;
        this.currentLocation = currentLocation;
        this.equippedArmor = equippedArmor;
        this.equippedWeapon = equippedWeapon;
    }

    // Method for printing the player's status to the console
    public void playerStatus(JsonObject gameData) {
        // Print the start of the status message
        System.out.println(
            gameData.getAsJsonObject("dialogue").get("player_status_start")
                .getAsString());

        // Print the player's HP, attack, and defense stats
        System.out.println(
            gameData.getAsJsonObject("dialogue").get("player_status_hp")
                .getAsString() + getHp());
        System.out.println(
            gameData.getAsJsonObject("dialogue").get("player_status_attack")
                .getAsString() + getAttack());
        System.out.println(
            gameData.getAsJsonObject("dialogue").get("player_status_defense")
                .getAsString() + getDefense());

        // Create a list of the player's inventory item names and join them into a string
        List<String> inventoryNames = inventory.stream().map(Item::getName)
            .collect(Collectors.toList());
        String inventoryString = String.join(", ", inventoryNames);

        // Print the player's inventory
        System.out.println(
            gameData.getAsJsonObject("dialogue").get("player_status_inventory")
                .getAsString() + inventoryString);

        // Print the name of the equipped weapon, if there is one
        if (equippedWeapon != null) {
            System.out.println(gameData.getAsJsonObject("dialogue")
                .get("player_status_weapon_equipped").getAsString()
                + getEquippedWeapon().getName());
        } else {
            // Print a message indicating that no weapon is equipped
            System.out.println(gameData.getAsJsonObject("dialogue")
                .get("player_status_weapon_not_equipped").getAsString());
        }

        // Print the name of the equipped armor, if there is one
        if (equippedArmor != null) {
            System.out.println(gameData.getAsJsonObject("dialogue")
                .get("player_status_armor_equipped").getAsString()
                + getEquippedArmor().getName());
        } else {
            // Print a message indicating that no armor is equipped
            System.out.println(gameData.getAsJsonObject("dialogue")
                .get("player_status_armor_not_equipped").getAsString());
        }

        // Print the end of the status message
        System.out.println(
            gameData.getAsJsonObject("dialogue").get("player_status_end")
                .getAsString());
    }

    // Attack method
    public int attack() {
        int attackValue = attack;
        if (equippedWeapon != null) {
            attackValue += equippedWeapon.getAttackBonus(); // Item class weapon has an attack attribute or how much damage it deals
        }
        // customize the attack logic here like add random damage variation
        return attackValue;
    }

    // Receive damage method
    public void receiveDamage(int damage) {
        int damageTaken = damage;
        if (equippedArmor != null) {
            damageTaken -= equippedArmor.getDefenseBonus(); // Assuming Item class with the armor you pick up has a defense bonus attribute
            if (damageTaken < 0) {
                damageTaken = 0;
            }
        }
        hp -= damageTaken;
        if (hp < 0) {
            hp = 0;
        }
    }

    // Check if the player is defeated
    public boolean isDefeated() {
        return hp <= 0;
    }


    public void addItem(Item item) {
        // Add an item to the player's inventory
        inventory.add(item);
    }


    public void dropItem(Item item) {
        // Remove an item from the player's inventory
        inventory.remove(item);
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getHp() {
        // Get the player's current HP
        return hp;
    }

    public int getAttack() {
        // Get the player's current attack stat
        return attack;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public int getDefense() {
        // Get the player's current defense stat
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public List<Item> getInventory() {
        // Get the player's inventory
        return inventory;
    }

    public int getCurrentLocation() {
        // Get the player's current location
        return currentLocation;
    }

    public void setCurrentLocation(int currentLocation) {
        // Set the player's current location
        this.currentLocation = currentLocation;
    }

    public Item getEquippedArmor() {
        // Get the item that is currently equipped as armor
        return equippedArmor;
    }

    public void setEquippedArmor(Item equippedArmor) {
        // Set the item that should be equipped as armor
        this.equippedArmor = equippedArmor;
    }

    public Item getEquippedWeapon() {
        // Get the item that is currently equipped as a weapon
        return equippedWeapon;
    }

    public void setEquippedWeapon(Item equippedWeapon) {
        // Set the item that should be equipped as a weapon
        this.equippedWeapon = equippedWeapon;
    }

    public String getName() {
        // Get the player's name
        return name;
    }
}
