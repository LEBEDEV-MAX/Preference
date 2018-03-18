public class Card{
    private int suit;  // масть
    private int rank;  // ранг
    private final char[] suits = new char[]{
            '\u2660',  //пики
            '\u2667',  //крести
            '\u2662',  //бубны
            '\u2661'   //червы
            };
    private final String[] ranks = new String[]{"7","8","9","10","J","Q","K","A"};

    Card(int suit, int rank){
        this.suit = suit;
        this.rank = rank;
    }

    public int getSuit(){
        return suit;
    }
    public int getRank(){
        return rank;
    }

    public String getName(){
        return suits[suit] + ranks[rank];
    }
}
