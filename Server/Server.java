package Version0a2.Server;

import Version0a2.Cards.Card;
import Version0a2.Cards.CardHandler;
import Version0a2.GameInfo;

import javax.sound.midi.SysexMessage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;

public class Server implements Runnable {
    public static final int STARTINGCARDS = 10;
    public static final int TIMEOUT = 1000;

    public List<IClientInputEvent> listeners;
    private ServerSocket ss;
    private ArrayList<Socket> clients;
    private ArrayList<ClientHandler> handlers;

    private String[][] cards;
    private Card chosenBlackCard;
    private HashMap<Integer, String> playerCards;
    private ClientHandler cardCzar;
    private int currentCardCzarID = 0;
    private String czarChosen;
    private ArrayList<String> chosenCards;

    private boolean started;
    private int currentClientID;
    public static final int WAITSEC = 10;

    public void addChosenCard(int clientID, String card) {
        playerCards.put(clientID, card);
    }

    public Server(int port) throws IOException {
        System.out.println("Starting server");
        this.listeners = new ArrayList<>();
        this.ss = new ServerSocket(port);
        ss.setSoTimeout(TIMEOUT);
        this.clients = new ArrayList<>();
        this.handlers = new ArrayList<>();
        this.started = false;
        this.currentClientID = 0;
        this.playerCards = new HashMap<>();
        this.czarChosen = null;
    }

    @Override
    public void run() {
        System.out.println("Server started");

        while (!started) {
            Socket currentSocket;
            try {
                currentSocket = ss.accept();
                clients.add(currentSocket);

                System.out.println("A new client has connected: " + currentSocket);

                ObjectOutputStream out = new ObjectOutputStream(currentSocket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(currentSocket.getInputStream());

                System.out.println("Assigning a new thread for this client");

                ClientHandler handler = new ClientHandler(currentSocket, in, out, currentClientID);
                handler.start();
                handler.addClientInputListener(new ClientInputListener(handler, this));
                handler.sendString("print", "Welcome to " + GameInfo.name + " version " + GameInfo.version);
                handlers.add(handler);

                currentClientID++;
            } catch (SocketTimeoutException e) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void assignCards() throws FileNotFoundException, IOException{
        CardHandler.analyzeCards();

        cards = new String[clients.size()][STARTINGCARDS];
        chosenBlackCard = CardHandler.randomBlackCard();

        for (int id = 0; id < handlers.size(); id++) {
            for (int cardNum = 0; cardNum < STARTINGCARDS; cardNum++) {
                cards[id][cardNum] = CardHandler.randomWhiteCard().getContent();
            }


        }
    }

    private void sendSelection() throws IOException{
        for (ClientHandler handler: handlers) {
            int id = handler.getClientID();

            handler.sendString("print", "\nBlack Card is:\n\t" + chosenBlackCard.getContent() + "\n\nYour playable cards are:");
            if (handler != cardCzar) {
                handler.sendString("select",cards[id]);
                handler.sendString("print", "Pick a card number as your response (1-" + STARTINGCARDS + ").");
            } else {
                handler.sendString("printList",cards[id]);
                handler.sendString("print", "You are the CARD CZAR.");
            }
        }
    }

    public int getClientNumber() {
        return handlers.size();
    }

    private ClientHandler pickCardCzar(ArrayList<ClientHandler> clients) {
        ClientHandler czar = clients.get(currentCardCzarID);
        currentClientID++;
        return czar;
    }

    @SuppressWarnings("unchecked")
    private void start() throws IOException,InterruptedException {
        System.out.println("Starting game");

        assignCards();

        System.out.println("Cards chosen");

        cardCzar = pickCardCzar(handlers);

        sendSelection();

        System.out.println("Selection sent");

        for (int i = 0; i < WAITSEC; i++) {
            System.out.println("Sleeping for 1 second, time " + (i + 1));
            Thread.sleep(1000);
        }

        System.out.println("Sending replies.");

        Collection<String> indexes = playerCards.values();

        chosenCards = new ArrayList<>(indexes);

        for (ClientHandler handler: handlers) {
            StringBuilder cards = new StringBuilder();

            cards.append("\nAll chosen cards are:");

            for (int i = 0; i < chosenCards.size(); i++) {
                cards.append("\n\t");
                cards.append((i+1));
                cards.append(". ");
                cards.append(chosenCards.get(i));
            }

            handler.sendString("print", cards.toString());
            if (handler != cardCzar) {
                handler.sendString("print","The CARD CZAR is choosing!");
            } else {
                handler.sendString("selectWO", "Pick your favorite card (1-" + chosenCards.size() + ")");
            }
        }

        while (czarChosen == null) {
            Thread.sleep(100);
        }

        System.out.println("The CARD CZAR chose " + czarChosen);

        for (ClientHandler handler: handlers) {
            handler.sendString("print", "\nThe CARD CZAR chose " + czarChosen);
        }
    }

    public String[][] getCards() {
        return cards;
    }

    public void getCzarCard(int val) {
        czarChosen = chosenCards.get(val);
    }

    public void startGame() {
        this.started = true;
    }
}