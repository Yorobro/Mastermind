package com.mastermind.view.component.atoms;

import java.util.Objects;
import javafx.scene.control.Button;

/**
 * Composant bouton générique : le texte et l'action déclenchée sont
 * fournis par l'appelant. Il ne connaît ni le modèle ni le contrôleur.
 */
public final class Bouton extends Button {
    /**
     * Crée un bouton qui exécute {@code action} à chaque clic.
     *
     * @param texte  libellé du bouton
     * @param action action déclenchée au clic
     * @throws NullPointerException si {@code action} est {@code null}
     */
    public Bouton(String texte, Runnable action) {
        super(texte);
        Objects.requireNonNull(action);
        setOnAction(e -> action.run());
    }
}
