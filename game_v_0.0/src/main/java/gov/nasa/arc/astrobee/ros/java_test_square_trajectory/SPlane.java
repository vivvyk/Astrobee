package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class SPlane {
    private SVector norm;

    public SPlane(SVector normal){
        if (normal.length() != 1){
            normal.normalize();
        }
        this.norm = normal;
    }

    public SVector getNorm(){
        return this.norm;
    }

    public void setNorm(SVector new_norm){
        this.norm = new_norm;
    }

    public static SPlane makePlane(SVector a, SVector b){
        SVector norm  = a.cross(b);
        return new SPlane(norm);
    }

    public static SPlane makePlane(SPoint a, SPoint b, SPoint c){
        SVector ab = new SVector(b.get_x() - a.get_x(), b.get_y() - a.get_y(), b.get_z() - a.get_x());
        SVector ac = new SVector(c.get_x() - a.get_x(), c.get_y() - a.get_y(), c.get_z() - a.get_x());
        SVector norm = ab.cross(ac);
        return new SPlane(norm);
    }

    public boolean contains(SVector of_interest){
        if (this.getNorm().dot(of_interest) == 0){
            return true;
        }
        return false;
    }
}
