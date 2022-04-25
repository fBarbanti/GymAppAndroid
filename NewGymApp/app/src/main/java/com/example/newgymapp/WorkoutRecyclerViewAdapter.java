package com.example.newgymapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WorkoutRecyclerViewAdapter extends RecyclerView.Adapter<WorkoutRecyclerViewAdapter.CustomView>{

    private final WorkoutRecyclerViewInterface workoutRecyclerViewInterface;
    private Context context;
    private ArrayList<Workout> workouts;


    public WorkoutRecyclerViewAdapter(Context context, ArrayList<Workout> workouts, WorkoutRecyclerViewInterface workoutRecyclerViewInterface){
        this.context = context;
        this.workouts = workouts;
        this.workoutRecyclerViewInterface = workoutRecyclerViewInterface;
    }

    @NonNull
    @Override
    public CustomView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create layout for each workout
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_workout, parent, false);
        return new WorkoutRecyclerViewAdapter.CustomView(view, workoutRecyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomView holder, int position) {
        // Change the data of each workout based on the position of each item
        holder.nameWorkView.setText(workouts.get(position).getName());
        holder.typeWorkView.setText(workouts.get(position).getType());
        String i = String.valueOf(workouts.get(position).getMinTot())+"m";
        holder.timeWorkView.setText(i);
        holder.listWorkView.setText(String.valueOf(workouts.get(position).getExerciseArrayList().size()));
    }

    @Override
    public int getItemCount() {
        // Tells the view the view how many workouts we have
        return workouts.size();
    }

    public static class CustomView extends RecyclerView.ViewHolder {

        TextView nameWorkView;
        TextView timeWorkView;
        TextView listWorkView;
        TextView typeWorkView;
        ImageView btnPlayWorkout;

        public CustomView(@NonNull View itemView, WorkoutRecyclerViewInterface workoutRecyclerViewInterface) {
            super(itemView);

            nameWorkView = itemView.findViewById(R.id.workout_name_card);
            timeWorkView = itemView.findViewById(R.id.timer_text_workout_card);
            listWorkView = itemView.findViewById(R.id.list_text_workout_card);
            typeWorkView = itemView.findViewById(R.id.type_text_workout_card);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(workoutRecyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            workoutRecyclerViewInterface.onWorkoutClick(pos);
                        }
                    }
                }
            });
        }
    }
}
