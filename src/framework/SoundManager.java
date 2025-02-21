package framework;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static Map<String, Clip> soundEffects = new HashMap<>();
    private static Clip backgroundMusic;
    private static SourceDataLine sourceDataLine;
    private static String currentTrack = "";
    private static boolean isPlaying = false;

    public static void loadSound(String name, String path) {
        try {
            URL soundURL = SoundManager.class.getResource(path);

            if(soundURL == null) {
                System.err.println("Couldn't find sound " + path);
                return;
            }

            AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundURL);
            Clip clip = AudioSystem.getClip();

            clip.open(audioInput);
            soundEffects.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void playSound(String name) {
        Clip clip = soundEffects.get(name);

        if(clip != null) {
            if(clip.isRunning()) {
                clip.stop();
            }

            clip.setFramePosition(0);
            clip.start();
        } else {
            System.err.println("Couldn't find sound " + name);
        }
    }

    public static void playMusic(String path) {

        if (isPlaying(path)) {
            return;
        }

        stopMusic();

        try {
            URL soundURL = SoundManager.class.getResource(path);

            if(soundURL == null) {
                System.err.println("Music file could not be found: " + path);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundURL);
            AudioFormat audioFormat = audioInputStream.getFormat();
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat);

            backgroundMusic = (Clip) AudioSystem.getLine(info);
            backgroundMusic.open(audioInputStream);
//            backgroundMusic.getFrameLength();

//            int sampleRate = (int) audioFormat.getFrameRate();
//            int startFrame = (int)4.168 * sampleRate;
//            int endFrame = (int)52.359 * sampleRate;

//            int sampleRate =  44100 / 75;
//            int startFrame = 301 * sampleRate;
//            int endFrame = 3913 * sampleRate;
//
//            backgroundMusic.setLoopPoints(startFrame, endFrame);
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
            currentTrack = path;
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void stopMusic() {
        if(backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
            backgroundMusic.close();
        }

        currentTrack = "";
    }

    public static boolean isPlaying(String path) {
        return backgroundMusic != null && backgroundMusic.isRunning() && currentTrack.equals(path);
    }
}
