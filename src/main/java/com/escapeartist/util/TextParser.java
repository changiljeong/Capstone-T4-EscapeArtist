package com.escapeartist.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Scanner;

public class TextParser {

  private JsonObject gameData;

  public TextParser(JsonObject gameData) {
    this.gameData = gameData;
  }

  public String cleanUserInput(String userInput) {
    userInput = userInput.trim().toLowerCase();
    JsonArray wordsToRemove = getDialogueArray("words_to_remove");

    if (wordsToRemove != null) {
      for (JsonElement wordToRemove : wordsToRemove) {
        String word = wordToRemove.getAsString();
        userInput = userInput.replaceAll("\\b" + word + "\\b", "").trim();
      }
    }

    return userInput;
  }

  public boolean isValidInput(JsonElement inputElement) {
    JsonObject validInputs = getValidInputs();
    if (validInputs == null) {
      return false;
    }

    for (String key : validInputs.keySet()) {
      if (validInputs.getAsJsonArray(key).contains(inputElement)) {
        return true;
      }
    }
    return false;
  }

  private JsonObject getValidInputs() {
    return getDialogueObject("valid_inputs");
  }

  private JsonObject getDialogueObject(String key) {
    JsonObject dialogue = gameData.getAsJsonObject("dialogue");
    return dialogue == null ? null : dialogue.getAsJsonObject(key);
  }

  private JsonArray getDialogueArray(String key) {
    JsonObject dialogue = gameData.getAsJsonObject("dialogue");
    return dialogue == null ? null : dialogue.getAsJsonArray(key);
  }

  public boolean isCommandOfType(JsonElement inputElement, String commandType) {
    if (inputElement == null) {
      return false;
    }

    JsonObject validInputs = getValidInputs();
    if (validInputs == null) {
      return false;
    }

    JsonArray commands = validInputs.getAsJsonArray(commandType);
    if (commands == null) {
      return false;
    }

    for (JsonElement word : commands) {
      if (inputElement.getAsString().startsWith(word.getAsString())) {
        return true;
      }
    }
    return false;
  }

  public boolean isQuitCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "quit");
  }


  public boolean isHelpCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "help");
  }

  public boolean isGoCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "go");
  }

  public boolean isLookCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "look");
  }

  public boolean isTalkCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "talk");
  }

  public boolean isGetCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "get");
  }

  public boolean isUseCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "use");
  }

  public boolean isEquipCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "equip");
  }

  public boolean isDropCommand(JsonElement inputElement) {
    return isCommandOfType(inputElement, "drop");
  }


  public boolean getConfirmation(String prompt) {
    Scanner scanner = new Scanner(System.in);
    JsonObject validInputs = getValidInputs();

    while (true) {
      System.out.println(prompt);
      System.out.print(getDialogueObject("dialogue").get("command_prompt").getAsString());
      String userInput = scanner.nextLine().trim().toLowerCase();
      JsonElement inputElement = new Gson().toJsonTree(userInput);

      if (validInputs.getAsJsonArray("yes").contains(inputElement)) {
        return true;
      } else if (validInputs.getAsJsonArray("no").contains(inputElement)) {
        return false;
      } else {
        System.out.println(gameData.getAsJsonObject("dialogue").get("invalid_input").getAsString());
      }
    }
  }

  public String getSecondWord(String userInput) {
    String[] words = userInput.split(" ");
    if (words.length > 1) {
      return words[1];
    }
    return "";
  }
}

