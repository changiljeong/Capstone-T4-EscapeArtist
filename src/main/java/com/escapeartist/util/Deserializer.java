package com.escapeartist.util;

import com.escapeartist.models.Riddle;
import com.escapeartist.models.Room;

import com.escapeartist.models.Trivia;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class Deserializer {

    public List<Room> deserializeLocations() {
        try (Reader reader = new InputStreamReader(
            Deserializer.class.getClassLoader().getResourceAsStream("room_data.json"))) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray locationsArray = jsonObject.getAsJsonArray("locations");
            Type listType = new TypeToken<List<Room>>() {}.getType();
            return gson.fromJson(locationsArray, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Riddle> deserializeRiddles() {
        try (Reader reader = new InputStreamReader(
            Deserializer.class.getClassLoader().getResourceAsStream("riddle_data.json"))) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray locationsArray = jsonObject.getAsJsonArray("riddles");
            Type listType = new TypeToken<List<Riddle>>() {}.getType();
            return gson.fromJson(locationsArray, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Trivia> deserializeTrivia() {
        try (Reader reader = new InputStreamReader(
            Deserializer.class.getClassLoader().getResourceAsStream("trivia_data.json"))) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);
            JsonArray locationsArray = jsonObject.getAsJsonArray("trivia");
            Type listType = new TypeToken<List<Trivia>>() {}.getType();
            return gson.fromJson(locationsArray, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
