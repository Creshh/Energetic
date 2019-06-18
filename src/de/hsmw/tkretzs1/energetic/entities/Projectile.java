package de.hsmw.tkretzs1.energetic.entities;

import de.hsmw.tkretzs1.energetic.collision.GameObject;
import de.hsmw.tkretzs1.energetic.collision.impl.CircleCollider;
import de.hsmw.tkretzs1.energetic.core.Game;
import org.jsfml.graphics.*;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
 * Repräsentation eines Asteroiden.
 * Spielobjekt, abgeleitet von GameObject, welches einen Asteroiden darstellt.
 *
 * @see de.hsmw.tkretzs1.energetic.collision.GameObject
 * @author Tom Kretzschmar
 */
public class Projectile extends GameObject{

    /**
     * Position des Projektils
     */
    private Vector2f position;

    /**
     * Bewegungsrichtung des Projektils
     */
    private Vector2f direction;

    /**
     * Geschwindigkeit als Skalar
     */
    private float speed;

    /**
     * Form zur Darstellung des Projektils
     */
    private RectangleShape shape;

    /**
     * Lebenszeit des Projektils
     */
    private Time lifetime;

    /**
     * Konstuktor.
     * Legt neuen Planeten an, setzt Kollisionsbox und Standardwerte.
     * @param position Positionsvektor
     * @param direction Richtungsvektor
     * @param speed Geschwindigkeit (Skalar)
     */
    public Projectile(Vector2f position, Vector2f direction, float speed) {
        super(new CircleCollider(position, 10));
        this.direction = direction;
        this.position = position;
        this.speed = speed;

        // Form erzeugen
        shape = new RectangleShape(new Vector2f(10,10));
        shape.setOrigin(5,5);
        shape.setPosition(position);
        shape.setFillColor(Color.TRANSPARENT);
        shape.setOutlineColor(Color.WHITE);
        shape.setOutlineThickness(1);
        lifetime = Time.getSeconds(2);
    }

    /**
     * Simuliert die Bewegung des Projektils und aktualisiert die Positionen der Kollisionsbox sowie die Lebenszeit.
     *
     * @param dt Zeit seit dem letzten Aufruf (deltaTime)
     */
    @Override
    public void update(Time dt) {
        // Bewegung simulieren
        position = Vector2f.add(position, Vector2f.mul(direction, speed * dt.asSeconds()));

        // Kollisionsbox aktualisieren
        getCollisionShape().updatePosition(position);

        // Lebenszeit simulieren
        lifetime = Time.sub(lifetime, dt);
        if(lifetime.asSeconds() < 0){
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
        // wenn Asteroid: Sound abspielen, Explosion hinzufügen, Asteroid entfernen und Score hinzufügen
        if(other instanceof Asteroid || other instanceof greenAsteroid){
            Game.getInstance().playSound(Game.Soundtype.impact );
            Game.getInstance().getCurrentLevel().addExplosion(this.position);
            Game.getInstance().addScore(5);
            alive = false;
        } else if(other instanceof Projectile){
            alive = false;
        }
    }

    /**
     * Gibt Masse des Spielobjektes zurück.
     * @return Masse
     */
    @Override
    public int getMass() {
        return 5;
    }

    /**
     * Gibt Dämpungsfaktor des Spielobjektes zurück.
     * @return Dämpfungsfaktor
     */
    @Override
    public float getRestitution(){
        return 0.8f;
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
     * Gibt aktuelle Geschwindigkeit des Spielobjektes zurück.
     * @return Geschwindigkeitsvektor
     */
    @Override
    public Vector2f getVelocity() {
        return Vector2f.mul(direction, speed);
    }

    /**
     * Zeichnet das Spielobjekt auf das RenderTarget.
     * @param renderTarget Das Renderziel, auf das gezeichnet werden soll.
     * @param renderStates aktuelle RenderStates
     */
    @Override
    public void draw(RenderTarget renderTarget, RenderStates renderStates) {
        shape.setPosition(position);
        renderTarget.draw(shape);
    }
}
