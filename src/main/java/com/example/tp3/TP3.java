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

import java.io.IOException;

public class TP3 extends Application {
    Pane root;
    Rectangle barre;
    Circle balle;
    Text debutDePartieText;
    Text scoreText;
    Text finDePartieText;
    ImageView imageViewExplosionViolet;
    ImageView imageViewExplosionRouge;

    double dxballe = 5;
    double dyballe = 5;
    int score = 0;
    boolean partieEnCours = false;

    @Override
    public void start(Stage mainwindow) throws IOException {
        root = new Pane();
        mainwindow.setScene(new Scene(root, 800, 600));
        mainwindow.setTitle("Pong Solo");

        root.setOnMouseClicked(e ->  resetJeu());

        debutDePartieText = new Text("Cliquez pour commencer la partie !");
        debutDePartieText.setStyle("-fx-font: 24 arial;");
        debutDePartieText.setLayoutX(220);
        debutDePartieText.setLayoutY(250);
        root.getChildren().add(debutDePartieText);

        scoreText = new Text("Score : " + score);
        scoreText.setStyle("-fx-font: 24 arial;");
        scoreText.setLayoutX(350);
        scoreText.setLayoutY(100);
        root.getChildren().add(scoreText);

        root.getChildren().add(barre());
        root.getChildren().add(balle());

        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                bouger_barre(mouseEvent.getSceneX(), mouseEvent.getSceneY());
            }
        });

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(20), e-> jeu()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        mainwindow.show();
    }

    public void resetJeu(){
        if(!partieEnCours) {
            score = 0;
            miseAJourScore(0);

            root.getChildren().remove(finDePartieText);

            partieEnCours = true;
        }
    }

    public void jeu(){
        if(partieEnCours) {
            root.getChildren().remove(debutDePartieText);

            if (balle.getLayoutX() >= root.getWidth()) {
                dxballe = dxballe * -1;
                root.getChildren().remove(imageViewExplosionRouge);
                root.getChildren().add(gifExplosionMur(balle.getLayoutX(), balle.getLayoutY()));
            }

            if (balle.getLayoutY() > root.getHeight()) {
                dyballe = dyballe * -1;
                root.getChildren().remove(imageViewExplosionRouge);
                root.getChildren().add(gifExplosionMur(balle.getLayoutX(), balle.getLayoutY()));
            }

            if (balle.getLayoutX() <= 0) {
                System.out.println("Partie perdue");
                partieEnCours = false;

                finDePartieText = new Text("Partie perdue");
                finDePartieText.setStyle("-fx-font: 24 arial;");
                finDePartieText.setStroke(Color.RED);
                finDePartieText.setFill(Color.RED);
                finDePartieText.setLayoutX(330);
                finDePartieText.setLayoutY(150);
                root.getChildren().add(finDePartieText);

                debutDePartieText = new Text("Cliquez pour commencer la partie !");
                debutDePartieText.setStyle("-fx-font: 24 arial;");
                debutDePartieText.setLayoutX(220);
                debutDePartieText.setLayoutY(250);
                root.getChildren().add(debutDePartieText);
            }

            if (balle.getLayoutY() <= 0) {
                dyballe = dyballe * -1;
                root.getChildren().remove(imageViewExplosionRouge);
                root.getChildren().add(gifExplosionMur(balle.getLayoutX(), balle.getLayoutY()));
            }

            if (balle.getLayoutX() == positionBarreX() + 50 && balle.getLayoutY() >= positionBarreY() && balle.getLayoutY() <= positionBarreY() + 100) {
                root.getChildren().remove(imageViewExplosionViolet);
                System.out.println("Touche");

                dxballe = dxballe * -1;

                miseAJourScore(1);

                root.getChildren().remove(imageViewExplosionViolet);
                root.getChildren().add(gifExplosionBarre(balle.getLayoutX(), balle.getLayoutY()));
            }

            bouger_balle(dxballe, dyballe);
        }
        else{
            balle.setLayoutX(root.getWidth()/2);
            balle.setLayoutY(root.getHeight()/2);
        }
    }

    public Rectangle barre(){
        barre = new Rectangle(50,100);
        barre.setLayoutX(10);

        return barre;
    }

    public double positionBarreY(){
        return barre.getLayoutY();
    }

    public double positionBarreX(){
        return barre.getLayoutX();
    }

    public void bouger_barre(double x, double y){
        barre.setLayoutY(y - 50);
    }

    public Circle balle(){
        balle = new Circle(10);
        balle.setLayoutX(root.getWidth()/2);
        balle.setLayoutY(root.getHeight()/2);

        return balle;
    }

    public void bouger_balle(double x, double y){
        balle.setLayoutX(balle.getLayoutX() + x);
        balle.setLayoutY(balle.getLayoutY() + y);
    }

    public void miseAJourScore(int scoreAAjouter){
        root.getChildren().remove(scoreText);

        score = score + scoreAAjouter;

        scoreText = new Text("Score : " + score);
        scoreText.setStyle("-fx-font: 24 arial;");
        scoreText.setLayoutX(350);
        scoreText.setLayoutY(100);
        root.getChildren().add(scoreText);
    }

    public ImageView gifExplosionBarre(double x, double y){
        Image explosion = new Image(this.getClass().getResource("/images/explosion_violet.gif").toExternalForm());

        imageViewExplosionViolet = new ImageView(explosion);
        imageViewExplosionViolet.setScaleX(0.3);
        imageViewExplosionViolet.setScaleY(0.3);
        imageViewExplosionViolet.setLayoutX(x-185);
        imageViewExplosionViolet.setLayoutY(y-150);

        return imageViewExplosionViolet;
    }

    public ImageView gifExplosionMur(double x, double y){
        Image explosion = new Image(this.getClass().getResource("/images/explosion_rouge.gif").toExternalForm());

        imageViewExplosionRouge = new ImageView(explosion);
        imageViewExplosionRouge.setScaleX(0.3);
        imageViewExplosionRouge.setScaleY(0.3);
        imageViewExplosionRouge.setLayoutX(x-150);
        imageViewExplosionRouge.setLayoutY(y-150);

        return imageViewExplosionRouge;
    }

    public static void main(String[] args) {
        launch();
    }
}