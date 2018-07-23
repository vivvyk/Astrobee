package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class Timer {
    /*
        May consider setting game time AFTER 15/16 seconds the first time student code will be run due to the fact that
        the Astrobee takes about 15 seconds simulation time to begin to move.
     */
    private long _start;

    // Constructor for a Timer Object to keep track of time
    public Timer(long start_time){
        _start = start_time;
    }

    // Used to return the given timer's start_time
    public long getStart(){
        return _start;
    }

    // Used to return the amount fo time that has passed for Timer Object
    public long timeElapsed(long end_time){
        long thousand = 1000;
        return (end_time - _start) / thousand;
    }

}
