package Calculators;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class BMIServer extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextArea ta = new TextArea();

        Scene scene = new Scene(new javafx.scene.control.ScrollPane(ta), 450, 200);
        primaryStage.setTitle("BMI Calculator Server");
        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> {
            try {
                ServerSocket serverSocket = new ServerSocket(7777);
                Platform.runLater(() ->
                        ta.appendText("Server started at " + new Date() +  '\n'));

                Socket socket = serverSocket.accept();
                Platform.runLater(() ->
                        ta.appendText("Connected to a client at " + new Date() +  "\n\n"));

                DataInputStream inputFromClient = new DataInputStream(socket.getInputStream());
                DataOutputStream outputToClient = new DataOutputStream(socket.getOutputStream());

                while(true) {
                    double weight = inputFromClient.readDouble();
                    double height = inputFromClient.readDouble();

                    double bmi = (100 * 100 *weight) / (height * height);
                    String bmiCategory = printBMICategory(bmi);

                    outputToClient.writeDouble(bmi);
                    outputToClient.writeUTF(bmiCategory);


                    Platform.runLater(() -> {
                        ta.appendText("Weight: " + weight + "\n Height: " + height + "\n"  );
                        ta.appendText("BMI is " + bmi + "." + " " + bmiCategory + "\n\n");


                    });

                }

            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    private String printBMICategory(double bmi) {
        if(bmi < 18.5) {
            return "underweight";
        }else if (bmi < 25) {
            return "normal";
        }else if (bmi < 30) {
            return "overweight";
        }else {
           return "obese";
        }
    }

    public static void main(String[] args) {
        launch();
    }

}
