package com.escapeartist;

import com.escapeartist.controllers.MainController;
import com.escapeartist.models.FlashMenuScreen;
import com.escapeartist.util.GameMusic;
import com.escapeartist.util.GameTimer;
import com.escapeartist.views.GUI;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
      // Create instances of the main game components
      MainController game = new MainController();
      GameMusic music = new GameMusic();


    // Display the game's title screen and start the game
    FlashMenuScreen flashmenu = new FlashMenuScreen(game, music );

  }

