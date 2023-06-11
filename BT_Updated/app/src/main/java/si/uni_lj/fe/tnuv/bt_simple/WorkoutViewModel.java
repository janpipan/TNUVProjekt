package si.uni_lj.fe.tnuv.bt_simple;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WorkoutViewModel extends ViewModel {

    private final MutableLiveData<Boolean> workoutInProgress = new MutableLiveData<>();
    private final MutableLiveData<WorkoutSession> currentWorkout = new MutableLiveData<>();

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
