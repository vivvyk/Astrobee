package gov.nasa.arc.astrobee.ros.java_test_square_trajectory;

import java.io.*;

public class ScoreManager {

    public static void main(String... args) throws IOException {
        try {
            FileWriter writer = new FileWriter("/home/" + System.getProperty("user.name") + "/out.txt");
            BufferedWriter out = new BufferedWriter(writer);
            out.write("SCORE!");
            out.newLine();

            out.write(Integer.toString(2)); //fw hits
            out.newLine();
            out.write(Integer.toString(3)); //pd hits
            out.newLine();
            out.write(Integer.toString(4)); //ar hits
            out.newLine();
            out.write(Integer.toString(5)); //hs hits
            out.newLine();
            out.write(Integer.toString(7)); //fw d
            out.newLine();
            out.write(Integer.toString(0)); //pd d
            out.newLine();
            out.write(Integer.toString(0)); //ar d
            out.newLine();
            out.write(Integer.toString(0)); //hs d
            out.newLine();
            out.write(Integer.toString(0)); //mp1
            out.newLine();
            out.write(Integer.toString(0)); //mp2
            out.newLine();
            out.write(Integer.toString(0)); //fr1
            out.newLine();
            out.write(Integer.toString(3)); //fr2
            out.newLine();
            out.write(Integer.toString(0)); //hrn
            out.newLine();
            out.write(Integer.toString(0)); //hrd
            out.newLine();
            out.write(Integer.toString(0)); //total score
            out.newLine();

            out.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
