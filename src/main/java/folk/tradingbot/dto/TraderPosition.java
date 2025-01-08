package folk.tradingbot.dto;

import lombok.*;

import java.time.LocalDateTime;
@AllArgsConstructor
@ToString
public class TraderPosition {
    @NonNull
    @Getter @Setter
    Long id;
    @NonNull
    String name;
    @NonNull
    @Getter
    String ticker;
    TraderIdea traderIdea;
    @NonNull
    @Setter
    Float startPrice;
    @NonNull
    @Setter
    Float profitPrice;
    @Setter
    Float profitPercent;
    @NonNull
    @Setter
    Float stopPrice;
    @Getter @Setter
    boolean isClosed;
    @NonNull
    @Setter
    LocalDateTime openTime;
    @Setter
    LocalDateTime closeTime;

    @Override
    protected Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
