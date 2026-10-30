package com.mastermind.view;

import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import java.nio.file.Path;

/**
 * Sauvegarde ou chargement appliqué au fichier choisi par le joueur.
 */
@FunctionalInterface
interface OperationFichier {
    /**
     * Applique l'opération au fichier.
     *
     * @param fichier fichier choisi par le joueur
     * @throws ErreurSauvegarde si l'opération échoue
     */
    void executer(Path fichier) throws ErreurSauvegarde;
}
