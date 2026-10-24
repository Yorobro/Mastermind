package com.mastermind.view;

import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Window;

/**
 * Fenêtres de dialogue de la sauvegarde : choix du fichier et affichage des erreurs.
 * Retient le dernier dossier utilisé pour y rouvrir le sélecteur.
 */
final class DialoguesFichier {
    private static final String NOM_PAR_DEFAUT = "save.json";
    private static final FileChooser.ExtensionFilter FILTRE_JSON =
            new FileChooser.ExtensionFilter("Partie Mastermind (*.json)", "*.json");

    /**
     * Dossier du dernier fichier choisi ; {@code null} tant qu'aucun fichier n'a été choisi.
     */
    private File dernierDossier;

    /**
     * Demande où enregistrer la partie, en proposant {@value #NOM_PAR_DEFAUT}.
     *
     * @param parent fenêtre au-dessus de laquelle s'ouvre le dialogue
     * @return le fichier choisi, ou vide si le joueur a annulé
     */
    Optional<Path> demanderEmplacementSauvegarde(Window parent) {
        FileChooser selecteur = creerSelecteur("Sauvegarder la partie");
        selecteur.setInitialFileName(NOM_PAR_DEFAUT);
        return retenir(selecteur.showSaveDialog(parent));
    }

    /**
     * Demande quelle sauvegarde charger.
     *
     * @param parent fenêtre au-dessus de laquelle s'ouvre le dialogue
     * @return le fichier choisi, ou vide si le joueur a annulé
     */
    Optional<Path> demanderFichierACharger(Window parent) {
        return retenir(creerSelecteur("Charger une partie").showOpenDialog(parent));
    }

    /**
     * Affiche une erreur de sauvegarde ou de chargement et attend que le joueur la ferme.
     *
     * @param parent fenêtre au-dessus de laquelle s'ouvre le dialogue
     * @param titre  résumé de l'opération qui a échoué
     * @param erreur erreur dont le message est affiché
     */
    void afficherErreur(Window parent, String titre, ErreurSauvegarde erreur) {
        Alert alerte = new Alert(Alert.AlertType.ERROR, erreur.getMessage(), ButtonType.OK);
        alerte.initOwner(parent);
        alerte.setHeaderText(titre);
        alerte.showAndWait();
    }

    /**
     * Crée un sélecteur limité aux fichiers JSON, ouvert dans le dernier dossier utilisé.
     *
     * @param titre titre de la fenêtre
     * @return le sélecteur
     */
    private FileChooser creerSelecteur(String titre) {
        FileChooser selecteur = new FileChooser();
        selecteur.setTitle(titre);
        selecteur.getExtensionFilters().add(FILTRE_JSON);
        if (dernierDossier != null && dernierDossier.isDirectory()) {
            selecteur.setInitialDirectory(dernierDossier);
        }
        return selecteur;
    }

    /**
     * Mémorise le dossier du fichier choisi.
     *
     * @param fichier fichier choisi, ou {@code null} si le joueur a annulé
     * @return le chemin du fichier, ou vide si le joueur a annulé
     */
    private Optional<Path> retenir(File fichier) {
        if (fichier == null) {
            return Optional.empty();
        }
        dernierDossier = fichier.getParentFile();
        return Optional.of(fichier.toPath());
    }
}
