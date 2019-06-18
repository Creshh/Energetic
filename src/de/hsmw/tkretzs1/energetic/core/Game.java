package de.hsmw.tkretzs1.energetic.core;

import de.hsmw.tkretzs1.energetic.collision.GameObject;
import de.hsmw.tkretzs1.energetic.collision.impl.CollisionPair;
import de.hsmw.tkretzs1.energetic.collision.impl.Quadtree;
import de.hsmw.tkretzs1.energetic.core.level_impl.GameOver;
import de.hsmw.tkretzs1.energetic.core.level_impl.Level1;
import de.hsmw.tkretzs1.energetic.core.level_impl.Menu;
import de.hsmw.tkretzs1.energetic.utils.Circle;
import de.hsmw.tkretzs1.energetic.utils.FileLoader;
import de.hsmw.tkretzs1.energetic.utils.Math2D;
import org.jsfml.audio.Music;
import org.jsfml.audio.Sound;
import org.jsfml.graphics.*;
import org.jsfml.system.Clock;
import org.jsfml.system.Time;
import org.jsfml.system.Vector2f;
import org.jsfml.system.Vector2i;
import org.jsfml.window.*;
import org.jsfml.window.event.Event;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Steuerungseinheit des gesamten Spiels und Kommunikationskanal zwischen allen Spielobjekten.
 * Die Klasse Game ist verantwortlich für das Starten und Laden von Leveln und Menüs, sowie für die Ausführung von Kollisionserkennung und Kollisionsauflösung.
 * Ebenso Kommunizieren die Spielobjekte über diese Klasse.
 * Game ist als Singleton implementiert, damit über das gesamte Projekt hinweg eine Referenz / Instanz auf die Klasse Game angefordert werden kann um auf Methoden zur Kommunikation zuzugreifen.
 * Ebenso ist dadurch gewährleistet, dass es nur eine einzige Instanz dieser Klasse gibt.
 * Implementiert ist außerdem der Mainloop und die Steuerung desselben. Alle Update und Zeichenmethoden werden ausgehend von dieser Klasse aufgerufen.
 *
 * @author Tom Kretzschmar
 */
public class Game{

    // SINGLETON ##############
    /**
     * Selbstreferenz für Singletonimplementierung
     */
    private static Game game = null;

    /**
     * Referenz auf die Instanz der Klasse Game holen.
     * @return Referenz auf Sinleton
     */
    public static Game getInstance() {

        // wenn noch keine Instanz vorhanden - neu anlegen
        if(game == null) {
            Game.game = new Game();
        }
        return game;
    }
    //########################

    /**
     * Viewport des Spieles
     */
    private View view;

    /**
     * Programmfenster, auf dem alle Ausgaben erfolgen
     */
    private RenderWindow window;

    /**
     * Clock zum messen der DeltaTime - Zeit zwischen Aufrufen der Update-Funktion
     */
    private Clock clock;

    /**
     * Flag, gibt an ob Mainloop läuft.
     */
    private boolean running = true;

    /**
     * Flag, gibt an ob Vertical Syncronization aktiviert ist.
     */
    private boolean vsync = true;

    /**
     * Flag, gibt an ob Spiel pausiert ist.
     */
    private boolean paused = false;

    /**
     * Flag, gibt an ob die Grafikkarte Shader unterstützt.
     */
    private boolean shaderAvailable = false;

    /**
     * Flag, gibt an ob Hilfefenster aktuell gerendert werden muss.
     */
    private boolean help = false;

    /**
     * Darstellung des Mauszeigers
     */
    private Sprite mouseSprite;

    /**
     * aktuell geladenes und aktives Level / Menu
     */
    private Level currentLevel;

    /**
     * Weltbegrenzung
     */
    private Circle worldBounds;

    /**
     * Quadtree zur Kollisionsvorbehandlung
     */
    private Quadtree quad;

    /**
     * Hintergrundmusik
     */
    private Music bgMusic;

    /**
     * Sound - Abschießen eines Projektils
     */
    private Sound gunShot;

    /**
     * Sound - Einschlag eines Projektils
     */
    private Sound impact;

    /**
     * Enumeration, welche Sounds vorhanden sind und abgespielt werden können.
     */
    public enum Soundtype{
        gunshot, impact
    }

    /**
     * aktueller Highscore
     */
    private float score = 0;

    /**
     * Array der verfügbaren Shader
     */
    private Shader shader[] = new Shader[3];

    /**
     * Nummer des aktuell zu verwendenden Shaders
     */
    private int currentShader;

    /**
     * Rendertextur, auf die alles gerendert wird und infolge dessen der Shader angewand wird.
     */
    private RenderTexture rtex;

