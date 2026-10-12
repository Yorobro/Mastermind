package com.mastermind.view.component.atoms;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

/**
 * Champ de saisie générique : le texte d'invite et les saisies autorisées sont
 * fournis par l'appelant, la validation (touche Entrée) est remontée par événement.
 * Masqué, il ne prend plus de place dans la mise en page.
 */
public final class ChampTexte extends TextField {
    /**
     * Crée un champ vide qui n'accepte que les saisies validées par {@code filtre}.
     *
     * @param invite texte affiché quand le champ est vide
     * @param filtre accepte ou refuse le texte complet après chaque frappe
     * @throws NullPointerException si {@code filtre} est {@code null}
     */
    public ChampTexte(String invite, Predicate<String> filtre) {
        Objects.requireNonNull(filtre);
        getStyleClass().add("champ-texte");
        setPromptText(invite);
        setTextFormatter(new TextFormatter<String>(
                modification -> filtre.test(modification.getControlNewText()) ? modification : null));
        managedProperty().bind(visibleProperty());
    }

    /**
     * Définit l'action déclenchée à l'appui sur Entrée.
     *
     * @param action reçoit le texte saisi
     * @throws NullPointerException si {@code action} est {@code null}
     */
    public void setOnValider(Consumer<String> action) {
        Objects.requireNonNull(action);
        setOnAction(e -> action.accept(getText()));
    }
}
