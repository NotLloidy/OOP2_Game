package UTILS;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Usage:
 *   SoundManager.playBGM(SoundManager.BGM_MENU);      // loop background music
 *   SoundManager.playBGM(SoundManager.BGM_BATTLE);    // loop battle music
 *   SoundManager.stopBGM();                           // stop current BGM
 *   SoundManager.playSFX(SoundManager.SFX_BUTTON);    // one-shot SFX
 *
 */
public class SoundManager {

    // ── BGM paths ─────────────────────────────────────────────────────────
    public static final String BGM_MENU   = "Assets/audio/misc/bgm_menu.wav";
    public static final String BGM_BATTLE = "Assets/audio/misc/bgm_battle.wav";

    // ── SFX paths ─────────────────────────────────────────────────────────
    public static final String SFX_BUTTON   = "Assets/audio/misc/sfx_button.wav";
    public static final String SFX_GAME_WIN = "Assets/audio/misc/sfx_game_win.wav";
    public static final String SFX_GAME_OVER= "Assets/audio/misc/sfx_game_over.wav";

    // ── Character names ─────────────────
    public static final String CHAR_A_VIN          = "A-Vin";
    public static final String CHAR_BRIVAN_JAWMIR  = "Brivan Jawmir";
    public static final String CHAR_CHUNG_MYUNG    = "Chung-Myung";
    public static final String CHAR_KENNETH        = "Kenneth";
    public static final String CHAR_KIJEL          = "Kij-EL";
    public static final String CHAR_SOLEIL         = "Soleil Mooncrest";
    public static final String CHAR_SUNG_JIN_WOO   = "Sung Jin-Woo";
    public static final String CHAR_ZAKKARR        = "Zakkarr";

    // ── Internal state ────────────────────────────────────────────────────
    private static Clip   bgmClip        = null;
    private static String currentBgmPath = null;
    private static float  bgmVolume      = 0.75f;   // 0.0 – 1.0
    private static float  sfxVolume      = 1.0f;    // 0.0 – 1.0

    // ── BGM ───────────────────────────────────────────────────────────────

    /**
     * Starts looping the given BGM track.
     * If the same track is already playing, does nothing.
     * Stops any previously playing BGM first.
     */
    public static void playBGM(String path) {
        if (path != null && path.equals(currentBgmPath) && bgmClip != null && bgmClip.isRunning()) {
            return; // already playing
        }
        stopBGM();
        try {
            File f = new File(path);
            if (!f.exists()) { System.out.println("[SoundManager] BGM not found: " + path); return; }
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            bgmClip = AudioSystem.getClip();
            bgmClip.open(ais);
            setClipVolume(bgmClip, bgmVolume);
            bgmClip.loop(Clip.LOOP_CONTINUOUSLY);
            bgmClip.start();
            currentBgmPath = path;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("[SoundManager] Cannot play BGM: " + path + " (" + e.getMessage() + ")");
        }
    }

    /** Stops and closes any currently playing BGM. */
    public static void stopBGM() {
        if (bgmClip != null) {
            if (bgmClip.isRunning()) bgmClip.stop();
            bgmClip.close();
            bgmClip = null;
            currentBgmPath = null;
        }
    }

    // ── SFX ───────────────────────────────────────────────────────────────

    /**
     * Plays a one-shot sound effect on a background thread.
     * Safe to call from the EDT.
     */
    public static void playSFX(String path) {
        new Thread(() -> {
            try {
                File f = new File(path);
                if (!f.exists()) { System.out.println("[SoundManager] SFX not found: " + path); return; }
                AudioInputStream ais = AudioSystem.getAudioInputStream(f);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                setClipVolume(clip, sfxVolume);
                clip.addLineListener(event -> {
                    if (event.getType() == LineEvent.Type.STOP) clip.close();
                });
                clip.start();
            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
                System.out.println("[SoundManager] Cannot play SFX: " + path + " (" + e.getMessage() + ")");
            }
        }, "SFX-thread").start();
    }

    /**
    * Returns the SFX path for a given character and skill number.
    * Example: skillSFX("Sung Jin Woo", 2)
    *          → "Assets/audio/Sung Jin Woo/sfx_skill2.wav"
    */
    public static String skillSFX(String characterName, int skillNumber) {
        String file = switch (skillNumber) {
            case 1 -> "sfx_skill1.wav";
            case 2 -> "sfx_skill2.wav";
            case 3 -> "sfx_skill3.wav";
            default -> "sfx_skill1.wav";
        };
        return "Assets/audio/skillSFX/" + characterName + "/" + file;
    }

    // ── Volume ────────────────────────────────────────────────────────────

    public static void setBGMVolume(float vol) {
        bgmVolume = Math.max(0f, Math.min(1f, vol));
        if (bgmClip != null) setClipVolume(bgmClip, bgmVolume);
    }

    public static void setSFXVolume(float vol) {
        sfxVolume = Math.max(0f, Math.min(1f, vol));
    }

    // ── Private helpers ───────────────────────────────────────────────────

    private static void setClipVolume(Clip clip, float volume) {
        try {
            FloatControl fc = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Convert 0–1 linear to dB
            float db = (volume == 0f) ? fc.getMinimum()
                     : 20f * (float) Math.log10(volume);
            fc.setValue(Math.max(fc.getMinimum(), Math.min(fc.getMaximum(), db)));
        } catch (IllegalArgumentException ignored) {}
    }
}