    /**
     * Aus der Rendertextur resultierender Sprite
     */
    private Sprite resultSprite;

    /**
     * Hilfefenster
     */
    private Sprite helpSprite;

    /**
     * Sprite der bei pausiertem Spiel gerendert wird.
     */
    private Sprite pauseSprite;

    /**
     * Privater Konstuktor.
     * Erzeugt Instanz der Klasse Game.
     * Legt das Programmfenster an, läd alle nötigen Ressourcen und startet Musik und Clock.
     */
    private Game(){

        // native Bildschirmauflösung des primären Fensters abfragen
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        // Videomode- bzw. Fenstereinstellungen
        ContextSettings settings = new ContextSettings(8);
        window = new RenderWindow(new VideoMode(width, height), "energetic", WindowStyle.FULLSCREEN, settings);

        // Standardmauscursor ausblenden
        window.setMouseCursorVisible(false);

        // VSync auf Standardwert setzen
        window.setVerticalSyncEnabled(vsync);

        // View anlegen
        view = new View();
        view.setSize(window.getSize().x, window.getSize().y);
        view.setCenter(0, 0);

        // Rendertextur anlegen
        rtex = new RenderTexture();
        try {
            rtex.create(window.getSize().x, window.getSize().y);
        } catch(TextureCreationException ex) {
            ex.printStackTrace();
            return;
        }
        rtex.setView(view);
        // Sprite mit Rendertextur anlegen
        resultSprite = new Sprite(rtex.getTexture());

        // Abfragen ob Grafikkarte Shader darstellen kann
        shaderAvailable = Shader.isAvailable();

        // wenn Shader verfügbar, diese aus den Ressourcen laden
        if(shaderAvailable) {
            // Shader erzeugen und aus Datei Laden
            shader[1] = new Shader();
            shader[2] = new Shader();
            try {
                shader[1].loadFromFile(new File("res\\shaders\\blur.frag").toPath(), Shader.Type.FRAGMENT);
                shader[2].loadFromFile(new File("res\\shaders\\pixelate.frag").toPath(), Shader.Type.FRAGMENT);
                shader[2].setParameter("pixel_threshold", 0.0005f);
            } catch (IOException | ShaderSourceException ex) {
                ex.printStackTrace();
                return;
            }
        } else {
            System.err.println("Shader not available on current System!");
        }

        // alternativen Mauscursor laden
        mouseSprite = new Sprite(FileLoader.loadTexture("res\\images\\crosshair.png"));
        mouseSprite.setOrigin(16, 16);

        // als erstes das Hauptmenü anzeigen
        currentLevel = new Menu();

        // Clock anlegen
        clock = new Clock();

        // Musikressourcen laden
        bgMusic = FileLoader.openMusic();
        bgMusic.setLoop(true);
        gunShot = FileLoader.loadSound("res\\sound\\gun.wav");
        impact = FileLoader.loadSound("res\\sound\\impact.wav");

        // Hilfesprite und Pausesprite laden
        Texture tex = FileLoader.loadTexture("res\\images\\help.png");
        helpSprite = new Sprite(tex);
        helpSprite.setPosition((window.getSize().x-tex.getSize().x)/2, (window.getSize().y-tex.getSize().y)/2);

        Texture tex2= FileLoader.loadTexture("res\\images\\paused.png");
        pauseSprite = new Sprite(tex2);
        pauseSprite.setPosition(window.getSize().x-tex2.getSize().x, tex2.getSize().y);
    }

    /**
     * Startet den Mainloop und damit die Simulation sowie das Rendern.
     */
    public void start(){
        bgMusic.play();
        while(running){
            update();
            draw();
        }
    }

    /**
     * Spiel bzw. Programm schließen
     */
    public void exitGame(){
        running = false;
        bgMusic.stop();
        window.close();
    }

    /**
     * Update-Methode.
     * Holt und Behandelt Eingabeevents bzw. leitet diese an aktuelles Level weiter.
     * Berechnet die Zeit seit dem letzten Updaten und ruft aktuelles Level zum Updaten auf.
     */
    public void update(){
        // Events vom Window holen
        Event event;
        ArrayList<Event> events= new ArrayList<>();
        while ((event = window.pollEvent()) != null) {
            handleInputEvents(event);
            events.add(event);
        }

        // DeltaTime holen
        Time dt = clock.restart();

        // Mauszeiger an aktuelle Mausposition verschieben (Welt - Bildschirmkoordinaten)
        mouseSprite.setPosition(window.mapPixelToCoords(Mouse.getPosition(window), view));

        // Wenn nicht pausiert, Level updaten
        if(!paused) {
            currentLevel.update(dt, events);
        }
    }

