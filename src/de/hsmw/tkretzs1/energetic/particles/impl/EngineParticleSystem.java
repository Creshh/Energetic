package de.hsmw.tkretzs1.energetic.particles.impl;

import de.hsmw.tkretzs1.energetic.particles.Particle;
import de.hsmw.tkretzs1.energetic.particles.ParticleSystem;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.Color;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

import java.util.Iterator;

/**
 * Partikelsystem für das Triebwerk des Spielerraumschiffs.
 * Repräsentiert ein Partikelsystem mit Update- und Zeichenfunktion für die Darstellung des Triebwerks. Hält Referenzen zu allen Partikeln des Systems.
 *
 * @see de.hsmw.tkretzs1.energetic.particles.ParticleSystem
 * @see de.hsmw.tkretzs1.energetic.particles.impl.EngineParticle
 *
 * @author Tom Kretzschmar
 */
public class EngineParticleSystem extends ParticleSystem{

    /**
     * Geschwindigkeit der Partikel wenn Raumschiff beschleunigt.
     */
    public static final int VELOCITY_BOOST = 850;

    /**
     * Geschwindigkeit der Partikel wenn Raumschiff stillsteht.
     */
    public static final int VELOCITY_IDLE = 500;

    /**
     * Position des Partikelsystemursprungs
     */
    private Vector2f position;

    /**
     * Richtungswinkel, in die die Partikel emittiert werden
     */
    private float angle;

    /**
     * Größe der Partikel
     */
    private float particleSize;

    /**
     * aktuelle Geschwindigkeit mit der Partikel erzeugt werden
     */
    private float velocity;

    /**
     * Maximalwert des Streuwinkels
     */
    private float maxSpread;

    /**
     * Lebenszeit der Partikel
     */
    private Time lifetime;

    /**
     * Anzahl der Partikel, die pro Sekunde emittiert werden
     */
    private int respawnCount;

    /**
     * Farbe der Partikel
     */
    private Color color;

    /**
     * Simpler Konstuktor.
     * Legt neues Partikelsystem an und setzt Initialwerte sowie Standardwerte für einige Variablen.
     *
     * @param maxParticles Maximale Anzahl von Partikeln im System
     * @param particleSize Größe der Partikel
     * @param maxSpread Maximaler Streuwinkel
     * @param lifetime Lebenszeit der Partikel
     * @param respawnCount Anzahl pro Sekunde emittierter Partikel
     * @param color Farbe der Partikel
     */
    public EngineParticleSystem(int maxParticles, float particleSize, float maxSpread, Time lifetime, int respawnCount, Color color) {
        this(maxParticles, new Vector2f(0,0), 0.0f, particleSize, 0.0f, maxSpread, lifetime, respawnCount, color);
    }

    /**
     * Konstukror.
     * Legt neues Partikelsystem an und setzt Initialwerte.
     *
     * @param maxParticles Maximale Anzahl von Partikeln im System
     * @param position Position des Partikelsystems
     * @param angle Richtungswinkel, in die die Partikel emittiert werden
     * @param particleSize Größe der Partikel
     * @param velocity Geschwindigkeit der Partikel
     * @param maxSpread Maximaler Streuwinkel
     * @param lifetime Lebenszeit der Partikel
     * @param respawnCount Anzahl pro Sekunde emittierter Partikel
     * @param color Farbe der Partikel
     */
    public EngineParticleSystem(int maxParticles, Vector2f position, float angle, float particleSize, float velocity, float maxSpread, Time lifetime, int respawnCount, Color color) {
        super(maxParticles);
        this.position = position;
        this.angle = angle;
        this.particleSize = particleSize;
        this.velocity = velocity;
        this.maxSpread = maxSpread;
        this.lifetime = lifetime;
        this.respawnCount = respawnCount;
        this.color = color;
    }

    /**
     * Update-Methode.
     * Aktualisiert alle Partikel und entfernt diese, wenn tot.
     * Emittiert außerdem pro Durchlauf eine bestimmte Anzahl Partikel, sodass das Partikelsystem gleichmäßig dargestellt wird.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    @Override
    public void update(Time dt) {
        // wenn Kapazität nicht überschritten - Partikel emittieren
        if(particleList.size()<maxParticles){

            // Anzahl zu emittierender Partikel anhand Partikel pro Sekunde und DeltaTime berechnen
            for (int i = 0; i < respawnCount*dt.asSeconds(); i++) {

                // Streuwinkel zufällig bestimmen
                float t_spread = (float) Math.random()*maxSpread-(maxSpread/2);

                // Geschwindigkeit zufällig bestimmen
                Vector2f t_velocity = Math2D.rotate(new Vector2f((float) Math.random() * velocity / 2 *Math2D.randomInRange(0.9f,1.1f) + velocity * 0.2f*Math2D.randomInRange(0.9f,1.1f), 0), angle);

                // Neues Partikel erzeugen und hinzufügen
                EngineParticle p = new EngineParticle(position, t_velocity, t_spread, color, particleSize, lifetime);
                particleList.add(p);
            }
        }

        // Partikel updaten und löschen wenn nötig
        Iterator<Particle> it = particleList.iterator();
        Particle particle;
        while(it.hasNext()){
            particle = it.next();
            if(particle.isAlive())
                particle.update(dt);
            else
                it.remove();
        }
    }

    /**
     * Emitterposition setzen
     * @param position Positionsvektor
     */
    public void setPosition(Vector2f position) {
        this.position = position;
    }

    /**
     * Richtungswinkel des Emitters setzen
     * @param angle Winkel
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    /**
     * Geschwindigkeit für neue Partikel setzen
     * @param velocity Geschwindigkeit (Skalar)
     */
    public void setVelocity(float velocity) {
        this.velocity = velocity;
    }
}
