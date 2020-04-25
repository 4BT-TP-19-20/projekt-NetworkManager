package sample;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Model implements Runnable {
    private Computer computer;

    public Model(Computer computer) {
        this.computer = computer;
    }

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

            System.out.println("Wake-on-LAN packet sent.");
        }
        catch (Exception e) {
            System.out.println("Failed to send Wake-on-LAN packet!");
        }
    }

    private static byte[] getMacBytes(String macStr) throws IllegalArgumentException {
        byte[] bytes = new byte[6];
        String[] hex = macStr.split("(\\:|\\-)");
        if (hex.length != 6) {
            throw new IllegalArgumentException("Invalid MAC address.");
        }
        try {
            for (int i = 0; i < 6; i++) {
                bytes[i] = (byte) Integer.parseInt(hex[i], 16);
            }
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid hex digit in MAC address.");
        }
        return bytes;
    }

    public boolean pingComputer(Computer computer) {
        InetAddress geek = null;
        try {
            geek = InetAddress.getByName(computer.getIp());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            if (geek.isReachable(500))
                return true;
            else
                return false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        Main2 main = new Main2();
        if (pingComputer(this.computer)) {
            main.changeToGreen(this.computer);
        } else {
            main.changeToRed(this.computer);
        }
    }
}
