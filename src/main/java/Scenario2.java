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
import rest.RestTemplate;

import java.io.IOException;
import java.util.*;

import static domain.LocationUtils.locationIdByRow60;
import static domain.LocationUtils.positionByRow60;
import static rest.HttpMethod.GET;

@Slf4j
public class Scenario2 {
    private static int[][] reserveIn = new int[721][3600];
    private static int[][] reserveOut = new int[721][3600];
    private static ModelMapper modelMapper = new ModelMapper();

    public static void main(String[] args) throws IOException {
        StartResponse start = RestApi.startApi(new StartRequest(2));
        initReserveLog();

        List<int[]>[] outPatterns = new List[]{maxReserveOut(0, 239), maxReserveOut(240, 479), maxReserveOut(480, 720)};
        List<int[]>[] inPatterns = new List[]{maxReserveIn(0, 239), maxReserveIn(240, 479), maxReserveIn(480, 720)};
        TrucksResponse trucksResponse = RestApi.trucksApi(start.getAuthKey());
        Trucks trucks = modelMapper.map(trucksResponse, Trucks.class);

        for(int i = 0; i < 5; i++) {
            Truck truck = trucks.get(i);
            truck.setRow(60);
            int[] info = outPatterns[0].get(i);

            int[] locationPos = positionByRow60(info[0]);
            truck.move(locationPos[0], locationPos[1]);
        }

        for(int i = 0; i < 5; i++) {
            Truck truck = trucks.get(i);
            truck.setRow(60);
            int[] info = inPatterns[0].get(i);

            int[] locationPos = positionByRow60(info[0]);
            truck.move(locationPos[0], locationPos[1]);
        }

        while(true) {
            SimulateResponse response = requestSimulate(start, trucks);
            log.debug("{}", response);

            if(response.getStatus().equals("finished")) {
                ScoreResponse score = RestApi.scoreApi(start.getAuthKey());
                log.debug("{}", score);
                break;
            }

            trucksResponse = RestApi.trucksApi(start.getAuthKey());
            trucks = modelMapper.map(trucksResponse, Trucks.class);
            LocationsResponse locationsResponse = RestApi.locationApi(start.getAuthKey());
            Locations locations = modelMapper.map(locationsResponse, Locations.class);
            for(Location location : locations) {
                location.setRow(60);
            }
            int patternIndex = response.getTime() / 240;

            for(int i = 0; i < 5; i++) {
                Truck truck = trucks.get(i);
                truck.setRow(60);
                int[] info = outPatterns[patternIndex].get(i);

                log.debug("\n====truck[{}]====\n{}\n{}=>{}", truck.getId(), truck, truck.getLocationId(), info[0]);
                log.debug("({}, {}) => ({}, {})", truck.getPosition()[0], truck.getPosition()[1], positionByRow60(info[0])[0], positionByRow60(info[0])[1]);
                if(truck.getLocationId() != info[0]) {
                    int[] locationPos = positionByRow60(info[0]);
                    truck.move(locationPos[0], locationPos[1]);
                } else {
                    int[] truckPosition = truck.getPosition();
                    int[] minPos = positionByRow60(info[0]);
                    Location max = null;

                    for(int dy = -8; dy <= 8; dy++) {
                        for(int dx = -8; dx <= 8; dx++) {
                            if(isValid(truckPosition[0] + dy, truckPosition[1] + dx)) {
                                int id = locationIdByRow60(truckPosition[0] + dy, truckPosition[1] + dx);
                                Location location = locations.get(id);

                                if(distance(truckPosition, location.getPosition()) + distance(location.getPosition(), minPos) <= 8) {
                                    if(max == null || max.getBikeCount() < location.getBikeCount()) {
                                        max = location;
                                    }
                                }
                            }
                        }
                    }

                    if(max != null) {
                        transferMaxToMin(truck, max, locations.get(info[0]));
                    }
                }
            }

            for(int i = 0; i < 5; i++) {
                Truck truck = trucks.get(5 + i);
                truck.setRow(60);
                int[] info = inPatterns[patternIndex].get(i);

                log.debug("\n====truck[{}]====\n{}\n{}=>{}", truck.getId(), truck, truck.getLocationId(), info[0]);
                log.debug("({}, {}) => ({}, {})", truck.getPosition()[0], truck.getPosition()[1], positionByRow60(info[0])[0], positionByRow60(info[0])[1]);
                if(truck.getLocationId() != info[0]) {
                    int[] locationPos = positionByRow60(info[0]);
                    truck.move(locationPos[0], locationPos[1]);
                } else {
                    int[] truckPosition = truck.getPosition();
                    int[] maxPos = positionByRow60(info[0]);
                    Location min = null;

                    for(int dy = -8; dy <= 8; dy++) {
                        for(int dx = -8; dx <= 8; dx++) {
                            if(isValid(truckPosition[0] + dy, truckPosition[1] + dx)) {
                                int id = locationIdByRow60(truckPosition[0] + dy, truckPosition[1] + dx);
                                Location location = locations.get(id);

                                if(distance(truckPosition, maxPos) + distance(maxPos, location.getPosition()) <= 8) {
                                    if(min == null || min.getBikeCount() > location.getBikeCount()) {
                                        min = location;
                                    }
                                }
                            }
                        }
                    }

                    if(min != null) {
                        transferMaxToMin(truck, locations.get(info[0]), min);
                    }
                }
            }
        }
    }

