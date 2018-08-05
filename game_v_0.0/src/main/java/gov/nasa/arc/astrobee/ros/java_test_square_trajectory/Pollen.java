package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Set;

public class Pollen {

    private final static String[] TYPES = {"FIREWEED", "HONEYSUCKLE", "ARUGULA", "PANDORA"};
    /*               Like blood types!!    AB, B, A, 0*/
    private final static int[] POLLINATE_SCORES = {50, 100, 100, 200};
    private final static int[] COLLECTION_SCORES = {200, 75, 75, 50};
    private final static int MISS_PENALTY = -100;
    private final static int MISPOLLINATE_PENALTY = -50;

    public static boolean prevCanGiveTo(String donor, String receiver) {
        if (donor.equals(TYPES[3])) {
            return true;
        } else if (receiver.equals(TYPES[0])) {
            return true;
        } else if (donor.equals(receiver)) {
            return true;
        } else return false;
    }

    public static int getPollinateScore(String pollen) {
        //Assumes pollen is a valid string in TYPES
        int i = Arrays.asList(TYPES).indexOf(pollen);
        return POLLINATE_SCORES[i];
    }

    public static int getCollectionScore(String pollen) {
        // Assumes pollen is a valid String in TYPES
        if (StringUtils.isEmpty(pollen))
            return 0;
        int i = Arrays.asList(TYPES).indexOf(pollen);
        return COLLECTION_SCORES[i];
    }

    /**
     *
     * @param number - number of plants to populate the rign with
     * @return array with order of plant TYPES
     */
    public static String[] populate(int number) {
        String[] plants = new String[number];
        for (int i = 0; i < number; i++) {
            plants[i] = TYPES[i % TYPES.length];
        }
        return plants;
    }

    public static int getMissPenalty() {
        return MISS_PENALTY;
    }

    public static int getMispollinatePenalty() {
        return MISPOLLINATE_PENALTY;
    }
}
