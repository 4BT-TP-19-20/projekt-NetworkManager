package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *@author Simon Niederwolfsgruber, Philipp Gruber, Matias Brandlechner
 * @version 1.0
 *
 */

class WakeOnLan implements Runnable{
    Computer computer;

    public WakeOnLan(Computer computer) {
        this.computer = computer;
    }

    /**
     * übergebener Computer soll über WOL aufgeweckt werden
     * @param computer Computer der aufgeweckt werden soll
     */
    public void wakeOnLan(Computer computer){
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
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, address, port);
            DatagramSocket socket = new DatagramSocket();
            socket.send(packet);
            socket.close();

            System.out.println("Wake-on-LAN Paket gesendet.");
        }
        catch (Exception e) {
            System.out.println("Wake-on-LAN Paket nicht gesendet!");
        }
    }

    /**
     * Wandelt übergebene MAC-Adresse in bytes um
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
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Ungültige Hex-Ziffer in MAC-Adresse.");
        }
        return bytes;
    }

    /**
     * Thread wird benötigt weil sonst das Programm aufgehalten wird
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
        InetAddress geek = null;
        try {
            geek = InetAddress.getByName(computer.getIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            if (geek != null) {
                if (geek.isReachable(2000))
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
     * Thread wird benötigt weil sonst das Programm aufgehalten wird
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

class RemoteAccess implements Runnable{
    Computer computer;

    public RemoteAccess(Computer computer) {
        this.computer = computer;
    }

    /**
     * Verbindet sich per Remote-Desktop mit dem übergebenen Computer
     * @param computer
     */
    public void remoteAccess (Computer computer){
        String ip = computer.getIp();
        Process p = null;
        try {
            p = Runtime.getRuntime().exec("cmdkey /generic:" + ip );
            p.destroy();
            Runtime.getRuntime().exec("mstsc /v: " + ip + " /f /console");
            Thread.sleep(2 * 60 * 1000); // Minutes seconds milliseconds
            // Deleting credentials
            Process p1 = Runtime.getRuntime().exec("cmdkey /delete:" + ip);
            p1.destroy();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * Thread wird benötigt weil sonst das Programm aufgehalten wird
     */
    @Override
    public void run() {
        remoteAccess(computer);
    }
}
