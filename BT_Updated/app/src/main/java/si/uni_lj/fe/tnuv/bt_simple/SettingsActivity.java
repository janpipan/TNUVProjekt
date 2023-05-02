package si.uni_lj.fe.tnuv.bt_simple;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void startActivityAnalysis(View view) {
        Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void startActivityWorkout(View view) {
        Intent intent = new Intent(SettingsActivity.this,WorkoutActivity.class);
        startActivity(intent);
    }
}