package com.mastermind.model.generation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

/**
 * Vérifie la longueur et la reproductibilité des combinaisons tirées au hasard.
 */
class GenerateurAleatoireTest {
    private final GenerateurAleatoire generateur = new GenerateurAleatoire();

    @Test
    void genereLaLongueurDemandee() {
        assertEquals(5, generateur.generer(5, OptionalLong.empty()).couleurs().size());
    }

    @Test
    void memeGraine_memeCombinaison() {
        assertEquals(
                generateur.generer(4, OptionalLong.of(123)),
                generateur.generer(4, OptionalLong.of(123)));
    }
}
