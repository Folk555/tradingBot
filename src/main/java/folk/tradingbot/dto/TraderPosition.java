package folk.tradingbot.dto;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@ToString
public class TraderPosition {
    @Getter @Setter
    Long id;
    String name;
    @Getter
    String ticker;
    TraderIdea traderIdea;
    @Setter
    Float startPrice;
    @Setter
    Float profitPrice;
    @Setter
    Float profitPercent;
    @Setter
    Float stopPrice;
    @Getter @Setter
    boolean isClosed;
    @Setter
    LocalDateTime openTime;
    @Setter
    LocalDateTime closeTime;

    public TraderPosition(TraderPosition traderPosition) {
        this.id = traderPosition.id;
        this.name = traderPosition.name;
        this.ticker = traderPosition.ticker;
        this.traderIdea = traderPosition.traderIdea;
        this.startPrice = traderPosition.startPrice;
        this.profitPrice = traderPosition.profitPrice;
        this.profitPercent = traderPosition.profitPercent;
        this.stopPrice = traderPosition.stopPrice;
        this.isClosed = traderPosition.isClosed;
        this.openTime = traderPosition.openTime;
        this.closeTime = traderPosition.closeTime;
    }
}
