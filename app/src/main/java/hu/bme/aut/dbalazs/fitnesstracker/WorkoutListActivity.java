package hu.bme.aut.dbalazs.fitnesstracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.Date;

import hu.bme.aut.dbalazs.fitnesstracker.adapter.WorkoutAdapter;
import hu.bme.aut.dbalazs.fitnesstracker.model.Workout;

public class WorkoutListActivity extends AppCompatActivity implements WorkoutCreateFragment.WorkoutCreatedI {

    private boolean mTwoPane;
    private ArrayList<Workout> woList;
    private WorkoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.workoutFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addWorkout();
                Snackbar.make(view, "New Workout added", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        View recyclerView = findViewById(R.id.workout_list);
        assert recyclerView != null;
        woList = createWorkoutList();

        if (findViewById(R.id.workout_detail_container) != null) {
            mTwoPane = true;
        }
        setupRecyclerView((RecyclerView) recyclerView, woList);
    }

    private void addWorkout(){
    }

    // TODO delete
    private ArrayList<Workout> createWorkoutList(){
        ArrayList<Workout> wo = new ArrayList<Workout>();
        wo.add(new Workout(Workout.WorkoutType.ARM_WORKOUT, null, new Date().getTime()));
        wo.add(new Workout(Workout.WorkoutType.CHEST_WORKOUT, null, new Date().getTime()));
        wo.add(new Workout(Workout.WorkoutType.SHOULDER_WORKOUT, null, new Date().getTime()));
        wo.add(new Workout(Workout.WorkoutType.LEG_WORKOUT, null, new Date().getTime()));
        return wo;
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView, ArrayList<Workout> woList) {
        adapter = new WorkoutAdapter(woList, mTwoPane, this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onWorkoutCreated(Workout newWo) {
        woList.add(newWo);
        adapter.notifyDataSetChanged();
    }
}
