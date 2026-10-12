package com.mastermind.model.combinaison;

import java.util.List;

/**
 * Suite immuable de couleurs : la combinaison secrète ou une proposition du joueur.
 *
 * @param couleurs couleurs de gauche à droite
 */
public record Combinaison(List<Couleur> couleurs) {
    /**
     * Copie la liste reçue : la combinaison ne peut plus être modifiée de l'extérieur.
     *
     * @throws NullPointerException si la liste ou l'une de ses couleurs est {@code null}
     */
    public Combinaison {
        couleurs = List.copyOf(couleurs);
    }

    /**
     * Crée une combinaison sans aucune couleur, utilisée tant qu'aucune partie n'est lancée.
     *
     * @return une combinaison vide
     */
    public static Combinaison vide() {
        return new Combinaison(List.of());
    }
}
