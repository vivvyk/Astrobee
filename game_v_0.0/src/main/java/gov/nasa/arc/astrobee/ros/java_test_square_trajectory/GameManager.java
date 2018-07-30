package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class GameManager {
    public int plant_number;
    public double cone_height;
    public double ring_radius;
    public double angular_velocity;

    public Plants plants1;
    public KeepOutZoneRing ring1;
    public SPoint center1;
    public SVector normal1;
    public static SPoint init1;

    public Plants plants2;
    public KeepOutZoneRing ring2;
    public SPoint center2;
    public SVector normal2;
    public static SPoint init2;

    public static Timer ctime;

    public int score;



    public GameManager(){

        this.score = 0;

        ctime = new Timer(0);

        this.plant_number = 4;
        this.cone_height = 1.5;
        this.ring_radius = 0.6;
        this.angular_velocity = Math.PI;

        this.center1 = new SPoint(3.0, 0.5, 4.9);
        this.normal1 = new SVector(0, -1, 0);
        this.ring1 = new KeepOutZoneRing(this.center1, this.ring_radius, this.ring_radius+0.2, this.normal1);
        this.plants1 = new Plants(this.plant_number, this.cone_height, this.center1, this.ring_radius, this.normal1, this.angular_velocity);
        init1 = this.plants1.set_plant();

        this.center2 = new SPoint(1.0, -0.5, 4.9);
        this.normal2 = new SVector(0, 1, 0);
        this.ring2 = new KeepOutZoneRing(this.center1, this.ring_radius, this.ring_radius+0.2, this.normal1);
        this.plants2 = new Plants(this.plant_number, this.cone_height, this.center1, this.ring_radius, this.normal1, this.angular_velocity);
        init2 = this.plants2.set_plant();
    }

    public static void main(String args[]) {

        final ApiCommandImplementation api = ApiCommandImplementation.getInstance();

        Thread trajectory = new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] args = {};
                SampleTrajectory.main(args);
            }
        });
        trajectory.start();

        Thread time = new Thread(new Runnable() {
            @Override
            public void run() {
                int timerange = 180;
                for (int i = 0; i < timerange; i++) {
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