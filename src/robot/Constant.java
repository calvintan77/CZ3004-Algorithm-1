package robot;

public class Constant {
    // range of short range sensor (tiles)
    public static final int SENSOR_SHORT_RANGE_MIN = 1;
    // range of short range sensor (tiles)
    public static final int SENSOR_SHORT_RANGE_MAX = 2;
    // range of long range sensor (tiles)
    public static final int SENSOR_LONG_RANGE_MIN = 3;
    // range of long range sensor (tiles)
    public static final int SENSOR_LONG_RANGE_MAX = 4;


    public static final int GOAL_ROW = 18;
    public static final int GOAL_COL = 13;
    public static final int START_ROW = 1;
    public static final int START_COL = 1;


    public static final int MOVE_COST = 10;
    public static final int TURN_COST = 20;
    public static final int INFINITE_COST = 9999;

    public static final int SPEED = 100;
    public static final DIRECTION START_DIR = DIRECTION.NORTH;
    

    public enum MOVEMENT {
        FORWARD, BACKWARD, RIGHT, LEFT, CALIBRATE, ERROR;
    }

    public enum DIRECTION {
        NORTH, EAST, SOUTH, WEST;

        public static DIRECTION next(DIRECTION cur) {
            return values()[(cur.ordinal() + 1) % values().length];
        }

        public static DIRECTION prev(DIRECTION cur) {
            return values()[(cur.ordinal() + values().length - 1) % values().length];
        }

    }
}