package com.dawnfall.engine.Server.features;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Disposable;

public class Audio implements Disposable {
    public byte id;
    public String name;
    public Music music;
    public Sound sound;
    public boolean isPlaying;
    public boolean isLooping;
    private float volume = 0.4f;
    public Audio(String name,byte id, Music music){
        this.id = id;
        this.name = name;
        this.music = music;
    }

    public Audio(String name, byte id,Sound sound){
        this.id = id;
        this.name = name;
        this.sound = sound;
    }

    public Audio(String name, byte id, Music music,Sound sound){
        this.id = id;
        this.name = name;
        this.sound = sound;
        this.music = music;
    }

    public void playMusic(){
        this.music.setVolume(volume);
        this.music.play();
        isPlaying = this.music.isPlaying();
    }
    public void playMusic(float volume){
        this.music.play();
        music.setVolume(volume);
        isPlaying = this.music.isPlaying();
        isLooping = this.music.isLooping();
    }

    public void playMusic(boolean loop,float volume){
        music.setLooping(loop);
        music.setVolume(volume);
        this.music.play();
        isPlaying = this.music.isPlaying();
        isLooping = this.music.isLooping();
    }

    public void playMusic(boolean loop){
        this.music.play();
        isPlaying = this.music.isPlaying();
        isLooping = this.music.isLooping();
    }

    public void playSound(){
       sound.play();
    }
    public void playSound(float volume,boolean looping){
      long id = sound.play();
        sound.setVolume(id,volume);
        sound.setLooping(id,true);
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    @Override
    public void dispose() {
        if (music != null){
            music.dispose();
        }
        if (sound != null){
            sound.dispose();
        }
    }
}
