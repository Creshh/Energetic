package de.hsmw.tkretzs1.energetic.particles.impl;

import de.hsmw.tkretzs1.energetic.particles.Particle;
import de.hsmw.tkretzs1.energetic.particles.ParticleSystem;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.Color;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

import java.util.Iterator;

/**
 * Partikelsystem für Explosionen.
 * Repräsentiert ein Partikelsystem mit Update- und Zeichenfunktion für die Darstellung der Explosionen bei Zerstörung eines Asteroiden. Hält Referenzen zu allen Partikeln des Systems.
 *
 * @see de.hsmw.tkretzs1.energetic.particles.ParticleSystem
 * @see de.hsmw.tkretzs1.energetic.particles.impl.EngineParticle
 *
 * @author Tom Kretzschmar
 */
public class ExplosionParticleSystem extends ParticleSystem {

    /**
     * Anzahl der Partikel des Systems, die pro Sekunde emittiert werden
     */
    private int respawnCount = 500;

    /**
     * Position an der die Partikel emittiert werden.
     */
    Vector2f position;

    /**
     * Lebenszeit des Partikelsystems
     */
    Time lifetime;

    /**
     * Flag, ob System noch Dargestellt werden muss.
     */
    boolean alive = true;

    /**
     * Konstuktor.
     * Legt neues Partikelsystem an und setzt Initialwerte.
     *
     * @param position Emitterposition
     * @param lifetime Lebenszeit des Partikelsystems
     */
    public ExplosionParticleSystem(Vector2f position, Time lifetime){
        this.position = position;
        this.lifetime = lifetime;
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

        // Lebenszeit aktualisieren und wenn kleiner 0 als tot markieren
        lifetime = Time.sub(lifetime, dt);
        if(lifetime.asSeconds()<0){
            alive = false;
        }

        // erste 20% Lebenszeit zum emittieren der Partikel nutzen
        if(lifetime.asSeconds()>0.8) {
            for (int i = 0; i < respawnCount * dt.asSeconds(); i++) {
                Vector2f velocity = Math2D.rndPointOnDonut(150, 220); // Zufällige Geschwindigkeit bestimmen
                particleList.add(new ExplosionParticle(position, new Color(255,(int)Math2D.randomInRange(50,255),1), 4.0f, velocity)); // neues Partikel erzeugen und hinzufügen
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
     * Get-Methode für boolean alive.
     * @return alive
     */
    public boolean isAlive(){
        return alive;
    }
}
