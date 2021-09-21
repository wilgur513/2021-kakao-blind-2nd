package domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class Truck {
    private int id;
    private int locationId;
    private int bikeCount;
    private List<Integer> command;

    public Truck() {
        command = new ArrayList<>();
    }

    public void move(int y, int x) {
        int[] truckPosition = LocationUtils.position(locationId);
        int diffY = Math.abs(truckPosition[0] - y);
        int diffX = Math.abs(truckPosition[1] - x);

        if(truckPosition[0] > y) {
            for(int i = 0; i < diffY; i++) {
                command.add(1);
            }
        } else if(truckPosition[0] < y) {
            for(int i = 0; i < diffY; i++) {
                command.add(3);
            }
        }

        if(truckPosition[1] > x) {
            for(int i = 0; i < diffX; i++) {
                command.add(4);
            }
        } else if (truckPosition[1] < x) {
            for(int i = 0; i < diffX; i++) {
                command.add(2);
            }
        }

        locationId = LocationUtils.locationId(y, x);
    }

    public void move(Location location) {
        int[] pos = location.getPosition();
        move(pos[0], pos[1]);
    }

    public void loadBike() {
        command.add(5);
    }

    public void unloadBike() {
        command.add(6);
    }

    public Command createCommand() {
        Command result = new Command();
        result.setTruckId(id);
        result.setCommand(command);
        return result;
    }

    public int[] getPosition() {
        return LocationUtils.position(locationId);
    }

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
