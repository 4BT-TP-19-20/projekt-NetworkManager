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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main2 extends Application {

    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    ScheduledFuture<?> scheduledFuture;

    private Group group;
    private Scene scene;
    private Computer[][] computer;
    @Override
    public void start(Stage primaryStage) throws Exception {
        group = new Group();
        int xCoord = 0;
        int labelCount = 0;
        computer = new Computer[4][6];
        scene = new Scene(group, 700, 600);
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 6; ++j) {
                ImageView imageView= new ImageView(new Image(new FileInputStream("PC_icon.png")));
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setX(xCoord);
                imageView.setY(100 * j);
                int finalI = i;
                int finalJ = j;
                imageView.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 1) {

                        scheduledFuture = executor.schedule(() -> {
                            System.out.println("single");
                            Model m = new Model(computer[finalI][finalJ]);
                            Thread thread = new Thread(m);
                            thread.start();
                        }, 500, TimeUnit.MILLISECONDS);

                    } else  {

                        if(scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
                            scheduledFuture.cancel(false);
                            remoteAccess(computer[finalI][finalJ]);
                            System.out.println("double");
                        }

                    }
                });

                group.getChildren().add(imageView);
                List<List<String>> list = readfromcsv();
                computer[i][j] = new Computer(list.get(labelCount).get(0), list.get(labelCount).get(1), imageView);
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
        primaryStage.setTitle("Hello World");
        scene.setFill(Color.DARKGRAY);
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(300);
        /*
        scene.widthProperty().addListener(observable -> {   //change size
            resize(rows, finalColums);
            changeWidth(finalColums, rows);
        });
        scene.heightProperty().addListener(observable -> {
            changeHeight(rows, finalColums);
            resize(rows, finalColums);
        });
        */
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    public void remoteAccess(Computer computer){
        String ip = "192.168.43.80";
        String userName = "Simon";
        String password = "stniesim";
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("cmdkey /generic:" + ip +
                    " /user:" + userName +
                    " /pass:" + password);
            p.destroy();
            Runtime.getRuntime().exec("mstsc /v: " + ip + " /f /console");
            Thread.sleep(2*60*1000); // Minutes seconds milliseconds
            // Deleting credentials
            Process p1 = Runtime.getRuntime().exec("cmdkey /delete:" + ip);
            p1.destroy();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }




    }

    public void pingAll(){
    }

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



    public void changeToRed(Computer computer) {
        ColorAdjust colorAdjust = new ColorAdjust();
        colorAdjust.setContrast(0);
        colorAdjust.setHue(0.0);
        colorAdjust.setBrightness(0);
        colorAdjust.setSaturation(1);
        computer.getImageView().setEffect(colorAdjust);
    }

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
}
