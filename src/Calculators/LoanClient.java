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

public class LoanClient extends Application {

    DataOutputStream toServer;
    DataInputStream fromServer;


    @Override
    public void start(Stage primaryStage) {

        TextArea ta = new TextArea();
        TextField tfRate = new TextField();
        TextField tfNumOfYears = new TextField();
        TextField tfLoanAmount = new TextField();
        Button btSubmit = new Button("SUBMIT");

        GridPane gp = new GridPane();
        gp.add(new Label("Annual Interest Rate "), 0, 0);
        gp.add(new Label("Number Of Years "), 0, 1);
        gp.add(new Label("Loan Amount "), 0, 2);
        gp.add(tfRate, 1, 0);
        gp.add(tfNumOfYears, 1, 1);
        gp.add(tfLoanAmount, 1, 2);
        gp.add(btSubmit, 2, 1);

        BorderPane pane = new BorderPane();
        pane.setTop(gp);
        pane.setCenter(new ScrollPane(ta));

        Scene scene = new Scene(pane, 450, 290);
        primaryStage.setTitle("Loan Calculator Client");
        primaryStage.setScene(scene);
        primaryStage.show();

        btSubmit.setOnAction(e -> {

            try {
                double annualRate = Double.parseDouble(tfRate.getText().trim());
                double loanAmount = Double.parseDouble(tfLoanAmount.getText().trim());
                int numberOfYear = Integer.parseInt(tfNumOfYears.getText());

                toServer.writeDouble(annualRate);
                toServer.writeInt(numberOfYear);
                toServer.writeDouble(loanAmount);
                toServer.flush();

                double monthlyPayment = fromServer.readDouble();
                double totalPayment = fromServer.readDouble();

                ta.appendText("Annual Interest: " + annualRate + '\n');
                ta.appendText("Number of years: " + numberOfYear + '\n');
                ta.appendText("Loan Amount: " +loanAmount + '\n');
                ta.appendText("Monthly Payment: " + monthlyPayment + '\n');
                ta.appendText("TotalPayment: " + totalPayment + '\n');

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
