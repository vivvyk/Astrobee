package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class GameManager {
    // Start the game wih two rings::
    private KeepOutZoneRing ring1;
    private KeepOutZoneRing ring2;
    public KeepOutZoneRing[] rings = {ring1, ring2};

    public int plant_number;
    public double cone_height;
    public Plants plants;
    public KeepOutZoneRing ring;
    public int ring_radius;
    public SPoint center;
    public SVector normal;
    public double angular_velocity;
    public static Timer ctime;
    public static SPoint init;


    public GameManager(){

        ctime = new Timer(0);

        this.plant_number = 4;
        this.cone_height = 1.5;
        this.ring_radius = 1;

        this.center = new SPoint(2.0, 0.0, 4.8);
        this.normal = new SVector(1, 1, 0);

        int degrees = 18;
        this.angular_velocity = degrees *  Math.PI/180;

        this.ring = new KeepOutZoneRing(this.center, this.ring_radius, 0.5, this.normal);
        this.plants = new Plants(this.plant_number, this.cone_height, this.center, this.ring_radius, this.normal, this.angular_velocity);
        init = this.plants.set_plant();
    }

    public static void main(String args[]) {

        final ApiCommandImplementation api = ApiCommandImplementation.getInstance();

        Thread trajectory = new Thread(new Runnable() {
            @Override
            public void run() {
                //final String[] args = {};
                //SampleTrajectory.main(args);
            }
        });
        trajectory.start();

        Thread time = new Thread(new Runnable() {
            @Override
            public void run() {
                int timerange = 180;
                for (int i = 0; i < timerange; i++) {
                    try {
                        api.pollinate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ctime.update();
                }
            }
        });
        time.start();


    }
}