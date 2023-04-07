package com.escapeartist.models;

import com.google.gson.annotations.SerializedName;

public class Chest {

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("opened")
    private Boolean opened;

    public Chest(String name, String description, Boolean opened) {
        this.name = name;
        this.description = description;
        this.opened = opened;
    }

    @Override
    public String toString(){
        return name;
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

    public Boolean getOpened() {
        return opened;
    }

    public void setOpened(Boolean opened) {
        this.opened = opened;
    }
}
