package com.escapeartist.util;

import java.util.concurrent.TimeUnit;

public class GameTimer implements Runnable {
  private Thread timerThread;
  private long startTime;
  private long duration;
  private long remainingTime;

  public GameTimer(int durationInMinutes) {
    this.startTime = System.currentTimeMillis();
    this.duration = durationInMinutes * 60 * 1000; // convert to milliseconds
  }

  public long getRemainingTime() {
    long elapsedTime = System.currentTimeMillis() - startTime;
    remainingTime = duration - elapsedTime;
    return remainingTime;
  }

  public void addTime(long timeToAdd) {
    duration += timeToAdd;
  }

  public boolean hasTimeExpired() {
    return getRemainingTime() <= 0;
  }

  public String getFormattedRemainingTime() {
    long remainingTimeMillis = getRemainingTime();
    long minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTimeMillis);
    long seconds = TimeUnit.MILLISECONDS.toSeconds(remainingTimeMillis) % 60;
    return String.format("%02d:%02d", minutes, seconds);
  }

  public void start() {
    if (timerThread == null) {
      timerThread = new Thread(this);
      timerThread.start();
    }
  }

  @Override
  public void run() {
    while (!hasTimeExpired()) {
      try {
        System.out.println("Remaining time: " + getFormattedRemainingTime());
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
