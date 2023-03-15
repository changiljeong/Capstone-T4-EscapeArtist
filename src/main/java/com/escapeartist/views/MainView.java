package com.escapeartist.views;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class MainView {
  private JsonObject menuData;

  public MainView() {
    loadMenuData();
  }

  private void loadMenuData() {
    Gson gson = new Gson();
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream("menu_dialogue.json");
    menuData = gson.fromJson(new InputStreamReader(inputStream), JsonObject.class);
  }

  public void showWelcomeMessage() {
    System.out.println(menuData.get("welcome_message").getAsString());
    JsonArray options = menuData.getAsJsonArray("options");
    for (int i = 0; i < options.size(); i++) {
      System.out.println(options.get(i).getAsString());
    }
  }

  public String getUserInput() {
    System.out.print("> "); // Display the prompt
    Scanner scanner = new Scanner(System.in);
    String userInput = scanner.nextLine().trim().toLowerCase();

    // Split the input into words
    String[] words = userInput.split("\\s+");

    // Get the words to remove from the menu_data JSON object
    JsonArray wordsToRemoveJson = menuData.getAsJsonArray("words_to_remove");
    List<String> wordsToRemove = new ArrayList<>();
    for (int i = 0; i < wordsToRemoveJson.size(); i++) {
      wordsToRemove.add(wordsToRemoveJson.get(i).getAsString());
    }

    // Remove the unwanted words
    StringBuilder cleanedInput = new StringBuilder();
    for (String word : words) {
      if (!wordsToRemove.contains(word)) {
        cleanedInput.append(word).append(" ");
      }
    }

    // Return the cleaned-up string
    return cleanedInput.toString().trim();
  }

  public boolean isValidInput(String userInput) {
    JsonArray validInputs = menuData.getAsJsonArray("valid_inputs");
    for (int i = 0; i < validInputs.size(); i++) {
      if (userInput.equals(validInputs.get(i).getAsString())) {
        return true;
      }
    }
    return false;
  }

  public void clear() {
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


  public void printMessage(String key) {
    System.out.println(menuData.get(key).getAsString());
  }
}
