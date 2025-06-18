package com.example.perso;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Outils {
    static int lignes, colonnes;
    static char[][] labyrinthe;
    static Position sortie;
    static Animal mouton;
    static Animal loup;

    static int herbeMangee = 0;
    static int margueriteMangee = 0;
    static int cactusMangee = 0;

    public static char[][] initLabyrinthe(int l, int c) {
        lignes = l;
        colonnes = c;
        labyrinthe = new char[lignes][colonnes];

        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (i == 0 || j == 0 || i == lignes - 1 || j == colonnes - 1) {
                    labyrinthe[i][j] = 'R';
                } else {
                    labyrinthe[i][j] = 'H';
                }
            }
        }
        return labyrinthe;
    }
}
