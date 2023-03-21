package com.escapeartist.views;

import java.util.List;
import java.util.Map;

import com.escapeartist.models.Item;
import com.escapeartist.models.Location;
import com.escapeartist.models.NPC;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

public class GameView {
    private JsonObject gameData;

    public GameView(JsonObject gameData) {
        this.gameData = gameData;
    }

    public void displayLocation(JsonElement location) {
        JsonObject locationObject = location.getAsJsonObject();
        System.out.println(locationObject.get("name").getAsString());
        System.out.println(locationObject.get("description").getAsString());
        System.out.print(gameData.getAsJsonObject("dialogue").get("exits_text").getAsString() + " ");
        locationObject.get("exits").getAsJsonObject().entrySet().forEach(entry -> System.out.print(entry.getKey() + ", "));
        System.out.println();

        JsonArray itemsArray = new JsonArray();
        if (locationObject.has("items")) {
            itemsArray = new Gson().toJsonTree(locationObject.get("items").getAsJsonArray()).getAsJsonArray();
        }
        System.out.println(gameData.getAsJsonObject("dialogue").get("items_text").getAsString() + " " + itemsArray);

        JsonArray npcsArray = new JsonArray();
        if (locationObject.has("npcs")) {
            npcsArray = new Gson().toJsonTree(locationObject.get("npcs").getAsJsonArray()).getAsJsonArray();
        }
        System.out.println(gameData.getAsJsonObject("dialogue").get("npcs_text").getAsString() + " " + npcsArray);
    }


    private String getExits(Location location) {
        Map<String, Integer> exits = location.getExits();
        StringBuilder exitString = new StringBuilder();
        int exitCount = 0;

        for (Map.Entry<String, Integer> entry : exits.entrySet()) {
            exitString.append(entry.getKey());
            exitCount++;

            if (exitCount < exits.size()) {
                exitString.append(", ");
            }
        }

        return exitString.toString();
    }

    private String getItems(List<Item> items) {
        StringBuilder itemText = new StringBuilder();

        for (Item item : items) {
            itemText.append(item.getName());
            itemText.append(", ");
        }

        if (items.size() > 0) {
            itemText.setLength(itemText.length() - 2); // remove the last comma and space
        }

        return itemText.toString();
    }

    private String getNPCs(List<NPC> npcs) {
        StringBuilder npcText = new StringBuilder();

        for (NPC npc : npcs) {
            npcText.append(npc.getName());
            npcText.append(", ");
        }

        if (npcs.size() > 0) {
            npcText.setLength(npcText.length() - 2); // remove the last comma and space
        }

        return npcText.toString();
    }

}
