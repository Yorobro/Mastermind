package com.mastermind.model.combinaison;

/**
 * Couleurs possibles d'un pion. Concept métier : aucun rendu ici,
 * l'apparence de chaque couleur est définie dans la feuille de style de la vue.
 * Ajouter une couleur impose d'ajouter sa règle {@code .case:<couleur>} dans {@code mastermind.css}.
 */
public enum Couleur {
    ROUGE,
    BLEU,
    VERT,
    JAUNE,
    ORANGE,
    VIOLET
}
