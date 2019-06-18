package de.hsmw.tkretzs1.energetic.core;


/**
 * Klasse zum Programmstart
 * @author Tom Kretzschmar
 */
public class Main {

    public static void main(String[] args) {
        Game game = Game.getInstance(); // Erzeuge Game-Instanz
        game.start();                   // Starte das Spiel
    }
}
