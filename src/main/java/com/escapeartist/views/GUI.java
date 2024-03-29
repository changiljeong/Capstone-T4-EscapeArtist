package com.escapeartist.views;

import com.escapeartist.controllers.Game;
import com.escapeartist.models.Boss;
import com.escapeartist.models.Chest;
import com.escapeartist.models.Item;
import com.escapeartist.models.Room;
import com.escapeartist.util.AudioPlayer;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class GUI extends JFrame {

  private static JTextArea gameTextDisplayArea;
  private JPanel buttonPanel;
  private JButton northButton;
  private JButton eastButton;
  private JButton westButton;
  private JButton southButton;
  private JButton attackButton;
  private JButton talkButton;
  private JButton pickUpButton;
  private JLabel countdownLabel;
  private Timer countdownTimer;
  private int remainingTimeInSeconds;
  private JButton helpButton;
  private Room bossRoom;
  private JLabel spiritImageLabel;
  private boolean isFighting = false;

  private boolean musicMuted = false;

  private Game game;
  private JTextArea roomDescription;

  private JPanel textPanel;
  public JTextArea textArea;

  private JPanel sidePanel;
  private DefaultListModel<String> inventoryModel;
  private JList<String> inventoryList;
  private JButton useButton;
  private JButton equipButton;

  private JFrame mapFrame;

  public GUI() {
    game = new Game();

    setTitle("Escape Artist");  // Set window title

    // Create splash screen panel
    JPanel splashScreenPanel = new JPanel(new BorderLayout());
    ImageIcon splashScreenIcon = new ImageIcon(getClass().getResource("/Escape_Artist2.png"));
    JLabel splashScreenLabel = new JLabel(splashScreenIcon);
    splashScreenPanel.add(splashScreenLabel, BorderLayout.CENTER);
    add(splashScreenPanel, BorderLayout.CENTER);

    //method to initialize the music when game starts

    // Create enter key listener for clearing the splash screen
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          remove(splashScreenPanel);
          // Add code for description panel here
          JTextArea descriptionArea = new JTextArea(
              "Welcome to Escape Artist, the exciting text-based adventure game set in a museum where you'll have to solve riddles, puzzles, and battles to gather resources and prepare for an epic battle against the museum curator.\n"
                  + "\n"
                  + "As you explore the museum, use your wits and strategic thinking to complete each challenge before moving on to the next one. Remember, time is limited! You have only 5 minutes to gather resources, so act quickly and efficiently.\n"
                  + "\n"
                  + "The more resources you gather, the better prepared you'll be for the final battle against the museum curator. With an immersive storyline and intense challenges, Escape Artist is a game that will keep you on the edge of your seat until the very end. Do you have what it takes to gather the resources and defeat the museum curator before time runs out? Play Escape Artist now and find out!");
          descriptionArea.setEditable(false);
          descriptionArea.setLineWrap(true);
          descriptionArea.setWrapStyleWord(true);
          descriptionArea.setFont(new Font("Arial", Font.PLAIN, 16));
          JScrollPane scrollPane = new JScrollPane(descriptionArea);
          scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
          add(scrollPane, BorderLayout.CENTER);
          JButton startButton = new JButton("Start Game");
          startButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
              remove(scrollPane);
              initGUI();
              revalidate();
              // Remove the start button
              Container parent = startButton.getParent();
              if (parent != null) {
                parent.remove(startButton);
              }
            }
          });

          // Add key listener for the startButton
          startButton.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
              if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                startButton.doClick(); // Trigger the start button click event
              }
            }
          });

          add(startButton, BorderLayout.SOUTH);

          // Make the startButton focusable and set focus to it
          startButton.setFocusable(true);
          startButton.requestFocusInWindow();

          // End code for description panel
          revalidate();
        }
      }
    });
    setFocusable(true);

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(580, 580);
    setVisible(true);
  }

  private void initGUI() {
    roomDescription = new JTextArea(
        game.getCurrentRoom().getDescription() + game.getCurrentRoom().getExits()
            + game.getCurrentRoom().getNpc());
    roomDescription.setEditable(false);

    JPanel mainContentPanel = new JPanel(new BorderLayout());

    countdownLabel = new JLabel();
    countdownLabel.setFont(new Font("Arial", Font.BOLD, 24));
    remainingTimeInSeconds = 1 * 30;
    updateCountdownLabel();
    countdownTimer = new Timer(1000, new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        remainingTimeInSeconds--;
        updateCountdownLabel();
        if (remainingTimeInSeconds <= 0) {
          countdownTimer.stop();
          setBossSpiritImage(); // Set the boss spirit image
          JOptionPane.showMessageDialog(GUI.this, "The boss fight has begun! Prepare for battle!",
              "Boss Fight", JOptionPane.WARNING_MESSAGE);
          fightMainBoss();
          game.checkAndPlayBossMusic();
        }
      }
    });
    countdownTimer.start();

    JPanel countdownPanel = new JPanel(new BorderLayout());
    countdownPanel.add(countdownLabel, BorderLayout.CENTER);

    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.add(roomDescription, BorderLayout.CENTER);
    buttonPanel = new JPanel();
    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
    add(mainPanel, BorderLayout.CENTER);
    spiritImageLabel = new JLabel();
    mainPanel.add(spiritImageLabel, BorderLayout.WEST);

    sidePanel = new JPanel(new BorderLayout());
    sidePanel.add(countdownPanel, BorderLayout.NORTH);
    inventoryModel = new DefaultListModel<>();
    inventoryList = new JList<>(inventoryModel);
    sidePanel.add(new JScrollPane(inventoryList), BorderLayout.CENTER);
    useButton = new JButton("Use");
    equipButton = new JButton("Equip");
    JPanel inventoryButtonPanel = new JPanel();
    inventoryButtonPanel.add(useButton);
    inventoryButtonPanel.add(equipButton);
    sidePanel.add(inventoryButtonPanel, BorderLayout.SOUTH);
    sidePanel.setPreferredSize(new Dimension(200, sidePanel.getPreferredSize().height));
    add(sidePanel, BorderLayout.EAST);

    JPanel topRightPanel = new JPanel(new BorderLayout());
    topRightPanel.add(sidePanel, BorderLayout.SOUTH);
    textPanel = new JPanel();
    textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
    textArea = new JTextArea(15, 40);
    textArea.setEditable(false);
    textArea.setLineWrap(true);
    textArea.setWrapStyleWord(true);
    textArea.setText(game.getCurrentRoom().getDescription());
    textPanel.add(new JScrollPane(textArea));

    topRightPanel.add(textPanel, BorderLayout.NORTH);

    //Text area that the game output will append to
    gameTextDisplayArea = new JTextArea(15, 40); // Set the rows and columns for the JTextArea
    gameTextDisplayArea.setEditable(false); // Set the JTextArea to be non-editable
    textPanel.add(gameTextDisplayArea); // Add the JTextArea to the panel
    gameTextDisplayArea.setLineWrap(true);
    gameTextDisplayArea.setWrapStyleWord(true);
    gameTextDisplayArea.setText("Items in room: \n" + game.getCurrentRoom().getItems() + "\n");

