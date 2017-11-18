package hu.bme.aut.dbalazs.fitnesstracker.model;

import java.util.ArrayList;

/**
 * Created by Balazs on 2017. 11. 08..
 */

public class Exercise {
    private String name;
    private ArrayList<Series> series;

    public Exercise(ArrayList<Series> reps, String name) {
        this.series = reps;
        this.name = name;
    }

    public Exercise(String name){
        this.name = name;
        this.series = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getReps(){
        return series.size();
    }
    public void addRep(Series newSer){
        series.add(newSer);
    }

    public void removeRep(){
        series.remove(series.size());
    }
}
