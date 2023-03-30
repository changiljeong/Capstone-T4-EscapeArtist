package com.escapeartist.views;

import java.io.IOException;
import java.io.InputStream;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.imageio.ImageIO;

public class SpriteImageFrame extends JFrame {

  public SpriteImageFrame() {
    setTitle("Escape Artist");
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setResizable(false);
    setLocationRelativeTo(null);

    InputStream imageUrl = getClass().getClassLoader().getResourceAsStream("sprite_image1.png");
    Image image;
    try {
      image = ImageIO.read(imageUrl);
    } catch (IOException e) {
      throw new RuntimeException("Error loading image", e);
    }

    ImageIcon imageIcon = new ImageIcon(image);
    JLabel imageLabel = new JLabel(imageIcon);
    getContentPane().add(imageLabel, BorderLayout.CENTER);
    pack();
  }

  public void closeAfter(int seconds) {
    Timer timer = new Timer(seconds * 1000, e -> dispose());
    timer.setRepeats(false);
    timer.start();
  }
}


