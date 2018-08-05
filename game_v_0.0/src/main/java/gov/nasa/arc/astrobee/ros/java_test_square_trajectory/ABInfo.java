package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class ABInfo {
    /**
     * Class to hold game-critical info about AB state
     */
    protected static final double collider_step = 0.4;
    protected static final double collider_radius = 0.26;
    protected static final SQuaternion initial_orient = new SQuaternion(0, 0, 0, 1);
    protected static final SVector intial_vector = new SVector(1, 0, 0);

    private static ABInfo instance = null;
    protected static String POLLEN_TYPE;
    protected static int pollinateSuccesses;
    protected static int pollinateAttempts;
    protected static int score;

    private ABInfo () {
        POLLEN_TYPE = null;
        pollinateAttempts = 0;
        pollinateSuccesses = 0;
        score = 0;
    }

    public static ABInfo getABInfoInstance() {
        if (instance == null) {
            instance = new ABInfo();
        }
        return instance;
    }

    public static String getPollenType() {
        return POLLEN_TYPE;
    }

    public static void setPollenType(String type) {
        POLLEN_TYPE = type;
    }

    public static int getPollinateSuccesses() {
        return pollinateSuccesses;
    }

    public static int getPollinateAttempts() {
        return pollinateAttempts;
    }

    public static int getScore() {
        return score;
    }

    public static void incrementSuccess() {
        pollinateSuccesses += 1;
    }

    public static void incrementAttempts() {
        pollinateAttempts += 1;
    }

    public static void changeScore(int change) {
        score += change;
    }
}
