package com.mastermind.model.partie;

import com.mastermind.model.combinaison.Combinaison;
import java.util.List;
import java.util.Objects;

/**
 * Photo complète d'une partie, combinaison secrète comprise, destinée uniquement à la sauvegarde.
 * Seules les données de base sont gardées : le statut et les indices se recalculent en rejouant les tentatives.
 *
 * @param configuration dimensions de la partie
 * @param secrete       combinaison à deviner
 * @param tentatives    propositions validées, de la première à la dernière
 * @param proposition   tentative en cours de saisie, pas encore validée
 */
public record PartieSauvegardee(
    Configuration configuration,
    Combinaison secrete,
    List<Combinaison> tentatives,
    Combinaison proposition
) {
    /**
     * Vérifie que la photo respecte ses dimensions, puis copie la liste des tentatives.
     *
     * @throws NullPointerException     si un composant, ou une tentative, est {@code null}
     * @throws IllegalArgumentException si la secrète ou une tentative n'a pas la bonne longueur,
     *                                  s'il y a plus de tentatives que d'essais ou si la proposition est trop longue
     */
    public PartieSauvegardee {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(secrete);
        Objects.requireNonNull(proposition);
        tentatives = List.copyOf(tentatives);
        int longueur = configuration.longueur();
        if (secrete.taille() != longueur) {
            throw new IllegalArgumentException("la secrète doit avoir " + longueur + " pions");
        }
        if (tentatives.size() > configuration.nbEssais()) {
            throw new IllegalArgumentException("plus de tentatives que d'essais autorisés");
        }
        if (tentatives.stream().anyMatch(tentative -> tentative.taille() != longueur)) {
            throw new IllegalArgumentException("chaque tentative doit avoir " + longueur + " pions");
        }
        if (proposition.taille() > longueur) {
            throw new IllegalArgumentException("la proposition dépasse " + longueur + " pions");
        }
    }
}
