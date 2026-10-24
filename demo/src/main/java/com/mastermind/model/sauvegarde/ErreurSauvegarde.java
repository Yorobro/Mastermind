package com.mastermind.model.sauvegarde;

/**
 * Échec d'une sauvegarde ou d'un chargement : emplacement inaccessible, fichier illisible ou contenu invalide.
 * Exception vérifiée : l'appelant doit prévoir ce cas, qui arrive en usage normal.
 * Son message est destiné au joueur.
 */
public class ErreurSauvegarde extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Crée une erreur sans cause technique sous-jacente.
     *
     * @param message explication destinée au joueur
     */
    public ErreurSauvegarde(String message) {
        super(message);
    }

    /**
     * Crée une erreur provoquée par un problème technique.
     *
     * @param message explication destinée au joueur
     * @param cause   problème d'origine
     */
    public ErreurSauvegarde(String message, Throwable cause) {
        super(message, cause);
    }
}
