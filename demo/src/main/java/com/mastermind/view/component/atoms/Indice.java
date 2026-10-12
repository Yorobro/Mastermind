package com.mastermind.view.component.atoms;

import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

/**
 * Petit rond d'indice : vide, avec une croix, ou rouge (voir {@link EtatIndice}).
 */
public final class Indice extends StackPane {
    /**
     * Crée un indice vide.
     */
    public Indice() {
        getStyleClass().add("indice");
        Region croix = new Region();
        croix.getStyleClass().add("croix");
        getChildren().add(croix);
        afficher(EtatIndice.VIDE);
    }

    /**
     * Change l'état affiché.
     *
     * @param etat état à afficher
     * @throws NullPointerException si {@code etat} est {@code null}
     */
    public void afficher(EtatIndice etat) {
        PseudoClasses.activer(this, etat);
    }
}
