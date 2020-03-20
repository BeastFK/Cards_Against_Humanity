package Version0a2.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends Thread {
    public List<IClientInputEvent> listeners;

    private Socket s;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private int clientID;

    public ClientHandler(Socket s, ObjectInputStream in, ObjectOutputStream out, int id) {
        this.s = s;
        this.in = in;
        this.out = out;
        this.clientID = id;
        this.listeners = new ArrayList<>();
    }

    public void sendString(String tag, String data) throws IOException {
        ArrayList<String> toSend = new ArrayList<>();
        toSend.add(tag);
        toSend.add(data);

        out.writeObject(toSend);
    }

    public void sendString(String tag, String... data) throws IOException {
        ArrayList<String> toSend = new ArrayList<>();
        toSend.add(tag);
        for (String str: data) {
            toSend.add(str);
        }

        out.writeObject(toSend);
    }

    public void sendString(String tag, ArrayList<String> data) throws IOException {
        data.add(0,tag);

        out.writeObject(data);
    }

    public void sendString(String tag) throws IOException {
        ArrayList toSend = new ArrayList<>();
        toSend.add(tag);

        out.writeObject(toSend);
    }

    public int getClientID() {
        return clientID;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object message = in.readObject();
                onClientInput(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void addClientInputListener(ClientInputListener listener) {
        listeners.add(listener);
    }

    public void onClientInput(Object data) {
        for (IClientInputEvent listener: listeners) {
            listener.onClientInput(data, clientID);
        }
    }
}