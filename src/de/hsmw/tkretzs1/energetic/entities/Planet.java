package de.hsmw.tkretzs1.energetic.entities;

import de.hsmw.tkretzs1.energetic.collision.GameObject;
import de.hsmw.tkretzs1.energetic.collision.impl.CircleCollider;
import de.hsmw.tkretzs1.energetic.utils.FileLoader;
import org.jsfml.graphics.*;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
 * Repräsentation des Planeten.
 * Spielobjekt, abgeleitet von GameObject, welches den Planeten im Mittelpunkt der Spielwelt darstellt.
 *
 * @see de.hsmw.tkretzs1.energetic.collision.GameObject
 * @author Tom Kretzschmar
 */
public class Planet extends GameObject{

    /**
     * Position des Planeten. Üblicherweise (0,0)
     */
    private Vector2f position;

    /**
     * Radius des Planeten und des Kollisionskreises.
     */
    private int radius;

    /**
     * Ring zur Darstellung des Schildes
     */
    private CircleShape shield;

    /**
     * Sprite zur Darstellung des Planeten
     */
    private Sprite sprite;

    /**
     * Energie des Planeten
     */
    private float energy = 1020;

    /**
     * Benötigte Energie pro Sekunde
     */
    private float power = 5;

    /**
     * Konstuktor.
     * Legt neuen Planeten an, läd Ressourcen, setzt Kollisionsbox und Standardwerte.
     */
    public Planet() {
        super(new CircleCollider(new Vector2f(0,0), 140));
        radius = 140;
        position = new Vector2f(0,0);

        // Planetentextur laden
        Texture tex = FileLoader.loadTexture("res\\images\\planet.png");
        sprite = new Sprite(tex);
        sprite.setOrigin(tex.getSize().x/2,tex.getSize().y/2);
        sprite.setPosition(0,0);

        // Schild erzeugen
        shield = new CircleShape(radius, 64);
        shield.setOrigin(radius,radius);
        shield.setPosition(position);
        shield.setFillColor(Color.TRANSPARENT);
        shield.setOutlineThickness(4);
    }

    /**
     * Update-Methode.
     * Aktualisiert die Energie und simuliert den Energieverbrauch.
     *
     * @param dt Zeit seit dem letzten Aufruf (deltaTime)
     */
    @Override
    public void update(Time dt) {
        if(energy > 0) {
            energy -= power*dt.asSeconds();
        } else {
            alive = false;
        }
    }

    /**
     * Behandelt Kollisionsereignis.
     * Methodenaufruf signalisiert eine eingetretene Kollision mit einem anderen Spielobjekt.
     * @param newVelocity neue Geschwindigkeit nach der Kollision.
     * @param other Spielobjekt, mit dem kollidiert wurde.
     */
    @Override
    public void resolveCollision(Vector2f newVelocity, GameObject other) {
        // wenn Asteroid: 30 Energie abziehen
        if(other instanceof Asteroid || other instanceof greenAsteroid) {
            energy -= 30;
            if (energy <= 0){
                energy = 0;
                alive = false;
            }
        }
    }

    /**
     * Gibt Masse des Spielobjektes zurück.
     * Planet - sehr hohe Masse / Bewegt sich nicht bei Kollision.
     * @return Masse
     */
    @Override
    public int getMass() {
        return 100000;
    }

    /**
     * Gibt Dämpungsfaktor des Spielobjektes zurück.
     * @return Dämpfungsfaktor
     */
    @Override
    public float getRestitution() {
        return 1;
    }

    /**
     * Gibt aktuelle Geschwindigkeit des Spielobjektes zurück.
     * @return Geschwindigkeitsvektor
     */
    @Override
    public Vector2f getVelocity() {
        return new Vector2f(0,0);
    }

    /**
     * Gibt aktuelle Position des Spielobjektes zurück.
     * @return aktuelle Position
     */
    @Override
    public Vector2f getPosition() {
        return position;
    }

    /**
     * Zeichnet das Spielobjekt auf das RenderTarget.
     * Darstellung des Planeten mittels der Textur und des Ringes.
     * @param renderTarget Das Renderziel, auf das gezeichnet werden soll.
     * @param renderStates aktuelle RenderStates
     */
    @Override
    public void draw(RenderTarget renderTarget, RenderStates renderStates) {
        // Farbe des Schildes abhängig vom Energiestand setzen
        shield.setOutlineColor(new Color((int)(255- energy/4), (int) energy/4,0));

        renderTarget.draw(sprite);
        renderTarget.draw(shield);
    }

    /**
     * Fügt dem Planeten Energie hinzu.
     * @param e Menge Energie
     */
    public void addEnergy(int e) {
        energy += e;
    }
}
