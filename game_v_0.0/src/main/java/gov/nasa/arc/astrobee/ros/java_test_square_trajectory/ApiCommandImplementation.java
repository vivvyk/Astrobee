
/* Copyright (c) 2017, United States Government, as represented by the
 * Administrator of the National Aeronautics and Space Administration.
 *
 * All rights reserved.
 *
 * The Astrobee platform is licensed under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import gov.nasa.arc.astrobee.AstrobeeException;
import gov.nasa.arc.astrobee.Kinematics;
import gov.nasa.arc.astrobee.PendingResult;
import gov.nasa.arc.astrobee.Result;
import gov.nasa.arc.astrobee.Robot;
import gov.nasa.arc.astrobee.RobotFactory;

import gov.nasa.arc.astrobee.ros.DefaultRobotFactory;
import gov.nasa.arc.astrobee.ros.RobotConfiguration;
import gov.nasa.arc.astrobee.types.FlashlightLocation;
import gov.nasa.arc.astrobee.types.PlannerType;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A simple API implementation tool that provides an easier way to work with the Astrobee API
 * This is a minimal version
 */

public class ApiCommandImplementation {
    private static final Log logger = LogFactory.getLog(ApiCommandImplementation.class);

    // The IP for the ROS Master
    private static final URI ROS_MASTER_URI = URI.create("http://10.42.0.1:11311");
    // IP for the local computer executing this code
    private static final String JAVA_ROS_HOSTNAME = "10.42.0.1";
    // Name of the node
    private static final String NODE_NAME = "astrobee_java_app";

    private static final int SUCCESS = -1;
    private static final int VALIDATE_ERROR = 0;
    private static final int MOVE_TO_ERROR = 1;

    // The instance to access this class
    private static ApiCommandImplementation instance = null;

    // Configuration that will keep data to connect with ROS master
    private RobotConfiguration robotConfiguration = new RobotConfiguration();

    // Instance that will create a robot with the given configuration
    private RobotFactory factory;

    // The robot itself
    private Robot robot;

    private PlannerType plannerType = null;
    private FlashlightLocation flashlight;

    private GameManager game = new GameManager();


    //The Keep Out Zone(s)
    /* Astrobee should start at 2, 0 , 4.8 */
    private final KeepOutZone test_sphere_1 = new KeepOutZone(new SPoint(4, 0, 4.8), 1);
    private final KeepOutZoneRing test_ring_1 = new KeepOutZoneRing(new SPoint(1,0,4.8), 2, 0.5, new SVector(1,0,0));
    private final KeepOutZone[] keepOutZones= {test_sphere_1, test_ring_1};

    /**
     * Private constructor that prevents other objects from creating instances of this class.
     * Instances of this class must be provided by a static function (Singleton)
     */
    private ApiCommandImplementation() {

        /* Alternative custom configuration
         *
         * configureRobot();
         * factory = new DefaultRobotFactory(robotConfiguration);
         *
         */

        factory = new DefaultRobotFactory();

        try {
            // Get the robot
            robot = factory.getRobot();

            Kinematics k = getTrustedRobotKinematics();

            logger.info("Position: " + k.getPosition());

            // Set default planner
            setPlanner(PlannerType.TRAPEZOIDAL);

        } catch (AstrobeeException e) {
            logger.info("Error with Astrobee");
        } catch (InterruptedException e) {
            logger.info("Connection Interrupted");
        }

        //game = new GameManager(1, 1.5, new SPoint(1, 0, 4.5));
    }

    /**
     * Static method that provides a unique instance of this class
     *
     * @return A unique instance of this class ready to use
     */
    public static ApiCommandImplementation getInstance() {
        if (instance == null) {
            instance = new ApiCommandImplementation();
        }
        return instance;
    }

    /**
     * This method sets a default configuration for the robot
     */
    private void configureRobot() {
        // Populating robot configuration
        robotConfiguration.setMasterUri(ROS_MASTER_URI);
        robotConfiguration.setHostname(JAVA_ROS_HOSTNAME);
        robotConfiguration.setNodeName(NODE_NAME);
    }

    /**
     * This method shutdown the robot factory in order to allow java to close correctly.
     */
    public void shutdownFactory() {
        factory.shutdown();
    }

