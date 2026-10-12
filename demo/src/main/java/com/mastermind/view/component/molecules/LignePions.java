package com.mastermind.view.component.molecules;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.view.component.atoms.Case;
import java.util.List;
import java.util.stream.Stream;
import javafx.scene.layout.HBox;

/**
 * Rangée de {@link Case} : sert pour la combinaison secrète comme pour chaque tentative.
 */
public final class LignePions extends HBox {
    private final List<Case> cases;

    /**
     * Crée une rangée de cases vides.
     *
     * @param longueur nombre de cases
     * @throws IllegalArgumentException si {@code longueur} n'est pas strictement positive
     */
    public LignePions(int longueur) {
        if (longueur <= 0) {
            throw new IllegalArgumentException("longueur doit être strictement positive");
        }
        getStyleClass().add("ligne-pions");
        cases = Stream.generate(Case::new).limit(longueur).toList();
        getChildren().addAll(cases);
    }

    /**
     * Remplit les cases de gauche à droite ; les cases restantes sont vidées (saisie partielle possible).
     *
     * @param couleurs couleurs à afficher, de gauche à droite
     * @throws IllegalArgumentException s'il y a plus de couleurs que de cases
     */
    public void afficher(List<Couleur> couleurs) {
        if (couleurs.size() > cases.size()) {
            throw new IllegalArgumentException("trop de couleurs : " + couleurs.size() + " > " + cases.size());
        }
        for (int i = 0; i < cases.size(); i++) {
            if (i < couleurs.size()) {
                cases.get(i).afficher(couleurs.get(i));
            } else {
                cases.get(i).vider();
            }
        }
    }

    /**
     * Vide toutes les cases.
     */
    public void vider() {
        afficher(List.of());
    }
}
