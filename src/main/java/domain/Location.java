package domain;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Location {
    private int row;
    private int id;
    private int bikeCount;

    public void setRow(int row) {
        this.row = row;
    }

    public int[] getPosition() {
        return LocationUtils.position(id, row);
    }

    public void incrementBike() {
        bikeCount++;
    }

    public void decrementBike() {
        bikeCount--;
    }
}
