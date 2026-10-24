package com.mastermind.model.partie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.combinaison.Indice;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Vérifie la cohérence et l'immuabilité de l'état publié.
 */
class EtatPartieTest {
    private static final Combinaison ROUGES =
            new Combinaison(List.of(Couleur.ROUGE, Couleur.ROUGE, Couleur.ROUGE, Couleur.ROUGE));

    @Test
    void listesModifieesApresCreation_neChangentPasLEtat() {
        List<Combinaison> tentatives = new ArrayList<>(List.of(ROUGES));
        List<Indice> indices = new ArrayList<>(List.of(new Indice(0, 0)));
        EtatPartie etat = new EtatPartie(Statut.EN_COURS, Combinaison.vide(), tentatives, indices, Combinaison.vide());

        tentatives.clear();
        indices.clear();

        assertEquals(List.of(ROUGES), etat.tentatives());
        assertEquals(List.of(new Indice(0, 0)), etat.indices());
    }

    @Test
    void listesExposees_nePeuventPasEtreModifiees() {
        EtatPartie etat = EtatPartie.nouvellePartie();

        assertThrows(UnsupportedOperationException.class, () -> etat.tentatives().add(ROUGES));
        assertThrows(UnsupportedOperationException.class, () -> etat.indices().add(new Indice(0, 0)));
    }

    @Test
    void tentativeSansIndice_estRefusee() {
        assertThrows(IllegalArgumentException.class, () -> new EtatPartie(
                Statut.EN_COURS, Combinaison.vide(), List.of(ROUGES), List.of(), Combinaison.vide()));
    }

    @Test
    void composantNul_estRefuse() {
        assertThrows(NullPointerException.class, () -> new EtatPartie(
                null, Combinaison.vide(), List.of(), List.of(), Combinaison.vide()));
        assertThrows(NullPointerException.class, () -> new EtatPartie(
                Statut.EN_COURS, null, List.of(), List.of(), Combinaison.vide()));
        assertThrows(NullPointerException.class, () -> new EtatPartie(
                Statut.EN_COURS, Combinaison.vide(), null, List.of(), Combinaison.vide()));
        assertThrows(NullPointerException.class, () -> new EtatPartie(
                Statut.EN_COURS, Combinaison.vide(), List.of(), null, Combinaison.vide()));
        assertThrows(NullPointerException.class, () -> new EtatPartie(
                Statut.EN_COURS, Combinaison.vide(), List.of(), List.of(), null));
    }

    @Test
    void initial_estNonCommenceEtVide() {
        assertEquals(
                new EtatPartie(Statut.NON_COMMENCEE, Combinaison.vide(), List.of(), List.of(), Combinaison.vide()),
                EtatPartie.initial());
    }

    @Test
    void nouvellePartie_estEnCoursEtVideAvecSecreteCachee() {
        assertEquals(
                new EtatPartie(Statut.EN_COURS, Combinaison.vide(), List.of(), List.of(), Combinaison.vide()),
                EtatPartie.nouvellePartie());
    }

    @Test
    void avecProposition_neChangeQueLaProposition() {
        EtatPartie etat = new EtatPartie(
                Statut.EN_COURS, Combinaison.vide(), List.of(ROUGES), List.of(new Indice(0, 0)), Combinaison.vide());
        Combinaison proposition = new Combinaison(List.of(Couleur.BLEU));

        assertEquals(
                new EtatPartie(Statut.EN_COURS, Combinaison.vide(), List.of(ROUGES), List.of(new Indice(0, 0)), proposition),
                etat.avecProposition(proposition));
    }
}
