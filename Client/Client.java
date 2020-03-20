package Version0a2.Client;

import Version0a2.GameInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    public static final int MAX_RECONNECT_ATTEMPTS = 10;
    public static final int TIME_BETWEEN_ATTEMPTS = 5000;
    public static final int PORT = GameInfo.PORT;
    public static final String IP = "127.0.0.1";

    private static Socket s;
    private static Handler handler;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private static PlayerInputHandler playerHandler;

    private static boolean connected = false;

    public static void main(String[] args) {
        for (int i = 0; i < MAX_RECONNECT_ATTEMPTS; i++) {
            if (!connected) {
                try {
                    attemptConnection();
                } catch (Exception e) {
                    System.out.println("Unable to connect to " + IP + ":" + PORT);
                }
                try {
                    Thread.sleep(TIME_BETWEEN_ATTEMPTS);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void attemptConnection() throws UnknownHostException, IOException {
        System.out.println("Attempting to connect to " + IP + ":" + PORT);
        s = new Socket(IP, PORT);
        connected = true;
        System.out.println("\nConnected to " + IP + ":" + PORT);

        Scanner sc = new Scanner(System.in);

        out = new ObjectOutputStream(s.getOutputStream());
        out.flush();
        in = new ObjectInputStream(s.getInputStream());

        playerHandler = new PlayerInputHandler(sc);
        handler = new Handler(s, in, out, playerHandler);
        playerHandler.setHandler(handler);

        ExecutorService executor = Executors.newCachedThreadPool();
        executor.execute(handler);
        ExecutorService executor2 = Executors.newCachedThreadPool();
        executor2.execute(playerHandler);
    }
}
