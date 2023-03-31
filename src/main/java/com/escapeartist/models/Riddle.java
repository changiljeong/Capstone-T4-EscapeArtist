package com.escapeartist.models;

import com.escapeartist.util.GsonDeserializer;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Riddle {
    // Fields
    @SerializedName("id")
    private int id;

    @SerializedName("question")
    private String question;

    @SerializedName("answer")
    private String answer;

    // Static Method: Get Riddle by ID
    // Static Method: Get Riddle by ID or random
    public static Riddle getRiddle(List<Riddle> riddles, int id) {
        if (id > 0 && id <= riddles.size()) {
            return riddles.get(id - 1);
        } else {
            return riddles.get((int)(Math.random() * riddles.size()));
        }
    }


    // Getter Methods
    public int getId() {
        return id;
    }

    public String getQuestion() {
        return question;
    }

    public String getAnswer() {
        return answer;
    }

    // Setter Methods
    public void setId(int id) {
        this.id = id;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
