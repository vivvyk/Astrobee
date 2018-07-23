package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;
import gov.nasa.arc.astrobee.types.Point;
import gov.nasa.arc.astrobee.types.Quaternion;

import java.lang.Math;

public class SPoint {
    private double _x;
    private double _y;
    private double _z;
    private double[] my_coords;

    public SPoint (double x, double y, double z){
        _x = x;
        _y = y;
        _z = z;
        this.my_coords = new double[] {_x, _y, _z};
    }

    public double get_x(){ return _x; }

    public double get_y(){ return _y; }

    public double get_z(){ return _z; }

    public void set_x(double _x) {
        this._x = _x;
    }

    public void set_y(double _y) {
        this._y = _y;
    }

    public void set_z(double _z) {
        this._z = _z;
    }

    public double[] getMy_coords(){ return my_coords; }

    public String toString(){
        return  "[" + _x +","+_y+","+_z+"]";

    }

    public double dist(SPoint other){
        /*
        *   returns the distance of this point from another point
         */
        double dist = Math.sqrt(Math.pow((_x - other.get_x()), 2.0) + Math.pow((_y - other.get_y()), 2.0) + Math.pow((_z - other.get_z()), 2.0));
        return dist;
    }

    public double distSquared(SPoint other){
        /*
        *   more efficient than dist, use this for calculations
         */
        return Math.pow((_x - other.get_x()), 2.0) + Math.pow((_y - other.get_y()), 2.0) + Math.pow((_z - other.get_z()), 2.0);
    }

    public static double dot(SPoint p1, SPoint p2){
        double product = 0.0;
        product += p1.get_x() * p2.get_x();
        product += p1.get_y() * p2.get_y();
        product += p1.get_z() * p2.get_z();
        return product;
    }


    public static double magnitude(SPoint vec){
        double mag = 0.0;
        mag += Math.pow(vec.get_x(), 2.0);
        mag += Math.pow(vec.get_y(), 2.0);
        mag += Math.pow(vec.get_z(), 2.0);
        mag = Math.sqrt(mag);
        return mag;
    }

    public static SPoint quat_rpy(Quaternion q){

        // roll (x-axis rotation)
        double sinr = 2.0 * (q.getW() * q.getX() + q.getY() * q.getZ());
        double cosr = 1.0 - 2.0 * (q.getX() * q.getX() + q.getY() * q.getY());
        double roll = Math.atan2(sinr, cosr);

        // pitch (y-axis rotation)
        double sinp = 2.0 * (q.getW() * q.getY() - q.getZ() * q.getX());
        double pitch = 0.0;
        if (Math.abs(sinp) >= 1) {
            pitch = Math.copySign(Math.PI/2, sinp); // use 90 degrees if out of range
        }else {
            pitch = Math.asin(sinp);
        }

        // yaw (z-axis rotation)
        double siny = 2.0 * (q.getW() * q.getZ() + q.getX() * q.getY());
        double cosy = 1.0 - 2.0 * (q.getY() * q.getY() + q.getZ() * q.getZ());
        double yaw = Math.atan2(siny, cosy);

        SPoint rpy = new SPoint(roll, pitch, yaw);

        return rpy;


    }

    public static SPoint toSPoint(Point astrobee_point) {
        // converts the Astrobee Point to an SPoint object
        double an_x = astrobee_point.getX();
        double an_y = astrobee_point.getY();
        double an_z = astrobee_point.getZ();
        SPoint a_Point = new SPoint(an_x, an_y, an_z);
        return a_Point;
    }

    public boolean dist_greater_than(SPoint other, double dist){
        /*
        *   Provides a quick way to check if the distance between two points is
        *   greater than a certain specified limit.
         */
        double dist_squared = Math.pow(dist, 2.0);
        // we square the distance to do an easy comparison
        double compare_to = this.distSquared(other);
        if (compare_to > dist_squared){ return true; }
        return false;

    }
    /*
    public static void main(String[] args){
        SPoint my_point = new SPoint(0.0, 0.0, 0.0);
        SPoint other_point = new SPoint(2.0, 2.0, 2.0);
        System.out.println(other_point);
        System.out.println(my_point.dist(other_point));
        System.out.println(other_point.dist(my_point));
        System.out.println(my_point.distSquared(other_point));
        System.out.println(Math.sqrt(12.0));
        System.out.println(my_point.dist_greater_than(other_point,Math.sqrt(12.0)));
    }
    */

}
