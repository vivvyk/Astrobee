package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timer {
    /*
        May consider setting game time AFTER 15/16 seconds the first time student code will be run due to the fact that
        the Astrobee takes about 15 seconds simulation time to begin to move.
     */
    private static long time;

    // Constructor for a Timer Object to keep track of time
    public Timer(long start_time){
        time = start_time;
    }


    public static void update(){
        time += 1;
    }

    // Used to return the amount fo time that has passed for Timer Object
    public static long getTime(){
        return time;
    }

    public static void main(String... args){
        int timerange = 180;
        for (int i = 0; i < timerange; i++) {
            System.out.println(getTime());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            update();
        }
    }

}
