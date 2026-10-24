package com.mastermind.model.sauvegarde;

import com.mastermind.model.partie.PartieSauvegardee;
import java.nio.file.Path;

/**
 * Port d'entrée : relit une partie enregistrée par {@link SauvegardePartie}.
 * Le modèle définit le contrat ; une couche externe (package {@code data}) l'implémente.
 */
@FunctionalInterface
public interface ChargementPartie {
    /**
     * Relit la partie enregistrée à l'emplacement donné.
     *
     * @param source emplacement choisi par le joueur
     * @return la partie lue, aux dimensions cohérentes
     * @throws ErreurSauvegarde si l'emplacement est illisible ou ne contient pas une sauvegarde valide
     */
    PartieSauvegardee charger(Path source) throws ErreurSauvegarde;
}
