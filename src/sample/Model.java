package sample;

import javafx.application.Platform;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 */

class WakeOnLan implements Runnable {
    private final Computer computer;
    private boolean isRed = false;
    private final Main main;

    public WakeOnLan(Computer computer, Main main) {
        this.computer = computer;
        this.main = main;
    }

    public static final int PORT = 9;

    /**
     * übergebener Computer wird über WOL aufgeweckt
     *
     * @param computer Computer der aufgeweckt werden soll
     */
    public void wakeOnLan(Computer computer) {

        String ipStr = computer.getIp();
        String macStr = computer.getMac();

        try {                                                                                   //ob do!!!!!!!!!!!!!!!!!!!!!
            byte[] macBytes = getMacBytes(macStr);
            byte[] bytes = new byte[6 + 16 * macBytes.length];
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) 0xff;
            }
            for (int i = 6; i < bytes.length; i += macBytes.length) {
                System.arraycopy(macBytes, 0, bytes, i, macBytes.length);
            }

            InetAddress address = InetAddress.getByName(ipStr);
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, PORT);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            long time1 = System.currentTimeMillis();
            socket.close();
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isRed) {
                        Platform.runLater(() -> {
                            main.changeToWhite(computer);
                        });
                        isRed = false;
                    } else {
                        Platform.runLater(() -> {
                            main.changeToRed(computer);
                        });
                        isRed = true;
                    }
                }
            }, 0, 500);
            PingComputer pc = new PingComputer(computer, main);
            long time2 = System.currentTimeMillis();
            while (!pc.pingComputer(computer)) {
                if ((time2 - time1) > 120000) {
                    t.cancel();
                    throw new Exception();
                }
                Thread.sleep(1000);
                time2 = System.currentTimeMillis();
            }
            t.cancel();
            Platform.runLater(() -> {
                main.changeToGreen(computer);
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                main.changeToRed(computer);
            });
            JOptionPane.showMessageDialog(null, "PC konnte nicht aufgeweckt werden!");
        }
    }

    /**
     * Wandelt MAC-Adresse von String zu byte um
     *
     * @param macStr MAC-Adresse als String
     * @return MAC-Adresse in bytes
     * @throws IllegalArgumentException
     */
    private byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Ungültige MAC-Adresse.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseUnsignedInt(hex[i], 16);
                //bytes[i] = (byte) hexToInt(hex[i]);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültige HEX-Ziffer in der MAC-Adresse!");
        }
        return bytes;
    }

    private int hexToInt(String toint) {

        int intvalue = 0;
        int hoch = toint.length() - 1;
        int zahlensystem = 16;

        for (int i = 0; i < toint.length(); ++i) {

            try {
                intvalue += Integer.parseInt(String.valueOf(toint.charAt(i))) * Math.pow(zahlensystem, hoch);
            } catch (NumberFormatException e) {
                int sepp = toint.charAt(i) - 65;
                sepp += 10;
                intvalue += Integer.parseInt("" + sepp) * Math.pow(zahlensystem, hoch);
            }

            --hoch;
        }

        return intvalue;
    }


    /**
     * übergebener Computer wird über WOL aufgeweckt (über den server 10.10.30.15)
     */
    public void wakeOnLANoverServer() {
        try {
            URL myURL = new URL(("http://10.10.30.15/wakeUp.php?mymac=" + this.computer.getMac()));
            HttpURLConnection myURLConnection = (HttpURLConnection) myURL.openConnection();
            myURLConnection.connect();
            InputStream is = myURLConnection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            long time1 = System.currentTimeMillis();
            Timer t = new Timer();
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isRed) {
                        Platform.runLater(() -> {
                            main.changeToWhite(computer);
                        });
                        isRed = false;
                    } else {
                        Platform.runLater(() -> {
                            main.changeToRed(computer);
                        });
                        isRed = true;
                    }
                }
            }, 0, 500);
            PingComputer pc = new PingComputer(computer, main);
            long time2 = System.currentTimeMillis();
            while (!pc.pingComputer(computer)) {
                if ((time2 - time1) > 120000) {
                    t.cancel();
                    throw new Exception();
                }
                Thread.sleep(1000);
                time2 = System.currentTimeMillis();
            }
            t.cancel();
            Platform.runLater(() -> {
                main.changeToGreen(computer);
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                main.changeToRed(computer);
            });
            JOptionPane.showMessageDialog(null, "PC konnte nicht aufgeweckt werden!");
        }
    }

    /**
     * Thread wird benötigt weil sonst das Hauptprogramm aufgehalten wird
     */
    @Override
    public void run() throws IllegalArgumentException {
//        wakeOnLan(computer);
        wakeOnLANoverServer();
    }
}


class PingComputer implements Runnable {
    private Computer computer;
    private Main main;

    public PingComputer(Computer computer, Main main) {
        this.computer = computer;
        this.main = main;
    }

    public PingComputer() {
    }

    /**
     * @param computer Computer, der gepingt werden soll
     * @return true wenn eingeschaltet; false wenn ausgeschaltet
     */
    public boolean pingComputer(Computer computer) throws IOException {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(computer.getIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        if (inetAddress != null) {
            if (inetAddress.isReachable(1000)) //*Der Computer muss Innerhalb von 2 Sek antworten, sonst wird er als nichterreichbar eingestuft
                return true;
            else
                return false;
        }

        return false;
    }

    /**
     * Thread wird benötigt weil sonst das Hauptprogramm aufgehalten wird
     */
    @Override
    public void run() {
        try {
            if (pingComputer(this.computer)) {
                Platform.runLater(() -> {
                    main.changeToGreen(this.computer);
                });
            } else {
                Platform.runLater(() -> {
                    main.changeToRed(this.computer);
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class RemoteAccess implements Runnable {
    Computer computer;

    public RemoteAccess(Computer computer) {
        this.computer = computer;
    }

    /**
     * Verbindet sich per Remote-Desktop mit dem übergebenen Computer
     *
     * @param computer Computer, mit dem sich verbunden wird
     */
    public void remoteAccess(Computer computer) throws IOException {
        String ip = computer.getIp();
        Process p = null;
        p = Runtime.getRuntime().exec("cmdkey /generic:" + ip);
        p.destroy();
        Runtime.getRuntime().exec("mstsc /v: " + ip + " /f /console");
    }


    /**
     * Thread wird benötigt weil sonst das Hauptprogramm aufgehalten wird
     */
    @Override
    public void run() {
        try {
            remoteAccess(computer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
