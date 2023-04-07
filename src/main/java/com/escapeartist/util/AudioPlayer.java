package com.escapeartist.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import javax.sound.sampled.*;
import java.io.IOException;

public class AudioPlayer {
  private Clip clip;
  private boolean muted = false;

  public AudioPlayer(String fileName) {
    try {
      // Load the audio file from the resources folder
      InputStream audioSource = getClass().getResourceAsStream("/" + fileName);
      BufferedInputStream bufferedIn = new BufferedInputStream(audioSource);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);

      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      muted = false;
    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
      e.printStackTrace();
    }
  }

  public void play() {
    if (!muted) {
      clip.start();
      clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }

  public void stop() {
    clip.stop();
  }

  public void toggleMute() {
    muted = !muted;
    if (muted) {
      clip.stop();
    } else {
      clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
  }
}

