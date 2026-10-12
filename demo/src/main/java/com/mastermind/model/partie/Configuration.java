package com.mastermind.model.partie;

/**
 * Paramètres d'une partie, partagés par le modèle et la vue.
 *
 * @param longueur nombre de pions d'une combinaison
 * @param nbEssais nombre de tentatives autorisées
 */
public record Configuration(int longueur, int nbEssais) {
    public static final Configuration STANDARD = new Configuration(4, 12);

    /**
     * Vérifie que les paramètres permettent de jouer.
     *
     * @throws IllegalArgumentException si {@code longueur} ou {@code nbEssais} n'est pas strictement positif
     */
    public Configuration {
        if (longueur <= 0 || nbEssais <= 0) {
            throw new IllegalArgumentException("longueur et nbEssais doivent être strictement positifs");
        }
    }
}
