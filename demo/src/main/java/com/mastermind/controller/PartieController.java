package com.mastermind.controller;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.partie.PartieModel;
import com.mastermind.view.ActionsJoueur;
import java.util.Objects;
import java.util.OptionalLong;

/**
 * Contrôleur : traduit les intentions du joueur en appels au modèle.
 * Il ne met jamais la vue à jour lui-même : c'est la vue qui observe le modèle.
 */
public class PartieController implements ActionsJoueur {
    private final PartieModel model;

    /**
     * Crée un contrôleur qui pilote le modèle donné.
     *
     * @param model modèle à piloter
     * @throws NullPointerException si {@code model} est {@code null}
     */
    public PartieController(PartieModel model) {
        this.model = Objects.requireNonNull(model);
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
    public void sauvegarder() {
        // TODO
    }

    @Override
    public void charger() {
        // TODO
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
