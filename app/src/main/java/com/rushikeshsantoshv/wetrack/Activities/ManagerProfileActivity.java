package com.rushikeshsantoshv.wetrack.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ManagerProfileActivity extends AppCompatActivity {

    Functions functions= new Functions();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Intent intent;
    String emp_path;

    TextInputLayout managerprofile_emp_name, managerprofile_phone;
    Spinner managerprofile_gender_dropdown;

    ImageButton managerprofile_back_btn;

    RelativeLayout managerprofile_mainlay;

    TextView managerprofile_edit_btn, managerprofile_update_cancelbtn, managerprofile_update_btn;

    String manager_name, manager_contact, manager_gender;
    ArrayAdapter genderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_profile);
        functions.coloredStatusBarDarkTextDesign(ManagerProfileActivity.this, R.color.maincolor_light, R.color.white);

        managerProfileInits();

        intent= getIntent();
        if (intent != null && intent.getStringExtra("manager_reference_id") != null
                && !intent.getStringExtra("manager_reference_id").equals("")) {
            emp_path = intent.getStringExtra("manager_reference_id");
        } else {
            emp_path = "Managers/sampleuser";
        }

        managerprofile_back_btn.setOnClickListener(v->{
            startActivity(new Intent(getApplicationContext(), ManagersListActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        genderAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,getGenders()){

            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view= super.getView(position, convertView, parent);
                if(position==0) ((TextView) view).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                else  ((TextView) view).setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView textview = (TextView) view;
                if (position == 0) textview.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
                else textview.setTextColor(Color.BLACK);
                return view;
            }
        };
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        managerprofile_gender_dropdown.setAdapter(genderAdapter);

        if(functions.checkInternetConnection(this)){
            db.collection("Managers")
                    .document(db.document(emp_path).getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            manager_name= task.getResult().getString("manager_name");
                            manager_contact= task.getResult().getString("manager_contact");
                            manager_gender= task.getResult().getString("manager_gender");
                            managerprofile_emp_name.getEditText().setText(manager_name);
                            managerprofile_phone.getEditText().setText(manager_contact);
                            managerprofile_gender_dropdown.setSelection(getGenders().indexOf(manager_gender));
                        }
                    });
        }
        else functions.no_internet_dialog(this, false);

        managerprofile_edit_btn.setOnClickListener(v->{
            managerprofile_emp_name.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            managerprofile_phone.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.black)));
            managerprofile_emp_name.setClickable(true);
            managerprofile_emp_name.setEnabled(true);
            managerprofile_phone.setClickable(true);
            managerprofile_phone.setEnabled(true);
            managerprofile_gender_dropdown.setClickable(true);
            managerprofile_gender_dropdown.setEnabled(true);
            managerprofile_update_btn.setVisibility(View.VISIBLE);
            managerprofile_update_cancelbtn.setVisibility(View.VISIBLE);
            managerprofile_edit_btn.setVisibility(View.GONE);
        });

        managerprofile_update_cancelbtn.setOnClickListener(v->{
            Dialog dialog = functions.createDialogBox(ManagerProfileActivity.this, R.layout.loading_dialog, false);
            dialog.show();
            managerprofile_emp_name.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
            managerprofile_phone.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
            managerprofile_emp_name.setClickable(false);
            managerprofile_emp_name.setEnabled(false);
            managerprofile_phone.setClickable(false);
            managerprofile_phone.setEnabled(false);
            managerprofile_gender_dropdown.setClickable(false);
            managerprofile_gender_dropdown.setEnabled(false);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                Intent intent1= new Intent(getApplicationContext(),ManagerProfileActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("emp_reference_id", emp_path);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 500);
        });

        managerprofile_update_btn.setOnClickListener(v->{
            String curr_emp_name = managerprofile_emp_name.getEditText().getText().toString();
            String curr_emp_contact = managerprofile_phone.getEditText().getText().toString();
            String selected_gender= (String) managerprofile_gender_dropdown.getSelectedItem();

            if (!curr_emp_name.equals(manager_name) || !curr_emp_contact.equals(manager_contact) || !manager_gender.equals(selected_gender)) {
                Dialog dialog = functions.createDialogBox(ManagerProfileActivity.this, R.layout.loading_dialog, false);
                dialog.show();

                Map<String, Object> data = new HashMap<>();
                if (!curr_emp_name.equals(manager_name)) {
                    data.put("manager_name", curr_emp_name);
                }
                if (!curr_emp_contact.equals(manager_contact)) {
                    data.put("manager_contact", curr_emp_contact);
                }
                if(!manager_gender.equals(selected_gender)){
                    data.put("manager_gender", selected_gender);
                }

                if(functions.checkInternetConnection(this)){
                    db.collection("Managers")
                            .document(db.document(emp_path).getId())
                            .update(data)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    managerprofile_emp_name.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
                                    managerprofile_phone.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
                                    managerprofile_emp_name.setClickable(false);
                                    managerprofile_emp_name.setEnabled(false);
                                    managerprofile_phone.setClickable(false);
                                    managerprofile_phone.setEnabled(false);
                                    managerprofile_gender_dropdown.setClickable(false);
                                    managerprofile_gender_dropdown.setEnabled(false);
                                    managerprofile_edit_btn.setVisibility(View.VISIBLE);
                                    managerprofile_update_btn.setVisibility(View.GONE);
                                    managerprofile_update_cancelbtn.setVisibility(View.GONE);

                                    if(functions.checkInternetConnection(this)){
                                        if (data.containsKey("manager_contact")) {
                                            db.collection("UserLogin")
                                                    .whereEqualTo("user_data_reference", db.document(emp_path))
                                                    .get()
                                                    .addOnCompleteListener(task12 -> {
                                                        if (task12.isSuccessful()) {
                                                            if (task12.getResult().getDocuments().size() > 0) {
                                                                DocumentSnapshot doc = task12.getResult().getDocuments().get(0);
                                                                db.collection("UserLogin")
                                                                        .document(doc.getId())
                                                                        .delete()
                                                                        .addOnCompleteListener(task13 -> {
                                                                            dialog.dismiss();
                                                                            smsPermissionRequestResponse(manager_contact);
                                                                        });
                                                            }
                                                            else{
                                                                dialog.dismiss();
                                                                Snackbar.make(managerprofile_mainlay, "Managers Details updated successfully.", Snackbar.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                        else{
                                                            dialog.cancel();
                                                            Snackbar.make(managerprofile_mainlay,"Unable to update details !! Please try again", Snackbar.LENGTH_SHORT).show();
                                                            String msg= task12.getException().getLocalizedMessage()!=null ? task12.getException().getLocalizedMessage() : "No Error !!";
                                                            Log.e("firebase_error","The Error is : "+msg);
                                                        }
                                                    });
                                        }
                                        else{
                                            dialog.dismiss();
                                            Snackbar.make(managerprofile_mainlay, "Managers Details updated successfully.", Snackbar.LENGTH_SHORT).show();
                                        }
                                    }
                                    else {
                                        dialog.dismiss();
                                        functions.no_internet_dialog(this, false);
                                    }
                                } else {
                                    dialog.dismiss();
                                    Snackbar.make(managerprofile_mainlay, "Unable to update the Managers details !! Please try again", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
                else functions.no_internet_dialog(this, false);
            }
            else {
                Dialog dialog = functions.createDialogBox(ManagerProfileActivity.this, R.layout.loading_dialog, false);
                dialog.show();
                managerprofile_emp_name.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
                managerprofile_phone.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
                managerprofile_emp_name.setClickable(false);
                managerprofile_emp_name.setEnabled(false);
                managerprofile_phone.setClickable(false);
                managerprofile_phone.setEnabled(false);
                managerprofile_gender_dropdown.setClickable(false);
                managerprofile_gender_dropdown.setEnabled(false);
                managerprofile_edit_btn.setVisibility(View.VISIBLE);
                managerprofile_update_btn.setVisibility(View.GONE);
                managerprofile_update_cancelbtn.setVisibility(View.GONE);
                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    Snackbar.make(managerprofile_mainlay, "No values updated in the Managers Details !!", Snackbar.LENGTH_SHORT).show();
                }, 1000);
            }
        });
    }

    private void smsPermissionRequestResponse(String emp_phoneno) {
        Dexter.withContext(ManagerProfileActivity.this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emp_phoneno, null, "Hello, this is WeTrack App. The company, have updated your phone number as requested by you. Please install WeTrack App and login to your account using "+manager_contact+" phone number.", null, null);
                        Snackbar.make(managerprofile_mainlay, "Manager Details updated successfully.", Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> Snackbar.make(managerprofile_mainlay, "SMS notification has been send to the employee about the updation of the contact number.", Snackbar.LENGTH_SHORT).show(),1500);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Snackbar.make(managerprofile_mainlay, "Unable to send SMS to the workers !! Please enable the SMS permission to enable this feature.", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @NonNull
    private ArrayList<String> getGenders(){
        ArrayList<String> arr= new ArrayList<>();
        arr.add("Select Gender");
        arr.add("Male");
        arr.add("Female");
        arr.add("Other");
        return arr;
    }

    private void managerProfileInits() {
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        db= FirebaseFirestore.getInstance();

        managerprofile_back_btn= findViewById(R.id.managerprofile_back_btn);
        managerprofile_emp_name= findViewById(R.id.managerprofile_emp_name);
        managerprofile_phone= findViewById(R.id.managerprofile_phone);
        managerprofile_gender_dropdown= findViewById(R.id.managerprofile_gender_dropdown);
        managerprofile_mainlay= findViewById(R.id.managerprofile_mainlay);

        managerprofile_edit_btn= findViewById(R.id.managerprofile_edit_btn);
        managerprofile_update_cancelbtn= findViewById(R.id.managerprofile_update_cancelbtn);
        managerprofile_update_btn= findViewById(R.id.managerprofile_update_btn);

        managerprofile_emp_name.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
        managerprofile_phone.getEditText().setTextColor(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
        managerprofile_emp_name.setClickable(false);
        managerprofile_emp_name.setEnabled(false);
        managerprofile_phone.setClickable(false);
        managerprofile_phone.setEnabled(false);
        managerprofile_gender_dropdown.setClickable(false);
        managerprofile_gender_dropdown.setEnabled(false);

        managerprofile_update_btn.setVisibility(View.GONE);
        managerprofile_update_cancelbtn.setVisibility(View.GONE);
        managerprofile_edit_btn.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), ManagersListActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}