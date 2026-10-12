package com.mastermind.observer;

/**
 * Reçoit les notifications d'un {@link Observable}.
 *
 * @param <T> type de la donnée transmise à chaque notification
 */
@FunctionalInterface
public interface Observateur<T> {
    /**
     * Reçoit le nouvel état de l'objet observé, après chacun de ses changements.
     *
     * @param donnee donnée transmise par l'objet observé
     */
    void mettreAJour(T donnee);
}
