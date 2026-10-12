package com.mastermind.view.component.atoms;

import java.util.Locale;
import javafx.css.PseudoClass;
import javafx.scene.Node;

/**
 * Relie l'état d'un composant (une valeur d'enum) à une pseudo-classe CSS.
 * Ex. {@code Couleur.ROUGE} active {@code :rouge}, {@code EtatIndice.MAL_PLACE} active {@code :mal-place}.
 * Le Java choisit l'état, la feuille de style décide du rendu.
 */
final class PseudoClasses {
    /**
     * Classe utilitaire : non instanciable.
     */
    private PseudoClasses() {
    }

    /**
     * Active la pseudo-classe de {@code actif} et désactive celles des autres valeurs de son enum.
     *
     * @param <E>   type de l'enum
     * @param node  composant à mettre à jour
     * @param actif valeur à activer
     * @throws NullPointerException si {@code actif} est {@code null}
     */
    static <E extends Enum<E>> void activer(Node node, E actif) {
        for (E valeur : actif.getDeclaringClass().getEnumConstants()) {
            node.pseudoClassStateChanged(pseudoClassDe(valeur), valeur == actif);
        }
    }

    /**
     * Désactive les pseudo-classes de toutes les valeurs d'un enum.
     *
     * @param <E>  type de l'enum
     * @param node composant à mettre à jour
     * @param type enum dont les pseudo-classes sont désactivées
     */
    static <E extends Enum<E>> void desactiver(Node node, Class<E> type) {
        for (E valeur : type.getEnumConstants()) {
            node.pseudoClassStateChanged(pseudoClassDe(valeur), false);
        }
    }

    /**
     * Calcule la pseudo-classe d'une valeur : son nom en minuscules, tirets à la place des soulignés.
     *
     * @param valeur valeur d'enum
     * @return la pseudo-classe correspondante, par exemple {@code :mal-place} pour {@code MAL_PLACE}
     */
    private static PseudoClass pseudoClassDe(Enum<?> valeur) {
        return PseudoClass.getPseudoClass(valeur.name().toLowerCase(Locale.ROOT).replace('_', '-'));
    }
}
