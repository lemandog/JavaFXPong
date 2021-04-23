package org.lemandog;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.net.URL;
import java.util.Objects;

public class App extends Application {
    public static Group root = new Group();
    public static Scene mainScene = new Scene(root,1200,600, Color.BLACK);
    public static Rectangle play1 = new Rectangle();
    public static Rectangle play2 = new Rectangle();
    public static Circle ball = new Circle();
    public static Text scoreAIT = new Text();
    public static Text scorePLT = new Text();
    public static int scoreAI = 0;
    public static int scorePL = 0;
    public static double xAcse = 4.5;
    public static double yAcse = 5.5;
    public static Timeline timeline;
    public static Timeline timeline2;
    @Override
    public void start(Stage primaryStage) {
        uiSetup(primaryStage);
        mainScene.setOnMouseMoved(mouseEvent -> play1.setY(mouseEvent.getY()) );
        startGameplay();
    }

    public static void startGameplay() {
        if (timeline == null) {
            // lazily create timelines
            timeline = new Timeline(new KeyFrame(Duration.millis(20), event -> NewPositionBall()));
            timeline.setCycleCount(Animation.INDEFINITE);

            timeline2 = new Timeline(new KeyFrame(Duration.millis(20), event -> NewEnemyPos()));
            timeline2.setCycleCount(Animation.INDEFINITE);
        }
        timeline.play();
        timeline2.play();
    }

    private static void NewEnemyPos() {
        double enemyAcse = 1.2f;
            if(play2.getY() > ball.getCenterY()-play2.getHeight()/2){ //If ball is lower
                play2.setY((play2.getY())-Math.abs(yAcse*enemyAcse)/2);
            } //Set new Y coord which equals old, moving toward ball
            else{                                 //If ball is higher
                play2.setY((play2.getY())+Math.abs(yAcse*enemyAcse)/2); }
    }

    private static void NewPositionBall() {

        if (ball.intersects(play1.getLayoutBounds())){ball.setCenterX(ball.getCenterX()+5);xAcse = - xAcse; playSound("Pling");}
        if (ball.intersects(play2.getLayoutBounds())){ball.setCenterX(ball.getCenterX()-5);xAcse = - xAcse; playSound("Pling");}
        if (ball.getCenterX()>1170){
            playSound("Score");
            scorePL++;
            scorePLT.setText(String.valueOf(scorePL));
            returnBall();
        }
        if (ball.getCenterX()<30){
            playSound("Score");
            scoreAI++;
            scoreAIT.setText(String.valueOf(scoreAI));
            returnBall();
        }

        ball.setCenterX(ball.getCenterX() + xAcse);
        ball.setCenterY(ball.getCenterY() + yAcse);
        //BORDER
        if (ball.getCenterY()<0){ yAcse = -yAcse; playSound("Pling");}
        if (ball.getCenterY()>mainScene.getHeight()){yAcse = -yAcse; playSound("Pling");}
        if(scoreAI>20 || scorePL> 20){ //END GAME when limit is broken
            timeline.stop();
            if(scoreAI>20){scoreAIT.setText("MACHINE IS VICTORIOUS!"); scorePLT.setText(""); scoreAIT.setX(scorePLT.getX());}
            if(scorePL>20){scorePLT.setText("HUMAN IS VICTORIOUS!"); scoreAIT.setText("");}
        }
    }

    private static void returnBall() {
        ball.setCenterX(mainScene.getWidth()/2);
        ball.setCenterY(mainScene.getHeight()/2);
        xAcse = xAcse*1.05;
        yAcse = yAcse*1.08;
    }

    public static synchronized void playSound(String type) {
        Thread sound = new Thread(() -> {
            try {
                Clip clip = AudioSystem.getClip();
                URL soundURL;
                if (type.equals("Score")) {
                    soundURL = App.class.getResource("/paba.wav");
                } else{
                    soundURL = App.class.getResource("/pling.wav");
                }
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(Objects.requireNonNull(soundURL));
                clip.open(inputStream);
                clip.start();
                clip.drain();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        });
        sound.start();
    }
    private void uiSetup(Stage primaryStage) {
        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        Image icon = new Image("/PONG.png");
        primaryStage.getIcons().add(icon);
        //USER`S BRICK
        play1.setX(50);
        play1.setY(200);
        play1.setWidth(40);
        play1.setHeight(100);
        play1.setFill(Color.BLUE);
        play1.setStroke(Color.BEIGE);

        //AI`S BRICK
        play2.setX(1100);
        play2.setY(200);
        play2.setWidth(40);
        play2.setHeight(100);
        play2.setFill(Color.RED);
        play2.setStroke(Color.BEIGE);

        ball.setRadius(20);
        ball.setStroke(Color.BEIGE);
        ball.setFill(Color.WHITE);

        scorePLT.setY(60);
        scorePLT.setFont(new Font("Times New Roman", 64));
        scorePLT.setFill(Color.BLUE);
        scorePLT.setText(String.valueOf(scorePL));
        scorePLT.setStroke(Color.WHITE);

        scoreAIT.setY(60);
        scoreAIT.setFont(new Font("Times New Roman", 64));
        scoreAIT.setFill(Color.RED);
        scoreAIT.setText(String.valueOf(scoreAI));
        scoreAIT.setStroke(Color.WHITE);

        Line center = new Line();
        center.setStartX(mainScene.getWidth()/2);
        center.setEndX(mainScene.getWidth()/2);
        center.setStartY(0);
        center.setEndY(mainScene.getHeight());
        center.setStroke(Color.GRAY);

        root.getChildren().add(center);
        root.getChildren().add(play1);
        root.getChildren().add(play2);
        root.getChildren().add(ball);

        root.getChildren().add(scoreAIT);
        root.getChildren().add(scorePLT);

        primaryStage.setTitle("PONG");
        primaryStage.show();

        scoreAIT.setX(3*primaryStage.getWidth()/4);
        scorePLT.setX(primaryStage.getWidth()/4);

        ball.setCenterX(mainScene.getWidth()/2);
        ball.setCenterY(mainScene.getHeight()/2);


    }
    public static void main(String[] args) {
        launch(args);
    }
}
