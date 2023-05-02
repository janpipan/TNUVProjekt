package si.uni_lj.fe.tnuv.bt_simple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class WorkoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
    }

    public void startActivityAnalysis(View view) {
        Intent intent = new Intent(WorkoutActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void startActivitySettings(View view) {
        Intent intent = new Intent(WorkoutActivity.this,SettingsActivity.class);
        startActivity(intent);
    }
}