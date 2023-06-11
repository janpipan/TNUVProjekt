package si.uni_lj.fe.tnuv.bt_simple;

import android.bluetooth.BluetoothSocket;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class Workout extends Fragment {

    private BluetoothData viewModel;

    private WorkoutViewModel workoutViewModel;

    private WorkoutSession workoutSession;


    public Workout() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_workout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // button for starting new workout
        Button btnNewWorkout = view.findViewById(R.id.new_workout);

        // check if workout is in progress
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.workoutViewModel.isWorkoutInProgress().observe(getViewLifecycleOwner(), inProgress -> {
            //Toast.makeText(requireContext(), "workout in progress", Toast.LENGTH_SHORT).show();
            btnNewWorkout.setVisibility(View.GONE);

            // restore workout session when workout is in progress
            workoutSession = mainActivity.workoutViewModel.getWorkoutSession().getValue();

            // if workout list is not empty display lifts on screen
            if (workoutSession.getLifts().size() > 0){
                for (Lift lift: workoutSession.getLifts()){
                    addLiftView(view, lift);
                }
            }
        });



        btnNewWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // create lift
                //addLiftView(view);

                // hidde button
                btnNewWorkout.setVisibility(View.GONE);

                // create new workout session and add it to workout view model
                workoutSession = new WorkoutSession();
                mainActivity.workoutViewModel.setWorkoutSession(workoutSession);
                mainActivity.workoutViewModel.startWorkout();

                // create observer for received bluetooth data
                viewModel = new ViewModelProvider(requireActivity()).get(BluetoothData.class);
                viewModel.clearReceivedData();
                viewModel.getReceivedData().observe(getViewLifecycleOwner(), new Observer<String>() {
                    @Override
                    public void onChanged(String s) {
                        if (s != null && !s.isEmpty()){
                            Lift lift = new Lift(s);
                            workoutSession.addLift(lift);
                            addLiftView(view, lift);
                            Toast.makeText(requireContext(), s, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        // button for finishing workout
        Button btnFinishWorkout = view.findViewById(R.id.finish_workout);
        btnFinishWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNewWorkout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void addLiftView(View view, Lift lift){
        LinearLayout parentLayout = view.findViewById(R.id.workouts_layout);
        // inflate a layout from xml
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View newWorkout = inflater.inflate(R.layout.new_workout, parentLayout, false);

        // Populate the views in the layout using the Lift data
        ((TextView) newWorkout.findViewById(R.id.exercise)).setText(lift.getExercise());
        ((TextView) newWorkout.findViewById(R.id.weight)).setText(String.valueOf(lift.getWeight()));
        ((TextView) newWorkout.findViewById(R.id.percentage)).setText(String.valueOf(lift.getPercentage()));
        ((TextView) newWorkout.findViewById(R.id.peak_velocity)).setText(String.valueOf(lift.getPeakVelocity()));
        ((TextView) newWorkout.findViewById(R.id.tag)).setText(lift.getTag());
        ((TextView) newWorkout.findViewById(R.id.picture)).setText(lift.getTag());

        // add view to parent layout

        parentLayout.addView(newWorkout);
    }
}