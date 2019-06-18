package de.hsmw.tkretzs1.energetic.collision.impl;

import de.hsmw.tkretzs1.energetic.collision.CollisionShape;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.system.Vector2f;

/**
 * Implementierung des CollisionShapes als Kreis.
 *
 * @author Tom Kretzschmar
 * @see de.hsmw.tkretzs1.energetic.collision.CollisionShape
 */
public class CircleCollider implements CollisionShape {

    /**
     * Radius des Kollisionskreises
     */
    public int radius;

    /**
     * Position - x-Koordinate
     *
     */
    public int x;

    /**
     * Position - y-Koordinate
     */
    public int y;

    /**
     * Konstruktor eines Kollisionskreises.
     *
     * @param position Mittelpunkt und Position des Kreises
     * @param radius Radius des Kreises
     */
    public CircleCollider(Vector2f position, int radius){
        this.x = (int)position.x;
        this.y = (int)position.y;
        this.radius = radius;
        updatePosition(position);
    }

    /**
     * Überprüfung auf Kollision.
     *
     * @param other Zweite CollisionShape mit der auf Kollision geprüft werden soll.
     * @return Kollision stattgefunden oder nicht.
     */
    @Override
    public boolean IntersectVisit(CollisionShape other) {
        return other.Intersect(this);
    }

    /**
     * Konkrete Implementierung der Kollisionsberechnung. Wird zur Laufzeit gebunden.
     *
     * @param circleCollider Zweiter CircleCollider mit dem auf Kollision geprüft werden soll.
     * @return  Kollision stattgefunden oder nicht.
     */
    @Override
    public boolean Intersect(CircleCollider circleCollider) {
        Vector2f A = new Vector2f(x,y);
        Vector2f B = new Vector2f(circleCollider.x, circleCollider.y);
        if(Math2D.getLength(Vector2f.sub(B, A)) <= radius + circleCollider.radius)
            return true;
        else
            return false;
    }

    /**
     * Benötigt um die Position der Kollisionsmaske bei Bewegung des unterliegenden Spielobjektes zu aktualisieren.
     *
     * @param position Die neue Poisition des Elementes.
     */
    @Override
    public void updatePosition(Vector2f position) {
        x = (int)position.x;
        y = (int)position.y;
    }
}
