package com.escapeartist.models;

// Import necessary classes for the GUI if any i dunno
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Fight {
  private Player player;
  private Enemy enemy;
  private JTextArea gameLog;
  private JButton attackButton;

  public Fight(Player player, Enemy enemy, JTextArea gameLog) {
    this.player = player;
    this.enemy = enemy;
    this.gameLog = gameLog;

    createGUI();
  }

  private void createGUI() {
    attackButton = new JButton("Attack");
    attackButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        executeRound();
      }
    });

    // Add the attack button to the gui
    // yourPanel.add(attackButton); (I think from what i read)
  }

  public JButton getAttackButton() {
    return attackButton;
  }

  private void executeRound() {
    // Player attacks enemy
    int playerDamage = player.attack();
    enemy.receiveDamage(playerDamage);
    gameLog.append("Player dealt " + playerDamage + " damage to the " + enemy.getName() + ".\n");

    if (enemy.isDefeated()) {
      gameLog.append(enemy.getName() + " has been defeated!\n");
      // Handle victory, e.g. reward player, end the fight, etc.
      attackButton.setEnabled(false);
      return;
    }

    // Enemy attacks player
    int enemyDamage = enemy.attack();
    player.receiveDamage(enemyDamage);
    gameLog.append(enemy.getName() + " dealt " + enemyDamage + " damage to the player.\n");

    if (player.isDefeated()) {
      gameLog.append("Player has been defeated...\n");
      // Handle defeat, e.g. game over, respawn, etc.
      attackButton.setEnabled(false);
    }
  }
}
