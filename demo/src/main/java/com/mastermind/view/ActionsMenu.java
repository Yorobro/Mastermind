package com.mastermind.view;

import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import java.nio.file.Path;

/**
 * Intentions que le joueur peut exprimer depuis l'écran d'accueil.
 * L'écran ne connaît que ce contrat ; le contrôleur l'implémente.
 */
public interface ActionsMenu {
    /**
     * Lance une nouvelle partie, qui remplace la partie précédente s'il y en a une.
     *
     * @param difficulte niveau choisi, qui fixe le nombre d'essais
     * @throws NullPointerException si {@code difficulte} est {@code null}
     */
    void lancerPartie(Difficulte difficulte);

    /**
     * Remplace la partie courante par celle enregistrée dans le fichier choisi.
     *
     * @param fichier sauvegarde à reprendre
     * @throws ErreurSauvegarde si le fichier est illisible ou ne contient pas une partie possible ;
     *                          la partie courante reste alors inchangée
     */
    void charger(Path fichier) throws ErreurSauvegarde;
}
