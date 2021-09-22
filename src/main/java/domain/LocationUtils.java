package domain;

public class LocationUtils {
    public static int locationIdByRow5(int y, int x) {
        return locationId(y, x, 5);
    }

    public static int[] positionByRow5(int id) {
        return position(id, 5);
    }

    public static int locationIdByRow60(int y, int x) {
        return locationId(y, x, 60);
    }

    public static int[] positionByRow60(int id) {
        return position(id, 60);
    }

    public static int locationId(int y, int x, int row) {
        return x*row + (row - y - 1);
    }

    public static int[] position(int id, int row) {
        int y = row - (id % row) - 1;
        int x = id / row;
        return new int[]{y, x};
    }
}
