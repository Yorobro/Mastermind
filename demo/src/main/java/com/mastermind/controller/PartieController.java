package com.mastermind.controller;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.partie.PartieModel;
import com.mastermind.model.sauvegarde.ChargementPartie;
import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import com.mastermind.model.sauvegarde.SauvegardePartie;
import com.mastermind.view.ActionsMenu;
import com.mastermind.view.ActionsPartie;
import java.nio.file.Path;
import java.util.Objects;
import java.util.OptionalLong;

/**
 * Contrôleur : traduit les intentions du joueur, depuis l'accueil comme depuis le plateau, en appels au modèle.
 * Il ne met jamais la vue à jour lui-même : c'est la vue qui observe le modèle.
 * Pour la sauvegarde, il fait le lien entre le modèle et les ports, sans savoir comment ils stockent la partie.
 */
public class PartieController implements ActionsMenu, ActionsPartie {
    private final PartieModel model;
    private final SauvegardePartie sauvegarde;
    private final ChargementPartie chargement;

    /**
     * Crée un contrôleur qui pilote le modèle donné.
     *
     * @param model      modèle à piloter
     * @param sauvegarde port qui enregistre une partie
     * @param chargement port qui relit une partie enregistrée
     * @throws NullPointerException si l'un des arguments est {@code null}
     */
    public PartieController(PartieModel model, SauvegardePartie sauvegarde, ChargementPartie chargement) {
        this.model = Objects.requireNonNull(model);
        this.sauvegarde = Objects.requireNonNull(sauvegarde);
        this.chargement = Objects.requireNonNull(chargement);
    }

    @Override
    public void lancerPartie(Difficulte difficulte) {
        model.lancer(difficulte.configuration(), OptionalLong.empty());
    }

    @Override
    public void choisirCouleur(Couleur couleur) {
        model.poserCouleur(couleur);
    }

    @Override
    public void retirerCouleur() {
        model.retirerCouleur();
    }

    @Override
    public void valider() {
        model.validerTentative();
    }

    @Override
    public void sauvegarder(Path fichier) throws ErreurSauvegarde {
        sauvegarde.sauvegarder(model.exporter(), fichier);
    }

    @Override
    public void charger(Path fichier) throws ErreurSauvegarde {
        try {
            model.restaurer(chargement.charger(fichier));
        } catch (IllegalArgumentException e) {
            throw new ErreurSauvegarde("La sauvegarde ne correspond à aucune partie possible", e);
        }
    }
}
