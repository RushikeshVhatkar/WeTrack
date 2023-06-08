package com.rushikeshsantoshv.wetrack.Activities;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.Fragments.UpdatePhoneFragment;
import com.rushikeshsantoshv.wetrack.R;

public class UpdatePhoneNumberActivity extends AppCompatActivity {

    ImageButton upn_close_btn;
    LinearLayout upn_fragcontainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_phone_number);

        new Functions().lightBackgroundStatusBarDesign(UpdatePhoneNumberActivity.this);

        upn_close_btn= findViewById(R.id.upn_close_btn);
        upn_fragcontainer= findViewById(R.id.upn_fragcontainer);

        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.upn_fragcontainer, new UpdatePhoneFragment());
        transaction.commit();

        upn_close_btn.setOnClickListener(v-> finish());
    }
}