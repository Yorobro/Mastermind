package com.mastermind.view.component.atoms;

/**
 * États d'affichage d'un {@link Indice}.
 */
public enum EtatIndice {
    /**
     * Pas d'information.
     */
    VIDE,
    /**
     * Bonne couleur, mauvaise position : une croix.
     */
    MAL_PLACE,
    /**
     * Bonne couleur, bonne position : un rond rouge.
     */
    BIEN_PLACE
}
