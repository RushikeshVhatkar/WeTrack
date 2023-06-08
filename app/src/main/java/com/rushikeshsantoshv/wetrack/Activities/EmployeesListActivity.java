package com.rushikeshsantoshv.wetrack.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rushikeshsantoshv.wetrack.Adapters.EmployeeListAdapter;
import com.rushikeshsantoshv.wetrack.DataModels.EmployeeModel;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeesListActivity extends AppCompatActivity {

    Functions functions = new Functions();
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    EmployeeListAdapter addEmployeesAdapter;

    RelativeLayout emplist_mainlay;
    Toolbar emplist_toolbar;
    ImageButton emplist_back_btn, emplist_searchemp_btn;
    TextView emplist_title;
    FloatingActionButton emplist_addemp_btn;
    LinearLayout emplist_emptylist_lay;
    RecyclerView emplist_rec;
    ProgressBar emplist_progressbar;
    String comp_path;
    String curr_user;

    int pagecheck = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees_list);
        functions.coloredStatusBarDarkTextDesign(EmployeesListActivity.this, R.color.maincolor_light, R.color.white);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        emplist_mainlay = findViewById(R.id.emplist_mainlay);
        emplist_toolbar = findViewById(R.id.emplist_toolbar);
        emplist_back_btn = findViewById(R.id.emplist_back_btn);
        emplist_searchemp_btn = findViewById(R.id.emplist_searchemp_btn);
        emplist_title = findViewById(R.id.emplist_title);
        emplist_addemp_btn = findViewById(R.id.emplist_addemp_btn);
        emplist_emptylist_lay = findViewById(R.id.emplist_emptylist_lay);
        emplist_rec = findViewById(R.id.emplist_rec);
        emplist_progressbar = findViewById(R.id.emplist_progressbar);
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        curr_user= functions.getSharedPrefsValue(getApplicationContext(), "user_data", "ptype", "string",null);

        Log.e("created_date", "The created date is : "+functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_created", "string",functions.getTodayDate("dd/MM/yyyy")));

        Intent page_data = getIntent();
        pagecheck = page_data.getIntExtra("pagecheck", 0);
        if (pagecheck != 0)
            emplist_addemp_btn.setVisibility(View.GONE);

        emplist_addemp_btn.setOnClickListener(v -> {
            if(functions.checkInternetConnection(this)){
                Dialog loading_dialog= functions.createDialogBox(EmployeesListActivity.this, R.layout.loading_dialog, false);
                loading_dialog.show();

                String user= functions.getSharedPrefsValue(getApplicationContext(), "user_data", "ptype", "string", null);
                String curr_logged_user= functions.getSharedPrefsValue(getApplicationContext(), "user_data", "user_reference", "string", null);

                Task<QuerySnapshot> ManagerCollQuery= user!=null && curr_logged_user!=null && user.equals("Managers") ?
                        db.collection("Managers")
                        .whereEqualTo(FieldPath.documentId(), db.document(curr_logged_user).getId())
                        .get() :
                        db.collection("Managers")
                        .whereEqualTo("company_path",db.document(comp_path))
                        .get();

                ManagerCollQuery.addOnCompleteListener(mtask -> {
                            if(mtask.isSuccessful()){
                                if(mtask.getResult().getDocuments().size() > 0){
                                    loading_dialog.dismiss();

                                    ArrayList<String> manager_names= new ArrayList<>();
                                    ArrayList<DocumentReference> manager_references= new ArrayList<>();

                                    manager_names.add("Select Manager");
                                    manager_references.add(db.document("Managers/SampleUser"));

                                    for(DocumentSnapshot doc : mtask.getResult().getDocuments()){
                                        manager_references.add(doc.getReference());
                                        manager_names.add(doc.getString("manager_name"));
                                    }

                                    BottomSheetDialog addemp_btm_dialog = new BottomSheetDialog(EmployeesListActivity.this);
                                    addemp_btm_dialog.setContentView(R.layout.addworkers_btm_dialog);
                                    addemp_btm_dialog.setCanceledOnTouchOutside(true);
                                    addemp_btm_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    LinearLayout aw_btm_mainlay= addemp_btm_dialog.findViewById(R.id.aw_btm_mainlay);
                                    TextView aw_btm_heading= addemp_btm_dialog.findViewById(R.id.aw_btm_heading);
                                    TextInputLayout aw_btm_name = addemp_btm_dialog.findViewById(R.id.aw_btm_name);
                                    TextInputLayout aw_btm_phone = addemp_btm_dialog.findViewById(R.id.aw_btm_phone);
                                    TextInputLayout aw_btm_advancedloans = addemp_btm_dialog.findViewById(R.id.aw_btm_advancedloans);
                                    SwitchCompat aw_btm_active_user_check = addemp_btm_dialog.findViewById(R.id.aw_btm_active_user_check);
                                    Button aw_btm_add_btn = addemp_btm_dialog.findViewById(R.id.aw_btm_add_btn);
                                    RadioGroup aw_btm_genders= addemp_btm_dialog.findViewById(R.id.aw_btm_genders);
                                    Spinner aw_btm_managers_dropdown= addemp_btm_dialog.findViewById(R.id.aw_btm_managers_dropdown);

                                    ArrayAdapter managerAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,manager_names){

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
                                    managerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    aw_btm_managers_dropdown.setAdapter(managerAdapter);
                                    aw_btm_managers_dropdown.setSelection(0, false);

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
                                            String loans = aw_btm_advancedloans.getEditText().getText().toString();
                                            String fullname_regx = "^[a-zA-Z\\s]*$";

                                            if (emp_name.length() <= 2) {
                                                aw_btm_name.setError("Enter a name with more than 2 characters length !!");
                                                aw_btm_name.requestFocus();
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            } else if (!emp_name.matches(fullname_regx)) {
                                                aw_btm_name.setError("Please enter a valid name !!");
                                                aw_btm_name.requestFocus();
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            } else
                                                aw_btm_name.setError(null);

                                            if (emp_contact.length() < 10)
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else if (loans.length() == 0)
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else
                                                updateAddEmployeeButtonStyle("#00A3FF", true, aw_btm_add_btn);
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
                                            String loans = aw_btm_advancedloans.getEditText().getText().toString();
                                            String fullname_regx = "^[a-zA-Z\\s]*$";

                                            if (emp_contact.length() < 10) {
                                                aw_btm_phone.setError("Enter a valid contact number !!");
                                                aw_btm_phone.requestFocus();
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            } else
                                                aw_btm_phone.setError(null);

                                            if (emp_name.length() <= 2)
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else if (!emp_name.matches(fullname_regx))
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else if (loans.length() == 0)
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else
                                                updateAddEmployeeButtonStyle("#00A3FF", true, aw_btm_add_btn);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                        }
                                    });

                                    aw_btm_advancedloans.getEditText().addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            String fullname_regx = "^[a-zA-Z\\s]*$";
                                            String emp_name = aw_btm_name.getEditText().getText().toString();
                                            String emp_contact = aw_btm_phone.getEditText().getText().toString();
                                            String loans = aw_btm_advancedloans.getEditText().getText().toString();

                                            if (loans.length() == 0) {
                                                aw_btm_advancedloans.setError("Enter a value or enter zero !!");
                                                aw_btm_advancedloans.requestFocus();
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            } else
                                                aw_btm_advancedloans.setError(null);

                                            if (emp_contact.length() < 10)
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else if (!emp_name.matches(fullname_regx))
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else if (emp_name.length() <= 2)
                                                updateAddEmployeeButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else
                                                updateAddEmployeeButtonStyle("#00A3FF", true, aw_btm_add_btn);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                        }
                                    });

                                    aw_btm_add_btn.setOnClickListener(v2 -> {
                                        String emp_name = aw_btm_name.getEditText().getText().toString();
                                        String emp_contact = aw_btm_phone.getEditText().getText().toString();
                                        String loans_str = aw_btm_advancedloans.getEditText().getText().toString();
                                        long loans = !loans_str.equals("") ? Long.parseLong(loans_str) : 0;
                                        String company_path_val = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
                                        String selected_gender=  aw_btm_genders.getCheckedRadioButtonId()==R.id.aw_btm_gender_male ? "Male" :
                                                (aw_btm_genders.getCheckedRadioButtonId()==R.id.aw_btm_gender_female ? "Female" : "Others");

                                        int manager_selected= aw_btm_managers_dropdown.getSelectedItemPosition();
                                        if(!curr_user.equals("Managers") && manager_selected > 0) {
                                            db.collection("Employees")
                                                    .whereEqualTo("emp_contact", ("+91" + emp_contact))
                                                    .get()
                                                    .addOnCompleteListener(task_1 -> {
                                                        if (task_1.isSuccessful()) {
                                                            if (task_1.getResult().getDocuments().size() > 0) {
                                                                aw_btm_phone.setErrorEnabled(true);
                                                                aw_btm_phone.setError("This number is already added as an employee in a company !!");
                                                                aw_btm_phone.requestFocus();
                                                            } else {
                                                                db.collection("Managers")
                                                                        .whereEqualTo("manager_contact", ("+91" + emp_contact))
                                                                        .get()
                                                                        .addOnCompleteListener(task_2 -> {
                                                                            if (task_2.isSuccessful()) {
                                                                                if (task_2.getResult().getDocuments().size() > 0) {
                                                                                    aw_btm_phone.setErrorEnabled(true);
                                                                                    aw_btm_phone.setError("This number is already added as a manager in a company !!");
                                                                                    aw_btm_phone.requestFocus();
                                                                                } else {
                                                                                    aw_btm_phone.setErrorEnabled(false);
                                                                                    aw_btm_phone.setError(null);
                                                                                    addemp_btm_dialog.dismiss();
                                                                                    if (company_path_val != null) {
                                                                                        DocumentReference company_path = db.document(company_path_val);
                                                                                        Map<String, Object> worker_data = new HashMap<>();
                                                                                        worker_data.put("emp_name", emp_name);
                                                                                        worker_data.put("emp_contact", "+91" + emp_contact);
                                                                                        worker_data.put("emp_timestamp", Timestamp.now());
                                                                                        worker_data.put("emp_status", aw_btm_active_user_check == null || aw_btm_active_user_check.isChecked());
                                                                                        worker_data.put("company_path", company_path);
                                                                                        worker_data.put("emp_sal_arrears", 0);
                                                                                        worker_data.put("emp_sal_paid", 0);
                                                                                        worker_data.put("emp_gender",selected_gender);
                                                                                        worker_data.put("emp_advance_loans", loans);
                                                                                        worker_data.put("emp_sal_total", 0);
                                                                                        worker_data.put("emp_manager_reference",manager_references.get(manager_selected));

                                                                                        db.collection("Employees")
                                                                                                .add(worker_data)
                                                                                                .addOnCompleteListener(task -> {
                                                                                                    emplist_progressbar.setVisibility(View.VISIBLE);
                                                                                                    if (task.isSuccessful()) {

                                                                                                        Map<String, Object> payment_data = new HashMap<>();
                                                                                                        payment_data.put("pemp_emp_reference", db.document(task.getResult().getPath()));
                                                                                                        payment_data.put("pemp_company_reference", company_path);
                                                                                                        payment_data.put("pemp_timestamp", Timestamp.now());
                                                                                                        payment_data.put("pemp_sal_arrears", 0);
                                                                                                        payment_data.put("pemp_sal_paid", 0);
                                                                                                        payment_data.put("pemp_sal_total", 0);
                                                                                                        payment_data.put("pemp_wage_total", 0);
                                                                                                        payment_data.put("pemp_wage_paid", 0);
                                                                                                        payment_data.put("pemp_loan_paid", 0);
                                                                                                        payment_data.put("pemp_loan_balance", loans);
                                                                                                        payment_data.put("pemp_loan_total", 0);
                                                                                                        payment_data.put("pemp_base_rate", 0);
                                                                                                        payment_data.put("pemp_start_date", Timestamp.now());

                                                                                                        db.collection("Payments")
                                                                                                                .add(payment_data)
                                                                                                                .addOnCompleteListener(task12 -> {
                                                                                                                    if (task12.isSuccessful()) {
                                                                                                                        if (loans > 0) {
                                                                                                                            Map<String, Object> loan_data = new HashMap<>();
                                                                                                                            loan_data.put("loan_emp_reference", db.document(task.getResult().getPath()));
                                                                                                                            loan_data.put("loan_company_reference", company_path);
                                                                                                                            loan_data.put("loan_amount", loans);
                                                                                                                            loan_data.put("loan_balance", loans);
                                                                                                                            loan_data.put("loan_pay_status", false);
                                                                                                                            loan_data.put("loan_from_sal", false);
                                                                                                                            loan_data.put("loan_timestamp", Timestamp.now());

                                                                                                                            db.collection("Loans")
                                                                                                                                    .add(loan_data)
                                                                                                                                    .addOnCompleteListener(task1 -> {
                                                                                                                                        if (task1.isSuccessful()) {
                                                                                                                                            smsPermissionRequestResponse("+91" + emp_contact);
                                                                                                                                            Snackbar.make(emplist_mainlay, "New employee added successfully.", Snackbar.LENGTH_SHORT).show();
                                                                                                                                            loadEmployeesList();
                                                                                                                                        }
                                                                                                                                    });
                                                                                                                        } else {
                                                                                                                            smsPermissionRequestResponse("+91" + emp_contact);
                                                                                                                            Snackbar.make(emplist_mainlay, "New employee added successfully.", Snackbar.LENGTH_SHORT).show();
                                                                                                                            loadEmployeesList();
                                                                                                                        }
                                                                                                                    } else {
                                                                                                                        Snackbar.make(emplist_mainlay, "Unable to add an employee !! Please try again.", Snackbar.LENGTH_SHORT).show();
                                                                                                                    }
                                                                                                                });
                                                                                                    } else {
                                                                                                        Snackbar.make(emplist_mainlay, "Unable to add an employee !! Please try again.", Snackbar.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                });
                                                                                    } else {
                                                                                        Snackbar.make(emplist_mainlay, "Unable to add new worker for the time being. Please try later !!", Snackbar.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            } else {
                                                                                Snackbar.make(emplist_mainlay, "Some Error Occurred !! Please try again", Snackbar.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            Snackbar.make(emplist_mainlay, "Some Error Occurred !! Please try again", Snackbar.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                        else{
                                            Toast.makeText(this, "Unable to add worker !! Please assign a manager to this worker", Toast.LENGTH_LONG).show();
                                        }
                                    });

                                    addemp_btm_dialog.show();
                                }
                                else{
                                    loading_dialog.cancel();
                                    Snackbar.make(emplist_mainlay,"Unable to some company details !! Please try again to add employee", Snackbar.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                loading_dialog.cancel();
                                Snackbar.make(emplist_mainlay,"Unable to some company details !! Please try again to add employee", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
            else functions.no_internet_dialog(this, false);
        });

        emplist_back_btn.setOnClickListener(v -> finish());

        if(functions.checkInternetConnection(this)) loadEmployeesList();
        else functions.no_internet_dialog(this, true);

    }

    public void updateAddEmployeeButtonStyle(String s, boolean b, @NonNull Button btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(s)));
        btn.setEnabled(b);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadEmployeesList() {
        String company_path_val = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        if (company_path_val != null && curr_user!=null) {

            Query emp_list_query;
            if(curr_user.equals("Owners")){
                emp_list_query = (pagecheck == 1) ? db.collection("Employees").whereEqualTo("company_path", db.document(company_path_val)).whereEqualTo("emp_status", true)
                        : db.collection("Employees").whereEqualTo("company_path", db.document(company_path_val));
            }
            else{
                String manager_path= functions.getSharedPrefsValue(getApplicationContext(), "user_data","user_reference","string",null);
                emp_list_query = (pagecheck == 1) ? db.collection("Employees").whereEqualTo("emp_manager_reference",db.document(manager_path)).whereEqualTo("company_path", db.document(company_path_val)).whereEqualTo("emp_status", true)
                        : db.collection("Employees").whereEqualTo("emp_manager_reference",db.document(manager_path)).whereEqualTo("company_path", db.document(company_path_val));
            }

            emp_list_query.get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().size() > 0) {
                                emplist_emptylist_lay.setVisibility(View.GONE);
                                emplist_rec.setVisibility(View.VISIBLE);
                                List<DocumentSnapshot> emplist = task.getResult().getDocuments();
                                ArrayList<EmployeeModel> emplist_arr = new ArrayList<>();
                                for (DocumentSnapshot emp : emplist) {
                                    emplist_arr.add(new EmployeeModel(emp.getDocumentReference("company_path"),
                                            emp.getString("emp_name"), emp.getString("emp_contact"),
                                            emp.getReference().getPath(), emp.getBoolean("emp_status"),
                                            emp.getDouble("emp_advance_loans"), emp.getDouble("emp_sal_arrears"),
                                            emp.getDouble("emp_sal_paid"), emp.getDouble("emp_sal_total")));
                                }
                                emplist_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                addEmployeesAdapter = new EmployeeListAdapter(getApplicationContext(), emplist_arr, pagecheck, EmployeesListActivity.this);
                                emplist_rec.setAdapter(addEmployeesAdapter);
                                addEmployeesAdapter.notifyDataSetChanged();
                                emplist_progressbar.setVisibility(View.GONE);
                            } else {
                                emplist_progressbar.setVisibility(View.GONE);
                                emplist_emptylist_lay.setVisibility(View.VISIBLE);
                                emplist_rec.setVisibility(View.GONE);
                            }
                        } else {
                            Snackbar.make(emplist_mainlay, "Unbale to retrieve data from the database !! Please try again !!", Snackbar.LENGTH_SHORT).show();
                            emplist_progressbar.setVisibility(View.GONE);
                            emplist_emptylist_lay.setVisibility(View.VISIBLE);
                            emplist_rec.setVisibility(View.GONE);
                        }
                    });
        } else {
            Snackbar.make(emplist_mainlay, "Unable to find the data due to absence of company path !! Please try again !!", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void smsPermissionRequestResponse(String emp_phoneno) {
        Dexter.withContext(EmployeesListActivity.this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emp_phoneno, null, "Hello, this is WeTrack App. The company, have added you as their employee. Please install WeTrack App and login to your account using your phone number.", null, null);
                        Snackbar.make(emplist_mainlay, "SMS notification has been send to the employee successfully.", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Snackbar.make(emplist_mainlay, "Unable to send SMS to the workers !! Please enable the SMS permission to enable this feature.", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

}