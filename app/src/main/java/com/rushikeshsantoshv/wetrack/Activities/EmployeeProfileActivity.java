package com.rushikeshsantoshv.wetrack.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.DataModels.InputFilterMinMax;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EmployeeProfileActivity extends AppCompatActivity {

    Functions functions = new Functions();

    TextInputLayout empprofile_emp_name, empprofile_phone;
    ImageButton empprofile_back_btn, empprofile_month_perform_infobtn;
    SwitchCompat empprofile_active_user_check;
    RelativeLayout empprofile_mainlay;
    TextView empprofile_edit_btn, empprofile_update_cancelbtn, empprofile_update_btn;
    Spinner empprofile_gender_dropdown, empprofile_assign_manager_dropdown;

    TextView empprofile_arrears_val, empprofile_advance_val;
    Button empprofile_addloanbtn, empprofile_repayloanbtn;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Intent intent;
    String emp_name, emp_contact, emp_path, comp_path;
    boolean active_check;
    Double advance_val, arrears_val;
    String emp_gender;
    DocumentReference emp_manager_ref;
    String emp_manager_name;
    String user;

    ArrayAdapter genderAdapter, managerAdapter;
    ArrayList<String> gender_arr= new ArrayList<>();
    ArrayList<String> manager_names= new ArrayList<>();
    ArrayList<DocumentReference> manager_ref= new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_profile);

        functions.coloredStatusBarDarkTextDesign(EmployeeProfileActivity.this, R.color.maincolor_light, R.color.white);

        if(functions.checkInternetConnection(this)){
            employeeProfileInits();
        }
        else functions.no_internet_dialog(this, true);

        empprofile_back_btn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), EmployeesListActivity.class);
            intent.putExtra("pagecheck", 0);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        if (intent != null && intent.getStringExtra("emp_reference_id") != null
                && !intent.getStringExtra("emp_reference_id").equals("")) {
            emp_path = intent.getStringExtra("emp_reference_id");
        } else {
            emp_path = "Employees/sampleuser";
        }

        empprofile_edit_btn.setOnClickListener(v -> {
            empprofile_update_btn.setVisibility(View.VISIBLE);
            empprofile_update_cancelbtn.setVisibility(View.VISIBLE);
            empprofile_edit_btn.setVisibility(View.GONE);
            empprofile_phone.setClickable(true);
            empprofile_phone.setEnabled(true);
            empprofile_emp_name.setClickable(true);
            empprofile_emp_name.setEnabled(true);
            empprofile_active_user_check.setClickable(true);
            empprofile_active_user_check.setEnabled(true);
            empprofile_gender_dropdown.setClickable(true);
            empprofile_gender_dropdown.setEnabled(true);
            empprofile_assign_manager_dropdown.setClickable(true);
            empprofile_assign_manager_dropdown.setEnabled(true);
        });

        empprofile_update_cancelbtn.setOnClickListener(v -> {
            Dialog dialog = functions.createDialogBox(EmployeeProfileActivity.this, R.layout.loading_dialog, false);
            dialog.show();
            empprofile_phone.setClickable(false);
            empprofile_phone.setEnabled(false);
            empprofile_emp_name.setClickable(false);
            empprofile_emp_name.setEnabled(false);
            empprofile_active_user_check.setClickable(false);
            empprofile_active_user_check.setEnabled(false);
            empprofile_gender_dropdown.setClickable(false);
            empprofile_gender_dropdown.setEnabled(false);
            empprofile_assign_manager_dropdown.setClickable(false);
            empprofile_assign_manager_dropdown.setEnabled(false);
            new Handler().postDelayed(() -> {
                dialog.dismiss();
                Intent intent1= new Intent(getApplicationContext(),EmployeeProfileActivity.class);
                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent1.putExtra("emp_reference_id", emp_path);
                startActivity(intent1);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }, 500);
        });

        empprofile_update_btn.setOnClickListener(v -> {
            String curr_emp_name = empprofile_emp_name.getEditText().getText().toString();
            String curr_emp_contact = empprofile_phone.getEditText().getText().toString();
            boolean curr_active_check = empprofile_active_user_check.isChecked();
            String selected_manager_name= (String) empprofile_assign_manager_dropdown.getSelectedItem();
            String selected_gender= (String) empprofile_gender_dropdown.getSelectedItem();

            if (!curr_emp_name.equals(emp_name) || !curr_emp_contact.equals(emp_contact) || curr_active_check != active_check
                    || !selected_manager_name.equals(emp_manager_name) || !emp_gender.equals(selected_gender)) {
                Dialog dialog = functions.createDialogBox(EmployeeProfileActivity.this, R.layout.loading_dialog, false);
                dialog.show();

                Map<String, Object> data = new HashMap<>();
                if (!curr_emp_name.equals(emp_name)) {
                    data.put("emp_name", curr_emp_name);
                }
                if (!curr_emp_contact.equals(emp_contact)) {
                    data.put("emp_contact", curr_emp_contact);
                }
                if (curr_active_check != active_check) {
                    data.put("emp_status", curr_active_check);
                }
                if(!selected_manager_name.equals(emp_manager_name)){
                    data.put("emp_manager_reference",manager_ref.get(manager_names.indexOf(empprofile_assign_manager_dropdown.getSelectedItem())));
                }
                if(!emp_gender.equals(selected_gender)){
                    data.put("emp_gender", gender_arr.get(gender_arr.indexOf(empprofile_gender_dropdown.getSelectedItem())));
                }

                if(functions.checkInternetConnection(this)){
                    db.collection("Employees")
                            .document(db.document(emp_path).getId())
                            .update(data)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    empprofile_phone.setClickable(false);
                                    empprofile_phone.setEnabled(false);
                                    empprofile_emp_name.setClickable(false);
                                    empprofile_emp_name.setEnabled(false);
                                    empprofile_active_user_check.setClickable(false);
                                    empprofile_active_user_check.setEnabled(false);
                                    empprofile_assign_manager_dropdown.setEnabled(false);
                                    empprofile_assign_manager_dropdown.setClickable(false);
                                    empprofile_gender_dropdown.setEnabled(false);
                                    empprofile_gender_dropdown.setClickable(false);
                                    empprofile_edit_btn.setVisibility(View.VISIBLE);
                                    empprofile_update_btn.setVisibility(View.GONE);
                                    empprofile_update_cancelbtn.setVisibility(View.GONE);
                                    if (data.containsKey("emp_contact")) {
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
                                                                        smsPermissionRequestResponse(emp_contact);
                                                                    });
                                                        }
                                                        else{
                                                            dialog.dismiss();
                                                            Snackbar.make(empprofile_mainlay, "Employee Details updated successfully.", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                    else{
                                                        dialog.cancel();
                                                        Snackbar.make(empprofile_mainlay,"Unable to update details !! Please try again", Snackbar.LENGTH_SHORT).show();
                                                        String msg= task12.getException().getLocalizedMessage()!=null ? task12.getException().getLocalizedMessage() : "No Error !!";
                                                        Log.e("firebase_error","The Error is : "+msg);
                                                    }
                                                });
                                    }
                                    else{
                                        dialog.dismiss();
                                        Snackbar.make(empprofile_mainlay, "Employee Details updated successfully.", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    dialog.dismiss();
                                    Snackbar.make(empprofile_mainlay, "Unable to update the employee details !! Please try again", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                }
                else functions.no_internet_dialog(this, false);

            }
            else {
                Dialog dialog = functions.createDialogBox(EmployeeProfileActivity.this, R.layout.loading_dialog, false);
                dialog.show();
                empprofile_phone.setClickable(false);
                empprofile_phone.setEnabled(false);
                empprofile_emp_name.setClickable(false);
                empprofile_emp_name.setEnabled(false);
                empprofile_active_user_check.setClickable(false);
                empprofile_active_user_check.setEnabled(false);
                empprofile_assign_manager_dropdown.setEnabled(false);
                empprofile_assign_manager_dropdown.setClickable(false);
                empprofile_gender_dropdown.setEnabled(false);
                empprofile_gender_dropdown.setClickable(false);
                empprofile_edit_btn.setVisibility(View.VISIBLE);
                empprofile_update_btn.setVisibility(View.GONE);
                empprofile_update_cancelbtn.setVisibility(View.GONE);

                new Handler().postDelayed(() -> {
                    dialog.dismiss();
                    Snackbar.make(empprofile_mainlay, "No values updated in the Employee Details !!", Snackbar.LENGTH_SHORT).show();
                }, 1000);
            }
        });

    }

    private void employeeProfileInits() {
        empprofile_emp_name = findViewById(R.id.empprofile_emp_name);
        empprofile_phone = findViewById(R.id.empprofile_phone);

        empprofile_arrears_val = findViewById(R.id.empprofile_arrears_val);
        empprofile_advance_val = findViewById(R.id.empprofile_advance_val);
        empprofile_addloanbtn = findViewById(R.id.empprofile_addloanbtn);
        empprofile_repayloanbtn = findViewById(R.id.empprofile_repayloanbtn);
        empprofile_mainlay = findViewById(R.id.empprofile_mainlay);

        empprofile_edit_btn = findViewById(R.id.empprofile_edit_btn);
        empprofile_update_cancelbtn = findViewById(R.id.empprofile_update_cancelbtn);
        empprofile_update_btn = findViewById(R.id.empprofile_update_btn);

        empprofile_active_user_check = findViewById(R.id.empprofile_active_user_check);
        empprofile_back_btn = findViewById(R.id.empprofile_back_btn);
        empprofile_gender_dropdown= findViewById(R.id.empprofile_gender_dropdown);
        empprofile_assign_manager_dropdown= findViewById(R.id.empprofile_assign_manager_dropdown);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        empprofile_phone.setClickable(false);
        empprofile_phone.setEnabled(false);
        empprofile_emp_name.setClickable(false);
        empprofile_emp_name.setEnabled(false);
        empprofile_active_user_check.setEnabled(false);
        empprofile_active_user_check.setClickable(false);

        empprofile_assign_manager_dropdown.setClickable(false);
        empprofile_assign_manager_dropdown.setEnabled(false);
        empprofile_gender_dropdown.setClickable(false);
        empprofile_gender_dropdown.setEnabled(false);

        empprofile_update_btn.setVisibility(View.GONE);
        empprofile_update_cancelbtn.setVisibility(View.GONE);
        empprofile_edit_btn.setVisibility(View.VISIBLE);
        user= functions.getSharedPrefsValue(getApplicationContext(), "user_data", "ptype", "string", null);
        if (user != null && user.equals("Managers")) empprofile_edit_btn.setVisibility(View.GONE);

        intent = getIntent();
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);

        gender_arr.add("Male");
        gender_arr.add("Female");
        gender_arr.add("Other");

        genderAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,gender_arr);
        genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        empprofile_gender_dropdown.setAdapter(genderAdapter);

        Dialog loading_dialog= functions.createDialogBox(EmployeeProfileActivity.this, R.layout.loading_dialog, false);
        loading_dialog.show();
        LoadManagersDetails(new ManagersCallBack<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> result) {
                if(result.size() > 0){
                    for(DocumentSnapshot doc : result){
                        manager_names.add(doc.getString("manager_name"));
                        manager_ref.add(doc.getReference());
                    }

                    managerAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_item,manager_names);
                    managerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    empprofile_assign_manager_dropdown.setAdapter(managerAdapter);

                    db.collection("Employees")
                            .whereEqualTo(FieldPath.documentId(), db.document(emp_path))
                            .whereEqualTo("company_path", db.document(comp_path))
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        emp_name = task.getResult().getDocuments().get(0).getString("emp_name");
                                        emp_contact = task.getResult().getDocuments().get(0).getString("emp_contact");
                                        advance_val = task.getResult().getDocuments().get(0).getDouble("emp_advance_loans");
                                        arrears_val = task.getResult().getDocuments().get(0).getDouble("emp_sal_arrears");
                                        empprofile_emp_name.getEditText().setText(emp_name);
                                        empprofile_phone.getEditText().setText(emp_contact);
                                        empprofile_advance_val.setText("₹ " + advance_val);
                                        empprofile_arrears_val.setText("₹ " + arrears_val);
                                        active_check = task.getResult().getDocuments().get(0).getBoolean("emp_status");
                                        empprofile_active_user_check.setChecked(active_check);
                                        emp_gender= task.getResult().getDocuments().get(0).getString("emp_gender");
                                        empprofile_gender_dropdown.setSelection(gender_arr.indexOf(emp_gender));
                                        emp_manager_ref= task.getResult().getDocuments().get(0).getDocumentReference("emp_manager_reference");
                                        Log.e("emp_manager_ref","The manager reference is : "+emp_manager_ref.getPath());
                                        empprofile_assign_manager_dropdown.setSelection(manager_ref.indexOf(emp_manager_ref));
                                        emp_manager_name= empprofile_assign_manager_dropdown.getSelectedItem().toString();
                                        loading_dialog.dismiss();

                                        empprofile_addloanbtn.setOnClickListener(v -> addNewLoan(advance_val, emp_path, true));
                                        empprofile_repayloanbtn.setOnClickListener(v -> addNewLoan(advance_val, emp_path, false));
                                    } else {
                                        loading_dialog.cancel();
                                        Snackbar.make(empprofile_mainlay, "No such employee found !! Please try again", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    loading_dialog.cancel();
                                    Snackbar.make(empprofile_mainlay, "Unable to find the employee details !! Please try again", Snackbar.LENGTH_LONG).show();
                                    new Handler().postDelayed(() -> finish(),2000);
                                }
                            });
                }
            }

            @Override
            public void onFail(Exception fail) {
                loading_dialog.cancel();
                Snackbar.make(empprofile_mainlay,"Unable to load data !! PLease try again", Snackbar.LENGTH_SHORT).show();
                Log.e("firebase_error","The error is : "+fail.getLocalizedMessage());
                new Handler().postDelayed(() -> finish(),1500);
            }
        });
    }

    private interface ManagersCallBack<T> {
        void onSuccess(T result);
        void onFail(Exception fail);
    }

    private void LoadManagersDetails(ManagersCallBack<List<DocumentSnapshot>> callBack){
        db.collection("Managers")
                .whereEqualTo("company_path", db.document(comp_path))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        callBack.onSuccess(task.getResult().getDocuments());

                    }
                    else{
                        callBack.onFail(task.getException());
                    }
                    managerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,manager_names);
                    genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    empprofile_gender_dropdown.setAdapter(genderAdapter);
                });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), EmployeesListActivity.class);
        intent.putExtra("pagecheck", 0);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void smsPermissionRequestResponse(String emp_phoneno) {
        Dexter.withContext(EmployeeProfileActivity.this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emp_phoneno, null, "Hello, this is WeTrack App. The company, have updated your phone number as requested by you. Please install WeTrack App and login to your account using "+emp_contact+" phone number.", null, null);
                        Snackbar.make(empprofile_mainlay, "Employee Details updated successfully.", Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> Snackbar.make(empprofile_mainlay, "SMS notification has been send to the employee about the updation of the contact number.", Snackbar.LENGTH_SHORT).show(),1500);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Snackbar.make(empprofile_mainlay, "Unable to send SMS to the workers !! Please enable the SMS permission to enable this feature.", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @SuppressLint("SetTextI18n")
    private void addNewLoan(Double curr_loan, String emp_reference, boolean take_loan) {
        BottomSheetDialog addloan_btmdialog = new BottomSheetDialog(EmployeeProfileActivity.this);
        addloan_btmdialog.setContentView(R.layout.update_loan_btmdialog);
        addloan_btmdialog.setCanceledOnTouchOutside(true);
        addloan_btmdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView addloan_currloan = addloan_btmdialog.findViewById(R.id.addloan_currloan);
        TextInputLayout addloan_val = addloan_btmdialog.findViewById(R.id.addloan_val);
        Button addloan_submitbtn = addloan_btmdialog.findViewById(R.id.addloan_submitbtn);
        addloan_submitbtn.setEnabled(false);
        addloan_submitbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
        if (!take_loan)
            addloan_val.getEditText().setFilters(new InputFilter[]{new InputFilterMinMax(0.0, curr_loan)});

        addloan_currloan.setText("Current Loan - ₹ " + curr_loan);
        addloan_val.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String val_txt = addloan_val.getEditText().getText().toString();
                Double loan_val = !Objects.equals(val_txt, "") ? Double.parseDouble(val_txt) : 0.0;
                if (loan_val > 0) {
                    addloan_val.setError(null);
                    addloan_val.setErrorEnabled(false);
                    addloan_submitbtn.setEnabled(true);
                    addloan_submitbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.maincolor)));
                } else {
                    addloan_val.setErrorEnabled(true);
                    addloan_val.setError("Please enter a value greater than zero !!");
                    addloan_val.requestFocus();
                    addloan_submitbtn.setEnabled(false);
                    addloan_submitbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        Log.e("addNewLoan_currloan", "The current loan available is : " + curr_loan);

        addloan_submitbtn.setOnClickListener(v1 -> {
            addloan_val.clearFocus();
            String val_txt = addloan_val.getEditText().getText().toString();
            Double loan_val = !Objects.equals(val_txt, "") ? Double.parseDouble(val_txt) : 1.0;
            Map<String, Object> new_loan_data = new HashMap<>();
            new_loan_data.put("loan_amount", loan_val);
            new_loan_data.put("loan_timestamp", Timestamp.now());
            new_loan_data.put("loan_balance", (take_loan ? (curr_loan + loan_val) : (curr_loan - loan_val)));
            new_loan_data.put("loan_emp_reference", db.document(emp_reference));
            new_loan_data.put("loan_company_reference", db.document(comp_path));
            new_loan_data.put("loan_from_sal", false);
            new_loan_data.put("loan_pay_status", !take_loan);

            db.collection("Loans")
                    .add(new_loan_data)
                    .addOnCompleteListener(task12 -> {
                        if (task12.isSuccessful()) {
                            addloan_btmdialog.dismiss();
                            Double loan_amount = take_loan ? (curr_loan + loan_val) : (curr_loan - loan_val);
                            db.collection("Employees")
                                    .document(db.document(emp_reference).getId())
                                    .update("emp_advance_loans", loan_amount)
                                    .addOnCompleteListener(task2 -> {
                                        if (task2.isSuccessful()) {
                                            advance_val = loan_amount;
                                            empprofile_advance_val.setText("₹ " + advance_val);
                                            Snackbar.make(empprofile_mainlay, "New Loan of Rs " + loan_val + " added successfully", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    });
        });

        addloan_btmdialog.show();
    }
}