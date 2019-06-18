package de.hsmw.tkretzs1.energetic.particles.impl;

import de.hsmw.tkretzs1.energetic.particles.Particle;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.Color;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
 * Partikel des EngineParticleSystems.
 * Repräsentiert ein Partikel für die Darstellung des Triebwerksstrahls mit Farbe, Position, Größe, Geschwindkeit und Lebenszeit.
 *
 * @author Tom Kretzschmar
 * @see de.hsmw.tkretzs1.energetic.particles.impl.EngineParticleSystem
 */
public class EngineParticle extends Particle {

    /**
     * Geschwindigkeitsvektor
     */
    private Vector2f velocity;

    /**
     * Streuwinkel
     */
    private float spread;

    /**
     * aktueller Wert der Lebenszeit
     */
    private Time lifetime;

    /**
     * Startwert der Lebenszeit
     */
    private Time maxLifetime;

    /**
     * Flag ob halbe Lebenszeit überschritten.
     */
    private boolean scnd = false;

    /**
     * Konstruktor. Legt neues Partikel an.
     *
     * @param position Position.
     * @param velocity Geschwindigkeit
     * @param spread Streuwinkel
     * @param color Farbe
     * @param size Größe
     * @param lifetime Lebenszeit
     */
    EngineParticle(Vector2f position, Vector2f velocity, float spread, Color color, float size, Time lifetime) {
        super(position, color, size);

        this.maxLifetime = lifetime;
        this.lifetime = lifetime;
        this.spread = spread;
        this.velocity = Math2D.rotate(velocity, spread); // Geschwindigkeitsvektor um Betrag des Streuwinkels drehen
    }

    /**
     * Update-Methode.
     * Simuliert Bewegung, Farbveränderung und Lebenszeit.
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    @Override
    public void update(Time dt) {

        // s = v * dt + s0 | Position berechnen
        position = Vector2f.add(position, Vector2f.mul(velocity, dt.asSeconds()));

        lifetime = Time.sub(lifetime, dt);  // aktuelle Lebenszeit berechnen
        alive = (lifetime.asSeconds() > 0); // wenn Lebenszeit < 0: Partikel wird entfernt
        float ratio = lifetime.asSeconds() / this.maxLifetime.asSeconds();  // Anteil der Lebenszeit für Farbveränderung
        color = new Color(color.r,color.g,color.b,(int)(255*ratio));
        if(!scnd && ratio < 0.5){   // Streuwinkel nach halber Lebenszeit umkehren um Form des Partikelsystems zu erreichen
            velocity = Math2D.rotate(velocity, -spread * 2);
            scnd = true;
        }
    }
}
