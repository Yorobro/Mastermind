package com.mastermind;

/**
 * Point d'entrée qui n'étend pas Application, pour pouvoir lancer JavaFX
 * depuis le classpath (VS Code) sans l'erreur "JavaFX runtime components are missing".
 */
public class Launcher {
    /**
     * Lance l'application en déléguant à {@link Main#main(String[])}.
     *
     * @param args arguments de la ligne de commande
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
