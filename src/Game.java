import java.util.ArrayList;

public class Game {
    private int round;                                    //номер сдачи
    private int firsthand;                                //номер "первой руки"
    private Bot westBot, eastBot, southBot;               //боты
    private Gamestat gamestat;                            //статус игры
    private History history;
    private Analysis analysis;
    final private int bribe = 2;                          //коэфф. взятки
    final private int poolValue = 10;                     //конечное значение пули
    final private int[] pointsOfRemise = new int[]        //очки при ремизе (в гору)
            //Игра: 6   7   8   9   10   Мизер
                  { 4,  8,  12, 16, 20,  20};  //Без 1
            //следующие значения увеличиваются на номинальное (т.е. Без 2 = 8,16,24,32,40,40)
    final private int[] pointsOfPool = new int[]          //очки в пулю
            //Игра: 6   7   8   9   10   Мизер
                  { 2,  4,  6,  8,  10,  10};

    enum Gamestat{
        IS_DEAL,            //идут торги
        IS_PLAY,            //идет игра
        IS_ALL_PASS,        //если все спасовали
        IS_END              //конец игры
    }

    Game(){
        westBot = new Bot("West");
        eastBot = new Bot("East");
        southBot = new Bot("South");
        round = 0;
        firsthand = 0;
        history = new History();
        analysis = new Analysis();
        gamestat = Gamestat.IS_DEAL;
    }

    //раздача карт
    private void distribution(){
        Croupier cr = new Croupier();
        //добавляем 2 карты в прикуп
        ArrayList<Card> store = new ArrayList<>();
        for(int i=0; i<2; i++){
            store.add(cr.getCardFromDeck());
        }
        history.setStore(store);
        //раздаём карты ботам
        for(int i=0; i<10; i++){
            westBot.addCard(cr.getCardFromDeck());
            eastBot.addCard(cr.getCardFromDeck());
            southBot.addCard(cr.getCardFromDeck());
        }
        ArrayList<Card> decks = new ArrayList<>();
        decks.addAll(southBot.getDeck());
        decks.addAll(westBot.getDeck());
        decks.addAll(eastBot.getDeck());
        history.setDecks(decks);
        // сортируем карты у ботов
        westBot.sortingCards();
        eastBot.sortingCards();
        southBot.sortingCards();
    }

    //запуск игры
    public void playTheGame(){
 //       while(southBot.getPool() < poolValue && westBot.getPool() < poolValue && //пока все игроки не заполнят пулю
 //               eastBot.getPool() < poolValue){

            distribution();
            Deal deal = new Deal();
            //идут торги
            if(gamestat == Gamestat.IS_DEAL){
                dealing(deal);
            }
            //распасы
            if(gamestat == Gamestat.IS_ALL_PASS){
                allPass();
                gamestat = Gamestat.IS_END;
            }
            //игра
            if(gamestat == Gamestat.IS_PLAY){
                Contract contract = concludeTheFinalContract(deal);
                //определяем будут ли вистовать
                if(!southBot.getBotStatus()) setWhistStatus(contract, southBot);
                if(!westBot.getBotStatus())  setWhistStatus(contract, westBot);
                if(!eastBot.getBotStatus())  setWhistStatus(contract, eastBot);
                //если два игрока не вистуют - конец игры
                if(!southBot.getWhistStatus() & !westBot.getWhistStatus() ||
                        !southBot.getWhistStatus() & !eastBot.getWhistStatus() ||
                        !westBot.getWhistStatus()  & !eastBot.getWhistStatus()){
                    gamestat = Gamestat.IS_END;
                }
                else{
                    play(contract);
                }
            }
            //конец игры - подсчет очков
            if(gamestat == Gamestat.IS_END){

            }
            firsthand++;
            if(firsthand == 2) firsthand =0;
        }
  //  }

    //определяем: вистует ли бот
    private void setWhistStatus(Contract contract, Bot bot){
        Contract bid = analysis.getBid(bot.getDeck());
        switch (contract.getRank()){
            case 6:
                if(bid.getRank() >= 4) bot.setWhistStatus(true); //если может взять 4 и болеее при заказе 6
                break;
            case 7:
                if(bid.getRank() >= 2) bot.setWhistStatus(true); //если может взять 2 и болеее при заказе 7
                break;
            case 8: case 9: case 10: case 0:
                if(bid.getRank() >= 1) bot.setWhistStatus(true); //если может взять 1 и болеее при заказе 8,9,10,мизер
                break;
        }

    }

