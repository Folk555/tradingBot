package folk.tradingbot.tinvestapi.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;

@NoArgsConstructor
@ToString
@Entity
@Table
@Setter @Getter
@Accessors(chain = true)
public class TBankShare {
    @Id
    private String isin;
    private String ticker;
    private String classCode;
    private String uid;
    private String name;
    private boolean isEnableForTrade;

    public TBankShare(String isin, String ticker, String classCode, String uid, String name, boolean isEnableForTrade) {
        this.isin = isin;
        this.ticker = ticker;
        this.classCode = classCode;
        this.uid = uid;
        this.name = name;
        this.isEnableForTrade = isEnableForTrade;
    }
}
