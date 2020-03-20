package Version0a2.Client;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class Handler implements Runnable {
    private PlayerInputHandler handler;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket s;

    public Handler(Socket s, ObjectInputStream dis, ObjectOutputStream dos, PlayerInputHandler handler) {
        this.s = s;
        this.in = dis;
        this.out = dos;
        this.handler = handler;
    }

    public void sendString(String tag, String send) throws IOException{
        ArrayList<String> toSend = new ArrayList<>();
        toSend.add(tag);
        toSend.add(send);

        out.writeObject(toSend);
    }

    @Override
    public void run() {
        while (true) {
            try {
                ArrayList<String> fromServer = convertToArray(in.readObject());

                String tag = fromServer.remove(0);

                if (tag.equals("print")) {
                    for (String str: fromServer) {
                        System.out.println(str);
                    }
                } else if (tag.equals("select")) {
                    for (int i = 0; i < fromServer.size(); i++) {
                        System.out.println("\t"+ (i+1) + ". " + fromServer.get(i));
                    }
                    handler.setLookingForResponse(true);
                } else if (tag.equals("selectWO")) {
                    System.out.println(fromServer.get(0));
                    handler.setLookingForResponse(true);
                    handler.setTag("czar");
                } else if (tag.equals("printList")) {
                    for (int i = 0; i < fromServer.size(); i++) {
                        System.out.println("\t"+ (i+1) + ". " + fromServer.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> convertToArray(Object e) {
        return (ArrayList<String>) e;
    }
}
