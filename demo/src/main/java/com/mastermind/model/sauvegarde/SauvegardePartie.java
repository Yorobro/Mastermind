package com.mastermind.model.sauvegarde;

import com.mastermind.model.partie.PartieSauvegardee;
import java.nio.file.Path;

/**
 * Port de sortie : enregistre une partie hors de l'application.
 * Le modèle définit le contrat ; une couche externe (package {@code data}) l'implémente.
 */
@FunctionalInterface
public interface SauvegardePartie {
    /**
     * Enregistre une partie à l'emplacement donné, en remplaçant le contenu existant.
     * Si l'enregistrement échoue, une sauvegarde déjà présente à cet emplacement reste intacte.
     *
     * @param partie      partie à enregistrer
     * @param destination emplacement choisi par le joueur
     * @throws ErreurSauvegarde si l'emplacement n'est pas accessible en écriture
     */
    void sauvegarder(PartieSauvegardee partie, Path destination) throws ErreurSauvegarde;
}