    //ведение торгов
    private void dealing(Deal deal){
        boolean somebodyIsPassed = false;        //кто-либо спасовал
        int voice = firsthand;                   //номер закачика контракта
        int n=0;
        while(gamestat == Gamestat.IS_DEAL){
            Contract bid;
            if(southBot.getBotStatus() && voice == 0) {
                bid = analysis.getBid(southBot.getDeck());
                deal.setContract(southBot, somebodyIsPassed, gamestat, bid);
            }
            voice++;
            n++;
            if(westBot.getBotStatus() && voice == 1) {
                bid = analysis.getBid(southBot.getDeck());
                deal.setContract(westBot, somebodyIsPassed, gamestat, bid);
            }
            voice++;
            n++;
            if(eastBot.getBotStatus() && voice == 2){
                bid = analysis.getBid(southBot.getDeck());
                deal.setContract(eastBot, somebodyIsPassed, gamestat, bid);
            }
            voice++;
            n++;
            if(voice == 2) voice=0;
            if(n == 3){   // когда все участвовали в торгах
                if(!(southBot.getBotStatus()) & !(westBot.getBotStatus()) & !(eastBot.getBotStatus()))      //все спасовали
                    gamestat = Gamestat.IS_ALL_PASS;
                if( (southBot.getBotStatus()) & !(westBot.getBotStatus()) & !(eastBot.getBotStatus()) ||   //играет юг
                   !(southBot.getBotStatus()) &   westBot.getBotStatus()  & !(eastBot.getBotStatus()) ||   //играет запад
                   !(southBot.getBotStatus()) & !(westBot.getBotStatus()) &   eastBot.getBotStatus()       //играет восток
                  )
                    gamestat = Gamestat.IS_PLAY;
            }
        }
    }

    private void play(Contract contract){
        int pointsOfSouth = 0, pointsOfWest = 0, pointsOfEast = 0;  //кол-во взяток каждого бота
        ArrayList<Card> moves = new ArrayList<>();                  //ходы в игре
        Card winnerCard = null;                                     //выигрышная карта
        String winnerBot = "";                                      //имя выигрышного бота
        int voice = firsthand;
        for(int i=0; i<10; i++){
            dropCards(winnerCard, moves, voice, contract);
            if(winnerBot.equals(southBot.getName())) {
                pointsOfSouth++;
                voice = 0;
            }
            if(winnerBot.equals(westBot.getName())) {
                pointsOfWest++;
                voice = 1;
            }
            if(winnerBot.equals(eastBot.getName())) {
                pointsOfEast++;
                voice = 2;
            }
        }
        history.setMoves(moves);
        setPoints(pointsOfSouth, southBot, contract);
        setPoints(pointsOfWest, westBot, contract);
        setPoints(pointsOfEast, eastBot, contract);
    }

    //распасы
    private void allPass(){
        int pointsOfSouth = 0, pointsOfWest = 0, pointsOfEast = 0;  //кол-во взяток каждого бота
        ArrayList<Card> moves = new ArrayList<>();                  //ходы в игре
        Card winnerCard;
        String winnerBot = "";
        int voice;
        for(int i=0; i<2; i++){
            voice = firsthand;
            winnerCard = history.getStore().get(round).get(i); //берем карту из прикупа
            dropCards(winnerCard, moves, voice);
            if(winnerBot.equals(southBot.getName())) pointsOfSouth++;
            if(winnerBot.equals(westBot.getName())) pointsOfWest++;
            if(winnerBot.equals(eastBot.getName())) pointsOfEast++;
        }
        voice = firsthand;
        for(int i=0; i<8; i++){
            winnerCard = null;
            dropCards(winnerCard, moves, voice);
            if(winnerBot.equals(southBot.getName())) {
                pointsOfSouth++;
                voice = 0;
            }
            if(winnerBot.equals(westBot.getName())) {
                pointsOfWest++;
                voice = 1;
            }
            if(winnerBot.equals(eastBot.getName())) {
                pointsOfEast++;
                voice = 2;
            }
        }
        history.setMoves(moves);
        setPoints(pointsOfSouth, pointsOfWest, pointsOfEast);
    }

    //сброс карт на стол на распасах
    private void dropCards(Card winnerCard, ArrayList<Card> moves, int voice){
        Card card = null;
        String winnerBot ="";
        int n = 0;
        while(n < 3){           //пока не походили все 3 бота
            switch (voice){
                case 0:
                    card = analysis.getSmallCard(southBot, winnerCard, winnerBot);
                    n++;
                    voice++;
                    break;
                case 1:
                    card = analysis.getSmallCard(westBot, winnerCard, winnerBot);
                    n++;
                    voice++;
                    break;
                case 2:
                    card = analysis.getSmallCard(eastBot, winnerCard, winnerBot);
                    n++;
                    voice = 0;
                    break;
                default:
                    moves.add(card);
            }
        }
    }

    //сброс карт на стол во время основной игры (с заказчиком и вистующими)
    private void dropCards(Card winnerCard, ArrayList<Card> moves, int voice, Contract contract){
        Card card = null;
        String winnerBot ="";
        int n = 0;
        while(n < 3){           //пока не походили все 3 бота
            switch (voice){
                case 0:
                    card = analysis.getCard(southBot, winnerCard, winnerBot, contract);
                    n++;
                    voice++;
                    break;
                case 1:
                    card = analysis.getCard(westBot, winnerCard, winnerBot, contract);
                    n++;
                    voice++;
                    break;
                case 2:
                    card = analysis.getCard(eastBot, winnerCard, winnerBot, contract);
                    n++;
                    voice = 0;
                    break;
                default:
                    moves.add(card);
            }
        }
    }

