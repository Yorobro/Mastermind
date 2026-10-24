package com.mastermind.model.partie;

/**
 * Cycle de vie d'une partie.
 */
public enum Statut {
    NON_COMMENCEE,
    EN_COURS,
    GAGNEE,
    PERDUE;

    /**
     * Indique si la partie est finie, qu'elle soit gagnée ou perdue.
     *
     * @return {@code true} pour {@link #GAGNEE} et {@link #PERDUE}
     */
    public boolean estTerminee() {
        return this == GAGNEE || this == PERDUE;
    }
}
