package com.mastermind.model.partie;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Indice;

import java.util.List;
import java.util.Objects;

/**
 * Instantané immuable de la partie, transmis aux observateurs.
 * La vue ne lit que ça : elle n'accède jamais directement à {@link Partie}.
 *
 * @param configuration dimensions de la partie : longueur des combinaisons et nombre d'essais
 * @param statut        étape courante de la partie
 * @param secrete       combinaison à deviner, révélée seulement une fois la partie terminée
 *                      ({@link Combinaison#vide()} avant)
 * @param tentatives    propositions validées, de la première à la dernière
 * @param indices       indice de chaque tentative, au même index que celle-ci
 * @param proposition   tentative en cours de saisie, pas encore validée
 */
public record EtatPartie(
    Configuration configuration,
    Statut statut,
    Combinaison secrete,
    List<Combinaison> tentatives,
    List<Indice> indices,
    Combinaison proposition
) {
    /**
     * Vérifie que l'état est complet et cohérent, puis copie les listes : l'état ne peut plus être modifié
     * de l'extérieur.
     *
     * @throws NullPointerException     si un composant, ou un élément d'une liste, est {@code null}
     * @throws IllegalArgumentException si le nombre d'indices diffère du nombre de tentatives,
     *                                  ou si les tentatives ou la proposition dépassent la configuration
     */
    public EtatPartie {
        Objects.requireNonNull(configuration);
        Objects.requireNonNull(statut);
        Objects.requireNonNull(secrete);
        Objects.requireNonNull(proposition);
        tentatives = List.copyOf(tentatives);
        indices = List.copyOf(indices);
        if (tentatives.size() != indices.size()) {
            throw new IllegalArgumentException("chaque tentative doit avoir exactement un indice");
        }
        if (tentatives.size() > configuration.nbEssais() || proposition.taille() > configuration.longueur()) {
            throw new IllegalArgumentException("l'état dépasse les dimensions de la configuration");
        }
    }

    /**
     * Crée l'état d'une partie pas encore lancée.
     *
     * @param configuration dimensions du plateau affiché en attendant le lancement
     * @return un état {@link Statut#NON_COMMENCEE} sans combinaison ni tentative
     */
    public static EtatPartie initial(Configuration configuration) {
        return new EtatPartie(
            configuration,
            Statut.NON_COMMENCEE,
            Combinaison.vide(),
            List.of(),
            List.of(),
            Combinaison.vide()
        );
    }

    /**
     * Crée l'état d'une partie qui vient d'être lancée.
     *
     * @param configuration dimensions de la partie
     * @return un état {@link Statut#EN_COURS} sans tentative, sans proposition et dont la secrète est cachée
     */
    public static EtatPartie nouvellePartie(Configuration configuration) {
        return new EtatPartie(
            configuration,
            Statut.EN_COURS,
            Combinaison.vide(),
            List.of(),
            List.of(),
            Combinaison.vide()
        );
    }

    /**
     * Crée une copie de cet état avec une autre proposition en cours.
     *
     * @param nouvelleProposition proposition qui remplace la proposition courante
     * @return le nouvel état, identique pour le reste
     */
    public EtatPartie avecProposition(Combinaison nouvelleProposition) {
        return new EtatPartie(configuration, statut, secrete, tentatives, indices, nouvelleProposition);
    }
}
