package si.uni_lj.fe.tnuv.bt_simple;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

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

        viewModel = new ViewModelProvider(requireActivity()).get(BluetoothData.class);

        LinearLayout iconContainer = view.findViewById(R.id.bluetooth_icon);
        TextView connectionText = view.findViewById(R.id.bluetooth_connection);
        if (viewModel.isConnected().getValue()){
            btnNewWorkout.setVisibility(View.VISIBLE);
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.baseline_bluetooth_connected_24);
            iconContainer.addView(imageView);
            connectionText.setText("CONNECTED");
        } else {
            btnNewWorkout.setVisibility(View.GONE);
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.drawable.baseline_bluetooth_searching_24);
            iconContainer.addView(imageView);
            connectionText.setText("NOT CONNECTED");
        }

        // check if workout is in progress
        MainActivity mainActivity = (MainActivity) requireActivity();
        mainActivity.workoutViewModel.isWorkoutInProgress().observe(getViewLifecycleOwner(), inProgress -> {

            if (inProgress){
                btnNewWorkout.setVisibility(View.GONE);

                // restore workout session when workout is in progress
                workoutSession = mainActivity.workoutViewModel.getWorkoutSession().getValue();

                // if workout list is not empty display lifts on screen
                if (workoutSession.getLifts().size() > 0){
                    for (Lift lift: workoutSession.getLifts()){
                        addLiftView(view, lift);
                    }
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

                if (mainActivity.workoutViewModel.isWorkoutInProgress().getValue()){
                    // write workout to file
                    File appSpecificExternalDir = getContext().getExternalFilesDir(null);
                    workoutSession.writeToFile("bluetoothData.txt", appSpecificExternalDir.getAbsolutePath());

                    // delete lift view
                    LinearLayout parentLayout = view.findViewById(R.id.workouts_layout);
                    parentLayout.removeAllViews();

                    mainActivity.workoutViewModel.setWorkoutSession(null);

                    // set workout to false
                    mainActivity.workoutViewModel.finishWorkout();

                    // make new workout button visible
                    btnNewWorkout.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void addLiftView(View view, Lift lift){
        LinearLayout parentLayout = view.findViewById(R.id.workouts_layout);
        // inflate a layout from xml
        LayoutInflater inflater = LayoutInflater.from(requireContext());
        View newWorkout = inflater.inflate(R.layout.new_lift, parentLayout, false);

        // set date formatter
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy/HH:mm:ss", Locale.getDefault());


        // Populate the views in the layout using the Lift data
        ((TextView) newWorkout.findViewById(R.id.date)).setText(dateFormat.format(lift.getDate()));
        ((TextView) newWorkout.findViewById(R.id.exercise)).setText(lift.getExercise());
        ((TextView) newWorkout.findViewById(R.id.weight)).setText(String.valueOf(lift.getWeight()));
        ((TextView) newWorkout.findViewById(R.id.percentage)).setText(String.valueOf(lift.getPercentage()));
        ((TextView) newWorkout.findViewById(R.id.peak_velocity)).setText("Peak velocity" + String.valueOf(lift.getPeakVelocity()));
        ((TextView) newWorkout.findViewById(R.id.tag)).setText(lift.getTag());
        ((EditText) newWorkout.findViewById(R.id.comment)).setText(lift.getComment());

        // add view to parent layout

        parentLayout.addView(newWorkout);
    }

}