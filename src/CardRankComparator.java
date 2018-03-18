import java.util.Comparator;

// сортировка по рангам карт
public class CardRankComparator implements Comparator<Card>{
    @Override
    public int compare(Card o1, Card o2) {
        if(o1.getSuit() == o2.getSuit()){
            if(o1.getRank() < o2.getRank()) return 1;
            if(o1.getRank() > o2.getRank()) return -1;
        }
        return 0;
    }
}
