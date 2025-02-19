package folk.tradingbot.tinvestapi;

import folk.tradingbot.tinvestapi.dto.TBankShare;
import folk.tradingbot.tinvestapi.repository.ShareRepo;
import folk.tradingbot.trader.dto.TraderPosition;
import jakarta.annotation.PostConstruct;
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

    @Autowired
    TbankConfigs configs;

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

    private Share getShareByInstrumentId(String instrumentId) {
        return tBankApi.getInstrumentsService().getShareByUidSync(instrumentId);
    }

    public String buyShares(String instrumentId, double maxPricePerShare, double buySum) {
        String uid = instrumentId;

        Quotation price = tBankApi.getMarketDataService().getLastPricesSync(Collections.singleton(uid)).getFirst()
                .getPrice();
        long unitPrice = price.getUnits();
        long nanoPrice = price.getNano();
        double kopecks = nanoPrice / 1000000000.0;
        double currentRubPricePerShare = unitPrice + kopecks;
        if (currentRubPricePerShare > maxPricePerShare) {
            System.out.println("Цена за акцию превышает максимальную допустимую цену за акцию");
            return null;
        }

        int lot = getShareByInstrumentId(uid).getLot();
        double currentRubPricePerLot = lot * currentRubPricePerShare;
        int buyCountLots = (int) (buySum / currentRubPricePerLot);
        if (buyCountLots < 1) {
            System.out.println("Цена за лот превышает максимальную цену закупки акций");
            return null;
        }
        Quotation maxPriceShare = Quotation.newBuilder()
                .setUnits((int) maxPricePerShare)
                .setNano((int) ((maxPricePerShare - (int) maxPricePerShare) * 1000000000)).build();

        CompletableFuture<PostOrderResponse> postOrderResponseAsync = tBankApi.getOrdersService().postLimitOrder(uid, buyCountLots, maxPriceShare,
                OrderDirection.ORDER_DIRECTION_BUY, accountId, TimeInForceType.TIME_IN_FORCE_FILL_AND_KILL,
                null);

        PostOrderResponse response;
        try {
            response = postOrderResponseAsync.get();
            System.out.println(response);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            System.out.println(e);
            if (e.getLocalizedMessage().contains("30034"))
                System.out.println("недостаточно средств");
            throw new RuntimeException(e);
        }
        return response.toString();
    }

    public String saleShares(String instrumentId, int selLotsCount) {
        String uid = instrumentId;
        PostOrderResponse postOrderResponse = tBankApi.getOrdersService().postOrderSync(uid, selLotsCount,
                Quotation.newBuilder().setUnits(0).setNano(0).build(),
                OrderDirection.ORDER_DIRECTION_SELL, accountId, OrderType.ORDER_TYPE_MARKET, null);
        return postOrderResponse.getOrderId();
    }

    public void loadShares() {
        List<Share> allBankShares = getAllShares();
        allBankShares.forEach(s -> {
            String ticker1 = s.getTicker().length() > 5 ? null : s.getTicker();
            boolean isEnable = ticker1 != null && s.getIsin().contains("RU");

            TBankShare tBankShare = new TBankShare(s.getIsin(), ticker1,
                    s.getClassCode(), s.getUid(), s.getName(), isEnable);
            if (isEnable)
                shareRepo.save(tBankShare);
        });
    }

    public void cancelPostStopLose(TraderPosition traderPosition) {
        tBankApi.getStopOrdersService().cancelStopOrderSync(accountId, traderPosition.getStopLoseOrderId());
    }

    public String createPostStopLose(TraderPosition traderPosition) {
        int lotsInPortfolio = getLotCountInPortfolioByInstrumentId(traderPosition.getShareInstrumentId());
        long stopPriceUnit = (long) traderPosition.getStopPrice().floatValue();
        BigDecimal stopPrice = new BigDecimal(String.valueOf(traderPosition.getStopPrice()));
        BigDecimal unitPrice = new BigDecimal(String.valueOf(stopPriceUnit));
        int stopPriceNano = getNanoSumFromKopecks(stopPrice.subtract(unitPrice));
        Quotation executePrice = Quotation.newBuilder().setUnits(stopPriceUnit).setNano(stopPriceNano).build();

        return tBankApi.getStopOrdersService().postStopOrderGoodTillCancelSync(
                traderPosition.getShareInstrumentId(),
                lotsInPortfolio,
                executePrice, executePrice,
                StopOrderDirection.STOP_ORDER_DIRECTION_SELL,
                accountId,
                StopOrderType.STOP_ORDER_TYPE_STOP_LOSS,
                (UUID) null);
    }

    public int getLotCountInPortfolioByInstrumentId(String instrumentId) {
        int shareInlot = tBankApi.getInstrumentsService().getShareByUidSync(instrumentId).getLot();
        Position positionInPortfolio = tBankApi.getOperationsService().getPortfolioSync(accountId).getPositions()
                .stream()
                .filter(position -> position.getInstrumentUid().equals(instrumentId))
                .findFirst().orElse(null);
        return (int) (positionInPortfolio.getQuantity().longValue() / shareInlot);
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

    private Double getKopecksSumFromNano(Long nanoSum) {
        return nanoSum / 1000000000.0;
    }

    public Portfolio getPortfel() {
        return tBankApi.getOperationsService().getPortfolioSync(accountId);
    }

}
