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

public class LoanServer extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextArea ta = new TextArea();

        Scene scene = new Scene(new javafx.scene.control.ScrollPane(ta), 450, 200);
        primaryStage.setTitle("Loan Calculator Server");
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
                    double annualRate = inputFromClient.readDouble();
                    int numberOfYear = inputFromClient.readInt();
                    double loanAmount = inputFromClient.readDouble();

                    Loan getPayment = new Loan(annualRate, numberOfYear, loanAmount);
                    double monthlyPayment =  getPayment.getMonthlyPayment();
                    double totalPayment  = getPayment.getTotalPayment();

                    outputToClient.writeDouble(monthlyPayment);
                    outputToClient.writeDouble(totalPayment);

                    Platform.runLater(() -> {
                        ta.appendText("Annual Interest: " + annualRate + "\n Number of years: " + numberOfYear +
                                        "\n Loan Amount: " +loanAmount + "\n"  );
                        ta.appendText("Monthly Payment: " + monthlyPayment + " " + "\n Total Payment: " +
                                        totalPayment + "\n\n");

                    });

                }

            } catch (IOException e){
                e.printStackTrace();
            }
        }).start();
    }

    public static void main(String[] args) {
        launch();
    }
}
