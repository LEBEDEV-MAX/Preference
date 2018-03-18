import java.util.Comparator;

// сортировка по мастям
public class CardSuitComparator implements Comparator<Card> {
    @Override
    public int compare(Card o1, Card o2){
        if(o1.getSuit() < o2.getSuit()) return -1;
        if(o1.getSuit() > o2.getSuit()) return 1;
        return 0;
    }
}
