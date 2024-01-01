package com.example.gpscw2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MakeMarkerActivity extends AppCompatActivity {

    TextView notificationRangeLabel;
    SeekBar notificationRange;
    EditText notificationDescription;
    EditText notificationTitle;

    Button confirm;
    Button discard;

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
        notificationRangeLabel.setText(getString(R.string.notificationRangeText,notificationRange.getProgress()));


        notificationRange.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                notificationRangeLabel.setText(getString(R.string.notificationRangeText,progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        discard.setOnClickListener(v -> finish());
        confirm.setOnClickListener(v -> {
            double lat = getIntent().getDoubleExtra("lat",0.0);
            double lon = getIntent().getDoubleExtra("lon",0.0);

            String title = notificationTitle.getText().toString();
            String description = notificationDescription.getText().toString();

            if(title.equals("")) {
                Toast.makeText(this,"Title can't be empty",Toast.LENGTH_SHORT).show();
                return;
            }
            viewModel.insert(new LocationNotificationEntity(lat,lon,notificationRange.getProgress(),title,description));
            finish();
        });
    }
}