    public Result getCommandResult(PendingResult pending, boolean printRobotPosition) {

        Result result = null;

        try {
            Kinematics k;
            //SPoint rollpitchyaw = new SPoint(0, 0, 0);
            //SPoint plant = new SPoint(1, 0, 4.8);
            //Plants xyz = new Plants(1, 1.5, plant);


            // Waiting until command is done.
            while (!pending.isFinished()) {
                if (printRobotPosition) {
                    // Meanwhile, let's get the positioning along the trajectory
                    /*

                    k = robot.getCurrentKinematics();
                    rollpitchyaw = rollpitchyaw.quat_rpy(k.getOrientation());

                    System.out.println(k.getPosition().toString());
                    System.out.println(xyz.rpy_cone(rollpitchyaw).toString());

                    SPoint plantvec = xyz.plant_vec(plant.toSPoint(k.getPosition()), plant);
                    System.out.println(plantvec.toString());

                    System.out.print(xyz.score(plantvec, xyz.rpy_cone(rollpitchyaw)));
                    System.out.println("-----");
                    */


                    //logger.info("Current Position: " + k.getPosition().toString());
                    //logger.info("Current Orientation" + k.getOrientation().toString());
                }

                // Wait a little bit before retry
                pending.getResult(1000, TimeUnit.MILLISECONDS);
            }

            // Getting final result
            result = pending.getResult();

            // Print result in the log.
            printLogCommandResult(result);

        } catch (AstrobeeException e) {
            logger.info("Error with Astrobee");
        } catch (InterruptedException e) {
            logger.info("Connection Interrupted");
        } catch (TimeoutException e) {
            logger.info("Timeout connection");
        } finally {
            // Return command execution result.
            return result;
        }
    }

    /**
     * Get trusted data related to the motion, positioning and orientation for Astrobee
     *
     * @return
     */
    public Kinematics getTrustedRobotKinematics() {
        logger.info("Waiting for robot to acquire position");

        // Variable that will keep all data related to positioning and movement.
        Kinematics k;

        // Waiting until we get a trusted kinematics
        while (true) {
            // Get kinematics
            k = robot.getCurrentKinematics();

            // Is it good?
            if (k.getConfidence() == Kinematics.Confidence.GOOD)
                // Don't wait anymore, move on.
                break;

            // It's not good, wait a little bit and try again
            try {
                Thread.sleep(250);
            } catch (InterruptedException e) {
                logger.info("It was not possible to get a trusted kinematics. Sorry");
                return null;
            }
        }

        return k;
    }

    /**
     * It moves Astrobee to the given point and rotate it to the given orientation.
     *
     * @param goalPoint   Absolute cardinal point (xyz)
     * @param orientation An instance of the Quaternion class.
     *                    You may want to use INITIAL_POSITION as an example.
     * @return A Result instance carrying data related to the execution.
     * Returns null if the command was NOT execute as a result of an error
     */
    public Result moveTo(Point goalPoint, Quaternion orientation) {

        //TODO:: restrict moveTo() for only validWaypoint()
        //TODO:: aka add an if statement with vlaid waypoint checker

        // First, stop all motion
        Result result = stopAllMotion();
        if (result.hasSucceeded()) {
            // We stopped, do your stuff now

            // Setting a simple movement command using the end point and the end orientation.
            PendingResult pending = robot.simpleMove6DOF(goalPoint, orientation);

            // Get the command execution result and send it back to the requester.
            result = getCommandResult(pending, true);
        }
        return result;
    }

    /**
     * Checks if the Astrobee destination will work given the KOZs
     * @param goalPoint (xyz) point you want to test if valid
     *
     * @return A bool, false if AB will collide, true if not
     */
    public boolean validWaypoint(Point goalPoint){
        SPoint cur_pos = SPoint.toSPoint(getTrustedRobotKinematics().getPosition());
        SPoint goal = new SPoint(goalPoint.getX(), goalPoint.getY(), goalPoint.getZ());
        ArrayList<Map<Integer, List<Object>>> results = new ArrayList<Map<Integer, List<Object>>>();
        ArrayList<Integer> projections = new ArrayList<Integer>();
        int koz_count = keepOutZones.length;
        for (int i = 0; i < koz_count; i++) {
            // iterates through the keepOutZones to check any collisions
            results.add(keepOutZones[i].aB_path_projection(cur_pos, goal));
        }
        for (Map<Integer, List<Object>> result : results) {
            for (Map.Entry<Integer, List<Object>> entry : result.entrySet()) {
                projections.add((Integer) entry.getValue().get(0));
            }
        }
        if (projections.contains(0)) {
            System.out.println("returns were" + projections);
            System.out.println("movement failed");
            return false;
        } else {
            System.out.println("returns were" + projections);
            return true;
        }
    }

