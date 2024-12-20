package connectfour;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class BGM {

    private Clip clip;  // Audio clip untuk background music
    private boolean isMusicEnabled = true; // Flag untuk status musik

    // Constructor untuk memuat dan memutar musik
    public BGM(String filePath) {
        try {
            File musicFile = new File(filePath);
            if (!musicFile.exists()) {
                throw new IOException("File not found: " + filePath);
            }

            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (Exception e) {
            System.err.println("Error loading music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method untuk memulai musik
    public void play() {
        if (clip != null && !clip.isRunning()) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);  // Memutar musik terus menerus
            clip.start();
        }
    }


    // Method untuk menghentikan musik
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();  // Menghentikan musik
        }
    }

    // Method untuk men-toggle status musik
    public void toggleMusic() {
        if (isMusicEnabled) {
            stop();
        } else {
            play();
        }
        isMusicEnabled = !isMusicEnabled; // Toggle status
    }

    // Method untuk memeriksa apakah musik sedang diputar
    public boolean isMusicEnabled() {
        return isMusicEnabled;
    }
}
