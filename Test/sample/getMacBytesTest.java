package sample;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class getMacBytesTest {

    @Test
    void wakeOnLanCorrect(){
        Computer computer = new Computer("10.10.30.212","9C-7B-EF-AC-8E-14");
        WakeOnLan wakeOnLan = new WakeOnLan(computer);
        assertEquals(, wakeOnLan.getMacBytes(computer.getMac()));
    }

    @Test
    void wakeOnLanCorrect(){
        Computer computer = new Computer("10.10.30.212","9C-7B-EF-AC-8E-14");
        WakeOnLan wakeOnLan = new WakeOnLan(computer);
        assertEquals(, wakeOnLan.getMacBytes(computer.getMac()));
    }
}