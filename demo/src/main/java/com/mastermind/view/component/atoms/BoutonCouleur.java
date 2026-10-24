package com.mastermind.view.component.atoms;

import com.mastermind.model.combinaison.Couleur;
import javafx.scene.control.Button;
import javafx.scene.shape.Circle;

/**
 * Bouton de la palette : représente une {@link Couleur} et exécute l'action fournie à chaque clic.
 * Le rendu est délégué à la feuille de style via la pseudo-classe de la couleur.
 */
public final class BoutonCouleur extends Button {
    /**
     * Crée un bouton de la couleur donnée.
     */
    // Fait le rayon du cercle
    private static final double RAYON = 12;

    /**
     * @param couleur couleur représentée par le bouton
     * @param action  action déclenchée au clic
     * @throws NullPointerException si {@code couleur} ou {@code action} est {@code null}
     */
    public BoutonCouleur(Couleur couleur, Runnable action) {
        getStyleClass().add("bouton-couleur");
        setGraphic(creerPastille(couleur));
        setAccessibleText(couleur.name());
        setOnAction(e -> action.run());
    }
    /**
     * Crée le cercle coloré affiché dans le bouton.
     *
     * @param couleur couleur de la pastille
     * @return la pastille, stylée par la pseudo-classe de {@code couleur}
     */
    private static Circle creerPastille(Couleur couleur) {
        Circle pastille = new Circle(RAYON);
        pastille.getStyleClass().add("pastille");
        PseudoClasses.activer(pastille, couleur);
        return pastille;
    }
}