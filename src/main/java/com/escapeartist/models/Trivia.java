package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Trivia {

  @SerializedName("id")
  private int id;

  @SerializedName("question")
  private String question;

  @SerializedName("answer")
  private String answer;

  @SerializedName("difficulty")
  private String difficulty;


  // Method to get the trivia question by its ID from a list of trivia objects
  public static Trivia getTriviaByID(List<Trivia> trivias, int id) {
    List<Trivia> filteredTrivias = new ArrayList<>();
    for (Trivia trivia : trivias) {
      if (trivia.getId() == id) {
        filteredTrivias.add(trivia);
      }
    }
    if (filteredTrivias.isEmpty()) {
      return null;
    }
    Collections.shuffle(filteredTrivias);
    return filteredTrivias.get(0);
  }



  private Object getDifficulty() {
    return difficulty;
  }


  // Getter method for the id field
  public int getId() {
    return id;
  }

  // Getter method for the question field
  public String getQuestion() {
    return question;
  }

  // Getter method for the answer field
  public String getAnswer() {
    return answer;
  }

}
