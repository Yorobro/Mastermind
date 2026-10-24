package com.mastermind.model.combinaison;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Réponse à une proposition, selon la règle du Mastermind : combien de pions sont bien placés
 * et combien sont mal placés, sans dire lesquels.
 *
 * @param bienPlaces nombre de pions de bonne couleur à la bonne position
 * @param malPlaces  nombre de pions de bonne couleur à la mauvaise position
 */
public record Indice(int bienPlaces, int malPlaces) {
    /**
     * Vérifie que l'indice est cohérent.
     *
     * @throws IllegalArgumentException si l'un des nombres est négatif
     */
    public Indice {
        if (bienPlaces < 0 || malPlaces < 0) {
            throw new IllegalArgumentException("un indice ne peut pas être négatif : " + bienPlaces + " / " + malPlaces);
        }
    }

    /**
     * Compare une proposition à la combinaison secrète. Chaque pion de la secrète ne peut justifier
     * qu'un seul indice : bien placé en priorité, sinon mal placé.
     *
     * @param proposition combinaison proposée par le joueur
     * @param secrete     combinaison à deviner
     * @return l'indice correspondant
     * @throws IllegalArgumentException si les deux combinaisons n'ont pas la même longueur
     */
    public static Indice evaluer(Combinaison proposition, Combinaison secrete) {
        if (proposition.taille() != secrete.taille()) {
            throw new IllegalArgumentException("la proposition et la secrète doivent avoir la même longueur");
        }
        int bienPlaces = compterBienPlaces(proposition, secrete);
        return new Indice(bienPlaces, compterCouleursCommunes(proposition, secrete) - bienPlaces);
    }

    /**
     * Compte les pions de bonne couleur à la bonne position.
     *
     * @param proposition combinaison proposée
     * @param secrete     combinaison à deviner, de même longueur
     * @return le nombre de pions bien placés
     */
    private static int compterBienPlaces(Combinaison proposition, Combinaison secrete) {
        return (int) IntStream.range(0, proposition.taille())
                .filter(position -> proposition.couleurA(position) == secrete.couleurA(position))
                .count();
    }

    /**
     * Compte les couleurs partagées, quelle que soit leur position. Pour chaque couleur, on retient
     * le plus petit nombre d'occurrences des deux combinaisons : un pion n'est jamais compté deux fois.
     *
     * @param proposition combinaison proposée
     * @param secrete     combinaison à deviner
     * @return le nombre de pions de bonne couleur, bien placés compris
     */
    private static int compterCouleursCommunes(Combinaison proposition, Combinaison secrete) {
        return Arrays.stream(Couleur.values())
                .mapToInt(couleur -> Math.min(proposition.occurrences(couleur), secrete.occurrences(couleur)))
                .sum();
    }
}
