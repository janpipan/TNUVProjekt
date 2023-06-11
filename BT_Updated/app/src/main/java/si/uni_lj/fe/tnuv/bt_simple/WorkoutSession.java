package si.uni_lj.fe.tnuv.bt_simple;

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
}
