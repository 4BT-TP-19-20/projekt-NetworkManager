package sample;

import javafx.scene.image.ImageView;

public class Computer {
        private String ip;
        private String mac;
        private ImageView imageView;

        public Computer(String ip, String mac, ImageView imageView) {
            this.ip = ip;
            this.mac = mac;
            this.imageView = imageView;
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

}
