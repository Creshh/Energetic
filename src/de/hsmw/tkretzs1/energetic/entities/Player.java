package de.hsmw.tkretzs1.energetic.entities;

import de.hsmw.tkretzs1.energetic.collision.GameObject;
import de.hsmw.tkretzs1.energetic.collision.impl.CircleCollider;
import de.hsmw.tkretzs1.energetic.core.Game;
import de.hsmw.tkretzs1.energetic.particles.impl.EngineParticleSystem;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.*;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.window.Keyboard;

import java.util.ArrayList;

/**
 * Spielerobjekt.
 * Repräsentiert das vom Spieler gesteuerte Raumschiff.
 *
 * @author Tom Kretzschmar
 * @see de.hsmw.tkretzs1.energetic.collision.GameObject
 */
public class Player extends GameObject{

    /**
     * Beschleunigung nach Vorn. (Forward)
     * Maßeinheit in px/s^2
     */
    private final float ACC_F = 1000f;

    /**
     * Beschleunigung nach Hinten. (Backward)
     * Maßeinheit in px/s^2
     */
    private final float ACC_B = -700f;

    /**
     * Beschleunigung nach Links. (Left)
     * Maßeinheit in px/s^2
     */
    private final float ACC_L = -700f;

    /**
     * Beschleunigung nach Rechts. (Right)
     * Maßeinheit in px/s^2
     */
    private final float ACC_R = 700f;

    /**
     * Maximale Geschwindigkeit des Raumschiffs als Skalar.
     * Maßeinheit in px/s
     */
    private final float MAXSPEED = 700f;

    /**
     * Trägheitsdämpfer an/aus
     * Standarfwert: an
     */
    private boolean damping = true;

    /**
     * aktueller Beschleunigungsvektor im lokalen Koordinatensystem des Raumschiffs
     */
    private Vector2f acceleration;

    /**
     * aktueller Geschwindigkeitsvektor im lokalen Koordinatensystem des Raumschiffs
     */
    private Vector2f velocity;

    /**
     * aktueller Geschwindigkeitsvektor im globalen (Welt-)Koordinatensystem
     */
    private Vector2f velocity_world;

    /**
     * aktuelle Position des Raumschiffs im globalen Koordinatensystem
     */
    private Vector2f position;

    /**
     * Darstellung (Shape) des Raumschiffs
     */
    private ConvexShape shape;

    /**
     * Liste von Shapes(Kreisen), zur Darstellung der Pickups (Energiekristalle)
     */
    private ArrayList<CircleShape> pickupCircles;

    /**
     * Darstellung (Shape) des Schildes um das Raumschiff
     */
    private CircleShape shield;

    /**
     * Partikelsystem zur Darstellung des Triebwerkes
     */
    private EngineParticleSystem particleSystem;

    /**
     * aktueller Energiestand des Raumschiffs
     * Startwert: 255;
     */
    private float energy = 255;

    /**
     * aktuelle Anzahl aufgenommener Pickups
     */
    private int energyPickupCount = 0;

    /**
     * maximale Anzahl aufnehmbarer Pickups
     */
    private final int maxPickups = 6;

    /**
     * Masse des Raumschiffs
     * @see de.hsmw.tkretzs1.energetic.core.Game
     */
    private final int mass = 5;

    /**
     * Elastizitätsfaktor des Raumschiffs
     * @see de.hsmw.tkretzs1.energetic.core.Game
     */
    private final float restitution = 0.8f;

    /**
     * Konstruktor.
     * Erzeugt neues Spielerobjekt respektive Raumschiff und setzt Standardwerte.
     */
    public Player(){
        super(new CircleCollider(new Vector2f(0,0),30));

        // Partikelsystem für Triebwerk anlegen
        particleSystem = new EngineParticleSystem(10000, 3.5f, 100.0f, Time.getSeconds(0.1f), 500, new Color(10,80,160));

        position = new Vector2f(0,0);
        acceleration = new Vector2f(0,0);
        velocity = new Vector2f(0,0);
        velocity_world = new Vector2f(0,0);

        createPlayerShape();
    }

