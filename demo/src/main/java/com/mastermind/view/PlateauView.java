package com.mastermind.view;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.combinaison.Indice;
import com.mastermind.model.partie.Configuration;
import com.mastermind.model.partie.EtatPartie;
import com.mastermind.model.partie.Statut;
import com.mastermind.view.component.atoms.Bouton;
import com.mastermind.view.component.atoms.BoutonCouleur;
import com.mastermind.view.component.atoms.StatutPartie;
import com.mastermind.view.component.molecules.LignePions;
import com.mastermind.view.component.organisms.LigneTentative;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Écran de jeu : plateau, palette et actions sur la tentative ; se met à jour à chaque notification du modèle.
 * Ne connaît ni le contrôleur concret ni le modèle concret. En fin de partie, il propose de revenir au menu
 * et le signale, sans savoir comment l'accueil est affiché.
 */
public final class PlateauView extends VBox implements VuePartie {
    private static final Runnable AUCUNE_REACTION = () -> { };

    private final StatutPartie statut = new StatutPartie();
    private final VBox plateauJeu = new VBox();
    private final DialoguesFichier dialogues;
    private final HBox palette;
    private final Bouton boutonRetirer;
    private final Bouton boutonValider;
    private final Bouton boutonSauvegarder;
    private final Bouton boutonRetourMenu;
    private Runnable surRetourMenu = AUCUNE_REACTION;

    /**
     * Dimensions du plateau actuellement dessiné ; {@code null} avant le premier affichage.
     */
    private Configuration configurationAffichee;
    private LignePions ligneSecrete;
    /**
     * Index 0 = premier essai, affiché tout en haut, juste sous la ligne secrète.
     */
    private List<LigneTentative> tentatives = List.of();

    /**
     * Construit l'écran de jeu : zone de jeu, statut, palette et actions.
     * La zone de jeu est dessinée au premier {@link #mettreAJour(EtatPartie)}, aux dimensions de l'état reçu.
     *
     * @param actions   destinataire des actions du joueur
     * @param dialogues dialogues de choix du fichier de sauvegarde
     * @throws NullPointerException si l'un des arguments est {@code null}
     */
    public PlateauView(ActionsPartie actions, DialoguesFichier dialogues) {
        Objects.requireNonNull(actions);
        this.dialogues = Objects.requireNonNull(dialogues);
        getStyleClass().add("plateau");
        plateauJeu.getStyleClass().add("plateau-jeu");
        palette = creerPalette(actions);
        boutonRetirer = new Bouton("Retirer", actions::retirerCouleur);
        boutonValider = new Bouton("Valider", actions::valider);
        boutonSauvegarder = new Bouton("Sauvegarder", () -> dialogues.sauvegarder(fenetre(), actions::sauvegarder));
        boutonRetourMenu = new Bouton("Retour au menu", () -> surRetourMenu.run());
        boutonRetourMenu.managedProperty().bind(boutonRetourMenu.visibleProperty());
        getChildren().addAll(plateauJeu, statut, palette, creerBoutonsAction());
    }

    /**
     * Définit la réaction au bouton « Retour au menu », proposé une fois la partie terminée.
     *
     * @param reaction exécutée quand le joueur demande à revenir au menu
     * @throws NullPointerException si {@code reaction} est {@code null}
     */
    public void setOnRetourMenu(Runnable reaction) {
        surRetourMenu = Objects.requireNonNull(reaction);
    }

    @Override
    public void mettreAJour(EtatPartie etat) {
        adapterPlateau(etat.configuration());
        statut.afficher(etat.statut());
        ligneSecrete.afficher(etat.secrete().couleurs());
        afficherTentatives(etat);
        activerActionsPossibles(etat);
    }

