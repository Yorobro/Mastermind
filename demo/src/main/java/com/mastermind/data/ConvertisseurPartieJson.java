package com.mastermind.data;

import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Configuration;
import com.mastermind.model.partie.PartieSauvegardee;
import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import java.util.List;

/**
 * Traduit une partie sauvegardée entre le modèle et le format du fichier.
 * Ne lit ni n'écrit aucun fichier : c'est le rôle de {@link FichierJsonPartie}.
 */
final class ConvertisseurPartieJson {
    /**
     * Version du format produit ; un fichier d'une autre version est refusé.
     */
    static final int VERSION = 1;

    /**
     * Convertit une partie du modèle vers le format du fichier.
     *
     * @param partie partie à convertir
     * @return la partie au format du fichier
     */
    PartieJson versJson(PartieSauvegardee partie) {
        return new PartieJson(
            VERSION,
            partie.configuration().longueur(),
            partie.configuration().nbEssais(),
            versNoms(partie.secrete()),
            partie.tentatives().stream().map(ConvertisseurPartieJson::versNoms).toList(),
            versNoms(partie.proposition())
        );
    }

    /**
     * Reconstruit une partie du modèle à partir du contenu d'un fichier.
     *
     * @param json contenu lu dans le fichier
     * @return la partie reconstruite, aux dimensions cohérentes
     * @throws ErreurSauvegarde si la version n'est pas gérée, si un champ manque ou si une donnée est invalide
     */
    PartieSauvegardee depuisJson(PartieJson json) throws ErreurSauvegarde {
        if (json.version() != VERSION) {
            throw new ErreurSauvegarde("Version de sauvegarde non gérée : " + json.version());
        }
        try {
            return new PartieSauvegardee(
                new Configuration(json.longueur(), json.nbEssais()),
                versCombinaison(json.secrete()),
                versCombinaisons(json.tentatives()),
                versCombinaison(json.proposition())
            );
        } catch (IllegalArgumentException e) {
            throw new ErreurSauvegarde("Sauvegarde invalide : " + e.getMessage(), e);
        }
    }

    /**
     * Donne le nom de chaque couleur d'une combinaison.
     *
     * @param combinaison combinaison à convertir
     * @return les noms des couleurs, de gauche à droite
     */
    private static List<String> versNoms(Combinaison combinaison) {
        return combinaison.couleurs().stream().map(Couleur::name).toList();
    }

    /**
     * Reconstruit la liste des tentatives.
     *
     * @param tentatives noms des couleurs de chaque tentative
     * @return les tentatives
     * @throws IllegalArgumentException si la liste ou l'une de ses tentatives manque, ou si une couleur est invalide
     */
    private static List<Combinaison> versCombinaisons(List<List<String>> tentatives) {
        if (tentatives == null) {
            throw new IllegalArgumentException("liste des tentatives manquante");
        }
        return tentatives.stream().map(ConvertisseurPartieJson::versCombinaison).toList();
    }

    /**
     * Reconstruit une combinaison à partir des noms de ses couleurs.
     *
     * @param noms noms des couleurs, de gauche à droite
     * @return la combinaison
     * @throws IllegalArgumentException si la liste manque ou si une couleur est absente ou inconnue
     */
    private static Combinaison versCombinaison(List<String> noms) {
        if (noms == null) {
            throw new IllegalArgumentException("combinaison manquante");
        }
        return new Combinaison(noms.stream().map(ConvertisseurPartieJson::versCouleur).toList());
    }

    /**
     * Retrouve une couleur à partir de son nom.
     *
     * @param nom nom de la couleur, par exemple {@code "ROUGE"}
     * @return la couleur
     * @throws IllegalArgumentException si le nom est absent ou ne correspond à aucune couleur
     */
    private static Couleur versCouleur(String nom) {
        if (nom == null) {
            throw new IllegalArgumentException("couleur manquante");
        }
        try {
            return Couleur.valueOf(nom);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("couleur inconnue : " + nom, e);
        }
    }
}
