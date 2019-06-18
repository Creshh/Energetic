package de.hsmw.tkretzs1.energetic.entities;

import de.hsmw.tkretzs1.energetic.collision.GameObject;
import de.hsmw.tkretzs1.energetic.collision.impl.CircleCollider;
import de.hsmw.tkretzs1.energetic.core.Game;
import de.hsmw.tkretzs1.energetic.core.level_impl.Level1;
import de.hsmw.tkretzs1.energetic.particles.impl.AsteroidParticleSystem;
import de.hsmw.tkretzs1.energetic.utils.Circle;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.Color;
import org.jsfml.graphics.RenderStates;
import org.jsfml.graphics.RenderTarget;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
 * Repräsentation eines Asteroiden.
 * Spielobjekt, abgeleitet von GameObject, welches einen grünen Asteroiden darstellt. Unterschied zum normalen Asteroiden ist die Farbe und die Fähigkeit bei Zerstörung ein EnergyPickup zu spawnen.
 *
 * TODO: Für zukünftige Version mit Klasse Asteroid zusammenfassen und Unterscheidung über einen boolean und Konstruktor-Flag regeln.
 *
 * @see de.hsmw.tkretzs1.energetic.collision.GameObject
 * @author Tom Kretzschmar
 */
public class greenAsteroid extends GameObject{

    /**
     * Partikelsystem zur Darstellung des Asteroiden.
     */
    private AsteroidParticleSystem particleSystem;

    /**
     * Aktuelle Position des Asteroiden sowie aller zugehörigen Elemente.
     */
    private Vector2f position;

    /**
     * Aktuelle Position des Asteroiden sowie aller zugehörigen Elemente.
     */
    private Vector2f velocity;

    /**
     * Masse des Asteroiden für Kollisionsberechnung.
     */
    private int mass;

    /**
     * Grundfarbe des Partikelsystems.
     */
    private final Color color = new Color(90,170,105);


    /**
     * Konstruktor.
     * Legt neues Asteroid-Objekt mit Kollisionsbox und Partikelsystem an.
     * @param position Startposition des Spielobjekts
     * @param fragments Anzahl der Partikel im Partikelsystem
     * @param velocity Startgeschwindigkeit des Spielobjekts
     */
    public greenAsteroid(Vector2f position, int fragments, Vector2f velocity) {
        super(new CircleCollider(position, 50));    // Position und Radius setzen
        this.mass = 75;
        this.position = position;
        this.velocity = velocity;
        this.particleSystem = new AsteroidParticleSystem(position, fragments, color);
    }

    /**
     * Simuliert die Bewegung des Asteroiden und aktualisiert die Positionen der Kollisionsbox und des Partikelsystems.
     * @param dt Zeit seit dem letzten Aufruf (deltaTime)
     */
    @Override
    public void update(Time dt) {
        Circle worldbounds = Game.getInstance().getWorldBounds();

        // Beschleunigungsvektor, benötigt für die Bewegungsumkehrung wenn außerhalb des Spielfeldes
        Vector2f a = new Vector2f(0,0);

        Vector2f distance = Vector2f.sub(new Vector2f(worldbounds.position), position); // Distanz zum Mittelpunkt des Spielfeldes berechnen
        if(Math2D.getLength(distance) > worldbounds.radius){                            // Wenn außerhalb, dann Beschleunigungswert abhängig von der Entfernung berechnen
            distance = Math2D.normalize(distance);
            a = Vector2f.mul(distance, 100);
        }

        velocity = new Vector2f(a.x * dt.asSeconds() + velocity.x, a.y * dt.asSeconds() + velocity.y);  // Geschwindigkeit berechnen

        Vector2f s = Vector2f.mul(velocity, dt.asSeconds());    // zurückgelegten Weg berechnen
        position = Vector2f.add(position, s);                   // Position berechnen

        // Partikelsystem und Kollisionsbox an neue Position verschieben
        particleSystem.setPosition(s);
        getCollisionShape().updatePosition(position);
    }


    /**
     * Zeichnet das Spielobjekt auf das RenderTarget.
     * Die Darstellung des Asteroiden erfolgt über das Partikelsystem, welches gezeichnet wird.
     * @param target Das Renderziel, auf das gezeichnet werden soll.
     * @param renderStates aktuelle RenderStates
     */
    @Override
    public void draw(RenderTarget target, RenderStates renderStates) {
        target.draw(particleSystem);
    }

    /**
     * Behandelt Kollisionsereignis.
     * Methodenaufruf signalisiert eine eingetretene Kollision mit einem anderen Spielobjekt.
     * @param newVelocity neue Geschwindigkeit nach der Kollision.
     * @param other Spielobjekt, mit dem kollidiert wurde.
     */
    @Override
    public void resolveCollision(Vector2f newVelocity, GameObject other) {
        if(other instanceof Asteroid || other instanceof greenAsteroid){    // Kollision mit Asteroid
            this.velocity = newVelocity;
        } else if(other instanceof Planet){ // Kollision mit Planet
            this.velocity = newVelocity;
        } else if(other instanceof Player){ // Kollision mit Spieler
            this.velocity = newVelocity;
        } else if(other instanceof Projectile){ // Kollision mit Projektil
            alive = false;
            ((Level1) Game.getInstance().getCurrentLevel()).addGameObject(new EnergyPickup(position)); // EnergyPickup spawnen
        }
    }

    /**
     * Gibt aktuelle Position des Spielobjektes zurück.
     * @return aktuelle Position
     */
    public Vector2f getPosition() {
        return position;
    }

    /**
     * Gibt Masse des Spielobjektes zurück.
     * @return Masse
     */
    public int getMass(){
        return mass;
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
     * Gibt aktuelle Geschwindigkeit des Spielobjektes zurück.
     * @return Geschwindigkeitsvektor
     */
    public Vector2f getVelocity(){
        return velocity;
    }
}
