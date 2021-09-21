package api;

import api.locations.LocationsResponse;
import api.score.ScoreResponse;
import api.simulate.SimulateRequest;
import api.simulate.SimulateResponse;
import api.start.StartRequest;
import api.start.StartResponse;
import api.trucks.TrucksResponse;
import rest.RestTemplate;

import static rest.HttpMethod.*;

public class RestApi {
    private static final String BASE_URL = "https://kox947ka1a.execute-api.ap-northeast-2.amazonaws.com/prod/users";
    private static final String TOKEN = "43b4d731e55c3568a5e83d5812b1df78";

    public static StartResponse startApi(StartRequest request) {
        return RestTemplate
                .uri(BASE_URL + "/start")
                .method(POST)
                .header("X-Auth-Token", TOKEN)
                .header("Content-Type", "application/json")
                .body(request)
                .getResponse(StartResponse.class);
    }

    public static LocationsResponse locationApi(String authKey) {
        return RestTemplate
                .uri(BASE_URL + "/locations")
                .method(GET)
                .header("Authorization", authKey)
                .header("Content-Type", "application/json")
                .getResponse(LocationsResponse.class);
    }

    public static TrucksResponse trucksApi(String authKey) {
        return RestTemplate
                .uri(BASE_URL + "/trucks")
                .method(GET)
                .header("Authorization", authKey)
                .header("Content-Type", "application/json")
                .getResponse(TrucksResponse.class);
    }

    public static SimulateResponse simulateApi(SimulateRequest request, String authKey) {
        return RestTemplate
                .uri(BASE_URL + "/simulate")
                .method(PUT)
                .header("Authorization", authKey)
                .header("Content-Type", "application/json")
                .body(request)
                .getResponse(SimulateResponse.class);
    }

    public static ScoreResponse scoreApi(String authKey) {
        return RestTemplate
                .uri(BASE_URL + "/score")
                .method(GET)
                .header("Authorization", authKey)
                .header("Content-Type", "application/json")
                .getResponse(ScoreResponse.class);
    }
}
