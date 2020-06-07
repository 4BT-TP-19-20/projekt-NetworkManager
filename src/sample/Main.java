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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.List;
import java.util.Timer;
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
    private Group group2;
    private Scene scene;
    private Scene scene2;
    private Computer[][] computer;
    private Computer[][] computer2;
    private Label[][] labels;
    private Label[][] labels2;
    private Rectangle rectangle1, rectangle2;
    private Rectangle rectangle21, rectangle22, rectangle23;
    private double sceneWidth = 700;
    private double sceneHeight = 600 + 20;
    private MenuBar menubar = new MenuBar();
    private boolean onSNLab;

    @Override
    public void start(Stage primaryStage) {


        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("mac")) {
            JOptionPane.showMessageDialog(null, "Oh mein Gott, du benutzsch net im Ernst grot MacOS\nBitte Loesch die...");
            System.exit(0);
        }

        //Wenn man VPN hot donn kimp net richtige IP

        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String ownIP = inetAddress.getHostAddress();

            //System.out.println(ownIP);
            if (!(ownIP.contains("10.10.30"))){
                JOptionPane.showMessageDialog(null, "Falls du durch das VPN mit dem Schulnetz Verbunden bist drücke OK\n" +
                        "Andernfalls starde die VPN-Verbindung oder verbinde dich direkt mit dem SN-Netz um das Programm richtig nutzen zu können.");
                //System.exit(0);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            System.exit(0);
        }


        onSNLab = true;
        MenuItem[] menuItems = new MenuItem[4];
        menuItems[0] = new MenuItem("Informationen");
        menuItems[0].setOnAction(event -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Informationen");
            alert.setHeaderText(null);
            alert.setContentText("DIESES PROGRMM IST FÜR DIE SCHULE AUSGELEGT UND WIRD NIRGENS ANDERS FUNKTIONIEREN, AUCH WENN DIE IP/MAC-ADRESSEN AN EIN ANDERES NETZ ANGEPASST WERDEN!!!\n\n" +
                    "Wenn der PC rot ist, ist er ausgeschaltet oder nicht erreichbar.\n" +
                    "Ist der PC grün, ist er eingeschaltet.\n" +
                    "Bei einem eifachen Click auf einen Computer wird der Computer per WOL aufgeweckt.\n" +
                    "Bei einem doppelten Click auf einen Computer wird man per RDP mit dem Computer verbunden.\n" +
                    "Mit PC-Info ändern kann man die MAC und IP Adressen der PCs ändern.\n");
            alert.showAndWait();
        });

        menuItems[1] = new MenuItem("PC-Info ändern");
        menuItems[1].setOnAction(event -> {
            if (onSNLab) {
                openCSVFile(new File("computer.csv"));
            } else {
                openCSVFile(new File("computer2.csv"));
            }

        });

        menuItems[2] = new MenuItem("Raum wechseln");
        menuItems[2].setOnAction(event -> {
            if (onSNLab) {
                onSNLab = false;
                sceneHeight = 600;
                sceneWidth = 880;

                for (int i = 0; i < 6; ++i) {
                    for (Computer c : computer2[i]) {
                        rectangle21.setHeight(620);
                        rectangle22.setHeight(620);
                        rectangle23.setHeight(620);
                        c.getLabel().setPrefWidth(80);
                        c.getLabel().setMinWidth(80);
                        c.getLabel().setLayoutX(c.getStdlabelx());
                        c.getLabel().setLayoutY(c.getStdlabely());
                        c.getImageView().setFitWidth(80);
                        c.getImageView().setFitHeight(100);
                        c.getImageView().setX(c.getStdx());
                        c.getImageView().setY(c.getStdy());
                    }
                }

                primaryStage.setScene(scene2);

                menubar.setPrefWidth(sceneWidth);
                group2.getChildren().add(menubar);
            } else {
                onSNLab = true;
                sceneHeight = 620;
                sceneWidth = 700;

                for (int i = 0; i < 4; ++i) {
                    for (Computer c : computer[i]) {
                        rectangle1.setHeight(620);
                        rectangle2.setHeight(620);
                        c.getLabel().setMinWidth(100);
                        c.getLabel().setPrefWidth(100);
                        c.getLabel().setLayoutX(c.getStdlabelx());
                        c.getLabel().setLayoutY(c.getStdlabely());
                        c.getImageView().setFitWidth(100);
                        c.getImageView().setFitHeight(100);
                        c.getImageView().setX(c.getStdx());
                        c.getImageView().setY(c.getStdy());
                    }
                }

                primaryStage.setScene(scene);

                menubar.setPrefWidth(sceneWidth);
                group.getChildren().add(menubar);
            }

        });

        menuItems[3] = new MenuItem("Nas");
        menuItems[3].setOnAction(event -> {
            try {
                String nasIP = readfromcsv("computer.csv").get(24).get(0);
                Process builder = Runtime.getRuntime().exec("cmd /c start \\\\" + nasIP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            drawElectronicLab(primaryStage);
        } catch (FileNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        try {
            drawSNLab(primaryStage);
        } catch (FileNotFoundException | IllegalArgumentException e) {
            e.printStackTrace();
        }


        Menu menu = new Menu("Optionen");
        menu.getItems().addAll(menuItems);
        menubar.setPrefWidth(scene.getWidth());
        menubar.getMenus().add(menu);
        menubar.setPrefHeight(25);
        menubar.setMinHeight(25);

        group.getChildren().add(menubar);

        primaryStage.setTitle("NetworkManager");
        primaryStage.setMinWidth(350);
        primaryStage.setMinHeight(300);
        try {
            primaryStage.getIcons().add(new Image(new FileInputStream("icon.png")));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    /**
     * Initialisiert das SN-Labor
     *
     * @param primaryStage Stage auf der die Scene initialisiert wird
     * @throws FileNotFoundException wird die CSV-Datei nicht gefunden, wird eine FileNotFoundException geworfen
     */
    private void drawSNLab(Stage primaryStage) throws FileNotFoundException, IllegalArgumentException {

        group = new Group();
        sceneWidth = 700;
        sceneHeight = 620;
        scene = new Scene(group, sceneWidth, sceneHeight);

        computer = new Computer[4][6];
        labels = new Label[4][6];
        int xCoord = 0;
        int labelCount = 0;
        List<List<String>> list = readfromcsv("computer.csv");

        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 6; ++j) {
                ImageView imageView = new ImageView(new Image(new FileInputStream("PC_icon.png"))); //Neues Imageview wird aus dem Bild "PC_icon.png" erstellt
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setX(xCoord);
                imageView.setY((100 * j) + 20);

                int finalI = i;
                int finalJ = j;
                imageView.setOnMouseClicked(event -> { //Wenn ein Computer geclickt wird, wird der Code im Lamda ausgefürht
                    if (event.getClickCount() == 1) { //bei einfachem Click wird der Computer aufgeweckt
                        scheduledFuture = executor.schedule(() -> { //wird nur ausgefürht, wenn innerhalb von 500ms kein 2. Click erfolgt
                            Thread thread2 = new Thread(new WakeOnLan(computer[finalI][finalJ], this));
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
                labels[i][j].setLayoutY(100 * j + 35);
                labels[i][j].setTextFill(Color.DARKGRAY);
                labels[i][j].setMouseTransparent(true);
                labels[i][j].setStyle("-fx-font-size: 14");

                group.getChildren().add(imageView);
                group.getChildren().add(labels[i][j]);
                computer[i][j] = new Computer(list.get(labelCount).get(0), list.get(labelCount).get(1), imageView, labels[i][j]);
                computer[i][j].setStdx(computer[i][j].getImageView().getX());
                computer[i][j].setStdy(computer[i][j].getImageView().getY());
                computer[i][j].setStdlabelx(computer[i][j].getLabel().getLayoutX());
                computer[i][j].setStdlabely(computer[i][j].getLabel().getLayoutY());
                ++labelCount;
            }
            xCoord += 100;
            if (i == 0) { //Die 2 "Tische" sollen nur zwischen Spalte 1 und 2 und zwischen Spalte 3 und 4 sein
                rectangle1 = new Rectangle(100, 600 + 20);
                rectangle1.setX(xCoord);
                rectangle1.setY(0);
                rectangle1.setFill(Color.DIMGRAY);
                group.getChildren().add(rectangle1);
            } else if (i == 2) {
                rectangle2 = new Rectangle(100, 600 + 20);
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

        Main m = this; //soll den Fehler, dass GUI Elemente nicht vom Thread aus verändert werden
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 4; ++i) {
                    for (int j = 0; j < 6; ++j) {
                        Thread t = new Thread(new PingComputer(computer[i][j], m));
                        t.start();
                    }
                }
            }
        }, 0, 10000); //Wiederholt den Task alle 10 Sekunden
        scene.setFill(Color.DARKGRAY);

        primaryStage.setOnCloseRequest(event -> {
            t.cancel(); //sonst läuft Timer immer weiter, auch wenn main thread beendet wird
        });

    }


    /**
     * Initialisiert das Elektornik-Labor
     *
     * @param primaryStage Stage auf der die Scene initialisiert wird
     * @throws FileNotFoundException wird die CSV-Datei nicht gefunden, wird eine FileNotFoundException geworfen
     */

    private void drawElectronicLab(Stage primaryStage) throws FileNotFoundException, IllegalArgumentException {

        group2 = new Group();
        sceneWidth = 880;
        sceneHeight = 600;
        scene2 = new Scene(group2, sceneWidth, sceneHeight);

        computer2 = new Computer[6][2];
        labels2 = new Label[6][2];
        int xCoord = 0;
        int labelCount = 0;
        List<List<String>> list = readfromcsv("computer2.csv"); //Kofigurationsdatei mit MAC und IP Adressen wird eingelesen

        for (int i = 0; i < 6; ++i) {
            for (int j = 0; j < 2; ++j) {
                ImageView imageView = new ImageView(new Image(new FileInputStream("PC_icon.png"))); //Neues Imageview wird aus dem Bild "PC_icon.png" erstellt
                imageView.setFitHeight(100);
                imageView.setFitWidth(80);
                imageView.setX(xCoord);
                imageView.setY((300 * j) + 100);

                int finalI = i;
                int finalJ = j;
                imageView.setOnMouseClicked(event -> { //Wenn ein Computer geclickt wird, wird der Code im Lamda ausgefürht
                    if (event.getClickCount() == 1) { //bei einfachem Click wird der Computer aufgeweckt
                        scheduledFuture = executor.schedule(() -> { //wird nur ausgefürht, wenn innerhalb von 500ms kein 2. Click erfolgt
                            Thread thread2 = new Thread(new WakeOnLan(computer2[finalI][finalJ], this));
                            thread2.start();
                        }, 500, TimeUnit.MILLISECONDS);

                    } else if (event.getClickCount() == 2) { //bei doppeltem Click wird RDP ausgeführt
                        if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
                            scheduledFuture.cancel(false);
                            RemoteAccess remoteAccess = new RemoteAccess(computer2[finalI][finalJ]);
                            Thread thread3 = new Thread(remoteAccess);
                            thread3.start();
                        }

                    }
                });

                //Eigenschaften der Labels mit der PC-Nummer werden gesetzt
                labels2[i][j] = new Label(Integer.toString(labelCount + 1));
                labels2[i][j].setAlignment(Pos.CENTER);
                labels2[i][j].setPrefWidth(80);
                labels2[i][j].setMinWidth(80);
                labels2[i][j].setLayoutX(xCoord + (labels2[i][j].getWidth() / 2));
                labels2[i][j].setLayoutY(300 * j + 115);
                labels2[i][j].setTextFill(Color.DARKGRAY);
                labels2[i][j].setMouseTransparent(true);
                labels2[i][j].setStyle("-fx-font-size: 14");

                group2.getChildren().add(imageView);
                group2.getChildren().add(labels2[i][j]);
                computer2[i][j] = new Computer(list.get(labelCount).get(0), list.get(labelCount).get(1), imageView, labels2[i][j]);
                computer2[i][j].setStdx(computer2[i][j].getImageView().getX());
                computer2[i][j].setStdy(computer2[i][j].getImageView().getY());
                computer2[i][j].setStdlabelx(computer2[i][j].getLabel().getLayoutX());
                computer2[i][j].setStdlabely(computer2[i][j].getLabel().getLayoutY());
                ++labelCount;
            }
            xCoord += 80;
            if (i == 0) { //Die 2 "Tische" sollen nur zwischen Spalte 1 und 2 und zwischen Spalte 3 und 4 sein
                rectangle21 = new Rectangle(80, 600 + 20);
                rectangle21.setX(xCoord);
                rectangle21.setY(0);
                rectangle21.setFill(Color.DIMGRAY);
                group2.getChildren().add(rectangle21);
            } else if (i == 2) {
                rectangle22 = new Rectangle(80, 600 + 20);
                rectangle22.setX(xCoord);
                rectangle22.setY(0);
                rectangle22.setFill(Color.DIMGRAY);
                group2.getChildren().add(rectangle22);
            } else if (i == 4) {
                rectangle23 = new Rectangle(80, 600 + 20);
                rectangle23.setX(xCoord);
                rectangle23.setY(0);
                rectangle23.setFill(Color.DIMGRAY);
                group2.getChildren().add(rectangle23);
            }
            xCoord += 80;
        }

        scene2.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            updateWidth2((double) newSceneWidth);
        });
        scene2.heightProperty().addListener((observableValue, oldSceneHight, newSceneHight) -> {
            updateHeight2((double) newSceneHight);
        });

        scene2.setFill(Color.DARKGRAY);
        Main m = this;
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < 6; ++i) {
                    for (int j = 0; j < 2; ++j) {
                        Thread t = new Thread(new PingComputer(computer2[i][j], m));
                        t.start();
                    }
                }
            }
        }, 0, 10000); //Wiederholt den Task alle 10 Sekunden

        primaryStage.setOnCloseRequest(event -> {
            t.cancel(); //sonst läuft Timer immer weiter, auch wenn main thread beendet wird
        });

    }

    /**
     * Passt die Breite der Elemente in der Scene an, wenn das Fenster breiter oder schmaler wird
     *
     * @param newSceneWidth neue Breite der Scene
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

        menubar.setPrefWidth(menubar.getWidth() * multiplikator);
    }

    /**
     * Passt die Breite der Elemente in der Scene an, wenn das Fenster breiter oder schmaler wird
     *
     * @param newSceneWidth neue Breite der Scene
     */
    private void updateWidth2(double newSceneWidth) {
        double multiplikator = newSceneWidth / sceneWidth;
        sceneWidth = newSceneWidth;

        for (int i = 0; i < 6; ++i) {
            for (Computer c : computer2[i]) {
                c.getImageView().setX(c.getImageView().getX() * multiplikator);
                c.getImageView().setFitWidth(c.getImageView().getFitWidth() * multiplikator);
                c.getLabel().setLayoutX(c.getLabel().getLayoutX() * multiplikator);
                c.getLabel().setMinWidth(c.getLabel().getMinWidth() * multiplikator);
                c.getLabel().setPrefWidth(c.getLabel().getMinWidth());
            }
        }

        rectangle21.setX(computer2[0][0].getImageView().getFitWidth());
        rectangle21.setWidth(computer2[1][0].getImageView().getX() - computer2[0][0].getImageView().getFitWidth());

        rectangle22.setX(computer2[2][0].getImageView().getX() + computer2[2][0].getImageView().getFitWidth());
        rectangle22.setWidth(computer2[3][0].getImageView().getX() - (computer2[2][0].getImageView().getX() + computer2[2][0].getImageView().getFitWidth()));

        rectangle23.setX(computer2[4][0].getImageView().getX() + computer2[4][0].getImageView().getFitWidth());
        rectangle23.setWidth(computer2[5][0].getImageView().getX() - (computer2[4][0].getImageView().getX() + computer2[4][0].getImageView().getFitWidth()));

        menubar.setPrefWidth(menubar.getWidth() * multiplikator);
    }

    /**
     * Passt die Höhe der Elemente in der Scene an, wenn das Fenster höher oder tiefer gemacht wird
     *
     * @param newSceneHeight neue Höhe der Scene
     */
    private void updateHeight(double newSceneHeight) {
        double multiplikator = newSceneHeight / sceneHeight;
        sceneHeight = newSceneHeight;

        for (int i = 0; i <= 3; ++i) {
            for (Computer c : computer[i]) {
                c.getImageView().setY((c.getImageView().getY() - 20) * multiplikator + 20);
                c.getImageView().setFitHeight(c.getImageView().getFitHeight() * multiplikator);
                c.getLabel().setLayoutY((c.getLabel().getLayoutY() - 20) * multiplikator + 20);
            }
        }
        rectangle1.setHeight(rectangle1.getHeight() * multiplikator);
        rectangle2.setHeight(rectangle2.getHeight() * multiplikator);
    }


    /**
     * Passt die Höhe der Elemente in der Scene an, wenn das Fenster höher oder tiefer gemacht wird
     *
     * @param newSceneHeight neue Höhe der Scene
     */
    private void updateHeight2(double newSceneHeight) {
        double multiplikator = newSceneHeight / sceneHeight;
        sceneHeight = newSceneHeight;

        for (int i = 0; i < 6; ++i) {
            for (Computer c : computer2[i]) {
                c.getImageView().setY((c.getImageView().getY() - 20) * multiplikator + 20);
                c.getImageView().setFitHeight(c.getImageView().getFitHeight() * multiplikator);
                c.getLabel().setLayoutY((c.getLabel().getLayoutY() - 20) * multiplikator + 20);
            }
        }
        rectangle21.setHeight(rectangle21.getHeight() * multiplikator);
        rectangle22.setHeight(rectangle22.getHeight() * multiplikator);
        rectangle23.setHeight(rectangle23.getHeight() * multiplikator);
    }

    /**
     * liest CSV-Datei aus
     *
     * @param filename Dateiname als String, aus der gelesen wird
     * @return Liste mit Listen von Strings
     */
    public List<List<String>> readfromcsv(String filename) {
        List<List<String>> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
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
     *
     * @param file File, das geöffnet wird
     */
    public void openCSVFile(File file) {
        //überprüft, ob Desktop unterstützt wird
        if (!Desktop.isDesktopSupported()) {
            System.out.println("Desktop wird nicht unterstützt!");
            return;
        }

        Desktop desktop = Desktop.getDesktop();
        if (file.exists()) {
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

    public void showDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("MessageBox");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * ändert die Farbe des übergebenen Computers zu weiß
     *
     * @param computer Computer, dessen Farbe geändert wird
     */
    public void changeToWhite(Computer computer) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setSaturation(0);
        computer.getImageView().setEffect(colorAdjust);
    }


    public static void main(String[] args) {
        launch(args);

        System.exit(0);
    }

}
