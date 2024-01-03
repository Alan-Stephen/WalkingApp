package com.example.gpscw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MakeMarkerActivity extends AppCompatActivity {

    TextView notificationRangeLabel;
    SeekBar notificationRange;
    SeekBar notificationConsumptionTimer;
    EditText notificationDescription;
    EditText notificationTitle;
    SwitchCompat notificationConsumption;
    Button confirm;
    Button discard;
    TextView notificationConsumptionTimeLabel;
    MakeMarkerActivityViewModel viewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_marker);

        viewModel = new ViewModelProvider(this).get(MakeMarkerActivityViewModel.class);
        notificationRange = findViewById(R.id.slider);
        notificationDescription = findViewById(R.id.notificationDescription);
        notificationTitle = findViewById(R.id.notificationTitle);
        confirm = findViewById(R.id.confirm);
        discard = findViewById(R.id.discard);
        notificationRangeLabel = findViewById(R.id.notificationRangeLabel);
        notificationConsumption = findViewById(R.id.notificationConsumptionSwitch);
        notificationConsumptionTimer = findViewById(R.id.notificationTimeSlider);
        notificationConsumptionTimeLabel = findViewById(R.id.notificationConsumptionTimeLabel);

        notificationTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setTitle(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        notificationDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setDescrpition(s.toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        viewModel.getRemoveAfterNotify().observe(this, aBoolean -> {
                    if (aBoolean) {
                        notificationConsumptionTimer.setVisibility(View.INVISIBLE);
                        notificationConsumptionTimeLabel.setVisibility(View.INVISIBLE);
                    } else {
                        notificationConsumptionTimer.setVisibility(View.VISIBLE);
                        notificationConsumptionTimeLabel.setVisibility(View.VISIBLE);
                    }
                });

        viewModel.getNotificationRange().observe(this, integer ->
                notificationRangeLabel.setText(getString(R.string.notificationRangeText,integer)));

        viewModel.getNotificationConsumptionTime().observe(this, integer ->
                notificationConsumptionTimeLabel.setText(getString(R.string.notificationTimerLabelText,integer)));



        notificationConsumption.setOnCheckedChangeListener((buttonView, isChecked) -> {
            viewModel.setRemoveAfterNotify(isChecked);
        });

        notificationConsumptionTimer.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewModel.setNotificationConsumptionTime(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        notificationRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewModel.setNotificationRange(progress);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        discard.setOnClickListener(v -> finish());
        confirm.setOnClickListener(v -> {
            double lat = getIntent().getDoubleExtra("lat",0.0);
            double lon = getIntent().getDoubleExtra("lon",0.0);

            if(viewModel.getTitle().equals("")) {
                Toast.makeText(this,"Title can't be empty",Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.createLocationNotification(lat,lon);
            finish();
        });
    }
}