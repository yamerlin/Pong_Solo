/*
Nom du programme : TP3.java
Auteur : MERLIN Yann
Date de remise : 05/05/2023
 */

package com.example.tp3;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Classe principale qui contient tout le jeu et ses fonctions
 */
public class TP3 extends Application {
    /**
     * Pane sur lequel seront positionnés les éléments du jeu
     */
    Pane root;
    /**
     * Rectangle qui représente la barre de jeu
     */
    Rectangle barre;
    /**
     * Circle qui représente la balle
     */
    Circle balle;
    /**
     * Text de début de partie
     */
    Text debutDePartieText;
    /**
     * Text qui indique le score
     */
    Text scoreText;
    /**
     * Text de fin partie
     */
    Text finDePartieText;
    /**
     * Explosion violette sur la barre de jeu
     */
    ImageView imageViewExplosionViolet;
    /**
     * Explosion rouge sur les murs au contact de la balle
     */
    ImageView imageViewExplosionRouge;
    /**
     * Position x de la balle
     */
    double dxballe = 5;
    /**
     * Position y de la balle
     */
    double dyballe = 5;
    /**
     * Variable qui stock le score
     */
    int score = 0;
    /**
     * Variable booléenne qui indique si la partie est en cours ou non
     */
    boolean partieEnCours = false;

    /**
     * Fonction d'entrée qui crée la scène, la timeline et gère les mouvemements de la barre suivant la position de la souris
     * @param mainwindow
     * @throws IOException
     */
    @Override
    public void start(Stage mainwindow) throws IOException {

        //Pane sur lequel seront positionnés les éléments du jeu
        root = new Pane();

        //Déclaration de la taille de la fenêtre
        mainwindow.setScene(new Scene(root, 800, 600));
        mainwindow.setTitle("Pong Solo");

        //Quand la souris est cliquée, remettre les paramètres de jeu à leur état initial
        root.setOnMouseClicked(e ->  resetJeu());

        //Afficher le texte de début de partie
        debutDePartieText = new Text("Cliquez pour commencer la partie !");
        debutDePartieText.setStyle("-fx-font: 24 arial;");
        debutDePartieText.setLayoutX(220);
        debutDePartieText.setLayoutY(250);
        root.getChildren().add(debutDePartieText);

        //Afficher le score
        scoreText = new Text("Score : " + score);
        scoreText.setStyle("-fx-font: 24 arial;");
        scoreText.setLayoutX(350);
        scoreText.setLayoutY(100);
        root.getChildren().add(scoreText);

        //Ajouter la barre et la balle a la scène
        root.getChildren().add(barre());
        root.getChildren().add(balle());

        /**
         * Détecter les mouvements de la souris et appeler la fonction bouger_barre()
         */
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                bouger_barre(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        /**
         * Créer la timeline de jeu()
         */
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), e-> jeu()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        mainwindow.show();
    }

    /**
     * Fonction qui remet les paramètres de jeu à leur état initial
     */
    public void resetJeu(){
        if(!partieEnCours) {
            score = 0;
            miseAJourScore(0);

            root.getChildren().remove(finDePartieText);

            partieEnCours = true;
        }
    }

    /**
     * Fonction de jeu, contient les collisions avec la barre et les murs, et fait bouger la balle avec la fonction bouger_balle()
     */
    public void jeu(){
        //Si la partie est en cours
        if(partieEnCours) {

            //Enlever le texte de début de partie
            root.getChildren().remove(debutDePartieText);

            //Collision avec le mur du fon
            if (balle.getLayoutX() >= root.getWidth()) {
                //Changer la balle de direction
                dxballe = dxballe * -1;
                //Enlever l'ancien gif d'explosion s'il y en avait un
                root.getChildren().remove(imageViewExplosionRouge);
                //Ajouter un gif d'explosion
                root.getChildren().add(gifExplosionMur(balle.getLayoutX(), balle.getLayoutY()));
            }

            //Collision avec le mur du haut
            if (balle.getLayoutY() > root.getHeight()) {
                //Changer la balle de direction
                dyballe = dyballe * -1;
                //Enlever l'ancien gif d'explosion s'il y en avait un
                root.getChildren().remove(imageViewExplosionRouge);
                //Ajouter un gif d'explosion
                root.getChildren().add(gifExplosionMur(balle.getLayoutX(), balle.getLayoutY()));
            }

            //Collision avec le mur derrière la barre (une balle ratée)
            if (balle.getLayoutX() <= 0) {
                //Jouer le son de défaite
                SonDefaite();

                System.out.println("Partie perdue");

                //Mettre fin à la partie
                partieEnCours = false;

                //Afficher le texte de fin de partie
                finDePartieText = new Text("Partie perdue");
                finDePartieText.setStyle("-fx-font: 24 arial;");
                finDePartieText.setStroke(Color.RED);
                finDePartieText.setFill(Color.RED);
                finDePartieText.setLayoutX(330);
                finDePartieText.setLayoutY(150);
                root.getChildren().add(finDePartieText);

                //Afficher le texte de début de partie
                debutDePartieText = new Text("Cliquez pour commencer la partie !");
                debutDePartieText.setStyle("-fx-font: 24 arial;");
                debutDePartieText.setLayoutX(220);
                debutDePartieText.setLayoutY(250);
                root.getChildren().add(debutDePartieText);
            }

            //Collision avec le mur du bas
            if (balle.getLayoutY() <= 0) {
                //Changer la balle de direction
                dyballe = dyballe * -1;
                //Enlever l'ancien gif d'explosion s'il y en avait un
                root.getChildren().remove(imageViewExplosionRouge);
                //Ajouter un gif d'explosion
                root.getChildren().add(gifExplosionMur(balle.getLayoutX(), balle.getLayoutY()));
            }

            //Collision avec la barre
            if (balle.getLayoutX() == positionBarreX() + 50 && balle.getLayoutY() >= positionBarreY() && balle.getLayoutY() <= positionBarreY() + 100) {
                System.out.println("Touche");

                //Changer la balle de direction
                dxballe = dxballe * -1;

                //Ajouter un point
                miseAJourScore(1);

                //Enlever l'ancien gif d'explosion s'il y en avait un
                root.getChildren().remove(imageViewExplosionViolet);
                //Ajouter un gif d'explosion
                root.getChildren().add(gifExplosionBarre(balle.getLayoutX(), balle.getLayoutY()));
            }

            //Faire bouger la balle
            bouger_balle(dxballe, dyballe);
        }
        //Si la partie n'est pas en cours
        else{
            //Remettre la balle au milieu de la fenêtre
            balle.setLayoutX(root.getWidth()/2);
            balle.setLayoutY(root.getHeight()/2);
        }
    }

