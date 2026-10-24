package com.mastermind.model.combinaison;

import java.util.List;
import java.util.stream.Stream;

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

    /**
     * Renvoie le nombre de pions de la combinaison.
     *
     * @return le nombre de couleurs posées
     */
    public int taille() {
        return couleurs.size();
    }

    /**
     * Renvoie la couleur d'un pion.
     *
     * @param position index du pion, à partir de 0
     * @return la couleur à cette position
     * @throws IndexOutOfBoundsException si {@code position} est hors de la combinaison
     */
    public Couleur couleurA(int position) {
        return couleurs.get(position);
    }

    /**
     * Compte les pions d'une couleur donnée.
     *
     * @param couleur couleur recherchée
     * @return le nombre de pions de cette couleur
     */
    public int occurrences(Couleur couleur) {
        return (int) couleurs.stream().filter(couleur::equals).count();
    }

    /**
     * Crée une nouvelle combinaison avec une couleur ajoutée à droite ; celle-ci reste inchangée.
     *
     * @param couleur couleur à ajouter
     * @return une combinaison d'un pion plus longue
     * @throws NullPointerException si {@code couleur} est {@code null}
     */
    public Combinaison avec(Couleur couleur) {
        return new Combinaison(Stream.concat(couleurs.stream(), Stream.of(couleur)).toList());
    }

    /**
     * Crée une nouvelle combinaison sans le pion le plus à droite ; celle-ci reste inchangée.
     *
     * @return une combinaison d'un pion plus courte
     * @throws IllegalStateException si la combinaison est vide
     */
    public Combinaison sansDernierPion() {
        if (couleurs.isEmpty()) {
            throw new IllegalStateException("une combinaison vide n'a aucun pion à retirer");
        }
        return new Combinaison(couleurs.subList(0, taille() - 1));
    }

    /**
     * Indique si la combinaison ne contient aucun pion.
     *
     * @return {@code true} si aucune couleur n'est posée
     */
    public boolean estVide() {
        return couleurs.isEmpty();
    }
}
