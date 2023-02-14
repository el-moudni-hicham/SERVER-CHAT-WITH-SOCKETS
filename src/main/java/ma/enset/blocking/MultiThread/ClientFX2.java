package ma.enset.blocking.MultiThread;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ClientFX2 extends Application {
    PrintWriter pw;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("HM Messanger");
        BorderPane borderPane = new BorderPane();
        borderPane.setStyle("-fx-background-color: white;"+"-fx-font-weight: bold;");
        ////
        Label title = new Label("HM Messanger");
        title.setAlignment(Pos.CENTER);

        title.setStyle("-fx-font-family: Corbel;"+"-fx-font-size: 24px;"+"-fx-text-fill: #0f7df2;");

        Label labelHost = new Label("");
        TextField textFieldHost = new TextField("localhost");

        Label labelPort = new Label(":");
        TextField textFieldPort = new TextField("1111");

        Button buttonConnecter = new Button("Connect");

        Label labelTo = new Label("To : ");
        TextField textFieldTo = new TextField();
        textFieldTo.setPrefWidth(50);
        textFieldTo.setStyle("-fx-background-color: #c9c9c9;");

        HBox hBox6 = new HBox();
        hBox6.setSpacing(10);
        hBox6.setPadding(new Insets(5));
        //hBox.setBackground(Background.fill(Color.AZURE));
        hBox6.getChildren().addAll(labelTo,textFieldTo);

        HBox hBox = new HBox();
        hBox.setSpacing(10);
        hBox.setPadding(new Insets(5));
        //hBox.setBackground(Background.fill(Color.AZURE));
        hBox.getChildren().addAll(labelHost,textFieldHost,labelPort,textFieldPort,buttonConnecter);
        VBox vBox5 = new VBox();
        vBox5.setAlignment(Pos.CENTER);
        vBox5.getChildren().addAll(title,hBox,hBox6);
        vBox5.setSpacing(10);
        borderPane.setTop(vBox5);

        VBox vBox = new VBox();
        vBox.setPrefSize(248,412);
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(256,418);
        scrollPane.setFitToWidth(true);
        scrollPane.setLayoutX(30);
        scrollPane.setLayoutY(70);
        scrollPane.setStyle("-fx-background-color: white;");
        scrollPane.setContent(vBox);
        borderPane.setCenter(scrollPane);

        //Label labelMsg = new Label("Message");
        TextField textFieldMsg = new TextField();
        textFieldMsg.setPromptText("Your Message");
        textFieldMsg.setPrefSize(236, 34);
        textFieldMsg.setStyle("-fx-background-color: #c9c9c9;"+"-fx-background-radius: 30px;");

        Button buttonMsg = new Button("Send");
        buttonMsg.setPadding(new Insets(5));
        buttonMsg.setStyle("-fx-background-color: transparent;");

        HBox hBox1 = new HBox();
        hBox1.setSpacing(10);
        hBox1.setPadding(new Insets(10));
        //hBox1.setBackground(Background.fill(Color.AZURE));
        hBox1.getChildren().addAll(textFieldMsg,buttonMsg);
        borderPane.setBottom(hBox1);


        ////
        Scene scene = new Scene(borderPane,440, 480);
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
                            HBox hBox2 = new HBox();
                            hBox2.setAlignment(Pos.CENTER_LEFT);
                            hBox2.setPadding(new Insets(5, 5, 5, 10));

                            Text text = new Text(response);
                            TextFlow textFlow = new TextFlow(text);

                            textFlow.setStyle(
                                    "-fx-background-color: rgb(233, 233, 235);" +
                                            "-fx-background-radius: 20px;");

                            textFlow.setPadding(new Insets(5, 10, 5, 10));
                            hBox2.getChildren().add(textFlow);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    vBox.getChildren().add(hBox2);
                                }
                            });
                        }catch (IOException e) {
                                throw new RuntimeException(e);
                        }

                    }
                }).start();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            hBox.setVisible(false);
        });

        buttonMsg.setOnAction((event) -> {
            String to = textFieldTo.getText();
            String messageToSend = textFieldMsg.getText();
            if (!messageToSend.isEmpty()) {
                HBox hBox3 = new HBox();
                hBox3.setAlignment(Pos.CENTER_RIGHT);

                hBox3.setPadding(new Insets(5, 5, 5, 10));
                Text text = new Text(messageToSend);
                TextFlow textFlow = new TextFlow(text);
                textFlow.setStyle(
                        "-fx-color: rgb(239, 242, 255);" +
                                "-fx-background-color: rgb(15, 125, 242);" +
                                "-fx-background-radius: 20px;");

                textFlow.setPadding(new Insets(5, 10, 5, 10));
                text.setFill(Color.color(0.934, 0.925, 0.996));

                hBox3.getChildren().add(textFlow);
                vBox.getChildren().add(hBox3);

                if(to.isEmpty()){
                    pw.println(messageToSend);
                } else{
                    pw.println(to+">"+messageToSend);
                }

                textFieldMsg.clear();
                textFieldTo.clear();
            }
        });
    }
}
