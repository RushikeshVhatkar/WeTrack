package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

public class PersonalInfoActivity extends AppCompatActivity {

    TextView mprofile_fullname, mprofile_emailid, mprofile_contactno;
    TextView mprofile_comp_name, mprofile_comp_location, mprofile_comp_contactno;
    LinearLayout mprofile_emailid_lay;
    FloatingActionButton mprofile_edit_btn;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    AppBarLayout mprofile_appbarlay;
    ImageButton mprofile_back_btn;
    TextView mprofile_toolbar_title;
    Toolbar mprofile_toolbar;
    FirebaseFirestore db;
    Functions functions= new Functions();
    String curr_user;

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);

        mprofile_fullname= findViewById(R.id.mprofile_fullname);
        mprofile_emailid= findViewById(R.id.mprofile_emailid);
        mprofile_contactno= findViewById(R.id.mprofile_contactno);
        mprofile_comp_name= findViewById(R.id.mprofile_comp_name);
        mprofile_comp_location= findViewById(R.id.mprofile_comp_location);
        mprofile_comp_contactno= findViewById(R.id.mprofile_comp_contactno);
        mprofile_back_btn= findViewById(R.id.mprofile_back_btn);
        mprofile_toolbar= findViewById(R.id.mprofile_toolbar);
        mprofile_toolbar_title= findViewById(R.id.mprofile_toolbar_title);
        mprofile_emailid_lay= findViewById(R.id.mprofile_emailid_lay);
        mprofile_appbarlay= findViewById(R.id.mprofile_appbarlay);
        mprofile_edit_btn= findViewById(R.id.mprofile_edit_btn);

        mprofile_back_btn.setOnClickListener(v-> {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        db= FirebaseFirestore.getInstance();
        boolean log_status= functions.getSharedPrefsValue(getApplicationContext(),"user_data","login_status","boolean",false);
        curr_user= functions.getSharedPrefsValue(getApplicationContext(), "user_data", "ptype", "string",null);

        if(log_status && curr_user!=null){
            if(curr_user.equals("Owners")){
                mprofile_edit_btn.setVisibility(View.VISIBLE);
                mprofile_fullname.setText(functions.getSharedPrefsValue(getApplicationContext(), "user_data", "full_name", "string", "Name"));
                String phone=functions.getSharedPrefsValue(getApplicationContext(), "user_data", "contact_num", "string", "None");
                mprofile_contactno.setText(phone);
                mprofile_emailid.setText(functions.getSharedPrefsValue(getApplicationContext(), "user_data", "email_id", "string", "None"));
                displayCompanyFullDetails();
            }
            else{
                mprofile_edit_btn.setVisibility(View.GONE);
                mprofile_emailid_lay.setVisibility(View.GONE);
                mprofile_fullname.setText(functions.getSharedPrefsValue(getApplicationContext(),"user_data","full_name","string","None"));
                String contact= functions.getSharedPrefsValue(getApplicationContext(),"user_data","contact_num","string","None");
                mprofile_contactno.setText(contact);
                displayCompanyFullDetails();
            }
        }

        mprofile_appbarlay.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {

            if (Math.abs(verticalOffset) == mprofile_appbarlay.getTotalScrollRange()) {
                mprofile_toolbar.setBackground(getDrawable(R.color.heading_no));
                mprofile_toolbar.setVisibility(View.VISIBLE);
                mprofile_toolbar_title.setVisibility(View.VISIBLE);
                mprofile_toolbar_title.setText("My Profile");
            } else {
                mprofile_toolbar.setBackground(getDrawable(android.R.color.transparent));
                mprofile_toolbar_title.setVisibility(View.GONE);
                mprofile_toolbar_title.setText("");
            }
        });

        mprofile_edit_btn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), EditCompanyProfileActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

    private void displayCompanyFullDetails(){
        String comp_path= functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string",null);
        if(comp_path!=null){
            if(functions.checkInternetConnection(this)){
                db.document(comp_path)
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                mprofile_comp_name.setText(task.getResult().getString("company_name"));
                                mprofile_comp_location.setText(task.getResult().getString("company_location"));
                                mprofile_comp_contactno.setText(task.getResult().getString("company_contactnum"));
                            }
                            else{
                                displayNoDataMessages();
                            }
                        });
            }
            else functions.no_internet_dialog(this, true);
        }
        else{
            displayNoDataMessages();
        }
    }

    private void displayNoDataMessages(){
        mprofile_comp_name.setText("No Data");
        mprofile_comp_location.setText("No Data");
        mprofile_comp_contactno.setText("No Data");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}