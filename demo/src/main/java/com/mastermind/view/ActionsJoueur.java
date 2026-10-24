package com.mastermind.view;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Difficulté;

/**
 * Intentions que le joueur peut exprimer depuis la vue.
 * La vue ne connaît que ce contrat ; le contrôleur l'implémente.
 */
public interface ActionsJoueur {
    /**
     * Demande le lancement d'une nouvelle partie, qui remplace la partie en cours s'il y en a une.
     *
     * @param graine texte saisi par le joueur : un entier pour rejouer une combinaison connue,
     *               vide (ou blancs) pour une combinaison imprévisible
     * @throws IllegalArgumentException si {@code graine} n'est ni vide ni un entier {@code long}
     */
    void lancerPartie(String graine, Difficulté diffculte);

    void effacer();
    void valider();
    void sauvegarder();
    void charger();
    void choisirCouleur(Couleur couleur);
}
