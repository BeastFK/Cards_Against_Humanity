package Version0a2.Cards;

public class Card {
    private String content;

    public Card(String content, boolean blackCard) {
        this.content = content;
    }

    public Card(String content) {
        this.content = content;
    }
    public String getContent() {
        return content;
    }

    @Override
    public String toString() {
        return content;
    }
}