package com.dawnfall.engine.Server.features;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.RandomXS128;
import com.badlogic.gdx.utils.Disposable;
import com.dawnfall.engine.DawnStar;
import com.dawnfall.engine.Server.util.IO.OptionProvider;
import com.dawnfall.engine.Server.util.Utils;

import java.util.ArrayList;

public class GameAudioRegistry implements Disposable {
    //This holds all the game audio sounds and music.
    public final ArrayList<Audio> AudioTracks = new ArrayList<>();
    private final RandomXS128 randomAudio;
    public float volume;
    public final float audioDelay;
    public float audioDelayCount = 0;
    public boolean currentlyPlayingAudio;
    private Audio audio;
    public GameAudioRegistry() {
        randomAudio = new RandomXS128(10);
        audioDelay = OptionProvider.AUDIO_DELAY;
        volume = OptionProvider.MUSIC_VOLUME;
        AudioTracks.add(registerAudio("open_fields", (byte) 0, Utils.getFile("audio/music/open_fields.wav"),false));
        AudioTracks.add(registerAudio("button_clicked", (byte) 1, Utils.getFile("audio/sounds/button_clicked.mp3"),false));
    }
    private Audio registerAudio(String name, byte id, FileHandle path,boolean isSound){
        if (isSound){
            final Sound sound = Gdx.audio.newSound(path);
            return new Audio(name,id,sound);
        }else {
            final Music music = Gdx.audio.newMusic(path);
            return new Audio(name,id,music);
        }
    }
    public Audio getAudio(int id){
       return AudioTracks.get(id);
    }

    public void updateAudioRegistry(){
      if (audioDelayCount >= audioDelay) {
          audioDelayCount = 0;
          if (!currentlyPlayingAudio){
              if (randomAudio.nextInt() < 5){
                  audio = getAudio(0);
                  audio.playMusic(volume);
                  currentlyPlayingAudio = audio.isPlaying;
              }
          }
      }
      audioDelayCount += 0.01f;
    }

    @Override
    public void dispose() {
        for (Audio audioTrack : AudioTracks) {
            if (audioTrack != null){
                audioTrack.dispose();
            }
           if (audio != null){
               audio.dispose();
           }
        }
    }
}
