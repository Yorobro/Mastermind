package com.mastermind.view;

import com.mastermind.view.component.atoms.Bouton;
import com.mastermind.view.component.atoms.ChoixDifficulte;
import java.util.Objects;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Écran d'accueil : choix de la difficulté, lancement d'une partie, chargement d'une sauvegarde et sortie.
 * Il ignore quel écran vient ensuite : il signale seulement qu'une partie a démarré ou que le joueur veut quitter.
 */
public final class MenuView extends VBox {
    private static final Runnable AUCUNE_REACTION = () -> { };

    private final ChoixDifficulte choixDifficulte = new ChoixDifficulte();
    private final DialoguesFichier dialogues;
    private Runnable surPartieDemarree = AUCUNE_REACTION;
    private Runnable surQuitter = AUCUNE_REACTION;

    /**
     * Construit l'écran d'accueil.
     *
     * @param actions   destinataire des actions du joueur
     * @param dialogues dialogues de choix du fichier à charger
     * @throws NullPointerException si l'un des arguments est {@code null}
     */
    public MenuView(ActionsMenu actions, DialoguesFichier dialogues) {
        Objects.requireNonNull(actions);
        this.dialogues = Objects.requireNonNull(dialogues);
        getStyleClass().add("menu");
        Label titre = new Label("Mastermind");
        titre.getStyleClass().add("titre");
        getChildren().addAll(
                titre,
                choixDifficulte,
                new Bouton("Lancer la partie", () -> lancer(actions)),
                new Bouton("Charger une partie", () -> charger(actions)),
                new Bouton("Quitter", () -> surQuitter.run())
        );
    }

    /**
     * Définit la réaction à une partie qui vient de démarrer, lancée ou chargée.
     *
     * @param reaction exécutée une fois la partie prête à être jouée
     * @throws NullPointerException si {@code reaction} est {@code null}
     */
    public void setOnPartieDemarree(Runnable reaction) {
        surPartieDemarree = Objects.requireNonNull(reaction);
    }

    /**
     * Définit la réaction au bouton « Quitter ».
     *
     * @param reaction exécutée quand le joueur demande à quitter
     * @throws NullPointerException si {@code reaction} est {@code null}
     */
    public void setOnQuitter(Runnable reaction) {
        surQuitter = Objects.requireNonNull(reaction);
    }

    /**
     * Lance une partie à la difficulté choisie, puis signale qu'elle a démarré.
     *
     * @param actions destinataire des actions du joueur
     */
    private void lancer(ActionsMenu actions) {
        actions.lancerPartie(choixDifficulte.getValue());
        surPartieDemarree.run();
    }

    /**
     * Fait choisir une sauvegarde puis la charge ; signale le démarrage seulement si le chargement a réussi.
     *
     * @param actions destinataire des actions du joueur
     */
    private void charger(ActionsMenu actions) {
        if (dialogues.charger(fenetre(), actions::charger)) {
            surPartieDemarree.run();
        }
    }

    /**
     * Renvoie la fenêtre qui contient l'écran, parent des dialogues.
     *
     * @return la fenêtre, ou {@code null} si l'écran n'est pas affiché
     */
    private Window fenetre() {
        return getScene() == null ? null : getScene().getWindow();
    }
}
