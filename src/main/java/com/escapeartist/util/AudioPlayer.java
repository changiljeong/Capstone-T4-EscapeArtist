package com.escapeartist.util;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class AudioPlayer {
  private Clip clip;

  public AudioPlayer(String fileName) {
    try {
      // Load the audio file from the resources folder
      InputStream audioSource = getClass().getResourceAsStream("/" + fileName);
      BufferedInputStream bufferedIn = new BufferedInputStream(audioSource);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }


  public void play() {
    clip.start();
    clip.loop(Clip.LOOP_CONTINUOUSLY);
  }

  public void stop() {
    clip.stop();
  }
}

