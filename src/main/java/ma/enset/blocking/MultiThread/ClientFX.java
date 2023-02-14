package ma.enset.blocking.MultiThread;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ClientFX extends Application {
    PrintWriter pw;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Who Chat");
        BorderPane borderPane = new BorderPane();
        ////
        Label labelHost = new Label("IP HOST");
        TextField textFieldHost = new TextField("localhost");

        Label labelPort = new Label("PORT");
        TextField textFieldPort = new TextField("1111");

        Button buttonConnecter = new Button("Connecter");

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(10));
        hBox.setBackground(Background.fill(Color.AZURE));
        hBox.getChildren().addAll(labelHost,textFieldHost,labelPort,textFieldPort,buttonConnecter);
        borderPane.setTop(hBox);

        ObservableList<String> observableList = FXCollections.observableArrayList();
        ListView<String> listView = new ListView<>(observableList);
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        vBox.getChildren().add(listView);
        borderPane.setCenter(vBox);


        Label labelMsg = new Label("Message");
        TextField textFieldMsg = new TextField();
        textFieldMsg.setPrefSize(300,50);

        Button buttonMsg = new Button("Envoyer");

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(10));
        hBox1.setBackground(Background.fill(Color.AZURE));
        hBox1.getChildren().addAll(labelMsg,textFieldMsg,buttonMsg);
        borderPane.setBottom(hBox1);


        ////
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();

        buttonConnecter.setOnAction((actionEvent) -> {
            String host = textFieldHost.getText();
            int port = Integer.parseInt(textFieldPort.getText());
            try {
                Socket socket = new Socket(host,port);
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);

                OutputStream os = socket.getOutputStream();
                pw = new PrintWriter(os,true);

                new Thread(() -> {
                    while (true) {
                        try {
                            String response = br.readLine();
                            Platform.runLater(() -> {
                                observableList.add(response);
                            });
                        }catch (IOException e) {
                                throw new RuntimeException(e);
                        }
                    }
                }).start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hBox.setVisible(true);
        });

        buttonMsg.setOnAction((event) -> {
            String message = textFieldMsg.getText();
            pw.println(message);
        });
    }
}
