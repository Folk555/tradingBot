package folk.tradingbot;

import folk.tradingbot.tinvestapi.TBankClient;
import folk.tradingbot.trader.FinamTrader;
import folk.tradingbot.trader.dto.TraderPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FinamTraderTest {

    @Autowired
    FinamTrader finamTrader;

    @Autowired
    private TBankClient tBankClient;

    @BeforeAll
    public void setup() {
        tBankClient.updateSharesDB();
    }

    @Test
    void cashFlow_traderSignalLong() {
        String message = """
                📈 ПАО «Татнефть»
                Тикер: #TATN
                Идея: Long ⬆️
                Срок идеи: 1 месяц
                Цель: 845 руб.
                Потенциал идеи: 22,6%
                Объем входа: 5%
                Стоп-приказ: 635 руб.
                
                📊 Технический анализ
                Цена двигается в рамках растущего тренда к сильному уровню сопротивления. При объеме позиции 5% и выставлении стоп-заявки на уровне 635 руб. риск на портфель составит 0,39%. Соотношение прибыль/риск составляет 2,89.
                
                ⛽️ Фундаментальный фактор
                #Татнефть — российская нефтяная компания. Чистая прибыль «Татнефти» по МСФО за 2024 год составила 308,9 млрд руб., увеличившись на 7,9% по сравнению с 286,3 млрд руб. в предыдущем году. Выручка выросла на 27,7% до 2,03 трлн руб. против 1,59 трлн руб. годом ранее.
                
                💡 AI-скринер
                Проверьте инвестиционную идею по TATN (https://ai.finam.ru/profile/MOEX:TATN?utm_source=telegram_alert&utm_medium=social&utm_campaign=ai_skriner) с помощью искусственного интеллекта (https://ai.finam.ru/profile/MOEX:TATN?utm_source=telegram_alert&utm_medium=social&utm_campaign=ai_skriner)💡
                
                🕯 Финам Торговые сигналы (https://t.me/+IrPbI36beWgzZGFi) - это рыночные сигналы, идеи, торговые прогнозы
                
                ❗️Данная информация не является индивидуальной инвестиционной рекомендацией, и финансовые инструменты либо сделки, упомянутые в ней, могут не соответствовать Вашему финансовому положению, цели (целям) инвестирования, допустимому риску, и (или) ожидаемой доходности. АО «ФИНАМ» не несет ответственности за возможные убытки в случае совершения сделок либо инвестирования в финансовые инструменты, упомянутые в данной информации.
                
                #мосбиржа #инвестиции #акции #аналитика #теханализ
                """;

        finamTrader.finamTrader(message);

        TraderPosition traderPosition = finamTrader.traderPositionRepo
                .getLastOpenTraderPositionByTicker("#TATN");

        Assertions.assertNotNull(traderPosition);
        Assertions.assertTrue(traderPosition.getName().contains("ПАО «Татнефть»"));
        Assertions.assertTrue(traderPosition.getTicker().contains("TATN"));
        Assertions.assertEquals(Float.valueOf(845f), traderPosition.getProfitPrice());
        Assertions.assertEquals(Float.valueOf(22.6f), traderPosition.getProfitPercent());
        Assertions.assertNull(traderPosition.getCloseProfitPercent());
        Assertions.assertEquals(Float.valueOf(635f), traderPosition.getStopPrice());
        Assertions.assertFalse(traderPosition.isClosed());
    }

    @Test
    void cashFlow_traderDebug() {
        String message = """
                📈 ПАО «РусГидро»
                Тикер: #HYDR
                Идея: Long ⬆️
                Срок идеи: 2-8 недель
                Цель: 0,5744 руб.
                Потенциал идеи: 11,5%
                Объем входа: 7%
                Стоп-приказ: 0,4989 руб.
                
                📊 Технический анализ
                Акции находятся на ключевом уровне поддержки. Идея на рост бумаги с целью 0,5744 руб. При объеме позиции 7% и выставлении стоп-заявки на уровне 0,4989 руб. риск на портфель составит 0,22%. Соотношение прибыль/риск составляет 3,7.
                
                🔌 Фундаментальный фактор
                #РусГидро входит в тройку крупнейших генерирующих компаний России по объему установленной мощности и контролирует более 600 объектов генерации в 31 регионе страны.
                
                Финансовые результаты «РусГидро» за 2024 год продемонстрировали уверенный рост выручки (+12,9% (г/г)) и EBITDA (+14,2% (г/г)), превысив наши ожидания. Основными драйверами стали увеличение продаж электроэнергии (+13,5% (г/г)) и мощности (+12,7% (г/г)) на фоне роста оптовых цен, тарифов и улучшения операционных показателей. При этом рост операционных расходов (+12,3% (г/г)) оставался относительно сдержанным, что способствовало повышению рентабельности.
                
                💡 AI-скринер
                Проверьте инвестиционную идею по HYDR с помощью искусственного интеллекта
                
                🕯 Финам Торговые сигналы - это рыночные сигналы, идеи, торговые прогнозы
                
                ❗️Данная информация не является индивидуальной инвестиционной рекомендацией, и финансовые инструменты либо сделки, упомянутые в ней, могут не соответствовать Вашему финансовому положению, цели (целям) инвестирования, допустимому риску, и (или) ожидаемой доходности. АО «ФИНАМ» не несет ответственности за возможные убытки в случае совершения сделок либо инвестирования в финансовые инструменты, упомянутые в данной информации.
                
                #мосбиржа #инвестиции #акции #аналитика #теханализ
                """;

        finamTrader.finamTrader(message);
    }

}