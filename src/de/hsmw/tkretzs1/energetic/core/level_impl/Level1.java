package de.hsmw.tkretzs1.energetic.core.level_impl;

import de.hsmw.tkretzs1.energetic.collision.GameObject;
import de.hsmw.tkretzs1.energetic.core.Game;
import de.hsmw.tkretzs1.energetic.core.Level;
import de.hsmw.tkretzs1.energetic.entities.*;
import de.hsmw.tkretzs1.energetic.particles.impl.BackgroundParticleSystem;
import de.hsmw.tkretzs1.energetic.particles.impl.ExplosionParticleSystem;
import de.hsmw.tkretzs1.energetic.utils.Circle;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.Keyboard;
import org.jsfml.window.Mouse;
import org.jsfml.window.event.Event;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Das erste Level des Spiels.
 * Implementiert die Spiellogik und enthält alle Spielobjekte, die in diesem Level benötigt werden, steuert diese und zeichnet sie.
 *
 * @author Tom Kretzschmar
 */
public class Level1 implements Level {

    /**
     * GameObject Spieler - stellt das Raumschiff dar
     */
    private Player player;

    /**
     * GameObject Planet
     */
    private Planet planet;

    /**
     * Partikelsystem für den Hintergrund (Sterne)
     */
    private BackgroundParticleSystem bgParticles;

    /**
     * Liste für Partikelsysteme der Explosionen
     */
    private List<ExplosionParticleSystem> explosions;

    /**
     * Liste aller weiteren Spielobjekte wie Asteroiden, Projektile und Pickups
     */
    private List<GameObject> objects;

    /**
     * Counter zum Spawnen neuer Asteroiden
     */
    private Clock clock;

    /**
     * gesamte Spielzeit im Level
     */
    private Time gameTime;

    /**
     * Konstruktor.
     * Legt neues Level an und initialisiert alle Spielobjekte.
     * @param worldbounds Begrenzung des Spielfeldes
     */
    public Level1(Circle worldbounds){
        objects = new ArrayList<>();
        player = new Player();
        planet = new Planet();
        explosions = new ArrayList<>();

        bgParticles = new BackgroundParticleSystem(5000);
        bgParticles.fill();

        // Asteroiden anlegen und auf dem Spielfeld verteilen, sowie Initialgeschwindigkeit festlegen.
        for (int i = 0; i < 80; i++) {
            objects.add(new Asteroid(Math2D.rndPointOnDonut(worldbounds.radius*0.25f, worldbounds.radius), 75, new Vector2f(Math2D.randomInRange(-200,200), Math2D.randomInRange(-200,200))));
        }
        for (int i = 0; i < 10; i++) {
            objects.add(new greenAsteroid(Math2D.rndPointOnDonut(worldbounds.radius*0.25f, worldbounds.radius), 75, new Vector2f(Math2D.randomInRange(-200,200), Math2D.randomInRange(-200,200))));
        }

        // Planet und Player zu Objekten hinzufügen zwecks einfacher Kollisionserkennung und Update-Möglichkeit
        objects.add(planet);
        objects.add(player);

        clock = new Clock();
        gameTime = Time.ZERO; // Spielzeit initialisieren
    }

