package Version0a2.Server;

import java.util.ArrayList;

public class ClientInputListener implements IClientInputEvent {

    private ClientHandler handler;
    private Server server;

    public ClientInputListener(ClientHandler handler, Server server) {
        this.handler = handler;
        this.server = server;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onClientInput(Object data, int id) {
        ArrayList<String> fromClient = (ArrayList<String>) data;

        String tag = fromClient.remove(0);
        String sent = fromClient.remove(0);

        if (tag.equals("reply")) {
            try {
                int value = Integer.valueOf(sent);
                if (value >= 0 && value <= Server.STARTINGCARDS) {
                    value--;
                    String[][] cards = server.getCards();

                    server.addChosenCard(handler.getClientID(), cards[handler.getClientID()][value]);
                }
            } catch (Exception e) {
                try {
                    handler.sendString("response");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        } else if (tag.equals("czar")) {
            try {
                int value = Integer.valueOf(sent);
                if (value >= 0 && value <= Server.STARTINGCARDS) {
                    value--;

                    server.getCzarCard(value);
                }
            } catch (Exception e) {
                try {
                    handler.sendString("response");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
        }
    }
}