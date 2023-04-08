package com.escapeartist.models;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private int health;
    private int attack;
    private int defense;
    private Item itemToEquip;
    private Item equippedArmor;
    private Item equippedWeapon;
    private List<Item> inventory;

    public Player() {
        this.health = 100;
        this.attack = 20;
        this.defense = 50;
        this.inventory = new ArrayList<Item>();
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
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

    public Item getItemToEquip() {
        return itemToEquip;
    }

    public Item getEquippedWeapon() {
        return equippedWeapon;
    }

    public Item getEquippedArmor() {
        return equippedArmor;
    }

    public void setEquippedArmor(Item equippedArmor) {
        this.equippedArmor = equippedArmor;
    }

    public String setEquippedItem(String itemString) {
        Item itemToEquip = null;
        for (Item item : inventory) {
            if (item.getName().equalsIgnoreCase(itemString)) {
                itemToEquip = item;
                break;
            }
        }
        if (itemToEquip != null) {
            if (itemToEquip.getEquable() && itemToEquip.getType().equalsIgnoreCase("weapon")) {
                if (getEquippedWeapon() != null) {
                    setAttack(getAttack() - getEquippedWeapon().getValue());
                }
                this.equippedWeapon = itemToEquip;
                setAttack(getAttack() + getEquippedWeapon().getValue());
                return itemToEquip.getName()+ " equipped.";
            } else if (itemToEquip.getEquable() && itemToEquip.getType().equalsIgnoreCase("armor")) {
                if (getEquippedArmor() != null) {
                    setDefense(getDefense() - getEquippedArmor().getValue());
                }
                this.equippedArmor = itemToEquip;
                setDefense(getDefense() + getEquippedArmor().getValue());
                return itemToEquip.getName()+ " equipped.";
            } else {
                return "Cannot equip " + itemToEquip.getName();
            }
        } else {
            return "Item not found in inventory.";
        }
    }

    public void attack(Boss boss) {
        int damage = this.attack;
        if (equippedWeapon != null) {
            damage += equippedWeapon.getValue();
        }
        boss.takeDamage(damage);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }

    public void addToInventory(Item item){
        inventory.add(item);
    }

    public void removeFromInventory(Item item){
        inventory.remove(item);
    }
}
