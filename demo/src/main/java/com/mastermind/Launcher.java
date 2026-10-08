package com.mastermind;

/**
 * Point d'entrée qui n'étend pas Application, pour pouvoir lancer JavaFX
 * depuis le classpath (VS Code) sans l'erreur "JavaFX runtime components are missing".
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
