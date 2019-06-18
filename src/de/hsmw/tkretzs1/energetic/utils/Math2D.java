package de.hsmw.tkretzs1.energetic.utils;

import org.jsfml.system.Vector2f;

import java.util.Random;

/**
 * Klasse mit statischen Funktionen zur Vektorarithmethik und Zufallsberechnung.
 * Math2D bietet Hilfsklassen zur einfachen Rechnung mit Vektoren (Vector2f) und zur ermittlung von Zufallszahlen in bestimmten Bereichen bzw. auf Flächen.
 * @author Tom Kretzschmar
 */
public class Math2D {

    private static Random random;

    /**
     * Initialisiert statische Variablen.
     */
    static{
        random = new Random();
    }

    //region ### Vector Arithmetics ###

    /**
     * Rotiere Vektor um einen bestimmten Winkel.
     * @param vector Ausgangsvektor
     * @param angle Winkel in Grad
     * @return Gedrehter Vektor
     */
    public static Vector2f rotate(Vector2f vector, float angle){
        float rad = (float)java.lang.Math.toRadians(angle);

        float x = vector.x;
        float y = vector.y;
        float temp_x = x;

        x = (float)(x * java.lang.Math.cos(rad) - y * java.lang.Math.sin(rad));
        y = (float)(temp_x * java.lang.Math.sin(rad) + y * java.lang.Math.cos(rad));

        return new Vector2f(x,y);
    }

    /**
     * Normalisiere übergebenen Vektor.
     * @param vector Ausgangsvektor
     * @return normalisierter Vektor.
     */
    public static Vector2f normalize(Vector2f vector) {
        float length = getLength(vector);
        float x = 0;
        float y = 0;
        if (length != 0) {
            x = vector.x/length;
            y = vector.y/length;
        }
        return new Vector2f(x,y);
    }

    /**
     * Ermittle Drehwinkel des Vektors.
     * @param vector Ausgangsvektor
     * @return Winkel in Grad
     */
    public static float getAngle(Vector2f vector) {
        float angle = (float) Math.toDegrees(Math.atan2(vector.y, vector.x));
        if(angle < 0.0f)
            angle += 360.0;
        return angle;
    }

    /**
     * Ermittle Länge (Betrag) des Vektors.
     * @param vector Ausgangsvektor
     * @return Länge des Vektors
     */
    public static float getLength(Vector2f vector){
        return (float) Math.sqrt(vector.x * vector.x + vector.y * vector.y);
    }
    //endregion

    //region ### Random Methods ###

    /**
     * Ermittelt zufälligen Punkt auf einer Scheibe.
     * Zufälliger Punkt liegt innerhalb eines Radius und wird in Form eines Vektor2f zurückgegeben. Der Algorithmus verteilt die Zufallswerte gleichmäßig über die gesamte Fläche.
     * @param radius Radius der Scheibe
     * @return Vektor mit den Koordinaten des Punktes
     */
    public static Vector2f rndPointOnDisc(float radius){
        float phi = (float)(2 * Math.PI * random());
        float r = (float)Math.sqrt(random(radius*radius));
        float x = (float)(r*Math.cos(phi));
        float y = (float)(r*Math.sin(phi));
        return new Vector2f(x,y);
    }

    /**
     * Ermittelt zufälligen Punkt auf einer Scheibe mit Loch.
     * Zufälliger Punkt liegt innerhalb eines Radius outer und außerhalb eines Radius outer und wird in Form eines Vektor2f zurückgegeben. Der Algorithmus verteilt die Zufallswerte gleichmäßig über die gesamte Fläche.
     * @param outer Radius der Scheibe
     * @param inner Radius des Loches
     * @return Vektor mit den Koordinaten des Punktes
     */
    public static Vector2f rndPointOnDonut(float inner, float outer){
        float phi = (float)(2 * Math.PI * random());
        float r = (float)Math.sqrt(randomInRange(inner*inner, outer*outer));
        float x = (float)(r*Math.cos(phi));
        float y = (float)(r*Math.sin(phi));
        return new Vector2f(x,y);
    }

    /**
     * Ermittelt Zufallswert zwischen low und high.
     * @param low Unterer Grenzwert
     * @param high Oberer Grenzwert
     * @return Zufallszahl
     */
    public static float randomInRange(float low, float high){
        return random() * (high-low) + low;
    }

    /**
     * Ermittelt Zufallswert zwischen 0 und high.
     * @param high Oberer Grenzwert
     * @return Zufallszahl
     */
    public static float random(float high){
        return random() * high;
    }

    /**
     * Ermittelt Zufallswert zwischen 0 und 1
     * @return Zufallszahl
     */
    public static float random(){
        return random.nextFloat();
    }

    //endregion

}
