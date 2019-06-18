package de.hsmw.tkretzs1.energetic.utils;

import org.jsfml.graphics.IntRect;
import org.jsfml.system.Vector2i;

/**
 * Repräsentiert einen Kreis.
 * Enthält einen Radius und eine Position sowie eine Methode um die BoundingBox zu berechnen.
 *
 * @see de.hsmw.tkretzs1.energetic.core.Game
 * @author Tom Kretzschmar
 */
public class Circle {

    /**
     * Radius des Kreises
     */
    public int radius;

    /**
     * Position des Mittelpunktes des Kreises
     */
    public Vector2i position;

    /**
     * Konstruktor.
     * Erzeugt neues Kreisobjekt.
     * @param radius Radius
     * @param position Mittelpunkt
     */
    public Circle(int radius, Vector2i position){
        this.radius = radius;
        this.position = position;
    }

    /**
     * Berechne BoundingBox des Kreises.
     * @return Rechteck
     */
    public IntRect getSurroundingRect(){
        return new IntRect(position.x-radius, position.y -radius, 2*radius, 2*radius);
    }
}

