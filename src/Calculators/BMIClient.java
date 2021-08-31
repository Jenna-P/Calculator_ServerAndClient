package Calculators;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class BMIClient extends Application {

    DataOutputStream toServer;
    DataInputStream fromServer;


    @Override
    public void start(Stage primaryStage) {

        TextArea ta = new TextArea();
        TextField tfWeight = new TextField();
        TextField tfHeight = new TextField();
        Button btSubmit = new Button("SUBMIT");

        GridPane gp = new GridPane();
        gp.add(new Label("Weight in kilogram "), 0, 0);
        gp.add(new Label("Height in meters "), 0, 1);

        gp.add(tfWeight, 1, 0);
        gp.add(tfHeight, 1, 1);
        gp.add(btSubmit, 2, 1);

        BorderPane pane = new BorderPane();
        pane.setTop(gp);
        pane.setCenter(new ScrollPane(ta));

        Scene scene = new Scene(pane, 450, 290);
        primaryStage.setTitle("BMI Calculator Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        btSubmit.setOnAction(e -> {

            try {
                double weight = Double.parseDouble(tfWeight.getText().trim());
                double height = Double.parseDouble(tfHeight.getText().trim());

                toServer.writeDouble(weight);
                toServer.writeDouble(height);
                toServer.flush();

                double bmi = fromServer.readDouble();
                String bmiCategory = fromServer.readUTF();

                ta.appendText("Weight: " + weight + '\n');
                ta.appendText("Height: " + height + '\n');
                ta.appendText("BMI is " + bmi + "." + " " + bmiCategory);

            }catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        try {

            Socket socket = new Socket("localhost", 7777);

            fromServer = new DataInputStream(socket.getInputStream());
            toServer = new DataOutputStream(socket.getOutputStream());

        } catch (IOException ex){
            ta.appendText(ex.toString() + '\n');

        }

    }

    public static void main(String[] args) {
        launch();
    }
}