    /**
     * Hilfsmethode zur Erstellung der Shapes zur Darstellung des Raumschiffs und dessen Komponenten.
     */
    private void createPlayerShape(){
        // Raumschiff
        shape = new ConvexShape();
        shape.setPointCount(3);
        shape.setPoint(0, new Vector2f(25, 0));
        shape.setPoint(1, new Vector2f(-15, 20));
        shape.setPoint(2, new Vector2f(-15, -20));
        shape.setOrigin(0,0);
        shape.setOutlineThickness(1);
        shape.setFillColor(Color.GREEN);
        shape.setOutlineColor(Color.WHITE);

        // Kollisionsbox anlegen
        CircleCollider circleCollider = ((CircleCollider)getCollisionShape());
        shield = new CircleShape(circleCollider.radius, 64);
        shield.setOrigin(circleCollider.radius, circleCollider.radius);
        shield.setFillColor(Color.TRANSPARENT);
        shield.setOutlineColor(new Color(0,220,255,100));
        shield.setOutlineThickness(2);

        // Kreise für Darstellung der Pickups
        pickupCircles = new ArrayList<>();
        for (int i = 0; i < maxPickups; i++) {
            CircleShape temp = new CircleShape(2);
            temp.setOrigin(2,2);
            temp.setFillColor(Color.RED);
            pickupCircles.add(temp);
        }
    }

    /**
     * Update-Methode. Simuliert die Bewegung und aktualisiert alle Werte.
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     * @see de.hsmw.tkretzs1.energetic.collision.GameObject
     */
    @Override
    public void update(Time dt){

        Vector2f mousePos = Game.getInstance().getMousePos();   // aktuelle Mausposition(globale Koordinaten) abfragen
        Vector2f direction = Vector2f.sub(mousePos, position);  // Abstands/Richtungsvektor zwischen Mausposition und Spielerposition berechnen
        direction = Math2D.normalize(direction);                // Richtungsvektor normalisieren

        float angle = Math2D.getAngle(direction);               // Winkel des Vektors in Bezug auf x-Achse bestimmen (Achtung! y-Achse ist nach unten gerichtet - Winkel im Uhrzeigersinn gemessen)
        velocity = Math2D.rotate(velocity_world, -angle);       // aktuellen Geschwindigkeitsvektor in lokales Koordinatensystem transformieren (drehen)

        float ax = 0.0f;
        float ay = 0.0f;

        // Eingaben abfragen - x-Richtung (lokal!)
        if (Keyboard.isKeyPressed(Keyboard.Key.W)) {            // wenn Spieler w drückt
            ax = ACC_F;                                             // Beschleunigung nach vorn setzen
        } else if (Keyboard.isKeyPressed(Keyboard.Key.S)) {     // wenn Spieler s drückt
            ax = ACC_B;                                             // Beschleunigung nach hinten setzen
        } else if (damping) {                                   // wenn Spieler nichts drückt und Trägheitsdämpfer an ist
            if (velocity.x > 5) {                                   // Bewegung vorwärts vorhanden und größer einem Schwellenwert (low pass)
                ax = ACC_B;                                             // setze Beschleunigung in Gegenrichtung
            } else if (velocity.x < -5) {                           // Bewegung rückwärts vorhanden und kleiner einem Schwellenwert (low pass)
                ax = ACC_F;                                             // setze Beschleunigung in Gegenrichtung
            } else {
                velocity = new Vector2f(0.0f, velocity.y);              // low pass X - vermeidet, dass die Geschwindigkeit unendlich klein wird
            }
        }

        // Eingaben abfragen - x-Richtung (lokal!)
        if (Keyboard.isKeyPressed(Keyboard.Key.D)) {           // siehe oben für Rechts/Links
            ay = ACC_L;
        } else if (Keyboard.isKeyPressed(Keyboard.Key.A)) {
            ay = ACC_R;
        } else if (damping) {
            if (velocity.y > 5) {
                ay = ACC_R;
            } else if (velocity.y < -5) {
                ay = ACC_L;
            } else {
                velocity = new Vector2f(velocity.x, 0.0f);              // low pass Y - vermeidet, dass die Geschwindigkeit unendlich klein wird
            }
        }

        acceleration = new Vector2f(ax, ay);    // Beschleunigungsvektor aus den Komponenten bilden
        velocity = new Vector2f(acceleration.x * dt.asSeconds() + velocity.x, -acceleration.y * dt.asSeconds() + velocity.y);   // lokale Geschwindigkeit berechnen: v = a * dt + v0

        // Geschwindigkeitsbegrenzung
        float speed = Math2D.getLength(velocity);   // Geschwindigkeit als Skalar berechnen (Länge des Vektors)
        if (speed > MAXSPEED) {                     // wenn maximale Geschwindigkeit überschritten
            velocity = Math2D.normalize(velocity);          // Geschwindigkeitsvektor normalisieren und
            velocity = Vector2f.mul(velocity, MAXSPEED);    // mit maximaler Geschwindigkeit(Skalar) multiplizieren
        }

        velocity_world = Math2D.rotate(velocity, angle);     // lokalen Geschwindigkeitsvektor in globales Koordinatensystem zurück transformieren (drehen)

        Vector2f s = Vector2f.mul(velocity_world, dt.asSeconds());  // zurückgelegten Weg berechnen: s = v * dt (Beschleunigte Bewegung kann hier als gleichförmige Bewegung betrachtet werden da dt sehr klein)
        position = Vector2f.add(position, s);                       // Weg zur Position addieren - neue Position berechnen


        shape.setRotation(angle);       // Raumschiff in Bewegungsrichtung drehen
        shape.setPosition(position);    // Raumschiff an neue Position setzen


        // Energieverbrauch berechnen
        float power = Math2D.getLength(acceleration) * 0.002f + 0.3f;
        if(energy > 0) {
            energy -= power*dt.asSeconds();
        } else {
            alive = false;  // wenn Energie unter 0 - GameOver (wird automatisch aufgerufen sobald alive=false
        }

        // Position der Pickup-Shapes aktualisieren
        for (int i = 0; i < maxPickups; i++) {
            pickupCircles.get(i).setPosition(position.x + (i * 6) - maxPickups * 5 / 2, position.y + 40);
        }

        // Position des Triebwerks-Partikelsystem aktualisieren
        particleSystem.setPosition(Vector2f.sub(position, Vector2f.mul(direction, 10)));
        particleSystem.setAngle(angle+180);
        particleSystem.setVelocity(acceleration.x > 0 ? EngineParticleSystem.VELOCITY_BOOST : EngineParticleSystem.VELOCITY_IDLE);  // je nachdem ob der Spieler beschleunigt oder nicht, Geschwindigkeit des Partikelsystems setzen
        particleSystem.update(dt);  // Partikelsystem updaten

        getCollisionShape().updatePosition(position);   // Kollisionsmodell an neue Position setzen
    }