    /**
     * It moves Astrobee to the given point and rotate it to the given orientation.
     * IFF valid wayPoint returns true
     *
     * @param goalPoint   Absolute cardinal point (xyz)
     * @param orientation An instance of the Quaternion class.
     *                    You may want to use INITIAL_POSITION as an example.
     * @return An int corresponding to the result of the action.
     */
    public int moveToValid(Point goalPoint, Quaternion orientation) {
        if (!validWaypoint(goalPoint)) {
            return VALIDATE_ERROR;
        } else {
            Result movement_result = moveTo(goalPoint, orientation);
            if (!movement_result.hasSucceeded()) {
                return MOVE_TO_ERROR;
            } else {
                return SUCCESS;
            }
        }
    }

    //  THIS COMMAND MAY NOT BE AVAILABLE TO STUDENTS, MAYBE USED TO START GAME
    // FAILED, prepare() command not available in "operating state"
    public Result startUp(){
        // First, make sure not moving
        Result result = stopAllMotion();
        logger.info("Hi, just started startUp() command");

        if (result.hasSucceeded()) {
            // Setting the mobility prepare command to get the robot ready to move
            PendingResult pending = robot.prepare();

            // Get the result of command execution to send back
            result = getCommandResult(pending, true);
        }
        logger.info("Hi, just finished startUp() command");
        return result;
    }

    public Result stopAllMotion() {
        PendingResult pendingResult = robot.stopAllMotion();
        return getCommandResult(pendingResult, false);
    }

    public Result pollinate() throws InterruptedException {

        Kinematics k;
        k = robot.getCurrentKinematics();
        robot.setFlashlightBrightness(FlashlightLocation.FRONT, 1);

        SPoint pos = SPoint.toSPoint(k.getPosition());
        SPoint approxpos = new SPoint(Math.round(pos.get_x() * 10.0)/10.0,Math.round(pos.get_y() * 10.0)/10.0,0);
        SPoint rpy = SPoint.quat_rpy(k.getOrientation());

        if(approxpos.get_x() == 3.0 && approxpos.get_y() == 0.5) {
            SPoint lead = game.plants1.plant_vec(game.init1, SPoint.toSPoint(k.getPosition()));
            SPoint[] spawned = game.plants1.spawn_plants(lead, (int)game.ctime.getTime());


            for(int i = 0; i < game.plant_number; i++) {
                boolean score = game.plants1.score(spawned[i], game.plants1.rpy_cone(rpy));
                if(score){
                    game.score = Plants.decide_score(i, game.score);
                }
            }
            System.out.print("CURRENT SCORE: ");
            System.out.println(game.score);

        }else if(approxpos.get_x() == 1 && approxpos.get_y() == -0.5){
            SPoint lead = game.plants2.plant_vec(game.init2, SPoint.toSPoint(k.getPosition()));
            SPoint[] spawned = game.plants2.spawn_plants(lead, (int) game.ctime.getTime());


            for(int i = 0; i < game.plant_number; i++) {
                boolean score = game.plants2.score(spawned[i], game.plants2.rpy_cone(rpy));
                if(score){
                    game.score = Plants.decide_score(i, game.score);
                }
            }
            System.out.print("CURRENT SCORE: ");
            System.out.println(game.score);
        }else{
            System.out.println("NOT IN RING");
            game.score -= 50;
        }

        Thread.sleep(200);

        PendingResult pending = robot.setFlashlightBrightness(FlashlightLocation.FRONT, 0);

        Result result = getCommandResult(pending, false);
        return result;
    }

    /**
     * An optional method used to print command execution results on the Android log
     * @param result
     */
    private void printLogCommandResult(Result result) {
        logger.info("Command status: " + result.getStatus().toString());

        // In case command fails
        if (!result.hasSucceeded()) {
            logger.info("Command message: " + result.getMessage());
        }

        logger.info("Done");
    }

    /**
     * Method to get the robot from this API Implementation.
     * @return
     */
    public Robot getRobot() {
        return robot;
    }

    public boolean setPlanner(PlannerType plannerType) {
        PendingResult pendingPlanner = robot.setPlanner(plannerType);
        Result result = getCommandResult(pendingPlanner, false);
        if (result.hasSucceeded()) {
            this.plannerType = plannerType;
            logger.info("Planner set to " + plannerType);
        }

        return result.hasSucceeded();
    }

}
