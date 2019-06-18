package de.hsmw.tkretzs1.energetic.entities;

import de.hsmw.tkretzs1.energetic.collision.GameObject;
import de.hsmw.tkretzs1.energetic.collision.impl.CircleCollider;
import de.hsmw.tkretzs1.energetic.utils.FileLoader;
import org.jsfml.graphics.*;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
 * Repräsentation eines Energiekristalls.
 * Spielobjekt, abgeleitet von GameObject, welches einen Energiekristalls darstellt.
 *
 * @see de.hsmw.tkretzs1.energetic.collision.GameObject
 * @author Tom Kretzschmar
 */
public class EnergyPickup extends GameObject{

    /**
     * Position des Spielobjektes
     */
    private Vector2f position;

    /**
     * Sprite zur Darstellung
     */
    private Sprite sprite;

    /**
     * Konstruktor.
     * Legt neues EnergyPickup an, läd Ressourcen und initialisiert Werte.
     * @param position
     */
    public EnergyPickup(Vector2f position) {
        super(new CircleCollider(position, 20));
        this.position = position;

        // Textur laden
        Texture tex = FileLoader.loadTexture("res\\images\\energy.png");
        sprite = new Sprite(tex);
        sprite.setOrigin(tex.getSize().x/2,tex.getSize().y/2);
        sprite.setPosition(0,0);
    }

    /**
     * Update-Methode.
     * Nicht implementiert, da statisches Objekt.
     *
     * @param dt Zeit seit dem letzten Aufruf (deltaTime)
     */
    @Override
    public void update(Time dt) {}

    /**
     * Behandelt Kollisionsereignis.
     * Methodenaufruf signalisiert eine eingetretene Kollision mit einem anderen Spielobjekt.
     * @param newVelocity neue Geschwindigkeit nach der Kollision.
     * @param other Spielobjekt, mit dem kollidiert wurde.
     */
    @Override
    public void resolveCollision(Vector2f newVelocity, GameObject other) {
        alive = false;
    }

    /**
     * Gibt Masse des Spielobjektes zurück.
     * @return Masse
     */
    @Override
    public int getMass() {
        return 10;
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
     * Die Darstellung des Energiekristalls erfolgt mittels der Textur.
     * @param renderTarget Das Renderziel, auf das gezeichnet werden soll.
     * @param renderStates aktuelle RenderStates
     */
    @Override
    public void draw(RenderTarget renderTarget, RenderStates renderStates) {
        sprite.setOrigin(20,20);
        sprite.setPosition(position);
        renderTarget.draw(sprite);
    }
}
