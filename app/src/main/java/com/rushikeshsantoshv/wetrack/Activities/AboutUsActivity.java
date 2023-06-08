package com.rushikeshsantoshv.wetrack.Activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        new Functions().coloredStatusBarDarkTextDesign(AboutUsActivity.this, R.color.maincolor_light, R.color.white);
        ImageButton abt_back_btn = findViewById(R.id.abt_back_btn);
        abt_back_btn.setOnClickListener(v -> finish());
    }
}