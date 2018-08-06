package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import org.ros.node.AbstractNodeMain;

import java.util.List;
import java.util.Map;

public class KeepOutZoneRingWPlants extends KeepOutZoneRing {

    private final int numPlants;
    private final double limitAngle; //essentially the angle at the cone tip, the SWEEP
    private final String[] plantList;
    private final SVector leadPlantStart;


    public KeepOutZoneRingWPlants(SPoint center, double inner_radius, double semi_circle_radius, SVector normal, double ang_vel) {
        super(center, inner_radius, semi_circle_radius, normal, ang_vel);
        numPlants = 4;
        limitAngle = getLimitAngle(numPlants);
        plantList = Pollen.populate(this.numPlants);
        leadPlantStart = new SVector(0, inner_radius, 0);
    }

    public KeepOutZoneRingWPlants(SPoint center, double inner_radius, double semi_circle_radius, SVector normal, double ang_vel, int numPlants) {
        super(center, inner_radius, semi_circle_radius, normal, ang_vel);
        this.numPlants = numPlants;
        limitAngle = getLimitAngle(numPlants);
        plantList = Pollen.populate(this.numPlants);
        leadPlantStart = new SVector(0, inner_radius, 0);
    }

    private double getLimitAngle(int numPlants) {
        double maxAngle = Math.toDegrees((2*Math.PI) / numPlants);
        return maxAngle * 0.2; // have to be pretty accurate
    }
    public int getPlantNumber() {
        return this.numPlants;
    }

    public String[] getPlantList() { return this.plantList; }

    public SVector getLeadPlantStart() { return leadPlantStart; }

    // Returns the lead plant cuurent RELATIVE position to the center of the ring
    public SVector getLeadPlantCurrentPos(SQuaternion ringOrientation) { return SQuaternion.rotateVecByQuat(this.leadPlantStart, ringOrientation); }

    public Map<Integer, List<Object>> aB_path_projection(SPoint AB_pos, SPoint AB_goal) {
        return super.aB_path_projection(AB_pos, AB_goal);
    }

    /**
     * @return SVector[], array of all the plant points RELATIVE to the center of the ring, in order::
     *                     [ red, purple, dandelion, white ]
     */
    public SVector[] getPlants(SQuaternion ringOrientation) {
        SVector leadPlantCurrentPos = getLeadPlantCurrentPos(ringOrientation);
        // separation angle b/w plants
        double delta_theta = (2 * Math.PI) / this.numPlants;

        // array to hold plant positions
        SVector[] plantPoses = new SVector[this.numPlants];

        double theta_s = 0.0;

        for (int i = 0; i < this.numPlants; i++) {
            if (i == 0) {
                plantPoses[i] = leadPlantCurrentPos;
                theta_s += delta_theta;
                continue;
            }
            plantPoses[i] = SVector.rodriguezRotation(getNormal_vec(), leadPlantCurrentPos, theta_s);
            theta_s += delta_theta;
        }
        return plantPoses;
    }

    /**
     * Returns the reuslt of the projection of the flashlight from the AB
     * @param abOrientation Quaternion describing the AB current orientation
     * @return the vector from the ab projection RELATIVE TO THE ASTROBEE, aka if
     * the astrobee is near enough to the ring enter, we just consider its cone as originating from
     * the ring center...
     */
    public SVector projectCone(SQuaternion abOrientation) {
        SVector initialCone = ABInfo.intial_vector.setMagnitude(this.get_radius());
        SVector abCone = SQuaternion.rotateVecByQuat(initialCone, abOrientation);
        return abCone;
    }

    public boolean scoreOnPlant(SVector plantVec, SVector abCone) {
        double angBetween = plantVec.getAngleDeg(abCone);
        if (Math.abs(angBetween) <= limitAngle) {
            System.out.println("SCORE");
            System.out.println(limitAngle+"<- limit & angBw ->" + angBetween);
            return true;
        }
        return false;
    }

    /**
     * Ultimate method called when determining if scored on a plant
     *
     * @param abOrient - ab quat input
     * @param ringOrient - ring quat input
     * @return String of plant Name you scored on, null if none
     */
    public String scoreOnRing(SQuaternion abOrient, SQuaternion ringOrient) {
        SVector[] plantPoses = getPlants(ringOrient);
        SVector abCone = projectCone(abOrient);
        for (int i = 0; i < plantPoses.length; i++) {
            if (scoreOnPlant(plantPoses[i], abCone)) {
                System.out.println("Scored on:: "+ plantList[i]);
                return this.plantList[i];
            }
        }
        return null;
    }
}
