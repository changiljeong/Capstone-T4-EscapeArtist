package com.escapeartist.views;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

public class GameView {
    private JsonObject gameData;

    public GameView(JsonObject gameData) {
        this.gameData = gameData;
    }

    public void displayLocation(JsonObject location) {
        System.out.println(location.get("description").getAsString());
        System.out.println(getExitText() + " " + getExits(location));
        System.out.println(getItemText() + location.get("items").getAsJsonArray());
        System.out.println(getNPCText() + location.get("npcs").getAsJsonArray());
    }

    private String getExitText() {
        return gameData.getAsJsonObject("dialogue").get("exits_text").getAsString();
    }

    private String getItemText() {
        return gameData.getAsJsonObject("dialogue").get("items_text").getAsString();
    }

    private String getNPCText() {
        return gameData.getAsJsonObject("dialogue").get("npcs_text").getAsString();
    }

    public String getExits(JsonObject location) {
        JsonObject exits = location.getAsJsonObject("exits");
        StringBuilder exitString = new StringBuilder();
        int exitCount = 0;

        for (Map.Entry<String, JsonElement> entry : exits.entrySet()) {
            exitString.append(entry.getKey());
            exitCount++;

            if (exitCount < exits.size()) {
                exitString.append(", ");
            }
        }

        return exitString.toString();
    }



}