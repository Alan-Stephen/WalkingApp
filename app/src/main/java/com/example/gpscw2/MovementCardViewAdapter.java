package com.example.gpscw2;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.util.List;

public class MovementCardViewAdapter  extends
        RecyclerView.Adapter<MovementCardViewAdapter.MovementViewHolder>{

    private List<TravelEntity> data;
    private AppCompatActivity activity;
    private LayoutInflater layoutInflater;

    MovementViewViewModel viewModel;
    MovementCardViewAdapter(AppCompatActivity activity, LayoutInflater layoutInflater,
                            MovementViewViewModel viewModel) {
        this.activity = activity;
        this.layoutInflater = layoutInflater;
        this.viewModel = viewModel;
        data = viewModel.getData();
    }
    @NonNull
    @Override
    public MovementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.movement_card,parent,false);
        return new MovementViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovementViewHolder holder, int position) {
        holder.bind(data.get(position),position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MovementViewHolder extends RecyclerView.ViewHolder {
        int id;
        EditText title;
        EditText description;
        TextView distanceTravelled;
        TextView duration;
        TextView date;
        Button delete;
        Button discardChanges;
        Button saveChanges;
        ImageView goodMovement;
        ImageView badMovement;
        ImageView sunWeather;
        ImageView rainWeather;
        ImageView snowWeather;

        MutableLiveData<Weather> weather;
        MutableLiveData<Boolean> positive;
        MutableLiveData<Boolean> changed;
        public MovementViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.cardTitle);
            description = itemView.findViewById(R.id.cardDescription);
            distanceTravelled = itemView.findViewById(R.id.cardDistance);
            duration = itemView.findViewById(R.id.cardDuration);
            date = itemView.findViewById(R.id.cardDate);
            delete = itemView.findViewById(R.id.cardDelete);
            goodMovement = itemView.findViewById(R.id.cardGoodMovement);
            badMovement = itemView.findViewById(R.id.cardBadMovement);
            sunWeather = itemView.findViewById(R.id.cardSunWeather);
            rainWeather = itemView.findViewById(R.id.cardRainWeather);
            snowWeather = itemView.findViewById(R.id.cardSnowWeather);
            discardChanges = itemView.findViewById(R.id.cardDiscardChanges);
            saveChanges = itemView.findViewById(R.id.cardSaveChanges);

            weather = new MutableLiveData<>(Weather.RAIN);
            positive = new MutableLiveData<>(true);
            changed = new MutableLiveData<>(false);
        }

        public void bind(TravelEntity entity,int position) {

            id = entity.getId();


            long seconds = entity.getLengthSeconds();
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long remainingSeconds = seconds % 60;

            duration.setText(activity.getString(R.string.hhmmss,hours,minutes,remainingSeconds));
            String dateAsString = LocalDate.ofEpochDay(entity.getDate()).toString();

            date.setText(dateAsString);

            delete.setOnClickListener(v -> {
                viewModel.deleteById(id);
                notifyItemRemoved(position);
            });

            weather.observe(activity, weather -> {
                switch(weather) {
                    case SUN:
                        sunWeather.setColorFilter(Color.argb(0, 0, 0, 0));
                        snowWeather.setColorFilter(Color.argb(155, 0, 0, 0));
                        rainWeather.setColorFilter(Color.argb(155, 0, 0, 0));
                        break;
                    case RAIN:
                        sunWeather.setColorFilter(Color.argb(155, 0, 0, 0));
                        snowWeather.setColorFilter(Color.argb(155, 0, 0, 0));
                        rainWeather.setColorFilter(Color.argb(0, 0, 0, 0));
                        break;
                    case SNOW:
                        sunWeather.setColorFilter(Color.argb(155, 0, 0, 0));
                        snowWeather.setColorFilter(Color.argb(0, 0, 0, 0));
                        rainWeather.setColorFilter(Color.argb(155, 0, 0, 0));
                        break;
                }
            });

            positive.observe(activity, positive -> {
                if(positive) {
                    badMovement.setColorFilter(Color.argb(150,0,0,0));
                    goodMovement.setColorFilter(Color.argb(0,0,0,0));
                } else {
                    badMovement.setColorFilter(Color.argb(0,0,0,0));
                    goodMovement.setColorFilter(Color.argb(150,0,0,0));
                }
            });

            changed.observe(activity, changed -> {
                if(changed) {
                    discardChanges.setVisibility(View.VISIBLE);
                    saveChanges.setVisibility(View.VISIBLE);
                } else {
                    discardChanges.setVisibility(View.INVISIBLE);
                    saveChanges.setVisibility(View.INVISIBLE);
                }
            });

            description.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    changed.setValue(true);
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });

            description.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    changed.setValue(true);
                }
                @Override
                public void afterTextChanged(Editable s) {}
            });
            goodMovement.setOnClickListener(v -> {
                if(!positive.getValue()) {
                    changed.setValue(true);
                }
                positive.setValue(true);
            });

            badMovement.setOnClickListener(v -> {
                if(positive.getValue()) {
                    changed.setValue(true);
                }
                positive.setValue(false);
            });

            rainWeather.setOnClickListener(v -> {
                if(weather.getValue() != Weather.RAIN) {
                    changed.setValue(true);
                }
                weather.setValue(Weather.RAIN);
            });
            sunWeather.setOnClickListener(v -> {
                if(weather.getValue() != Weather.SUN) {
                    changed.setValue(true);
                }
                weather.setValue(Weather.SUN);
            });
            snowWeather.setOnClickListener(v -> {
                if(weather.getValue() != Weather.SNOW) {
                    changed.setValue(true);
                }
                weather.setValue(Weather.SNOW);
            });

            discardChanges.setOnClickListener(v -> {
                positive.setValue(entity.isPositive());
                weather.setValue(entity.getWeather());
                title.setText(entity.getTitle());
                description.setText(entity.getDescription());
                changed.setValue(false);
            });

            saveChanges.setOnClickListener(v -> {
                viewModel.saveChanges(id,positive.getValue(),
                        weather.getValue(),
                        title.getText().toString(),
                        description.getText().toString()
                        );

                changed.setValue(false);
            });

            title.setText(entity.getTitle());
            description.setText(entity.getDescription());
            distanceTravelled.setText(activity.getString(R.string.metres,entity.getDistance()));
            weather.setValue(entity.getWeather());
            positive.setValue(entity.isPositive());
            changed.setValue(false);
        }
    }
}
