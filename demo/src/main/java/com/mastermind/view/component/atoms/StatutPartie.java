package com.mastermind.view.component.atoms;

import com.mastermind.model.partie.Statut;
import javafx.scene.control.Label;

/**
 * Composant affichant l'étape de la partie.
 */
public final class StatutPartie extends Label {
    /**
     * Crée le libellé, vide jusqu'au premier affichage.
     */
    public StatutPartie() {
        getStyleClass().add("statut");
    }

    /**
     * Affiche le libellé correspondant au statut. Le switch est exhaustif :
     * ajouter un {@link Statut} sans le traiter ici empêche la compilation.
     *
     * @param statut statut à afficher
     */
    public void afficher(Statut statut) {
        setText(switch (statut) {
            case NON_COMMENCEE -> "Aucune partie en cours";
            case EN_COURS -> "Partie en cours";
        });
    }
}
