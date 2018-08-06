package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import org.omg.PortableInterceptor.INACTIVE;

import java.util.*;

public class KeepOutZone {
    /*
    *   Implements a basic sphere shaped zone that is designated as a "keep-out"
    *   zone for the Astrobee. Used to restrict movement of Astrobee when it comes
    *   to the moveTo() command. Eventually will be MODIFIED or INHERITED from to
    *   simulate the rings.
     */
    protected SPoint _center;
    protected String _shape;
    protected double _radius;


    public KeepOutZone(SPoint center, double radius){
        /*
        *   Sets the zone center, assumes the zone is a SPHERE with this specified radius.
         */
        _center = center;
        _radius = radius;
        _shape = "SPHERE";
    }

    public static double getAB_collider_radius() {
        return ABInfo.collider_radius;
    }

    public SPoint get_center() { return _center; }

    public double get_radius() { return _radius; }

    public String get_shape() { return _shape; }

    public Map<Integer, List<Object>> aB_path_projection(SPoint AB_pos, SPoint AB_goal){
        /*
        *   uses int to return info about the AB path projection::
        *   if 0, astrobee will collide on current path
        *   if 1, astrobee will NOT collide on current path
        *   note that this projection is for a SPHERE KOZ
        *
        *   TODO: change the output so it returns an ArrayList of failures/succeses (0/1)
        *
         */
        double collision_threshold = this._radius + ABInfo.collider_radius;
        ArrayList<SPoint> points = AB_pos.splitPath(AB_goal);
        ArrayList<SVector> vecs = SVector.genVecs(points, this._center);
        ArrayList<Double> lengths = SVector.lengths(vecs);
        int num_points = points.size();
        Map<Integer, List<Object>> results = new HashMap<>();
        for (int i = 0; i < num_points; i++) {
            double ab_dist_from_center = lengths.get(i);
            List<Object> info = new ArrayList<>();
            if (ab_dist_from_center >= collision_threshold) {
                info.add(new Integer(1));
            } else {
                info.add(new Integer(0));
            }
            info.add(lengths.get(i));
            info.add(points.get(i));
            info.add(vecs.get(i));
            results.put(i, info);
        }
        return results;
    }
/*
    public static void main(String[] args){
        KeepOutZone k = new KeepOutZone(new SPoint(0,0,0), 2.0);
        System.out.println(k.aB_path_projection(new SPoint(3, 0, 0), new SPoint(-1.5, -1.5, -1.5)));
    }
*/
}
