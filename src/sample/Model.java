package sample;

import javax.swing.*;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

        try {
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
                        main.changeToWhite(computer);
                        isRed = false;
                    } else {
                        main.changeToRed(computer);
                        isRed = true;

                    }
                }
            }, 0, 500);
            PingComputer pc = new PingComputer(computer, main);
            long time2 = System.currentTimeMillis();
            while (!pc.pingComputer(computer)) {
                if ((time2 - time1) > 60000) {
                    t.cancel();
                    throw new Exception();
                }
                Thread.sleep(1000);
                time2 = System.currentTimeMillis();
            }
            t.cancel();
            main.changeToGreen(computer);
        } catch (Exception e) {
            main.changeToRed(computer);
            JOptionPane.showMessageDialog(null, "PC konnte nicht aufgeweckt werden!");
        }
    }

    private byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Ungültige MAC-Adresse.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültige HEX-Ziffer in der MAC-Adresse!");
        }
        return bytes;
    }

    /**
     * Thread wird benötigt weil sonst das Hauptprogramm aufgehalten wird
     */
    @Override
    public void run() {
        wakeOnLan(computer);
    }
}


class PingComputer implements Runnable {
    private Computer computer;
    private Main main;

    public PingComputer(Computer computer, Main main) {
        this.computer = computer;
        this.main = main;
    }


    /**
     * @param computer Computer, der gepingt werden soll
     * @return true wenn eingeschaltet; false wenn ausgeschaltet
     */
    public boolean pingComputer(Computer computer) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(computer.getIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            if (inetAddress != null) {
                if (inetAddress.isReachable(2000)) //*Der Computer muss Innerhalb von 2 Sek antworten, sonst wird er als nichterreichbar eingestuft
                    return true;
                else
                    return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Thread wird benötigt weil sonst das Hauptprogramm aufgehalten wird
     */
    @Override
    public void run() {
        if (pingComputer(this.computer)) {
            main.changeToGreen(this.computer);
        } else {
            main.changeToRed(this.computer);
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
    public void remoteAccess(Computer computer) {
        String ip = computer.getIp();
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("cmdkey /generic:" + ip);
            p.destroy();
            Runtime.getRuntime().exec("mstsc /v: " + ip + " /f /console");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Thread wird benötigt weil sonst das Hauptprogramm aufgehalten wird
     */
    @Override
    public void run() {
        remoteAccess(computer);
    }
}
