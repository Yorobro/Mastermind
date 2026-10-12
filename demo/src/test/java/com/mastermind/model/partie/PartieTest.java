package com.mastermind.model.partie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
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

    /**
     * Générateur factice : la combinaison est connue d'avance, le test ne dépend pas du hasard.
     */
    private final Partie partie = new Partie(Configuration.STANDARD, (longueur, graine) -> SECRETE);

    @Test
    void nouvellePartie_estNonCommenceeSansCombinaison() {
        assertEquals(EtatPartie.initial(), partie.getEtat());
    }

    @Test
    void lancer_passeEnCoursAvecLaCombinaisonGeneree() {
        partie.lancer(OptionalLong.empty());

        assertEquals(new EtatPartie(Statut.EN_COURS, SECRETE), partie.getEtat());
    }

    @Test
    void lancer_transmetLaLongueurEtLaGraineAuGenerateur() {
        List<Object> recus = new ArrayList<>();
        Partie espionnee = new Partie(new Configuration(5, 10), (longueur, graine) -> {
            recus.add(longueur);
            recus.add(graine);
            return SECRETE;
        });

        espionnee.lancer(OptionalLong.of(42));

        assertEquals(List.of(5, OptionalLong.of(42)), recus);
    }

    @Test
    void lancer_notifieLesObservateurs() {
        List<EtatPartie> recus = new ArrayList<>();
        partie.ajouterObservateur(recus::add);

        partie.lancer(OptionalLong.empty());

        assertEquals(List.of(new EtatPartie(Statut.EN_COURS, SECRETE)), recus);
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
}
