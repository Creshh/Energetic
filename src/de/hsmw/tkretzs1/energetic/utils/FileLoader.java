package de.hsmw.tkretzs1.energetic.utils;

import org.jsfml.audio.Music;
import org.jsfml.audio.Sound;
import org.jsfml.audio.SoundBuffer;
import org.jsfml.graphics.Font;
import org.jsfml.graphics.Texture;

import java.io.File;
import java.io.IOException;

/**
 * FileLoader stellt Hilfsmethoden zum Laden von Ressourcen aus Dateien bereit.
 * @author Tom Kretzschmar
 */
public class FileLoader {

    /**
     * Läd Textur aus Datei spezifiert in "path".
     * @param path Pfad zur Textur-Datei
     * @return Textur-Objekt
     */
    public static Texture loadTexture(String path){
        Texture tex = new Texture();
        try {
            tex.loadFromFile(new File(path).toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tex;
    }

    /**
     * Lädt Standardmäßigen Font aus Datei.
     * @return Font Objekt
     */
    public static Font loadFont() {
        Font font = new Font();
        try {
            font.loadFromFile(new File("res\\courbd.ttf").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return font;
    }

    /**
     * Läd Hintergrundmusik aus Datei.
     * @return Music Objekt
     */
    public static Music openMusic(){
        Music music = new Music();
        try {
            music.openFromFile(new File("res\\sound\\tfh3.ogg").toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return music;
    }

    /**
     * Läd Sound aus Datei spezifiert in "path".
     * @param path Pfad zur Datei
     * @return
     */
    public static Sound loadSound(String path){
        SoundBuffer soundBuffer = new SoundBuffer();
        try {
            soundBuffer.loadFromFile(new File(path).toPath());
        } catch(IOException ex) {
            System.err.println("Failed to load the sound:");
            ex.printStackTrace();
        }

        // Erstelle Sound und setzte Bufferobjekt
        Sound sound = new Sound();
        sound.setBuffer(soundBuffer);
        return sound;
    }
}
