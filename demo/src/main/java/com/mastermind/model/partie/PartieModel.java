package com.mastermind.model.partie;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.observer.Observable;
import java.util.OptionalLong;

/**
 * Contrat du modèle : le contrôleur dépend de cette abstraction,
 * jamais de l'implémentation concrète (DIP).
 * Toute modification d'état est notifiée aux observateurs via un {@link EtatPartie}.
 */
public interface PartieModel extends Observable<EtatPartie> {
    /**
     * Démarre une nouvelle partie en repartant de zéro : rien de la partie précédente n'est conservé.
     *
     * @param configuration dimensions de la nouvelle partie (longueur, nombre d'essais)
     * @param graine        graine de la combinaison secrète, ou vide pour une combinaison imprévisible
     * @throws NullPointerException  si {@code configuration} est {@code null}
     * @throws IllegalStateException si la combinaison secrète obtenue n'a pas la longueur attendue ;
     *                               l'état de la partie reste alors inchangé
     */
    void lancer(Configuration configuration, OptionalLong graine);

    /**
     * Pose une couleur dans la prochaine case libre de la tentative en cours.
     *
     * @param couleur couleur à poser
     * @throws NullPointerException  si {@code couleur} est {@code null}
     * @throws IllegalStateException si la partie n'est pas en cours ou si la proposition est déjà complète
     */
    void poserCouleur(Couleur couleur);

    /**
     * Retire la dernière couleur posée dans la tentative en cours, pour corriger avant de valider.
     *
     * @throws IllegalStateException si la partie n'est pas en cours ou si la proposition est vide
     */
    void retirerCouleur();

    /**
     * Valide la tentative en cours : l'ajoute aux tentatives avec son indice, puis vide la proposition.
     * La partie est gagnée si la combinaison secrète est trouvée, perdue si c'était le dernier essai ;
     * dans les deux cas, la combinaison secrète est alors révélée.
     *
     * @throws IllegalStateException si la partie n'est pas en cours ou si la proposition est incomplète
     */
    void validerTentative();

    /**
     * Renvoie l'état courant, celui transmis lors de la dernière notification.
     *
     * @return instantané immuable de la partie
     */
    EtatPartie getEtat();
}
