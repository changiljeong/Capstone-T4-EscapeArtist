package com.escapeartist.models;

import com.escapeartist.controllers.MainController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FlashMenuScreen {
  public FlashMenuScreen(MainController game) throws IOException{
    startGame(game);
  }

  public void startGame(MainController game) throws IOException {
    readAsciiFile();
    String message = "Press any key to continue...";
    int length = message.length() + 4;
    String border = "+".repeat(length);

    System.out.println(border);
    System.out.println("| " + message + " |");
    System.out.println(border);
    System.in.read(); // Wait for user to press any key
    clearConsole();
    game.startMenu();
  }

  public void readAsciiFile() {
    String fileName = "title_screen.txt";

    //noinspection ConstantConditions
    try (BufferedReader br = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)))) {
      String line;
      while ((line = br.readLine()) != null) {
        System.out.println(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void clearConsole() {
    try {
      String os = System.getProperty("os.name");

      if (os.contains("Windows")) {
        new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
      } else {
        System.out.print("\033[H\033[2J");
        System.out.flush();
      }
    } catch (IOException | InterruptedException e) {
      e.printStackTrace();
    }
  }
}
