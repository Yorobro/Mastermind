package com.mastermind.view.component.atoms;

import com.mastermind.model.partie.Difficulte;
import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

/**
 * Liste déroulante des niveaux de difficulté, présélectionnée sur {@link Difficulte#parDefaut()}.
 * Le libellé affiché est choisi ici : le modèle ne contient aucun texte d'interface.
 */
public final class ChoixDifficulte extends ComboBox<Difficulte> {
    /**
     * Crée la liste avec tous les niveaux.
     */
    public ChoixDifficulte() {
        super(FXCollections.observableArrayList(Difficulte.values()));
        getStyleClass().add("choix-difficulte");
        setConverter(new ConvertisseurLibelle());
        setValue(Difficulte.parDefaut());
    }

    /**
     * Construit le libellé d'un niveau, nombre d'essais compris. Le switch est exhaustif :
     * ajouter une {@link Difficulte} sans la traiter ici empêche la compilation.
     *
     * @param difficulte niveau à nommer
     * @return le libellé affiché, par exemple « Facile (14 essais) »
     */
    private static String libelle(Difficulte difficulte) {
        String nom = switch (difficulte) {
            case FACILE -> "Facile";
            case DIFFICILE -> "Difficile";
        };
        return nom + " (" + difficulte.configuration().nbEssais() + " essais)";
    }

    /**
     * Affiche chaque niveau par son libellé. La liste n'étant pas éditable, la conversion inverse n'est jamais utilisée.
     */
    private static final class ConvertisseurLibelle extends StringConverter<Difficulte> {
        @Override
        public String toString(Difficulte difficulte) {
            return difficulte == null ? "" : libelle(difficulte);
        }

        @Override
        public Difficulte fromString(String texte) {
            throw new UnsupportedOperationException("la liste des difficultés n'est pas éditable");
        }
    }
}
