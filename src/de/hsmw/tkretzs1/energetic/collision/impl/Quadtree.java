package de.hsmw.tkretzs1.energetic.collision.impl;

import de.hsmw.tkretzs1.energetic.collision.CollisionShape;
import de.hsmw.tkretzs1.energetic.collision.GameObject;
import org.jsfml.graphics.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Baum-Datenstruktur für zweidimensionale Elemente.
 *
 * @author Tom Kretzschmar
 */
public class Quadtree{

    private int MAX_OBJECTS = 10;   // Maximale Objekte bevor aufgeteilt wird
    private int MAX_LEVELS = 5;     // Tiefste Knoten-Ebene

    private int level;              // aktuelle Ebene des Knotens
    private ArrayList<GameObject> objects;
    private IntRect bounds;         // verwendeter 2D-Raum
    private Quadtree[] nodes;       // 4 Kindknoten (subnodes)

    /**
     * Konstruktor.
     * Erstellt neuen Quadtree auf der Ebene und mit dem zugewiesenen 2D-Raum
     *
     * @param pLevel Ebene des Quadtree-Knotens
     * @param pBounds überdeckter 2D-Raum
     */
    public Quadtree(int pLevel, IntRect pBounds){
        level = pLevel;
        objects = new ArrayList();
        bounds = pBounds;
        nodes = new Quadtree[4];
    }

    /**
     * Leert den Quadtree
     */
    public void clear() {
        objects.clear();

        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] != null) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    /**
     * Spaltet Knoten in 4 Kindknoten auf
     */
    private void split() {
        int subWidth = (bounds.width / 2);
        int subHeight = (bounds.height / 2);
        int x = bounds.left;
        int y = bounds.top;

        nodes[0] = new Quadtree(level+1, new IntRect(x + subWidth, y, subWidth, subHeight));
        nodes[1] = new Quadtree(level+1, new IntRect(x, y, subWidth, subHeight));
        nodes[2] = new Quadtree(level+1, new IntRect(x, y + subHeight, subWidth, subHeight));
        nodes[3] = new Quadtree(level+1, new IntRect(x + subWidth, y + subHeight, subWidth, subHeight));
    }

    /**
     * Gibt den Index des übergebenen Spielobjektes zurück.
     * Hilfsmethode - Ruft getIndex(CircleCollider circleCollider) auf
     * @param gameObject Spielobjekt
     * @return Index; -1 wenn nicht gefunden.
     */
    private int getIndex(GameObject gameObject){
        CollisionShape shape = gameObject.getCollisionShape();
        int index = -1;
        if( shape instanceof CircleCollider) {
            index = getIndex((CircleCollider) shape);
        }
        return index;
    }

    /**
     * Gibt den Index des übergebenen Colliders zurück.
     * @param circleCollider Kollisionsbox
     * @return Index; -1 wenn nicht gefunden.
     */
    private int getIndex(CircleCollider circleCollider) {
        int index = -1;
        double verticalMidpoint = bounds.left + (bounds.width / 2);
        double horizontalMidpoint = bounds.top + (bounds.height / 2);

        // Objekt passt komplett in den oberen Quadrant
        boolean topQuadrant = (circleCollider.y - circleCollider.radius < horizontalMidpoint && circleCollider.y + circleCollider.radius < horizontalMidpoint);
        // Objekt passt komplett in den unteren Quadrant
        boolean bottomQuadrant = (circleCollider.y - circleCollider.radius > horizontalMidpoint);

        // Objekt passt komplett in die linken Quadranten
        if (circleCollider.x - circleCollider.radius < verticalMidpoint && circleCollider.x + circleCollider.radius < verticalMidpoint) {
            if (topQuadrant) {
                index = 1;
            }
            else if (bottomQuadrant) {
                index = 2;
            }
        }
        // Objekt passt komplett in die rechten Quadranten
        else if (circleCollider.x - circleCollider.radius > verticalMidpoint) {
            if (topQuadrant) {
                index = 0;
            }
            else if (bottomQuadrant) {
                index = 3;
            }
        }
        return index;
    }

    /**
     * Fügt das Spielobjekt in den Quadtree ein.
     * Wenn der Knoten die Kapazität übersteigt, teilt er sich auf und fügt alle Objekte den zugehörigen Knoten hinzu.
     *
     * @param gameObject Spielobjekt
     */
    public void insert(GameObject gameObject) {
        if (nodes[0] != null) {
            int index = getIndex(gameObject);

            if (index != -1) {
                nodes[index].insert(gameObject);

                return;
            }
        }

        objects.add(gameObject);

        // Wenn Kapazität überstiegen wird -> aufsplitten
        if (objects.size() > MAX_OBJECTS && level < MAX_LEVELS) {
            if (nodes[0] == null) {
                split();
            }

            // Alle Objekte in korrespondierende Knoten einfügen
            int i = 0;
            while (i < objects.size()) {
                int index = getIndex(objects.get(i));
                if (index != -1) {
                    if (index != -1) {
                        nodes[index].insert(objects.get(i));
                        objects.remove(i);
                    }
                    else {
                        i++;
                    }
                }
                else {
                    i++;
                }
            }
        }
    }

    /**
     * Gibt alle Objekte zurück, die mit dem übergebenen Objekt kollidieren könnten.
     *
     * @param returnObjects Liste für die zurückgegebenen Objekte
     * @param gameObject  Spielobjekt dessen Umgebung geprüft werden soll.
     * @return Liste der Objekte
     */
    public List retrieve(List returnObjects, GameObject gameObject) {
        int index = getIndex(gameObject);
        if (index != -1 && nodes[0] != null) {
            nodes[index].retrieve(returnObjects, gameObject);
        }

        returnObjects.addAll(objects);
        return returnObjects;
    }
}
