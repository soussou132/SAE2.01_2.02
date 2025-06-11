package com.example.perso;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.Random;
import java.util.Scanner;

public class Outils {
    static final char ROCHE = 'R';
    static final char HERBE = 'H';
    static final char MARGUERITE = 'M';
    static final char CACTUS = 'C';
    static final char SORTIE = 'S';
    static final char VIDE = ' ';

    static final int VISION = 5;

    static int lignes, cols;
    static char[][] labyrinthe;
    static Position sortie;
    static Animal mouton;
    static Animal loup;
    static Random rand = new Random();
    static Scanner sc = new Scanner(System.in);

    static int herbeMangee = 0;
    static int margueriteMangee = 0;
    static int cactusMangee = 0;

    public static char[][] initLabyrinthe(int lignes, int colonnes) {
        Outils.lignes = lignes;
        Outils.cols = colonnes;
        Outils.labyrinthe = new char[lignes][colonnes];

        for (int i = 0; i < lignes; i++) {
            for (int j = 0; j < colonnes; j++) {
                if (i == 0 || j == 0 || i == lignes - 1 || j == colonnes - 1) {
                    Outils.labyrinthe[i][j] = 'R'; // murs
                } else {
                    Outils.labyrinthe[i][j] = 'H';
                }
            }
        }

        return Outils.labyrinthe;
    }


    static int distanceManhattan(Position a, Position b) {
        return Math.abs(a.ligne - b.ligne) + Math.abs(a.colonne - b.colonne);
    }

    static void manger(Animal animal) {
        if (!animal.type.equals("Mouton")) return;

        char caseActuelle = labyrinthe[animal.pos.ligne][animal.pos.colonne];
        switch (caseActuelle) {
            case CACTUS -> {
                cactusMangee++;
                animal.vitesse = 1;
                labyrinthe[animal.pos.ligne][animal.pos.colonne] = VIDE;
            }
            case HERBE -> {
                herbeMangee++;
                animal.vitesse = 2;
                labyrinthe[animal.pos.ligne][animal.pos.colonne] = VIDE;
            }
            case MARGUERITE -> {
                margueriteMangee++;
                animal.vitesse = 3;
                labyrinthe[animal.pos.ligne][animal.pos.colonne] = VIDE;
            }
        }
    }




    public void afficherStatsPane(VBox root, int herbeMangee, int margueriteMangee, int cactusMangee) {
        Label titre = new Label("Statistiques :");
        Label herbe = new Label("Herbes mangées : " + herbeMangee);
        Label marguerite = new Label("Marguerites mangées : " + margueriteMangee);
        Label cactus = new Label("Cactus mangés : " + cactusMangee);

        titre.setStyle("-fx-font-weight: bold; -fx-underline: true;");

        VBox statsBox = new VBox(5, titre, herbe, marguerite, cactus); // 5 = espacement vertical
        root.getChildren().add(statsBox);
    }





}

