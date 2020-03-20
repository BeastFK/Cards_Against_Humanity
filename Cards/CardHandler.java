package Version0a2.Cards;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class CardHandler {
    private static ArrayList<Card> whiteCards = new ArrayList<>();
    private static ArrayList<Card> blackCards = new ArrayList<>();

    private static final Random random = new Random();

    public static final String directory = "src/Version0a2/Cards/";
    public static final String whiteTxt = "WhiteCards.txt";
    public static final String blackTxt = "BlackCards.txt";

    public static ArrayList<Card> getWhiteCards() {
        return whiteCards;
    }
    public static ArrayList<Card> getBlackCards() {
        return blackCards;
    }

    public static Card randomWhiteCard() {
        return whiteCards.remove(random.nextInt(whiteCards.size()));
    }

    public static Card randomBlackCard() {
        return blackCards.remove(random.nextInt(blackCards.size()));
    }

    public static void analyzeCards() throws FileNotFoundException {
        Scanner txt = new Scanner(new File(directory + whiteTxt));

        while (txt.hasNextLine()) {
            String line = txt.nextLine();
            if (!line.contains("//")) {
                Card card = new Card(line);
                whiteCards.add(card);
            }
        }

        txt.close();
        txt = new Scanner(new File(directory + blackTxt));

        while (txt.hasNextLine()) {
            String line = txt.nextLine();
            if (!line.contains("//")) {
                Card card = new Card(line);
                blackCards.add(card);
            }
        }
    }
}