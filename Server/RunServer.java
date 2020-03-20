package Version0a2.Server;

import Version0a2.GameInfo;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunServer {
    public static final int port = GameInfo.PORT;

    public static void main(String[] args) {
        try {
            Server server = new Server(port);

            ExecutorService executor = Executors.newCachedThreadPool();
            executor.execute(server);

            Scanner in = new Scanner(System.in);

            System.out.println("Type \"start\" to start Cards Against Humanity");

            while (true) {
                String input = in.nextLine();

                if (input.equals("start")) {
                    if (server.getClientNumber() >= 2) {
                        System.out.println("Starting game");
                        server.startGame();
                    } else {
                        System.out.println("Waiting for " + (2 - server.getClientNumber()) + " more players");
                    }
                } else {
                    System.out.println("Invalid token");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}