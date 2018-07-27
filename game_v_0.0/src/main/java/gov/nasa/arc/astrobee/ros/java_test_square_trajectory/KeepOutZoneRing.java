package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeepOutZoneRing extends KeepOutZone {

    private double semi_circle_radius;
    private SVector normal_vec;

    public KeepOutZoneRing(SPoint center, double inner_radius, double second_radius, SVector normal){
        /*
        *   extends the keep out zone class for a ring, which will be sued to hold the "plants" in space
        *   for the game. Adds two fields, the semicircle radius of the ring, and the normal vector to the
        *   plane which the ring lies on.
        *   Note: inner_radius should ALWAYS be GREATER than AB_collider_radius
         */
        super(center, inner_radius);
        this._shape = "RING";
        semi_circle_radius = second_radius;
        if (normal.length() != 1){
            normal.normalize();
        }
        normal_vec = normal;
    }

    public double getSemi_circle_radius() { return semi_circle_radius; }

    public SVector getNormal_vec(){ return normal_vec; }

    protected void setSemi_circle_radius(double second_radius){ this.semi_circle_radius = second_radius; }

    protected void setNormal_vec(SVector normal){ this.normal_vec = normal; }

    protected double[] angle_limits(){
        //returns an array with [angle low, angle high]
        //may not actually be used
        double[] lims = new double[2];
        double theta = Math.toDegrees(Math.asin(this.semi_circle_radius / this.getHypotenuse(_radius, semi_circle_radius)));
        lims[0] = 90 - theta;
        lims[1] = 90 + theta;
        return lims;
    }

    public double[] angle_limts_spec(double leg1, double leg2){
        // returns an array with [angle low, angle high] for the angle across leg 1
        double[] lims = new double[2];
        double theta = Math.toDegrees(Math.asin(leg1/getHypotenuse(leg1, leg2)));
        lims[0] = theta;
        lims[1] = 180 - theta;
        return lims;
    }

    protected double[] angle_limits_buffer(double AB_radius){
        //returns an array with [angle low, angle high] that includes AB radius
        double[] lims = new double[2];
        double hyp = this.getHypotenuse(_radius, semi_circle_radius+AB_radius);
        double theta = Math.toDegrees(Math.asin((this.semi_circle_radius + AB_radius) / hyp));
        lims[0] = 90 - theta;
        lims[1] = 90 + theta;
        return lims;
    }

    protected double[] length_limits(double angle){
        // takes in angle and returns the lower and upper length limit
        // of the astrobee-to-ring vector to avoid collision
        /*
        *   TO DO: Write new function that implements the plane method
        *   and direct semicircle cross section comparison
         */
        double[] lims = new double[2];
        double theta_deg = Math.abs(angle - 90.0);
        if (theta_deg <= 1 ){
            lims[0] = _radius - AB_collider_radius;
            lims[1] = _radius + semi_circle_radius + AB_collider_radius;
        } else {
            double theta = Math.toRadians(theta_deg);
            double theta_comp_deg = 90 - theta_deg;
            double theta_comp = Math.toRadians(theta_comp_deg);
            double hyp = _radius / Math.sin(theta_comp);
            // gets the hyp of the inner triangle, then subtracts the collider to get estimate of lower limit
            lims[0] = hyp - AB_collider_radius;

            double opp_radius_deg = Math.toDegrees(Math.asin(_radius*Math.sin(theta) / semi_circle_radius));
            // use law of sines to find the angle opposite the radius
            double obtuse_deg = 180 -  theta_deg - opp_radius_deg;
            double obtuse = Math.toRadians(obtuse_deg);
            double long_leg = (Math.sin(obtuse) * semi_circle_radius) / Math.sin(theta);
            //System.out.println("long_leg_len:: "+long_leg);
            lims[1] = long_leg + AB_collider_radius;
        }
        return lims;
    }


    protected double getHypotenuse(double leg1, double leg2){
        double hyp = Math.sqrt(Math.pow(leg1, 2) + Math.pow(leg2, 2));
        return hyp;
    }

    public Map<Integer, List<Object>> aB_path_projection(SPoint AB_pos, SPoint AB_goal){
        /*
         *   uses int to return info about the AB path projection::
         *   if 0, astrobee will collide on current path
         *   if 1, astrobee will NOT collide on current path
         *   note that this projection is for a SPHERE KOZ
         *   //TODO:: Change output to a map from integer to array of points, vecs, lengths, and angles
         */
        double collision_threshold = this._radius + this.semi_circle_radius + AB_collider_radius;
        // By default, the collision threshold will be set to AB collider + longest length vec inside ring
        double proximity_threshold = this._radius - AB_collider_radius;
        // If the Astrobee is within the proximity threshold, the AB should be safe from collision (since it so close to the center of the ring)
        ArrayList<SPoint> points = AB_pos.splitPath(AB_goal);
        ArrayList<SVector> vecs = SVector.genVecs(points, this._center);
        ArrayList<Double> lengths = SVector.lengths(vecs);
        ArrayList<Double> angles = SVector.getAnglesDeg(vecs, this.normal_vec);
        int num_points = points.size();
        double[] angle_lims = this.angle_limits_buffer(AB_collider_radius);
        Map<Integer, List<Object>> results = new HashMap<>();
        System.out.println("The number of waypoints is:: " + num_points);
        for (int i = 0; i < num_points; i++) {
            List<Object> info = new ArrayList<>();
            double ab_dist_from_ring_center = lengths.get(i);
            if (ab_dist_from_ring_center <= proximity_threshold) {
                System.out.println("#################");
                System.out.println("Safe by proximity" + i);
                info.add(1);
                // If Astrobee is close enough to center, auto not colliding so we assign a 1
            } else {
                double angle = angles.get(i);
                // accessing the angle the astrobee vector makes with the normal vector
                if (angle <= angle_lims[0] || angle >= angle_lims[1]) {
                    // If astrobee is outside angle of interest range, point is safe
                    System.out.println("#################");
                    System.out.println("Safe by angle lims (1)" + i);
                    info.add(1);
                } else if (ab_dist_from_ring_center > collision_threshold) {
                    // If astrobee is far enough away, no need to worry about collision checking
                    System.out.println("#################");
                    System.out.println("Safe by outside collision threshold " + i);
                    info.add(1);
                } else {
                    SPlane ring_plane = new SPlane(this.normal_vec);
                    // make a plane object for the ring just to check vector contained
                    SVector ring_center_to_ab = vecs.get(i);
                    ring_center_to_ab.normalize();
                    // make length 1 just to get a unit vector of intersection
                    SPlane astrobee_plane = SPlane.makePlane(ring_center_to_ab, this.normal_vec);
                    SVector intersection = this.normal_vec.cross(astrobee_plane.getNorm());
                    // this vector lies on the line of intersection of planes
                    if(!(ring_plane.contains(intersection) && astrobee_plane.contains(intersection))){
                        System.out.println("Houston, we gotta big ass problem");
                        break;
                    }
                    intersection.setMagnitude(this._radius);
                    SPoint cur_pos = points.get(i);
                    SPoint center_1 = this._center.shift(intersection);
                    SPoint center_2 = this._center.shift(intersection.negate());
                    // check both possible semicircle centers for which to assign
                    double c1_dist = center_1.distSquared(cur_pos);
                    double c2_dist = center_2.distSquared(cur_pos);
                    SPoint semi_circle_center;
                    if (c1_dist > c2_dist) { semi_circle_center = center_2; } else { semi_circle_center = center_1; }
                    SVector semi_cir_to_ab = SVector.genVec(cur_pos, semi_circle_center);
                    // assign the vector accordingly
                    if (semi_cir_to_ab.length() > this.semi_circle_radius+this.AB_collider_radius){
                        info.add(1);
                        System.out.println("#################");
                        System.out.println("Safe by far away enough from semicircle center "+i);
                    } else {
                        double semi_circle_angle = semi_cir_to_ab.getAngleDeg(this.normal_vec);
                        // we use the OG normal vec since it is still parallel to the semi circle base
                        double hypotenuse_lim = this.getHypotenuse(this._radius, semi_cir_to_ab.length());
                        /*
                        *   The astrobee's position relative to the semicircle center can be defined by
                        *   an angle, but we need to know which "side" of the semicircle the astrobee is on.
                        *   The hyp_lim provides this by defining a certain length over which we know
                        *   the astrobee is on the side of the semicircle, by properties of right triangles.
                         */
                        if (ab_dist_from_ring_center >= hypotenuse_lim){
                            // in the case that length is greater than hyp_lim, we know AB is on OUTSIDE OF CIRCLE
                            info.add(0);
                            System.out.println("hyp lim:: "+hypotenuse_lim);
                            System.out.println("#################");
                            System.out.println("Failed by bumping into outside "+i);
                        } else {
                            // in the case that the length is shorter than hyp_lim, we know the astrobee is on the side closer to the ring center
                            double[] in_angle_lims = this.angle_limts_spec(AB_collider_radius, semi_circle_radius);
                            if (semi_circle_angle > in_angle_lims[0] && semi_circle_angle < in_angle_lims[1]){
                                // In this case, Astrobee is close to the flat side of the semicircle
                                double length_limit = semi_cir_to_ab.length() / Math.sin(Math.toRadians(semi_circle_angle));
                                if (length_limit > AB_collider_radius){
                                    info.add(1);
                                    System.out.println("Safe by distance from inner wall "+i);
                                } else {
                                    info.add(0);
                                    System.out.println("#################");
                                    System.out.println("Failed by bumping into semicircle wall "+i);
                                }
                            } else {
                                SPoint corner1 = semi_circle_center.shift(this.normal_vec.scalarMultiply(this.semi_circle_radius));
                                SPoint corner2 = semi_circle_center.shift(this.normal_vec.scalarMultiply(this.semi_circle_radius).negate());
                                SVector corner1_to_ab = SVector.genVec(corner1, cur_pos);
                                SVector corner2_to_ab = SVector.genVec(corner2, cur_pos);
                                if (corner1_to_ab.length() < AB_collider_radius || corner2_to_ab.length() < AB_collider_radius){
                                    info.add(0);
                                    System.out.println("#################");
                                    System.out.println("Failed by hitting semicircle conrer "+i);
                                } else {
                                    info.add(1);
                                    System.out.println("#################");
                                    System.out.println("Safe by avoiding corner "+i);
                                }
                            }
                        }
                    }
                }
            }
            info.add(lengths.get(i));
            info.add(points.get(i));
            info.add(vecs.get(i));
            info.add(angles.get(i));
            results.put(i, info);
        }
        System.out.println("Results are:: " + results);
        return results;
    }
/*
    public static void main(String[] args){
        System.out.println(Math.sin(Math.toRadians(90)));
        KeepOutZoneRing hi = new KeepOutZoneRing(new SPoint(0,0,0), 4, 0.5, new SVector(1,0,0));
        double lower = Math.ceil(hi.angle_limits()[0]);
        double higher = Math.floor(hi.angle_limits()[1]);
        System.out.println(hi.angle_limits()[1]);
        System.out.println(hi.angle_limits()[0]);
        System.out.println(hi.angle_limits_buffer(0.26)[1]);
        System.out.println(hi.angle_limits_buffer(0.26)[0]);
        for (int i = (int)lower; i <= higher; i++){
            double[] length_lims = hi.length_limits(i);
            System.out.println("lims for "+i+" angle::");
            System.out.println(length_lims[0]);
            System.out.println(length_lims[1]);
            System.out.println("that's an angle!!");
            System.out.println();
        }
        System.out.println(hi.aB_path_projection(new SPoint(4, 0 , 0), new SPoint(-1, 3, 3.5)));
        System.out.println();
        System.out.println();
        System.out.println(hi.aB_path_projection(new SPoint(4, 0 , 0), new SPoint(-1, 4, 3.5)));
    }
*/
}
