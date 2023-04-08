package com.escapeartist.models;


import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.*;

public class MapFrame extends JFrame {

  private MapPanel mapPanel;

  public MapFrame() {
    // Set the title of the window
    setTitle("Adventure Map");

    // Set the default close operation to do nothing on close
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

    // Add a window listener to handle the window closing event
    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        // Hide the map window instead of closing it
        setVisible(false);
      }
    });

    // Create a new map panel and add it to the frame
    mapPanel = new MapPanel();
    add(mapPanel);

    // Pack the frame to fit the contents and make it visible
    pack();
    setVisible(true);
  }

  public void readMap() {
    // Call the map panel's readAsciiFile method to read the map from file
    mapPanel.readAsciiFile();

    // Clear the console after reading the map
  }

  class MapPanel extends JPanel {

    private static final int CELL_SIZE = 15;
    private char[][] map;

    public MapPanel() {
      // Set the preferred size of the panel
      setPreferredSize(new Dimension(800, 600));
    }

    public void readAsciiFile() {
      // Name of the file to read the map from
      String fileName = "game_map.txt";

      // Variables to hold the number of rows and columns in the map
      int rows = 0;
      int cols = 0;

      // Read the file to determine the number of rows and columns in the map
      try (BufferedReader br = new BufferedReader(new InputStreamReader(
          getClass().getClassLoader().getResourceAsStream(fileName)))) {
        String line;
        while ((line = br.readLine()) != null) {
          cols = Math.max(cols, line.length());
          rows++;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Create a new char array to hold the map
      map = new char[rows][cols];

      // Read the file again to fill in the map array
      try (BufferedReader br = new BufferedReader(new InputStreamReader(
          getClass().getClassLoader().getResourceAsStream(fileName)))) {
        String line;
        int row = 0;
        while ((line = br.readLine()) != null) {
          for (int col = 0; col < line.length(); col++) {
            map[row][col] = line.charAt(col);
          }
          row++;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      // Repaint the panel to display the new map
      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      // If the map hasn't been read yet, return early
      if (map == null) {
        return;
      }

      // Set the font for drawing the map
      g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, CELL_SIZE));

      // Loop over the map array and draw each cell
      for (int row = 0; row < map.length; row++) {
        for (int col = 0; col < map[row].length; col++) {
          // Get the character at the current cell
          char c = map[row][col];
          // Set the default color to red
          Color color = Color.RED;

          // Determine the color to use based on the character
          if (c == '.' || c == ':') {
            color = Color.BLACK;
          } else if (c == '#') {
            color = Color.BLACK;
          } else if (c == '^') {
            color = Color.BLACK;
          } else if (Character.isAlphabetic(c)) {
            color = Color.BLACK;
          }

          // Set the color and draw the character at the current cell
          g.setColor(color);
          g.drawString(Character.toString(c), col * CELL_SIZE,
              (row + 1) * CELL_SIZE);
        }
      }
    }
  }
}
