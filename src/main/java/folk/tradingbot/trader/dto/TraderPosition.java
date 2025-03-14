package folk.tradingbot.trader.dto;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "trader_position")
@Setter @Getter
@Accessors(chain = true)
public class TraderPosition {
    @Setter(AccessLevel.NONE)
    @Id @GeneratedValue(strategy = IDENTITY)
    private Long id;
    private String name;
    private String ticker;
    //todo пока без этого функционала
    //TraderIdea traderIdea;
    private Float startPrice;
    private Float profitPrice;
    private Float profitPercent;
    private Float closeProfitPercent;
    private Float stopPrice;
    private boolean isClosed;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
    @Column(length = 1024)
    private String errorCreate;
    private String shareInstrumentId;
    private String stopLoseOrderId;
    private String takeProfitOrderId;
    private String traderChanel;

    public TraderPosition(String name, String ticker, Float startPrice, Float profitPrice,
                          Float profitPercent, Float closeProfitPercent, Float stopPrice, boolean isClosed,
                          LocalDateTime openTime, LocalDateTime closeTime, String traderChanel) {
        this.name = name;
        this.ticker = ticker;
        this.startPrice = startPrice;
        this.profitPrice = profitPrice;
        this.profitPercent = profitPercent;
        this.closeProfitPercent = closeProfitPercent;
        this.stopPrice = stopPrice;
        this.isClosed = isClosed;
        this.openTime = openTime;
        this.closeTime = closeTime;
        this.traderChanel = traderChanel;
    }

    public TraderPosition(TraderPosition traderPosition) {
        this.name = traderPosition.name;
        this.ticker = traderPosition.ticker;
        //this.traderIdea = traderPosition.traderIdea;
        this.startPrice = traderPosition.startPrice;
        this.profitPrice = traderPosition.profitPrice;
        this.profitPercent = traderPosition.profitPercent;
        this.closeProfitPercent = traderPosition.closeProfitPercent;
        this.stopPrice = traderPosition.stopPrice;
        this.isClosed = traderPosition.isClosed;
        this.openTime = traderPosition.openTime;
        this.closeTime = traderPosition.closeTime;
    }
}
