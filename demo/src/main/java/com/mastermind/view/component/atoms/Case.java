package com.mastermind.view.component.atoms;

import com.mastermind.model.combinaison.Couleur;
import javafx.scene.layout.Region;

/**
 * Emplacement d'un pion : vide, ou rempli d'une {@link Couleur}.
 */
public final class Case extends Region {
    /**
     * Crée une case vide.
     */
    public Case() {
        getStyleClass().add("case");
    }

    /**
     * Remplit la case avec une couleur, en remplaçant la précédente.
     *
     * @param couleur couleur du pion
     * @throws NullPointerException si {@code couleur} est {@code null}
     */
    public void afficher(Couleur couleur) {
        PseudoClasses.activer(this, couleur);
    }

    /**
     * Retire le pion : la case redevient vide.
     */
    public void vider() {
        PseudoClasses.desactiver(this, Couleur.class);
    }
}
