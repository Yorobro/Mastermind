package com.mastermind;

import com.mastermind.controller.PartieController;
import com.mastermind.data.FichierJsonPartie;
import com.mastermind.model.generation.GenerateurAleatoire;
import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.partie.Partie;
import com.mastermind.model.partie.PartieModel;
import com.mastermind.view.DialoguesFichier;
import com.mastermind.view.FenetrePrincipale;
import com.mastermind.view.MenuView;
import com.mastermind.view.PlateauView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Racine de composition : seul endroit qui instancie les classes concrètes
 * et les relie entre elles, navigation entre les écrans comprise.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        PartieModel model = new Partie(Difficulte.parDefaut().configuration(), new GenerateurAleatoire());
        FichierJsonPartie fichiersDeSauvegarde = new FichierJsonPartie();
        PartieController controller = new PartieController(model, fichiersDeSauvegarde, fichiersDeSauvegarde);

        DialoguesFichier dialogues = new DialoguesFichier();
        MenuView menu = new MenuView(controller, dialogues);
        PlateauView plateau = new PlateauView(controller, dialogues);
        model.ajouterObservateur(plateau);
        plateau.mettreAJour(model.getEtat());

        FenetrePrincipale fenetre = new FenetrePrincipale(stage, menu, plateau);
        menu.setOnPartieDemarree(fenetre::afficherPartie);
        menu.setOnQuitter(Platform::exit);
        plateau.setOnRetourMenu(fenetre::afficherMenu);
        fenetre.ouvrir();
    }

    /**
     * Lance l'application JavaFX.
     *
     * @param args arguments de la ligne de commande, transmis à JavaFX
     */
    public static void main(String[] args) {
        launch(args);
    }
}