// Create a JScrollPane with the JTextArea
    JScrollPane scrollPane = new JScrollPane(gameTextDisplayArea);
    scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

// Add the JScrollPane (with the JTextArea inside) to the panel instead of adding the JTextArea directly
    textPanel.add(scrollPane);


    topRightPanel.add(textPanel, BorderLayout.NORTH);




    topRightPanel.add(textPanel, BorderLayout.NORTH);

    topRightPanel.add(textPanel, BorderLayout.NORTH);

    topRightPanel.add(textPanel, BorderLayout.NORTH);

    topRightPanel.add(textPanel, BorderLayout.NORTH);

    mainContentPanel.add(topRightPanel, BorderLayout.CENTER);
    spiritImageLabel = new JLabel();
    mainContentPanel.add(spiritImageLabel, BorderLayout.WEST);
    add(mainContentPanel, BorderLayout.CENTER);

    add(mainContentPanel, BorderLayout.SOUTH);

    mainContentPanel.add(topRightPanel, BorderLayout.EAST);
    spiritImageLabel = new JLabel();
    mainContentPanel.add(spiritImageLabel, BorderLayout.WEST);
    add(mainContentPanel, BorderLayout.CENTER);

    northButton = new JButton("Go North");
    eastButton = new JButton("Go East");
    westButton = new JButton("Go West");
    southButton = new JButton("Go South");
    attackButton = new JButton("Attack");
    talkButton = new JButton("Talk");
    pickUpButton = new JButton("Pick up Item");
    helpButton = new JButton("Help");

    northButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        game.moveNorth();
        textArea.setText(game.getCurrentRoom().getDescription());
        if (game.getCurrentRoom().getItems().isEmpty()) {
          gameTextDisplayArea.setText("Items in room: \n");
        } else {
          gameTextDisplayArea.setText("Items in room: \n" + game.getCurrentRoom().getItems());
        }
        if (game.getCurrentRoom().getNpc().isEmpty()) {
          gameTextDisplayArea.append("NPCs in the room: \n");
        } else {
          gameTextDisplayArea.append(
              "\n\nNPCs in the room:\n" + game.getCurrentRoom().getNpc().get(0).getName() + "\n");
        }
        if (!game.getCurrentRoom().getChests().isEmpty()) {
          gameTextDisplayArea.append("\n\nUnopened chests in the room: \n");
          for (Chest chest : game.getCurrentRoom().getChests()) {
            if (!chest.getOpened()) {
              gameTextDisplayArea.append(chest.getName() + "\n");
            }
          }
        }
        setButtonEnabled();
      }
    });

    southButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        game.moveSouth();
        textArea.setText(game.getCurrentRoom().getDescription());
        if (game.getCurrentRoom().getItems().isEmpty()) {
          gameTextDisplayArea.setText("Items in room: \n");
        } else {
          gameTextDisplayArea.setText("Items in room: \n" + game.getCurrentRoom().getItems());
        }
        if (game.getCurrentRoom().getNpc().isEmpty()) {
          gameTextDisplayArea.append("NPCs in the room: \n");
        } else {
          gameTextDisplayArea.append(
              "\n\nNPCs in the room:\n" + game.getCurrentRoom().getNpc().get(0).getName() + "\n");
        }
        if (!game.getCurrentRoom().getChests().isEmpty()) {
          gameTextDisplayArea.append("\n\nUnopened chests in the room: \n");
          for (Chest chest : game.getCurrentRoom().getChests()) {
            if (!chest.getOpened()) {
              gameTextDisplayArea.append(chest.getName() + "\n");
            }
          }
        }
        setButtonEnabled();
      }
    });

    eastButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        game.moveEast();
        textArea.setText(game.getCurrentRoom().getDescription());
        if (game.getCurrentRoom().getItems().isEmpty()) {
          gameTextDisplayArea.setText("Items in room: \n");
        } else {
          gameTextDisplayArea.setText("Items in room: \n" + game.getCurrentRoom().getItems());
        }
        if (game.getCurrentRoom().getNpc().isEmpty()) {
          gameTextDisplayArea.append("NPCs in the room: \n");
        } else {
          gameTextDisplayArea.append(
              "\n\nNPCs in the room:\n" + game.getCurrentRoom().getNpc().get(0).getName() + "\n");
        }
        if (!game.getCurrentRoom().getChests().isEmpty()) {
          gameTextDisplayArea.append("\n\nUnopened chests in the room: \n");
          for (Chest chest : game.getCurrentRoom().getChests()) {
            if (!chest.getOpened()) {
              gameTextDisplayArea.append(chest.getName() + "\n");
            }
          }
        }
        setButtonEnabled();
      }
    });

    westButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        game.moveWest();
        textArea.setText(game.getCurrentRoom().getDescription());
        if (game.getCurrentRoom().getItems().isEmpty()) {
          gameTextDisplayArea.setText("Items in room: \n");
        } else {
          gameTextDisplayArea.setText("Items in room: \n" + game.getCurrentRoom().getItems());
        }
        if (game.getCurrentRoom().getNpc().isEmpty()) {
          gameTextDisplayArea.append("NPCs in the room: \n");
        } else {
          gameTextDisplayArea.append(
              "\n\nNPCs in the room:\n" + game.getCurrentRoom().getNpc().get(0).getName() + "\n");
        }
        if (!game.getCurrentRoom().getChests().isEmpty()) {
          gameTextDisplayArea.append("\n\nUnopened chests in the room: \n");
          for (Chest chest : game.getCurrentRoom().getChests()) {
            if (!chest.getOpened()) {
              gameTextDisplayArea.append(chest.getName() + "\n");
            }
          }
        }
        setButtonEnabled();
      }
    });

    attackButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        game.fightNPC();
        setButtonEnabled();
      }
    });

    talkButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String npcQuestion = game.getNPCQuestion();
        com.escapeartist.views.AnswerDialog answerDialog = new com.escapeartist.views.AnswerDialog(
            GUI.this, npcQuestion);
        answerDialog.setVisible(true);

        String playerAnswer = answerDialog.getAnswer();

        String interactionResult = game.talkNPC(npcQuestion, playerAnswer);
        roomDescription.setText(game.getCurrentRoom().getDescription() + "\nItems in the room: \n"
            + game.getCurrentRoom().getItems().toString() + "\n\n" + interactionResult);
        gameTextDisplayArea.append(interactionResult + "\n");
        setButtonEnabled();
      }
    });

    pickUpButton.addActionListener(new ActionListener() {

      public void actionPerformed(ActionEvent e) {
        JFrame pickUpFrame = new JFrame("Pick Up Item");
        pickUpFrame.setSize(300, 200);
        pickUpFrame.setLocationRelativeTo(null);
        JPanel pickUpPanel = new JPanel();
        JLabel pickUpLabel = new JLabel("Please select an item to pick up:");
        pickUpPanel.add(pickUpLabel);
        JComboBox<String> itemsComboBox = new JComboBox<String>();
        for (Item item : game.getCurrentRoom().getItems()) {
          itemsComboBox.addItem(item.getName());
        }
        pickUpPanel.add(itemsComboBox);
        JButton pickUpButton = new JButton("Pick Up");
        pickUpButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            String selectedItem = (String) itemsComboBox.getSelectedItem();
            inventoryModel.addElement(selectedItem);
            game.pickUpItem();
            gameTextDisplayArea.append("You picked up the " + selectedItem + "\n");
            setButtonEnabled();
            pickUpFrame.dispose();
          }
        });
        pickUpPanel.add(pickUpButton);
        pickUpFrame.add(pickUpPanel);
        pickUpFrame.setVisible(true);
      }
    });

    equipButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String selectedItem = inventoryList.getSelectedValue();
        if (selectedItem != null)
          gameTextDisplayArea.append((game.getPlayer().setEquippedItem(selectedItem)));
      }
    });

    useButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String selectedItem = inventoryList.getSelectedValue();
        if (selectedItem == null) {
//          JOptionPane.showMessageDialog(this, "Please select an item to use.");
          return;
        }
        Item itemToUse = null;
        for (Item item : game.getPlayer().getInventory()) {
          if (item.getName().equalsIgnoreCase(selectedItem)) {
            itemToUse = item;
          }
        }
        switch (itemToUse.getType()) {
          case "weapon":
            gameTextDisplayArea.append(
                "You try to use the " + itemToUse.getName() + ". It has no effect.\n");
            break;
          case "key":
            if (game.getChestRooms().contains(game.getCurrentRoom().getName())) {
              openChest();
              game.getPlayer().getInventory().remove(itemToUse);
              game.getPlayer().getInventory().remove(itemToUse);
              DefaultListModel<String> listModel = (DefaultListModel<String>) inventoryList.getModel();
              listModel.removeElement(itemToUse.getName());
              setButtonEnabled();
              break;
            } else {
              System.out.println("No effect");
            }
          case "armor":
            gameTextDisplayArea.append(
                "You try to use the " + itemToUse.getName() + ". It has no effect.\n");
            break;
          case "heal":
            gameTextDisplayArea.append("You used the " + itemToUse.getName() + ".\n");
            game.getPlayer().setHealth(itemToUse.getValue() + game.getPlayer().getHealth());
            game.getPlayer().getInventory().remove(itemToUse);
            DefaultListModel<String> listModel = (DefaultListModel<String>) inventoryList.getModel();
            listModel.removeElement(itemToUse.getName());
            setButtonEnabled();
            break;
          case "map":
            showMap();
            break;
          default:
            gameTextDisplayArea.append("You cannot use this item.\n");
            break;
        }
        setButtonEnabled();
      }
    });

    helpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showHelpWindow();
      }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(northButton);
    buttonPanel.add(eastButton);
    buttonPanel.add(westButton);
    buttonPanel.add(southButton);
    buttonPanel.add(attackButton);
    buttonPanel.add(talkButton);
    buttonPanel.add(pickUpButton);
    buttonPanel.add(helpButton);
    setButtonEnabled();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1290, 760);
    setVisible(true);
    add(buttonPanel, BorderLayout.SOUTH);

  }

  public void setButtonEnabled() {
    northButton.setEnabled(game.getCurrentRoom().getExits().containsKey("north"));
    eastButton.setEnabled(game.getCurrentRoom().getExits().containsKey("east"));
    westButton.setEnabled(game.getCurrentRoom().getExits().containsKey("west"));
    southButton.setEnabled(game.getCurrentRoom().getExits().containsKey("south"));
    attackButton.setEnabled(!game.getCurrentRoom().getNpc().isEmpty());
    talkButton.setEnabled(!game.getCurrentRoom().getNpc().isEmpty());
    pickUpButton.setEnabled(!game.getCurrentRoom().getItems().isEmpty());
    ImageIcon spiritImage = game.getCurrentRoom().getSpiritImage();
    if (spiritImage != null) {
      spiritImageLabel.setIcon(spiritImage);
    } else {
      spiritImageLabel.setIcon(null);
    }
  }

  private void updateCountdownLabel() {
    int minutes = remainingTimeInSeconds / 60;
    int seconds = remainingTimeInSeconds % 60;
    countdownLabel.setText(String.format("Time remaining: %02d:%02d", minutes, seconds));
  }

  private JFrame showMap() {
    try {
      String location = game.getCurrentRoom().getName().toLowerCase(Locale.ROOT);
      System.out.println(location);

      // Create a new JFrame for the child window
      mapFrame = new JFrame("Map of " + location);
      mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

      // Create the playerMapPanel and add it to the child window
      JPanel playerMapPanel = new JPanel(new BorderLayout());
      InputStream stream = getClass().getResourceAsStream("/" + location + ".png");
      ImageIcon playerMap = new ImageIcon(ImageIO.read(stream));
      JLabel playerMapLabel = new JLabel(playerMap);
      playerMapPanel.add(playerMapLabel, BorderLayout.CENTER);
      mapFrame.add(playerMapPanel);

      // Set the size and make the child window visible
      mapFrame.setSize(480, 780);
      mapFrame.setVisible(true);

      return mapFrame;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private void openChest() {

    if (game.getChestRooms().contains(game.getCurrentRoom().getName())) {
      JFrame helpFrame = new JFrame("Open Chest");

      // Add buttons and their associated logic to the help popup window
      JPanel openChestButtonsPanel = new JPanel();

      JButton leftChest = new JButton(
          "Open " + game.getCurrentRoom().getChests().get(0).getName() + "?");
      leftChest.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          game.getCurrentRoom().getChests().get(0).setOpened(true);
          if (game.getCurrentRoom().getName().equalsIgnoreCase("weapons exhibit")) {
            Item scimitar = new Item(10, "Scimitar", "A curved sword.", "weapon", true);
            game.getCurrentRoom().getItems().add(scimitar);
            gameTextDisplayArea.append("A weapon emerges from the chest, a Scimitar!\n");
            setButtonEnabled();
          } else if (game.getCurrentRoom().getName().equalsIgnoreCase("armor exhibit")) {
            Item chainMail = new Item(10, "Chainmail", "Interlocking rings that block attacks.",
                "armor", true);
            game.getCurrentRoom().getItems().add(chainMail);
            gameTextDisplayArea.append("Armor emerges from the chest, its Chainmail!\n");
            setButtonEnabled();
          } else if (game.getCurrentRoom().getName().equalsIgnoreCase("Homeopathy Exhibit")) {
            Item largePotion = new Item(30, "Large Potion", "A potion to heal health", "heal",
                false);
            game.getCurrentRoom().getItems().add(largePotion);
            gameTextDisplayArea.append("A potion emerges from the chest, its a Large Potion!\n");
            setButtonEnabled();
          }
          JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(leftChest);
          frame.dispose();
        }
      });

      JButton middleChest = new JButton(
          "Open " + game.getCurrentRoom().getChests().get(1).getName() + "?");
      middleChest.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          game.getCurrentRoom().getChests().get(1).setOpened(true);
          if (game.getCurrentRoom().getName().equalsIgnoreCase("weapons exhibit")) {
            Item katana = new Item(15, "Katana", "An ancient sword.", "weapon", true);
            game.getCurrentRoom().getItems().add(katana);
            gameTextDisplayArea.append("A weapon emerges from the chest, a Katana!\n");
            setButtonEnabled();
          } else if (game.getCurrentRoom().getName().equalsIgnoreCase("armor exhibit")) {
            Item yori = new Item(20, "Yori",
                "Elaborate woven cloth meant to give the user ease of movement", "armor", true);
            game.getCurrentRoom().getItems().add(yori);
            gameTextDisplayArea.append("Armor emerges from the chest, its a Yori!\n");
            setButtonEnabled();
          } else if (game.getCurrentRoom().getName().equalsIgnoreCase("Homeopathy Exhibit")) {
            Item smallPotion = new Item(5, "Small Potion", "A potion to heal health", "heal",
                false);
            game.getCurrentRoom().getItems().add(smallPotion);
            gameTextDisplayArea.append("A potion emerges from the chest, its a Small Potion!\n");
            setButtonEnabled();
          }
          JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(middleChest);
          frame.dispose();
        }
      });

      JButton rightChest = new JButton(
          "Open " + game.getCurrentRoom().getChests().get(2).getName() + "?");
      rightChest.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          game.getCurrentRoom().getChests().get(2).setOpened(true);
          if (game.getCurrentRoom().getName().equalsIgnoreCase("weapons exhibit")) {
            Item longsword = new Item(20, "Longsword", "A straight blade from ancient times.",
                "weapon", true);
            game.getCurrentRoom().getItems().add(longsword);
            gameTextDisplayArea.append("A weapon emerges from the chest, a Longsword!\n");
            setButtonEnabled();
          } else if (game.getCurrentRoom().getName().equalsIgnoreCase("armor exhibit")) {
            Item plateArmor = new Item(15, "Plate Armor",
                "Heavy and thick plates meant to deflect away blows.", "armor", true);
            game.getCurrentRoom().getItems().add(plateArmor);
            gameTextDisplayArea.append("Armor emerges from the chest, its Plate Armor!\n");
            setButtonEnabled();
          } else if (game.getCurrentRoom().getName().equalsIgnoreCase("Homeopathy Exhibit")) {
            Item potion = new Item(20, "Potion", "A potion to heal health", "heal", false);
            game.getCurrentRoom().getItems().add(potion);
            gameTextDisplayArea.append("A potion emerges from the chest, its a Medium Potion!\n");
            setButtonEnabled();
          }
          JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(rightChest);
          frame.dispose();
        }
      });

      openChestButtonsPanel.add(leftChest);
      openChestButtonsPanel.add(middleChest);
      openChestButtonsPanel.add(rightChest);

      leftChest.setEnabled(!game.getCurrentRoom().getChests().get(0).getOpened());
      middleChest.setEnabled(!game.getCurrentRoom().getChests().get(1).getOpened());
      rightChest.setEnabled(!game.getCurrentRoom().getChests().get(2).getOpened());

      helpFrame.add(openChestButtonsPanel, BorderLayout.CENTER);

      helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      helpFrame.setSize(400, 300);
      helpFrame.setVisible(true);
    } else {
      gameTextDisplayArea.append("Cannot use key in this room.\n");
    }
  }


  private void fightMainBoss() {
    // Find the boss room
    for (Room room : game.getRoomJSON()) {
      if (room.getName().equalsIgnoreCase("Boss Room")) {
        bossRoom = room;
        break;
      }
    }
    // If boss room is found, set it as the current room
    if (bossRoom != null) {
      game.setCurrentRoom(bossRoom);
      roomDescription.setText(
          game.getCurrentRoom().getDescription() + "\nThe Boss is very Mad at you! \n"
              + game.getCurrentRoom().getItems().toString());
      gameTextDisplayArea.append("this is the fighting method");
      attackButton.setEnabled(true);
      northButton.setEnabled(false);
      eastButton.setEnabled(false);
      westButton.setEnabled(false);
      southButton.setEnabled(false);
      talkButton.setEnabled(false);
      pickUpButton.setEnabled(false);
      useButton.setEnabled(true);
    } else {
      System.out.println("Boss room not found.");
    }
    bossFight();
  }

  private void bossFight() {
    Boss boss = new Boss(game.getPlayer());
    boss.setActive(true);
    playerTurn(boss);
  }

  private void playerTurn(Boss boss) {
    isFighting = true;

    attackButton.removeActionListener(attackButton.getActionListeners()[0]); // Remove any existing action listeners
    attackButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (isFighting) {
          String playerAttackMessage = game.getPlayer().attack(boss);
          gameTextDisplayArea.append("\n" + playerAttackMessage + "\n"); // append the player's attack message

          game.getPlayer().attack(boss);
          if (boss.getHealth() <= 0) {
            handleVictory(boss);
            isFighting = false;
          } else {
            String bossTaunt = boss.attackPlayer(game.getPlayer());
            gameTextDisplayArea.append("\n" + bossTaunt + "\n"); // append the taunt to the JTextArea

            boss.attackPlayer(game.getPlayer());
            if (game.getPlayer().getHealth() <= 0) {
              handleDefeat();
              isFighting = false;
            }
          }
        }
      }
    });
  }

  private void handleVictory(Boss boss) {
    ImageIcon victoryIcon = new ImageIcon(getClass().getResource("/Ending_Victorious.png"));
    showEndGameDialog("Victory", "Congratulations! You have defeated the boss!", victoryIcon,
        false);
    boss.setActive(false);
    attackButton.setEnabled(true);
  }

  private void handleDefeat() {
    ImageIcon defeatIcon = new ImageIcon(getClass().getResource("/Ending_Defeated.png"));
    showEndGameDialog("Defeat", "You have been defeated by the boss. Better luck next time.",
        defeatIcon, true);
    attackButton.setEnabled(true);
  }

  private void showHelpWindow() {
    JFrame helpFrame = new JFrame("Help");
    JTextArea helpTextArea = new JTextArea();
    helpTextArea.setEditable(false);
    helpTextArea.setLineWrap(true);
    helpTextArea.setWrapStyleWord(true);
    JScrollPane helpScrollPane = new JScrollPane(helpTextArea);
    helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    helpFrame.add(helpScrollPane);

    // Read help instructions from help.json file
    try (Reader reader = new InputStreamReader(
        getClass().getClassLoader().getResourceAsStream("help.json"))) {
      Gson gson = new Gson();
      JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
      String helpMenu = jsonObject.get("help_menu").getAsString();
      helpTextArea.setText(helpMenu);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Add buttons and their associated logic to the help popup window
    JPanel helpButtonsPanel = new JPanel();

    JButton closeButton = new JButton("Close");
    closeButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        helpFrame.dispose();
      }
    });

    JButton restartButton = new JButton("Restart");
    restartButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        helpFrame.dispose();
        dispose();
        new GUI();
      }
    });

    JButton muteUnmuteButton = new JButton(musicMuted ? "Unmute" : "Mute");
    muteUnmuteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        toggleMusic();
        muteUnmuteButton.setText(musicMuted ? "Unmute" : "Mute");
      }
    });

    JButton quitButton = new JButton("Quit");
    quitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    helpButtonsPanel.add(closeButton);
    helpButtonsPanel.add(restartButton);
    helpButtonsPanel.add(quitButton);
    helpButtonsPanel.add(muteUnmuteButton);

    helpFrame.add(helpButtonsPanel, BorderLayout.SOUTH);

    helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    helpFrame.setSize(400, 300);
    helpFrame.setVisible(true);
  }

  private void toggleMusic() {
    game.toggleMute();
    musicMuted = !musicMuted;
  }

  public static JTextArea getGameTextDisplayArea() {
    return gameTextDisplayArea;
  }

  public void setGameTextDisplayArea(JTextArea gameTextDisplayArea) {
    this.gameTextDisplayArea = gameTextDisplayArea;
  }

  private void showEndGameDialog(String title, String message, ImageIcon icon, boolean isDefeat) {
    {
      JDialog endGameDialog = new JDialog();
      endGameDialog.setTitle(title);
      endGameDialog.setModal(true);
      endGameDialog.setLayout(new BorderLayout());
      endGameDialog.setSize(800, 800);

      if (isDefeat) {
        endGameDialog.setSize(600, 700);
      } else {
        endGameDialog.setSize(600, 700);
      }

      JPanel messagePanel = new JPanel(new BorderLayout());
      messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

      JLabel messageLabel = new JLabel(message, JLabel.CENTER);
      messagePanel.add(messageLabel, BorderLayout.NORTH);

      JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
      messagePanel.add(iconLabel, BorderLayout.CENTER);

      JPanel buttonPanel = new JPanel(new FlowLayout());
      JButton restartButton = new JButton("Restart");
      JButton quitButton = new JButton("Quit");

      restartButton.addActionListener(e -> {
        endGameDialog.dispose();
        restartGame();
      });

      quitButton.addActionListener(e -> {
        endGameDialog.dispose();
        System.exit(0);
      });

      buttonPanel.add(restartButton);
      buttonPanel.add(quitButton);

      endGameDialog.add(messagePanel, BorderLayout.CENTER);
      endGameDialog.add(buttonPanel, BorderLayout.SOUTH);
      endGameDialog.setLocationRelativeTo(null);
      endGameDialog.setVisible(true);
    }
  }
    private void restartGame() {
      getContentPane().removeAll();
      game = new Game();
      initGUI();
      revalidate();
      repaint();
    }

    private void setBossSpiritImage() {
      ImageIcon bossSpiritImage = new ImageIcon(getClass().getResource("/boss_room.png"));
      spiritImageLabel.setIcon(bossSpiritImage);

  }
}
