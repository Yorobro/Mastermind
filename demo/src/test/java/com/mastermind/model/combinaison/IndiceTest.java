package com.mastermind.model.combinaison;

import static com.mastermind.model.combinaison.Couleur.BLEU;
import static com.mastermind.model.combinaison.Couleur.JAUNE;
import static com.mastermind.model.combinaison.Couleur.ROUGE;
import static com.mastermind.model.combinaison.Couleur.VERT;
import static com.mastermind.model.combinaison.Couleur.VIOLET;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Vérifie le calcul des pions bien placés et mal placés selon la règle du Mastermind.
 */
class IndiceTest {
    private static final Combinaison SECRETE = combinaison(ROUGE, VERT, VERT, BLEU);

    @Test
    void propositionIdentique_toutEstBienPlace() {
        assertEquals(new Indice(4, 0), Indice.evaluer(SECRETE, SECRETE));
    }

    @Test
    void bienEtMalPlaces_sontComptesSansDireLesquels() {
        assertEquals(new Indice(1, 1), Indice.evaluer(combinaison(VERT, VIOLET, JAUNE, BLEU), SECRETE));
    }

    @Test
    void bonnesCouleursMelangees_comptentLesBienEtMalPlaces() {
        assertEquals(new Indice(1, 3), Indice.evaluer(combinaison(BLEU, VERT, ROUGE, VERT), SECRETE));
    }

    @Test
    void couleurRepeteeDansLaProposition_nEstCompteeQuAutantQueDansLaSecrete() {
        assertEquals(new Indice(1, 0), Indice.evaluer(combinaison(BLEU, BLEU, BLEU, BLEU), SECRETE));
    }

    @Test
    void couleurRepeteeDansLaSecrete_peutDonnerPlusieursMalPlaces() {
        assertEquals(new Indice(0, 2), Indice.evaluer(combinaison(VERT, JAUNE, JAUNE, VERT), SECRETE));
    }

    @Test
    void pionBienPlace_nEstPasRecompteCommeMalPlace() {
        assertEquals(new Indice(1, 0), Indice.evaluer(combinaison(ROUGE, ROUGE, JAUNE, JAUNE), SECRETE));
    }

    @Test
    void couleursAbsentes_donnentUnIndiceVide() {
        assertEquals(new Indice(0, 0), Indice.evaluer(combinaison(JAUNE, VIOLET, JAUNE, VIOLET), SECRETE));
    }

    @Test
    void evaluer_donneLeMemeResultatQueLAlgorithmeDeReferencePourToutesLesCombinaisons() {
        List<Combinaison> toutes = toutesLesCombinaisons(4);

        for (Combinaison secrete : toutes) {
            for (Combinaison proposition : toutes) {
                assertEquals(evaluerParMarquage(proposition, secrete), Indice.evaluer(proposition, secrete),
                        () -> proposition + " contre " + secrete);
            }
        }
    }

    @Test
    void longueursDifferentes_sontRefusees() {
        assertThrows(IllegalArgumentException.class, () -> Indice.evaluer(combinaison(ROUGE), SECRETE));
    }

    @Test
    void indiceNegatif_estRefuse() {
        assertThrows(IllegalArgumentException.class, () -> new Indice(-1, 0));
        assertThrows(IllegalArgumentException.class, () -> new Indice(0, -1));
    }

    /**
     * Implémentation de référence, écrite autrement que {@link Indice#evaluer} : on raye d'abord les pions
     * bien placés, puis chaque pion restant de la proposition raye le premier pion libre de même couleur
     * dans la secrète.
     *
     * @param proposition combinaison proposée
     * @param secrete     combinaison à deviner, de même longueur
     * @return l'indice attendu
     */
    private static Indice evaluerParMarquage(Combinaison proposition, Combinaison secrete) {
        int longueur = secrete.taille();
        boolean[] rayeDansProposition = new boolean[longueur];
        boolean[] rayeDansSecrete = new boolean[longueur];
        int bienPlaces = 0;
        for (int i = 0; i < longueur; i++) {
            if (proposition.couleurA(i) == secrete.couleurA(i)) {
                rayeDansProposition[i] = true;
                rayeDansSecrete[i] = true;
                bienPlaces++;
            }
        }
        int malPlaces = 0;
        for (int i = 0; i < longueur; i++) {
            for (int j = 0; j < longueur && !rayeDansProposition[i]; j++) {
                if (!rayeDansSecrete[j] && proposition.couleurA(i) == secrete.couleurA(j)) {
                    rayeDansProposition[i] = true;
                    rayeDansSecrete[j] = true;
                    malPlaces++;
                }
            }
        }
        return new Indice(bienPlaces, malPlaces);
    }

    /**
     * Énumère toutes les combinaisons possibles d'une longueur donnée.
     *
     * @param longueur nombre de pions
     * @return les {@code nbCouleurs ^ longueur} combinaisons
     */
    private static List<Combinaison> toutesLesCombinaisons(int longueur) {
        List<Combinaison> combinaisons = List.of(Combinaison.vide());
        for (int i = 0; i < longueur; i++) {
            List<Combinaison> allongees = new ArrayList<>();
            for (Combinaison combinaison : combinaisons) {
                for (Couleur couleur : Couleur.values()) {
                    allongees.add(combinaison.avec(couleur));
                }
            }
            combinaisons = allongees;
        }
        return combinaisons;
    }

    /**
     * Raccourci de construction d'une combinaison.
     *
     * @param couleurs couleurs de gauche à droite
     * @return la combinaison correspondante
     */
    private static Combinaison combinaison(Couleur... couleurs) {
        return new Combinaison(List.of(couleurs));
    }
}
