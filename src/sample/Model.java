package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 */

class WakeOnLan implements Runnable {
    Computer computer;

    public WakeOnLan(Computer computer) {
        this.computer = computer;
    }

    /**
     * übergebener Computer wird über WOL aufgeweckt
     *
     * @param computer Computer der aufgeweckt werden soll
     */
    public void wakeOnLan(Computer computer) {
        int port = 9;
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
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port); //Paket wird erstellt
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet); //Paket wird gesendet
            socket.close();

            System.out.println("Wake-on-LAN Paket gesendet.");
        } catch (Exception e) {
            System.err.println("Wake-on-LAN Paket nicht gesendet!");
        }
    }

    /**
     * Wandelt übergebene MAC-Adresse von String in bytes um
     *
     * @param macStr MAC-Adresse als String
     * @return MAC-Adresse in Bytes
     * @throws IllegalArgumentException
     */
    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("MAC-Adresse Ungültig.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültige Hex-Ziffer in MAC-Adresse.");
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

    public PingComputer(Computer computer) {
        this.computer = computer;
    }

    public boolean pingComputer(Computer computer) {
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(computer.getIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            if (inetAddress != null) {
                if (inetAddress.isReachable(2000)) //Der Computer muss Innerhalb von 2 Sek antworten,
                    return true;                           //sonst wird er als nichterreichbar eingestuft
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
        Main main = new Main();
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
