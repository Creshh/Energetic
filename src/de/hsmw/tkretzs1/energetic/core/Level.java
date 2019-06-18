package de.hsmw.tkretzs1.energetic.core;


import org.jsfml.graphics.Drawable;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.window.event.Event;

import java.util.ArrayList;

/**
 * Level Interface.
 * Schnittstelle für die Kommunikation der Klasse Game mit den einzelnen Leveln.
 * Ein Level ist für die gesamte Spiellogik und aktuell angezeigten Elemente Zuständig. Es kann immer nur eine Levelimplementierung aktiv sein.
 */
public interface Level extends Drawable {

    /**
     * Update-Methode.
     * Zum Simulieren von Bewegung oder anderen Veränderungen der Spielobjekte oder Umgebungszustände im Level.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     * @param events von Game übergebene Events
     */
    public void update(Time dt, ArrayList<Event> events);

    /**
     * Gibt Kameraposition in Weltkoordinaten zurück.
     * @return Kamerposition
     */
    public Vector2f getCameraPos();

    /**
     * Fügt Explosions-Partikelsystem hinzu.
     * TODO: In zukünftiger Version Workaround anders lösen. U.u. Level von Menü's trennen.
     * @param position Position der Explosion
     */
    public void addExplosion(Vector2f position);


}
