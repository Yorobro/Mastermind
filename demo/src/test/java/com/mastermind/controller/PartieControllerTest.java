package com.mastermind.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Configuration;
import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.partie.EtatPartie;
import com.mastermind.model.partie.Partie;
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
     * Faux modèle : enregistre les appels reçus, sans aucune logique de jeu.
     */
    private static final class ModeleEspion implements PartieModel {
        final List<Configuration> configurations = new ArrayList<>();
        final List<OptionalLong> graines = new ArrayList<>();
        final List<String> appels = new ArrayList<>();

        @Override
        public void lancer(Configuration configuration, OptionalLong graine) {
            configurations.add(configuration);
            graines.add(graine);
        }

        @Override
        public void poserCouleur(Couleur couleur) {
            appels.add("poserCouleur " + couleur);
        }

        @Override
        public void retirerCouleur() {
            appels.add("retirerCouleur");
        }

        @Override
        public void validerTentative() {
            appels.add("validerTentative");
        }

        @Override
        public EtatPartie getEtat() {
            return EtatPartie.initial(Configuration.STANDARD);
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

    @Test
    void saisieVide_lanceSansGraine() {
        controleur.lancerPartie("  ", Difficulte.FACILE);

        assertEquals(List.of(OptionalLong.empty()), modele.graines);
    }

    @Test
    void saisieNumerique_lanceAvecCetteGraine() {
        controleur.lancerPartie("42", Difficulte.FACILE);

        assertEquals(List.of(OptionalLong.of(42)), modele.graines);
    }

    @Test
    void saisieNonNumerique_estRefuseeSansLancer() {
        assertThrows(IllegalArgumentException.class, () -> controleur.lancerPartie("abc", Difficulte.FACILE));

        assertEquals(List.of(), modele.graines);
    }

    @Test
    void difficulteChoisie_lanceAvecSaConfiguration() {
        controleur.lancerPartie("", Difficulte.DIFFICILE);

        assertEquals(List.of(Difficulte.DIFFICILE.configuration()), modele.configurations);
    }

    @Test
    void sansDifficulte_estRefuseSansLancer() {
        assertThrows(NullPointerException.class, () -> controleur.lancerPartie("", null));

        assertEquals(List.of(), modele.configurations);
    }

    @Test
    void choisirCouleur_poseCetteCouleurDansLeModele() {
        controleur.choisirCouleur(Couleur.VERT);

        assertEquals(List.of("poserCouleur VERT"), modele.appels);
    }

    @Test
    void effacer_retireLaDerniereCouleurDuModele() {
        controleur.effacer();

        assertEquals(List.of("retirerCouleur"), modele.appels);
    }

    @Test
    void valider_valideLaTentativeDuModele() {
        controleur.valider();

        assertEquals(List.of("validerTentative"), modele.appels);
    }

    @Test
    void actionRefuseeParLeModele_remonteAuJoueur() {
        PartieController surModeleReel = new PartieController(
                new Partie(Configuration.STANDARD, (longueur, graine) -> Combinaison.vide()));

        assertThrows(IllegalStateException.class, surModeleReel::valider);
    }
}
