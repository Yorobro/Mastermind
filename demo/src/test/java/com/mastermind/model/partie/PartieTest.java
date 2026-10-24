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
        assertEquals(EtatPartie.initial(), partie.getEtat());
    }

    @Test
    void lancer_passeEnCoursEnCachantLaSecrete() {
        partie.lancer(OptionalLong.empty());

        assertEquals(EtatPartie.nouvellePartie(), partie.getEtat());
    }

    @Test
    void lancer_transmetLaLongueurEtLaGraineAuGenerateur() {
        List<Object> recus = new ArrayList<>();
        Partie espionnee = new Partie(new Configuration(5, 10), (longueur, graine) -> {
            recus.add(longueur);
            recus.add(graine);
            return SECRETE.avec(Couleur.ROUGE);
        });

        espionnee.lancer(OptionalLong.of(42));

        assertEquals(List.of(5, OptionalLong.of(42)), recus);
    }

    @Test
    void lancer_notifieLesObservateurs() {
        List<EtatPartie> recus = new ArrayList<>();
        partie.ajouterObservateur(recus::add);

        partie.lancer(OptionalLong.empty());

        assertEquals(List.of(EtatPartie.nouvellePartie()), recus);
    }

    @Test
    void observateurRetire_nEstPlusNotifie() {
        List<EtatPartie> recus = new ArrayList<>();
        Observateur<EtatPartie> observateur = recus::add;
        partie.ajouterObservateur(observateur);
        partie.retirerObservateur(observateur);

        partie.lancer(OptionalLong.empty());

        assertEquals(List.of(), recus);
    }

    @Test
    void poserCouleur_ajouteLaCouleurALaProposition() {
        partie.lancer(OptionalLong.empty());

        partie.poserCouleur(Couleur.ROUGE);

        assertEquals(new Combinaison(List.of(Couleur.ROUGE)), partie.getEtat().proposition());
    }

    @Test
    void poserCouleur_surPropositionComplete_estRefuse() {
        partie.lancer(OptionalLong.empty());
        proposer(partie, RATEE);

        assertThrows(IllegalStateException.class, () -> partie.poserCouleur(Couleur.ROUGE));
    }

    @Test
    void retirerCouleur_enleveLaDerniereCouleurPosee() {
        partie.lancer(OptionalLong.empty());
        partie.poserCouleur(Couleur.ROUGE);
        partie.poserCouleur(Couleur.BLEU);

        partie.retirerCouleur();

        assertEquals(new Combinaison(List.of(Couleur.ROUGE)), partie.getEtat().proposition());
    }

    @Test
    void retirerCouleur_surPropositionVide_estRefuse() {
        partie.lancer(OptionalLong.empty());

        assertThrows(IllegalStateException.class, partie::retirerCouleur);
    }

    @Test
    void validerTentative_enregistreLaTentativeEtSonIndiceSansReveler() {
        partie.lancer(OptionalLong.empty());
        proposer(partie, RATEE);

        partie.validerTentative();

        assertEquals(new EtatPartie(
                Statut.EN_COURS,
                Combinaison.vide(),
                List.of(RATEE),
                List.of(new Indice(0, 0)),
                Combinaison.vide()
        ), partie.getEtat());
    }

    @Test
    void validerTentative_avecLaSecrete_gagneEtRevele() {
        partie.lancer(OptionalLong.empty());
        proposer(partie, SECRETE);

        partie.validerTentative();

        assertEquals(Statut.GAGNEE, partie.getEtat().statut());
        assertEquals(SECRETE, partie.getEtat().secrete());
    }

    @Test
    void validerTentative_auDernierEssaiSansTrouver_perdEtRevele() {
        Partie courte = new Partie(new Configuration(4, 2), (longueur, graine) -> SECRETE);
        courte.lancer(OptionalLong.empty());

        proposer(courte, RATEE);
        courte.validerTentative();
        proposer(courte, RATEE);
        courte.validerTentative();

        assertEquals(Statut.PERDUE, courte.getEtat().statut());
        assertEquals(SECRETE, courte.getEtat().secrete());
    }

    @Test
    void validerTentative_avecPropositionIncomplete_estRefusee() {
        partie.lancer(OptionalLong.empty());
        partie.poserCouleur(Couleur.ROUGE);

        assertThrows(IllegalStateException.class, partie::validerTentative);
    }

    @Test
    void validerTentative_avantLancement_estRefusee() {
        assertThrows(IllegalStateException.class, partie::validerTentative);
    }

    @Test
    void actionDeJeu_apresLaFinDePartie_estRefusee() {
        partie.lancer(OptionalLong.empty());
        proposer(partie, SECRETE);
        partie.validerTentative();

        assertThrows(IllegalStateException.class, () -> partie.poserCouleur(Couleur.ROUGE));
    }

    @Test
    void relancer_repartDeZeroAvecUneSecreteCachee() {
        partie.lancer(OptionalLong.empty());
        proposer(partie, SECRETE);
        partie.validerTentative();

        partie.lancer(OptionalLong.empty());

        assertEquals(EtatPartie.nouvellePartie(), partie.getEtat());
    }

    @Test
    void poserCouleur_nulle_estRefusee() {
        partie.lancer(OptionalLong.empty());

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
        Partie courte = new Partie(new Configuration(4, 1), (longueur, graine) -> SECRETE);
        courte.lancer(OptionalLong.empty());
        proposer(courte, RATEE);
        courte.validerTentative();

        assertThrows(IllegalStateException.class, () -> courte.poserCouleur(Couleur.ROUGE));
        assertThrows(IllegalStateException.class, courte::retirerCouleur);
        assertThrows(IllegalStateException.class, courte::validerTentative);
    }

    @Test
    void validerTentative_trouverAuDernierEssai_gagneEtNePerdPas() {
        Partie courte = new Partie(new Configuration(4, 2), (longueur, graine) -> SECRETE);
        courte.lancer(OptionalLong.empty());
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
        partie.lancer(OptionalLong.empty());
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
        partie.lancer(OptionalLong.empty());
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

        partie.lancer(OptionalLong.empty());
        partie.poserCouleur(Couleur.ROUGE);
        partie.retirerCouleur();
        proposer(partie, RATEE);
        partie.validerTentative();

        assertEquals(1 + 1 + 1 + RATEE.taille() + 1, recus.size());
        assertEquals(partie.getEtat(), recus.get(recus.size() - 1));
    }

    @Test
    void actionRefusee_neNotifiePasEtNeChangePasLEtat() {
        partie.lancer(OptionalLong.empty());
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

        assertThrows(IllegalStateException.class, () -> defaillante.lancer(OptionalLong.empty()));
        assertEquals(EtatPartie.initial(), defaillante.getEtat());
    }

    @Test
    void lancer_avecGenerateurRenvoyantNull_estRefuse() {
        Partie defaillante = new Partie(Configuration.STANDARD, (longueur, graine) -> null);

        assertThrows(IllegalStateException.class, () -> defaillante.lancer(OptionalLong.empty()));
    }

    @Test
    void relancer_enCoursDePartie_effaceTentativesEtProposition() {
        partie.lancer(OptionalLong.empty());
        proposer(partie, RATEE);
        partie.validerTentative();
        partie.poserCouleur(Couleur.ROUGE);

        partie.lancer(OptionalLong.empty());

        assertEquals(EtatPartie.nouvellePartie(), partie.getEtat());
    }

    @Test
    void partieComplete_avecGenerateurAleatoire_seGagneEnRejouantLaGraine() {
        OptionalLong graine = OptionalLong.of(2024);
        Combinaison secreteAttendue = new GenerateurAleatoire().generer(4, graine);
        Partie reelle = new Partie(Configuration.STANDARD, new GenerateurAleatoire());
        reelle.lancer(graine);

        proposer(reelle, secreteAttendue);
        reelle.validerTentative();

        assertEquals(Statut.GAGNEE, reelle.getEtat().statut());
        assertEquals(List.of(new Indice(4, 0)), reelle.getEtat().indices());
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
