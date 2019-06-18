package de.hsmw.tkretzs1.energetic.core.level_impl;


import de.hsmw.tkretzs1.energetic.core.Game;
import de.hsmw.tkretzs1.energetic.core.Level;
import de.hsmw.tkretzs1.energetic.utils.FileLoader;
import org.jsfml.graphics.*;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;
import org.jsfml.window.event.Event;

import java.util.ArrayList;

/**
 * Menü-Level.
 * Repräsentiert das Hauptmenü welches erscheint, wenn der Spieler das Programm startet.
 *
 * @author Tom Kretzschmar
 */
public class Menu implements Level {

    /**
     * Titel-Sprite (Grafik)
     */
    Sprite titel;

    /**
     * Menü-Sprite (Grafik)
     */
    Sprite menu;

    /**
     * Konstruktor.
     * Legt neues Menü-Level an.
     */
    public Menu(){

        // Sprites laden
        Texture tex1 = FileLoader.loadTexture("res\\images\\energetic.png");
        titel = new Sprite(tex1);
        Texture tex2 = FileLoader.loadTexture("res\\images\\menu.png");
        menu = new Sprite(tex2);
    }

    /**
     * Zeichnet das Level auf das RenderTarget.
     * Stellt alle Elemente des Levels in Abhängigkeit der implementierten Logik dar.
     * @param renderTarget Das Renderziel, auf das gezeichnet werden soll.
     * @param renderStates aktuelle RenderStates
     */
    @Override
    public void draw(RenderTarget renderTarget, RenderStates renderStates) {

        // Skalierungsfaktor anhand der Bildschirmgröße ermitteln und Werte setzen
        float fac = (float) Game.getInstance().getViewBounds().width/(float)titel.getTexture().getSize().x;
        titel.setScale(fac, fac);
        titel.setPosition(Game.getInstance().getViewBounds().left, Game.getInstance().getViewBounds().top);


        menu.setScale(fac, fac);
        menu.setPosition(Game.getInstance().getViewBounds().left, Game.getInstance().getViewBounds().top + Game.getInstance().getViewBounds().height/2);


        renderTarget.draw(titel);
        renderTarget.draw(menu);
    }

    /**
     * Update-Methode.
     * Gibt Events an Hilfsmethode weiter.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     * @param events von Game übergebene Events
     */
    @Override
    public void update(Time dt, ArrayList<Event> events) {
        handleEvents(events);
    }

    /**
     * Gibt Kameraposition in Weltkoordinaten zurück.
     * @return Kamerposition
     */
    @Override
    public Vector2f getCameraPos() {
        return new Vector2f(0,0);
    }

    /**
     * @see de.hsmw.tkretzs1.energetic.core.Level
     * @param position Position der Explosion
     */
    @Override
    public void addExplosion(Vector2f position) {

    }

    /**
     * Hilfsmethode zum behandeln der übergebenen Events.
     * Verarbeitet Nutzereingaben zum Verlassen und Starten des Spiels.
     * @param events übergebene Events.
     */
    private void handleEvents(ArrayList<Event> events){
        for (Event event : events) {
            if (event.type == Event.Type.KEY_PRESSED) {
                if (event.asKeyEvent().key == Keyboard.Key.SPACE) {
                    Game.getInstance().startLevel(1);
                }
                if (event.asKeyEvent().key == Keyboard.Key.ESCAPE) {
                    Game.getInstance().exitGame();
                }
            }
        }
    }
}
