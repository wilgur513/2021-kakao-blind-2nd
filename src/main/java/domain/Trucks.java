package domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.util.Iterator;
import java.util.List;

@Getter @Setter
public class Trucks implements Iterable<Truck>{
    private List<Truck> trucks;

    public Truck get(int i) {
        return trucks.get(i);
    }

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @Override
    public Iterator<Truck> iterator() {
        return trucks.iterator();
    }
}
