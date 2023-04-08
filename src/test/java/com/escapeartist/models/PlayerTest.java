package com.escapeartist.models;

import static org.junit.jupiter.api.Assertions.*;

import com.escapeartist.models.Item;
import com.escapeartist.models.Player;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlayerTest {
  private Player player;

  @BeforeEach
  void setUp() {
    this.player = new Player();
    player.getInventory().add(new Item(20, "Longsword", "A straight blade from ancient times.", "weapon", true));
    player.getInventory().add(new Item(15, "Plate Armor",
        "Heavy and thick plates meant to deflect away blows.", "armor", true));
    player.getInventory().add(new Item(20, "Potion", "A potion to heal health", "heal", false));
  }

  @Test
  @DisplayName("Equips a weapon")
  void setEquippedWeapon() {
    String result = player.setEquippedItem("Longsword");
    assertEquals("Longsword equipped.", result);
    assertEquals(40, player.getAttack());
  }

  @Test
  @DisplayName("Equips an armor")
  void setEquippedArmor() {
    String result = player.setEquippedItem("Plate Armor");
    assertEquals("Plate Armor equipped.", result);
    assertEquals(65, player.getDefense());
  }

  @Test
  @DisplayName("Tries to equip an invalid item")
  void setEquippedInvalidItem() {
    String result = player.setEquippedItem("Potion");
    assertEquals("Cannot equip Potion", result);
    assertNull(player.getEquippedWeapon());
    assertNull(player.getEquippedArmor());
    assertEquals(20, player.getAttack());
    assertEquals(50, player.getDefense());
  }

  @Test
  @DisplayName("Tries to equip a nonexistent item")
  void setEquippedNonexistentItem() {
    String result = player.setEquippedItem("Hammer");
    assertEquals("Item not found in inventory.", result);
    assertNull(player.getEquippedWeapon());
    assertNull(player.getEquippedArmor());
    assertEquals(20, player.getAttack());
    assertEquals(50, player.getDefense());
  }
}
