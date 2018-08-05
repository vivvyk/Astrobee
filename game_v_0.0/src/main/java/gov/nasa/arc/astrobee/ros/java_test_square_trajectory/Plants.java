package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;


public class Plants {
    private final KeepOutZoneRing myRing;
    private final int plant_number;
    private final double cone_height;
    private final SPoint center;
    private final double inner_radius;
    private final SVector normal;
    private final double angular_velocity;

    public Plants(KeepOutZoneRing ring, int plant_number) {
        this.myRing = ring;
        if (plant_number > 0 ) {
            this.plant_number = plant_number;
        } else {
            this.plant_number = 4;
        }
        this.cone_height = myRing.get_radius() + 0.2;
        this.center = myRing.get_center();
        this.inner_radius = myRing.get_radius();
        this.normal = myRing.getNormal_vec();
        this.angular_velocity = myRing.getAng_vel();
    }

    public int getPlant_number() {
        return plant_number;
    }

    public KeepOutZoneRing getMyRing() {
        return myRing;
    }

    /**
     * sets the initial plant position for the red flower plant
     *
     * @return:: SPoint of the initial plant position in world space
     *
     * TODO:: Fix so it works with any normal, not just <0,1,0> or <0,-1,0>
     */
    /*
    public SPoint set_plant(){

        int max = 5;
        int min = 1;
        int range = max - min + 1;

        if(this.normal.x == 0.0 && this.normal.y == 0.0 && this.normal.z == 0.0){
            System.out.println("IMPROPER NORMAL VECTOR");
            SPoint failure = new SPoint(0,0,0);
            return failure;
        }

        int rand1, rand2 = 0;
        SPoint pv = new SPoint(0.0, 0.0, 0.0);
        /*
        if(this.normal.x != 0.0){
            rand1 = (int)(Math.random() * range) + min;
            rand2 = (int)(Math.random() * range) + min;
            pv.set_y(rand1);
            pv.set_z(rand2);
            double x = -1 * (this.normal.y * pv.get_y() + this.normal.z * pv.get_z()) / this.normal.x;
            pv.set_x(x);
        }else if(this.normal.y != 0.0){
            rand1 = (int)(Math.random() * range) + min;
            rand2 = (int)(Math.random() * range) + min;
            pv.set_x(rand1);
            pv.set_z(rand2);
            double y = -1 * (this.normal.x * pv.get_x() + this.normal.z * pv.get_z()) / this.normal.y;
            pv.set_y(y);
        }else if(this.normal.z != 0.0){
            rand1 = (int)(Math.random() * range) + min;
            rand2 = (int)(Math.random() * range) + min;
            pv.set_x(rand1);
            pv.set_y(rand2);
            double z = -1 * (this.normal.x * pv.get_x() + this.normal.y * pv.get_y()) /this.normal.z;
            pv.set_z(z);
        }else{
            System.out.println("IMPROPER NORMAL VECTOR");
            SPoint failure = new SPoint(0,0,0);
            return failure;
        }
        pv.set_x(0);
        pv.set_y(0);
        pv.set_z(this.inner_radius/this.normal.y);

        //double new_mag = this.inner_radius;
        //double mag = SPoint.magnitude(pv);

        double n_x = pv.get_x() + this.center.get_x(); // * new_mag/mag +
        double n_y = pv.get_y() + this.center.get_y();
        double n_z = pv.get_z() + this.center.get_z();

        SPoint leadplant = new SPoint(n_x, n_y, n_z);
        return leadplant;
    }
    */

    /**
     *  Assumes
     * @param leadplant, the vector pointing from the center of the ring to the lead plant (red)
     * @return SPoint[], array of all the plant points relative to the center, in order::
     *                     [ red, purple, dandelion, white ]
     */

    public SPoint[] spawn_plants(SVector leadplant) {
        // separation angle b/w plants
        double delta_theta = (2 * Math.PI) / this.plant_number;
        // array to hold plant poses
        SPoint[] plants = new SPoint[this.plant_number];

        double theta_s = 0.0;

        //  SPoint new_leadplant = rodriguez_rotation(this.normal, leadplant_vec, theta);

        for (int i = 0; i < this.plant_number; i++) {
            if (i == 0) {
                plants[i] = SPoint.toSPoint(leadplant);
                theta_s += delta_theta;
                continue;
            }
            plants[i] = SPoint.toSPoint(rodriguez_rotation(this.normal, leadplant, theta_s));
            theta_s += delta_theta;
        }
        return plants;
    }
/*
    public SPoint[] spawn_plants(SPoint leadplant, double angle){
        // separation angle b/w plants
        double delta_t = (2 * Math.PI) / this.plant_number;
        // array to hold plant poses
        SPoint[] plants = new SPoint[this.plant_number];
        SVector leadplant_vec = new SVector(leadplant.get_x(), leadplant.get_y(), leadplant.get_z());

        double theta = angle;

        double theta_s = 0.0;

        SPoint new_leadplant = rodriguez_rotation(this.normal, leadplant_vec, theta);
        SVector new_leadplantv = new SVector(new_leadplant.get_x(), new_leadplant.get_y(), new_leadplant.get_z());

        for(int i = 0; i < this.plant_number; i++){
            if(i == 0){
                plants[i] = new_leadplant;
                theta_s += delta_t;
                continue;
            }
            plants[i] = rodriguez_rotation(this.normal, new_leadplantv, theta_s);
            theta_s += delta_t;
        }
        return plants;

    }
*/

