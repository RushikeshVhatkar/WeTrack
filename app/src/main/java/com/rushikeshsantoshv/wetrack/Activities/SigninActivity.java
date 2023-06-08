package com.rushikeshsantoshv.wetrack.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.Fragments.SigninFragment;
import com.rushikeshsantoshv.wetrack.R;

public class SigninActivity extends AppCompatActivity {

    private LinearLayout signin_maincontainer;
    private boolean isUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseAuth.AuthStateListener mAuthStateListner;

    @Override
    protected void onStart() {
        super.onStart();
        if (isUser) {
            firebaseAuth.addAuthStateListener(mAuthStateListner);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        new Functions().lightBackgroundStatusBarDesign(SigninActivity.this);
        signin_maincontainer = findViewById(R.id.signin_maincontainer);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        isUser = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "login_status", "boolean", false);
        if (isUser) verify();

        Intent intent = getIntent();
        String selected_user = intent.getStringExtra("selected_user");
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.signin_maincontainer, new SigninFragment(selected_user));
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void verify() {
        mAuthStateListner = firebaseAuth -> {
            FirebaseUser mFirebaseuser = firebaseAuth.getCurrentUser();
            if (mFirebaseuser != null) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        };
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (firebaseUser != null) {
            firebaseAuth.signOut();
        }
    }
}