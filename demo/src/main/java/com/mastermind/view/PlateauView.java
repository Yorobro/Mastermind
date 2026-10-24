package com.mastermind.view;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.*;
import com.mastermind.view.component.atoms.Bouton;
import com.mastermind.view.component.atoms.BoutonCouleur;
import com.mastermind.view.component.atoms.ChampTexte;
import com.mastermind.view.component.atoms.StatutPartie;
import com.mastermind.view.component.molecules.LignePions;
import com.mastermind.view.component.organisms.LigneTentative;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ComboBox;

/**
 * Vue JavaFX : assemble les composants et se met à jour à chaque notification du modèle.
 * Ne connaît ni le contrôleur concret ni le modèle concret.
 */
public final class PlateauView extends VBox implements VuePartie {
    private static final String FEUILLE_DE_STYLE = Objects.requireNonNull(
            PlateauView.class.getResource("mastermind.css"), "mastermind.css introuvable").toExternalForm();
    /**
     * Chiffres uniquement, 18 au plus : toujours convertible en {@code long}.
     */
    private static final String FORMAT_GRAINE = "\\d{0,18}";
    /**
     * Couleurs proposées dans la palette ; chacune correspond à une classe CSS {@code couleur-<nom>}.
     */
    private static final List<String> COULEURS = List.of("rouge", "bleu", "vert", "jaune", "orange", "violet");

    private final StatutPartie statut = new StatutPartie();
    private final ChampTexte champGraine = new ChampTexte("Graine (optionnelle)", t -> t.matches(FORMAT_GRAINE));
    private final LignePions ligneSecrete;

    /**
     * Mis en place du choix de la difficulté.
     */
    private final ComboBox<Difficulté> comboDifficulte = new ComboBox<>(
            FXCollections.observableArrayList(
                    new Facile(),
                    new Difficile()
            )
    );
    /**
     * Index 0 = premier essai, affiché tout en bas du plateau.
     */
    private final List<LigneTentative> tentatives;

    /**
     * Construit le plateau vide : ligne secrète, tentatives, statut, commandes, palette et actions.
     *
     * @param actions       destinataire des actions du joueur
     * @param configuration dimensions du plateau (longueur des lignes, nombre d'essais)
     */
    public PlateauView(ActionsJoueur actions, Configuration configuration) {
        getStylesheets().add(FEUILLE_DE_STYLE);
        getStyleClass().add("plateau");
        ligneSecrete = creerLigneSecrete(configuration);
        tentatives = creerTentatives(configuration);
        getChildren().addAll(
                creerPlateauJeu(),
                statut,
                creerCommandes(actions),
                creerPalette(actions),
                creerBoutonsAction(actions)
        );
    }

    @Override
    public void mettreAJour(EtatPartie etat) {
        statut.afficher(etat.statut());
        champGraine.setVisible(etat.statut() == Statut.NON_COMMENCEE);
        ligneSecrete.afficher(etat.secrete().couleurs());
        tentatives.forEach(LigneTentative::vider);
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
     * Assemble la zone de jeu : la ligne secrète en haut, puis les tentatives de la dernière à la première.
     *
     * @return la zone de jeu
     */
    private VBox creerPlateauJeu() {
        VBox lignes = new VBox();
        lignes.getStyleClass().add("tentatives");
        lignes.getChildren().addAll(tentatives.reversed());

        VBox plateauJeu = new VBox(ligneSecrete, lignes);
        plateauJeu.getStyleClass().add("plateau-jeu");
        return plateauJeu;
    }

    /**
     * Assemble la barre de commandes : champ de graine et bouton de lancement,
     * tous deux reliés à {@link ActionsJoueur#lancerPartie(String,com.mastermind.model.partie.Difficulté)}.
     *
     * @param actions destinataire des actions du joueur
     * @return la barre de commandes
     */
    private HBox creerCommandes(ActionsJoueur actions) {
        champGraine.setOnValider(graine ->
                actions.lancerPartie(graine, comboDifficulte.getValue())
        );
        Bouton boutonLancer = new Bouton("Lancer partie", () ->
                actions.lancerPartie(champGraine.getText(), comboDifficulte.getValue())
        );
        HBox commandes = new HBox(champGraine, comboDifficulte, boutonLancer);
        commandes.getStyleClass().add("commandes");
        return commandes;
    }

    /**
     * Assemble la palette : un bouton par couleur.
     *
     * @return la palette de couleurs
     */
    private HBox creerPalette(ActionsJoueur actions) {
        HBox palette = new HBox();
        palette.getStyleClass().add("palette");
        for (Couleur couleur : Couleur.values()) {
            palette.getChildren().add(new BoutonCouleur(couleur, () ->
                    actions.choisirCouleur(couleur)
            ));
        }
        return palette;
    }


    /**
     * Assemble la barre des actions sur la tentative en cours : effacer, valider et sauvegarder,
     * appel au contrôleur .
     *
     * @return la barre d'actions
     */
    private HBox creerBoutonsAction(ActionsJoueur actions) {
        HBox actionsTentative = new HBox(
                new Bouton("Effacer", actions::effacer),
                new Bouton("Valider", actions::valider),
                new Bouton("Sauvegarder", actions::sauvegarder),
                new Bouton("Charger", actions::charger)
        );
        actionsTentative.getStyleClass().add("actions-tentative");
        return actionsTentative;
    }


}