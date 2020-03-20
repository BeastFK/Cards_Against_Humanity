package Version0a2.Cards;

public class CardTester {
    public static void main(String[] args) {
        try {
            CardHandler.analyzeCards();
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (Card whiteCard: CardHandler.getWhiteCards()) {
            System.out.println(whiteCard);
        }

        for (Card blackCard: CardHandler.getBlackCards()) {
            System.out.println(blackCard);
        }
    }
}