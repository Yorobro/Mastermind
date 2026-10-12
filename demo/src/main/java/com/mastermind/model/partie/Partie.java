package com.mastermind.model.partie;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.generation.GenerateurCombinaison;
import com.mastermind.observer.Observateur;
import com.mastermind.observer.Sujet;
import java.util.Objects;
import java.util.OptionalLong;

/**
 * Modèle : état et règles d'une partie de Mastermind.
 * Aucune dépendance vers la vue, le contrôleur ou JavaFX.
 */
public class Partie implements PartieModel {
    private final Sujet<EtatPartie> sujet = new Sujet<>();
    private final Configuration configuration;
    private final GenerateurCombinaison generateur;
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
        Combinaison secrete = generateur.generer(configuration.longueur(), graine);
        changerEtat(new EtatPartie(Statut.EN_COURS, secrete));
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
