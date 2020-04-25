package sample;

import javafx.scene.image.ImageView;

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

        public void setIp(String ip) {
            this.ip = ip;
        }

        public String getMac() {
            return mac;
        }

        public void setMac(String mac) {
            this.mac = mac;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public int getId() {
            return id;
        }

}
