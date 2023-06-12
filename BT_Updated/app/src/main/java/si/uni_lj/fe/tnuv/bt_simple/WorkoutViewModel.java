package si.uni_lj.fe.tnuv.bt_simple;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;

public class WorkoutViewModel extends ViewModel {

    private final MutableLiveData<Boolean> workoutInProgress = new MutableLiveData<>();
    private final MutableLiveData<WorkoutSession> currentWorkout = new MutableLiveData<>();
    //private final MutableLiveData<HashMap<String, HashMap<String, Integer>>> vmax = new MutableLiveData<>();


    public void startWorkout() {
        workoutInProgress.setValue(true);
    }

    public void finishWorkout() {
        workoutInProgress.setValue(false);
    }

    public LiveData<Boolean> isWorkoutInProgress() {
        return workoutInProgress;
    }

    public LiveData<WorkoutSession> getWorkoutSession(){
        return currentWorkout;
    }

    public void setWorkoutSession(WorkoutSession workoutSession){
        currentWorkout.setValue(workoutSession);
    }

}
