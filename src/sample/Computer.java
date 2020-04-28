package sample;

import javafx.scene.image.ImageView;

/**
 *@author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 *
 */

public class Computer {
        private String ip;
        private String mac;
        private ImageView imageView;
        private int id;

        public Computer(String ip, String mac, ImageView imageView, int id) {
            this.ip = ip;
            this.mac = mac;
            this.imageView = imageView;
            this.id = id;
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
}
