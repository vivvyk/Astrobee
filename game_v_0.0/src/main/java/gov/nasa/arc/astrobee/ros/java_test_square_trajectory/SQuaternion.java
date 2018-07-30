package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

public class SQuaternion {
    public double x = 0;
    public double y = 0;
    public double z = 0;
    public double w = 1;
    private double[] quat_mat = {x, y, z, w};
    private SVector orig_ring_norm = new SVector(0,0 ,1);

    public SQuaternion() {
    }

    public SQuaternion(double yaw, double pitch, double roll) {
        yaw = Math.toRadians(yaw / 2);
        pitch = Math.toRadians(pitch / 2);
        roll = Math.toRadians(roll / 2);

        x = Math.sin(yaw) * Math.sin(pitch) * Math.cos(roll) + Math.cos(yaw) * Math.cos(pitch) * Math.sin(roll);
        y = Math.sin(yaw) * Math.cos(pitch) * Math.cos(roll) + Math.cos(yaw) * Math.sin(pitch) * Math.sin(roll);
        z = Math.cos(yaw) * Math.sin(pitch) * Math.cos(roll) - Math.sin(yaw) * Math.cos(pitch) * Math.sin(roll);
        w = Math.cos(yaw) * Math.cos(pitch) * Math.cos(roll) - Math.sin(yaw) * Math.sin(pitch) * Math.sin(roll);
        quat_mat = new double[] {x, y, z, w};
    }

    public SQuaternion(double x, double y, double z, double w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        quat_mat = new double[] {x, y, z, w};
    }

    public double[] getQuat() {
        return quat_mat;
    }

    /**
     * Normalizes your quaternion
     */
    public void norm() {
        double mag = Math.sqrt(Math.pow(w, 2) + Math.pow(y, 2) + Math.pow(x, 2) + Math.pow(z, 2));
        this.x /= mag;
        this.y /= mag;
        this.z /= mag;
        this.w /= mag;
        this.quat_mat = new double[] {x, y, z, w};
    }

    /**
     * Method for the quaternion it will take to convert from base ring norm vector (above)
     * to the desired normal vector
     *
     * IF REQUESTING NORM OF 0 0 -1 aka dot returns ~ -1, WILL RETURN QUAT FOR MAKING RING ROTATE ABOUT Z AXIS 180
     *
     * @param desired is the vector you want to reach
     * @return SQuaternion object that specifies correct rotation quaternion for the task
     */
    public SQuaternion vecDiffToQuat(SVector desired) {
        SQuaternion q = new SQuaternion();
        desired.normalize();
        if (SVector.dot(orig_ring_norm, desired) > 0.999) {
            return new SQuaternion();
        } else if (SVector.dot(orig_ring_norm, desired) < -0.999) {
            return new SQuaternion(0, 0, 1, 0);
        } else {
            SVector cross = orig_ring_norm.cross(desired);
            q.x = cross.x;
            q.y = cross.y;
            q.z = cross.z;
            q.w = Math.sqrt(Math.pow(desired.length(), 2)) + SVector.dot(orig_ring_norm, desired);
            q.norm();
        }
        return q;
    }

    public static void main(String... args) {
        SQuaternion quat = new SQuaternion( 0, 0, 90);
        SQuaternion other = new SQuaternion();
        SVector desired_norm = new SVector(0, 1, 0);
        SQuaternion q = other.vecDiffToQuat(desired_norm);
        double[] val = q.getQuat();
        for (double x: val) {
            System.out.println(x);
        }
    }
}
