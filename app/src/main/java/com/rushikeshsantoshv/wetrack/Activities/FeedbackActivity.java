package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("SetTextI18n")
public class FeedbackActivity extends AppCompatActivity {

    ImageButton feedback_back_btn;
    TextInputLayout feedback_message;
    TextView feedback_submit_btn, feedback_message_limit;
    RelativeLayout feedback_mainlay;

    Functions functions= new Functions();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    String comp_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        new Functions().lightBackgroundStatusBarDesign(FeedbackActivity.this);

        feedback_back_btn= findViewById(R.id.feedback_back_btn);
        feedback_message= findViewById(R.id.feedback_message);
        feedback_message_limit= findViewById(R.id.feedback_message_limit);
        feedback_submit_btn= findViewById(R.id.feedback_submit_btn);
        feedback_mainlay= findViewById(R.id.feedback_mainlay);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        db= FirebaseFirestore.getInstance();
        comp_path = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);

        feedback_message_limit.setText("0 / 100 words");
        feedback_message_limit.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.my_red));
        feedback_message.getEditText().setFilters(new InputFilter.LengthFilter[]{new InputFilter.LengthFilter(500)});
        feedback_submit_btn.setClickable(false);
        feedback_submit_btn.setEnabled(false);
        feedback_submit_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));

        feedback_message.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fb_message_txt= feedback_message.getEditText().getText().toString();
                String[] words = fb_message_txt.split("\\s+");
                if(fb_message_txt.length() > 3){
                    if(words.length == 100){
                        feedback_message_limit.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.my_orange));
                    }
                    else{
                        feedback_submit_btn.setClickable(true);
                        feedback_submit_btn.setEnabled(true);
                        feedback_submit_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.maincolor)));
                        feedback_message_limit.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.my_green));
                    }
                }
                else{
                    feedback_submit_btn.setClickable(false);
                    feedback_submit_btn.setEnabled(false);
                    feedback_submit_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
                    feedback_message_limit.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.my_red));
                }

                feedback_message_limit.setText(words.length+" / 100 words");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        feedback_submit_btn.setOnClickListener(v->{
            if(functions.checkInternetConnection(this)){
                String fb_message_txt= feedback_message.getEditText().getText().toString();
                String user_ref= functions.getSharedPrefsValue(getApplicationContext(), "user_data","user_reference","string",null);

                Map<String, Object> feedback_data= new HashMap<>();
                feedback_data.put("company_reference", db.document(comp_path));
                feedback_data.put("message", fb_message_txt);
                feedback_data.put("timestamp", Timestamp.now());
                feedback_data.put("user_reference", user_ref!=null ? db.document(user_ref) : db.document("SampleCollection/SampleUser"));

                db.collection("UserFeedback")
                        .document()
                        .set(feedback_data)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                feedback_message_limit.setText("0 / 100 words");
                                feedback_message.getEditText().setText("");
                                Snackbar.make(feedback_mainlay, "Thank you for your feedback. Your feedback has been recorded successfully.", Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(this::finish, 1500);
                            }
                            else{
                                Snackbar.make(feedback_mainlay, "Unable to save feedback !! Please try again", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
            else functions.no_internet_dialog(this, false);
        });

        feedback_back_btn.setOnClickListener(v-> finish());

    }
}