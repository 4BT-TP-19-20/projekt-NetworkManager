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

/**
 * @author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 */

public class Main2 extends Application {


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
                ImageView imageView = new ImageView(new Image(new FileInputStream("PC_icon.png"))); //Neues Imageview wird aus dem Bild "PC_icon.png" erstellt
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);
                imageView.setX(xCoord);
                imageView.setY(100 * j);
                int finalI = i;
                int finalJ = j;

                imageView.setOnMouseClicked(event -> { //Wenn ein Computer geclickt wird, wird der Code im Lamda ausgefürht
                    if (event.getClickCount() == 1) { //bei einfachem Click
                        scheduledFuture = executor.schedule(() -> { //wird nur ausgefürht, wenn innerhalt von 500ms kein 2. Click erfolgt
                            PingComputer m = new PingComputer(computer[finalI][finalJ]);
                            Thread thread = new Thread(m);
                            thread.start();

                            WakeOnLan wakeOnLan = new WakeOnLan(computer[finalI][finalJ]);
                            Thread thread2 = new Thread(m);
                            thread2.start();
                        }, 500, TimeUnit.MILLISECONDS);

                    } else if (event.getClickCount() == 2) { //bei doppeltem Click
                        if (scheduledFuture != null && !scheduledFuture.isCancelled() && !scheduledFuture.isDone()) {
                            scheduledFuture.cancel(false);
                            RemoteAccess remoteAccess = new RemoteAccess(computer[finalI][finalJ]);
                            Thread thread3 = new Thread(remoteAccess);
                            thread3.start();
                        }

                    }
                });

                group.getChildren().add(imageView);
                List<List<String>> list = readfromcsv(); //Kofigurationsdatei mit MAC und IP Adressen wird eingelesen
                computer[i][j] = new Computer(list.get(labelCount).get(0), list.get(labelCount).get(1), imageView, id);
                ++labelCount;
                ++id;
            }
            xCoord += 100;
            if (i == 0) { //Die Tische sollen nur zwischen Spalte 1 und 2 und zwischen Spalte 3 und 4 sein
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
        primaryStage.setTitle("SN-Projekt");
        scene.setFill(Color.DARKGRAY);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(300);

        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> {
            echteschangewith((double) newSceneWidth);
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
//                changeHeight(4, 6);
                canChange = true;
            }
<<<<<<< HEAD
=======
//            resize(rows, finalColums);
*/
>>>>>>> dc5882f7abaec3dace782df865fc72888f7a066f
        });

        primaryStage.setScene(scene);
        primaryStage.show();
        stage = primaryStage;
    }


    /**
     * soll die Größe anpassen, wenn die Scene verändert wird
     * @param rows Anzahl der Reihen des Raumes
     * @param colums Anzahl der Spalten des Raumes
     */
    private void changeHeight(int rows, int colums) {
        double newSize = scene.getHeight() / rows;
        double posX = 0;
        double posY = 0;
        for (int i = 0; i < colums; ++i) {
            for (int j = 0; j < rows; ++j) {
                computer[i][j].getImageView().setFitHeight(newSize);
                computer[i][j].getImageView().setFitWidth(newSize);
                computer[i][j].getImageView().setX(posX);
                computer[i][j].getImageView().setY(posY);
                stage.setWidth(computer[i][j].getImageView().getFitWidth() * 7);
                posY += newSize;
            }
            posX += newSize;
            if (i == 0) {
                rectangle1.setX(posX);
                rectangle1.setHeight(scene.getHeight());
                rectangle1.setWidth(newSize);
            } else if (i == 2) {
                rectangle2.setX(posX);
                rectangle2.setHeight(scene.getHeight());
                rectangle2.setWidth(newSize);
            }
            posY = 0;
            posX += newSize;
        }

    }

    //Keine ahnung wiso des net geat
    //Hon i (Philipp) probiert, weil is ondere a bisele lost isch, und dess ah!!!!


    /**
     * soll die Größe anpassen, wenn die Scene verändert wird
     * @param newseenwidth neue Größe der Scene
     */
    private void echteschangewith(double newseenwidth) {
        double multiplikator = newseenwidth / sceenwidth;
        sceenwidth = newseenwidth;

        for (int i = 0; i <= 3; ++i) {
            for (Computer C : computer[i]) {
                C.getImageView().setX(C.getImageView().getX() * multiplikator);
            }
        }

<<<<<<< HEAD
        rectangle1.setWidth(computer[0][1].getImageView().getX() - 100);
=======
        rectangle1.setWidth(computer[1][0].getImageView().getX() - computer[0][0].getImageView().getFitWidth());
>>>>>>> dc5882f7abaec3dace782df865fc72888f7a066f

        rectangle2.setX(computer[2][0].getImageView().getX() + computer[2][0].getImageView().getFitWidth());
        rectangle2.setWidth(computer[3][0].getImageView().getX() - (computer[2][0].getImageView().getX() + computer[2][0].getImageView().getFitWidth()));

        //System.out.println(computer[2][0].getImageView().getX());
        //changeToGreen(computer[2][0]);

    }

<<<<<<< HEAD
    /**
     * soll die Größe anpassen, wenn die Scene verändert wird
     * @param colums
     * @param rows
     */
=======
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


>>>>>>> dc5882f7abaec3dace782df865fc72888f7a066f
    private void changeWidth(int colums, int rows) {
        double newSize = scene.getWidth() / 7;
        double posX = 0;
        double posY = 0;
        for (int i = 0; i < colums; ++i) {
            for (int j = 0; j < rows; ++j) {
                computer[i][j].getImageView().setFitHeight(newSize);
                computer[i][j].getImageView().setFitWidth(newSize);
                computer[i][j].getImageView().setX(posX);
                computer[i][j].getImageView().setY(posY);
                stage.setHeight(computer[i][j].getImageView().getFitHeight() * 6);
                posY += newSize;
            }
            posX += newSize;
            if (i == 0) {
                rectangle1.setX(posX);
                rectangle1.setHeight(scene.getHeight());
                rectangle1.setWidth(newSize);
            } else if (i == 2) {
                rectangle2.setX(posX);
                rectangle2.setHeight(scene.getHeight());
                rectangle2.setWidth(newSize);
            }
            posY = 0;
            posX += newSize;
        }

    }


    /**
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
     * @param computer Farbe des übergebenen Computers wird zu rot geändert
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
     * @param computer Farbe des übergebenen Computers wird zu grün geändert
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
