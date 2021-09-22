package domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.List;

@Getter @Setter
public class Locations implements Iterable<Location> {
    private List<Location> locations;

    public Location get(int i) {
        return locations.get(i);
    }

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public Location findLocation(int y, int x) {
        int locationId = LocationUtils.locationIdByRow5(y, x);
        return locations.get(locationId);
    }

    @Override
    public Iterator<Location> iterator() {
        return locations.iterator();
    }
}
