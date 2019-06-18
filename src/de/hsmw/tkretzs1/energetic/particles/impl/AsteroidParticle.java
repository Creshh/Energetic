package de.hsmw.tkretzs1.energetic.particles.impl;

import de.hsmw.tkretzs1.energetic.particles.Particle;
import org.jsfml.graphics.Color;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
 * Partikel des AsteroidParticleSystems
 * Repräsentiert ein Partikel für die Darstellung eines Asteroids mit Farbe, Position und Größe.
 *
 * @author Tom Kretzschmar
 * @see de.hsmw.tkretzs1.energetic.particles.impl.AsteroidParticleSystem
 */
public class AsteroidParticle extends Particle {

    /**
     * Konstruktor. Legt neues Partikel an.
     *
     * @param position Position
     * @param color Farbe
     * @param size Größe
     */
    public AsteroidParticle(Vector2f position, Color color, float size) {
        super(position, color, size);
    }

    /**
     * Update-Methode.
     * Nicht benötigt und nicht implementiert.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    @Override
    public void update(Time dt){}

    /**
     * Versetzt Position des Partikels.
     * @param s Weg, um den versetzt werden soll.
     */
    protected void setPosition(Vector2f s){
        super.position = Vector2f.add(position, s);
    }
}