package frontEnd.Audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.File;

/**
 * Created by matt on 07/04/17.
 */
public class AudioManager {
    private static Clip clip;

    public static void play(File song) {
        if (!song.getName().endsWith(".wav")) {
            song = new File(song.getAbsolutePath()+".wav");
        }

        if (clip != null && clip.isRunning()) {
            clip.stop();
        }

        try {
            clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(song));

            clip.start();
        }
        catch (Exception exc) {
            exc.printStackTrace(System.out);
        }
    }
}
