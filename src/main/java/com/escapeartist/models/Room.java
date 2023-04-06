package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import javax.swing.ImageIcon;

public class Room {
    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("exits")
    private Map<String, String> exits;

    @SerializedName("npcs")
    private List<NPC> npcs;

    @SerializedName("items")
    private List<Item> items;

    @SerializedName("spiritImage")
    private String spiritImage;

    public Room(String name, String description, Map<String, String> exits, List<NPC> npcs, List<Item> items) {
        this.name = name;
        this.description = description;
        this.exits = exits;
        this.npcs = npcs;
        this.items = items;
    }

    public String getName() {
        return name;
    }


    public String getDescription() {
        return description;
    }


    public Map<String, String> getExits() {
        return exits;
    }


    public List<Item> getItems() {
        return items;
    }

    public ImageIcon getSpiritImage() {
        if (spiritImage == null) {
            return null;
        }
        return new ImageIcon(getClass().getResource(spiritImage));
    }


    public void setItems(List<Item> items) {
        this.items = items;
    }

    public List<NPC> getNpc() {
        return npcs;
    }

    public void setNpc(List<NPC> npc) {
        this.npcs = npc;
    }

    public void addKey(){
        Item key = new Item(1,"Key", "A small brass key.", "key", false);
        items.add(0, key);
    }

    public void removeItem(Item item){
        items.remove(item);
    }
}


