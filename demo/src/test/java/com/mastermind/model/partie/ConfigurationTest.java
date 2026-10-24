package com.mastermind.model.partie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Vérifie les paramètres de partie et les étapes du cycle de vie.
 */
class ConfigurationTest {
    @Test
    void standard_correspondAuMastermindClassique() {
        assertEquals(new Configuration(4, 12), Configuration.STANDARD);
    }

    @Test
    void parametreNulOuNegatif_estRefuse() {
        assertThrows(IllegalArgumentException.class, () -> new Configuration(0, 12));
        assertThrows(IllegalArgumentException.class, () -> new Configuration(4, 0));
        assertThrows(IllegalArgumentException.class, () -> new Configuration(-1, 12));
    }

    @Test
    void seulesGagneeEtPerdue_sontTerminees() {
        assertFalse(Statut.NON_COMMENCEE.estTerminee());
        assertFalse(Statut.EN_COURS.estTerminee());
        assertTrue(Statut.GAGNEE.estTerminee());
        assertTrue(Statut.PERDUE.estTerminee());
    }
}
