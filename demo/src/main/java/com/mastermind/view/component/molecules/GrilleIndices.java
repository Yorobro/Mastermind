package com.mastermind.view.component.molecules;

import com.mastermind.view.component.atoms.EtatIndice;
import com.mastermind.view.component.atoms.Indice;
import java.util.List;
import java.util.stream.Stream;
import javafx.scene.layout.GridPane;

/**
 * Bloc d'{@link Indice} disposés sur deux rangées (un carré 2×2 pour 4 pions).
 */
public final class GrilleIndices extends GridPane {
    private static final int RANGEES = 2;

    private final List<Indice> indices;

    /**
     * Crée une grille d'indices vides.
     *
     * @param nombre nombre d'indices, égal à la longueur d'une combinaison
     * @throws IllegalArgumentException si {@code nombre} n'est pas strictement positif
     */
    public GrilleIndices(int nombre) {
        if (nombre <= 0) {
            throw new IllegalArgumentException("nombre doit être strictement positif");
        }
        getStyleClass().add("grille-indices");
        indices = Stream.generate(Indice::new).limit(nombre).toList();
        int colonnes = (nombre + RANGEES - 1) / RANGEES;
        for (int i = 0; i < nombre; i++) {
            add(indices.get(i), i % colonnes, i / colonnes);
        }
    }

    /**
     * Affiche un résultat : d'abord les ronds rouges, puis les croix ; les indices restants sont vides.
     *
     * @param bienPlaces nombre de pions de bonne couleur à la bonne position
     * @param malPlaces  nombre de pions de bonne couleur à la mauvaise position
     * @throws IllegalArgumentException si un nombre est négatif ou si leur somme dépasse le nombre d'indices
     */
    public void afficher(int bienPlaces, int malPlaces) {
        if (bienPlaces < 0 || malPlaces < 0 || bienPlaces + malPlaces > indices.size()) {
            throw new IllegalArgumentException("résultat incohérent : " + bienPlaces + " / " + malPlaces);
        }
        for (int i = 0; i < indices.size(); i++) {
            indices.get(i).afficher(etatA(i, bienPlaces, malPlaces));
        }
    }

    /**
     * Remet tous les indices à vide.
     */
    public void vider() {
        afficher(0, 0);
    }

    /**
     * Détermine l'état de l'indice à une position donnée.
     *
     * @param position   index de l'indice dans la grille
     * @param bienPlaces nombre de pions bien placés
     * @param malPlaces  nombre de pions mal placés
     * @return l'état à afficher à cette position
     */
    private static EtatIndice etatA(int position, int bienPlaces, int malPlaces) {
        if (position < bienPlaces) {
            return EtatIndice.BIEN_PLACE;
        }
        if (position < bienPlaces + malPlaces) {
            return EtatIndice.MAL_PLACE;
        }
        return EtatIndice.VIDE;
    }
}
