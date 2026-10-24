package com.mastermind.model.combinaison;

import static com.mastermind.model.combinaison.Couleur.BLEU;
import static com.mastermind.model.combinaison.Couleur.ROUGE;
import static com.mastermind.model.combinaison.Couleur.VERT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Vérifie l'immuabilité et les opérations d'une combinaison.
 */
class CombinaisonTest {
    @Test
    void listeModifieeApresCreation_neChangePasLaCombinaison() {
        List<Couleur> couleurs = new ArrayList<>(List.of(ROUGE, BLEU));
        Combinaison combinaison = new Combinaison(couleurs);

        couleurs.add(VERT);

        assertEquals(List.of(ROUGE, BLEU), combinaison.couleurs());
    }

    @Test
    void couleursExposees_nePeuventPasEtreModifiees() {
        Combinaison combinaison = new Combinaison(List.of(ROUGE));

        assertThrows(UnsupportedOperationException.class, () -> combinaison.couleurs().add(BLEU));
    }

    @Test
    void couleurNulle_estRefusee() {
        assertThrows(NullPointerException.class, () -> new Combinaison(Arrays.asList(ROUGE, null)));
    }

    @Test
    void vide_neContientAucunPion() {
        assertTrue(Combinaison.vide().estVide());
        assertEquals(0, Combinaison.vide().taille());
    }

    @Test
    void couleurA_renvoieLaCouleurALaPosition() {
        Combinaison combinaison = new Combinaison(List.of(ROUGE, BLEU, VERT));

        assertEquals(BLEU, combinaison.couleurA(1));
    }

    @Test
    void occurrences_compteLesPionsDUneCouleur() {
        Combinaison combinaison = new Combinaison(List.of(ROUGE, BLEU, ROUGE));

        assertEquals(2, combinaison.occurrences(ROUGE));
        assertEquals(0, combinaison.occurrences(VERT));
    }

    @Test
    void avec_ajouteADroiteSansModifierLOriginale() {
        Combinaison originale = new Combinaison(List.of(ROUGE));

        Combinaison allongee = originale.avec(BLEU);

        assertEquals(List.of(ROUGE, BLEU), allongee.couleurs());
        assertEquals(List.of(ROUGE), originale.couleurs());
    }

    @Test
    void avec_couleurNulle_estRefusee() {
        assertThrows(NullPointerException.class, () -> Combinaison.vide().avec(null));
    }

    @Test
    void sansDernierPion_retireADroiteSansModifierLOriginale() {
        Combinaison originale = new Combinaison(List.of(ROUGE, BLEU));

        Combinaison raccourcie = originale.sansDernierPion();

        assertEquals(List.of(ROUGE), raccourcie.couleurs());
        assertEquals(List.of(ROUGE, BLEU), originale.couleurs());
    }

    @Test
    void sansDernierPion_surCombinaisonVide_estRefuse() {
        assertThrows(IllegalStateException.class, () -> Combinaison.vide().sansDernierPion());
    }

    @Test
    void combinaisonsDeMemesCouleurs_sontEgales() {
        assertEquals(new Combinaison(List.of(ROUGE, BLEU)), Combinaison.vide().avec(ROUGE).avec(BLEU));
        assertFalse(new Combinaison(List.of(ROUGE, BLEU)).equals(new Combinaison(List.of(BLEU, ROUGE))));
    }
}
