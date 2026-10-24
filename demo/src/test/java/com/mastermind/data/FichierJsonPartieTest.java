package com.mastermind.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mastermind.model.combinaison.Combinaison;
import com.mastermind.model.combinaison.Couleur;
import com.mastermind.model.partie.Configuration;
import com.mastermind.model.partie.Difficulte;
import com.mastermind.model.partie.Partie;
import com.mastermind.model.partie.PartieSauvegardee;
import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Vérifie l'écriture et la relecture des sauvegardes JSON sur le système de fichiers.
 */
class FichierJsonPartieTest {
    private static final Combinaison SECRETE =
            new Combinaison(List.of(Couleur.ROUGE, Couleur.VERT, Couleur.VERT, Couleur.BLEU));
    private static final Combinaison TENTATIVE =
            new Combinaison(List.of(Couleur.VERT, Couleur.VIOLET, Couleur.JAUNE, Couleur.BLEU));
    private static final PartieSauvegardee PARTIE = new PartieSauvegardee(
            Difficulte.FACILE.configuration(), SECRETE, List.of(TENTATIVE), new Combinaison(List.of(Couleur.ROUGE)));

    @TempDir
    Path dossier;

    private final FichierJsonPartie fichiers = new FichierJsonPartie();

    @Test
    void sauvegarderPuisCharger_redonneLaMemePartie() throws ErreurSauvegarde {
        Path fichier = dossier.resolve("save.json");

        fichiers.sauvegarder(PARTIE, fichier);

        assertEquals(PARTIE, fichiers.charger(fichier));
    }

    @Test
    void sauvegarder_ecritUnJsonLisible() throws ErreurSauvegarde, IOException {
        Path fichier = dossier.resolve("save.json");

        fichiers.sauvegarder(PARTIE, fichier);

        JsonObject json = JsonParser.parseString(Files.readString(fichier, StandardCharsets.UTF_8)).getAsJsonObject();
        assertEquals(ConvertisseurPartieJson.VERSION, json.get("version").getAsInt());
        assertEquals(4, json.get("longueur").getAsInt());
        assertEquals(14, json.get("nbEssais").getAsInt());
        assertEquals("[\"ROUGE\",\"VERT\",\"VERT\",\"BLEU\"]", json.get("secrete").toString());
        assertEquals("[[\"VERT\",\"VIOLET\",\"JAUNE\",\"BLEU\"]]", json.get("tentatives").toString());
        assertEquals("[\"ROUGE\"]", json.get("proposition").toString());
    }

    @Test
    void sauvegarder_remplaceUneSauvegardeExistanteSansLaisserDeFichierTemporaire()
            throws ErreurSauvegarde, IOException {
        Path fichier = dossier.resolve("save.json");
        Files.writeString(fichier, "ancienne sauvegarde");

        fichiers.sauvegarder(PARTIE, fichier);

        assertEquals(PARTIE, fichiers.charger(fichier));
        try (Stream<Path> contenu = Files.list(dossier)) {
            assertEquals(List.of(fichier), contenu.toList());
        }
    }

    @Test
    void sauvegarder_dansUnDossierInexistant_estUneErreurDeSauvegarde() {
        Path fichier = dossier.resolve("absent").resolve("save.json");

        assertThrows(ErreurSauvegarde.class, () -> fichiers.sauvegarder(PARTIE, fichier));
    }

    @Test
    void sauvegarderPuisRestaurer_reprendLaPartieLaOuElleEnEtait() throws ErreurSauvegarde {
        Partie originale = new Partie(Configuration.STANDARD, (longueur, graine) -> SECRETE);
        originale.lancer(Configuration.STANDARD, OptionalLong.empty());
        TENTATIVE.couleurs().forEach(originale::poserCouleur);
        originale.validerTentative();
        Path fichier = dossier.resolve("save.json");
        fichiers.sauvegarder(originale.exporter(), fichier);
        Partie reprise = new Partie(Configuration.STANDARD, (longueur, graine) -> Combinaison.vide());

        reprise.restaurer(fichiers.charger(fichier));

        assertEquals(originale.getEtat(), reprise.getEtat());
    }

    @Test
    void charger_fichierAbsent_estUneErreurDeSauvegarde() {
        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(dossier.resolve("absent.json")));
    }

    @Test
    void charger_fichierVide_estUneErreurDeSauvegarde() throws IOException {
        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(ecrire("")));
    }

    @Test
    void charger_jsonCorrompu_estUneErreurDeSauvegarde() throws IOException {
        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(ecrire("{ \"version\": 1, \"secrete\": [")));
    }

    @Test
    void charger_autreChoseQuUnObjet_estUneErreurDeSauvegarde() throws IOException {
        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(ecrire("[1, 2, 3]")));
    }

    @Test
    void charger_versionNonGeree_estUneErreurDeSauvegarde() throws IOException {
        Path fichier = ecrire(json(2, 4, 12, "[\"ROUGE\",\"VERT\",\"VERT\",\"BLEU\"]", "[]", "[]"));

        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(fichier));
    }

    @Test
    void charger_couleurInconnue_estUneErreurDeSauvegarde() throws IOException {
        Path fichier = ecrire(json(1, 4, 12, "[\"ROUGE\",\"VERT\",\"VERT\",\"NOIR\"]", "[]", "[]"));

        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(fichier));
    }

    @Test
    void charger_couleurNulle_estUneErreurDeSauvegarde() throws IOException {
        Path fichier = ecrire(json(1, 4, 12, "[\"ROUGE\",\"VERT\",\"VERT\",null]", "[]", "[]"));

        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(fichier));
    }

    @Test
    void charger_champManquant_estUneErreurDeSauvegarde() throws IOException {
        Path fichier = ecrire("{ \"version\": 1, \"longueur\": 4, \"nbEssais\": 12, \"tentatives\": [], \"proposition\": [] }");

        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(fichier));
    }

    @Test
    void charger_configurationInvalide_estUneErreurDeSauvegarde() throws IOException {
        Path fichier = ecrire(json(1, 0, 12, "[]", "[]", "[]"));

        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(fichier));
    }

    @Test
    void charger_dimensionsIncoherentes_estUneErreurDeSauvegarde() throws IOException {
        Path fichier = ecrire(json(1, 4, 12, "[\"ROUGE\",\"VERT\"]", "[]", "[]"));

        assertThrows(ErreurSauvegarde.class, () -> fichiers.charger(fichier));
    }

    /**
     * Construit le texte d'une sauvegarde à partir de ses champs.
     *
     * @param version     version du format
     * @param longueur    nombre de pions
     * @param nbEssais    nombre d'essais
     * @param secrete     tableau JSON de la secrète
     * @param tentatives  tableau JSON des tentatives
     * @param proposition tableau JSON de la proposition
     * @return le texte JSON
     */
    private static String json(int version, int longueur, int nbEssais,
                               String secrete, String tentatives, String proposition) {
        return """
                { "version": %d, "longueur": %d, "nbEssais": %d,
                  "secrete": %s, "tentatives": %s, "proposition": %s }
                """.formatted(version, longueur, nbEssais, secrete, tentatives, proposition);
    }

    /**
     * Écrit un contenu brut dans un fichier du dossier temporaire.
     *
     * @param contenu texte à écrire
     * @return le fichier écrit
     * @throws IOException si l'écriture échoue
     */
    private Path ecrire(String contenu) throws IOException {
        return Files.writeString(dossier.resolve("save.json"), contenu, StandardCharsets.UTF_8);
    }
}
