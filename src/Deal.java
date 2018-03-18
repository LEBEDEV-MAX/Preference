import java.util.ArrayList;
import java.util.Arrays;

public class Deal {
    private Contract contract;  // контракт на игру
    private ArrayList<String> historyOfContracts;  // история заявок в раздаче
    private final ArrayList<Contract> tableOfContracts = new ArrayList<>(Arrays.asList(
            contract = new Contract(6, Contract.Suits.SPADES), contract = new Contract(6, Contract.Suits.CLUBS),
            contract = new Contract(6, Contract.Suits.DIAMONDS), contract = new Contract(6, Contract.Suits.HEARTS),
            contract = new Contract(6, Contract.Suits.NO_TRUMP),
            contract = new Contract(7, Contract.Suits.SPADES), contract = new Contract(7, Contract.Suits.CLUBS),
            contract = new Contract(7, Contract.Suits.DIAMONDS), contract = new Contract(7, Contract.Suits.HEARTS),
            contract = new Contract(7,Contract.Suits.NO_TRUMP),
            contract = new Contract(8, Contract.Suits.SPADES), contract = new Contract(8, Contract.Suits.CLUBS),
            contract = new Contract(8, Contract.Suits.DIAMONDS), contract = new Contract(8, Contract.Suits.HEARTS),
            contract = new Contract(8,Contract.Suits.NO_TRUMP),
            contract = new Contract(0,Contract.Suits.NO_TRUMP), //мизер
            contract = new Contract(9, Contract.Suits.SPADES), contract = new Contract(9, Contract.Suits.CLUBS),
            contract = new Contract(9, Contract.Suits.DIAMONDS), contract = new Contract(9, Contract.Suits.HEARTS),
            contract = new Contract(9,Contract.Suits.NO_TRUMP),
            contract = new Contract(10, Contract.Suits.SPADES), contract = new Contract(10, Contract.Suits.CLUBS),
            contract = new Contract(10, Contract.Suits.DIAMONDS), contract = new Contract(10, Contract.Suits.HEARTS),
            contract = new Contract(10,Contract.Suits.NO_TRUMP)
    ));

    Deal(){
        contract = null;
        historyOfContracts = new ArrayList<>();
    }

    public ArrayList<String> getContractHistory() {
        return historyOfContracts;
    }

    public void setContract(Bot bot, boolean somebodyIsPassed, Game.Gamestat gamestat, Contract bid){
        // игрок спасовал
        if(bid == null) {
            historyOfContracts.add(bot.getName() + ": Пас");
            bot.setBotStatus(false);
            return;
        }
        //если заявок не было (первая заявка)
        if(contract == null){
            bidIsOK(bot, bid);
            return;
        }
        int indexOfBid = tableOfContracts.indexOf(bid);
        int indexOfContract = tableOfContracts.indexOf(contract);
        //заявляет новый контракт (заказ мизера возможен только во время торгов)
        if(indexOfBid > indexOfContract){
            if(bid.getRank() == 0){   // мизер
                if(gamestat == Game.Gamestat.IS_DEAL){
                    bidIsOK(bot, bid);
                }
                //идет игра
                else{
                    return;
                }
            }
            else{
                bidIsOK(bot, bid);
            }
        }
        else if(indexOfBid == indexOfContract){
            //перебивает эту же заявку, при условии что это не мизер и один из игроков спасовал
            if(bid.getRank() != 0 & somebodyIsPassed){
                bidIsOK(bot, bid);
                bot.setBotStatus(true);
            }
            else {
                bot.setBotStatus(false); // нужно пасовать
                historyOfContracts.add(bot.getName() + ": Пас");
            }
        }
        //заявка меньше текущей заявки
        else {
            bot.setBotStatus(false); // нужно пасовать
            historyOfContracts.add(bot.getName() + ": Пас");
        }
    }

    private void bidIsOK(Bot bot, Contract bid){
        contract = bid;
        historyOfContracts.add(bot.getName() + ": " + bid.getNameOfContract());
        bot.setBotStatus(true);
    }
}
