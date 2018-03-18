import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Bot{
    private ArrayList<Card> deck;                 //колода в руке
    private int leftWhist, rightWhist;            //левый и правый висты
    private int pool;                             //пуля
    private int mountain;                         //гора
    private String name;                          //имя бота
    private boolean dealStatus;                   //участвует ли бот в торгах: true = играет; false = пасует
    private boolean whistStatus;                  //вистует ли бот

    Bot(String name){
        deck = new ArrayList<>();
        leftWhist = 0;
        rightWhist = 0;
        pool = 0;
        mountain = 0;
        this.name = name;
        dealStatus = true;
        whistStatus = false;
    }

    public boolean getWhistStatus(){
        return whistStatus;
    }

    public void setWhistStatus(boolean whistStatus){
        this.whistStatus = whistStatus;
    }

    public boolean getBotStatus(){
        return dealStatus;
    }

    public void setBotStatus(boolean botStatus){
        this.dealStatus = botStatus;
    }

    public int getLeftWhist(){
        return leftWhist;
    }

    public int getRightWhist(){
        return rightWhist;
    }

    public int getPool(){
        return pool;
    }

    public int getMountain(){
        return mountain;
    }

    public String getName(){
        return name;
    }

    public void setLeftWhist(int leftWhist){
        this.leftWhist += leftWhist;
    }

    public void setRightWhist(int rightWhist){
        this.rightWhist += rightWhist;
    }

    public void setPool(int pool){
        this.pool += pool;
    }

    public void setMountain(int mountain){
        this.mountain += mountain;
    }

    public ArrayList<Card> getDeck(){
        return deck;
    }

    public void addCard(Card cd){
        deck.add(cd);
    }

    public void sortingCards() {
        Comparator<Card> comp = new CardSuitComparator().thenComparing(new CardRankComparator());
        deck.sort(comp);
    }

    //получение заявки
/*    public String getBid(){
        String bid;
        int numberOfWinCards;     //кол-во выигрышных карт в масти
        int numberOfWinSuit = 0;  //масть с наибольшым кол-вом выигрышных карт
        int numberOfContract = 0; //общее колво выигрышных карт в руке
        int winSuit = 0;          //номер выигрышной масти
        ArrayList<Card> suit = new ArrayList<>();
        for(int i=0; i<deck.size(); i++){
            if( i+1 < deck.size() && deck.get(i).getSuit() == deck.get(i+1).getSuit())
                suit.add(deck.get(i));
            else{
                suit.add(deck.get(i));
                numberOfWinCards = getNumberOfWinCards(suit);
                if(suit.size() >= 8 - suit.size()){
                    numberOfWinCards += 2*suit.size() - 8;
                }
                if(numberOfWinSuit < numberOfWinCards){
                    numberOfWinSuit = numberOfWinCards;
                    winSuit = suit.get(0).getSuit();
                }
                numberOfContract += numberOfWinCards;
                if(numberOfContract > 10) numberOfContract = 10;
                suit = new ArrayList<>();
            }
        }
        if(numberOfContract >=5){
            bid = String.valueOf(numberOfContract);
            if(numberOfWinSuit > 3){
                switch (winSuit){
                    case 0:
                        bid += "\u2660";  //пики
                        break;
                    case 1:
                        bid += "\u2667";  //крести
                        break;
                    case 2:
                        bid += "\u2662";  //бубны
                        break;
                    case 3:
                        bid += "\u2661";  //червы
                        break;
                }
            }
            else bid += "Б.К.";
        }
        else bid = "";
        return bid;
      }

    //получаем кол-во выигрышных карт в масти
    private int getNumberOfWinCards(ArrayList<Card> suit){
        int numberOfWinCards = 0;
        if(suit.contains(7)){ //есть туз
            for(int i=7; i>=0; i--){
                if(suit.contains(i)) numberOfWinCards++;
                else {
                    break;
                }
            }
        }
        else{
            for(int i=7; i>=0; i--){
                if(suit.contains(i)) numberOfWinCards++;
                else numberOfWinCards--;
            }
            if(numberOfWinCards < 0) return 0;
        }
        return numberOfWinCards;
      }
*/

    //создание заявки на контракт
/*    public String getBid(){
        final double percentOfMiser = 1.328125;         //4.25 - максимальная вероятность для мизера
        final double percentOfSmallContract = 6.0;  //6 - минимальная ставка в игре
        String bid;                                 //заявка на контракт
        int[] percentOfWinOfSuits = new int[4];     //процент победы каждой масти
        int[] numberOfCardsOfSuits = new int[4];    //кол-во карт каждой масти
        double percentOfWin = 0.0;                  //процент выигрыша карты
        double maxWinPercentage = 0.0;              //максимальный процент выигрыша карт
        boolean space = false;
        final ArrayList<Double> winPercentage = new ArrayList<>(Arrays.asList(
                //вероятности выигрыша каждой карты от 7 до туза
                //0.125, 0.25, 0.375, 0.5, 0.626, 0.75, 0.875, 1.0));
                0.0078125, 0.015625, 0.03125, 0.0625, 0.125, 0.25, 0.5, 1.0));

        //находим кол-во точных взяток
        for(int i = deck.size()-1; i>=0; i--){
            switch (deck.get(i).getRank()){
                case 0:
                    maxWinPercentage += winPercentage.get(0);
                    break;
                case 1:
                    maxWinPercentage += winPercentage.get(1);
                    break;
                case 2:
                    maxWinPercentage += winPercentage.get(2);
                    break;
                case 3:
                    maxWinPercentage += winPercentage.get(3);
                    break;
                case 4:
                    maxWinPercentage += winPercentage.get(4);
                    break;
                case 5:
                    maxWinPercentage += winPercentage.get(5);
                    break;
                case 6:
                    maxWinPercentage += winPercentage.get(6);
                    break;
                case 7:
                    maxWinPercentage += winPercentage.get(7);
                    break;
                default:
                    if( //если не выходим за пределы массива...
                        i+1 < deck.size() &&
                        //если текущая и предыдущая карты одной масти...
                        deck.get(i).getSuit() == deck.get(i+1).getSuit()){
                            if( //если текущая карта ниже на единицу предыдущей (по рангу) ...
                                deck.get(i).getRank() == deck.get(i+1).getRank()+1){
                                if( //если карты шли не подряд (был пропуск)...
                                    space){
                                    //записываем процент выигрыша предыдущей карты
                                    percentOfWin = winPercentage.get(deck.get(i + 1).getRank());
                                }
                                //иначе зпишется процент выигрыша высшей карты в масти (percentOfWin не изменится)
                            }
                            else{
                                space = true;
                                percentOfWin = winPercentage.get(deck.get(i).getRank());
                            }
                    }
                    else{
                        space = false;
                        percentOfWin = winPercentage.get(deck.get(i).getRank());
                    }
                    //записываем проценты победы для каждой масти
                    setPercentOfWinOfSuit(percentOfWinOfSuits, percentOfWin, deck.get(i));
                    //записываем кол-во карт для каждой масти
                    setNumberOfCardsOfSuits(numberOfCardsOfSuits, deck.get(i));
            }
        }
        if(maxWinPercentage <= percentOfMiser) bid = "Мизер";
        else if(maxWinPercentage < percentOfSmallContract) {
            //пасуем (не набираем для заявки, но есть вероятность взять карты на мизере)
            bid = "";
        }
        else{
            bid = String.valueOf((int)maxWinPercentage);
            switch ( getTrumpOfBid(percentOfWinOfSuits, numberOfCardsOfSuits) ){
                case -1:
                    bid+="Б.К.";
                    break;
                case 0:
                    bid+="\u2660";  //пики
                    break;
                case 1:
                    bid+="\u2667";  //крести
                    break;
                case 2:
                    bid+="\u2662";  //бубны
                    break;
                case 3:
                    bid+="\u2661";  //червы
                    break;
            }
        }
        return bid;
    }

    //запись процентов побед каждой масти
    private void setPercentOfWinOfSuit(int[] percentOfWinOfSuits, double percentOfWin, Card card){
        switch (card.getSuit()){
            case 0:         //пики
                percentOfWinOfSuits[0] += percentOfWin;
                break;
            case 1:         //крести
                percentOfWinOfSuits[1] += percentOfWin;
                break;
            case 2:         //бубны
                percentOfWinOfSuits[2] += percentOfWin;
                break;
            case 3:         //червы
                percentOfWinOfSuits[3] += percentOfWin;
                break;
        }
    }

    //запись кол-ва карт каждой масти
    private void setNumberOfCardsOfSuits(int[] numberOfCardsOfSuits,Card card){
        switch (card.getSuit()){
            case 0:         //пики
                numberOfCardsOfSuits[0]++;
                break;
            case 1:         //крести
                numberOfCardsOfSuits[1]++;
                break;
            case 2:         //бубны
                numberOfCardsOfSuits[2]++;
                break;
            case 3:         //червы
                numberOfCardsOfSuits[3]++;
                break;
        }
    }

    //получение козыря для заявки
    private int getTrumpOfBid(int[] percentsOfWinOfSuits, int[] numberOfCardsOfSuits){
        int max = percentsOfWinOfSuits[0];
        //находим масть с самым большим процентом победы...
        for(int i=1; i<percentsOfWinOfSuits.length; i++){
            if(max < percentsOfWinOfSuits[i]) max = i;
        }
        //если кол-во карт этой масти больше 3 - возвращаем масть, иначе возвращаем Б.К.
        if(numberOfCardsOfSuits[max] > 3) return max;
        else return -1;
    }
*/
}
