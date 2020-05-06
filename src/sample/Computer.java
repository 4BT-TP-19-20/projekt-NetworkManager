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

    public Computer(String ip, String mac, ImageView imageView, Label label) {
        this.ip = ip;
        this.mac = mac;
        this.imageView = imageView;
        this.label = label;
    }

    public Computer(String ip, String mac) {
        this.ip = ip;
        this.mac = mac;
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
}
