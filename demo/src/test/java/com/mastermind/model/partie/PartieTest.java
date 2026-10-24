package com.mastermind.model.partie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.combinaison.Indice;
import com.mastermind.model.generation.GenerateurAleatoire;
import com.mastermind.observer.Observateur;
import java.util.ArrayList;
import java.util.List;
import java.util.OptionalLong;
import org.junit.jupiter.api.Test;

/**
 * Vérifie le cycle de vie d'une partie et la notification de ses observateurs.
 */
class PartieTest {
    private static final Combinaison SECRETE =
            new Combinaison(List.of(Couleur.ROUGE, Couleur.BLEU, Couleur.VERT, Couleur.JAUNE));
    private static final Combinaison RATEE =
            new Combinaison(List.of(Couleur.ORANGE, Couleur.ORANGE, Couleur.ORANGE, Couleur.ORANGE));

    /**
     * Générateur factice : la combinaison est connue d'avance, le test ne dépend pas du hasard.
     */
    private final Partie partie = new Partie(Configuration.STANDARD, (longueur, graine) -> SECRETE);

    @Test
    void nouvellePartie_estNonCommenceeSansCombinaison() {
        assertEquals(EtatPartie.initial(Configuration.STANDARD), partie.getEtat());
    }

    @Test
    void lancer_passeEnCoursEnCachantLaSecrete() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());

        assertEquals(EtatPartie.nouvellePartie(Configuration.STANDARD), partie.getEtat());
    }

    @Test
    void lancer_transmetLaLongueurEtLaGraineAuGenerateur() {
        List<Object> recus = new ArrayList<>();
        Partie espionnee = new Partie(new Configuration(5, 10), (longueur, graine) -> {
            recus.add(longueur);
            recus.add(graine);
            return SECRETE.avec(Couleur.ROUGE);
        });

        espionnee.lancer(new Configuration(5, 10), OptionalLong.of(42));

        assertEquals(List.of(5, OptionalLong.of(42)), recus);
    }

    @Test
    void lancer_notifieLesObservateurs() {
        List<EtatPartie> recus = new ArrayList<>();
        partie.ajouterObservateur(recus::add);

        partie.lancer(Configuration.STANDARD, OptionalLong.empty());

        assertEquals(List.of(EtatPartie.nouvellePartie(Configuration.STANDARD)), recus);
    }

    @Test
    void observateurRetire_nEstPlusNotifie() {
        List<EtatPartie> recus = new ArrayList<>();
        Observateur<EtatPartie> observateur = recus::add;
        partie.ajouterObservateur(observateur);
        partie.retirerObservateur(observateur);

        partie.lancer(Configuration.STANDARD, OptionalLong.empty());

        assertEquals(List.of(), recus);
    }

    @Test
    void poserCouleur_ajouteLaCouleurALaProposition() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());

        partie.poserCouleur(Couleur.ROUGE);

        assertEquals(new Combinaison(List.of(Couleur.ROUGE)), partie.getEtat().proposition());
    }

    @Test
    void poserCouleur_surPropositionComplete_estRefuse() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        proposer(partie, RATEE);

        assertThrows(IllegalStateException.class, () -> partie.poserCouleur(Couleur.ROUGE));
    }

    @Test
    void retirerCouleur_enleveLaDerniereCouleurPosee() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        partie.poserCouleur(Couleur.ROUGE);
        partie.poserCouleur(Couleur.BLEU);

        partie.retirerCouleur();

        assertEquals(new Combinaison(List.of(Couleur.ROUGE)), partie.getEtat().proposition());
    }

    @Test
    void retirerCouleur_surPropositionVide_estRefuse() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());

        assertThrows(IllegalStateException.class, partie::retirerCouleur);
    }

    @Test
    void validerTentative_enregistreLaTentativeEtSonIndiceSansReveler() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        proposer(partie, RATEE);

        partie.validerTentative();

        assertEquals(new EtatPartie(
                Configuration.STANDARD,
                Statut.EN_COURS,
                Combinaison.vide(),
                List.of(RATEE),
                List.of(new Indice(0, 0)),
                Combinaison.vide()
        ), partie.getEtat());
    }

    @Test
    void validerTentative_avecLaSecrete_gagneEtRevele() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        proposer(partie, SECRETE);

        partie.validerTentative();

        assertEquals(Statut.GAGNEE, partie.getEtat().statut());
        assertEquals(SECRETE, partie.getEtat().secrete());
    }

    @Test
    void validerTentative_auDernierEssaiSansTrouver_perdEtRevele() {
        Partie courte = partieLanceeAvec(new Configuration(4, 2));

        proposer(courte, RATEE);
        courte.validerTentative();
        proposer(courte, RATEE);
        courte.validerTentative();

        assertEquals(Statut.PERDUE, courte.getEtat().statut());
        assertEquals(SECRETE, courte.getEtat().secrete());
    }

    @Test
    void validerTentative_avecPropositionIncomplete_estRefusee() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        partie.poserCouleur(Couleur.ROUGE);

        assertThrows(IllegalStateException.class, partie::validerTentative);
    }

    @Test
    void validerTentative_avantLancement_estRefusee() {
        assertThrows(IllegalStateException.class, partie::validerTentative);
    }

    @Test
    void actionDeJeu_apresLaFinDePartie_estRefusee() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        proposer(partie, SECRETE);
        partie.validerTentative();

        assertThrows(IllegalStateException.class, () -> partie.poserCouleur(Couleur.ROUGE));
    }

    @Test
    void relancer_repartDeZeroAvecUneSecreteCachee() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        proposer(partie, SECRETE);
        partie.validerTentative();

        partie.lancer(Configuration.STANDARD, OptionalLong.empty());

        assertEquals(EtatPartie.nouvellePartie(Configuration.STANDARD), partie.getEtat());
    }

    @Test
    void poserCouleur_nulle_estRefusee() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());

        assertThrows(NullPointerException.class, () -> partie.poserCouleur(null));
    }

    @Test
    void actionsDeJeu_avantLancement_sontRefusees() {
        assertThrows(IllegalStateException.class, () -> partie.poserCouleur(Couleur.ROUGE));
        assertThrows(IllegalStateException.class, partie::retirerCouleur);
        assertThrows(IllegalStateException.class, partie::validerTentative);
    }

    @Test
    void actionsDeJeu_apresDefaite_sontRefusees() {
        Partie courte = partieLanceeAvec(new Configuration(4, 1));
        proposer(courte, RATEE);
        courte.validerTentative();

        assertThrows(IllegalStateException.class, () -> courte.poserCouleur(Couleur.ROUGE));
        assertThrows(IllegalStateException.class, courte::retirerCouleur);
        assertThrows(IllegalStateException.class, courte::validerTentative);
    }

    @Test
    void validerTentative_trouverAuDernierEssai_gagneEtNePerdPas() {
        Partie courte = partieLanceeAvec(new Configuration(4, 2));
        proposer(courte, RATEE);
        courte.validerTentative();
        proposer(courte, SECRETE);

        courte.validerTentative();

        assertEquals(Statut.GAGNEE, courte.getEtat().statut());
    }

    @Test
    void validerTentative_garderLesTentativesEtIndicesDansLOrdre() {
        Combinaison deuxBonnes =
                new Combinaison(List.of(Couleur.BLEU, Couleur.ROUGE, Couleur.ORANGE, Couleur.ORANGE));
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        proposer(partie, RATEE);
        partie.validerTentative();
        proposer(partie, deuxBonnes);

        partie.validerTentative();

        assertEquals(List.of(RATEE, deuxBonnes), partie.getEtat().tentatives());
        assertEquals(List.of(new Indice(0, 0), new Indice(0, 2)), partie.getEtat().indices());
    }

    @Test
    void secrete_resteCacheeDansChaqueNotificationJusquALaFin() {
        List<EtatPartie> recus = new ArrayList<>();
        partie.ajouterObservateur(recus::add);
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        proposer(partie, RATEE);
        partie.retirerCouleur();
        partie.poserCouleur(Couleur.ORANGE);
        partie.validerTentative();
        proposer(partie, SECRETE);

        partie.validerTentative();

        EtatPartie dernier = recus.get(recus.size() - 1);
        recus.subList(0, recus.size() - 1).forEach(etat -> assertEquals(Combinaison.vide(), etat.secrete()));
        assertEquals(SECRETE, dernier.secrete());
    }

    @Test
    void chaqueAction_notifieUneSeuleFois() {
        List<EtatPartie> recus = new ArrayList<>();
        partie.ajouterObservateur(recus::add);

        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        partie.poserCouleur(Couleur.ROUGE);
        partie.retirerCouleur();
        proposer(partie, RATEE);
        partie.validerTentative();

        assertEquals(1 + 1 + 1 + RATEE.taille() + 1, recus.size());
        assertEquals(partie.getEtat(), recus.get(recus.size() - 1));
    }

    @Test
    void actionRefusee_neNotifiePasEtNeChangePasLEtat() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        EtatPartie avant = partie.getEtat();
        List<EtatPartie> recus = new ArrayList<>();
        partie.ajouterObservateur(recus::add);

        assertThrows(IllegalStateException.class, partie::validerTentative);

        assertEquals(avant, partie.getEtat());
        assertEquals(List.of(), recus);
    }

    @Test
    void lancer_avecGenerateurDeMauvaiseLongueur_estRefuseSansChangerLEtat() {
        Partie defaillante = new Partie(Configuration.STANDARD,
                (longueur, graine) -> new Combinaison(List.of(Couleur.ROUGE)));

        assertThrows(IllegalStateException.class, () -> defaillante.lancer(Configuration.STANDARD, OptionalLong.empty()));
        assertEquals(EtatPartie.initial(Configuration.STANDARD), defaillante.getEtat());
    }

    @Test
    void lancer_avecGenerateurRenvoyantNull_estRefuse() {
        Partie defaillante = new Partie(Configuration.STANDARD, (longueur, graine) -> null);

        assertThrows(IllegalStateException.class, () -> defaillante.lancer(Configuration.STANDARD, OptionalLong.empty()));
    }

    @Test
    void relancer_enCoursDePartie_effaceTentativesEtProposition() {
        partie.lancer(Configuration.STANDARD, OptionalLong.empty());
        proposer(partie, RATEE);
        partie.validerTentative();
        partie.poserCouleur(Couleur.ROUGE);

        partie.lancer(Configuration.STANDARD, OptionalLong.empty());

        assertEquals(EtatPartie.nouvellePartie(Configuration.STANDARD), partie.getEtat());
    }

    @Test
    void partieComplete_avecGenerateurAleatoire_seGagneEnRejouantLaGraine() {
        OptionalLong graine = OptionalLong.of(2024);
        Combinaison secreteAttendue = new GenerateurAleatoire().generer(4, graine);
        Partie reelle = new Partie(Configuration.STANDARD, new GenerateurAleatoire());
        reelle.lancer(Configuration.STANDARD, graine);

        proposer(reelle, secreteAttendue);
        reelle.validerTentative();

        assertEquals(Statut.GAGNEE, reelle.getEtat().statut());
        assertEquals(List.of(new Indice(4, 0)), reelle.getEtat().indices());
    }

    @Test
    void nouvellePartie_annonceLaConfigurationInitiale() {
        Partie annoncee = new Partie(Difficulte.FACILE.configuration(), (longueur, graine) -> SECRETE);

        assertEquals(Difficulte.FACILE.configuration(), annoncee.getEtat().configuration());
    }

    @Test
    void lancer_appliqueLaConfigurationDemandee() {
        partie.lancer(Difficulte.DIFFICILE.configuration(), OptionalLong.empty());

        assertEquals(Difficulte.DIFFICILE.configuration(), partie.getEtat().configuration());
    }

    @Test
    void lancer_enDifficile_perdApresHuitEssais() {
        partie.lancer(Difficulte.DIFFICILE.configuration(), OptionalLong.empty());

        for (int essai = 1; essai <= 7; essai++) {
            proposer(partie, RATEE);
            partie.validerTentative();
        }
        assertEquals(Statut.EN_COURS, partie.getEtat().statut());
        proposer(partie, RATEE);
        partie.validerTentative();

        assertEquals(Statut.PERDUE, partie.getEtat().statut());
    }

    @Test
    void relancer_avecUneAutreDifficulte_changeLeNombreDEssais() {
        partie.lancer(Difficulte.DIFFICILE.configuration(), OptionalLong.empty());

        partie.lancer(Difficulte.FACILE.configuration(), OptionalLong.empty());

        assertEquals(EtatPartie.nouvellePartie(Difficulte.FACILE.configuration()), partie.getEtat());
    }

    @Test
    void lancer_sansConfiguration_estRefuse() {
        assertThrows(NullPointerException.class, () -> partie.lancer(null, OptionalLong.empty()));
        assertEquals(EtatPartie.initial(Configuration.STANDARD), partie.getEtat());
    }

    /**
     * Crée une partie au générateur factice et la lance avec la configuration donnée.
     *
     * @param configuration dimensions de la partie
     * @return la partie, en cours
     */
    private static Partie partieLanceeAvec(Configuration configuration) {
        Partie lancee = new Partie(configuration, (longueur, graine) -> SECRETE);
        lancee.lancer(configuration, OptionalLong.empty());
        return lancee;
    }

    /**
     * Pose une à une les couleurs d'une combinaison dans la proposition en cours.
     *
     * @param cible       partie dans laquelle jouer
     * @param combinaison couleurs à poser, de gauche à droite
     */
    private static void proposer(Partie cible, Combinaison combinaison) {
        combinaison.couleurs().forEach(cible::poserCouleur);
    }
}
