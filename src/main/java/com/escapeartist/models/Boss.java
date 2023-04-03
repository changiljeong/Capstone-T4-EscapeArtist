package com.escapeartist.models;

public class Boss {
  private boolean isActive;

  public Boss() {
    this.isActive = false;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean active) {
    isActive = active;
  }
}