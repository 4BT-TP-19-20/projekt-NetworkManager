package sample;

import java.io.PrintWriter;
public class Cmd {
    public void pingComputer(){
        String[] command =
                {
                        "cmd",
                };
        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
            new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
            PrintWriter stdin = new PrintWriter(p.getOutputStream());
            stdin.println("ping localhost");
            stdin.close();
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
