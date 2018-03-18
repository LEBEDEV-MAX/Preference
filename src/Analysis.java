import java.util.ArrayList;

public class Analysis {

    //анализируем карты и возвращаем заяку на контракт
    public Contract getBid(ArrayList<Card> deck){
        Contract bid = null;             //заявка
        int numberOfWinCardsInDeck = 0;  //общее кол-во выигрышных карт
        int winSuit =0;                  //индекс выигрышной масти
        int winCards = 0;                //кол-во выигрышных карт в масти
        ArrayList<Card> suit = new ArrayList<>(); //карты одной масти
        for(int i=0; i<deck.size(); i++){
            if( i+1 < deck.size() && deck.get(i).getSuit() == deck.get(i+1).getSuit())
                suit.add(deck.get(i));
            else{
                suit.add(deck.get(i));
                int numberOfWinCardsInSuit = 0;   //кол-во выигрышных карт в масти
                int numberOfLosingCards = 0;      //кол-во проигрышных карт
                boolean wasPreviousCard = true;   //бала ли предыдущая карта на ранг выше текущей
                for(int rankOfCard=7, j=0; j < suit.size(); rankOfCard--){
                    //проверяем, равен ли ранг карты в руке рангу карты от туза до 7
                    if(suit.get(j).getRank() == rankOfCard){
                        if(numberOfLosingCards >= 0){        //если нет проигранных карт
                            if(wasPreviousCard){
                                numberOfWinCardsInSuit++;    //выигрываем одну карту
                            }
                        }
                        else numberOfLosingCards++;    //отбиваем одну карту
                        j++;                           //переходим к следующей карте в масти
                        wasPreviousCard = true;
                    }
                    else {
                        numberOfLosingCards--;         //проигрываем одну карту
                        wasPreviousCard = false;
                    }
                }
                if(winCards < numberOfWinCardsInSuit){
                    winCards = numberOfWinCardsInSuit;
                    winSuit = suit.get(0).getSuit();
                }
                numberOfWinCardsInDeck += numberOfWinCardsInSuit;
                suit = new ArrayList<>();
            }
        }
        if(numberOfWinCardsInDeck >=5){   // от 5 - риск на получение нужных карт в прикупе
            if(numberOfWinCardsInDeck == 5) numberOfWinCardsInDeck +=1; //заявляем 6
            int i =numberOfWinCardsInDeck;
            if(winCards > 3){
                switch (winSuit){
                    case 0:
                        bid = new Contract(i, Contract.Suits.SPADES);
                        break;
                    case 1:
                        bid = new Contract(i, Contract.Suits.CLUBS);
                        break;
                    case 2:
                        bid = new Contract(i, Contract.Suits.DIAMONDS);
                        break;
                    case 3:
                        bid = new Contract(i, Contract.Suits.HEARTS);
                        break;
                }
            }
            else bid = new Contract(i, Contract.Suits.NO_TRUMP);
        }
        return bid;
    }

    //получаем окончательный контракт
    public Contract getFinalContract(ArrayList<Card> deck, History history){
        ArrayList<Card> drop = new ArrayList<>();
        ArrayList<Card> cards = deck;
        Contract contract = getBid(deck);
        //если был мизер, то удаляем 2 самые большие карты (по рангу)
        if(contract.getRank() == 0){
            for(int i=0; i<2; i++) {
                deck.remove(cards.get(i));
                drop.add(cards.get(i));
            }
        }
        //если была заявка без козыря, то удаляем 2 самые мелкие карты (по рангу)
        else if(contract.getTrump() == Contract.Suits.NO_TRUMP.suit){
            for(int i = cards.size()-1; i > cards.size()-3; i--) {
                deck.remove(cards.get(i));
                drop.add(cards.get(i));
            }
        }
        //если была заявка с козырем, то удаляем 2 самые мелкие карты (по рангу), которые не являются козырем
        else{
            int suit=0;
            if(contract.getTrump().equals(Contract.Suits.CLUBS.suit)) suit =1;
            if(contract.getTrump().equals(Contract.Suits.DIAMONDS.suit)) suit =2;
            if(contract.getTrump().equals(Contract.Suits.HEARTS.suit)) suit =3;
            for(int i = cards.size()-1 , n=0; i > 0; i--) {
                if(cards.get(i).getSuit() != suit) {
                    deck.remove(cards.get(i));
                    drop.add(cards.get(i));
                    n++;
                }
                if(n == 2) break; //когда удалили 2 карты
            }

        }
        history.setDrop(drop);
        return contract;
    }