    // Events behandeln
    private void handleInputEvents(Event event){
        if (event.type == Event.Type.CLOSED) {  // Wenn Fenster geschlossen, Spiel abrrechen und beenden
            game.running = false;
            window.close();
        }

        if (event.type == Event.Type.RESIZED) { // Wenn Größe des Fensters verändert wird, View anpassen (bei Vollbild nicht benötigt=
            FloatRect visibleArea = new FloatRect(0, 0, event.asSizeEvent().size.x, event.asSizeEvent().size.y);
            view = new View(visibleArea);
            window.setView(view);
        }

        if (event.type == Event.Type.KEY_PRESSED) {
            if (event.asKeyEvent().key == Keyboard.Key.V){ // VSync Umschalten
                vsync = !vsync;
                window.setVerticalSyncEnabled(vsync);
            }
            if (event.asKeyEvent().key == Keyboard.Key.P){ // Pause umschalten wenn nicht in einem Menübildschirm
                if(!(currentLevel instanceof Menu || currentLevel instanceof GameOver))
                    paused = !paused;
            }
            if (event.asKeyEvent().key == Keyboard.Key.C){ // Shader umschalten: Shader 1, Shader 2, kein Shader
                if(currentShader < 2 )
                    currentShader ++;
                else
                    currentShader = 0;
            }
            if(event.asKeyEvent().key == Keyboard.Key.F1){ // Hilfe anzeigen
                help = true;
            }
        }

        if(event.type == Event.Type.KEY_RELEASED){
            if(event.asKeyEvent().key == Keyboard.Key.F1){ // Hilfe ausblenden
                help = false;
            }
        }
    }

    /**
     * Render-Methode.
     * Stellt alle Elemente, z.B. Maussprite oder Pausesprite sowie das Level auf dem Fenster dar.
     */
    public void draw(){
        // Rendertextur leeren
        rtex.clear();

        // View auf aktuelle Kameraposition (Spielerposition) setzen und in Rendertextur aktualisieren
        view.setCenter(currentLevel.getCameraPos());
        rtex.setView(view);

        // Elemente zeichnen und Rendertextur darstellen
        rtex.draw(currentLevel);
        rtex.draw(mouseSprite);
        rtex.display();

        // Fenster leeren
        window.clear();

        // Rendertextur mit aktuellem Shader auf das Fenster zeichnen
        if(shaderAvailable && currentShader != 0) {
            shader[currentShader].setParameter("texture", Shader.CURRENT_TEXTURE);
            RenderStates states = new RenderStates(shader[currentShader]);
            states = new RenderStates(states, BlendMode.ADD); // Alpha/Add?!
            window.draw(resultSprite, states);
        }else {
            window.draw(resultSprite);
        }

        // u.U. Pause und Hilfesprite zeichnen
        if(help) {
            window.draw(helpSprite);
        }
        if(paused) {
            window.draw(pauseSprite);
        }

        // Fenster darstellen
        window.display();
    }

    /**
     * Kollisionsüberprüfung.
     * Übergebene Liste von GameObjects wird auf Kollision geprüft. Dabei wird aus Performancegründen ein Quadtree zur Vorsortierung verwendet
     * @param gameObjects zu überprüfende Spielobjekte
     */
    public void detectCollisions(List<GameObject> gameObjects){

        // Quadtree leeren und alle aktuell vorhandenen Objekte neu einfügen
        quad.clear();
        for (GameObject gameObject : gameObjects) {
            quad.insert(gameObject);
        }

        // Datenstrukturen für Rückgabe und Kollisionspaare anlegen.
        List<GameObject> returnObjects = new ArrayList();
        Set<CollisionPair> collisionPairs = new HashSet<>(); // Set, da so doppelte Paare herausgefiltert werden. CollisionPair hat equals und hashCode Methoden.

        // Für jedes GameObject mögliche Kollisionsobjekte holen(Nachbarn).
        for (GameObject object : gameObjects) {
            returnObjects.clear();
            quad.retrieve(returnObjects, object);

            // Für jeden Nachbarn auf echte Kollision prüfen und wenn vorhanden in collisionPairs-Set einfügen.
            // Ein Set wird verwendet, da es unnötige Dopplungen durch implementierte Equals() und HashSet() Methoden von CollisionPair Dopplungen vermeidet
            for (GameObject returnObject : returnObjects) {
                if(object.equals(returnObject)) continue;   // Kollision mit sich selbst ignorieren
                if(object.intersects(returnObject)) {
                    collisionPairs.add(new CollisionPair(object, returnObject)); // dem Set hinzufügen
                }
            }
        }

        // Methode zur Kollisionsauflösung aufrufen
        resolveCollisions(collisionPairs);
    }

