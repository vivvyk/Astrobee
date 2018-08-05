package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import geometry_msgs.Quaternion;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.*;
import org.ros.node.topic.Subscriber;
import geometry_msgs.PoseStamped;

import java.net.URI;

public class RingNode extends AbstractNodeMain {

    static final String RING_TOPIC_ONE = "/ring1/loc/truth/pose";
    static final String RING_TOPIC_TWO = "/ring2/loc/truth/pose";
    static final String RING_TOPIC_THREE = "/ring3";
    static final String RING_TOPIC_FOUR = "/ring4";
    static final String RING_TOPIC_FIVE = "/ring5";

    private Quaternion ring1_orientation;
    public SQuaternion ring1_orient;
    private Quaternion ring2_orientation;
    public SQuaternion ring2_orient;

    Subscriber<PoseStamped> instantPose1;
    Subscriber<PoseStamped> instantPose2;
    Subscriber<PoseStamped> instantPose3;
    Subscriber<PoseStamped> instantPose4;
    Subscriber<PoseStamped> instantPose5;


    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("ring_pose_zr");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        // Creating subscribers for ROS Topics and adding message listeners
        System.out.println("ring1 node started");
        instantPose1 = connectedNode.newSubscriber(RING_TOPIC_ONE, PoseStamped._TYPE);

        instantPose1.addMessageListener(new MessageListener<PoseStamped>() {
            @Override
            public void onNewMessage(PoseStamped pose1) {
                ring1_orientation = pose1.getPose().getOrientation();
                ring1_orient = new SQuaternion(ring1_orientation.getX(), ring1_orientation.getY(),
                        ring1_orientation.getZ(), ring1_orientation.getW());
            }
        }, 10);

        System.out.println("ring2 node started");
        instantPose2 = connectedNode.newSubscriber(RING_TOPIC_TWO, PoseStamped._TYPE);

        instantPose2.addMessageListener(new MessageListener<PoseStamped>() {
            @Override
            public void onNewMessage(PoseStamped pose2) {
                ring2_orientation = pose2.getPose().getOrientation();
                ring2_orient = new SQuaternion(ring2_orientation.getX(), ring2_orientation.getY(),
                        ring2_orientation.getZ(), ring2_orientation.getW());
            }
        }, 2);

    }
}
