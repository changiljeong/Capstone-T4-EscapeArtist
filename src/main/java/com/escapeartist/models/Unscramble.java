package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Unscramble {

  @SerializedName("name")
  private String name;

  @SerializedName("win")
  private String win;

  @SerializedName("lose")
  private String lose;

  @SerializedName("words")
  private List<String> words;

  @SerializedName("scrambled")
  private String scrambled;

  @SerializedName("answer")
  private String answer;

  @SerializedName("no_answer")
  private String noAnswer;

  @SerializedName("try_again")
  private String tryAgain;

  @SerializedName("no_play_again")
  private String noPlayAgain;

  @SerializedName("give_up")
  private String giveUp;

  List<Character> randList = new ArrayList<>();  // a list to store characters of a randomly chosen word
  StringBuilder randWord = new StringBuilder();  // a StringBuilder object to build the randomly chosen word
  String randWordString;  // a String object to store the randomly chosen word
  boolean anotherTry;  // a boolean flag to indicate if another try is made to guess the word

  public String randomizeWord(List<String> answer) {
    if (anotherTry) {  // if another try is made, reset the randomization process
      randList.clear();
      randWord = new StringBuilder();
      randWordString = "";
    }
    // Need to choose a word to scramble randomly and store
    Random random = new Random();
    //get size of list and randomly choose index, then return word at that index as rand
    String rand = answer.get(
        random.nextInt(answer.size()));  // randomly choose a word from the list
    // get characters and add to list
    for (char letter : rand.toCharArray()) {
      randList.add(letter);  // add each character to the randList
    }

    for (char letter : randList) {
      randWord.append(letter);  // build the word using the StringBuilder object
    }
    randWordString = randWord.toString();  // convert the StringBuilder object to a String object
    anotherTry = true;  // set the flag to indicate another try is made
    return randWordString;  // return the randomly chosen word
  }

  public String scrambleWord() {
    // Then scramble chosen word and store
    Collections.shuffle(randList);  // shuffle the randList
    StringBuilder scrambled = new StringBuilder();
    for (char letter : randList) {
      scrambled.append(
          letter);  // build the scrambled word using the StringBuilder object
    }
    return scrambled.toString();  // return the scrambled word
  }

  public boolean guesses(String guess) {
    if (guess.equals(
        randWordString)) {  // if the guessed word is the same as the randomly chosen word
      return true;  // return true
    }
    return false;  // otherwise return false
  }

  public String getName() {
    return name;
  }

  public String getWin() {
    return win;
  }

  public String getLose() {
    return lose;
  }

  public String getAnswer() {
    return answer;
  }

  public String getTryAgain() {
    return tryAgain;
  }

  public String getNoPlayAgain() {
    return noPlayAgain;
  }

  public String getNoAnswer() {
    return noAnswer;
  }

  public String getGiveUp() {
    return giveUp;
  }
}

