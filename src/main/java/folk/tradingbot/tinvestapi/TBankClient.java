package folk.tradingbot.tinvestapi;

import folk.tradingbot.tinvestapi.dto.TBankShare;
import folk.tradingbot.tinvestapi.repository.ShareRepo;
import folk.tradingbot.trader.dto.TraderPosition;
import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import ru.tinkoff.piapi.contract.v1.*;
import ru.tinkoff.piapi.core.InvestApi;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Position;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;


public class TBankClient {

    private static final Logger LOGGER = LogManager.getLogger(TBankClient.class);

    @Autowired
    private TbankConfigs configs;

    @Autowired
    private ShareRepo shareRepo;

    private String token;
//    private final InvestApi tBankApi = InvestApi.createSandbox(tokenSandbox);
    private InvestApi tBankApi;
    private String accountId;

    @PostConstruct
    private void init() {
        token = configs.token;
        tBankApi = InvestApi.create(token);
        accountId = configs.accountId;
    }

    private List<Share> getAllShares() {
        return tBankApi.getInstrumentsService().getTradableSharesSync();
    }

    public Share getShareByInstrumentId(String instrumentId) {
        return tBankApi.getInstrumentsService().getShareByUidSync(instrumentId);
    }

    public String buyShares(String instrumentId, double maxPricePerShare, long lotsCount) {
        String uid = instrumentId;
        Quotation maxPriceShare = Quotation.newBuilder()
                .setUnits((int) maxPricePerShare)
                .setNano((int) ((maxPricePerShare - (int) maxPricePerShare) * 1000000000)).build();

        LOGGER.debug("Попытка выставить заявку на покупку {} акций", lotsCount);
        CompletableFuture<PostOrderResponse> postOrderResponseAsync =
                tBankApi.getOrdersService().postLimitOrder(uid, lotsCount, maxPriceShare,
                OrderDirection.ORDER_DIRECTION_BUY, accountId, TimeInForceType.TIME_IN_FORCE_FILL_AND_KILL,
                null);

        PostOrderResponse response = null;
        try {
            response = postOrderResponseAsync.get();
            LOGGER.trace(response);
        } catch (InterruptedException e) {
            LOGGER.error(e);
        } catch (ExecutionException e) {
            LOGGER.error(e);
            if (e.getLocalizedMessage().contains("30034"))
                LOGGER.error("недостаточно средств");
        }
        return response.toString();
    }

    public double getCurrentSharePriceFromBroker(String uid) {
        Quotation price = tBankApi.getMarketDataService().getLastPricesSync(Collections.singleton(uid)).getFirst()
                .getPrice();
        long unitPrice = price.getUnits();
        long nanoPrice = price.getNano();
        double kopecks = nanoPrice / 1000000000.0;
        return unitPrice + kopecks;
    }

    public String saleShares(String instrumentId, int selLotsCount) {
        String uid = instrumentId;
        PostOrderResponse postOrderResponse = tBankApi.getOrdersService().postOrderSync(uid, selLotsCount,
                Quotation.newBuilder().setUnits(0).setNano(0).build(),
                OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET, null);
        return postOrderResponse.getOrderId();
    }

    public void updateSharesDB() {
        LOGGER.debug("Обновление акций банка в БД начато");
        List<Share> allBankShares = getAllShares();
        LOGGER.debug("Получено {} потенциальных акций", allBankShares.size());
        allBankShares.forEach(s -> {
            String ticker1 = s.getTicker().length() > 5 ? null : s.getTicker();
            boolean isEnable = ticker1 != null && s.getIsin().contains("RU");
            if (!isEnable) return;
            double minStepPrice = s.getMinPriceIncrement().getUnits() +
                    getKopecksSumFromNano(s.getMinPriceIncrement().getNano());

            TBankShare tBankShare = new TBankShare(s.getIsin(), ticker1,
                    s.getClassCode(), s.getUid(), s.getName(), minStepPrice, isEnable);
            TBankShare byIsin = shareRepo.findByIsin(s.getIsin());
            if (byIsin != null) {
                tBankShare.setEnableForTrade(byIsin.isEnableForTrade());
            }
            shareRepo.save(tBankShare);
        });
        LOGGER.debug("Завершено обновление акций банка в БД");
    }

    public void cancelPostStopLose(TraderPosition traderPosition) {
        LOGGER.debug("Отменяем стоп-лос для {}", traderPosition.getTicker());
        tBankApi.getStopOrdersService().cancelStopOrderSync(accountId, traderPosition.getStopLoseOrderId());
    }

