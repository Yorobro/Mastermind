package com.mastermind.view.component.organisms;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.view.component.molecules.GrilleIndices;
import com.mastermind.view.component.molecules.LignePions;
import java.util.List;
import javafx.scene.layout.HBox;

/**
 * Une ligne du plateau : les pions proposés et leurs indices.
 */
public final class LigneTentative extends HBox {
    private final LignePions pions;
    private final GrilleIndices indices;

    /**
     * Crée une ligne vide.
     *
     * @param longueur nombre de pions de la ligne
     * @throws IllegalArgumentException si {@code longueur} n'est pas strictement positive
     */
    public LigneTentative(int longueur) {
        pions = new LignePions(longueur);
        indices = new GrilleIndices(longueur);
        getStyleClass().add("ligne-tentative");
        getChildren().addAll(pions, indices);
    }

    /**
     * Affiche une tentative et son résultat.
     *
     * @param couleurs   pions proposés, de gauche à droite
     * @param bienPlaces nombre de pions bien placés
     * @param malPlaces  nombre de pions mal placés
     * @throws IllegalArgumentException si les couleurs dépassent la longueur ou si le résultat est incohérent
     */
    public void afficher(List<Couleur> couleurs, int bienPlaces, int malPlaces) {
        pions.afficher(couleurs);
        indices.afficher(bienPlaces, malPlaces);
    }

    /**
     * Vide les pions et les indices.
     */
    public void vider() {
        pions.vider();
        indices.vider();
    }
}
