import api.RestApi;
import api.locations.LocationsResponse;
import api.score.ScoreResponse;
import api.simulate.CommandRequest;
import api.simulate.SimulateRequest;
import api.simulate.SimulateResponse;
import api.start.StartRequest;
import api.start.StartResponse;
import api.trucks.TrucksResponse;
import domain.*;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class Application {
    private static final ModelMapper modelMapper = new ModelMapper();
    private static int[][] startPosition = {{1, 1}, {3, 3}, {1, 3}, {3, 1}, {2, 2}};
    private static int[][] rangeY = {{0, 2}, {2, 4}, {0, 2}, {2, 4}, {1, 3}};
    private static int[][] rangeX = {{0, 2}, {2, 4}, {2, 4}, {0, 2}, {1, 3}};

    public static void main(String[] args) {
        StartResponse start = RestApi.startApi(new StartRequest(1));
        log.debug("start : {}", start);

        Trucks trucks = requestTrucksAndMapping(start);
        for(int i = 0; i < 5; i++) {
            Truck truck = trucks.get(i);
            truck.move(startPosition[i][0], startPosition[i][1]);
        }

        while(true) {
            SimulateResponse simulateResponse = requestSimulate(start, trucks);
            log.debug("\n====simulate response====\n{}", simulateResponse);

            if(simulateResponse.getStatus().equals("finished")) {
                ScoreResponse scoreResponse = RestApi.scoreApi(start.getAuthKey());
                log.debug("\n====score====\n{}", scoreResponse);
                return;
            }

            Locations locations = requestLocationsAndMapping(start);
            trucks = requestTrucksAndMapping(start);

            for(int i = 0; i < 5; i++) {
                Truck truck = trucks.get(i);
                Location min = null;

                for(int y = rangeY[i][0]; y <= rangeY[i][1]; y++) {
                    for(int x = rangeX[i][0]; x <= rangeX[i][1]; x++) {
                        Location location = locations.findLocation(y, x);
                        if(min == null || min.getBikeCount() > location.getBikeCount()) {
                            min = location;
                        }
                    }
                }

                Location max = null;

                for(int y = rangeY[i][0]; y <= rangeY[i][1]; y++) {
                    for(int x = rangeX[i][0]; x <= rangeX[i][1]; x++) {
                        Location location = locations.findLocation(y, x);
                        if(distance(truck.getPosition(), location.getPosition()) + distance(location.getPosition(), min.getPosition()) <= 8) {
                            if(max == null || max.getBikeCount() < location.getBikeCount()) {
                                max = location;
                            }
                        }
                    }
                }

                if(max != null || max.getBikeCount() != min.getBikeCount()) {
                    transferMaxToMin(truck, max, min);
                }
            }
        }
    }

    private static SimulateResponse requestSimulate(StartResponse start, Trucks trucks) {
        List<CommandRequest> commandRequests = new ArrayList<>();
        for(Truck truck : trucks) {
            Command command = truck.createCommand();
            CommandRequest request = modelMapper.map(command, CommandRequest.class);
            commandRequests.add(request);
        }
        SimulateRequest simulateRequest = new SimulateRequest();
        simulateRequest.setCommands(commandRequests);
        log.debug("\n====simulate request====\n{}", simulateRequest);

        return RestApi.simulateApi(simulateRequest, start.getAuthKey());
    }

    private static Trucks requestTrucksAndMapping(StartResponse start) {
        TrucksResponse response = RestApi.trucksApi(start.getAuthKey());
        Trucks trucks = modelMapper.map(response, Trucks.class);
        log.debug("\n====trucks====\n{}", trucks);
        return trucks;
    }

    private static Locations requestLocationsAndMapping(StartResponse start) {
        LocationsResponse locationsResponse = RestApi.locationApi(start.getAuthKey());
        Locations locations = modelMapper.map(locationsResponse, Locations.class);
        log.debug("\n====locations====\n{}", locations);
        return locations;
    }

    private static int distance(int[] from, int[] to) {
        return Math.abs(from[0] - to[0]) + Math.abs(from[1] - to[1]);
    }

    private static void transferMaxToMin(Truck truck, Location max, Location min) {
        truck.move(max);
        truck.loadBike();
        truck.move(min);
        truck.unloadBike();
        max.decrementBike();
        min.incrementBike();
        log.debug("\n====truck[{}]====\n{}\nMax: {} => Min: {}", truck.getId(), truck, max.getId(), min.getId());
    }
}
