package com.mastermind.view;

import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.combinaison.Indice;
import com.mastermind.model.partie.Configuration;
import com.mastermind.model.partie.EtatPartie;
import com.mastermind.model.partie.Statut;
import com.mastermind.view.component.atoms.Bouton;
import com.mastermind.view.component.atoms.BoutonCouleur;
import com.mastermind.view.component.atoms.ChampTexte;
import com.mastermind.view.component.atoms.ChoixDifficulte;
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

    private final StatutPartie statut = new StatutPartie();
    private final ChampTexte champGraine = new ChampTexte("Graine (optionnelle)", t -> t.matches(FORMAT_GRAINE));
    private final ChoixDifficulte choixDifficulte = new ChoixDifficulte();
    private final VBox plateauJeu = new VBox();
    private final HBox palette;
    private final Bouton boutonEffacer;
    private final Bouton boutonValider;

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
     * Construit le plateau : zone de jeu, statut, commandes, palette et actions.
     * La zone de jeu est dessinée au premier {@link #mettreAJour(EtatPartie)}, aux dimensions de l'état reçu.
     *
     * @param actions destinataire des actions du joueur
     */
    public PlateauView(ActionsJoueur actions) {
        getStylesheets().add(FEUILLE_DE_STYLE);
        getStyleClass().add("plateau");
        plateauJeu.getStyleClass().add("plateau-jeu");
        palette = creerPalette(actions);
        boutonEffacer = new Bouton("Effacer", actions::effacer);
        boutonValider = new Bouton("Valider", actions::valider);
        getChildren().addAll(
                plateauJeu,
                statut,
                creerCommandes(actions),
                palette,
                creerBoutonsAction(actions)
        );
    }

    @Override
    public void mettreAJour(EtatPartie etat) {
        adapterPlateau(etat.configuration());
        statut.afficher(etat.statut());
        champGraine.setVisible(etat.statut() != Statut.EN_COURS);
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
        ajusterFenetre();
    }

    /**
     * Redimensionne la fenêtre à son contenu, pour que toutes les lignes restent visibles.
     */
    private void ajusterFenetre() {
        Window fenetre = getScene() == null ? null : getScene().getWindow();
        if (fenetre != null) {
            fenetre.sizeToScene();
        }
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
     * déclencher aucune action refusée.
     *
     * @param etat état courant de la partie
     */
    private void activerActionsPossibles(EtatPartie etat) {
        boolean enCours = etat.statut() == Statut.EN_COURS;
        int longueur = etat.configuration().longueur();
        int nbPionsPoses = etat.proposition().taille();
        palette.setDisable(!enCours || nbPionsPoses == longueur);
        boutonEffacer.setDisable(!enCours || nbPionsPoses == 0);
        boutonValider.setDisable(!enCours || nbPionsPoses < longueur);
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
     * Assemble la barre de commandes : champ de graine, choix de la difficulté et bouton de lancement,
     * reliés à {@link ActionsJoueur#lancerPartie(String, com.mastermind.model.partie.Difficulte)}.
     *
     * @param actions destinataire des actions du joueur
     * @return la barre de commandes
     */
    private HBox creerCommandes(ActionsJoueur actions) {
        champGraine.setOnValider(graine ->
                actions.lancerPartie(graine, choixDifficulte.getValue())
        );
        Bouton boutonLancer = new Bouton("Lancer partie", () ->
                actions.lancerPartie(champGraine.getText(), choixDifficulte.getValue())
        );
        HBox commandes = new HBox(champGraine, choixDifficulte, boutonLancer);
        commandes.getStyleClass().add("commandes");
        return commandes;
    }

    /**
     * Assemble la palette : un bouton par couleur.
     *
     * @param actions destinataire des actions du joueur
     * @return la palette de couleurs
     */
    private static HBox creerPalette(ActionsJoueur actions) {
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
     * @param actions destinataire des actions du joueur
     * @return la barre d'actions
     */
    private HBox creerBoutonsAction(ActionsJoueur actions) {
        HBox actionsTentative = new HBox(
                boutonEffacer,
                boutonValider,
                new Bouton("Sauvegarder", actions::sauvegarder),
                new Bouton("Charger", actions::charger)
        );
        actionsTentative.getStyleClass().add("actions-tentative");
        return actionsTentative;
    }


}
