package com.mastermind.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Configuration;
import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.partie.EtatPartie;
import com.mastermind.model.partie.Partie;
import com.mastermind.model.partie.PartieModel;
import com.mastermind.model.partie.PartieSauvegardee;
import com.mastermind.model.sauvegarde.ChargementPartie;
import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import com.mastermind.model.sauvegarde.SauvegardePartie;
import com.mastermind.observer.Observateur;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

/**
 * Vérifie la traduction des saisies du joueur en appels au modèle et aux ports de sauvegarde.
 */
class PartieControllerTest {
    private static final Combinaison SECRETE =
            new Combinaison(List.of(Couleur.ROUGE, Couleur.BLEU, Couleur.VERT, Couleur.JAUNE));
    private static final PartieSauvegardee SAUVEGARDE =
            new PartieSauvegardee(Configuration.STANDARD, SECRETE, List.of(), Combinaison.vide());
    private static final Path FICHIER = Path.of("save.json");

    /**
     * Faux modèle : enregistre les appels reçus, sans aucune logique de jeu.
     */
    private static final class ModeleEspion implements PartieModel {
        final List<Configuration> configurations = new ArrayList<>();
        final List<OptionalLong> graines = new ArrayList<>();
        final List<String> appels = new ArrayList<>();
        final List<PartieSauvegardee> restaurees = new ArrayList<>();

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
        public PartieSauvegardee exporter() {
            return SAUVEGARDE;
        }

        @Override
        public void restaurer(PartieSauvegardee sauvegarde) {
            restaurees.add(sauvegarde);
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
    private final List<PartieSauvegardee> sauvegardees = new ArrayList<>();
    private final List<Path> destinations = new ArrayList<>();
    private final SauvegardePartie sauvegardeEspionne = (partie, destination) -> {
        sauvegardees.add(partie);
        destinations.add(destination);
    };
    private final ChargementPartie chargementFixe = source -> SAUVEGARDE;
    private final PartieController controleur = new PartieController(modele, sauvegardeEspionne, chargementFixe);

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
                new Partie(Configuration.STANDARD, (longueur, graine) -> SECRETE), sauvegardeEspionne, chargementFixe);

        assertThrows(IllegalStateException.class, surModeleReel::valider);
    }

    @Test
    void sauvegarder_transmetLaPartieExporteeAuPort() throws ErreurSauvegarde {
        controleur.sauvegarder(FICHIER);

        assertEquals(List.of(SAUVEGARDE), sauvegardees);
        assertEquals(List.of(FICHIER), destinations);
    }

    @Test
    void sauvegarder_echecDuPort_remonteAuJoueur() {
        ErreurSauvegarde erreur = new ErreurSauvegarde("disque plein");
        PartieController controleurEnEchec = new PartieController(modele, (partie, destination) -> {
            throw erreur;
        }, chargementFixe);

        assertSame(erreur, assertThrows(ErreurSauvegarde.class, () -> controleurEnEchec.sauvegarder(FICHIER)));
    }

    @Test
    void charger_restaureDansLeModeleLaPartieLue() throws ErreurSauvegarde {
        controleur.charger(FICHIER);

        assertEquals(List.of(SAUVEGARDE), modele.restaurees);
    }

    @Test
    void charger_echecDuPort_neRestaureRien() {
        PartieController controleurEnEchec = new PartieController(modele, sauvegardeEspionne, source -> {
            throw new ErreurSauvegarde("fichier illisible");
        });

        assertThrows(ErreurSauvegarde.class, () -> controleurEnEchec.charger(FICHIER));
        assertEquals(List.of(), modele.restaurees);
    }

    @Test
    void charger_partieImpossible_devientUneErreurDeSauvegarde() {
        PartieSauvegardee apresVictoire = new PartieSauvegardee(
                Configuration.STANDARD, SECRETE, List.of(SECRETE, SECRETE), Combinaison.vide());
        PartieController surModeleReel = new PartieController(
                new Partie(Configuration.STANDARD, (longueur, graine) -> SECRETE),
                sauvegardeEspionne, source -> apresVictoire);

        assertThrows(ErreurSauvegarde.class, () -> surModeleReel.charger(FICHIER));
    }
}
