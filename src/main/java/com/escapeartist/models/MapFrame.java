package com.escapeartist.models;

import com.escapeartist.util.Clear;

import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javax.swing.*;

public class MapFrame extends JFrame {

  private MapPanel mapPanel;

  public MapFrame() {
    setTitle("Adventure Map");
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

    pack();
    setVisible(true);
  }

  public void readMap() {
    mapPanel.readAsciiFile();
    Clear.clearConsole();
  }

  class MapPanel extends JPanel {

    private static final int CELL_SIZE = 15;
    private char[][] map;

    public MapPanel() {
      setPreferredSize(new Dimension(800, 600));
    }

    public void readAsciiFile() {
      String fileName = "game_map.txt";
      int rows = 0;
      int cols = 0;

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

      map = new char[rows][cols];

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

      repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);

      if (map == null) {
        return;
      }

      g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, CELL_SIZE));

      for (int row = 0; row < map.length; row++) {
        for (int col = 0; col < map[row].length; col++) {
          char c = map[row][col];
          Color color = Color.RED;
          if (c == '.' || c == ':') {
            color = Color.BLACK;
          } else if (c == '#') {
            color = Color.BLACK;
          } else if (c == '^') {
            color = Color.BLACK;
          } else if (Character.isAlphabetic(c)) {
            color = Color.BLACK;
          }
          g.setColor(color);
          g.drawString(Character.toString(c), col * CELL_SIZE,
              (row + 1) * CELL_SIZE);
        }
      }
    }
  }
}
