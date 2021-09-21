package api.locations;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class LocationResponse {
    private int id;
    @JsonProperty("located_bikes_count")
    private int bikeCount;
}
