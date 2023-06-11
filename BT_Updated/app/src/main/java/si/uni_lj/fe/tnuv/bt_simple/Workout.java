package si.uni_lj.fe.tnuv.bt_simple;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class Workout extends Fragment {



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
        btnNewWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // inflate a layout from xml
                LayoutInflater inflater = LayoutInflater.from(requireContext());
                View newWorkout = inflater.inflate(R.layout.new_workout, null);
                // add view to parent layout
                LinearLayout parentLayout = view.findViewById(R.id.workouts_layout);
                parentLayout.addView(newWorkout);
                btnNewWorkout.setVisibility(View.GONE);
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
}