package domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class Command {
    private int truckId;
    private List<Integer> command;
}
