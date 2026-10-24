package com.mastermind.data;

import java.util.List;

/**
 * Format du fichier de sauvegarde, séparé du modèle : le modèle peut évoluer sans casser les fichiers existants.
 * Les couleurs sont stockées par leur nom ({@code "ROUGE"}, {@code "BLEU"}...).
 *
 * @param version     version du format, pour reconnaître les fichiers d'une autre version
 * @param longueur    nombre de pions d'une combinaison
 * @param nbEssais    nombre de tentatives autorisées
 * @param secrete     combinaison à deviner
 * @param tentatives  propositions validées, de la première à la dernière
 * @param proposition tentative en cours de saisie
 */
record PartieJson(
    int version,
    int longueur,
    int nbEssais,
    List<String> secrete,
    List<List<String>> tentatives,
    List<String> proposition
) {
}
