package com.mastermind.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Difficulté;
import com.mastermind.model.partie.EtatPartie;
import com.mastermind.model.partie.Facile;
import com.mastermind.model.partie.PartieModel;
import com.mastermind.observer.Observateur;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

/**
 * Vérifie la traduction des saisies du joueur en appels au modèle.
 */
class PartieControllerTest {
    /**
     * Faux modèle : enregistre les graines reçues, sans aucune logique de jeu.
     */
    private static final class ModeleEspion implements PartieModel {
        final List<OptionalLong> graines = new ArrayList<>();

        @Override
        public void lancer(OptionalLong graine) {
            graines.add(graine);
        }

        @Override
        public void poserCouleur(Couleur couleur) {
        }

        @Override
        public void retirerCouleur() {
        }

        @Override
        public void validerTentative() {
        }

        @Override
        public EtatPartie getEtat() {
            return EtatPartie.initial();
        }

        @Override
        public void ajouterObservateur(Observateur<? super EtatPartie> observateur) {
        }

        @Override
        public void retirerObservateur(Observateur<? super EtatPartie> observateur) {
        }
    }

    private final ModeleEspion modele = new ModeleEspion();
    private final PartieController controleur = new PartieController(modele);
    //test
    private Facile Facile=new Facile();

    @Test
    void saisieVide_lanceSansGraine() {
        controleur.lancerPartie("  ",Facile);

        assertEquals(List.of(OptionalLong.empty()), modele.graines);
    }

    @Test
    void saisieNumerique_lanceAvecCetteGraine() {
        controleur.lancerPartie("42",Facile);

        assertEquals(List.of(OptionalLong.of(42)), modele.graines);
    }

    @Test
    void saisieNonNumerique_estRefuseeSansLancer() {
        assertThrows(IllegalArgumentException.class, () -> controleur.lancerPartie("abc",Facile));

        assertEquals(List.of(), modele.graines);
    }
}
