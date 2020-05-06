package sample;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class WakeOnLanTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setOut(originalOut);
    }

    @Test
    void correctPC(){
        Computer computer = new Computer("10.10.30.212","9C-7B-EF-AC-8E-14");
        WakeOnLan wakeOnLan = new WakeOnLan(computer);
        wakeOnLan.wakeOnLan(computer);
        assertEquals("Wake-on-LAN Paket gesendet.", outContent.toString());
    }


    @Test
    void wrongPC(){
        Computer computer = new Computer("10.10.30.201","98-7B-EF-AC-8E-14");
        WakeOnLan wakeOnLan = new WakeOnLan(computer);
        wakeOnLan.wakeOnLan(computer);
        assertEquals("Wake-on-LAN Paket nicht gesendet!", outContent.toString());
    }


}