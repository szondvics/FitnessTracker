package hu.bme.aut.dbalazs.fitnesstracker;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hu.bme.aut.dbalazs.fitnesstracker.adapter.WorkoutAdapter;
import hu.bme.aut.dbalazs.fitnesstracker.application.FitnessApplication;
import hu.bme.aut.dbalazs.fitnesstracker.database.FitnessDatabaseInterface;
import hu.bme.aut.dbalazs.fitnesstracker.database.LoadWorkoutsTask;
import hu.bme.aut.dbalazs.fitnesstracker.model.Workout;

public class WorkoutListActivity extends AppCompatActivity implements WorkoutCreateFragment.CreateWorkoutListener, DatePickerDialog.OnDateSetListener {

    private boolean mTwoPane;
    private WorkoutAdapter adapter;
    private RecyclerView recyclerView;
    private LoadWorkoutsTask loadWorkoutsTask;
    private FitnessDatabaseInterface databaseIf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.workout_list_activity);

        if (findViewById(R.id.exercise_list_fragment) != null) {
            mTwoPane = true;
        }
        else{
            mTwoPane = false;
        }
        if(!mTwoPane) {
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.workoutFab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WorkoutCreateFragment fragment = new WorkoutCreateFragment();
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().add(fragment, WorkoutCreateFragment.TAG)
                            .addToBackStack(null)
                            .show(fragment)
                            .commit();
                }
            });
        }
        else{
            findViewById(R.id.workout_list_add_twoPane).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WorkoutCreateFragment fragment = new WorkoutCreateFragment();
                    FragmentManager fm = getSupportFragmentManager();
                    fm.beginTransaction().add(fragment, WorkoutCreateFragment.TAG)
                            .addToBackStack(null)
                            .show(fragment)
                            .commit();
                }
            });
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = (RecyclerView) findViewById(R.id.workout_list);
        assert recyclerView != null;

        databaseIf = FitnessApplication.getDatabaseInterface();
    }

    public void showWorkouts(Cursor cursor){
        adapter = new WorkoutAdapter(this, cursor, mTwoPane);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onWorkoutCreated(Workout newWo) {
        databaseIf.insertWorkout(newWo);
        refreshList();
        if(mTwoPane) {
            Snackbar.make(findViewById(android.R.id.content), "New Workout added", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else {
            Snackbar.make(findViewById(R.id.workoutFab), "New Workout added", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void removeWorkout(long woId){
        databaseIf.removeWorkout(woId);
        refreshList();
        if(mTwoPane) {
            Snackbar.make(findViewById(android.R.id.content), "Workout removed", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else {
            Snackbar.make(findViewById(R.id.workoutFab), "Workout removed", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        WorkoutCreateFragment currentFragment = (WorkoutCreateFragment) getSupportFragmentManager().findFragmentByTag(WorkoutCreateFragment.TAG);
        TextView dateTV = currentFragment.getView().getRootView().findViewById(R.id.workoutAddDateTV);
        Calendar c = Calendar.getInstance();
        c.set(year, month, day, 0, 0);
        Date date = c.getTime();
        currentFragment.setDate(date);
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d. EEE");
        dateTV.setText(sdf.format(date));
    }

    @Override
    public void onResume(){
        super.onResume();
        // Frissitjuk a lista tartalmat, ha visszater a user
        refreshList();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (loadWorkoutsTask != null) {
            loadWorkoutsTask.cancel(false);
        }
    }

    @Override
    public void onDestroy() {
        // Ha van Cursor rendelve az Adapterhez, lezarjuk
        if (adapter != null && adapter.getCursor() != null) {
            adapter.getCursor().close();
        }
        super.onDestroy();
    }

    private void refreshList() {
        if (loadWorkoutsTask != null) {
            loadWorkoutsTask.cancel(false);
        }

        loadWorkoutsTask = new LoadWorkoutsTask(this, databaseIf);
        loadWorkoutsTask.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
