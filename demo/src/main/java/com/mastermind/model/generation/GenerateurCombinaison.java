package com.mastermind.model.generation;

import com.mastermind.model.combinaison.Combinaison;
import java.util.OptionalLong;

/**
 * Source externe de la combinaison secrète. La partie en dépend sans savoir
 * comment elle est produite (aléatoire, fixe pour les tests, choisie par un joueur...).
 */
@FunctionalInterface
public interface GenerateurCombinaison {
    /**
     * Produit une combinaison de la longueur demandée.
     *
     * @param longueur nombre de couleurs à produire
     * @param graine   graine imposée pour rejouer la même combinaison, ou vide pour laisser l'implémentation choisir
     * @return une combinaison d'exactement {@code longueur} couleurs
     */
    Combinaison generer(int longueur, OptionalLong graine);
}