    /**
     * Behandelt Kollisionsereignis.
     * Methodenaufruf signalisiert eine eingetretene Kollision mit einem anderen Spielobjekt.
     * @param newVelocity neue Geschwindigkeit nach der Kollision.
     * @param other Spielobjekt, mit dem kollidiert wurde.
     */
    @Override
    public void resolveCollision(Vector2f newVelocity, GameObject other) {
        // wenn Asteroid: 30 Energie abziehen
        if(other instanceof Asteroid || other instanceof greenAsteroid){
            this.velocity_world = newVelocity;
            energy -= 30;
            if (energy <= 0){
                energy = 0;
                alive = false;
            }
        // wenn EnergyPickup: zur Anzahl Pickups hinzufügen, wenn nicht voll
        } else if(other instanceof EnergyPickup){
            if(energyPickupCount < maxPickups){
                energyPickupCount++;
            }
        // wenn Planet, Pickups auf diesen übertragen
        } else if(other instanceof Planet){
            ((Planet)other).addEnergy(energyPickupCount*100);
            energyPickupCount = 0;

            int max = 255 - (int)energy;
            energy += max;
            ((Planet)other).addEnergy(-max);
        }
    }

    /**
     * Gibt Masse des Spielobjektes zurück.
     * @return Masse
     */
    @Override
    public int getMass() {
        return mass;
    }

    /**
     * Gibt Dämpungsfaktor des Spielobjektes zurück.
     * @return Dämpfungsfaktor
     */
    @Override
    public float getRestitution(){
        return restitution;
    }

    /**
     * Gibt aktuelle Geschwindigkeit des Spielobjektes zurück.
     * @return Geschwindigkeitsvektor
     */
    @Override
    public Vector2f getVelocity() {
        return velocity_world;
    }

    /**
     * Zeichnet das Spielobjekt auf das RenderTarget.
     * Die Darstellung des Raumschiffs erfolgt mittels der Form und dem Partikelsystem des Triebwerks.
     * @param renderTarget Das Renderziel, auf das gezeichnet werden soll.
     * @param renderStates aktuelle RenderStates
     */
    @Override
    public void draw(RenderTarget renderTarget, RenderStates renderStates) {
        shield.setPosition(position);

        // Farbe des Raumschiffs abhängig vom Energiestand setzen
        shape.setFillColor(new Color((int)(255- energy), (int) energy,0));

        renderTarget.draw(particleSystem);
        renderTarget.draw(shape);
        renderTarget.draw(shield);

        // Anzeige der Aufgesammelten Pickups
        for (int i = 0; i < maxPickups; i++) {
            CircleShape temp = pickupCircles.get(i);
            if(i<energyPickupCount)
                temp.setFillColor(Color.GREEN);
            else
                temp.setFillColor(Color.RED);

            renderTarget.draw(temp);
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
     * Schaltet den Trägheitsdämpfer um
     */
    public void changeDamping(){
        damping = !damping;
    }

    /**
     * Fügt dem Spielerobjekt Energie hinzu.
     * @param e Menge Energie
     */
    public void addEnergy(int e){
        energy += e;
    }
}
