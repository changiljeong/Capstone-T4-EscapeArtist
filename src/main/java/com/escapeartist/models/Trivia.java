package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Random;

public class Trivia {
  @SerializedName("question")
  private String question;

  @SerializedName("answer")
  private String answer;

  @SerializedName("difficulty")
  private String difficulty;

  public Trivia getQuestion(List<Trivia> trivia) {
    Random rand = new Random();
    int randNum = rand.nextInt(trivia.size());
    return trivia.get(randNum);
  }


  public String getQuestion() {
    return question;
  }

  public void setQuestion(String question) {
    this.question = question;
  }

  public String getAnswer() {
    return answer;
  }

  public void setAnswer(String answer) {
    this.answer = answer;
  }
}
