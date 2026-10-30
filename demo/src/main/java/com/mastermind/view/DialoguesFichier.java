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
 * Dialogues de la sauvegarde, partagés par l'accueil et le plateau : choix du fichier, exécution de l'opération
 * et affichage de l'erreur éventuelle. Retient le dernier dossier utilisé, d'un écran à l'autre.
 */
public final class DialoguesFichier {
    private static final String NOM_PAR_DEFAUT = "save.json";
    private static final FileChooser.ExtensionFilter FILTRE_JSON =
            new FileChooser.ExtensionFilter("Partie Mastermind (*.json)", "*.json");

    /**
     * Dossier du dernier fichier choisi ; {@code null} tant qu'aucun fichier n'a été choisi.
     */
    private File dernierDossier;

    /**
     * Crée les dialogues ; le premier sélecteur s'ouvre dans le dossier par défaut du système.
     */
    public DialoguesFichier() {
    }

    /**
     * Demande où enregistrer la partie, en proposant {@value #NOM_PAR_DEFAUT}, puis l'enregistre.
     *
     * @param parent     fenêtre au-dessus de laquelle s'ouvrent les dialogues
     * @param sauvegarde opération qui enregistre la partie dans le fichier choisi
     * @return {@code true} si la partie a été enregistrée ; {@code false} si le joueur a annulé ou si l'erreur
     *         lui a été affichée
     */
    boolean sauvegarder(Window parent, OperationFichier sauvegarde) {
        FileChooser selecteur = creerSelecteur("Sauvegarder la partie");
        selecteur.setInitialFileName(NOM_PAR_DEFAUT);
        return retenir(selecteur.showSaveDialog(parent))
                .map(fichier -> appliquer(parent, fichier, sauvegarde, "Sauvegarde impossible"))
                .orElse(false);
    }

    /**
     * Demande quelle sauvegarde reprendre, puis la charge.
     *
     * @param parent     fenêtre au-dessus de laquelle s'ouvrent les dialogues
     * @param chargement opération qui reprend la partie enregistrée dans le fichier choisi
     * @return {@code true} si la partie a été chargée ; {@code false} si le joueur a annulé ou si l'erreur
     *         lui a été affichée
     */
    boolean charger(Window parent, OperationFichier chargement) {
        return retenir(creerSelecteur("Charger une partie").showOpenDialog(parent))
                .map(fichier -> appliquer(parent, fichier, chargement, "Chargement impossible"))
                .orElse(false);
    }

    /**
     * Exécute l'opération sur le fichier ; en cas d'échec, explique l'erreur au joueur.
     *
     * @param parent      fenêtre au-dessus de laquelle s'ouvre le message d'erreur
     * @param fichier     fichier choisi
     * @param operation   sauvegarde ou chargement
     * @param titreErreur résumé affiché si l'opération échoue
     * @return {@code true} si l'opération a réussi
     */
    private static boolean appliquer(Window parent, Path fichier, OperationFichier operation, String titreErreur) {
        try {
            operation.executer(fichier);
            return true;
        } catch (ErreurSauvegarde erreur) {
            afficherErreur(parent, titreErreur, erreur);
            return false;
        }
    }

    /**
     * Affiche une erreur de sauvegarde ou de chargement et attend que le joueur la ferme.
     *
     * @param parent fenêtre au-dessus de laquelle s'ouvre le dialogue
     * @param titre  résumé de l'opération qui a échoué
     * @param erreur erreur dont le message est affiché
     */
    private static void afficherErreur(Window parent, String titre, ErreurSauvegarde erreur) {
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
