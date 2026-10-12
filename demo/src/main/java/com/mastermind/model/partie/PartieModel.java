package com.mastermind.model.partie;

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
     * @param graine graine de la combinaison secrète, ou vide pour une combinaison imprévisible
     */
    void lancer(OptionalLong graine);

    /**
     * Renvoie l'état courant, celui transmis lors de la dernière notification.
     *
     * @return instantané immuable de la partie
     */
    EtatPartie getEtat();
}
