import java.util.ArrayList;

public class History {                //история...
    ArrayList<ArrayList<Card>> store;        //прикупа
    ArrayList<ArrayList<Card>> drop;         //сброса
    ArrayList<ArrayList<Card>> deal;         //торгов
    ArrayList<ArrayList<Card>> moves;        //ходов
    ArrayList<ArrayList<Card>> decks;        //колод ботов
    ArrayList<ArrayList<Integer>> pools;     //очков в пуле
    ArrayList<ArrayList<Integer>> mountains; //очков в горе
    ArrayList<ArrayList<Integer>> whists;    //очков в вистах

    History(){
        store = new ArrayList<>();
        drop = new ArrayList<>();
        deal = new ArrayList<>();
        moves = new ArrayList<>();
        decks = new ArrayList<>();
        pools = new ArrayList<>();
        mountains = new ArrayList<>();
        whists = new ArrayList<>();
    }

    public void setStore(ArrayList<Card> cards){
        store.add(cards);
    }

    public ArrayList<ArrayList<Card>> getStore() {
        return store;
    }

    public void setDrop(ArrayList<Card> cards){
        drop.add(cards);
    }

    public ArrayList<ArrayList<Card>> getDrop() {
        return drop;
    }

    public void setDecks(ArrayList<Card> cards){
        decks.add(cards);
    }

    public ArrayList<ArrayList<Card>> getDecks(){
        return decks;
    }

    public void setMoves(ArrayList<Card> cards){
        moves.add(cards);
    }

    public ArrayList<ArrayList<Card>> getMoves(){
        return moves;
    }

}
