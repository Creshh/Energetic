package de.hsmw.tkretzs1.energetic.collision;

import org.jsfml.graphics.Drawable;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;

/**
 * Basisklasse für alle Spielobjekte, die miteiander interagieren müssen und u.U. physikalisch simuliert werden.
 * Objekte die von dieser Klasse erben können geupdatet und gezeichnet, sowie zur Kollisionserkennung angemeldet werden.
 *
 * @author Tom Kretzschmar
 */
public abstract class GameObject implements Drawable {

    /**
     * Kollisionsumriss welches das Spielobjekt zur Kollisionsberechnung abstrahiert (oft als Kollisionsbox bezeichnet!).
     */
    private CollisionShape collisionShape;

    /**
     * Flag ob Spielobjekt noch benötigt wird.
     */
    protected boolean alive = true;

    /**
     * Konstruktor.
     * Muss von jeder implementierenden Klasse aufgerufen werden, um die Kollisionsbox zu setzen.
     * @param collisionShape
     */
    public GameObject(CollisionShape collisionShape){
        this.collisionShape = collisionShape;
    }

    /**
     * Simuliert alle Vorgänge des Spielobjektes.
     * Implementierte Methode dient dem aktualisieren und berechnen aller veränderbaren Werte des erbenden Spielobjektes.
     * @param dt Zeit seit dem letzten Aufruf (deltaTime)
     */
    public abstract void update(Time dt);

    /**
     * Prüft auf Kollision.
     * Prüft die Überlappung der eigenen Kollisionsbox mit der übergebenen.
     * @param other Kollisionsbox mit der geprüft werden soll.
     * @return Kollision vorhanden
     */
    public boolean intersects(GameObject other){
        return collisionShape.IntersectVisit(other.collisionShape);
    }

    /**
     * Gibt Kollisionsbox zurück.
     * @return Kollisionsbox
     */
    public CollisionShape getCollisionShape() {
        return collisionShape;
    }

    /**
     * Behandelt Kollisionsereignis.
     * Methodenaufruf signalisiert eingetretene Kollision mit einem anderen Spielobjekt.
     * @param newVelocity neue Geschwindigkeit nach der Kollision.
     * @param other Spielobjekt, mit dem kollidiert wurde.
     */
    public abstract void resolveCollision(Vector2f newVelocity, GameObject other);

    /**
     * Gibt zurück, ob Spielobjekt noch lebt.
     * @return Alive
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Gibt Masse des Spielobjektes zurück.
     * @return Masse
     */
    public abstract int getMass();

    /**
     * Gibt Dämpungsfaktor des Spielobjektes zurück.
     * @return Dämpfungsfaktor
     */
    public abstract float getRestitution();

    /**
     * Gibt aktuelle Geschwindigkeit des Spielobjektes zurück.
     * @return Geschwindigkeitsvektor
     */
    public abstract Vector2f getVelocity();

    /**
     * Gibt aktuelle Position des Spielobjektes zurück.
     * @return aktuelle Position
     */
    public abstract Vector2f getPosition();

}
