package folk.tradingbot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NonNull
public class TraderIdea {
    Long id;
    String name;
    String ticker;
    ArrayList<Integer> goals;
    TraderIdeaStatus status;
}
