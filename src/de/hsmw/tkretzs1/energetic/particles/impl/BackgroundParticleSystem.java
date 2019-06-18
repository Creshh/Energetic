package de.hsmw.tkretzs1.energetic.particles.impl;

import de.hsmw.tkretzs1.energetic.core.Game;
import de.hsmw.tkretzs1.energetic.particles.Particle;
import de.hsmw.tkretzs1.energetic.particles.ParticleSystem;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

import java.util.Iterator;

/**
 * Partikelsystem für den Hintergrund des Spielfeldes.
 * Repräsentiert ein Partikelsystem mit Update- und Zeichenfunktion für die Darstellung der Sterne im Hintergrund. Hält Referenzen zu allen Partikeln des Systems.
 *
 * @see de.hsmw.tkretzs1.energetic.particles.ParticleSystem
 * @see de.hsmw.tkretzs1.energetic.particles.impl.BackgroundParticle
 *
 * @author Tom Kretzschmar
 */
public class BackgroundParticleSystem extends ParticleSystem
{

    /**
     * aktuelle Position des Spieler-Raumschiffs
     */
    private Vector2f playerPosition;

    /**
     * Ausgangsfarbe der Partikel
     */
    private Color color;

    /**
     * Ausgangsgröße der Partikel
     */
    private float size;

    /**
     * Konstruktor.
     * Legt neues Partikelsystem an und setzt Initialwerte.
     *
     * @param maxParticles maximale Anzahl an Partikeln
     * @param color Ausgangsfarbe
     * @param size Ausgangsgröße
     */
    public BackgroundParticleSystem(int maxParticles, Color color, float size)
    {
        super(maxParticles);
        this.color = color;
        this.size = size;
        this.playerPosition = Vector2f.ZERO;
    }

    /**
     * Konstruktor
     * Legt neues Partikelsystem an und setzt Standardwerte.
     * @param maxParticles maximale Anzahl an Partikeln
     */
    public BackgroundParticleSystem(int maxParticles)
    {
        this(maxParticles, new Color(1, 8, 16), 2.0F);
    }

    /**
     * Erzeugt zufällige Partikel innerhalb des sichtbaren Ausschnitts und füllt das Partikelsystem.
     */
    public void fill()
    {
        IntRect viewBounds = Game.getInstance().getViewBounds();
        for (int i = 0; i < this.maxParticles; i++)
        {
            Vector2f position = new Vector2f(Math2D.randomInRange(viewBounds.left, viewBounds.left + viewBounds.width), Math2D.randomInRange(viewBounds.top, viewBounds.top + viewBounds.height));
            BackgroundParticle particle = new BackgroundParticle(position, new Color(Color.mul(this.color, Math2D.random(255)), (int)(Math2D.random(140))), this.size * Math2D.random(), 150.0F); // neues Partikel mit zufälliger Farbe ausgehend von der übergebenen Farbe
            this.particleList.add(particle);
        }
    }

    /**
     * Update-Methode.
     * Aktualisiert alle Partikel und entfernt diese, wenn tot.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    @Override
    public void update(Time dt)
    {
        Iterator<Particle> it = this.particleList.iterator();
        while (it.hasNext())
        {
            BackgroundParticle particle = (BackgroundParticle)it.next();
            if (particle.isAlive().booleanValue())
            {
                particle.setPlayerPosition(this.playerPosition);
                particle.update(dt);
            }
            else
            {
                it.remove();
            }
        }
    }

    /**
     * Spielerposition aktualisieren.
     * @param playerPosition Positionsvektor
     */
    public void setPlayerPosition(Vector2f playerPosition)
    {
        this.playerPosition = playerPosition;
    }
}
