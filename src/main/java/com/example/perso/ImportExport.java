package com.example.perso;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ImportExport {

    public static char[][] importerLabyrinthe(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importer un labyrinthe");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier", "*.txt"));

        File file = fileChooser.showOpenDialog(stage);
        if (file == null) return null;

        try {
            java.util.List<String> lignes = Files.readAllLines(Paths.get(file.getAbsolutePath()));
            if (lignes.isEmpty()) return null;

            Outils.mouton = null;
            Outils.loup = null;
            Outils.sortie = null;

            int hauteur = lignes.size();
            int largeur = lignes.get(0).length();
            Outils.lignes = hauteur;
            Outils.colonnes = largeur;

            char[][] laby = new char[hauteur][largeur];

            for (int i = 0; i < hauteur; i++) {
                String ligne = lignes.get(i);
                for (int j = 0; j < largeur && j < ligne.length(); j++) {
                    char c = ligne.charAt(j);
                    Position pos = new Position(i, j);

                    switch (c) {
                        case 'l':
                            Outils.loup = new Animal("Loup", pos);
                            laby[i][j] = 'H';
                            break;
                        case 'm':
                            Outils.mouton = new Animal("Mouton", pos);
                            laby[i][j] = 'H';
                            break;
                        case 's':
                            Outils.sortie = pos;
                            laby[i][j] = 'H';
                            break;
                        case 'x':
                            laby[i][j] = 'R';
                            break;
                        case 'h':
                            laby[i][j] = 'H';
                            break;
                        case 'f':
                            laby[i][j] = 'M';
                            break;
                        case 'c':
                            laby[i][j] = 'C';
                            break;
                        default:
                            laby[i][j] = 'H';
                            break;
                    }
                }
            }

            Outils.labyrinthe = laby;
            return laby;

        } catch (IOException e) {
            return null;
        }
    }

    public static void exporterLabyrinthe(Stage stage) {
        if (Outils.labyrinthe == null) return;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter le labyrinthe");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichiers texte", "*.txt"));
        fileChooser.setInitialFileName("labyrinthe.txt");

        File file = fileChooser.showSaveDialog(stage);
        if (file == null) return;

        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            for (int i = 0; i < Outils.lignes; i++) {
                for (int j = 0; j < Outils.colonnes; j++) {
                    writer.print(getChar(i, j));
                }
                writer.println();
            }
        } catch (IOException e) {
        }
    }

    private static char getChar(int i, int j) {
        Position pos = new Position(i, j);

        if (Outils.mouton != null && Outils.mouton.pos.equals(pos)) {
            return 'm';
        }
        if (Outils.loup != null && Outils.loup.pos.equals(pos)) {
            return 'l';
        }
        if (Outils.sortie != null && Outils.sortie.equals(pos)) {
            return 's';
        }

        char c = Outils.labyrinthe[i][j];
        switch (c) {
            case 'R': return 'x';
            case 'H': return 'h';
            case 'M': return 'f';
            case 'C': return 'c';
            default: return 'h';
        }
    }
}