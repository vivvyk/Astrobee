package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class GameManager {
    public int plant_number;
    public double cone_height;
    public Plants plants;
    public int ring_radius;
    public float center[] = new float[3];
    public double inclination;
    public double circ_s;


    public GameManager(){
        this.plant_number = 4;
        this.cone_height = 1.5;
        this.ring_radius = 1;

        this.center[0] = 2.0f;
        this.center[1] = 0.0f;
        this.center[2] = 4.8f;

        this.inclination = 0;

        this.plants = new Plants(this.plant_number, this.cone_height);
        this.circ_s = this.plants.init_plants(this.plants, this.plant_number, this.center, this.ring_radius, this.inclination);
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