    private static List<int[]> maxReserveOut(int start, int end) {
        int[] reserveOutSum = new int[3600];

        for(int minute = start; minute <= end; minute++) {
            for(int id = 0; id < 3600; id++){
                reserveOutSum[id] += reserveOut[minute][id];
            }
        }

        List<int[]> list = new ArrayList<>();
        for(int id = 0; id < 3600; id++) {
            list.add(new int[]{id, reserveOutSum[id]});
        }
        Collections.sort(list, (a, b) -> Integer.compare(b[1], a[1]));

        List<int[]> result = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            result.add(list.get(i));
        }

        return result;
    }

    private static List<int[]> maxReserveIn(int start, int end) {
        int[] reserveInSum = new int[3600];

        for(int minute = start; minute <= end; minute++) {
            for(int id = 0; id < 3600; id++){
                reserveInSum[id] += reserveIn[minute][id];
            }
        }

        List<int[]> list = new ArrayList<>();
        for(int id = 0; id < 3600; id++) {
            list.add(new int[]{id, reserveInSum[id]});
        }
        Collections.sort(list, (a, b) -> Integer.compare(b[1], a[1]));

        List<int[]> result = new ArrayList<>();
        for(int i = 0; i < 5; i++) {
            result.add(list.get(i));
        }

        return result;
    }

    private static void initReserveLog() throws IOException {
        initReserveLog(1);
        initReserveLog(2);
        initReserveLog(3);
    }

    private static void initReserveLog(int day) throws IOException {
        Map<Integer, List<List<Integer>>> data = getPrevScenarioData(day);

        for(int minute = 0; minute <= 720; minute++) {
            if(data.get(minute) == null) {
                continue;
            }

            List<List<Integer>> dayList = data.get(minute);
            for(List<Integer> list : dayList) {
                int out = list.get(0);
                int in = list.get(1);
                int time = list.get(2);

                if(minute + time <= 720) {
                    reserveOut[minute][out]++;
                    reserveIn[minute + time][in]++;
                }
            }
        }
    }

    private static Map<Integer, List<List<Integer>>> getPrevScenarioData(int day) throws IOException {
        Map<String, Object> jsonResponse =  RestTemplate
                .uri("https://grepp-cloudfront.s3.ap-northeast-2.amazonaws.com/programmers_imgs/competition-imgs/2021kakao/problem2_day-" + day + ".json")
                .method(GET)
                .getResponse(Map.class);

        Map<Integer, List<List<Integer>>> result = new HashMap<>();

        for(int minute = 0; minute <= 720; minute++) {
            String minuteToString = String.valueOf(minute);

            if(jsonResponse.get(minuteToString) != null) {
                List<List<Integer>> values = new ArrayList<>();
                List<Object> objects = (List<Object>) jsonResponse.get(minuteToString);

                for(Object object : objects) {
                    List<Integer> intList = (List<Integer>)object;
                    values.add(intList);
                }
                result.put(minute, values);
            }
        }
        return result;
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

    private static boolean isValid(int y, int x) {
        return (0 <= y && y < 60) && (0 <= x && x < 60);
    }
}
