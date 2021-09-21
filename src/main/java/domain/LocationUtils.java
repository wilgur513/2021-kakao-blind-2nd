package domain;

public class LocationUtils {
    private static final int ROW = 5;

    public static int locationId(int y, int x) {
        return x*5 + (ROW - y - 1);
    }

    public static int[] position(int id) {
        int y = ROW - (id % 5) - 1;
        int x = id / 5;
        return new int[]{y, x};
    }
}
