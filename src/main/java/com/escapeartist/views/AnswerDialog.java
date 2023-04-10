package com.escapeartist.views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AnswerDialog extends JDialog {

  private JTextField answerField;
  private JButton submitButton;
  private String answer;

  public AnswerDialog(JFrame parent, String npcQuestion) {
    super(parent, "Answer the question", true);

    JPanel panel = new JPanel(new GridBagLayout());
    GridBagConstraints cs = new GridBagConstraints();

    cs.fill = GridBagConstraints.HORIZONTAL;

    JLabel questionLabel = new JLabel(npcQuestion);
    cs.gridx = 0;
    cs.gridy = 0;
    cs.gridwidth = 2;
    panel.add(questionLabel, cs);

    answerField = new JTextField(20);
    answerField.addKeyListener(new KeyListener() {
      @Override
      public void keyTyped(KeyEvent e) {
      }

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
          submitAnswer();
        }
      }

      @Override
      public void keyReleased(KeyEvent e) {
      }
    });
    cs.gridx = 0;
    cs.gridy = 1;
    cs.gridwidth = 2;
    panel.add(answerField, cs);

    submitButton = new JButton("Submit");
    submitButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        submitAnswer();
      }
    });
    cs.gridx = 1;
    cs.gridy = 2;
    cs.gridwidth = 1;
    panel.add(submitButton, cs);

    getContentPane().add(panel, BorderLayout.CENTER);

    pack();
    setResizable(false);
    setLocationRelativeTo(parent);
  }

  public String getAnswer() {
    return answer;
  }

  private void submitAnswer() {
    answer = answerField.getText();
    dispose();
  }
}

