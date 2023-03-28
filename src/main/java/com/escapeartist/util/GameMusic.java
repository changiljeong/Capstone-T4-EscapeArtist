package com.escapeartist.util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class GameMusic {

  private Clip clip;
  private FloatControl volumeControl;
  private boolean isMuted;

  public GameMusic() {
    isMuted = false;
  }

  public void playMusic() {
    try {
      InputStream audioFile = GameMusic.class.getResourceAsStream("/game_music.wav");
      InputStream bufferedStream = new BufferedInputStream(audioFile);
      AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(bufferedStream);
      clip = AudioSystem.getClip();
      clip.open(audioInputStream);
      volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
      clip.start();
    } catch (Exception ex) {
      System.out.println("Error playing sound.");
      ex.printStackTrace();
    }
  }

  public void stopMusic() {
    if (clip != null && clip.isRunning()) {
      clip.stop();
    }
  }

  public void toggleMute() {
    if (isMuted) {
      volumeControl.setValue(volumeControl.getMinimum());
    } else {
      volumeControl.setValue(0);
    }
    isMuted = !isMuted;
  }

  public void setVolume(float value) {
    if (volumeControl != null) {
      volumeControl.setValue(value);
    }
  }

  public float getVolume() {
    return volumeControl.getValue();
  }
}

