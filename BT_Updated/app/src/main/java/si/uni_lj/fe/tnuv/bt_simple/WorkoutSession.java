package si.uni_lj.fe.tnuv.bt_simple;

import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class WorkoutSession {
    private List<Lift> lifts;

    public WorkoutSession(){
        this.lifts = new ArrayList<>();
    }

    public List<Lift> getLifts(){
        return lifts;
    }

    public void setLifts(List<Lift> lifts){
        this.lifts = lifts;
    }

    public void addLift(Lift lift) {
        this.lifts.add(lift);
    }

    public void writeToFile(String fileName, String appSpecificExternalDir) {
        File file = new File(appSpecificExternalDir, fileName);

        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            // Write the content to the file
            for (Lift lift: this.lifts){
                fos.write(lift.toString().getBytes());
                fos.write("\n".getBytes());
            }
        } catch (IOException e) {
            Log.e("fileWrite", "Failed to write to file", e);
        }
    }


}
