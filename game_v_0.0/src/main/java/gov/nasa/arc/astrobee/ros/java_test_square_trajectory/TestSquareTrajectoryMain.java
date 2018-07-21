package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;
import gov.nasa.arc.astrobee.Result;


public class TestSquareTrajectoryMain {

    // Fixed trajectory points
    private static final Point HOME_POSITION = new Point(2.0, 0.0, 4.8);
    private static final Point POINT_1 = new Point(1.5, 1, 4.8);
    private static final Point POINT_2 = new Point(5, 3, 4.8);
    private static final Point POINT_3 = new Point(2.5, -0.6, 4.8);
    private static final Point POINT_4 = new Point(1.5, -0.6, 4.8);
    // new point to test changes
    private static final Point POINT_5 = new Point(3, -0.6, 4.8);


    // Fixed trajectory orientations (POINT_1 and 2 use default orientation)
    private static final Quaternion DEFAULT_ORIENT = new Quaternion();
    private static final Quaternion ORIENT_3 = new Quaternion(0, 0, -0.707f, 0.707f);
    private static final Quaternion ORIENT_4 = new Quaternion(0, 0, 1, 0);

    // Defining trajectory. Fixed positions and orientations. An orientation for each position.
    private static Point[] arrayPoint = {POINT_1, POINT_2, POINT_3, POINT_4, HOME_POSITION};
    private static Quaternion[] arrayOrient = {DEFAULT_ORIENT, ORIENT_3, ORIENT_4, DEFAULT_ORIENT, DEFAULT_ORIENT};


    public static void main(String[] args) {
        // Because log4j doesn't do the needful
        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler());

        // Get a unique instance of the Astrobee API in order to command the robot.
        ApiCommandImplementation api = ApiCommandImplementation.getInstance();


        // At the end of execution, this variable will contain the last command result. It may be useful
        Result result = null;

        //keep track of time to test timing of game
       // long start_time = System.currentTimeMillis();
       // System.out.println(start_time);
       // Timer startUpTest = new Timer(start_time);
        

        // Loop the points and orientation previously defined.
        for (int i = 0; i < arrayPoint.length; i++) {
            System.out.println("attempting to move to:: " + SPoint.toSPoint(arrayPoint[i]));
            System.out.println("another loop");
            result = api.moveTo(arrayPoint[i], arrayOrient[i]);
            if (!result.hasSucceeded()) {
                System.out.println("QUITTING");
                // If any movement fails we cancel all execution.
                break;
            }
        }

        /* Will print the elapsed time it took for the calls to execute above */
       // System.out.println("This is the amount of time it took::" + startUpTest.timeElapsed(System.currentTimeMillis()));

        // Stop the API
        api.shutdownFactory();
    }
}