    public String createPostStopLose(TraderPosition traderPosition) {
        LOGGER.debug("Устанавливаем стоп-лос для {}", traderPosition.getTicker());
        int lotsInPortfolio = getLotCountInPortfolioByInstrumentId(traderPosition.getShareInstrumentId());
        long stopPriceUnit = (long) traderPosition.getStopPrice().floatValue();
        BigDecimal stopPrice = new BigDecimal(String.valueOf(traderPosition.getStopPrice()));
        BigDecimal unitPrice = new BigDecimal(String.valueOf(stopPriceUnit));
        int stopPriceNano = getNanoSumFromKopecks(stopPrice.subtract(unitPrice));
        Quotation executePrice = Quotation.newBuilder().setUnits(stopPriceUnit).setNano(stopPriceNano).build();

        String res = tBankApi.getStopOrdersService().postStopOrderGoodTillCancelSync(
                traderPosition.getShareInstrumentId(),
                lotsInPortfolio,
                executePrice, executePrice,
                StopOrderDirection.STOP_ORDER_DIRECTION_SELL,
                accountId,
                StopOrderType.STOP_ORDER_TYPE_STOP_LOSS,
                (UUID) null);
        LOGGER.trace("Ответ на установку стоп-лос {}", res);
        return res;
    }

    public int getLotCountInPortfolioByInstrumentId(String instrumentId) {
        int shareInlot = tBankApi.getInstrumentsService().getShareByUidSync(instrumentId).getLot();
        Position positionInPortfolio = tBankApi.getOperationsService().getPortfolioSync(accountId).getPositions()
                .stream()
                .filter(position -> position.getInstrumentUid().equals(instrumentId))
                .findFirst().orElse(null);
        return positionInPortfolio == null ? 0 :
                (int) (positionInPortfolio.getQuantity().longValue() / shareInlot);
    }

    private void cancelStopLose(TraderPosition traderPosition) {
        tBankApi.getStopOrdersService().cancelStopOrderSync(accountId, traderPosition.getStopLoseOrderId());
    }

    private Integer getNanoSumFromKopecks(BigDecimal kopecksSum) {
        if (kopecksSum.intValue() >= 1)
            throw new RuntimeException("ошибка в методе getNanoSumFromKopecks");
        BigDecimal nanoSum = kopecksSum.multiply(BigDecimal.valueOf(1000000000));
        return nanoSum.intValue();
    }

    private Double getKopecksSumFromNano(Integer nanoSum) {
        return nanoSum / 1000000000.0;
    }

    public Portfolio getPortfel() {
        return tBankApi.getOperationsService().getPortfolioSync(accountId);
    }

    public Double getSharePriceByTicker(String ticker) {
        List<TBankShare> sharesByTicker = shareRepo.findSharesByTicker(ticker);
        if (sharesByTicker.isEmpty()) {
            LOGGER.error("В БД выгруженных акций брокера не найдена акция {}", ticker);
            return null;
        }
        String instrumentId = sharesByTicker.getFirst().getUid();
        LastPrice lastPrice = tBankApi.getMarketDataService()
                .getLastPricesSync(Collections.singleton(instrumentId)).getFirst();
        return lastPrice.getPrice().getUnits() + getKopecksSumFromNano(lastPrice.getPrice().getNano());
    }

    public String createPostTakeProfit(TraderPosition traderPosition) {
        LOGGER.debug("Устанавливаем тейк профит для {}", traderPosition.getTicker());
        int lotsInPortfolio = getLotCountInPortfolioByInstrumentId(traderPosition.getShareInstrumentId());
        double profitPrice = traderPosition.getProfitPrice();
        long profitPriceUnit = (long) profitPrice;
        BigDecimal bdProfitPrice = new BigDecimal(String.valueOf(profitPrice));
        BigDecimal unitPrice = new BigDecimal(String.valueOf(profitPriceUnit));
        int profitPriceNano = getNanoSumFromKopecks(bdProfitPrice.subtract(unitPrice));
        Quotation executePrice = Quotation.newBuilder().setUnits(profitPriceUnit).setNano(profitPriceNano).build();


        String res = tBankApi.getStopOrdersService().postStopOrderGoodTillCancelSync(
                traderPosition.getShareInstrumentId(),
                lotsInPortfolio,
                executePrice, executePrice,
                StopOrderDirection.STOP_ORDER_DIRECTION_SELL,
                accountId,
                StopOrderType.STOP_ORDER_TYPE_TAKE_PROFIT,
                (UUID) null);

        LOGGER.trace("Ответ на установку тейк профит {}", res);
        return res;
    }

    public double getMinStepPrice(String uid) {
        return shareRepo.findByIsin(uid).getMinPriceIncrement();
    }

}