    /**
     * @param k - normal vector to be rotated about
     * @param v - vector to be rotated about
     * @param theta - angle amount to rotate by
     * @return SVector of newly rotated vector
     */
    public SVector rodriguez_rotation(SVector k, SVector v, double theta){
        SVector e1 = v.scalarMultiply(Math.cos(theta));
        SVector e2 = SVector.cross(k, v).scalarMultiply(Math.sin(theta));
        SVector e3 = k.scalarMultiply(SVector.dot(k, v)*(1-Math.cos(theta)));

        SVector vrot = new SVector(e1.x + e2.x + e3.x, e1.y + e2.y + e3.y, e1.z + e2.z + e3.z);
        return vrot;
    }

    public SPoint rpy_cone(SPoint rpy){

        double pitch = rpy.get_y();
        double yaw = rpy.get_z();

        double y = this.cone_height * Math.sin(yaw);
        double x = this.cone_height * Math.cos(yaw);
        double z = this.cone_height * Math.sin(pitch);

        SPoint xyz = new SPoint(x, y, z);
        return  xyz;
    }

    public SPoint plant_vec(SPoint plantloc, SPoint astrobeeloc){
        SPoint plantv = new SPoint(plantloc.get_x() - astrobeeloc.get_x(), plantloc.get_y() - astrobeeloc.get_y(), plantloc.get_z() - astrobeeloc.get_z());
        return plantv;
    }

    public double scalar_projection(SPoint vec1, SPoint vec2){
        //PROJECTION OF VEC2 ONTO VEC1
        if(SPoint.magnitude(vec1) != 0.0) {
            double scalar_p = SPoint.dot(vec1, vec2) / SPoint.magnitude(vec1);
            return scalar_p;
        }else{
            System.out.println("IMPROPER VECTOR PROJECTION: DIVISION BY ZERO");
            return 0.0;
        }
    }

    public boolean score(SPoint plantv, SPoint conev){
       double plant_proj = scalar_projection(conev, plantv);
       /*
       System.out.println("PROJECTION");
       System.out.println(plant_proj);
        */
       if(plant_proj > (this.cone_height) || plant_proj <= 0) {
           System.out.println("MISSED!");
           return false;
       }

       double dist_conev = Math.sqrt(Math.pow(SPoint.magnitude(plantv), 2) - Math.pow(plant_proj, 2));
       if(dist_conev <= (plant_proj)){
          // System.out.println("SCORE!");
           return true;
       }

       //System.out.println("MISSED");
       return false;
    }

    public static int decide_score(int index, int score){
        if(index == 0){
            System.out.println("SCORED ON FIREWEED");
            score -= 50;
        }else if(index == 1){
            System.out.println("SCORED ON HONEYSUCKLE");
            score += 300;
        }else if(index == 2){
            System.out.println("SCORED ON ARUGULA");
            score += 200;
        }else if(index == 3){
            System.out.println("SCORED ON PANDORA");
            score += 100;
        }
        return score;
    }

    public static void main(String args[]) throws InterruptedException {
        Plants plant = new Plants(new KeepOutZoneRing(new SPoint(1, -0.5, 4.9), 0.6, 0.2, new SVector(0, 1.0, 0), 0), 4);


        /*
        SVector k = new SVector(1, 0, 0);
        SVector v = new SVector(0, 0, 1);
        double theta = 0;
        SVector e1 = v.scalarMult(Math.cos(theta));
        SVector e2 = SVector.cross(k, v).scalarMult(Math.sin(theta));
        SVector e3 = k.scalarMult(SVector.dot(k, v)*(1-Math.cos(theta)));

        SVector vrot = new SVector(e1.x + e2.x + e3.x, e1.y + e2.y + e3.y, e1.z + e2.z + e3.z);
        System.out.println(vrot.toString());
        */

        // The following lines prove the order of plants by rodriguez rotation
        SVector original = new SVector(0, 0.6, 0);
        SQuaternion rotation = new SQuaternion(-0.7071, 0, 0, 0.7071);
        Thread.sleep(3000);
        SPoint[] poses = plant.spawn_plants(SQuaternion.rotateVecByQuat(original, rotation));
        for (SPoint point : poses) {
            System.out.println(point);
        }
    }


}
