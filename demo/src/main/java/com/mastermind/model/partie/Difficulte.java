package com.mastermind.model.partie;

/**
 * Niveaux de difficulté proposés au joueur : chacun fixe le nombre d'essais autorisés,
 * la combinaison garde la longueur du Mastermind classique.
 */
public enum Difficulte {
    FACILE(14),
    DIFFICILE(8);

    private final int nbEssais;

    /**
     * Associe un nombre d'essais au niveau.
     *
     * @param nbEssais nombre de tentatives autorisées à ce niveau
     */
    Difficulte(int nbEssais) {
        this.nbEssais = nbEssais;
    }

    /**
     * Renvoie le niveau sélectionné tant que le joueur n'en a pas choisi d'autre.
     *
     * @return le niveau par défaut
     */
    public static Difficulte parDefaut() {
        return FACILE;
    }

    /**
     * Traduit le niveau en paramètres de partie.
     *
     * @return une configuration de longueur standard avec le nombre d'essais de ce niveau
     */
    public Configuration configuration() {
        return new Configuration(Configuration.STANDARD.longueur(), nbEssais);
    }
}
