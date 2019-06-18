package de.hsmw.tkretzs1.energetic.particles.impl;

import de.hsmw.tkretzs1.energetic.particles.Particle;
import de.hsmw.tkretzs1.energetic.particles.ParticleSystem;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.Color;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

import java.util.Iterator;

/**
 * Partikelsystem für Asteroiden.
 * Repräsentiert ein Partikelsystem mit Update- und Zeichenfunktion für das Spielobjekt Asteroid und greenAsteroid. Hält Referenzen zu allen Partikeln des Systems.
 *
 * @see de.hsmw.tkretzs1.energetic.particles.ParticleSystem
 * @see de.hsmw.tkretzs1.energetic.particles.impl.AsteroidParticle
 *
 * @author Tom Kretzschmar
 */
public class AsteroidParticleSystem extends ParticleSystem {

    /**
     * aktuelle Position
     */
    private Vector2f position;

    /**
     * Anzahl der Partikel oder Fragmente
     */
    private int fragments;

    /**
     * Radius des Asteroiden
     */
    public int radius;

    /**
     * Ausgangsfarbe der Prtikel
     */
    private Color color;

    /**
     * Konstuktor.
     * Legt neues Partikelsystem an und setzt Initialwerte.
     * @param position Position
     * @param fragments Anzahl der Partikel
     * @param color Ausgangsfarbe
     */
    public AsteroidParticleSystem(Vector2f position, int fragments, Color color) {
        super(fragments);
        this.position = position;
        this.fragments = fragments;
        radius = 50;
        this.color = color;
        fill();

    }

    /**
     * Erzeugt zufällige Partikel innerhalb des Radius und füllt das Partikelsystem.
     */
    private void fill(){
        for (int i = 0; i < fragments; i++) {
            Color temp = Color.mul(color, Math2D.random()); // Farbvariation

            AsteroidParticle p = new AsteroidParticle(Vector2f.add(Math2D.rndPointOnDisc(radius),position), temp, 10); // Erzeugen an zufälliger Position
            particleList.add(p);
        }
    }

    /**
     * Update-Methode.
     * Nicht implementiert - siehe setPosition()
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    @Override
    public void update(Time dt) {}

    /**
     * Hilfsmethode zum aktualisieren der Position des gesamten Partikelsystems.
     * Wird hier benötigt, da sich die Partikel nicht eigenständig Bewegen sondern immer im gleichen Abstand zueiander bleiben.
     * Entfernt Partikel, wenn diese tot sind.
     *
     * @param s zurückgelegt
     */
    public void setPosition(Vector2f s){
        this.position = Vector2f.add(position, s);
        Iterator<Particle> it = particleList.iterator();
        AsteroidParticle particle;
        while(it.hasNext()){
            particle = (AsteroidParticle)it.next();
            if(particle.isAlive())
                particle.setPosition(s);
            else
                it.remove();
        }
    }

    /**
     * Aktuelle Position zurückgeben.
     * @return Positionsvektor.
     */
    public Vector2f getPosition() {
        return position;
    }
}
