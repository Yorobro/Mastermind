package com.mastermind.controller;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.partie.PartieModel;
import com.mastermind.model.sauvegarde.ChargementPartie;
import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import com.mastermind.model.sauvegarde.SauvegardePartie;
import com.mastermind.view.ActionsJoueur;
import java.nio.file.Path;
import java.util.Objects;
import java.util.OptionalLong;

/**
 * Contrôleur : traduit les intentions du joueur en appels au modèle.
 * Il ne met jamais la vue à jour lui-même : c'est la vue qui observe le modèle.
 * Pour la sauvegarde, il fait le lien entre le modèle et les ports, sans savoir comment ils stockent la partie.
 */
public class PartieController implements ActionsJoueur {
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
    public void lancerPartie(String graine, Difficulte difficulte) {
        model.lancer(difficulte.configuration(), versGraine(graine));
    }

    @Override
    public void choisirCouleur(Couleur couleur) {
        model.poserCouleur(couleur);
    }

    @Override
    public void effacer() {
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

    /**
     * Convertit la saisie de l'interface en donnée métier.
     *
     * @param saisie texte saisi par le joueur
     * @return la graine, ou vide si la saisie ne contient que des blancs
     * @throws NumberFormatException si la saisie n'est ni vide ni un entier {@code long}
     */
    private static OptionalLong versGraine(String saisie) {
        String texte = saisie.strip();
        return texte.isEmpty() ? OptionalLong.empty() : OptionalLong.of(Long.parseLong(texte));
    }
}
