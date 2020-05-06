package sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PingComputerTest {

    /**
     * Pings a pc with fake ip-Address
     */
    @Test
    void fakeIP(){
        Computer computer = new Computer("10.10.30.200","9C-7B-EF-AC-8E-14");
        PingComputer ping = new PingComputer(computer);
        boolean erg = ping.pingComputer(computer);
        assertFalse(erg);
    }

    /**
     * Pings a online pc with correct ip-Address
     */
    @Test
    void correctPC(){
        Computer computer = new Computer("10.10.30.212","9C-7B-EF-AB-FE-BA");
        PingComputer ping = new PingComputer(computer);
        boolean erg = ping.pingComputer(computer);
        assertTrue(erg);
    }


}