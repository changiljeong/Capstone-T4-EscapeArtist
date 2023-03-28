package com.escapeartist.util;

import java.util.Scanner;

public class MusicController {

  private GameMusic gameMusic;
  private Scanner scanner;

  public MusicController(GameMusic gameMusic) {
    this.gameMusic = gameMusic;
    this.scanner = new Scanner(System.in);
  }

  public void showMenu() {
    boolean exitMenu = false;
    while (!exitMenu) {
      System.out.println("\n=== Music Controls ===");
      System.out.println("1. Toggle Mute");
      System.out.println("2. Adjust Volume");
      System.out.println("3. Return to Game");
      System.out.print("Enter your choice: ");
      int choice = scanner.nextInt();

      switch (choice) {
        case 1:
          gameMusic.toggleMute();
          System.out.println("Mute toggled.");
          break;
        case 2:
          System.out.print("Enter new volume level (0 to 100): ");
          float volume = scanner.nextFloat();
          gameMusic.setVolume(volume);
          System.out.println("Volume adjusted.");
          break;
        case 3:
          exitMenu = true;
          break;
        default:
          System.out.println("Invalid choice. Please try again.");
          break;
      }
    }
  }
}
