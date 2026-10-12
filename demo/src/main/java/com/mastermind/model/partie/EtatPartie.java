package com.mastermind.model.partie;

import com.mastermind.model.combinaison.Combinaison;
import java.util.Objects;

/**
 * Instantané immuable de la partie, transmis aux observateurs.
 * La vue ne lit que ça : elle n'accède jamais directement à {@link Partie}.
 *
 * @param statut  étape courante de la partie
 * @param secrete combinaison à deviner ({@link Combinaison#vide()} avant le lancement)
 */
public record EtatPartie(Statut statut, Combinaison secrete) {
    /**
     * Vérifie que l'état est complet.
     *
     * @throws NullPointerException si {@code statut} ou {@code secrete} est {@code null}
     */
    public EtatPartie {
        Objects.requireNonNull(statut);
        Objects.requireNonNull(secrete);
    }

    /**
     * Crée l'état d'une partie pas encore lancée.
     *
     * @return un état {@link Statut#NON_COMMENCEE} avec une combinaison vide
     */
    public static EtatPartie initial() {
        return new EtatPartie(Statut.NON_COMMENCEE, Combinaison.vide());
    }
}
