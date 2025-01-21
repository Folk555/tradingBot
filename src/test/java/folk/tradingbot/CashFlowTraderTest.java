package folk.tradingbot;

import folk.tradingbot.trader.CashFlowTrader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class CashFlowTraderTest {

    @Test
    void cashFlow_traderIdea() {
        String message = """
                Инвестиционная идея: OZON #OZON
                С прошлого нашего обзора цена долгое время
                находилась в консолидации, но сейчас приблизилась к
                сильной поддержке 2500Р, от которой возможен отскок.
                Однако сохраняется риск падения до 1600-1400 рублей.
                Поэтому стоит использовать стратегию поэтапного входа
                в позицию для оптимизации средней цены.
                Торговый план:
                Если вы только планируете набирать позицию, то
                сейчас стоит рассмотреть возможность покупки не более
                чем на 1/3 от планируемого объема.
                Если компания продолжит снижаться, то вторую 1/3
                часть позиции купить по 1600Р.
                Последнюю 1/3 позиции купить в случае снижения к
                1400Р.
                Цели:
                Первая цель: 2900Р (+14%),
                Вторая цель: 3440Р (+34%),
                Третья цель: 4000Р (+56%),
                Третья цель: 4800Р (+86%),
                Срок реализации от 12 до 18 месяцев.
                Не рекомендация!
                Дисклеймер
                #идея
                Ваше мнение:
                Вырастет, Упадет, Наблюдаю
                """;
        CashFlowTrader cashFlowTrader = new CashFlowTrader();
        cashFlowTrader.cashFlow(message);

        Assertions.assertFalse(cashFlowTrader.traderIdeaRepo.getAllTraderIdeas().isEmpty());

        System.out.println(cashFlowTrader.traderIdeaRepo.getAllTraderIdeas());
    }

    @Test
    void cashFlow_traderSignalLong() {
        String message = """
                ПОКУПКА LONG!
                Полюс
                Тикер: #PLZL
                ВХОД: 13955Р
                Стоп: 13040P
                Цель: 15300Р (+9.64%)
                Диапазон входа: 13815 - 14094Р
                Не рекомендация!
                Высокий риск!
                Дисклеймер
                Ваше мнение:
                Вырастет Упадет Наблюдаю
                """;
        CashFlowTrader cashFlowTrader = new CashFlowTrader();
        cashFlowTrader.cashFlow(message);

        Assertions.assertFalse(cashFlowTrader.traderPositionRepo.getAllTraderPosition().isEmpty());

        cashFlowTrader.traderPositionRepo.getAllTraderPosition().forEach(System.out::println);
    }

    @Test
    void cashFlow_reopenTraderPosition() {
        String message = """
                Фиксирую вторую 1/3 по лонгу #GAZP по 119.5Р, это +10.58%
                P.S. Стоп был переставлен в безубыток 108.07Р
                """;
        CashFlowTrader cashFlowTrader = new CashFlowTrader();
        cashFlowTrader.cashFlow(message);

        Assertions.assertFalse(cashFlowTrader.traderIdeaRepo.getAllTraderIdeas().isEmpty());

        System.out.println(cashFlowTrader.traderIdeaRepo.getAllTraderIdeas());
    }
}