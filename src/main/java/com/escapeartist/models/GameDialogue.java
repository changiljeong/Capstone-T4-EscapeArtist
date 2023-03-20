package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

public class GameDialogue {

  @SerializedName("quit_confirm")
  private String quitConfirm;

  // TODO: 3/20/2023 add additional scaler fields

  @SerializedName("words_to_remove")
  private String[] wordsToRemove;

  @SerializedName("valid_inputs")
  private Map<String, String[]> validInputs;

  public String getQuitConfirm() {
    return quitConfirm;
  }

  public String[] getWordsToRemove() {
    return wordsToRemove;
  }

  public Map<String, String[]> getValidInputs() {
    return validInputs;
  }
}
