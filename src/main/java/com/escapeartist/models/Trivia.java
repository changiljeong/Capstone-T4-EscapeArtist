package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Trivia {

  @SerializedName("id")
  private int id;

  @SerializedName("question")
  private String question;

  @SerializedName("answer")
  private String answer;


  // Method to get the trivia question by its ID from a list of trivia objects
  public static Trivia getTriviaByID(List<Trivia> trivias, int id) {
    for (Trivia trivia : trivias) {
      if (trivia.getId() == id) {
        return trivia;
      }
    }
    return null;
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
