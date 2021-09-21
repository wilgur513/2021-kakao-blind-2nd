package domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Location {
    private int id;
    private int bikeCount;

    public int[] getPosition() {
        return LocationUtils.position(id);
    }

    public void incrementBike() {
        bikeCount++;
    }

    public void decrementBike() {
        bikeCount--;
    }
}
