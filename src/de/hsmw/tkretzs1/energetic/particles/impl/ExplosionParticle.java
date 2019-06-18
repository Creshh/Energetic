package de.hsmw.tkretzs1.energetic.particles.impl;

import de.hsmw.tkretzs1.energetic.particles.Particle;
import org.jsfml.graphics.Color;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
 * Partikel des ExplosionParticleSystems.
 * Repräsentiert ein Partikel für die Darstellung der Explosionen mit Farbe, Position, Größe und Geschwindkeit.
 *
 * @author Tom Kretzschmar
 * @see de.hsmw.tkretzs1.energetic.particles.impl.ExplosionParticleSystem
 */
public class ExplosionParticle extends Particle{

    /**
     * Geschwindigkeitsvektor
     */
    private Vector2f velocity;

    /**
     * aktuelle und anfängliche Lebenszeit
     */
    private Time lifetime = Time.getSeconds(0.3f);

    /**
     * Konstruktor. Legt neues Partikel an.
     *
     * @param position Position
     * @param color Farbe
     * @param size Größe
     * @param velocity Geschwindigkeit
     */
    public ExplosionParticle(Vector2f position, Color color, float size, Vector2f velocity) {
        super(position, color, size);
        this.velocity = velocity;
    }

    /**
     * Update-Methode.
     * Simuliert Bewegung und Lebenszeit.
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    @Override
    public void update(Time dt) {

        // s = v * dt + s0 | Position berechnen
        position = Vector2f.add(position, Vector2f.mul(velocity, dt.asSeconds()));

        lifetime = Time.sub(lifetime, dt); // aktuelle Lebenszeit berechnen
        if(lifetime.asSeconds()<0){  // wenn Lebenszeit < 0: Partikel wird entfernt
            alive = false;
        }
    }
}
