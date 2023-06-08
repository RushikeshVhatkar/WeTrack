package com.rushikeshsantoshv.wetrack.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.Activities.AboutUsActivity;
import com.rushikeshsantoshv.wetrack.Activities.AppSettingsActivity;
import com.rushikeshsantoshv.wetrack.Activities.CompanyDetailsActivity;
import com.rushikeshsantoshv.wetrack.Activities.PersonalInfoActivity;
import com.rushikeshsantoshv.wetrack.Activities.SelectUserActivity;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class ProfileFragment extends Fragment {

    RelativeLayout profilefrag_mainlay;
    LinearLayout prof_profile_settings, prof_acc_settings;
    LinearLayout prof_app_settings, prof_aboutus, prof_faqs, prof_contactus, prof_feedback, prof_privacy;
    TextView prof_username, prof_joined;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    Functions functions= new Functions();


    public ProfileFragment() {}

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);

        profilefrag_mainlay= v.findViewById(R.id.profilefrag_mainlay);

        prof_profile_settings= v.findViewById(R.id.prof_profile_settings);
        prof_acc_settings= v.findViewById(R.id.prof_acc_settings);
        prof_app_settings= v.findViewById(R.id.prof_app_settings);
        prof_aboutus= v.findViewById(R.id.prof_aboutus);
        prof_faqs= v.findViewById(R.id.prof_faqs);
        prof_contactus= v.findViewById(R.id.prof_contactus);
        prof_feedback= v.findViewById(R.id.prof_feedback);
        prof_privacy= v.findViewById(R.id.prof_privacy);
        prof_username= v.findViewById(R.id.prof_username);
        prof_joined= v.findViewById(R.id.prof_joined);
        SimpleDateFormat sdf = new SimpleDateFormat("dd, MMMM, yyyy", Locale.getDefault());

        String created_date= new Functions().getSharedPrefsValue(getContext(), "user_data", "company_created", "string", sdf.format(new Date()));
        prof_joined.setText("Created on "+created_date);

        String ptype= new Functions().getSharedPrefsValue(getContext(),"user_data","ptype","string",null);
        if(ptype!=null){
            String pname;
            if(ptype.equals("Owners")){
                String smaplepname= new Functions().getSharedPrefsValue(getContext(),"user_data","full_name","string",null);
                pname= smaplepname!=null ? smaplepname : "Username";
            }
            else{
                String smaplepname= new Functions().getSharedPrefsValue(getContext(),"user_data","full_name","string",null);
                pname= smaplepname!=null ? smaplepname : "Username";
            }
            prof_username.setText(pname);
        }

        prof_profile_settings.setOnClickListener(v1-> startActivity(new Intent(getContext(), PersonalInfoActivity.class)));

        prof_acc_settings.setOnClickListener(v1-> startActivity(new Intent(getContext(), CompanyDetailsActivity.class)));

        prof_app_settings.setOnClickListener(v1-> startActivity(new Intent(getContext(), AppSettingsActivity.class)));

        prof_aboutus.setOnClickListener(v1-> startActivity(new Intent(getContext(), AboutUsActivity.class)));

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();

        return v;
    }


    private void verify() {
        if(functions.checkInternetConnection(getActivity())){
            FirebaseAuth.AuthStateListener mAuthStateListner = firebaseAuth -> {
                FirebaseUser mFirebaseuser = firebaseAuth.getCurrentUser();
                if (mFirebaseuser == null) {
                    Toast.makeText(getContext(), "You have already logged out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getContext(), SelectUserActivity.class));
                }
            };
        }
        else functions.no_internet_dialog(getActivity(), false);
    }

    private void accountLogoutFun(){
        if(functions.checkInternetConnection(getActivity())){
            new Functions().putSharedPrefsValue(getContext(), "user_data", "login_status", "boolean", false);
            verify();
            firebaseAuth.signOut();
//            new Functions().clearSharedPreferences(getContext(),"user_data");
//            new Functions().clearSharedPreferences(getContext(), "attend_frag");
            Snackbar.make(profilefrag_mainlay,"Logout Successful. Please login to an account.",Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> startActivity(new Intent(getContext(), SelectUserActivity.class)),1000);
        }
        else functions.no_internet_dialog(getActivity(), false);
    }

}