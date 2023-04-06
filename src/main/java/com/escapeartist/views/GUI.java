package com.escapeartist.views;

import com.escapeartist.controllers.Game;
import com.escapeartist.models.Item;
import com.escapeartist.models.Room;
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
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


public class GUI extends JFrame {

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
  private JTextField inputField;


  private Game game;
  private JTextArea roomDescription;

  private JPanel textPanel;
  public JTextArea textArea;

  private JPanel sidePanel;
  private DefaultListModel<String> inventoryModel;
  private JList<String> inventoryList;
  private JButton useButton;
  private JButton equipButton;

  public GUI() {
    game = new Game();

    setTitle("Escape Artist");  // Set window title

    // Create splash screen panel
    JPanel splashScreenPanel = new JPanel(new BorderLayout());
    ImageIcon splashScreenIcon = new ImageIcon(getClass().getResource("/EscapeArtistTitle.png"));
    JLabel splashScreenLabel = new JLabel(splashScreenIcon);
    splashScreenPanel.add(splashScreenLabel, BorderLayout.CENTER);
    add(splashScreenPanel, BorderLayout.CENTER);

    // Create enter key listener for clearing the splash screen
    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          remove(splashScreenPanel);
          // Add code for description panel here
          JTextArea descriptionArea = new JTextArea("Welcome to Escape Artist, the exciting text-based adventure game set in a museum where you'll have to solve riddles, puzzles, and battles to gather resources and prepare for an epic battle against the museum curator.\n"
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
    remainingTimeInSeconds = 1 * 60;
    updateCountdownLabel();
    countdownTimer = new Timer(1000, new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        remainingTimeInSeconds--;
        updateCountdownLabel();
        if (remainingTimeInSeconds <= 0) {
          countdownTimer.stop();
          JOptionPane.showMessageDialog(GUI.this, "The boss fight has begun! Prepare for battle!",
              "Boss Fight", JOptionPane.WARNING_MESSAGE);
          fightMainBoss();
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

    JPanel textPanel = new JPanel();

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

    inputField = new JTextField(10);
    Dimension currentPreferredSize = inputField.getPreferredSize();
    Dimension newPreferredSize = new Dimension(currentPreferredSize.width * 3, currentPreferredSize.height * 2); // Change the multiplier for the desired height
    inputField.setPreferredSize(newPreferredSize);
    inputField.setMaximumSize(newPreferredSize);
    inputField.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String input = inputField.getText();
        inputField.setText("");

      }
    });
    textPanel.add(inputField);

    topRightPanel.add(textPanel, BorderLayout.NORTH);


    textPanel.add(inputField);

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
        roomDescription.setText(game.getCurrentRoom().getDescription() + "\nItems in the room: \n"
            + game.getCurrentRoom().getItems().toString());
        textArea.setText(game.getCurrentRoom().getDescription());
        setButtonEnabled();
      }
    });

    southButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        game.moveSouth();
        roomDescription.setText(game.getCurrentRoom().getDescription() + "\nItems in the room: \n"
            + game.getCurrentRoom().getItems().toString());
        textArea.setText(game.getCurrentRoom().getDescription());
        setButtonEnabled();
      }
    });


    eastButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        game.moveEast();
        roomDescription.setText(game.getCurrentRoom().getDescription() + "\nItems in the room: \n"
            + game.getCurrentRoom().getItems().toString());
        textArea.setText(game.getCurrentRoom().getDescription());
        setButtonEnabled();
      }
    });

    westButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        game.moveWest();
        roomDescription.setText(game.getCurrentRoom().getDescription() + "\nItems in the room: \n"
            + game.getCurrentRoom().getItems().toString());
        textArea.setText(game.getCurrentRoom().getDescription());
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
        String npcQuestion = game.getNPCQuestion(); // Replace this with a method from your Game class that retrieves the NPC's question
        com.escapeartist.views.AnswerDialog answerDialog = new com.escapeartist.views.AnswerDialog(GUI.this, npcQuestion);
        answerDialog.setVisible(true);

        String playerAnswer = answerDialog.getAnswer();

        String interactionResult = game.talkNPC(playerAnswer);
        roomDescription.setText(game.getCurrentRoom().getDescription() + "\nItems in the room: \n" + game.getCurrentRoom().getItems().toString() + "\n\n" + interactionResult);
        setButtonEnabled();
      }
    });

    pickUpButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        inventoryModel.addElement(game.getCurrentRoom().getItems().get(0).getName());
        game.pickUpItem();
        setButtonEnabled();
      }
    });

    equipButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String selectedItem = inventoryList.getSelectedValue();
        if (selectedItem != null) {
          game.getPlayer().setEquippedItem(selectedItem);
        }
      }
    });

    useButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String selectedItem = inventoryList.getSelectedValue();
        if(selectedItem != null){
          for(Item item : game.getPlayer().getInventory()){
            if(item.getType().equalsIgnoreCase("map")){
              showMap();
            }
          }
        }
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

  private void Map() {
    String location = game.getCurrentRoom().getName();

// Create a new JFrame for the child window
    JFrame mapFrame = new JFrame("Map of " + location);
    mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

// Create the playerMapPanel and add it to the child window
    JPanel playerMapPanel = new JPanel(new BorderLayout());
    ImageIcon playerMap = new ImageIcon(getClass().getResource("/" + location + ".png"));
    JLabel playerMapLabel = new JLabel(playerMap);
    playerMapPanel.add(playerMapLabel, BorderLayout.CENTER);
    mapFrame.add(playerMapPanel);

// Set the size and make the child window visible
    mapFrame.setSize(480, 780);
    mapFrame.setVisible(true);
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
      setButtonEnabled();
      northButton.setEnabled(false);
      eastButton.setEnabled(false);
      westButton.setEnabled(false);
      southButton.setEnabled(false);
      talkButton.setEnabled(false);
      pickUpButton.setEnabled(false);
      useButton.setEnabled(false);
    } else {
      System.out.println("Boss room not found.");
    }
  }

  private void showMap() {
    String location = game.getCurrentRoom().getName();

    JFrame mapFrame = new JFrame("Map of " + location);
    mapFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JPanel playerMapPanel = new JPanel(new BorderLayout());
    ImageIcon playerMap = new ImageIcon(getClass().getResource("/" + location + ".png"));
    JLabel playerMapLabel = new JLabel(playerMap);
    playerMapPanel.add(playerMapLabel, BorderLayout.CENTER);
    mapFrame.add(playerMapPanel);

    mapFrame.setSize(480, 780);
    mapFrame.setVisible(true);
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

    JButton quitButton = new JButton("Quit");
    quitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });

    helpButtonsPanel.add(closeButton);
    helpButtonsPanel.add(restartButton);
    helpButtonsPanel.add(quitButton);

    helpFrame.add(helpButtonsPanel, BorderLayout.SOUTH);


    helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    helpFrame.setSize(400, 300);
    helpFrame.setVisible(true);
  }

}
