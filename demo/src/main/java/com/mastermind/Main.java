package com.mastermind;

import com.mastermind.controller.PartieController;
import com.mastermind.data.FichierJsonPartie;
import com.mastermind.model.generation.GenerateurAleatoire;
import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.partie.Partie;
import com.mastermind.model.partie.PartieModel;
import com.mastermind.view.PlateauView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Racine de composition : seul endroit qui instancie les classes concrètes
 * et les relie entre elles.
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        PartieModel model = new Partie(Difficulte.parDefaut().configuration(), new GenerateurAleatoire());
        FichierJsonPartie fichiersDeSauvegarde = new FichierJsonPartie();
        PartieController controller = new PartieController(model, fichiersDeSauvegarde, fichiersDeSauvegarde);
        PlateauView vue = new PlateauView(controller);

        model.ajouterObservateur(vue);
        vue.mettreAJour(model.getEtat());

        stage.setTitle("Mastermind");
        stage.setScene(new Scene(vue));
        stage.show();
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
