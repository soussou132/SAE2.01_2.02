package com.example.perso;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.Objects;

public class mainJavaFX extends Application {

    private final ToggleGroup toolsGroup = new ToggleGroup();
    private boolean loupPlace = false;
    private boolean moutonPlace = false;
    private boolean sortie = false;
    private boolean jeuLance = false;
    private Button boutonExporter;
    private Button boutonImporter;
    private Button boutonLancerJeu;
    private Button boutonEnleverMouton;
    private Button boutonEnleverLoup;
    private Stage primaryStage;
    private Pane currentLabyPane;
    private ComboBox<String> methodeMouton;
    private ComboBox<String> methodeLoup;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox rootMenu = new VBox(20);
        rootMenu.setStyle("-fx-padding: 40; -fx-alignment: center;");

        Button boutonPlay = new Button("Play");
        Button boutonCredits = new Button("Crédits");
        Button boutonQuitter = new Button("Quitter");

        rootMenu.getChildren().addAll(boutonPlay, boutonCredits, boutonQuitter);

        Scene menuScene = new Scene(rootMenu, 600, 600);
        primaryStage.setTitle("Mange-moi si tu peux !");
        primaryStage.setScene(menuScene);
        primaryStage.show();

        boutonPlay.setOnAction(e -> openGameScene(primaryStage));
        boutonQuitter.setOnAction(e -> primaryStage.close());

