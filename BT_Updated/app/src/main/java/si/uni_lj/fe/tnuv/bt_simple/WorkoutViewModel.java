package si.uni_lj.fe.tnuv.bt_simple;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class WorkoutViewModel extends ViewModel {

    private final MutableLiveData<Boolean> workoutInProgress = new MutableLiveData<>();
    private final MutableLiveData<WorkoutSession> currentWorkout = new MutableLiveData<>();
    private final MutableLiveData<HashMap<String, HashMap<String, Double>>> vmax = new MutableLiveData<>();

    public WorkoutViewModel(){
        initializeData();
    }

    public void initializeData(){
        HashMap<String, HashMap<String, Double>> initialData = new HashMap<>();

        List<String> exercises = Arrays.asList("Poteg", "Poteg na Moc", "Poteg na Roke", "Nalog", "Nalog na Roke", "Nalog na Moc");
        List<String> percentages = Arrays.asList("80%RM", "85%RM", "90%RM", "95%RM", "100%RM");

        for (String exercise : exercises) {
            HashMap<String, Double> exerciseData = new HashMap<>();
            for (String percentage : percentages) {
                exerciseData.put(percentage, 0.0);
            }
            initialData.put(exercise, exerciseData);
        }

        vmax.setValue(initialData);
    }

    public void readDataFromFile(Context context, String filename) {
        FileInputStream fis = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;

        HashMap<String, HashMap<String, Double>> data = new HashMap<>();

        try {
            File file = new File(context.getExternalFilesDir(null), filename);

            if (!file.exists()) {
                Log.e("readDataFromFile", "File does not exist: " + filename);
                return;
            }

            fis = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fis);
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitLine = line.split(",");

                String exercise = splitLine[0].trim();
                String percentage = splitLine[4].trim();
                Double value = Double.parseDouble(splitLine[2].trim());

                if (!data.containsKey(exercise)) {
                    data.put(exercise, new HashMap<>());
                }

                HashMap<String, Double> innerMap = data.get(exercise);
                Double currentMaxValue = innerMap.getOrDefault(percentage, 0.0);

                if (value > currentMaxValue) {
                    innerMap.put(percentage, value);
                }
            }

            vmax.setValue(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public LiveData<HashMap<String, HashMap<String, Double>>> vmaxData() {
        return vmax;
    }

    public void updateMaxValue(String exercise, String percentage, Double newValue) {
        HashMap<String, HashMap<String, Double>> currentData = vmax.getValue();
        if (currentData.containsKey(exercise)) {
            HashMap<String, Double> exerciseData = currentData.get(exercise);
            if (exerciseData.containsKey(percentage)) {
                Double currentMax = exerciseData.get(percentage);
                if (newValue > currentMax) {
                    exerciseData.put(percentage, newValue);
                }
            } else {
                exerciseData.put(percentage, newValue);
            }
        } else {
            HashMap<String, Double> newExerciseData = new HashMap<>();
            newExerciseData.put(percentage, newValue);
            currentData.put(exercise, newExerciseData);
        }
        vmax.setValue(currentData);
    }
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
