package api.simulate;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CommandRequest {
    @JsonProperty("truck_id")
    private int truckId;
    private List<Integer> command;
}
