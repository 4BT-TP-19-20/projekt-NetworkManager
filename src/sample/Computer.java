package sample;

import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

/**
 * @author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 */

public class Computer {
    private String ip;
    private String mac;
    private ImageView imageView;
    private int id;
    private Label label;

    public Computer(String ip, String mac, ImageView imageView, int id, Label label) {
        this.ip = ip;
        this.mac = mac;
        this.imageView = imageView;
        this.id = id;
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
}