    /**
     * Méthode qui crée la barre
     * @return Retourne un rectangle qui représente la barre
     */
    public Rectangle barre(){
        barre = new Rectangle(50,100);
        barre.setLayoutX(10);

        return barre;
    }

    /**
     * Méthode qui récupère la position Y de la barre
     * @return Retourne un double qui correspond à la position Y de la barre
     */
    public double positionBarreY(){
        return barre.getLayoutY();
    }

    /**
     * Méthode qui récupère la position X de la barre
     * @return Retourne un double qui correspond à la position X de la barre
     */
    public double positionBarreX(){
        return barre.getLayoutX();
    }

    /**
     * Méthode qui fait bouger la barre
     * @param x Position x de la barre
     * @param y Position y de la barre
     */
    public void bouger_barre(double x, double y){
        barre.setLayoutY(y - 50);
    }

    /**
     * Méthode qui crée la balle
     * @return Retourne un cercle qui représente la balle
     */
    public Circle balle(){
        balle = new Circle(10);
        balle.setLayoutX(root.getWidth()/2);
        balle.setLayoutY(root.getHeight()/2);

        return balle;
    }

    /**
     * Méthode qui fait bouger la balle
     * @param x Position x de la balle
     * @param y Position y de la balle
     */
    public void bouger_balle(double x, double y){
        balle.setLayoutX(balle.getLayoutX() + x);
        balle.setLayoutY(balle.getLayoutY() + y);
    }

    /**
     * Méthode qui met à jour le score
     * @param scoreAAjouter Un integer qui représente le nombre de points à ajouter
     */
    public void miseAJourScore(int scoreAAjouter){
        //Enlever l'ancien score
        root.getChildren().remove(scoreText);

        //Incrémenter le score
        score = score + scoreAAjouter;

        //Afficher le nouveau score
        scoreText = new Text("Score : " + score);
        scoreText.setStyle("-fx-font: 24 arial;");
        scoreText.setLayoutX(350);
        scoreText.setLayoutY(100);
        root.getChildren().add(scoreText);
    }

    /**
     * Méthode qui fait apparaitre un gif d'explosion
     * @param x Position x de l'explosion
     * @param y Position y de l'explosion
     * @return Retourne une imageview à ajouter à la scène
     */
    public ImageView gifExplosionBarre(double x, double y){
        Image explosion = new Image(this.getClass().getResource("/images/explosion_violet.gif").toExternalForm());

        imageViewExplosionViolet = new ImageView(explosion);
        imageViewExplosionViolet.setScaleX(0.3);
        imageViewExplosionViolet.setScaleY(0.3);
        imageViewExplosionViolet.setLayoutX(x-185);
        imageViewExplosionViolet.setLayoutY(y-150);

        SonExplosion();

        return imageViewExplosionViolet;
    }

    /**
     * Méthode qui fait apparaitre un gif d'explosion
     * @param x Position x de l'explosion
     * @param y Position y de l'explosion
     * @return Retourne une imageview à ajouter à la scène
     */
    public ImageView gifExplosionMur(double x, double y){
        Image explosion = new Image(this.getClass().getResource("/images/explosion_mur.gif").toExternalForm());

        imageViewExplosionRouge = new ImageView(explosion);
        imageViewExplosionRouge.setScaleX(0.3);
        imageViewExplosionRouge.setScaleY(0.3);
        imageViewExplosionRouge.setLayoutX(x-150);
        imageViewExplosionRouge.setLayoutY(y-150);

        System.out.println("BOUM");

        return imageViewExplosionRouge;
    }

    /**
     * Méthode qui fait jouer un son d'explosion
     */
    public void SonExplosion(){
        Media sonExplosion = new Media(Paths.get("sonExplosion.mp3").toUri().toString());
        MediaPlayer player = new MediaPlayer(sonExplosion);
        player.play();
    }

    /**
     * Méthode qui fait jouer un son lors de la défaite
     */
    public void SonDefaite(){
        Media sonExplosion = new Media(Paths.get("defaite.mp3").toUri().toString());
        MediaPlayer player = new MediaPlayer(sonExplosion);
        player.setStartTime(Duration.millis(1));
        player.setStopTime(Duration.millis(3000));
        player.play();
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }
}