    /**
     * Redessine la ligne secrète et les lignes de tentatives quand les dimensions changent,
     * par exemple quand une partie est lancée avec une autre difficulté.
     *
     * @param configuration dimensions de la partie à afficher
     */
    private void adapterPlateau(Configuration configuration) {
        if (configuration.equals(configurationAffichee)) {
            return;
        }
        configurationAffichee = configuration;
        ligneSecrete = creerLigneSecrete(configuration);
        tentatives = creerTentatives(configuration);

        VBox lignes = new VBox();
        lignes.getStyleClass().add("tentatives");
        lignes.getChildren().addAll(tentatives);
        plateauJeu.getChildren().setAll(ligneSecrete, lignes);
    }

    /**
     * Affiche les tentatives validées avec leur indice, puis la tentative en cours de saisie
     * sur la ligne suivante ; les lignes restantes sont vidées.
     *
     * @param etat état à afficher
     */
    private void afficherTentatives(EtatPartie etat) {
        int nbJouees = etat.tentatives().size();
        for (int i = 0; i < tentatives.size(); i++) {
            LigneTentative ligne = tentatives.get(i);
            if (i < nbJouees) {
                Indice indice = etat.indices().get(i);
                ligne.afficher(etat.tentatives().get(i).couleurs(), indice.bienPlaces(), indice.malPlaces());
            } else if (i == nbJouees) {
                ligne.afficherProposition(etat.proposition().couleurs());
            } else {
                ligne.vider();
            }
        }
    }

    /**
     * Active seulement les actions que le modèle accepte dans cet état : le joueur ne peut
     * déclencher aucune action refusée. Le retour au menu n'apparaît qu'en fin de partie.
     *
     * @param etat état courant de la partie
     */
    private void activerActionsPossibles(EtatPartie etat) {
        boolean enCours = etat.statut() == Statut.EN_COURS;
        int longueur = etat.configuration().longueur();
        int nbPionsPoses = etat.proposition().taille();
        palette.setDisable(!enCours || nbPionsPoses == longueur);
        boutonRetirer.setDisable(!enCours || nbPionsPoses == 0);
        boutonValider.setDisable(!enCours || nbPionsPoses < longueur);
        boutonSauvegarder.setDisable(etat.statut() == Statut.NON_COMMENCEE);
        boutonRetourMenu.setVisible(etat.statut().estTerminee());
    }

    /**
     * Renvoie la fenêtre qui contient le plateau, parent des dialogues.
     *
     * @return la fenêtre, ou {@code null} si le plateau n'est pas affiché
     */
    private Window fenetre() {
        return getScene() == null ? null : getScene().getWindow();
    }

    /**
     * Crée la ligne qui affiche la combinaison secrète.
     *
     * @param configuration fournit le nombre de pions
     * @return la ligne secrète, vide
     */
    private static LignePions creerLigneSecrete(Configuration configuration) {
        LignePions ligne = new LignePions(configuration.longueur());
        ligne.getStyleClass().add("ligne-secrete");
        return ligne;
    }

    /**
     * Crée une ligne vide par essai autorisé.
     *
     * @param configuration fournit le nombre d'essais et de pions par ligne
     * @return les lignes, de la première tentative à la dernière
     */
    private static List<LigneTentative> creerTentatives(Configuration configuration) {
        return Stream.generate(() -> new LigneTentative(configuration.longueur()))
                .limit(configuration.nbEssais())
                .toList();
    }

    /**
     * Assemble la palette : un bouton par couleur.
     *
     * @param actions destinataire des actions du joueur
     * @return la palette de couleurs
     */
    private static HBox creerPalette(ActionsPartie actions) {
        HBox palette = new HBox();
        palette.getStyleClass().add("palette");
        for (Couleur couleur : Couleur.values()) {
            palette.getChildren().add(new BoutonCouleur(couleur, () -> actions.choisirCouleur(couleur)));
        }
        return palette;
    }

    /**
     * Assemble la barre d'actions : retirer et valider la tentative, sauvegarder la partie, revenir au menu.
     *
     * @return la barre d'actions
     */
    private HBox creerBoutonsAction() {
        HBox actionsTentative = new HBox(boutonRetirer, boutonValider, boutonSauvegarder, boutonRetourMenu);
        actionsTentative.getStyleClass().add("actions-tentative");
        return actionsTentative;
    }
}
