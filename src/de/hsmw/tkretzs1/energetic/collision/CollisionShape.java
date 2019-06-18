package de.hsmw.tkretzs1.energetic.collision;


import de.hsmw.tkretzs1.energetic.collision.impl.CircleCollider;
import org.jsfml.system.Vector2f;

/**
 *  Visitor interface.
 *  Bei weiteren Implementierungen von CollisionShape muss das Interface um weitere Intersect Methoden erweitert werden.
 *
 *  @author Tom Kretzschmar
 */
public interface CollisionShape {
    public boolean IntersectVisit(CollisionShape other);
    public boolean Intersect(CircleCollider circleCollider);

    /**
     * Benötigt um die Position der Kollisionsmaske bei Bewegung des unterliegenden Spielobjektes zu aktualisieren.
     *
     * @param position Die neue Poisition des Elementes.
     */
    public void updatePosition(Vector2f position);
}
