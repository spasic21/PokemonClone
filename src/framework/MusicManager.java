package framework;

import framework.enums.Location;

import javax.sound.sampled.Clip;

public class MusicManager {

    public enum MusicState {
        IDLE,
        PLAYING_LOCATION,
        CROSSFADING,
        PLAYING_BATTLE,
        PLAYING_FANFARE,
        FANFARE_TO_LOCATION
    }

    private static final int CROSSFADE_FRAMES = 80; // ~1.5 seconds at 60fps

    private static final String WILD_BATTLE_MUSIC    = "/sounds/johto_wild_pokemon_battle.wav";
    private static final String TRAINER_BATTLE_MUSIC = "/sounds/rival_battle.wav";
    private static final String VICTORY_FANFARE      = "/sounds/victory_wild_pokemon.wav";

    private MusicState state = MusicState.IDLE;

    private Location currentLocation;

    private Clip activeClip;
    private String activeClipPath;

    private Clip fadingOutClip;
    private Clip fadingInClip;
    private String fadingInClipPath;

    private int fadeFramesRemaining;

    // ---- Public API ----

    /** Called every frame from Game.update(). Drives crossfade and fanfare completion. */
    public void update() {
        switch (state) {
            case CROSSFADING, FANFARE_TO_LOCATION -> updateCrossfade();
            case PLAYING_FANFARE                  -> updateFanfare();
            default                               -> {}
        }
    }

    /**
     * Start music for the given location. Crossfades if a different track is already playing.
     * No-op if the same track is already at full volume.
     */
    public void playLocationMusic(Location location) {
        String newPath = location.getMusicPath();
        this.currentLocation = location;

        if (newPath == null) {
            stopAllMusic();
            state = MusicState.IDLE;
            return;
        }

        if (state == MusicState.PLAYING_LOCATION && newPath.equals(activeClipPath)) {
            return; // already playing this track — do nothing
        }

        Clip newClip = SoundManager.getMusicClip(newPath);
        if (newClip == null) return;

        if (activeClip != null && activeClip.isRunning()) {
            startCrossfade(activeClip, newClip, newPath);
            state = MusicState.CROSSFADING;
        } else {
            stopAllMusic();
            SoundManager.setClipVolume(newClip, 1.0f);
            SoundManager.startClipLooping(newClip);
            activeClip = newClip;
            activeClipPath = newPath;
            state = MusicState.PLAYING_LOCATION;
        }
    }

    /**
     * Hard-cut to battle music. No fade. Immediately stops all other music.
     * Called when a battle transition begins.
     */
    public void playBattleMusic(boolean isWild) {
        stopAllMusic();

        String path = isWild ? WILD_BATTLE_MUSIC : TRAINER_BATTLE_MUSIC;
        Clip clip = SoundManager.getMusicClip(path);
        if (clip == null) return;

        SoundManager.setClipVolume(clip, 1.0f);
        SoundManager.startClipLooping(clip);
        activeClip = clip;
        activeClipPath = path;
        state = MusicState.PLAYING_BATTLE;
    }

    /**
     * Stop battle music and play the victory fanfare once (no loop).
     * Called when the player wins a battle.
     */
    public void playVictoryFanfare() {
        stopAllMusic();

        Clip clip = SoundManager.getMusicClip(VICTORY_FANFARE);
        if (clip == null) return;

        SoundManager.setClipVolume(clip, 1.0f);
        SoundManager.startClipOnce(clip);
        activeClip = clip;
        activeClipPath = VICTORY_FANFARE;
        state = MusicState.PLAYING_FANFARE;
    }

    /**
     * Crossfade back to the current location's music from whatever is playing.
     * Location music always restarts from the beginning.
     * Called when the player exits a battle (win or run).
     */
    public void resumeLocationMusic() {
        if (currentLocation == null || currentLocation.getMusicPath() == null) {
            stopAllMusic();
            state = MusicState.IDLE;
            return;
        }

        String locationPath = currentLocation.getMusicPath();
        Clip locationClip = SoundManager.getMusicClip(locationPath);
        if (locationClip == null) return;

        if (activeClip != null && activeClip.isRunning()) {
            startCrossfade(activeClip, locationClip, locationPath);
            state = MusicState.FANFARE_TO_LOCATION;
        } else {
            stopAllMusic();
            SoundManager.setClipVolume(locationClip, 1.0f);
            SoundManager.startClipLooping(locationClip);
            activeClip = locationClip;
            activeClipPath = locationPath;
            state = MusicState.PLAYING_LOCATION;
        }
    }

    public MusicState getState() {
        return state;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    /** Update the tracked location without changing music (e.g. during a pending world apply). */
    public void setCurrentLocation(Location location) {
        this.currentLocation = location;
    }

    // ---- Internal ----

    private void startCrossfade(Clip outClip, Clip inClip, String inPath) {
        // Clean up any previous in-progress fade-out
        SoundManager.stopClip(fadingOutClip);

        fadingOutClip = outClip;
        fadingInClip = inClip;
        fadingInClipPath = inPath;

        SoundManager.setClipVolume(fadingInClip, 0.0f);
        SoundManager.startClipLooping(fadingInClip);

        fadeFramesRemaining = CROSSFADE_FRAMES;
        activeClip = null;
        activeClipPath = null;
    }

    private void updateCrossfade() {
        if (fadeFramesRemaining <= 0) {
            SoundManager.stopClip(fadingOutClip);
            SoundManager.setClipVolume(fadingInClip, 1.0f);

            activeClip = fadingInClip;
            activeClipPath = fadingInClipPath;
            fadingOutClip = null;
            fadingInClip = null;
            fadingInClipPath = null;

            state = MusicState.PLAYING_LOCATION;
            return;
        }

        fadeFramesRemaining--;
        float progress = 1.0f - ((float) fadeFramesRemaining / CROSSFADE_FRAMES);

        SoundManager.setClipVolume(fadingOutClip, 1.0f - progress);
        SoundManager.setClipVolume(fadingInClip, progress);
    }

    private void updateFanfare() {
        // Fanfare plays once — detect when the clip stops naturally
        if (activeClip != null && !activeClip.isRunning()) {
            activeClip = null;
            activeClipPath = null;
            state = MusicState.IDLE;
            // resumeLocationMusic() will be called when the player dismisses the win screen
        }
    }

    private void stopAllMusic() {
        SoundManager.stopClip(activeClip);
        SoundManager.stopClip(fadingOutClip);
        SoundManager.stopClip(fadingInClip);

        activeClip = null;
        activeClipPath = null;
        fadingOutClip = null;
        fadingInClip = null;
        fadingInClipPath = null;
    }
}
