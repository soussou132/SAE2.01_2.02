package com.example.perso;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Gameplay {
    private final Stage stage;
    private Pane gamePane;
    private Button boutonProchainTour;
    private Label labelTour;
    private Label labelStatut;
    private VBox statsPanel;

    private int numeroTour = 1;
    private boolean jeuTermine = false;
    private boolean tourMouton = true;
    private Random random = new Random();
    private String methodeMouton;
    private String methodeLoup;

    private static final int DistFuite = 5;
    private static final int DistChasse = 5;

    public Gameplay(Stage stage, String methodeMouton, String methodeLoup) {
        this.stage = stage;
        this.methodeMouton = methodeMouton;
        this.methodeLoup = methodeLoup;
    }

    public void demarrer() {
        BorderPane root = new BorderPane();

        HBox controles = new HBox(20);
        controles.setPadding(new Insets(10));
        controles.setStyle("-fx-background-color: #f0f0f0; -fx-alignment: center;");

        labelTour = new Label("Tour n°" + numeroTour);
        labelTour.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        boutonProchainTour = new Button("Prochain Tour");
        boutonProchainTour.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        boutonProchainTour.setOnAction(e -> jouerTour());

        Button boutonQuitter = new Button("Retour au menu");
        boutonQuitter.setStyle("-fx-font-size: 14px; -fx-padding: 10;");
        boutonQuitter.setOnAction(e -> retourMenu());

        controles.getChildren().addAll(labelTour, boutonProchainTour, boutonQuitter);

        statsPanel = new VBox(10);
        statsPanel.setPadding(new Insets(20));
        statsPanel.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #cccccc; -fx-border-width: 1;");
        statsPanel.setPrefWidth(200);

        labelStatut = new Label("Au mouton de jouer...");
        labelStatut.setStyle("-fx-font-weight: bold;");

        mettreAJourStats();

        gamePane = new Pane();
        gamePane.setStyle("-fx-background-color: white;");

        afficherLabyrinthe();

        root.setTop(controles);
        root.setCenter(gamePane);
        root.setRight(statsPanel);

        Scene gameScene = new Scene(root, 1200, 800);
        stage.setScene(gameScene);
        stage.setTitle("Mange-moi si tu peux ! - Jeu en cours");
    }

    private void afficherLabyrinthe() {
        gamePane.getChildren().clear();

        double cellSize = Math.min(600.0 / Outils.lignes, 600.0 / Outils.colonnes);
        cellSize = Math.max(cellSize, 30);
        cellSize = Math.min(cellSize, 60);

        double totalWidth = Outils.colonnes * cellSize;
        double totalHeight = Outils.lignes * cellSize;
        double offsetX = (800 - totalWidth) / 2;
        double offsetY = (600 - totalHeight) / 2;

        for (int i = 0; i < Outils.lignes; i++) {
            for (int j = 0; j < Outils.colonnes; j++) {
                double x = offsetX + j * cellSize;
                double y = offsetY + i * cellSize;

                Rectangle rect = new Rectangle(x, y, cellSize, cellSize);
                rect.setStroke(Color.GRAY);
                rect.setStrokeWidth(1);
                rect.setFill(Color.LIGHTGRAY);

                char cellContent = Outils.labyrinthe[i][j];
                if (cellContent == 'O' || cellContent == 'L') {
                    cellContent = '.';
                }

                if (Outils.sortie != null && Outils.sortie.ligne == i && Outils.sortie.colonne == j) {
                    rect.setFill(Color.GOLD);
                    rect.setStroke(Color.ORANGE);
                    rect.setStrokeWidth(3);
                }

                ImageView imageView = createImageViewFromChar(cellContent);
                if (imageView.getImage() != null) {
                    imageView.setX(x);
                    imageView.setY(y);
                    imageView.setFitWidth(cellSize);
                    imageView.setFitHeight(cellSize);
                    gamePane.getChildren().addAll(rect, imageView);
                } else {
                    gamePane.getChildren().add(rect);
                }

                if (Outils.mouton != null && Outils.mouton.pos.ligne == i && Outils.mouton.pos.colonne == j) {
                    ImageView moutonView = createImageViewFromChar('O');
                    moutonView.setX(x);
                    moutonView.setY(y);
                    moutonView.setFitWidth(cellSize);
                    moutonView.setFitHeight(cellSize);

                    if (Outils.mouton.estEnFuite()) {
                        moutonView.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0.5, 0, 0);");
                    }

                    gamePane.getChildren().add(moutonView);
                }

                if (Outils.loup != null && Outils.loup.pos.ligne == i && Outils.loup.pos.colonne == j) {
                    ImageView loupView = createImageViewFromChar('L');
                    loupView.setX(x);
                    loupView.setY(y);
                    loupView.setFitWidth(cellSize);
                    loupView.setFitHeight(cellSize);

                    if (Outils.loup.estEnFuite()) {
                        loupView.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0.5, 0, 0);");
                    }

                    gamePane.getChildren().add(loupView);
                }
            }
        }
    }

    private void jouerTour() {
        if (jeuTermine) return;
        Animal animalActuel = tourMouton ? Outils.mouton : Outils.loup;
        if (tourMouton) {
            verifModeFuite();
        } else {
            verifierModeChasse();
        }
        if (animalActuel != null) {
            jouerMouvementsSequentiels(animalActuel, 0);
        }
    }

    public void verifierModeChasse() {
        if (Outils.loup == null || Outils.mouton == null) return;
        int distance = AStar.distanceManhattan(Outils.loup.pos, Outils.mouton.pos);
        boolean doitChasser = distance <= DistChasse;
        Outils.loup.activerFuite(doitChasser);
    }

    public void verifModeFuite() {
        if (Outils.mouton == null || Outils.loup == null) return;
        int distance = AStar.distanceManhattan(Outils.mouton.pos, Outils.loup.pos);
        boolean doitFuir = distance <= DistFuite;
        Outils.mouton.activerFuite(doitFuir);
    }

    public void jouerMouvementsSequentiels(Animal animalActuel, int mouvementIndex) {
        if (jeuTermine || mouvementIndex >= animalActuel.vitesse) {
            finirTour();
            return;
        }

        boutonProchainTour.setDisable(true);

        List<Position> moves = obtenirMouvementsPossibles(animalActuel.pos, tourMouton);
        if (!moves.isEmpty()) {
            Position nouvellePosition = moves.get(random.nextInt(moves.size()));
            animalActuel.setPosition(nouvellePosition);

            afficherLabyrinthe();
            mettreAJourStats();

            if (tourMouton && Outils.sortie != null &&
                    Outils.mouton.pos.ligne == Outils.sortie.ligne &&
                    Outils.mouton.pos.colonne == Outils.sortie.colonne) {

                jeuTermine = true;
                labelStatut.setText("Le mouton a gagné !");
                boutonProchainTour.setDisable(true);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fin de la partie");
                alert.setHeaderText("Le mouton a gagné !");
                alert.setContentText("Le mouton a atteint la sortie au tour n°" + numeroTour);
                alert.showAndWait();
                return;
            }

            if (Outils.loup != null && Outils.mouton != null &&
                    Outils.loup.pos.ligne == Outils.mouton.pos.ligne &&
                    Outils.loup.pos.colonne == Outils.mouton.pos.colonne) {

                jeuTermine = true;
                labelStatut.setText("Le loup a attrapé le mouton !");
                boutonProchainTour.setDisable(true);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Fin de la partie");
                alert.setHeaderText("Le loup a gagné !");
                alert.setContentText("Le mouton a été mangé au tour n°" + numeroTour);
                alert.showAndWait();
                return;
            }

            Platform.runLater(() -> {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                jouerMouvementsSequentiels(animalActuel, mouvementIndex + 1);
            });

        } else {
            Platform.runLater(() -> jouerMouvementsSequentiels(animalActuel, mouvementIndex + 1));
        }
    }

    public void finirTour() {
        if (tourMouton && Outils.mouton != null && !jeuTermine) {
            nourrirMouton();
        }
        tourMouton = !tourMouton;
        numeroTour++;
        labelTour.setText("Tour n°" + numeroTour);
        labelStatut.setText(tourMouton ? "Au mouton de jouer…" : "Au loup de jouer…");

        mettreAJourStats();
        afficherLabyrinthe();
        boutonProchainTour.setDisable(false);
    }

    public void nourrirMouton() {
        if (Outils.mouton == null) return;

        int ligne = Outils.mouton.pos.ligne;
        int colonne = Outils.mouton.pos.colonne;
        char contenuCase = Outils.labyrinthe[ligne][colonne];

        switch (contenuCase) {
            case 'H':
                Outils.herbeMangee++;
                Outils.mouton.vitesse = 2;
                break;
            case 'M':
                Outils.margueriteMangee++;
                Outils.mouton.vitesse = 4;
                break;
            case 'C':
                Outils.cactusMangee++;
                Outils.mouton.vitesse = 1;
                break;
        }
    }

    public List<Position> obtenirMouvementsPossibles(Position pos, boolean estMouton) {
        if (estMouton) {
            if (Outils.mouton != null && Outils.mouton.estEnFuite()) {
                return mouveFuite(pos);
            } else {
                return mouvementPossible(pos, true);
            }
        } else {
            if (Outils.loup != null && Outils.loup.estEnFuite()) {
                return mouveChasse(pos);
            } else {
                return mouvementPossible(pos, false);
            }
        }
    }

    public List<Position> mouveChasse(Position pos) {
        List<Position> mouvements = new ArrayList<>();

        if (Outils.mouton == null) {
            return mouvementPossible(pos, false);
        }

        switch (methodeLoup) {
            case "A*":
                mouvements = obtenirMouvementAStar(pos, false);
                break;
//            case "Dijkstra":
//                Position prochainePos = Dijkstra.prochainePosition(pos, Outils.mouton.pos, false);
//                if (prochainePos != null) {
//                    mouvements.add(prochainePos);
//                }
//                break;
            case "Random":
            default:
                mouvements = mouvementPossible(pos, true);
                break;
        }

        if (mouvements.isEmpty()) {
            mouvements = mouvementPossible(pos, false);
        }

        return mouvements;
    }

    public List<Position> mouveFuite(Position pos) {
        List<Position> mouvements = new ArrayList<>();

        if (Outils.sortie == null) {
            return mouvementPossible(pos, true);
        }

        switch (methodeMouton) {
            case "A*":
                mouvements = obtenirMouvementAStar(pos, true);
                break;
//
//            case "Dijkstra":
//                Position prochainePos = Dijkstra.prochainePosition(pos, Outils.sortie, true);
//                if (prochainePos != null) {
//                    mouvements.add(prochainePos);
//                }
//                break;

            case "Random":
            default:
                mouvements = mouvementPossible(pos, true);
                break;
        }

        if (mouvements.isEmpty()) {
            mouvements = mouvementPossible(pos, true);
        }

        return mouvements;
    }

    public List<Position> obtenirMouvementAStar(Position pos, boolean estMouton) {
        List<Position> mouvements = new ArrayList<>();

        Position objectif;
        if (estMouton) {
            objectif = Outils.sortie;
        } else {
            objectif = Outils.mouton != null ? Outils.mouton.pos : null;
        }
        if (objectif != null) {
            Position prochainePos = AStar.prochainePosition(pos, objectif, estMouton);
            if (prochainePos != null) {
                mouvements.add(prochainePos);
            }
        }

        if (mouvements.isEmpty()) {
            mouvements = mouvementPossible(pos, estMouton);
        }

        return mouvements;
    }

    public List<Position> mouvementPossible(Position pos, boolean estMouton) {
        List<Position> mouvements = new ArrayList<>();
        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

        for (int[] dir : directions) {
            Position nouvellePos = new Position(pos.ligne + dir[0], pos.colonne + dir[1]);

            if (!nouvellePos.estValide(Outils.lignes, Outils.colonnes)) {
                continue;
            }

            char cellule = Outils.labyrinthe[nouvellePos.ligne][nouvellePos.colonne];
            if (cellule == 'R') {
                continue;
            }

            if (estSortie(nouvellePos)) {
                if (estMouton) {
                    mouvements.add(nouvellePos);
                }
            } else {
                mouvements.add(nouvellePos);
            }
        }
        return mouvements;
    }

    public boolean estSortie(Position pos) {
        return pos.equals(Outils.sortie);
    }

    private void mettreAJourStats() {
        statsPanel.getChildren().clear();

        Label titre = new Label("STATISTIQUES");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-underline: true;");

        Label tourLabel = new Label("Tour: " + numeroTour);
        Label herbeLabel = new Label("Herbes mangées: " + Outils.herbeMangee);
        Label margueriteLabel = new Label("Marguerites mangées: " + Outils.margueriteMangee);
        Label cactusLabel = new Label("Cactus mangés: " + Outils.cactusMangee);

        String vitesseMouton = Outils.mouton != null ? String.valueOf(Outils.mouton.vitesse) : "N/A";
        Label vitesseLabel = new Label("Vitesse mouton: " + vitesseMouton);

        String distanceLoupMouton = "N/A";
        if (Outils.loup != null && Outils.mouton != null) {
            distanceLoupMouton = String.valueOf(AStar.distanceManhattan(Outils.loup.pos, Outils.mouton.pos));
        }
        Label distanceLabel = new Label("Distance loup-mouton: " + distanceLoupMouton);

        String positionSortie = "N/A";
        if (Outils.sortie != null) {
            positionSortie = "(" + Outils.sortie.ligne + ", " + Outils.sortie.colonne + ")";
        }
        Label sortieLabel = new Label("Position sortie: " + positionSortie);

        String modeFuite = "N/A";
        if (Outils.mouton != null) {
            modeFuite = Outils.mouton.estEnFuite() ? "FUITE ACTIVE" : "Normal";
        }
        Label fuiteLabel = new Label("Mode mouton: " + modeFuite);

        String modeChasse = "N/A";
        if (Outils.loup != null) {
            modeChasse = Outils.loup.estEnFuite() ? "CHASSE ACTIVE" : "Normal";
        }
        Label chasseLabel = new Label("Mode loup: " + modeChasse);

        statsPanel.getChildren().addAll(
                titre, labelStatut,
                new Label(""),
                tourLabel, vitesseLabel, distanceLabel,
                new Label(""),
                sortieLabel, fuiteLabel, chasseLabel,
                new Label(""),
                herbeLabel, margueriteLabel, cactusLabel
        );
    }

    public static ImageView createImageViewFromChar(char c) {
        String path = switch (c) {
            case 'R' -> "/images/Rocher.png";
            case 'H' -> "/images/Herbe.png";
            case 'M' -> "/images/Margerite.png";
            case 'C' -> "/images/Cactus.png";
            case 'L' -> "/images/Loup.png";
            case 'O' -> "/images/Mouton.png";
            default -> null;
        };
        if (path == null) return new ImageView();

        try {
            Image image = new Image(Objects.requireNonNull(Gameplay.class.getResource(path)).toExternalForm());
            return new ImageView(image);
        } catch (Exception e) {
            return new ImageView();
        }
    }

    private void retourMenu() {
        numeroTour = 1;
        jeuTermine = false;
        tourMouton = true;

        try {
            new mainJavaFX().start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}