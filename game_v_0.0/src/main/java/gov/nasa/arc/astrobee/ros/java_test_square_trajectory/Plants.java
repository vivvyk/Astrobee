package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;


public class Plants {
    private int plant_number;
    private double cone_height;
    public SPoint[] plant_loc;

    public Plants(int plant_num, double cone_h) {
        this.plant_number = plant_num;
        this.cone_height = cone_h;
        this.plant_loc = new SPoint[this.plant_number];
    }

    public double init_plants(Plants plants, int plantnum, float center[], int radius, double inclination){
        double azimuth = 0;
        double delta_angles = (2 * Math.PI) / plantnum;
        double m = 0;
        double z = 0;

        for(int i = 0; i < plantnum; i++){
            double x = center[0] + radius * Math.cos(azimuth);
            double y = center[1] + radius * Math.sin(azimuth);
            //double z = (center[2] + radius * Math.cos(inclination));
            if(i==0){
                z = center[2] + radius * Math.cos(inclination);
                if(x - center[0] == 0.0) {
                    x += 0.001;
                }
                m = (z - center[2]) / (x - center[0]);
            }else{
                z = center[2] + m*x;
            }
            plants.plant_loc[i] = new SPoint(x, y, z);
            azimuth += delta_angles;
        }
        return m;
    }

    public void move_plants(Plants plants, int plantnum, float center[], int radius, double inclination, int step_size){
        double new_azimuth = 0;
        for(int i = 0; i < plantnum; i++){
            new_azimuth = Math.atan((plants.plant_loc[i].get_y() - center[1])/(plants.plant_loc[i].get_x() - center[0])) + step_size;
            if(new_azimuth >= 2*Math.PI){
                new_azimuth = 0;
            }
            double x = center[0] + radius * Math.sin(inclination) * Math.cos(new_azimuth);
            double y = center[1] + radius * Math.sin(inclination) * Math.sin(new_azimuth);
            double z = center[2] + radius * Math.cos(inclination);
            plants.plant_loc[i] = new SPoint(x, y, z);
        }
    }

    public SPoint rpy_cone(SPoint rpy){
        double roll = rpy.get_x();
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

    public int score(SPoint plantv, SPoint conev){
       double plant_proj = scalar_projection(conev, plantv);

       System.out.println("PROJECTION");
       System.out.println(plant_proj);

       if(plant_proj > (this.cone_height) || plant_proj <= 0) {
           System.out.println("MISSED!");
           return 0;
       }

       double dist_conev = Math.sqrt(Math.pow(SPoint.magnitude(plantv), 2) - Math.pow(plant_proj, 2));
       if(dist_conev <= (plant_proj)){
           System.out.println("SCORE!");
           return 1;
       }

       System.out.println("MISSED");
       return 0;


    }


}
