package com.escapeartist.util;

import com.escapeartist.models.Game;
import java.io.File;
import java.io.InputStream;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.SourceDataLine;

public class GameMusic {

  private Clip clip;

  public void playMusic() {
//    try {
//      InputStream audioFile = GameMusic.class.getResourceAsStream("/game_music.mp3");
//      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);
//      SourceDataLine line = AudioSystem.getSourceDataLine(audioInputStream.getFormat());
//      line.open();
//      clip = AudioSystem.getClip();
//      while (!clip.isRunning()) {
//        clip.open(audioInputStream);
//      }
//      clip.start();
//    } catch (Exception ex) {
//      System.out.println("Error playing sound.");
//      ex.printStackTrace();
//    }
  }
}

