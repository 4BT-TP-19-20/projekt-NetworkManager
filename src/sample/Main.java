package sample;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 *@author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 *
 */

public class Main extends Application {
    private Group group;
    private ImageView[] imageView;
    private Image image;
    private GridPane gridPane = new GridPane();
    private int pcCount = 0;
    private Scene scene;
    private Stage stage;
    private boolean resizing=false;
    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        pcCount = 25;
        imageView = new ImageView[pcCount];
        for (int i = 0; i < pcCount; ++i) {
            imageView[i] = new ImageView(new Image(new FileInputStream("PC_icon.png")));
            imageView[i].setFitHeight(100);
            imageView[i].setFitWidth(100);
            int finalI = i;
            imageView[i].setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
//                    remoteAccess(ip)
                    System.out.println("doppelclick");
                } else if (event.getClickCount() == 1) {
                    changeToGreen(imageView[finalI]);
                    System.out.println("einzelclick");
                    /*
                    pingComputer(ip);
                    if (pingComputer) {
                        changeToGreen(imageView[i]);
                    } else {
                        changeToRed(imageView[i);
                    }
                     */
                }
            });
        }
        double root = Math.sqrt(pcCount);
        int rows, colums;
        rows = (int) root;
        colums = (int) root;
        if (root > (int) root) {
            colums += 1;
        }
        int counter2 = 0;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < colums; ++j) {
                if (counter2 < pcCount) {
                    gridPane.add(imageView[counter2], j, i);
//                    gridPane.setPadding(new Insets(5,5,5,5));
                }
                ++counter2;
            }
        }
//        gridPane.add(imageView[i], i, 0);
//        group = new Group();
//        group.getChildren().addAll(imageView);
        primaryStage.setTitle("Hello World");
        scene = new Scene(gridPane, imageView[0].getFitWidth() * colums, imageView[0].getFitHeight() * rows);
        scene.setFill(Color.GRAY);
        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(300);
        int finalColums = colums;
        scene.widthProperty().addListener(observable -> {
//            resize(rows, finalColums);

            if (!resizing){
                System.out.println("width");
                resizing=true;
//                changeWidth(finalColums, rows);
                resizing=false;
            }
        });


        scene.heightProperty().addListener(observable -> {
            if (!resizing){
                System.out.println("height");
                resizing=true;
//                changeHeight(rows, finalColums);
                resizing=false;
            }
//            resize(rows, finalColums);
        });
        primaryStage.setScene(scene);
        primaryStage.show();
    }
        boolean isAlreadyOneClick;

        public void doubleClick(){
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


    private void changeHeight(int rows, int colums) {
        double newSize = scene.getHeight()/rows;
        for (int i=0; i< pcCount;++i){
            imageView[i].setFitHeight(newSize);
            imageView[i].setFitWidth(newSize);
            stage.setWidth(imageView[i].getFitWidth()*colums*1.035);
        }
    }

    private void changeWidth(int colums, int rows) {
        double newSize = scene.getWidth()/colums;
        for (int i=0; i< pcCount;++i){
            imageView[i].setFitHeight(newSize);
            imageView[i].setFitWidth(newSize);
            stage.setHeight(imageView[i].getFitHeight()*rows);
        }
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
