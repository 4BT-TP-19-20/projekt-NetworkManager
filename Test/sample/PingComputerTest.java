package sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PingComputerTest {

    @Test
    void fakeIP(){
        Computer computer = new Computer("10.10.30.200","9C-7B-EF-AC-8E-14");
        PingComputer ping = new PingComputer(computer);
        boolean erg = ping.pingComputer(computer);
        assertFalse(erg);
    }

    @Test
    void fakeMAC(){
        Computer computer = new Computer("10.10.30.201","98-7B-EF-AC-8E-14");
        PingComputer ping = new PingComputer(computer);
        boolean erg = ping.pingComputer(computer);
        assertFalse(erg);
    }

    @Test
    void correctPC(){
        Computer computer = new Computer("10.10.30.212","9C-7B-EF-AC-8E-14");
        PingComputer ping = new PingComputer(computer);
        boolean erg = ping.pingComputer(computer);
        assertTrue(erg);
    }
}