        boutonCredits.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Crédits");
            alert.setHeaderText("Jeu développé par :");
            alert.setContentText("Soulayman SAIDI" +
                            "\n\nUniversité Polytechnique Hauts-de-France" +
                            "\nProjet SAE 2.01/2.02");
            alert.showAndWait();
        });
    }

    public void openGameScene(Stage stage) {
        loupPlace = false;
        moutonPlace = false;
        sortie = false;
        jeuLance = false;

        TabPane tabs = new TabPane();
        Tab tabTailleLaby = new Tab("Taille Labyrinthe");
        Tab tabLabyrinthe = new Tab("Labyrinthe");

        tabLabyrinthe.setClosable(false);
        tabTailleLaby.setClosable(false);

        TextField ligneField = new TextField(); ligneField.setPromptText("Nombre de lignes");
        TextField colonneField = new TextField(); colonneField.setPromptText("Nombre de colonnes");

        Label labelMethodes = new Label("Méthodes de parcours :");
        labelMethodes.setStyle("-fx-font-weight: bold;");

        Label labelMouton = new Label("Mouton :");
        methodeMouton = new ComboBox<>();
        methodeMouton.getItems().addAll("Random", "A*");
        methodeMouton.setValue("Random");

        Label labelLoup = new Label("Loup :");
        methodeLoup = new ComboBox<>();
        methodeLoup.getItems().addAll("Random", "A*");
        methodeLoup.setValue("Random");

        Button validerCreationLaby = new Button("Valider");

        VBox formulaire = new VBox(10,
                ligneField, colonneField,

                labelMethodes,
                new HBox(10, labelMouton, methodeMouton),
                new HBox(10, labelLoup, methodeLoup),
                validerCreationLaby);

        formulaire.setPadding(new Insets(20));
        tabTailleLaby.setContent(formulaire);

        Pane labyPane = new Pane();
        currentLabyPane = labyPane;

        boutonImporter = new Button("Importer Labyrinthe");
        boutonExporter = new Button("Exporter Labyrinthe");
        boutonEnleverMouton = new Button("Enlever Mouton");
        boutonEnleverLoup = new Button("Enlever Loup");
        boutonLancerJeu = new Button("Lancer le Jeu");


        boutonExporter.setOnAction(e -> {
            ImportExport.exporterLabyrinthe(stage);
        });

        boutonImporter.setOnAction(e -> {
            char[][] labyImporte = ImportExport.importerLabyrinthe(stage);
            if (labyImporte != null) {
                rafraichirAffichageLabyrinthe(labyPane, Outils.labyrinthe);
                ligneField.setText(String.valueOf(Outils.lignes));
                colonneField.setText(String.valueOf(Outils.colonnes));
                loupPlace = (Outils.loup != null);
                moutonPlace = (Outils.mouton != null);
                sortie = (Outils.sortie != null);
                tabs.getSelectionModel().select(tabLabyrinthe);
            }
        });

        boutonEnleverMouton.setOnAction(e -> {
            if (moutonPlace && Outils.mouton != null) {
                Position posMouton = Outils.mouton.pos;
                Outils.labyrinthe[posMouton.ligne][posMouton.colonne] = 'H';
                Outils.mouton = null;
                moutonPlace = false;
                rafraichirAffichageLabyrinthe(currentLabyPane, Outils.labyrinthe);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Mouton enlevé");
                info.setContentText("Le mouton a été retiré du labyrinthe.");
                info.showAndWait();
            } else {
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Aucun mouton");
                warning.setContentText("Il n'y a pas de mouton à enlever.");
                warning.showAndWait();
            }
        });

        boutonEnleverLoup.setOnAction(e -> {
            if (loupPlace && Outils.loup != null) {
                Position posLoup = Outils.loup.pos;
                Outils.labyrinthe[posLoup.ligne][posLoup.colonne] = 'H';
                Outils.loup = null;
                loupPlace = false;
                rafraichirAffichageLabyrinthe(currentLabyPane, Outils.labyrinthe);

                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setTitle("Loup enlevé");
                info.setContentText("Le loup a été retiré du labyrinthe.");
                info.showAndWait();
            } else {
                Alert warning = new Alert(Alert.AlertType.WARNING);
                warning.setTitle("Aucun loup");
                warning.setContentText("Il n'y a pas de loup à enlever.");
                warning.showAndWait();
            }
        });

        boutonLancerJeu.setOnAction(e -> {
            if (loupPlace && moutonPlace && sortie) {
                lancerJeu(stage, methodeMouton.getValue(), methodeLoup.getValue());
            } else {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Labyrinthe incomplet");
                alert.setHeaderText("Impossible de lancer le jeu");
                alert.setContentText("Vous devez placer :\n" +
                        (loupPlace ? "" : "- Un loup\n") +
                        (moutonPlace ? "" : "- Un mouton\n") +
                        (sortie ? "" : "- Une sortie (herbe sur un bord)"));
                alert.showAndWait();
            }
        });

        tabLabyrinthe.setContent(labyPane);
        tabs.getTabs().addAll(tabTailleLaby, tabLabyrinthe);

        validerCreationLaby.setOnAction(e -> {
            try {
                int lignes = Integer.parseInt(ligneField.getText());
                int colonnes = Integer.parseInt(colonneField.getText());

                if (lignes < 5 || colonnes < 5) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Taille invalide");
                    alert.setContentText("Le labyrinthe doit faire au minimum 5x5");
                    alert.showAndWait();
                    return;
                }

                char[][] laby = Outils.initLabyrinthe(lignes, colonnes);
                loupPlace = false;
                moutonPlace = false;
                sortie = false;

                labyPane.getChildren().clear();

                for (int i = 0; i < laby.length; i++) {
                    for (int j = 0; j < laby[i].length; j++) {
                        Rectangle rect = new Rectangle(j * 50, i * 50, 50, 50);
                        rect.setStroke(Color.BEIGE);

                        char cell = laby[i][j];
                        ImageView imageView = Gameplay.createImageViewFromChar(cell);
                        imageView.setX(j * 50);
                        imageView.setY(i * 50);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);

                        final int fi = i;
                        final int fj = j;
                        Position pos = new Position(fi, fj);

                        imageView.setOnMouseClicked(evt -> {
                            Toggle selected = toolsGroup.getSelectedToggle();
                            if (selected != null) {
                                char symbole = (char) selected.getUserData();

                                if ((symbole == 'L' && loupPlace) || (symbole == 'O' && moutonPlace)) return;

                                if (Bordure.estCoin(fi, fj, lignes, colonnes)) return;

                                if (laby[fi][fj] == 'L') loupPlace = false;
                                if (laby[fi][fj] == 'O') moutonPlace = false;

                                if (laby[fi][fj] == 'H' && Bordure.estSurBord(fi, fj, lignes, colonnes) &&
                                        sortie && Outils.sortie != null &&
                                        Outils.sortie.ligne == fi && Outils.sortie.colonne == fj) {
                                    sortie = false;
                                    Outils.sortie = null;
                                }

                                if (Bordure.estSurBord(fi, fj, lignes, colonnes)) {
                                    if (symbole == 'H') {
                                        if (sortie) return;
                                        sortie = true;
                                        Outils.sortie = pos;
                                    } else if (symbole != 'R') {
                                        return;
                                    }
                                }

                                laby[fi][fj] = symbole;

                                if (symbole == 'L') {
                                    loupPlace = true;
                                    Outils.loup = new Animal("L", pos);
                                    laby[fi][fj] = 'H';
                                }

                                if (symbole == 'O') {
                                    moutonPlace = true;
                                    Outils.mouton = new Animal("O", pos);
                                    laby[fi][fj] = 'H';
                                }

                                ImageView newImage = Gameplay.createImageViewFromChar(symbole);
                                newImage.setX(fj * 50);
                                newImage.setY(fi * 50);
                                newImage.setFitWidth(50);
                                newImage.setFitHeight(50);

                                newImage.setOnMouseClicked(imageView.getOnMouseClicked());
                                labyPane.getChildren().remove(imageView);
                                labyPane.getChildren().add(newImage);
                            }
                        });

                        labyPane.getChildren().addAll(rect, imageView);
                    }
                }

                tabTailleLaby.setDisable(false);
                tabs.getSelectionModel().select(tabLabyrinthe);

            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de saisie");
                alert.setContentText("Veuillez entrer des nombres valides pour les dimensions");
                alert.showAndWait();
            }
        });

        VBox toolBox = createToolBox();

        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(toolBox, tabs);

        Scene scene = new Scene(layout, 1000, 800);
        stage.setScene(scene);
    }

    public VBox createToolBox() {
        VBox tools = new VBox(10);
        tools.setPadding(new Insets(20));

        Label labelOutils = new Label();
        labelOutils.setStyle("-fx-font-weight: bold;");

        Label labelFichier = new Label("Fichier :");
        labelFichier.setStyle("-fx-font-weight: bold;");

        Label labelJeu = new Label("Jeu :");
        labelJeu.setStyle("-fx-font-weight: bold;");

//        Label labelAnimaux = new Label("Gestion Animaux :");
//        labelAnimaux.setStyle("-fx-font-weight: bold;");

        tools.getChildren().addAll(
                labelOutils,
                createToolButton("Mouton", "/images/Mouton.png", 'O'),
                createToolButton("Loup", "/images/Loup.png", 'L'),
                createToolButton("Marguerite", "/images/Margerite.png", 'M'),
                createToolButton("Cactus", "/images/Cactus.png", 'C'),
                createToolButton("Rocher", "/images/Rocher.png", 'R'),
                createToolButton("Herbe (Sortie)", "/images/Herbe.png", 'H'),

//                new Separator(),
//                labelAnimaux,
//                boutonEnleverMouton,
//                boutonEnleverLoup,

                new Separator(),
                labelFichier,
                boutonImporter,
                boutonExporter,

                new Separator(),
                labelJeu,
                boutonLancerJeu
        );
        return tools;
    }

    public ToggleButton createToolButton(String name, String imagePath, char symbole) {
        Image image = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm());
        ImageView view = new ImageView(image);
        view.setFitWidth(50);
        view.setFitHeight(50);

        ToggleButton button = new ToggleButton("", view);
        button.setUserData(symbole);
        button.setToggleGroup(toolsGroup);
        button.setTooltip(new Tooltip(name));
        return button;
    }

    public void rafraichirAffichageLabyrinthe(Pane labyPane, char[][] laby) {
        labyPane.getChildren().clear();

        for (int i = 0; i < laby.length; i++) {
            for (int j = 0; j < laby[i].length; j++) {
                Rectangle rect = new Rectangle(j * 50, i * 50, 50, 50);
                rect.setStroke(Color.BEIGE);

                char cell = laby[i][j];

                char displayChar = cell;
                if (Outils.mouton != null && Outils.mouton.pos.ligne == i && Outils.mouton.pos.colonne == j) {
                    displayChar = 'O';
                } else if (Outils.loup != null && Outils.loup.pos.ligne == i && Outils.loup.pos.colonne == j) {
                    displayChar = 'L';
                }

                ImageView imageView = Gameplay.createImageViewFromChar(displayChar);
                imageView.setX(j * 50);
                imageView.setY(i * 50);
                imageView.setFitWidth(50);
                imageView.setFitHeight(50);

                final int fi = i;
                final int fj = j;
                Position pos = new Position(fi, fj);

                imageView.setOnMouseClicked(evt -> {
                    Toggle selected = toolsGroup.getSelectedToggle();
                    if (selected != null) {
                        char symbole = (char) selected.getUserData();

                        if ((symbole == 'L' && loupPlace) || (symbole == 'O' && moutonPlace)) return;
                        if (Bordure.estCoin(fi, fj, Outils.lignes, Outils.colonnes)) return;
                        if (Outils.loup != null && Outils.loup.pos.ligne == fi && Outils.loup.pos.colonne == fj) {
                            loupPlace = false;
                            Outils.loup = null;}

                        if (Outils.mouton != null && Outils.mouton.pos.ligne == fi && Outils.mouton.pos.colonne == fj) {
                            moutonPlace = false;
                            Outils.mouton = null;}

                        if (laby[fi][fj] == 'H' && Bordure.estSurBord(fi, fj, Outils.lignes, Outils.colonnes) &&
                                sortie && Outils.sortie != null &&
                                Outils.sortie.ligne == fi && Outils.sortie.colonne == fj) {
                            sortie = false;
                            Outils.sortie = null;}

                        if (Bordure.estSurBord(fi, fj, Outils.lignes, Outils.colonnes)) {
                            if (symbole == 'H') {
                                if (sortie) return;
                                sortie = true;
                                Outils.sortie = pos;
                            } else if (symbole != 'R') {
                                return;
                            }
                        }

                        if (symbole != 'L' && symbole != 'O') {
                            laby[fi][fj] = symbole;
                        }

                        if (symbole == 'L') {
                            loupPlace = true;
                            Outils.loup = new Animal("L", pos);
                            laby[fi][fj] = 'H';
                        }

                        if (symbole == 'O') {
                            moutonPlace = true;
                            Outils.mouton = new Animal("O", pos);
                            laby[fi][fj] = 'H';
                        }

                        rafraichirAffichageLabyrinthe(labyPane, laby);
                    }
                });

                labyPane.getChildren().addAll(rect, imageView);
            }
        }
    }

    public void lancerJeu(Stage stage, String methodeMouton, String methodeLoup) {
        if (jeuLance) return;
        jeuLance = true;

        try {
            Gameplay gameplay = new Gameplay(stage, methodeMouton, methodeLoup);
            gameplay.demarrer();
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText("Erreur lors du lancement du jeu : " + e.getMessage());
            alert.showAndWait();
        }
    }
}
