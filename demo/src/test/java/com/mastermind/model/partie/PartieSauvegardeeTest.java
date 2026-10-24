package com.mastermind.model.partie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Vérifie qu'une partie sauvegardée respecte ses dimensions et reste immuable.
 */
class PartieSauvegardeeTest {
    private static final Configuration DEUX_ESSAIS = new Configuration(4, 2);
    private static final Combinaison SECRETE =
            new Combinaison(List.of(Couleur.ROUGE, Couleur.BLEU, Couleur.VERT, Couleur.JAUNE));

    @Test
    void listeModifieeApresCreation_neChangePasLaSauvegarde() {
        List<Combinaison> tentatives = new ArrayList<>(List.of(SECRETE));
        PartieSauvegardee sauvegarde = new PartieSauvegardee(DEUX_ESSAIS, SECRETE, tentatives, Combinaison.vide());

        tentatives.clear();

        assertEquals(List.of(SECRETE), sauvegarde.tentatives());
    }

    @Test
    void secreteDeMauvaiseLongueur_estRefusee() {
        assertThrows(IllegalArgumentException.class, () -> new PartieSauvegardee(
                DEUX_ESSAIS, SECRETE.sansDernierPion(), List.of(), Combinaison.vide()));
    }

    @Test
    void plusDeTentativesQueDEssais_estRefuse() {
        assertThrows(IllegalArgumentException.class, () -> new PartieSauvegardee(
                DEUX_ESSAIS, SECRETE, List.of(SECRETE, SECRETE, SECRETE), Combinaison.vide()));
    }

    @Test
    void tentativeIncomplete_estRefusee() {
        assertThrows(IllegalArgumentException.class, () -> new PartieSauvegardee(
                DEUX_ESSAIS, SECRETE, List.of(SECRETE.sansDernierPion()), Combinaison.vide()));
    }

    @Test
    void propositionTropLongue_estRefusee() {
        assertThrows(IllegalArgumentException.class, () -> new PartieSauvegardee(
                DEUX_ESSAIS, SECRETE, List.of(), SECRETE.avec(Couleur.ROUGE)));
    }

    @Test
    void composantNul_estRefuse() {
        assertThrows(NullPointerException.class, () -> new PartieSauvegardee(
                null, SECRETE, List.of(), Combinaison.vide()));
        assertThrows(NullPointerException.class, () -> new PartieSauvegardee(
                DEUX_ESSAIS, null, List.of(), Combinaison.vide()));
        assertThrows(NullPointerException.class, () -> new PartieSauvegardee(
                DEUX_ESSAIS, SECRETE, null, Combinaison.vide()));
        assertThrows(NullPointerException.class, () -> new PartieSauvegardee(
                DEUX_ESSAIS, SECRETE, List.of(), null));
    }
}
