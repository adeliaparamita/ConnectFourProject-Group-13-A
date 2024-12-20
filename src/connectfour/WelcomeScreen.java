/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #13
 * 1- 5026231020- Diva Nesia Putri
 * 2- 5026231114- Imanuel Dwi Prasetyo
 * 3- 5026231196- Ni Kadek Adelia Paramita Putri
 */

package connectfour;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class WelcomeScreen extends JPanel {
    private static final long serialVersionUID = 1L;
    private Image backgroundImage;
    private JButton startButton;
    private JButton exitButton;
    private JFrame parentFrame;

    public WelcomeScreen(JFrame frame) {
        this.parentFrame = frame;

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("src/images/bg2.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set layout
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel(" Welcome To Connect Four Game");
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 48));
        titleLabel.setForeground(Color.DARK_GRAY);
        titlePanel.add(titleLabel);

        // Create buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        // Create start button
        startButton = new JButton("Start Game");
        startButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        startButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        startButton.setPreferredSize(new Dimension(200, 50));
        startButton.setMaximumSize(new Dimension(200, 50));

        // Create exit button
        exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 24));
        exitButton.setPreferredSize(new Dimension(200, 50));
        exitButton.setMaximumSize(new Dimension(200, 50));

        // Add action listeners
        startButton.addActionListener(e -> startGame());
        exitButton.addActionListener(e -> System.exit(0));

        // Add components with spacing
        add(Box.createVerticalGlue());
        add(titlePanel);
        add(Box.createRigidArea(new Dimension(0, 50)));
        buttonPanel.add(startButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(exitButton);
        add(buttonPanel);
        add(Box.createVerticalGlue());

        // Set preferred size
        setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT));
    }

    private void startGame() {
        parentFrame.getContentPane().removeAll();
        parentFrame.setContentPane(new ConnectFour());
        parentFrame.pack();
        parentFrame.setLocationRelativeTo(null);
        parentFrame.revalidate();
        parentFrame.repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            setBackground(Color.BLACK);
        }
    }
}