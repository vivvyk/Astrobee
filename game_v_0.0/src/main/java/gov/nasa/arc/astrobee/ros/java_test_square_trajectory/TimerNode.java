package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import org.ros.message.MessageListener;
import org.ros.message.Time;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Subscriber;
import rosgraph_msgs.Clock;


public class TimerNode extends AbstractNodeMain {

    static final String CLOCK_TOPIC = "/clock";
    public static Time time = new Time();

    Subscriber<Clock> clockSubscriber;

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("timer_zr");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
        // Creating subscribers for ROS Topics and adding message listeners
        System.out.println("node started");
        clockSubscriber = connectedNode.newSubscriber(
                CLOCK_TOPIC, Clock._TYPE);
        clockSubscriber.addMessageListener(new MessageListener<Clock>() {
            @Override
            public void onNewMessage(Clock clock) {
                //System.out.println("message recieved");
                time = clock.getClock();
                //System.out.println(time.toString());
            }
        }, 10);
    }
}
