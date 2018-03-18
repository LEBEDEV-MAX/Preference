public class Contract {
    private int rank;
    private String trump;
    private int valueOfTrump;

    Contract(int rank, Suits trump){
        this.rank = rank;
        this.trump = trump.suit;
        valueOfTrump = trump.value;
    }

    public int getRank(){
        return rank;
    }

    public String getTrump(){
        return  trump;
    }

    public int getValueOfTrump(){
        return valueOfTrump;
    }

    public String getNameOfContract(){
        return String.valueOf(rank)+trump;
    }

    enum Suits{
        SPADES("\u2660", 0), CLUBS("\u2667", 1), DIAMONDS("\u2662", 2), HEARTS("\u2661", 3), NO_TRUMP("Б.К.", -1);

        String suit;
        int value;

        Suits(String suit, int value) {
            this.suit = suit;
            this.value = value;
        }
    }
}
