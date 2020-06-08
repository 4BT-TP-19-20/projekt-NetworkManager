package sample;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * @author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 * Diese Klasse vereint die Eigenschaften des Computers (IP, MAC-Adresse, Bild, Label mit PC-Nummer)
 */
public class Computer {
    private String ip;
    private String mac;
    private ImageView imageView;
    private Label label;
    private double stdx;
    private double stdy;
    private double stdlabelx;
    private double stdlabely;

    public Computer(String ip) {
        this.ip = ip;
    }

    public Computer(String ip, String mac, ImageView imageView, Label label) {
        this.ip = ip;
        this.mac = mac;
        this.imageView = imageView;
        this.label = label;
    }

    public String getIp() {
        return ip;
    }

    public String getMac() {
        return mac;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public Label getLabel() {
        return label;
    }

    public double getStdx() {
        return stdx;
    }

    public void setStdx(double stdx) {
        this.stdx = stdx;
    }

    public double getStdy() {
        return stdy;
    }

    public void setStdy(double stdy) {
        this.stdy = stdy;
    }

    public double getStdlabelx() {
        return stdlabelx;
    }

    public void setStdlabelx(double stdlabelx) {
        this.stdlabelx = stdlabelx;
    }

    public double getStdlabely() {
        return stdlabely;
    }

    public void setStdlabely(double stdlabely) {
        this.stdlabely = stdlabely;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
