import java.util.ArrayList;
import java.util.Random;

public class Croupier {
    private ArrayList<Card> deck;

    Croupier(){
        deck = new ArrayList<>();
        for(int i=0; i<4; i++){
            for(int j=0; j<8; j++){
                deck.add(new Card(i,j));
            }
        }
    }

    public Card getCardFromDeck(){
        Random rnd = new Random();
        int indexOfCard = rnd.nextInt(deck.size());
        Card card = deck.get(indexOfCard);
        deck.remove(indexOfCard);
        return card;
    }
}
