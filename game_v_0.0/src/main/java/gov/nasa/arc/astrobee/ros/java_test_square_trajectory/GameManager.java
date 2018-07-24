package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class GameManager {
    public int plant_number;
    public double cone_height;
    public Plants plants;
    public int ring_radius;
    public SPoint center;
    public SVector normal;
    public double angular_velocity;


    public GameManager(){
        this.plant_number = 4;
        this.cone_height = 1.5;
        this.ring_radius = 1;

        this.center = new SPoint(2.0, 0.0, 4.8);
        this.normal = new SVector(0, 0, 1);

        this.angular_velocity = Math.PI/2;

        this.plants = new Plants(this.plant_number, this.cone_height, this.center, this.ring_radius, this.normal, this.angular_velocity);
    }

    public static void main(String args[]) {

        Thread trajectory = new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] args = {};
                SampleTrajectory.main(args);
            }
        });
        trajectory.start();
    }
}