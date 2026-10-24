package com.mastermind.view;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Difficulte;

/**
 * Intentions que le joueur peut exprimer depuis la vue.
 * La vue ne connaît que ce contrat ; le contrôleur l'implémente.
 */
public interface ActionsJoueur {
    /**
     * Demande le lancement d'une nouvelle partie, qui remplace la partie en cours s'il y en a une.
     *
     * @param graine     texte saisi par le joueur : un entier pour rejouer une combinaison connue,
     *                   vide (ou blancs) pour une combinaison imprévisible
     * @param difficulte niveau choisi, qui fixe le nombre d'essais
     * @throws IllegalArgumentException si {@code graine} n'est ni vide ni un entier {@code long}
     * @throws NullPointerException     si {@code difficulte} est {@code null}
     */
    void lancerPartie(String graine, Difficulte difficulte);

    /**
     * Retire la dernière couleur posée dans la tentative en cours.
     *
     * @throws IllegalStateException si la partie n'est pas en cours ou si la tentative est vide
     */
    void effacer();

    /**
     * Soumet la tentative en cours pour obtenir son indice.
     *
     * @throws IllegalStateException si la partie n'est pas en cours ou si la tentative est incomplète
     */
    void valider();

    void sauvegarder();
    void charger();

    /**
     * Pose une couleur dans la prochaine case libre de la tentative en cours.
     *
     * @param couleur couleur choisie dans la palette
     * @throws IllegalStateException si la partie n'est pas en cours ou si la tentative est déjà complète
     */
    void choisirCouleur(Couleur couleur);
}
