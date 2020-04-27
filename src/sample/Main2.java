package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main2 extends Application {
    //EpasCooles
    ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1);
    ScheduledFuture<?> scheduledFuture;
    private Stage stage;
    private Group group;
    private Scene scene;
    private Computer[][] computer;
    private static boolean canChange = true;
    private Rectangle rectangle1, rectangle2;
    private int id = 1;

    private double sceenwidth = 700;
    private double sceenhight = 600;

    @Override
    public void start(Stage primaryStage) throws Exception {
        group = new Group();
        int xCoord = 0;
        int labelCount = 0;
        computer = new Computer[4][6];
        scene = new Scene(group, sceenwidth, sceenhight);
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 6; ++j) {
                ImageView imageView = new ImageView(new Image(new FileInputStream("PC_icon.png")));
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

                    } else {

                        if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
                            scheduledFuture.cancel(false);
                            remoteAccess(computer[finalI][finalJ]);
                            System.out.println("double");
                        }

                    }
                });

                group.getChildren().add(imageView);
                List<List<String>> list = readfromcsv();
                computer[i][j] = new Computer(list.get(labelCount).get(0), list.get(labelCount).get(1), imageView, id);
                ++labelCount;

                ++id;
            }
            xCoord += 100;
            if (i == 0) {
                rectangle1 = new Rectangle(100, 600);
                rectangle1.setX(xCoord);
                rectangle1.setY(0);
                rectangle1.setFill(Color.DIMGRAY);
                group.getChildren().add(rectangle1);
            }else if (i==2){
                rectangle2 = new Rectangle(100, 600);
                rectangle2.setX(xCoord);
                rectangle2.setY(0);
                rectangle2.setFill(Color.DIMGRAY);
                group.getChildren().add(rectangle2);
            }
            xCoord += 100;
        }
        primaryStage.setTitle("Hello World");
        scene.setFill(Color.DARKGRAY);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {   //change size
//            resize(rows, finalColums);
            echteschangewith((double)newSceneWidth);
            if (canChange) {
//                canChange = false;
//                System.out.println("changewith");
//                changeWidth(4, 6);
//                canChange = true;
            }
        });
        scene.heightProperty().addListener((observableValue, oldSceneHight, newSceneHight) -> {
            echteschangehight((double)newSceneHight);
 /*
            if (canChange) {
                canChange = false;
                //System.out.println("changeheight");
                changeHeight(4, 6);
                canChange = true;
            }
//            resize(rows, finalColums);
*/
        });

        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
    }


    private void changeHeight(int rows, int colums) {
        double newSize = scene.getHeight() / 6;
        double posX = 0;
        double posY = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 6; ++j) {
                computer[i][j].getImageView().setFitHeight(newSize);
                computer[i][j].getImageView().setFitWidth(newSize);
                computer[i][j].getImageView().setX(posX);
                computer[i][j].getImageView().setY(posY);
                stage.setWidth(computer[i][j].getImageView().getFitWidth() * 7);
                posY+=newSize;
            }
            posX+=newSize;
            if (i == 0) {
                rectangle1.setX(posX);
                rectangle1.setHeight(scene.getHeight());
                rectangle1.setWidth(newSize);
            }else if (i==2){
                rectangle2.setX(posX);
                rectangle2.setHeight(scene.getHeight());
                rectangle2.setWidth(newSize);
            }
            posY=0;
            posX+=newSize;
        }

    }

    //Keine ahnung wiso des net geat
    //Hon i (Philipp) probiert, weil is ondere a bisele lost isch, und dess ah!!!!

    private void echteschangewith(double newseenwidth) {
        double multiplikator = newseenwidth / sceenwidth;
        sceenwidth = newseenwidth;

        for (int i = 0; i <= 3; ++i) {
            for(Computer C : computer[i]) {
                C.getImageView().setX(C.getImageView().getX() * multiplikator);
            }
        }

        rectangle1.setWidth(computer[1][0].getImageView().getX() - computer[0][0].getImageView().getFitWidth());

        rectangle2.setX(computer[2][0].getImageView().getX() + computer[2][0].getImageView().getFitWidth());
        rectangle2.setWidth(computer[3][0].getImageView().getX() - (computer[2][0].getImageView().getX() + computer[2][0].getImageView().getFitWidth()));

        //System.out.println(computer[2][0].getImageView().getX());
        //changeToGreen(computer[2][0]);

    }

    private void echteschangehight (double newsceenhight) {
        double multiplikator = newsceenhight / sceenhight;
        sceenhight = newsceenhight;

        for (int i = 0; i <= 3; ++i) {
            for(Computer C : computer[i]) {
                C.getImageView().setY(C.getImageView().getY() * multiplikator);
            }
        }

        rectangle1.setHeight(rectangle1.getHeight() * multiplikator);
        rectangle2.setHeight(rectangle2.getHeight() * multiplikator);

    }


    private void changeWidth(int colums, int rows) {
        double newSize = scene.getWidth() / 7;
        double posX = 0;
        double posY = 0;
        for (int i = 0; i < 4; ++i) {
            for (int j = 0; j < 6; ++j) {
                computer[i][j].getImageView().setFitHeight(newSize);
                computer[i][j].getImageView().setFitWidth(newSize);
                computer[i][j].getImageView().setX(posX);
                computer[i][j].getImageView().setY(posY);
                stage.setHeight(computer[i][j].getImageView().getFitHeight() * 6);
                posY+=newSize;
            }
            posX+=newSize;
            if (i == 0) {
                rectangle1.setX(posX);
                rectangle1.setHeight(scene.getHeight());
                rectangle1.setWidth(newSize);
            }else if (i==2){
                rectangle2.setX(posX);
                rectangle2.setHeight(scene.getHeight());
                rectangle2.setWidth(newSize);
            }
            posY=0;
            posX+=newSize;
        }

    }
        public void remoteAccess (Computer computer){
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
                Thread.sleep(2 * 60 * 1000); // Minutes seconds milliseconds
                // Deleting credentials
                Process p1 = Runtime.getRuntime().exec("cmdkey /delete:" + ip);
                p1.destroy();

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }

        public void pingAll () {
        }

        public List<List<String>> readfromcsv () {
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


        public void changeToRed (Computer computer){
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setContrast(0);
            colorAdjust.setHue(0.0);
            colorAdjust.setBrightness(0);
            colorAdjust.setSaturation(1);
            computer.getImageView().setEffect(colorAdjust);
        }

        public void changeToGreen (Computer computer){
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setContrast(0);
            colorAdjust.setHue(0.6);
            colorAdjust.setBrightness(0);
            colorAdjust.setSaturation(1);
            computer.getImageView().setEffect(colorAdjust);
        }


        public static void main (String[]args){
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


    }
