package api.trucks;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TruckResponse {
    private int id;
    @JsonProperty("location_id")
    private int locationId;
    @JsonProperty("loaded_bikes_count")
    private int bikeCount;
}
