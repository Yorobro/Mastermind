package com.mastermind.model.partie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Vérifie les paramètres de partie associés à chaque niveau.
 */
class DifficulteTest {
    @Test
    void facile_donneQuatorzeEssais() {
        assertEquals(new Configuration(4, 14), Difficulte.FACILE.configuration());
    }

    @Test
    void difficile_donneHuitEssais() {
        assertEquals(new Configuration(4, 8), Difficulte.DIFFICILE.configuration());
    }

    @Test
    void chaqueNiveau_gardeLaLongueurStandard() {
        for (Difficulte difficulte : Difficulte.values()) {
            assertEquals(Configuration.STANDARD.longueur(), difficulte.configuration().longueur());
        }
    }

    @Test
    void parDefaut_estFacile() {
        assertEquals(Difficulte.FACILE, Difficulte.parDefaut());
    }
}