    /**
     * Kollisionsauflösung.
     * Löst alle Kollisionen aus dem übergebenen Set auf, berechnet neue Geschwindigkeiten und informiert die Spielobjekte über die Kollision.
     * @param collisionPairs Set von Kollisionspaaren
     */
    private void resolveCollisions(Set<CollisionPair> collisionPairs){
        for (CollisionPair collisionPair : collisionPairs) {

            GameObject a = collisionPair.getFirst();
            GameObject b = collisionPair.getSecond();

            // Normale der Kollision berechnen
            Vector2f normal = Math2D.normalize(Vector2f.sub(a.getPosition(), b.getPosition()));

            // Relative Geschwindigkeit berechnen
            Vector2f rv = Vector2f.sub(b.getVelocity(), a.getVelocity());

            // Relative Geschwindkeit in Bezug auf Kollisionsnormale berechnen
            float velAlongNormal = normal.x * rv.x + normal.y * rv.y;

            // Kollision nicht lösen, wenn sich die Objekte auseinander Bewegen
            if (velAlongNormal > 0) {

                // Elastische Komponente berechnen
                float e = Math.min(a.getRestitution(), b.getRestitution());
                velAlongNormal = velAlongNormal * e;

                // Geschwindigkeit in Richtung Normale Ausrichten - Kollisionsgeschwindigkeit
                Vector2f resultingVel = Vector2f.mul(normal, velAlongNormal);

                // Impulsgewichte berechnen
                float collisionWeightA;
                float collisionWeightB;
                if (a.getMass() == 0) {
                    collisionWeightA = 0;
                    collisionWeightB = 2;
                } else if (b.getMass() == 0) {
                    collisionWeightA = 2;
                    collisionWeightB = 0;
                } else {
                    float combinedMass = a.getMass() + b.getMass();
                    collisionWeightA = 2 * b.getMass() / combinedMass;
                    collisionWeightB = 2 * a.getMass() / combinedMass;
                }
                // resultierende Geschwindigkeiten berechnen
                Vector2f velocityA = new Vector2f(a.getVelocity().x + collisionWeightA * resultingVel.x, a.getVelocity().y + collisionWeightA * resultingVel.y);
                Vector2f velocityB = new Vector2f(b.getVelocity().x - collisionWeightB * resultingVel.x, b.getVelocity().y - collisionWeightB * resultingVel.y);

                // Kollision an Objekte melden
                a.resolveCollision(velocityA, b);
                b.resolveCollision(velocityB, a);
            }

        }
    }

    /**
     * Starte Level i und setzte es als aktuelles Level.
     * @param i Nummer des Levels.
     */
    public void startLevel(int i) {
        score = 0;
        switch (i){
            case 0: currentLevel = new Menu();
                    break;
            case 1: worldBounds = new Circle(3000, new Vector2i(0,0));
                    currentLevel = new Level1(worldBounds);
                    quad = new Quadtree(0, worldBounds.getSurroundingRect());

                break;
        }

    }

    /**
     * Öffne GameOver-Bildschirm
     */
    public void gameOver(){
        currentLevel = new GameOver((int)score);
    }


    /**
     * Gibt Rechteck des aktuellen Bildschirmausschnitts zurück.
     * @return sichtbarer Ausschnitt
     */
    public IntRect getViewBounds() {
        int left = (int) (view.getCenter().x - view.getSize().x / 2);
        int top = (int) (view.getCenter().y - view.getSize().y / 2);
        int width = (int) (view.getSize().x);
        int height = (int) (view.getSize().y);

        return new IntRect(left, top, width, height);
    }

    /**
     * Gibt Weltbegrenzung zurück.
     * @return Weltbegrenzung.
     */
    public Circle getWorldBounds(){
        return worldBounds;
    }

    /**
     * Gibt aktuelle Mausposition in Weltkoordinaten zurück.
     * @return Mausposition.
     */
    public Vector2f getMousePos(){
        return rtex.mapPixelToCoords(Mouse.getPosition(window));
    }

    /**
     * Gibt Referenz auf aktuelles Level zurück.
     * @return aktuells Level.
     */
    public Level getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Spielt Sound vom Typ type ab.
     * @param type Soundtyp.
     */
    public void playSound(Soundtype type){
        switch (type){
            case impact: impact.play();
                break;
            case gunshot: gunShot.play();
                break;
        }
    }

    /**
     * Inkrementiert den Highscore um den Wert s.
     * @param s Highscore
     */
    public void addScore(float s){
        score +=s;
    }
}
