package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import gov.nasa.arc.astrobee.types.Point;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.math.BigDecimal;

public class SPoint {
    private double _x;
    private double _y;
    private double _z;
    private double[] my_coords;
    private static double AB_collider_step = 0.52;

    public SPoint(double x, double y, double z) {
        _x = x;
        _y = y;
        _z = z;
        this.my_coords = new double[]{_x, _y, _z};
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public double get_x() {
        return _x;
    }

    public double get_y() {
        return _y;
    }

    public double get_z() {
        return _z;
    }

    public double[] getMy_coords() {
        return my_coords;
    }

    public String toString() {
        return "[" + round(_x, 3) + "," + round(_y, 3) + "," + round(_z, 3) + "]";

    }

    public SPoint shift(SVector shifter){
        double new_x = _x + shifter.x;
        double new_y = _y + shifter.y;
        double new_z = _z + shifter.z;
        return new SPoint(new_x, new_y, new_z);
    }

    public static SPoint toSPoint(Point astrobee_point) {
        // converts the Astrobee Point to an SPoint object
        double an_x = astrobee_point.getX();
        double an_y = astrobee_point.getY();
        double an_z = astrobee_point.getZ();
        SPoint a_Point = new SPoint(an_x, an_y, an_z);
        return a_Point;
    }

    public double dist(SPoint other) {
        /*
         *   returns the distance of this point from another point
         */
        double dist = Math.sqrt(Math.pow((_x - other.get_x()), 2.0) + Math.pow((_y - other.get_y()), 2.0) + Math.pow((_z - other.get_z()), 2.0));
        return dist;
    }

    public double distSquared(SPoint other) {
        /*
         *   more efficient than dist, use this for calculations
         */
        return Math.pow((_x - other.get_x()), 2.0) + Math.pow((_y - other.get_y()), 2.0) + Math.pow((_z - other.get_z()), 2.0);
    }

    public boolean dist_greater_than(SPoint other, double limit) {
        /*
         *   Provides a quick way to check if the distance between two points is
         *   greater than a certain specified limit.
         */
        double limit_squared = Math.pow(limit, 2.0);
        // we square the distance to do an easy comparison
        double compare_to = this.distSquared(other);
        if (compare_to > limit_squared) {
            return true;
        }
        return false;
    }

    public static ArrayList<SPoint> splitPath(SPoint start, SPoint end) {
        /*
         *   Returns an ArrayList object of SPoint "waypoints", including the
         *   start and end
         */
        ArrayList<SPoint> sPoints = new ArrayList<SPoint>();
        int num_steps = (int) Math.ceil(start.dist(end) / AB_collider_step);
        double start_x = start.get_x();
        double start_y = start.get_y();
        double start_z = start.get_z();
        double x_step = (end.get_x() - start.get_x()) / num_steps;
        double y_step = (end.get_y() - start.get_y()) / num_steps;
        double z_step = (end.get_z() - start.get_z()) / num_steps;
        for (int i = 0; i < num_steps; i++) {
            double s_x = start_x + x_step * i;
            double s_y = start_y + y_step * i;
            double s_z = start_z + z_step * i;
            sPoints.add(new SPoint(s_x, s_y, s_z));
        }
        sPoints.add(end);
        return sPoints;
    }

    public ArrayList<SPoint> splitPath (SPoint end){
        /*
         *   Returns an ArrayList object of SPoint "waypoints", from the given
         *   SPoint to the end point
         */
        ArrayList<SPoint> sPoints = new ArrayList<SPoint>();
        int num_steps = (int) Math.ceil(this.dist(end) / AB_collider_step);
        double start_x = this.get_x();
        double start_y = this.get_y();
        double start_z = this.get_z();
        double x_step = (end.get_x() - this.get_x()) / num_steps;
        double y_step = (end.get_y() - this.get_y()) / num_steps;
        double z_step = (end.get_z() - this.get_z()) / num_steps;
        for (int i = 0; i < num_steps; i++) {
            double s_x = start_x + x_step * i;
            double s_y = start_y + y_step * i;
            double s_z = start_z + z_step * i;
            sPoints.add(new SPoint(s_x, s_y, s_z));
        }
        sPoints.add(end);
        return sPoints;
    }

/*
    public static void main(String[]args){
        SPoint my_point = new SPoint(-1, -4, 5);
        SPoint other_point = new SPoint(2.0, 5, -6);
        System.out.println(other_point);
        System.out.println(my_point.dist(other_point));
        System.out.println(other_point.dist(my_point));
        System.out.println(my_point.distSquared(other_point));
        System.out.println(Math.sqrt(12.0));
        System.out.println(my_point.dist_greater_than(other_point, Math.sqrt(12.0)));
        System.out.println(SPoint.splitPath(my_point, other_point));
        System.out.println(my_point.splitPath(other_point));
    }
*/
}
