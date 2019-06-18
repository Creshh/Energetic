package de.hsmw.tkretzs1.energetic.particles.impl;

import de.hsmw.tkretzs1.energetic.utils.Circle;
import de.hsmw.tkretzs1.energetic.core.Game;
import de.hsmw.tkretzs1.energetic.particles.Particle;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.IntRect;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
* Partikel des BackgroundParticleSystems
* Repräsentiert ein Partikel für die Darstellung der Sterne des Hintergrunds mit Farbe, Position und Größe.
*
* @author Tom Kretzschmar
* @see de.hsmw.tkretzs1.energetic.particles.impl.AsteroidParticleSystem
*/
public class BackgroundParticle extends Particle
{
    /**
     * originale Position auf dem Spielfeld
     */
    private Vector2f origPos;

    /**
     * minimaler Abstand zum Spieler
     */
    private float distance;

    /**
     * aktuelle Spielerposition
     */
    private Vector2f playerPosition;

    /**
     * originale Farbe, benötigt für die Rotfärbung außerhalb des Spielfeldes
     */
    private Color oldColor;

    /**
     * Konstruktor. Legt neues Partikel an.
     * @param position Position
     * @param color Farbe
     * @param size Größe
     * @param distance minimaler Abstand zum Spieler
     */
    public BackgroundParticle(Vector2f position, Color color, float size, float distance)
    {
        super(position, color, size);
        oldColor = color;
        this.distance = distance;
        this.playerPosition = Vector2f.ZERO;
        this.origPos = position;
    }

    /**
     * Update-Methode.
     * Verschiebt Partikel in Bezug auf die Sichtbare Spielfläche, die Position des Spielers und färbt sie in Bezug auf die Spielflächenbegrenzung.
     * Durch diese Methode muss nicht das gesamte Spielfeld, sondern nur der sichtbare Bereich gefüllt werden, was zu einer erheblichen Minimierung der Anzahl der Partikel führt.
     * Partikel die den Bildschirmausschnitt verlassen, werden auf die andere Seite verschoben und dort neu gezeichnet. Partikel die außerhalb des Spielfeldes sind werden rot gezeichnet
     * und Partikel die zu nahe am Spieler sind, von diesem weggeschoben.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    @Override
    public void update(Time dt)
    {
        // Verschieben an den Bildschirmrändern
        IntRect viewBounds = Game.getInstance().getViewBounds();
        if (position.x < viewBounds.left)
        {
            position = new Vector2f(position.x + viewBounds.width, position.y);
            origPos = position;
        }
        else if (position.x > viewBounds.left + viewBounds.width)
        {
            position = new Vector2f(position.x - viewBounds.width, position.y);
            origPos = position;
        }
        if (position.y < viewBounds.top)
        {
            position = new Vector2f(position.x, position.y + viewBounds.height);
            origPos = position;
        }
        else if (position.y > viewBounds.top + viewBounds.height)
        {
            position = new Vector2f(position.x, position.y - viewBounds.height);
            origPos = position;
        }

        // Bewegungsrichtung berechnen
        Vector2f direction = Vector2f.sub(this.position, this.playerPosition);

        // Wenn kleiner der Distanz zum Spieler - Wegschieben
        if (Math2D.getLength(direction) < this.distance)
        {
            float curr_distance = Math2D.getLength(direction);
            direction = Math2D.normalize(direction);

            this.position = Vector2f.add(this.position, Vector2f.mul(direction, 30 / curr_distance * dt.asSeconds() * 1000));   // Wegschieben erfolgt langsam, in einer beschleunigten Bewegung
        }
        // Wenn größer - zurück zur Originalposition
        else if (!this.position.equals(this.origPos))
        {
            direction = Vector2f.sub(this.origPos, this.position);
            this.position = Vector2f.add(this.position, Vector2f.mul(direction, 5 * dt.asSeconds()));
        }

        // Wenn außerhalb des Spielfeldes - rot einfärben
        Circle worldbounds = Game.getInstance().getWorldBounds();
        Vector2f distance = Vector2f.sub(position, new Vector2f(worldbounds.position));
        if(Math2D.getLength(distance) > worldbounds.radius){
            color = Color.RED;
        } else {
            color = oldColor;
        }

    }

    /**
     * Setzen der aktuellen Spielerposition.
     * @param playerPosition Positionsvektor des Spielers.
     */
    public void setPlayerPosition(Vector2f playerPosition)
    {
        this.playerPosition = playerPosition;
    }
}
