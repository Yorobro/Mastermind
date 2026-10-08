package com.mastermind;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("Mastermind");
        stage.setScene(new Scene(new StackPane(), 800, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
