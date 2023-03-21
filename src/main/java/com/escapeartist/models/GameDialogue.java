package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;

public class GameDialogue {

  @SerializedName("quit_confirm")
  private String quitConfirm;

  @SerializedName("goodbye_message")
  private String goodbyeMessage;

  @SerializedName("command_prompt")
  private String commandPrompt;

  @SerializedName("invalid_input")
  private String invalidInput;

  @SerializedName("invalid_exit")
  private String invalidExit;

  @SerializedName("exits_text")
  private String exitsText;

  @SerializedName("items_text")
  private String itemsText;

  @SerializedName("npcs_text")
  private String npcsText;

  @SerializedName("valid_inputs")
  private Map<String, List<String>> validInputs;

  @SerializedName("words_to_remove")
  private List<String> wordsToRemove;

  @SerializedName("help_menu")
  private String helpMenu;

  public String getQuitConfirm() {
    return quitConfirm;
  }

  public String getGoodbyeMessage() {
    return goodbyeMessage;
  }

  public String getCommandPrompt() {
    return commandPrompt;
  }

  public String getInvalidInput() {
    return invalidInput;
  }

  public String getInvalidExit() {
    return invalidExit;
  }

  public String getExitsText() {
    return exitsText;
  }

  public String getItemsText() {
    return itemsText;
  }

  public String getNpcsText() {
    return npcsText;
  }

  public Map<String, List<String>> getValidInputs() {
    return validInputs;
  }

  public List<String> getWordsToRemove() {
    return wordsToRemove;
  }

  public String getHelpMenu() {
    return helpMenu;
  }
}
