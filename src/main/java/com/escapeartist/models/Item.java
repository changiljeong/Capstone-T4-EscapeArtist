package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;

public class Item {
    @SerializedName("value")
    private int value;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private String type;

    @SerializedName("equable")
    private Boolean equable;

    public Item(int value, String name, String description, String type, Boolean equable) {
        this.value = value;
        this.name = name;
        this.description = description;
        this.type = type;
        this.equable = equable;
    }

    @Override
    public String toString(){
        return name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getEquable() {
        return equable;
    }

    public void setEquable(Boolean equable) {
        this.equable = equable;
    }
}
