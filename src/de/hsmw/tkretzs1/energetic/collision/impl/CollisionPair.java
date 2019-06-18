package de.hsmw.tkretzs1.energetic.collision.impl;

import de.hsmw.tkretzs1.energetic.collision.GameObject;


/**
 * Repräsentiert ein Tupel von GameObjekten, die durch die Kollisionserkennung als kollidiert erkannt wurden.
 *
 * @author Tom Kretzschmar
 */
public class CollisionPair {

    /**
     * Spielobjekt A
     */
    private GameObject first;

    /**
     * Spielobjekt B
     */
    private GameObject second;

    /**
     * Konstuktor.
     * Legt ein neues Tupel an.
     * @param first
     * @param second
     */
    public CollisionPair(GameObject first, GameObject second){
        this.first = first;
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CollisionPair)) return false;

        CollisionPair that = (CollisionPair) o;

        if(first.equals(that.first) && second.equals(that.second) || first.equals(that.second) && second.equals(that.first)) return true;

        return false;

    }

    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }

    /**
     * Gibt Spielobjekt A zurück.
     * @return GameObject
     */
    public GameObject getFirst() {
        return first;
    }

    /**
     * Gibt Spielobjekt B zurück.
     * @return GameObject
     */
    public GameObject getSecond() {
        return second;
    }
}
