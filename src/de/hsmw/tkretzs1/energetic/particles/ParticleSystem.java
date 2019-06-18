package de.hsmw.tkretzs1.energetic.particles;

import org.jsfml.graphics.*;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Basisklasse für Partikelsystem.
 * Repräsentiert ein Partikelsystem mit Update- und Zeichenfunktion. Hält Referenzen zu allen Partikeln des Systems.
 *
 * @author Tom Kretzschmar
 */
public abstract class ParticleSystem implements Drawable
{
    /**
     * Maximale Anzahl von Partikeln im System.
     */
    protected int maxParticles;

    /**
     * Liste mit Referenzen zu allen Partikeln.
     */
    protected List<Particle> particleList;

    /**
     * VertexArray zum optimierten Zeichnen aller Partikel.
     */
    private VertexArray vertexArray;

    /**
     * Konstruktor.
     * Legt neues Partikelsystem an.
     *
     * @param maxParticles Maximale Anzahl der Partikel im System.
     */
    public ParticleSystem(int maxParticles)
    {
        this.particleList = new ArrayList<>();
        this.vertexArray = new VertexArray(PrimitiveType.QUADS);    // legt neues VertexArray an - ein Partikel wird durch ein Quadrat mit 4 Eckpunkten beschrieben.
        this.maxParticles = maxParticles;
    }

    /**
     * Standardkonstruktor.
     * Legt Partikelsystem mit maximal 1000 Partikeln an.
     */
    public ParticleSystem()
    {
        this(1000);
    }

    /**
     * Update-Methode.
     * Zum Simulieren von Bewegung oder anderen Veränderungen.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     */
    public abstract void update(Time dt);

    /**
     * Zeichenmethode.
     * Zeichnet alle Partikel des Systems auf das übergebene RenderTarget.
     *
     * @param target RenderTarget auf welches die Partikel gezeichnet werden sollen.
     * @param renderStates Einstellungen mit denen gezeichnet wird.
     */
    public void draw(RenderTarget target, RenderStates renderStates)
    {
        computeVertices();
        target.draw(this.vertexArray);
    }

    /**
     * Berechnung der Vertices aus den aktuellen Positionen der Partikel.
     */
    private void computeVertices()
    {
        // VertexArray leeren und jedes Partikel mit aktueller Position neu einfügen.
        this.vertexArray.clear();
        for (Particle particle : this.particleList)
        {
            Vector2f half = new Vector2f(particle.size, particle.size);
            Vector2f pos = particle.position;
            Color c = particle.color;

            addVertex(pos.x - half.x, pos.y - half.y, c);
            addVertex(pos.x + half.x, pos.y - half.y, c);
            addVertex(pos.x + half.x, pos.y + half.y, c);
            addVertex(pos.x - half.x, pos.y + half.y, c);
        }
    }

    /**
     * Hilfsmethode zum einfügen von Vertices in das Vertexarray
     * @param worldX x-Koordinate
     * @param worldY y-Koordinate
     * @param color Farbe
     */
    private void addVertex(float worldX, float worldY, Color color)
    {
        Vertex vertex = new Vertex(new Vector2f(worldX, worldY), color);
        this.vertexArray.add(vertex);
    }
}
