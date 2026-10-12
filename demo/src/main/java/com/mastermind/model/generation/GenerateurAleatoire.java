package com.mastermind.model.generation;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import java.util.OptionalLong;
import java.util.Random;

/**
 * Tire chaque couleur au hasard ; une même graine donne toujours la même combinaison.
 */
public final class GenerateurAleatoire implements GenerateurCombinaison {
    private static final Couleur[] COULEURS = Couleur.values();

    @Override
    public Combinaison generer(int longueur, OptionalLong graine) {
        Random random = graine.isPresent() ? new Random(graine.getAsLong()) : new Random();
        return new Combinaison(random.ints(longueur, 0, COULEURS.length)
                .mapToObj(i -> COULEURS[i])
                .toList());
    }
}
