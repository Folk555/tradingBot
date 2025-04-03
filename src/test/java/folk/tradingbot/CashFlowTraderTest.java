package folk.tradingbot;

import folk.tradingbot.trader.CashFlowTrader;
import folk.tradingbot.trader.dto.TraderPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CashFlowTraderTest {

    @Autowired
    CashFlowTrader cashFlowTrader;

    @Disabled//не актуален
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
            🏦 Банк ВТБ
            
            Тикер: #VTBR
            
            🟢 ВХОД: 86.39Р
            🔴 Стоп: 81P
            💰 Цель: 100Р (+15.75%)
            🕯 Диапазон входа: 85.52 - 87.25Р
            ⚠️ Не рекомендация!
            ❗️ Высокий риск!
            ‼️ Дисклеймер
            
            Ваше мнение:
            👍Вырастет 👎Упадет 🔥Наблюдаю
                """;

        cashFlowTrader.cashFlow(message);

        TraderPosition traderPosition = cashFlowTrader.traderPositionRepo
                .getLastOpenTraderPositionByTicker("#VTBR");

        Assertions.assertNotNull(traderPosition);
        Assertions.assertTrue(traderPosition.getName().contains("Банк ВТБ"));
        Assertions.assertTrue(traderPosition.getTicker().contains("#VTBR"));
        Assertions.assertEquals(Float.valueOf(86.39f), traderPosition.getStartPrice());
        Assertions.assertEquals(Float.valueOf(100f), traderPosition.getProfitPrice());
        Assertions.assertEquals(Float.valueOf(15.75f), traderPosition.getProfitPercent());
        Assertions.assertNull(traderPosition.getCloseProfitPercent());
        Assertions.assertEquals(Float.valueOf(81f), traderPosition.getStopPrice());
        Assertions.assertFalse(traderPosition.isClosed());
    }

    @Test
    void cashFlow_reopenTraderPosition() {
        String openTrader = """
            ПОКУПКА LONG!
            какая-то штука
            
            Тикер: #MTLR
            
            🟢 ВХОД: 86.39Р
            🔴 Стоп: 81P
            💰 Цель: 100Р (+15.75%)
            🕯 Диапазон входа: 85.52 - 87.25Р
            ⚠️ Не рекомендация!
            ❗️ Высокий риск!
            ‼️ Дисклеймер
            
            Ваше мнение:
            👍Вырастет 👎Упадет 🔥Наблюдаю
                """;
        String reopenTrader = """
            ✅Фиксирую вторую 1/3 по лонгу #MTLR по 114.8Р, это +5.51%
            P.S. Стоп был переставлен в безубыток 108.81Р
                """;
        cashFlowTrader.cashFlow(openTrader);
        TraderPosition firstTraderPosition = cashFlowTrader.traderPositionRepo
                .getLastOpenTraderPositionByTicker("#MTLR");
        cashFlowTrader.cashFlow(reopenTrader);
        TraderPosition traderPosition = cashFlowTrader.traderPositionRepo
                .getLastOpenTraderPositionByTicker("#MTLR");

        Assertions.assertNotNull(traderPosition);
        Assertions.assertTrue(traderPosition.getName().contains("какая-то штука"));
        Assertions.assertTrue(traderPosition.getTicker().contains("#MTLR"));
        Assertions.assertEquals(Float.valueOf(114.8f), traderPosition.getStartPrice());
        Assertions.assertNull(traderPosition.getProfitPrice());
        Assertions.assertEquals(Float.valueOf(15.75f), traderPosition.getProfitPercent());
        Assertions.assertNull(traderPosition.getCloseProfitPercent());
        Assertions.assertEquals(Float.valueOf(108.81f), traderPosition.getStopPrice());
        Assertions.assertFalse(traderPosition.isClosed());

        firstTraderPosition = cashFlowTrader.traderPositionRepo
                .getTraderPositionById(Math.toIntExact(firstTraderPosition.getId()));
        Assertions.assertNotNull(firstTraderPosition);
        Assertions.assertTrue(firstTraderPosition.getName().contains("какая-то штука"));
        Assertions.assertTrue(firstTraderPosition.getTicker().contains("#MTLR"));
        Assertions.assertEquals(Float.valueOf(86.39f), firstTraderPosition.getStartPrice());
        Assertions.assertEquals(Float.valueOf(100f), firstTraderPosition.getProfitPrice());
        Assertions.assertEquals(Float.valueOf(15.75f), firstTraderPosition.getProfitPercent());
        Assertions.assertEquals(5.51f, firstTraderPosition.getCloseProfitPercent());
        Assertions.assertEquals(Float.valueOf(81f), firstTraderPosition.getStopPrice());
        Assertions.assertTrue(firstTraderPosition.isClosed());
    }

    @Test
    void cashFlow_reopenTraderPosition_noOpenPosition() {
        String reopenTrader = """
            ✅Фиксирую вторую 1/3 по лонгу #MRRR по 114.8Р, это +5.51%
            P.S. Стоп был переставлен в безубыток 108.81Р
                """;
        int firstSize = cashFlowTrader.traderPositionRepo.getAllTraderPositions().size();
        cashFlowTrader.cashFlow(reopenTrader);
        int secondSize = cashFlowTrader.traderPositionRepo.getAllTraderPositions().size();

        Assertions.assertEquals(firstSize, secondSize);
    }

    @Test
    void cashFlow_traderClosePosition() {
        String openTrader = """
            ПОКУПКА LONG!
            открыли
            
            Тикер: #PLZL
            
            🟢 ВХОД: 86.39Р
            🔴 Стоп: 81P
            💰 Цель: 100Р (+15.75%)
            🕯 Диапазон входа: 85.52 - 87.25Р
            ⚠️ Не рекомендация!
            ❗️ Высокий риск!
            ‼️ Дисклеймер
            
            Ваше мнение:
            👍Вырастет 👎Упадет 🔥Наблюдаю
                """;
        String closeTrader = """
                ✅Фиксирую финальную третью часть по лонгу #PLZL по 15500Р, это +11.07%💪🚀
                P.S. Мы фиксировали прибыль по бумаге два раза +5.16% и +7.77%
                Итого средняя прибыль по сделке составила +8%
                """;

        cashFlowTrader.cashFlow(openTrader);
        TraderPosition openTraderPosition = cashFlowTrader.traderPositionRepo
                .getLastOpenTraderPositionByTicker("#PLZL");
        int countBeforeClosePosition = cashFlowTrader.traderPositionRepo
                .getAllTraderPositions().size();
        cashFlowTrader.cashFlow(closeTrader);
        TraderPosition traderPosition = cashFlowTrader.traderPositionRepo
                .getTraderPositionById(Math.toIntExact(openTraderPosition.getId()));
        int countAfterClosePosition = cashFlowTrader.traderPositionRepo
                .getAllTraderPositions().size();

        Assertions.assertEquals(countBeforeClosePosition, countAfterClosePosition);
        Assertions.assertNotNull(traderPosition);
        Assertions.assertTrue(traderPosition.getName().contains("открыли"));
        Assertions.assertTrue(traderPosition.getTicker().contains("#PLZL"));
        Assertions.assertEquals(Float.valueOf(86.39f), traderPosition.getStartPrice());
        Assertions.assertEquals(Float.valueOf(100f), traderPosition.getProfitPrice());
        Assertions.assertEquals(Float.valueOf(15.75f), traderPosition.getProfitPercent());
        Assertions.assertEquals(11.07f, traderPosition.getCloseProfitPercent());
        Assertions.assertEquals(Float.valueOf(81f), traderPosition.getStopPrice());
        Assertions.assertTrue(traderPosition.isClosed());
    }

    @Test
    void cashFlow_reopenTraderPosition_debug() {
        String openTrader = """
                "ПОКУПКА LONG!
                Транснефть - привилегированные акции
                
                Тикер: #TRNFP
                
                 ВХОД: 1300Р
                 Стоп: 1152P
                 Цель: 1633Р (+31.06%)
                 Диапазон входа: 1233 - 1258Р
                 Не рекомендация!
                 Высокий риск!
                 Дисклеймер
                
                Ваше мнение:
                Вырастет Упадет Наблюдаю"
                """;
        cashFlowTrader.cashFlow(openTrader);

        String reopenTrader = """
            ✅Фиксирую первую 1/3 по лонгу #TRNFP по 1300Р это +4.33%
            P.S. Стоп был переставлен в безубыток 1246Р
            """;
        cashFlowTrader.cashFlow(reopenTrader);
    }

}