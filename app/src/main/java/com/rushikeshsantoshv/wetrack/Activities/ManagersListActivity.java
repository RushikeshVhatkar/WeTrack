package com.rushikeshsantoshv.wetrack.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rushikeshsantoshv.wetrack.Adapters.AddManagersAdapter;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.DataModels.ManagerModel;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManagersListActivity extends AppCompatActivity {

    Functions functions = new Functions();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    AddManagersAdapter addManagersAdapter;

    RelativeLayout managerlist_mainlay;
    Toolbar managerlist_toolbar;
    ImageButton managerlist_back_btn, managerlist_searchemp_btn;
    TextView managerlist_title;
    FloatingActionButton managerlist_addemp_btn;
    LinearLayout managerlist_emptylist_lay;
    RecyclerView managerlist_rec;
    ProgressBar managerlist_progressbar;

    int pagecheck = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_managers_list);
        functions.coloredStatusBarDarkTextDesign(ManagersListActivity.this, R.color.maincolor_light, R.color.white);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        managerlist_mainlay = findViewById(R.id.managerlist_mainlay);
        managerlist_toolbar = findViewById(R.id.managerlist_toolbar);
        managerlist_back_btn = findViewById(R.id.managerlist_back_btn);
        managerlist_searchemp_btn = findViewById(R.id.managerlist_searchemp_btn);
        managerlist_title = findViewById(R.id.managerlist_title);
        managerlist_addemp_btn = findViewById(R.id.managerlist_addemp_btn);
        managerlist_emptylist_lay = findViewById(R.id.managerlist_emptylist_lay);
        managerlist_rec = findViewById(R.id.managerlist_rec);
        managerlist_progressbar = findViewById(R.id.managerlist_progressbar);

        Intent page_data = getIntent();
        pagecheck = page_data.getIntExtra("pagecheck", 0);

        View lay= managerlist_mainlay;

        managerlist_addemp_btn.setOnClickListener(v -> {
            BottomSheetDialog addmanager_btm_dialog = new BottomSheetDialog(this);
            addmanager_btm_dialog.setContentView(R.layout.addworkers_btm_dialog);
            addmanager_btm_dialog.setCanceledOnTouchOutside(true);
            addmanager_btm_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            TextInputLayout aw_btm_name = addmanager_btm_dialog.findViewById(R.id.aw_btm_name);
            TextInputLayout aw_btm_phone = addmanager_btm_dialog.findViewById(R.id.aw_btm_phone);
            TextView aw_btm_heading = addmanager_btm_dialog.findViewById(R.id.aw_btm_heading);
            TextInputLayout aw_btm_advancedloans = addmanager_btm_dialog.findViewById(R.id.aw_btm_advancedloans);
            SwitchCompat aw_btm_active_user_check = addmanager_btm_dialog.findViewById(R.id.aw_btm_active_user_check);
            Spinner aw_btm_managers_dropdown = addmanager_btm_dialog.findViewById(R.id.aw_btm_managers_dropdown);
            TextView aw_btm_managers_heading= addmanager_btm_dialog.findViewById(R.id.aw_btm_managers_heading);
            RadioGroup aw_btm_genders= addmanager_btm_dialog.findViewById(R.id.aw_btm_genders);
            aw_btm_advancedloans.setVisibility(View.GONE);
            Button aw_btm_add_btn = addmanager_btm_dialog.findViewById(R.id.aw_btm_add_btn);

            aw_btm_add_btn.setText("Add Manager");
            aw_btm_heading.setText("Add a new Manager");
            aw_btm_name.setHint("Manager Name");
            aw_btm_active_user_check.setText("Active Manager");
            aw_btm_managers_heading.setVisibility(View.GONE);
            aw_btm_managers_dropdown.setVisibility(View.GONE);

            aw_btm_add_btn.setEnabled(false);
            aw_btm_add_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));

            aw_btm_name.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    String emp_name = aw_btm_name.getEditText().getText().toString();
                    String emp_contact = aw_btm_phone.getEditText().getText().toString();
                    String fullname_regx = "^[a-zA-Z\\s]*$";

                    if (emp_name.length() <= 2) {
                        aw_btm_name.setError("Enter a name with more than 2 characters length !!");
                        aw_btm_name.requestFocus();
                        functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                    } else if (!emp_name.matches(fullname_regx)) {
                        aw_btm_name.setError("Please enter a valid name !!");
                        aw_btm_name.requestFocus();
                        functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                    } else
                        aw_btm_name.setError(null);

                    if (emp_contact.length() < 10)
                        functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                    else
                        functions.updateButtonStyle("#00A3FF", true, aw_btm_add_btn);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            aw_btm_phone.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    String emp_name = aw_btm_name.getEditText().getText().toString();
                    String emp_contact = aw_btm_phone.getEditText().getText().toString();
                    String fullname_regx = "^[a-zA-Z\\s]*$";

                    if (emp_contact.length() < 10) {
                        aw_btm_phone.setError("Enter a valid contact number !!");
                        aw_btm_phone.requestFocus();
                        functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                    } else
                        aw_btm_phone.setError(null);

                    if (emp_name.length() <= 2)
                        functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                    else if (!emp_name.matches(fullname_regx))
                        functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                    else
                        functions.updateButtonStyle("#00A3FF", true, aw_btm_add_btn);
                }

                @Override
                public void afterTextChanged(Editable editable) {
                }
            });

            aw_btm_add_btn.setOnClickListener(v2 -> {
                addmanager_btm_dialog.dismiss();
                String emp_name = aw_btm_name.getEditText().getText().toString();
                String emp_contact = aw_btm_phone.getEditText().getText().toString();
                String company_path_val = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
                String selected_gender=  aw_btm_genders.getCheckedRadioButtonId()==R.id.aw_btm_gender_male ? "Male" :
                        (aw_btm_genders.getCheckedRadioButtonId()==R.id.aw_btm_gender_female ? "Female" : "Others");

                if (company_path_val != null) {
                    DocumentReference company_path = db.document(company_path_val);
                    Map<String, Object> worker_data = new HashMap<>();
                    worker_data.put("manager_name", emp_name);
                    worker_data.put("manager_contact", "+91" + emp_contact);
                    worker_data.put("manager_timestamp", Timestamp.now());
                    worker_data.put("manager_gender",selected_gender);
                    worker_data.put("manager_status", aw_btm_active_user_check.isChecked());
                    worker_data.put("company_path", company_path);

                    if(functions.checkInternetConnection(this)){
                        db.collection("Managers")
                                .add(worker_data)
                                .addOnCompleteListener(task -> {
                                    managerlist_progressbar.setVisibility(View.VISIBLE);
                                    if (task.isSuccessful()) {
                                        smsPermissionRequestResponse("+91" + emp_contact);
                                        Snackbar.make(managerlist_mainlay, "New manager added successfully.", Snackbar.LENGTH_SHORT).show();
                                        loadManagersList();
                                    } else {
                                        Snackbar.make(managerlist_mainlay, "Unable to add an manager !! Please try again.", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else functions.no_internet_dialog(this, false);

                } else {
                    Snackbar.make(managerlist_mainlay, "Unable to add new manager for the time being. Please try later !!", Snackbar.LENGTH_SHORT).show();
                }
            });

            addmanager_btm_dialog.show();
        });

        managerlist_back_btn.setOnClickListener(v -> finish());

        loadManagersList();

    }



    @SuppressLint("NotifyDataSetChanged")
    public void loadManagersList() {
        String company_path_val = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        if (company_path_val != null) {
            Log.e("company_path", "Company Path is - " + company_path_val);
            if(functions.checkInternetConnection(this)){
                db.collection("Managers")
                        .whereEqualTo("company_path", db.document(company_path_val))
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    managerlist_emptylist_lay.setVisibility(View.GONE);
                                    managerlist_rec.setVisibility(View.VISIBLE);
                                    List<DocumentSnapshot> managerlist = task.getResult().getDocuments();
                                    ArrayList<ManagerModel> managerlist_arr = new ArrayList<>();
                                    for (DocumentSnapshot manager : managerlist) {
                                        managerlist_arr.add(new ManagerModel(manager.getDocumentReference("company_path"),
                                                manager.getString("manager_name"), manager.getString("manager_contact"),
                                                manager.getReference().getPath(), manager.getBoolean("manager_status")));
                                    }
                                    managerlist_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    addManagersAdapter = new AddManagersAdapter(getApplicationContext(), managerlist_arr, pagecheck, ManagersListActivity.this);
                                    managerlist_rec.setAdapter(addManagersAdapter);
                                    addManagersAdapter.notifyDataSetChanged();
                                    managerlist_progressbar.setVisibility(View.GONE);
                                } else {
                                    managerlist_progressbar.setVisibility(View.GONE);
                                    managerlist_emptylist_lay.setVisibility(View.VISIBLE);
                                    managerlist_rec.setVisibility(View.GONE);
                                }
                            } else {
                                Snackbar.make(managerlist_mainlay, "Unbale to retrieve data from the database !! Please try again !!", Snackbar.LENGTH_SHORT).show();
                                managerlist_progressbar.setVisibility(View.GONE);
                                managerlist_emptylist_lay.setVisibility(View.VISIBLE);
                                managerlist_rec.setVisibility(View.GONE);
                            }
                        });
            }
            else functions.no_internet_dialog(this, true);
        } else {
            Snackbar.make(managerlist_mainlay, "Unable to find the data due to absence of company path !! Please try again !!", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void smsPermissionRequestResponse(String emp_phoneno) {
        Dexter.withContext(ManagersListActivity.this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emp_phoneno, null, "Hello, this is WeTrack App. The company, have added you as their manager. Please install WeTrack App and login to your account using your phone number.", null, null);
                        Snackbar.make(managerlist_mainlay, "SMS notification has been send to the employee successfully.", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Snackbar.make(managerlist_mainlay, "Unable to send SMS to the workers !! Please enable the SMS permission to enable this feature.", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }
}