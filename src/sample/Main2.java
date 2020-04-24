package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

public class Main2 extends Application {
    private Group group;
    private ImageView[][] imageView;
    private Label[][] labels;
    private GridPane gridPane = new GridPane();
    private int pcCount = 0;
    private Scene scene;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        pcCount = 24;
        imageView = new ImageView[4][6];
        labels = new Label[4][6];
        group = new Group();
        int xCoord = 0;
        int labelCount = 0;
        scene = new Scene(group, 700, 600);
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 6; ++j) {
                imageView[i][j] = new ImageView(new Image(new FileInputStream("PC_icon.png")));
                imageView[i][j].setFitHeight(100);
                imageView[i][j].setFitWidth(100);
                imageView[i][j].setX(xCoord);
                imageView[i][j].setY(100 * j);
                labels[i][j] = new Label(String.valueOf(labelCount + 1));
                labels[i][j].setLayoutX(xCoord + 50);
                labels[i][j].setLayoutY(100 * j + 15);
                labels[i][j].setTextFill(Color.WHITE);

                int finalI = i;
                int finalJ = j;
                imageView[i][j].setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2) {
//                    remoteAccess(ip)
                        System.out.println("doppelclick");
                    } else if (event.getClickCount() == 1) {
                        System.out.println("einzelclick");
                        if (pingComputer()) {
                            changeToGreen(imageView[finalI][finalJ]);
                        } else {
                            changeToRed(imageView[finalI][finalJ]);
                        }
                    }
                });
                group.getChildren().add(imageView[i][j]);
//                group.getChildren().add(labels[i][j]);
                ++labelCount;
            }
            xCoord += 100;
            if (i == 0 || i == 2) {
                Rectangle rectangle = new Rectangle(100, 600);
                rectangle.setX(xCoord);
                rectangle.setY(0);
                rectangle.setFill(Color.DIMGRAY);
                group.getChildren().add(rectangle);
            }
            xCoord += 100;
        }
        double root = Math.sqrt(pcCount);
        int rows, colums;
        rows = (int) root;
        colums = (int) root;
        if (root > (int) root) {
            colums += 1;
        }


        primaryStage.setTitle("Hello World");
        scene.setFill(Color.DARKGRAY);
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(300);
        int finalColums = colums;
        scene.widthProperty().addListener(observable -> {
//            resize(rows, finalColums);
//            changeWidth(finalColums, rows);
        });


        scene.heightProperty().addListener(observable -> {
//            changeHeight(rows, finalColums);
//            resize(rows, finalColums);
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    boolean isAlreadyOneClick;

    public void doubleClick() {
        if (isAlreadyOneClick) {
            System.out.println("double click");
            isAlreadyOneClick = false;
        } else {
            isAlreadyOneClick = true;
            Timer t = new Timer("doubleclickTimer", false);
            t.schedule(new TimerTask() {

                @Override
                public void run() {
                    isAlreadyOneClick = false;
                }
            }, 500);
        }
    }
//    private void resize(int rows, int colums) {
//        double newHeight = gridPane.getHeight() / rows;
//        double newWidht = gridPane.getHeight() / colums;
//        for (int i = 0; i < pcCount; ++i) {
//            imageView[i].setFitHeight(newHeight);
//            imageView[i].setFitWidth(newHeight);
//        }
//        stage.setHeight(imageView[0].getFitHeight() * rows * 1.035);
//        stage.setWidth(imageView[0].getFitWidth() * colums * 1.035);
//    }


    //    private void changeHeight(int rows, int colums) {
//        double newSize = scene.getHeight() / rows;
//        for (int i = 0; i < pcCount; ++i) {
//            imageView[i].setFitHeight(newSize);
//            imageView[i].setFitWidth(newSize);
//            stage.setWidth(imageView[i].getFitWidth() * colums * 1.035);
//        }
//    }
//
//    private void changeWidth(int colums, int rows) {
//        double newSize = scene.getWidth() / colums;
//        for (int i = 0; i < pcCount; ++i) {
//            imageView[i].setFitHeight(newSize);
//            imageView[i].setFitWidth(newSize);
//            stage.setHeight(imageView[i].getFitHeight() * rows);
//        }
//    }
    public boolean pingComputer() {
        String ipAddress = "187.187.187.187";
        InetAddress geek = null;
        try {
            geek = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            if (geek.isReachable(5000))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void changeToRed(ImageView imageView) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0);
        colorAdjust.setHue(0.0);
        colorAdjust.setBrightness(0);
        colorAdjust.setSaturation(1);
        imageView.setEffect(colorAdjust);
    }

    public void changeToGreen(ImageView imageView) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0);
        colorAdjust.setHue(0.6);
        colorAdjust.setBrightness(0);
        colorAdjust.setSaturation(1);
        imageView.setEffect(colorAdjust);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
