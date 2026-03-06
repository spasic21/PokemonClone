package framework;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SoundManager {

    private static final Map<String, Clip> soundEffects = new HashMap<>();
    private static final Map<String, Clip> musicCache = new HashMap<>();

    // ---- Sound Effects ----

    public static void loadSound(String name, String path) {
        try {
            URL soundURL = SoundManager.class.getResource(path);

            if (soundURL == null) {
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

        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.setFramePosition(0);
            clip.start();
        } else {
            System.err.println("Couldn't find sound " + name);
        }
    }

    // ---- Music Clip Management ----

    /**
     * Returns a cached Clip for the given resource path, loading from disk on first access.
     * The clip is NOT started — MusicManager controls playback.
     */
    public static Clip getMusicClip(String path) {
        Clip clip = musicCache.get(path);
        if (clip != null) return clip;

        try {
            URL soundURL = SoundManager.class.getResource(path);
            if (soundURL == null) {
                System.err.println("Music file not found: " + path);
                return null;
            }
            AudioInputStream ais = AudioSystem.getAudioInputStream(soundURL);
            clip = AudioSystem.getClip();
            clip.open(ais);
            musicCache.put(path, clip);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Sets the volume on a clip using MASTER_GAIN.
     * @param volume linear 0.0 (silent) to 1.0 (full)
     */
    public static void setClipVolume(Clip clip, float volume) {
        if (clip == null) return;
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) return;

        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = 20f * (float) Math.log10(Math.max(volume, 0.0001f));
        dB = Math.max(dB, gain.getMinimum());
        dB = Math.min(dB, gain.getMaximum());
        gain.setValue(dB);
    }

    /** Starts a clip from the beginning, looping continuously. */
    public static void startClipLooping(Clip clip) {
        if (clip == null) return;
        clip.setFramePosition(0);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
    }

    /** Starts a clip from the beginning, plays once (no loop). */
    public static void startClipOnce(Clip clip) {
        if (clip == null) return;
        clip.setFramePosition(0);
        clip.loop(0);
        clip.start();
    }

    /** Stops a clip without closing it so it can be restarted later. */
    public static void stopClip(Clip clip) {
        if (clip == null) return;
        if (clip.isRunning()) {
            clip.stop();
        }
    }
}
