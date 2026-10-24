package com.mastermind.model.partie;

public class Difficile extends Difficulté {

    int essais_max=8;

    @Override
    public String toString() {
        return "Difficile";
    }
}
// Rajouter le fait que le mode difficle rend le le test de la première bool null.