    //получем самую маленькую карту соответствующей масти
    public Card getSmallCard(Bot bot, Card winnerCard, String winnerBot){
        Card cd = null;
        if(winnerCard != null){                     //если сбрасывается не первая карта
            for(Card card : bot.getDeck()){
                if(card.getSuit() == winnerCard.getSuit()){
                    cd = card;
                    if(winnerCard.getRank() < cd.getRank()) {
                        winnerCard = cd;            //заменяем выигрышную карту
                        winnerBot = bot.getName();  //запоминаем имя победившего бота
                    }
                }
            }
        }
        //если не нашли карты нужной масти, выбираем любую наименьшую
        if(cd == null) {
            int min = bot.getDeck().get(0).getRank();
            for (Card card : bot.getDeck()) {
                if (card.getRank() < min) {
                    cd = card;
                    min = card.getRank();
                }
            }
        }
        if(winnerCard == null) {   //если сбрасывается первая карта
            winnerCard = cd;
            winnerBot = bot.getName();
        }
        bot.getDeck().remove(cd);
        return cd;
    }

    //получаем карту для основной игры
    public Card getCard(Bot bot, Card winnerCard, String winnerBot, Contract contract){
        Card cd = null;
        if(bot.getBotStatus()){      //если это бот-заказчик
            if(winnerBot != null){   //если сбрасывается первая карта
                for(Card card : bot.getDeck()){
                    if(card.getSuit() == contract.getValueOfTrump()){ //ищем козырную карту
                        cd = card;
                        break;
                    }
                }
                if(cd == null){                      //если козырная карта не найдена
                    int max = bot.getDeck().get(0).getRank();
                    for(Card card: bot.getDeck()){
                        if (card.getRank() > max) {  //ищем макс. большую карту
                            cd = card;
                            max = card.getRank();
                        }
                    }
                }
                bot.getDeck().remove(cd);
            }
            else{
                for(Card card : bot.getDeck()){
                    if(card.getSuit() == winnerCard.getSuit()){ //ищем карту такой же масти
                        cd = card;
                        if(winnerCard.getRank() < cd.getRank()){
                            winnerCard = cd;
                            winnerBot = bot.getName();
                            break;
                        }
                    }
                }
                if(cd == null){ //если не нашли карту такой же масти
                    for(Card card : bot.getDeck()){
                        if(card.getSuit() == contract.getValueOfTrump()){ //ищем козырную карту
                            cd = card;
                            winnerCard = cd;
                            winnerBot = bot.getName();
                        }
                    }
                    if(cd == null){ //если не нашли козырную карту
                        cd = getSmallCard(bot, winnerCard, winnerBot);
                    }
                    else bot.getDeck().remove(cd);
                }
                else bot.getDeck().remove(cd);
            }
        }
//////////////////////////////////////////////////////////////////////
        else{                        //если это вистующий бот
            if(winnerCard != null){  //если сбрасывается первая карта
                cd = getSmallCard(bot, winnerCard, winnerBot);
            }
            else{
                for(Card card: bot.getDeck()){
                    if(card.getSuit() == winnerCard.getSuit()){     //ищем карту такой же масти
                        cd = card;
                        if(winnerCard.getRank() < cd.getRank()) {
                            winnerCard = cd;            //заменяем выигрышную карту
                            winnerBot = bot.getName();  //запоминаем имя победившего бота
                            break;
                        }
                    }
                }
                if(cd == null){      //если не нашли карту такой же масти
                    for(Card card : bot.getDeck()){
                        if(card.getSuit() == contract.getValueOfTrump()){  //ищем козырную карту
                            cd = card;
                            winnerBot = bot.getName();
                            winnerCard = cd;
                        }
                    }
                    if(cd == null){   //если козырная карта не была найдена
                        cd = getSmallCard(bot, winnerCard, winnerBot);
                    }
                    else bot.getDeck().remove(cd);
                }
                else bot.getDeck().remove(cd);
            }
        }
        return  cd;
    }

}
