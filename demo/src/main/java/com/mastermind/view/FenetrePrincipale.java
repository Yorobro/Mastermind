package com.mastermind.view;

import java.util.Objects;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Fenêtre de l'application : affiche un écran à la fois, l'accueil ou la partie, et s'ajuste à chaque changement.
 * Elle ignore tout du jeu, et les écrans ne la connaissent pas : {@code Main} relie leurs événements à ses méthodes.
 */
public final class FenetrePrincipale {
    private static final String TITRE = "Mastermind";
    private static final String FEUILLE_DE_STYLE = Objects.requireNonNull(
            FenetrePrincipale.class.getResource("mastermind.css"), "mastermind.css introuvable").toExternalForm();

    private final Stage stage;
    private final Scene scene;
    private final Parent accueil;
    private final Parent partie;

    /**
     * Prépare la fenêtre avec ses deux écrans, sans l'afficher.
     *
     * @param stage   fenêtre fournie par JavaFX
     * @param accueil écran d'accueil
     * @param partie  écran de jeu
     * @throws NullPointerException si l'un des arguments est {@code null}
     */
    public FenetrePrincipale(Stage stage, Parent accueil, Parent partie) {
        this.stage = Objects.requireNonNull(stage);
        this.accueil = Objects.requireNonNull(accueil);
        this.partie = Objects.requireNonNull(partie);
        scene = new Scene(accueil);
        scene.getStylesheets().add(FEUILLE_DE_STYLE);
        stage.setTitle(TITRE);
        stage.setScene(scene);
    }

    /**
     * Affiche la fenêtre sur l'écran d'accueil.
     */
    public void ouvrir() {
        afficherMenu();
        stage.show();
    }

    /**
     * Remplace l'écran affiché par l'accueil.
     */
    public void afficherMenu() {
        afficher(accueil);
    }

    /**
     * Remplace l'écran affiché par la partie.
     */
    public void afficherPartie() {
        afficher(partie);
    }

    /**
     * Affiche un écran, adapte la taille de la fenêtre à son contenu et la recentre :
     * le plateau, plus haut que l'accueil, reste entièrement visible.
     *
     * @param ecran écran à afficher
     */
    private void afficher(Parent ecran) {
        scene.setRoot(ecran);
        stage.sizeToScene();
        stage.centerOnScreen();
    }
}
