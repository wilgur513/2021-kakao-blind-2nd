package api.simulate;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter @Setter
public class SimulateResponse {
    private String status;
    private int time;
    @JsonProperty("failed_requests_count")
    private double failedRequestsCount;
    private String distance;

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