    //запись очков при распасах
    private void setPoints(int pointsOfSouth, int pointsOfWest, int pointsOfEast){
        //если 0 взяток, то пишем в пулю
        if(pointsOfSouth == 0) southBot.setPool(bribe);
        if(pointsOfWest == 0) westBot.setPool(bribe);
        if(pointsOfEast == 0) eastBot.setPool(bribe);

        int max = Math.max(Math.max(pointsOfSouth, pointsOfWest), pointsOfEast);
        //если набрал больше всех взяток, пишем в гору
        if(pointsOfSouth == max) southBot.setMountain(bribe * pointsOfSouth);
        if(pointsOfWest == max) westBot.setMountain(bribe * pointsOfWest);
        if(pointsOfEast == max) eastBot.setMountain(bribe * pointsOfEast);
    }

    //запись очков при обычной игре
    private void setPoints(int points, Bot bot, Contract contract){
        if(bot.getBotStatus()){                 //если данный бот был заказчиком
            switch (contract.getRank()){
                case 6:
                    if(points >= 6) bot.setPool(pointsOfPool[0]);
                    else bot.setMountain(pointsOfRemise[0]*(6 - points));
                    break;
                case 7:
                    if(points >= 7) bot.setPool(pointsOfPool[1]);
                    else bot.setMountain(pointsOfRemise[1]*(7 - points));
                    break;
                case 8:
                    if(points >= 8) bot.setPool(pointsOfPool[2]);
                    else bot.setMountain(pointsOfRemise[2]*(8 - points));
                    break;
                case 9:
                    if(points >= 9) bot.setPool(pointsOfPool[3]);
                    else bot.setMountain(pointsOfRemise[3]*(9 - points));
                    break;
                case 10:
                    if(points == 10) bot.setPool(pointsOfPool[4]);
                    else bot.setMountain(pointsOfRemise[4]*(10 - points));
                    break;
                case 0:
                    if(points == 0) bot.setPool(pointsOfPool[5]);
                    else bot.setMountain(pointsOfRemise[5]* points);
                    break;
            }
        }
        if(bot.getWhistStatus()){               //если бот вистовал
            int whist =0;
            switch (contract.getRank()){
                case 6:
                    if(points < 3) bot.setMountain(pointsOfPool[0] * (4-points));
                    whist = pointsOfRemise[0]*points;
                    break;
                case 7:
                    if(points < 2) bot.setMountain(pointsOfPool[1] * (2-points));
                    whist = pointsOfRemise[1]*points;
                    break;
                case 8:
                    if(points < 1) bot.setMountain(pointsOfPool[2]);
                    whist = pointsOfRemise[2]*points;
                    break;
                case 9:
                    if(points < 1) bot.setMountain(pointsOfPool[3]);
                    whist = pointsOfRemise[3]*points;
                    break;
                case 10:
                    if(points < 1) bot.setMountain(pointsOfPool[4]);
                    whist = pointsOfRemise[4]*points;
                    break;
            }
            setWhist(whist, bot.getName());
        }
    }

    //записываем висты на игрока
    private void setWhist(int whist, String botName){
        /* Расположение игроков:
            Запад (слева) <- Юг -> (справа)Восток
            Юг (слева) <- Восток -> (справа)Запад
            Восток (слева) <- Запад -> (справа)Юг
        */
        if(westBot.getBotStatus()){                                               //если играет Запад
            if(botName.equals(southBot.getName())) southBot.setLeftWhist(whist);  //вистовал Юг
            if(botName.equals(eastBot.getName())) eastBot.setRightWhist(whist);   //Вистовал Восток
        }
        if(eastBot.getBotStatus()){                                               //если играет Восток
            if(botName.equals(southBot.getName())) southBot.setRightWhist(whist); //вистовал Юг
            if(botName.equals(westBot.getName())) westBot.setLeftWhist(whist);    //вистовал Запад
        }
        if(southBot.getBotStatus()){                                              //если играет Юг
            if(botName.equals(westBot.getName())) westBot.setRightWhist(whist);   //вистовал Запад
            if(botName.equals(eastBot.getName())) eastBot.setLeftWhist(whist);    //вистовал Восток
        }
    }

    //объявление окончательного контракта
    private Contract concludeTheFinalContract(Deal deal){
        Contract contract = null;
        if(southBot.getBotStatus()) {
            contract = getContract(southBot);
        }
        if(westBot.getBotStatus()) {
            contract = getContract(westBot);
        }
        if(eastBot.getBotStatus()) {
            contract = getContract(eastBot);
        }
        return contract;
    }

    //получаем контракт для бота
    private Contract getContract(Bot bot){
        for(Card card : history.getStore().get(round)){ //добавляем карты из прикупа в колоду
            bot.addCard(card);
        }
        bot.sortingCards();
        return analysis.getFinalContract(bot.getDeck(), history);

    }
}
