package com.escapeartist.models;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Scanner;

public class TextParser {

  private final JsonObject gameData;

  public TextParser(JsonObject gameData) {
    this.gameData = gameData;
  }


  public boolean isValidInput(JsonObject validInputs, JsonElement inputElement) {
    for (String key : validInputs.keySet()) {
      if (validInputs.getAsJsonArray(key).contains(inputElement)) {
        return true;
      }
    }
    return false;
  }

  public JsonElement cleanInput(String userInput) {
    userInput = userInput.trim().toLowerCase();
    JsonArray wordsToRemove = gameData.getAsJsonArray("words_to_remove");
    for (JsonElement wordToRemove : wordsToRemove) {
      userInput = userInput.replaceAll("\\b" + wordToRemove.getAsString() + "\\b", "").trim();
    }
    return new Gson().toJsonTree(userInput);
  }

  public boolean handleQuit(JsonObject validInput, JsonElement inputElement,
      String quitConfirmMessage, String commandPrompt, String invalidInputMessage, Scanner scanner) {
    if (validInput.getAsJsonArray("quit").contains(inputElement)) {
      System.out.println(quitConfirmMessage);
      System.out.print(commandPrompt);
      String userInput = scanner.nextLine().trim().toLowerCase();
      JsonElement confirmInput = cleanInput(userInput);
      if (validInput.getAsJsonArray("yes").contains(confirmInput)) {
        return true;
      } else if (validInput.getAsJsonArray("no").contains(confirmInput)) {
        return false;
      } else {
        System.out.println(gameData.get("invalidInputMessage").getAsString());
        return false;
      }
    }
    return false;
  }
}



