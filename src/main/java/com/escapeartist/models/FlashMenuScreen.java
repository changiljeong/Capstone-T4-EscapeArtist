package com.escapeartist.models;

import com.escapeartist.controllers.MainController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FlashMenuScreen {
  public FlashMenuScreen(MainController game) throws IOException {
    clearConsole();
    startGame(game);
  }

  public static void startGame(MainController game) throws IOException {
    readAsciiFile();
    String message = "Press any key to continue...";
    int length = message.length() + 4;
    String border = "+".repeat(length);

    System.out.println(border);
    System.out.println("| " + message + " |");
    System.out.println(border);
    System.in.read(); // Wait for user to press any key
    System.in.read(); // Skip user input into menu

    game.startMenu();
  }

  public static void readAsciiFile() {
    String fileName = "src/main/resources/title_screen.txt";

    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void clearConsole() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }
}
