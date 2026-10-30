package com.mastermind.view;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import java.nio.file.Path;

/**
 * Intentions que le joueur peut exprimer depuis l'écran de jeu.
 * L'écran ne connaît que ce contrat ; le contrôleur l'implémente.
 */
public interface ActionsPartie {
    /**
     * Pose une couleur dans la prochaine case libre de la tentative en cours.
     *
     * @param couleur couleur choisie dans la palette
     * @throws IllegalStateException si la partie n'est pas en cours ou si la tentative est déjà complète
     */
    void choisirCouleur(Couleur couleur);

    /**
     * Retire la dernière couleur posée dans la tentative en cours.
     *
     * @throws IllegalStateException si la partie n'est pas en cours ou si la tentative est vide
     */
    void retirerCouleur();

    /**
     * Soumet la tentative en cours pour obtenir son indice.
     *
     * @throws IllegalStateException si la partie n'est pas en cours ou si la tentative est incomplète
     */
    void valider();

    /**
     * Enregistre la partie, en cours ou terminée, dans le fichier choisi.
     *
     * @param fichier emplacement de la sauvegarde ; un fichier existant est remplacé
     * @throws IllegalStateException si aucune partie n'a été lancée
     * @throws ErreurSauvegarde      si le fichier ne peut pas être écrit
     */
    void sauvegarder(Path fichier) throws ErreurSauvegarde;
}
