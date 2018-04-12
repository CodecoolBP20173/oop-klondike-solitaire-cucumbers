package com.codecool.klondike;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;



import java.awt.*;

public class Klondike extends Application {

    private static final double WINDOW_WIDTH = 1400;
    private static final double WINDOW_HEIGHT = 900;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Card.loadCardImages();
        Game game = new Game();
        startGame(primaryStage, game);
    }
    public void startGame(Stage primaryStage, Game game){
        game.setTableBackground(new Image("/table/green.png"));

        MenuItem start = new MenuItem("Restart");
        MenuItem quit = new MenuItem("Quit game");

        start.setOnAction(event -> {Game newGame = game.newGame();
        startGame(primaryStage, newGame);});

        MenuButton menuButton = new MenuButton("Menu", null, start, quit);
        game.getChildren().addAll(menuButton);
        primaryStage.setTitle("Klondike Solitaire");
        primaryStage.setScene(new Scene(game, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();


    }

}
