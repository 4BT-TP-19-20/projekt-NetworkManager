package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 */

public class Main extends Application {

    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    ScheduledFuture<?> scheduledFuture;
    private Group group;
    private Scene scene;
    private Computer[][] computer;
    private Label[][] labels;
    private Rectangle rectangle1, rectangle2;
    private double sceneWidth = 700;
    private double sceneHeight = 600 + 20;
    private MenuBar menubar = new MenuBar();
    @Override
    public void start(Stage primaryStage) throws Exception {
        group = new Group();
        computer = new Computer[4][6];
        scene = new Scene(group, sceneWidth, sceneHeight);
        labels = new Label[4][6];
        List<List<String>> list = readfromcsv(); //Kofigurationsdatei mit MAC und IP Adressen wird eingelesen
        int xCoord = 0;
        int labelCount = 0;

        MenuItem[] menuItems = new MenuItem[2];
        menuItems[0] = new MenuItem("How to use");
        menuItems[0].setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Ist der PC rot ist er ausgeschaltet oder nicht erreichbar.\n" +
                    "Ist der PC grün ist er eingeschaltet.\n" +
                    "Bei einem eifachen Click auf einen Computer wird der Computer mit WOL aufgeweckt.\n" +
                    "Bei einem doppelten Click auf einen Computer wird sich über RDP mit dem Computer verbunden.\n"+
                    "Mit change PC-Info kann man die MAC und IP Adressen der PCs ändern.\n"+
                    "Manchmal werden einige PCs nicht richtig verschoben.");
            alert.showAndWait();
        });

        menuItems[1] = new MenuItem("change PC-Info");
        menuItems[1].setOnAction(event -> {
            openCSVFile(new File("computer.csv"));

        });

        Menu menu = new Menu("Options");
        menu.getItems().addAll(menuItems);
        menubar.setPrefWidth(scene.getWidth());
        menubar.getMenus().add(menu);
        menubar.setPrefHeight(25);
        menubar.setMinHeight(25);
        System.out.println(menubar.getHeight());
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 6; ++j) {
                ImageView imageView = new ImageView(new Image(new FileInputStream("PC_icon.png"))); //Neues Imageview wird aus dem Bild "PC_icon.png" erstellt
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setX(xCoord);
                imageView.setY(100 * j);

                int finalI = i;
                int finalJ = j;
                imageView.setOnMouseClicked(event -> { //Wenn ein Computer geclickt wird, wird der Code im Lamda ausgefürht
                    if (event.getClickCount() == 1) { //bei einfachem Click wird der Computer aufgeweckt
                        scheduledFuture = executor.schedule(() -> { //wird nur ausgefürht, wenn innerhalb von 500ms kein 2. Click erfolgt
                            Thread thread2 = new Thread(new WakeOnLan(computer[finalI][finalJ]));
                            thread2.start();
                        }, 500, TimeUnit.MILLISECONDS);

                    } else if (event.getClickCount() == 2) { //bei doppeltem Click wird RDP ausgeführt
                        if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
                            scheduledFuture.cancel(false);
                            RemoteAccess remoteAccess = new RemoteAccess(computer[finalI][finalJ]);
                            Thread thread3 = new Thread(remoteAccess);
                            thread3.start();
                        }

                    }
                });

                //Eigenschaften der Labels mit der PC-Nummer werden gesetzt
                labels[i][j] = new Label(Integer.toString(labelCount + 1));
                labels[i][j].setAlignment(Pos.CENTER);
                labels[i][j].setPrefWidth(100);
                labels[i][j].setMinWidth(100);
                labels[i][j].setLayoutX(xCoord + (labels[i][j].getWidth() / 2));
                labels[i][j].setLayoutY(100 * j + 15);
                labels[i][j].setTextFill(Color.DARKGRAY);
                labels[i][j].setMouseTransparent(true);
                labels[i][j].setStyle("-fx-font-size: 14");

                group.getChildren().add(imageView);
                group.getChildren().add(labels[i][j]);
                computer[i][j] = new Computer(list.get(labelCount).get(0), list.get(labelCount).get(1), imageView, labels[i][j]);
                ++labelCount;
            }
            xCoord += 100;
            if (i == 0) { //Die 2 "Tische" sollen nur zwischen Spalte 1 und 2 und zwischen Spalte 3 und 4 sein
                rectangle1 = new Rectangle(100, 600);
                rectangle1.setX(xCoord);
                rectangle1.setY(0);
                rectangle1.setFill(Color.DIMGRAY);
                group.getChildren().add(rectangle1);
            } else if (i == 2) {
                rectangle2 = new Rectangle(100, 600);
                rectangle2.setX(xCoord);
                rectangle2.setY(0);
                rectangle2.setFill(Color.DIMGRAY);
                group.getChildren().add(rectangle2);
            }
            xCoord += 100;
        }

        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            updateWidth((double) newSceneWidth);
        });
        scene.heightProperty().addListener((observableValue, oldSceneHight, newSceneHight) -> {
            updateHeight((double) newSceneHight);
        });

        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 4; ++i) {
                    for (int j = 0; j < 6; ++j) {
                        Thread t = new Thread(new PingComputer(computer[i][j]));
                        t.start();
                    }
                }
            }
        }, 0, 10000); //Wiederholt den Task alle 10 Sekunden
        scene.setFill(Color.DARKGRAY);

        primaryStage.setOnCloseRequest(event -> {
            t.cancel(); //sonst läuft Timer immer weiter
        });

        group.getChildren().add(menubar);
        primaryStage.setTitle("NetworkManager");
        primaryStage.setMinWidth(350);
        primaryStage.setMinHeight(300);
        primaryStage.getIcons().add(new Image(new FileInputStream("icon.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * Passt die Breite der Elemente in der Scene an, wenn das Fenster breiter oder schmaler wird
     *
     * @param newSceneWidth neue Größe der Scene
     */
    private void updateWidth(double newSceneWidth) {
        double multiplikator = newSceneWidth / sceneWidth;
        sceneWidth = newSceneWidth;

        for (int i = 0; i <= 3; ++i) {
            for (Computer c : computer[i]) {
                c.getImageView().setX(c.getImageView().getX() * multiplikator);
                c.getImageView().setFitWidth(c.getImageView().getFitWidth() * multiplikator);
                c.getLabel().setLayoutX(c.getLabel().getLayoutX() * multiplikator);
                c.getLabel().setMinWidth(c.getLabel().getMinWidth() * multiplikator);
                c.getLabel().setPrefWidth(c.getLabel().getMinWidth());
            }
        }

        rectangle1.setX(computer[0][0].getImageView().getFitWidth());
        rectangle1.setWidth(computer[1][0].getImageView().getX() - computer[0][0].getImageView().getFitWidth());

        rectangle2.setX(computer[2][0].getImageView().getX() + computer[2][0].getImageView().getFitWidth());
        rectangle2.setWidth(computer[3][0].getImageView().getX() - (computer[2][0].getImageView().getX() + computer[2][0].getImageView().getFitWidth()));

    }


    /**
     * Passt die Höhe der Elemente in der Scene an, wenn das Fenster höher oder tiefer gemacht wird
     *
     * @param newSceneHeight aktuelle SceneHeight
     */
    private void updateHeight(double newSceneHeight) {
        double multiplikator = newSceneHeight / sceneHeight;
        sceneHeight = newSceneHeight;

        for (int i = 0; i <= 3; ++i) {
            for (Computer c : computer[i]) {
                c.getImageView().setY(c.getImageView().getY() * multiplikator);
                c.getImageView().setFitHeight(c.getImageView().getFitHeight() * multiplikator);
                c.getLabel().setLayoutY(c.getLabel().getLayoutY() * multiplikator);
            }
        }
        rectangle1.setHeight(rectangle1.getHeight() * multiplikator);
        rectangle2.setHeight(rectangle2.getHeight() * multiplikator);
    }

    /**
     * liest CSV-Datei aus
     *
     * @return Liste mit Listen von Strings
     */
    public List<List<String>> readfromcsv() {
        List<List<String>> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("computer.csv"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                list.add(Arrays.asList(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * öffnet übergebene Dateien
     */
    public void openCSVFile(File file){
        //überprüft, ob Desktop unterstützt wird
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop wird nicht unterstützt!");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if(file.exists()) {
            try {
                desktop.open(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ändert die Farbe des übergebenen Computers zu rot
     *
     * @param computer Computer, dessen Farbe geändert wird
     */
    public void changeToRed(Computer computer) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0);
        colorAdjust.setHue(0.0);
        colorAdjust.setBrightness(0);
        colorAdjust.setSaturation(1);
        computer.getImageView().setEffect(colorAdjust);
    }

    /**
     * ändert die Farbe des übergebenen Computers zu grün
     *
     * @param computer Computer, dessen Farbe geändert wird
     */
    public void changeToGreen(Computer computer) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0);
        colorAdjust.setHue(0.6);
        colorAdjust.setBrightness(0);
        colorAdjust.setSaturation(1);
        computer.getImageView().setEffect(colorAdjust);
    }


    public static void main(String[] args) {
        launch(args);
        System.exit(0);
    }

}
