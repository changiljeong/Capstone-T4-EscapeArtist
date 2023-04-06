package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Random;

public class Riddle {
    @SerializedName("question")
    private String riddle;

    @SerializedName("answer")
    private String answer;

    public Riddle getRiddle(List<Riddle> riddles) {
        Random rand = new Random();
        int randNum = rand.nextInt(riddles.size());
        return riddles.get(randNum);
    }

    public String getRiddle() {
        return riddle;
    }

    public void setRiddle(String riddle) {
        this.riddle = riddle;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
