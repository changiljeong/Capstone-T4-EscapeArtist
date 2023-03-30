package com.escapeartist.views;

import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class GUI extends JFrame {

  private JPanel buttonPanel;
  private JButton button1;
  private JButton button2;
  private JButton button3;
  private JButton button4;
  private JButton button5;
  private JButton button6;

  private JPanel listPanel;
  private DefaultListModel<String> listModel;
  private JList<String> list;
  private JTextField textField;
  private JButton addToListButton;

  private JPanel textPanel;
  public JTextArea textArea;

  public GUI() {
    buttonPanel = new JPanel();
    JLabel outputLabel = new JLabel();
    outputLabel.setPreferredSize(new Dimension(200, 30));

    JPanel outputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    outputPanel.setPreferredSize(new Dimension(250, 250));
    outputPanel.setBackground(Color.GRAY);
    outputPanel.add(outputLabel);
    buttonPanel.add(outputPanel, BorderLayout.NORTH);

    button1 = new JButton("Go North");
    button2 = new JButton("Go East");
    button3 = new JButton("Go West");
    button4 = new JButton("Go South");
    button5 = new JButton("Additional Button");

    button1.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        outputLabel.setText("North was clicked!");
      }
    });

    button2.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        outputLabel.setText("East was clicked!");
      }
    });

    button3.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        outputLabel.setText("West was clicked!");
      }
    });

    button4.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        outputLabel.setText("South was clicked!");
      }
    });

    button5.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String item = outputLabel.getText();
        listModel.addElement(item);
        outputLabel.setText("Additional was clicked!");
      }
    });

    buttonPanel.add(button1);
    buttonPanel.add(button2);
    buttonPanel.add(button3);
    buttonPanel.add(button4);
    buttonPanel.add(button5);

    listModel = new DefaultListModel<String>();
    list = new JList<String>(listModel);

    textArea = new JTextArea(10, 20);
    textPanel = new JPanel(new BorderLayout());
    textPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);

    listPanel = new JPanel(new BorderLayout());
    listPanel.add(new JScrollPane(list), BorderLayout.CENTER);

    JPanel mainPanel = new JPanel(new GridLayout(1, 3));
    mainPanel.add(buttonPanel);
    mainPanel.add(listPanel);
    mainPanel.add(textPanel);

    add(mainPanel);
    setTitle("My GUI");
    setSize(800, 400);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setVisible(true);
  }

  public void showGameIntro(String introText) {
    InputStream imageUrl = getClass().getClassLoader().getResourceAsStream("sprite_image1.png");
    Image image;
    try {
      image = ImageIO.read(imageUrl);
    } catch (IOException e) {
      throw new RuntimeException("Error loading image", e);
    }

    ImageIcon imageIcon = new ImageIcon(image);
    JLabel imageLabel = new JLabel(imageIcon);

    JTextArea introTextArea = new JTextArea(introText);
    introTextArea.setLineWrap(true);
    introTextArea.setWrapStyleWord(true);
    introTextArea.setEditable(false);
    introTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
    introTextArea.setColumns(30);
    introTextArea.setRows(10);

    JScrollPane scrollPane = new JScrollPane(introTextArea);

    JPanel introPanel = new JPanel(new BorderLayout());
    introPanel.add(imageLabel, BorderLayout.NORTH);
    introPanel.add(scrollPane, BorderLayout.CENTER);

    JOptionPane.showMessageDialog(this, introPanel, "Game Introduction", JOptionPane.INFORMATION_MESSAGE);
  }
}