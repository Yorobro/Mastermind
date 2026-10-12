package com.mastermind.observer;

/**
 * Objet auquel on peut s'abonner pour être notifié de ses changements.
 *
 * @param <T> type de la donnée transmise aux observateurs
 */
public interface Observable<T> {
    /**
     * Abonne un observateur : il sera notifié de chaque changement à venir.
     *
     * @param observateur observateur à abonner
     * @throws NullPointerException si {@code observateur} est {@code null}
     */
    void ajouterObservateur(Observateur<? super T> observateur);

    /**
     * Désabonne un observateur : il ne reçoit plus aucune notification.
     * Sans effet s'il n'était pas abonné.
     *
     * @param observateur observateur à désabonner
     */
    void retirerObservateur(Observateur<? super T> observateur);
}
