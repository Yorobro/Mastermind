package com.mastermind.model.partie;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.combinaison.Indice;
import com.mastermind.model.generation.GenerateurCombinaison;
import com.mastermind.observer.Observateur;
import com.mastermind.observer.Sujet;

import java.util.List;
import java.util.Objects;
import java.util.OptionalLong;
import java.util.stream.Stream;

/**
 * Modèle : état et règles d'une partie de Mastermind.
 * Aucune dépendance vers la vue, le contrôleur ou JavaFX.
 */
public class Partie implements PartieModel {
    private final Sujet<EtatPartie> sujet = new Sujet<>();
    private final Configuration configuration;
    private final GenerateurCombinaison generateur;
    /**
     * Combinaison à deviner. Gardée ici et non dans {@link #etat} : l'état publié la cache tant que la partie dure.
     */
    private Combinaison secrete = Combinaison.vide();
    private EtatPartie etat = EtatPartie.initial();

    /**
     * Crée une partie non commencée.
     *
     * @param configuration paramètres de la partie
     * @param generateur    source de la combinaison secrète à chaque lancement
     * @throws NullPointerException si l'un des arguments est {@code null}
     */
    public Partie(Configuration configuration, GenerateurCombinaison generateur) {
        this.configuration = Objects.requireNonNull(configuration);
        this.generateur = Objects.requireNonNull(generateur);
    }

    @Override
    public void lancer(OptionalLong graine) {
        secrete = genererSecrete(graine);
        changerEtat(EtatPartie.nouvellePartie());
    }

    @Override
    public void poserCouleur(Couleur couleur) {
        Objects.requireNonNull(couleur);
        verifierEnCours();
        if (estComplete(etat.proposition())) {
            throw new IllegalStateException("Impossible de poser une couleur : la proposition est déjà complète");
        }
        changerEtat(etat.avecProposition(etat.proposition().avec(couleur)));
    }

    @Override
    public void retirerCouleur() {
        verifierEnCours();
        if (etat.proposition().estVide()) {
            throw new IllegalStateException("Impossible de retirer une couleur : la proposition est vide");
        }
        changerEtat(etat.avecProposition(etat.proposition().sansDernierPion()));
    }

    @Override
    public void validerTentative() {
        verifierEnCours();
        Combinaison proposition = etat.proposition();
        if (!estComplete(proposition)) {
            throw new IllegalStateException("Impossible de valider : la proposition est incomplète");
        }

        List<Combinaison> tentatives = ajouter(etat.tentatives(), proposition);
        Statut statut = statutApres(proposition, tentatives.size());
        changerEtat(new EtatPartie(
            statut,
            statut.estTerminee() ? secrete : Combinaison.vide(),
            tentatives,
            ajouter(etat.indices(), Indice.evaluer(proposition, secrete)),
            Combinaison.vide()
        ));
    }

    @Override
    public EtatPartie getEtat() {
        return etat;
    }

    @Override
    public void ajouterObservateur(Observateur<? super EtatPartie> observateur) {
        sujet.ajouterObservateur(observateur);
    }

    @Override
    public void retirerObservateur(Observateur<? super EtatPartie> observateur) {
        sujet.retirerObservateur(observateur);
    }

    /**
     * Demande une combinaison secrète au générateur et vérifie qu'elle respecte la configuration,
     * pour que le problème soit signalé au lancement et non à la première tentative.
     *
     * @param graine graine transmise au générateur
     * @return la combinaison secrète
     * @throws IllegalStateException si le générateur ne renvoie pas une combinaison de la longueur demandée
     */
    private Combinaison genererSecrete(OptionalLong graine) {
        Combinaison generee = generateur.generer(configuration.longueur(), graine);
        if (generee == null || generee.taille() != configuration.longueur()) {
            throw new IllegalStateException("le générateur doit renvoyer une combinaison de "
                    + configuration.longueur() + " pions");
        }
        return generee;
    }

    /**
     * Garantit qu'une action de jeu n'est possible que pendant la partie.
     *
     * @throws IllegalStateException si la partie n'est pas {@link Statut#EN_COURS}
     */
    private void verifierEnCours() {
        if (etat.statut() != Statut.EN_COURS) {
            throw new IllegalStateException("Action impossible : la partie n'est pas en cours");
        }
    }

    /**
     * Indique si une proposition a autant de pions que la combinaison secrète.
     *
     * @param proposition proposition à examiner
     * @return {@code true} si aucune case n'est libre
     */
    private boolean estComplete(Combinaison proposition) {
        return proposition.taille() == configuration.longueur();
    }

    /**
     * Détermine l'étape de la partie après une tentative.
     *
     * @param proposition  proposition qui vient d'être validée
     * @param nbTentatives nombre de tentatives jouées, celle-ci comprise
     * @return {@link Statut#GAGNEE} si la secrète est trouvée, {@link Statut#PERDUE} si les essais
     *         sont épuisés, {@link Statut#EN_COURS} sinon
     */
    private Statut statutApres(Combinaison proposition, int nbTentatives) {
        if (proposition.equals(secrete)) {
            return Statut.GAGNEE;
        }
        if (nbTentatives >= configuration.nbEssais()) {
            return Statut.PERDUE;
        }
        return Statut.EN_COURS;
    }

    /**
     * Crée une nouvelle liste avec un élément ajouté à la fin ; la liste reçue reste inchangée.
     *
     * @param liste   liste d'origine
     * @param element élément à ajouter
     * @param <T>     type des éléments
     * @return une liste immuable contenant les éléments d'origine puis {@code element}
     */
    private static <T> List<T> ajouter(List<T> liste, T element) {
        return Stream.concat(liste.stream(), Stream.of(element)).toList();
    }

    /**
     * Remplace l'état courant et le notifie. Point unique de changement d'état :
     * garantit qu'aucune modification n'échappe aux observateurs.
     *
     * @param nouvelEtat état qui remplace l'état courant
     */
    private void changerEtat(EtatPartie nouvelEtat) {
        etat = nouvelEtat;
        sujet.notifier(etat);
    }
}
