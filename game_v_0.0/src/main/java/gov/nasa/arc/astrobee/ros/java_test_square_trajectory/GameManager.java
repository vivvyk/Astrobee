package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class GameManager {

    public static Timer ctime;
    public int score;

    public GameManager(){

        this.score = 0;

        ctime = new Timer(0);
        // not sure if needs to be removed yet
    }

    public int getScore() {
        return score;
    }

    public static void main(String args[]) {

        final ApiCommandImplementation api = ApiCommandImplementation.getInstance();

        Thread trajectory = new Thread(new Runnable() {
            @Override
            public void run() {
                final String[] args = {};
                try {
                    TestSquareTrajectoryMain.main(args);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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