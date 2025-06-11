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





    @Override
    public void start(Stage primaryStage) {
        // Fenêtre principale (menu)
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
            alert.setContentText("""
                                    • Soulayman SAIDI
                              
                                    • Université Polytechnique Hauts-de-France
                                    • Projet SAE 2.01   
                                    
                                    Images : Ressources libres ou créées
                                """);
            alert.showAndWait();
        });



    }

    private void openGameScene(Stage stage) {
        loupPlace = false;
        moutonPlace = false;
        sortie = false;



        TabPane tabs = new TabPane();
        Tab tabTailleLaby = new Tab("Taille Labyrinthe");
        Tab tabLabyrinthe = new Tab("Labyrinthe");

        tabLabyrinthe.setClosable(false);
        tabTailleLaby.setClosable(false);


        TextField ligneField = new TextField();
        ligneField.setPromptText("Nombre de lignes");

        TextField colonneField = new TextField();
        colonneField.setPromptText("Nombre de colonnes");

        Button validerCreationLaby = new Button("Valider");

        VBox formulaire = new VBox(10, ligneField, colonneField, validerCreationLaby);

        formulaire.setPadding(new Insets(20));
        tabTailleLaby.setContent(formulaire);

        Pane labyPane = new Pane();
        tabLabyrinthe.setContent(labyPane);
        tabs.getTabs().addAll(tabTailleLaby, tabLabyrinthe);

        validerCreationLaby.setOnAction(e -> {
            int lignes = Integer.parseInt(ligneField.getText());
            int colonnes = Integer.parseInt(colonneField.getText());

            char[][] laby = Outils.initLabyrinthe(lignes, colonnes);
            labyPane.getChildren().clear();


            for (int i = 0; i < laby.length; i++) {
                for (int j = 0; j < laby[i].length; j++) {
                    Rectangle rect = new Rectangle(j * 50, i * 50, 50, 50);
                    rect.setStroke(Color.BEIGE);

                    char cell = laby[i][j];
                    ImageView imageView = createImageViewFromChar(cell);
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

                            if (laby[fi][fj] == 'L') loupPlace = false;
                            if (laby[fi][fj] == 'O') moutonPlace = false;

                            // Pour que les coins ne puissent pas être touchés
                            if (    (fi == 0 && fj == 0) ||                                 // coin haut-gauche
                                    (fi == 0 && fj == colonnes - 1) ||                      // coin haut-droit
                                    (fi == lignes - 1 && fj == 0) ||                        // coin bas-gauche
                                    (fi == lignes - 1 && fj == colonnes - 1)                // coin bas-droit
                            ) return;

                            // Empêche de toucher les bords, sauf ceux en rocher ou en herbe, tant que la sortie n’est pas créée.
                            if (((symbole != 'H' && symbole != 'R')  &&
                                    (       (fi == 0 && fj > 0 && fj < colonnes - 1) ||                   // bordure haut
                                            (fi == lignes - 1 && fj > 0 && fj < colonnes - 1) ||         // bordure bas
                                            (fj == 0 && fi > 0 && fi < lignes - 1) ||                    // bordure gauche
                                            (fj == colonnes - 1 && fi > 0 && fi < lignes - 1) ))        // bordure droite
                                            || (symbole == 'H' && sortie))                              // si la sortie n'est pas créee
                                            return;

                            if (laby[fi][fj] == 'H') sortie = false;

                            laby[fi][fj] = symbole;

                            if (symbole == 'L') {
                                loupPlace = true;
                                Outils.loup = new Animal("L", pos);}

                            if (symbole == 'O') {
                                    moutonPlace = true;
                                    Outils.mouton = new Animal("O", pos);}

                            if (symbole == 'H') {
                                sortie = true;
                                Outils.sortie = pos;}

                            ImageView newImage = createImageViewFromChar(symbole);
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
        });


        VBox toolBox = createToolBox();

        HBox layout = new HBox(20);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(toolBox, tabs);

        Scene scene = new Scene(layout, 1000, 800);
        stage.setScene(scene);
    }

    private VBox createToolBox() {
        VBox tools = new VBox(10);
        tools.setPadding(new Insets(20));

        tools.getChildren().addAll(
                createToolButton("Mouton", "/images/Mouton.png", 'O'),
                createToolButton("Loup", "/images/Loup.png", 'L'),
                createToolButton("Marguerite", "/images/Margerite.png", 'M'),
                createToolButton("Cactus", "/images/Cactus.png", 'C'),
                createToolButton("Rocher", "/images/Rocher.png", 'R'),
                createToolButton("Herbe", "/images/Herbe.png", 'H'),
                createToolButton("Herbe", "/images/fin.png", 'F')

        );
        return tools;
    }





    private ToggleButton createToolButton(String name, String imagePath, char symbole) {
        Image image = new Image(Objects.requireNonNull(getClass().getResource(imagePath)).toExternalForm());
        ImageView view = new ImageView(image);
        view.setFitWidth(50);
        view.setFitHeight(50);

        ToggleButton button = new ToggleButton("", view);
        button.setUserData(symbole);
        button.setToggleGroup(toolsGroup);
        return button;
    }



    private ImageView createImageViewFromChar(char c) {
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
        Image image = new Image(Objects.requireNonNull(getClass().getResource(path)).toExternalForm());
        return new ImageView(image);
    }






    private void lancerJeu(Scene scene) {
        if (jeuLance) return;
        jeuLance = true;


        };


}
