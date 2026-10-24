package com.mastermind.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.mastermind.model.partie.PartieSauvegardee;
import com.mastermind.model.sauvegarde.ChargementPartie;
import com.mastermind.model.sauvegarde.ErreurSauvegarde;
import com.mastermind.model.sauvegarde.SauvegardePartie;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * Adaptateur des ports de sauvegarde vers le système de fichiers : une partie par fichier JSON, encodé en UTF-8.
 * Une sauvegarde est d'abord écrite dans un fichier temporaire du même dossier, puis mise à la place de la
 * destination : un échec en cours d'écriture n'abîme jamais l'ancienne sauvegarde.
 */
public final class FichierJsonPartie implements SauvegardePartie, ChargementPartie {
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final ConvertisseurPartieJson convertisseur = new ConvertisseurPartieJson();

    @Override
    public void sauvegarder(PartieSauvegardee partie, Path destination) throws ErreurSauvegarde {
        Objects.requireNonNull(partie);
        Objects.requireNonNull(destination);
        String json = gson.toJson(convertisseur.versJson(partie));
        try {
            ecrireEnRemplacant(destination, json);
        } catch (IOException e) {
            throw new ErreurSauvegarde("Impossible d'écrire la sauvegarde dans " + destination, e);
        }
    }

    @Override
    public PartieSauvegardee charger(Path source) throws ErreurSauvegarde {
        Objects.requireNonNull(source);
        return convertisseur.depuisJson(analyser(lire(source)));
    }

    /**
     * Écrit le texte dans un fichier temporaire voisin, puis remplace la destination.
     *
     * @param destination fichier à remplacer
     * @param texte       contenu à écrire
     * @throws IOException si le dossier n'est pas accessible en écriture
     */
    private static void ecrireEnRemplacant(Path destination, String texte) throws IOException {
        Path dossier = destination.toAbsolutePath().getParent();
        Path temporaire = Files.createTempFile(dossier, "mastermind-", ".tmp");
        try {
            Files.writeString(temporaire, texte, StandardCharsets.UTF_8);
            Files.move(temporaire, destination, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporaire);
        }
    }

    /**
     * Lit le contenu d'un fichier de sauvegarde.
     *
     * @param source fichier à lire
     * @return son contenu
     * @throws ErreurSauvegarde si le fichier est absent, inaccessible ou n'est pas du texte UTF-8
     */
    private static String lire(Path source) throws ErreurSauvegarde {
        try {
            return Files.readString(source, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ErreurSauvegarde("Impossible de lire le fichier " + source, e);
        }
    }

    /**
     * Analyse le texte JSON d'une sauvegarde.
     *
     * @param json texte lu dans le fichier
     * @return le contenu structuré
     * @throws ErreurSauvegarde si le texte est vide ou n'est pas un JSON de sauvegarde
     */
    private PartieJson analyser(String json) throws ErreurSauvegarde {
        PartieJson contenu;
        try {
            contenu = gson.fromJson(json, PartieJson.class);
        } catch (JsonParseException e) {
            throw new ErreurSauvegarde("Le fichier n'est pas une sauvegarde Mastermind valide", e);
        }
        if (contenu == null) {
            throw new ErreurSauvegarde("Le fichier de sauvegarde est vide");
        }
        return contenu;
    }
}
