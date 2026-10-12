package com.mastermind.observer;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implémentation réutilisable d'{@link Observable}, à utiliser par composition :
 * une classe observable délègue à un {@code Sujet} au lieu d'en hériter.
 *
 * @param <T> type de la donnée transmise aux observateurs
 */
public final class Sujet<T> implements Observable<T> {
    /**
     * Abonnés dans l'ordre d'inscription. Copie à l'écriture : un observateur
     * peut se désabonner pendant une notification sans la perturber.
     */
    private final List<Observateur<? super T>> observateurs = new CopyOnWriteArrayList<>();

    @Override
    public void ajouterObservateur(Observateur<? super T> observateur) {
        observateurs.add(Objects.requireNonNull(observateur));
    }

    @Override
    public void retirerObservateur(Observateur<? super T> observateur) {
        observateurs.remove(observateur);
    }

    /**
     * Transmet une donnée à chaque observateur, dans l'ordre d'abonnement.
     *
     * @param donnee donnée à transmettre
     */
    public void notifier(T donnee) {
        observateurs.forEach(o -> o.mettreAJour(donnee));
    }
}
