package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import org.ros.message.Time;
import org.ros.node.*;
import java.net.URI;

import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class Timer {
    /*
        May consider setting game time AFTER 15/16 seconds the first time student code will be run due to the fact that
        the Astrobee takes about 15 seconds simulation time to begin to move.
     */
    private static final URI ROS_MASTER_URI = URI.create("http://localhost:11311");
    private static TimerNode timerNode = null;

    public static void exec(NodeMainExecutor nodeMainExecutor) {
        timerNode = new TimerNode();

        // Setting configurations for ROS Node
        NodeConfiguration nodeConfiguration = NodeConfiguration.newPublic("10.0.3.15");
        nodeConfiguration.setMasterUri(ROS_MASTER_URI);

        nodeMainExecutor.execute(timerNode, nodeConfiguration);
    }

    public static int getTime(){
        return timerNode.time.secs;
    }

    public static void main(String... args){
        Timer t = new Timer();
        exec(DefaultNodeMainExecutor.newDefault());
        for(int i =0; i<10; i++) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //System.out.println((((int)getTime().secs)));
        }

    }


}
