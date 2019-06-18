package de.hsmw.tkretzs1.energetic.particles;

        import org.jsfml.graphics.Color;
        import org.jsfml.system.Time;
        import org.jsfml.system.Vector2f;

/**
 * Basisklasse für Partikel.
 * Repräsentiert ein Partikel eines Partikelsystems mit Farbe, Position und Größe.
 *
 * @author Tom Kretzschmar
 * @see de.hsmw.tkretzs1.energetic.particles.ParticleSystem
 */
public abstract class Particle
{
    /**
     * Position des Partikels.
     */
    protected Vector2f position;

    /**
     * Farbe de Partikels.
     */
    protected Color color;

    /**
     * Größe des Partikels.
     */
    protected float size;

    /**
     * Partikel tot oder lebendig.
     */
    protected boolean alive;

    /**
     * Konstruktor. Legt neues Partikel an.
     *
     * @param position Position des Partikel.
     * @param color Farbe des Partikel.
     * @param size Größe des Partikel.
     */
    public Particle(Vector2f position, Color color, float size)
    {
        this.position = position;
        this.color = color;
        this.size = size;
        this.alive = true;
    }

    /**
     * Update-Methode. Zum Simulieren von Bewegung oder Farbveränderungen.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    public abstract void update(Time dt);

    /**
     * Get-Methode für boolean alive.
     * @return alive
     */
    public Boolean isAlive()
    {
        return Boolean.valueOf(this.alive);
    }
}