    /**
     * Update-Methode.
     * Zum Erzeugen von Asteroiden in Zeitintervallen und zum Updaten aller registrierter Spielobjekte.
     *
     * @param dt DeltaTime - Zeit seit letztem Aufruf
     * @param events von Game übergebene Events
     */
    @Override
    public void update(Time dt, ArrayList<Event> events){
        // Score und Spielzeit kontinuierlich inkrementieren
        Game.getInstance().addScore(dt.asSeconds());
        gameTime = Time.add(gameTime, dt);

        // In 2-sekündigen Intervallen neuen Asteroiden spawnen.
        if(clock.getElapsedTime().asSeconds()>2){
            clock.restart(); // Counter zurücksetzen

            // Zufällige Position auf dem Spielfeld ermitteln
            Vector2f rndPoint = Math2D.rndPointOnDonut(Game.getInstance().getWorldBounds().radius*0.25f, Game.getInstance().getWorldBounds().radius);

            // Falls zufällig erzeugter Punkt im Sichtbereich des Spielers versuchen einen Punkt zu finden der außerhalb liegt. Nach 1ß Iterationen zwecks Performance abbrechen
            int i = 0;
            while(Game.getInstance().getViewBounds().contains(new Vector2i(rndPoint)) && i < 10){
                rndPoint = Math2D.rndPointOnDonut(Game.getInstance().getWorldBounds().radius*0.25f, Game.getInstance().getWorldBounds().radius);
                i++;
            }

            // Zufällige Geschwindigkeit festlegen. Nach 30 sek Spielzeit ist Anfangsgeschwindigkeit höher
            Vector2f velocity;
            if(gameTime.asSeconds()<30)
                velocity = new Vector2f(Math2D.randomInRange(-200,200), Math2D.randomInRange(-200,200));
            else
                velocity = new Vector2f(Math2D.randomInRange(-400,400), Math2D.randomInRange(-400,400));

            // 30% grüne Asteroiden, 70% normale Asteroiden
            if(Math2D.random() < 0.7)
                objects.add(new Asteroid(rndPoint, 75, velocity));
            else
                objects.add(new greenAsteroid(rndPoint, 75, velocity));

        }

        // Events behandeln
        handleEvents(events);

        // falls Spielerobjekt oder Planet nicht mehr leben (zu wenig Energie haben) - GameOver Event auslösen
        if(!player.isAlive() || !planet.isAlive()){
            Game.getInstance().gameOver();
        }

        // alle Objekte updaten und falls tot aus der Liste entfernen
        Iterator<GameObject> it_o = objects.iterator();
        GameObject currentObj;
        while(it_o.hasNext()){
            currentObj = it_o.next();
            if(currentObj.isAlive())
                currentObj.update(dt);
            else
                it_o.remove();
        }

        // Hintergrundpartikel aktualisieren (benötigen Spielerposition)
        bgParticles.setPlayerPosition(player.getPosition());
        bgParticles.update(dt);

        // alle Explosionen updaten und falls tot aus der Liste entfernen
        Iterator<ExplosionParticleSystem> it_e = explosions.iterator();
        ExplosionParticleSystem explosion;
        while(it_e.hasNext()){
            explosion = it_e.next();
            if(explosion.isAlive())
                explosion.update(dt);
            else
                it_e.remove();
        }


        // Kollisionserkennung starten
        Game.getInstance().detectCollisions(objects);
    }

    @Override
    public Vector2f getCameraPos() {
        return player.getPosition();
    }

    /**
     * Zeichnet das Level auf das RenderTarget.
     * Stellt alle Elemente des Levels in Abhängigkeit der implementierten Logik dar.
     * @param target Das Renderziel, auf das gezeichnet werden soll.
     * @param renderStates aktuelle RenderStates
     */
    @Override
    public void draw(RenderTarget target, RenderStates renderStates) {
        target.draw(bgParticles);
        for (GameObject object : objects) {
            target.draw(object);
        }
        for (ExplosionParticleSystem explosion : explosions) {
            target.draw(explosion);
        }
    }

    /**
     * Hilfsmethode zum behandeln der übergebenen Events.
     * Verarbeitet Nutzereingaben zum Schießen, zum Umschalten des Trägheitsdämpfers sowie zum abbrechen des Levels.
     * @param events übergebene Events.
     */
    private void handleEvents(ArrayList<Event> events){
        for (Event event : events) {

            // wenn linke Maustaste gedrückt - neues Projektil erzeugen
            if(event.type == Event.Type.MOUSE_BUTTON_PRESSED && event.asMouseButtonEvent().button == Mouse.Button.LEFT){
                objects.add(new Projectile(player.getPosition(), Math2D.normalize(Vector2f.sub(Game.getInstance().getMousePos(), player.getPosition())), 1000));
                player.addEnergy(-10);
                Game.getInstance().playSound(Game.Soundtype.gunshot);
            }

            // Trägheitsdämpfer umschalten
            if (event.type == Event.Type.KEY_PRESSED && event.asKeyEvent().key == Keyboard.Key.LSHIFT) {
                player.changeDamping();
            }

            // Spiel verlassen
            if (event.type == Event.Type.KEY_PRESSED && event.asKeyEvent().key == Keyboard.Key.ESCAPE) {
                Game.getInstance().startLevel(0);
            }
        }

    }

    /**
     * Fügt Explosions-Partikelsystem hinzu, wenn Asteroid abgeschossen wird.
     * @param position Position der Explosion
     */
    public void addExplosion(Vector2f position){
        explosions.add(new ExplosionParticleSystem(position, Time.getSeconds(1)));
    }

    /**
     * Fügt dem Level neues Spielobjekt hinzu.
     * Benötigt um durch Asteroiden EnergyPickups zu erzeugen.
     * @param object Spielobjekt, dass hinzugefügt werden soll
     */
    public void addGameObject(GameObject object){
        objects.add(object);
    }
}
