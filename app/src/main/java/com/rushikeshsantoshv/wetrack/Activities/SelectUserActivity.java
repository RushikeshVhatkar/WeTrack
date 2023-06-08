package com.rushikeshsantoshv.wetrack.Activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

public class SelectUserActivity extends AppCompatActivity {

    private LinearLayout selectuser_mainlay;
    private RadioButton owner_radio_btn, employee_radio_btn, manager_radio_btn;
    private Button selectuser_submitbtn;
    private RadioGroup select_radiogroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_user);

        new Functions().lightBackgroundStatusBarDesign(SelectUserActivity.this);


        selectuser_mainlay = findViewById(R.id.selectuser_mainlay);
        owner_radio_btn = findViewById(R.id.owner_radio_btn);
        employee_radio_btn = findViewById(R.id.employee_radio_btn);
        manager_radio_btn = findViewById(R.id.manager_radio_btn);
        selectuser_submitbtn = findViewById(R.id.selectuser_submitbtn);
        select_radiogroup = findViewById(R.id.select_radiogroup);

        select_radiogroup.setOnCheckedChangeListener((radioGroup, i) -> {

            Typeface light= ResourcesCompat.getFont(SelectUserActivity.this, R.font.poppins_light);
            Typeface semibold= ResourcesCompat.getFont(SelectUserActivity.this, R.font.poppins_semibold);

            if(owner_radio_btn== findViewById(i)){
                owner_radio_btn.setTypeface(semibold);
                employee_radio_btn.setTypeface(light);
                manager_radio_btn.setTypeface(light);
            }
            else if(manager_radio_btn== findViewById(i)){
                owner_radio_btn.setTypeface(light);
                employee_radio_btn.setTypeface(light);
                manager_radio_btn.setTypeface(semibold);
            }
            else if(employee_radio_btn== findViewById(i)){
                owner_radio_btn.setTypeface(light);
                employee_radio_btn.setTypeface(semibold);
                manager_radio_btn.setTypeface(light);
            }

            selectuser_submitbtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00A3FF")));
            selectuser_submitbtn.setTextColor(getResources().getColor(R.color.white));
            selectuser_submitbtn.setEnabled(true);
        });

        selectuser_submitbtn.setOnClickListener(v -> {
            int selected_user_id = select_radiogroup.getCheckedRadioButtonId();
            // int owner= R.id.owner_radio_btn;
            int manager = R.id.manager_radio_btn;
            int employee = R.id.employee_radio_btn;
            String selected_user = (selected_user_id == employee) ? "Employees" : ((selected_user_id == manager) ? "Managers" : "Owners");
            Intent intent = new Intent(getApplicationContext(), SigninActivity.class);
            intent.putExtra("selected_user", selected_user);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}