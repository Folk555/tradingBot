package folk.tradingbot.trader.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NonNull
@ToString
public class TraderIdea {
    Long id;
    String name;
    String ticker;
    ArrayList<Integer> goals;
    TraderIdeaStatus status;


}
