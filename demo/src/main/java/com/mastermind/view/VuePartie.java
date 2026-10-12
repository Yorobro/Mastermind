package com.mastermind.view;

import com.mastermind.model.partie.EtatPartie;
import com.mastermind.observer.Observateur;

/**
 * Contrat d'une vue de partie : elle observe le modèle et se redessine
 * à partir de l'{@link EtatPartie} reçu. Permet de changer de vue sans toucher au reste.
 */
public interface VuePartie extends Observateur<EtatPartie> {
}
