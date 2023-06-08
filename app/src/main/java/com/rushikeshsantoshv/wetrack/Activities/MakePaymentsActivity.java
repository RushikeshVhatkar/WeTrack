package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.DataModels.InputFilterMinMax;
import com.rushikeshsantoshv.wetrack.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MakePaymentsActivity extends AppCompatActivity {

    TextView makepayments_getdatelist, makepayments_present, makepayments_halfday, makepayments_absent, makepayments_holiday;
    TextView makepayments_amtpending, makepayments_amtadvanceloan, makepayments_arrears, makepayments_total;
    TextInputLayout makepayments_amtpending_giving, makepayments_amtadvanceloan_giving, makepayments_baserate, makepayments_final_amount_giving;
    TextView makepayments_todaydate, makepayments_finalpaymentval, makepayments_empname, makepayments_empcontact;
    Button makepayments_submitbtn, makepayments_addloanbtn;
    ImageButton makepayments_back_btn;
    RelativeLayout paymentrecords_mainlay;
    NestedScrollView makepayments_nestlay;
    LinearLayout makepayments_paymentbtnlay;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Functions functions = new Functions();

    String emp_name, emp_contact, emp_reference, comp_path;
    int present_count = 0, half_day_count = 0, absent_count = 0;
    double company_base_rate = 0;
    double salary_amount = 0, arrears_amount = 0, loan_amount = 0, loan_payable_amount = 0, total_amount = 0, total_sal_giving = 0, total_amount_giving = 0;
    double emp_sal_paid = 0, emp_sal_total = 0, new_loan_val = 0;
    Timestamp newstart_payment_timestamp = Timestamp.now();
    double predict_loan_taken = 0, predict_loan_paid = 0, total_loan_count = 0, total_loan_from_sal = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        functions.coloredStatusBarDarkTextDesign(MakePaymentsActivity.this, R.color.maincolor_light, R.color.white);
        setContentView(R.layout.activity_make_payments);

        makePaymentsInits();
        makepayments_nestlay.setVisibility(View.GONE);
        makepayments_paymentbtnlay.setVisibility(View.GONE);

        makepayments_back_btn.setOnClickListener(v -> finish());

        Intent intent = getIntent();
        if (intent != null) {
            if(functions.checkInternetConnection(this)){
                emp_name = intent.getStringExtra("emp_name") != null ? intent.getStringExtra("emp_name") : "Not Available";
                emp_contact = intent.getStringExtra("emp_contact") != null ? intent.getStringExtra("emp_contact") : "Not Available";
                emp_reference = intent.getStringExtra("emp_reference_id") != null ? intent.getStringExtra("emp_reference_id") : db.document("Employees/sampledoc").getPath();
                loan_amount = intent.getDoubleExtra("emp_advance_loans", 0.0);
                arrears_amount = intent.getDoubleExtra("emp_sal_arrears", 0.0);
                emp_sal_paid = intent.getDoubleExtra("emp_sal_paid", 0.0);
                emp_sal_total = intent.getDoubleExtra("emp_sal_total", 0.0);


                db.collection("Payments")
                        .whereEqualTo("pemp_emp_reference", db.document(emp_reference))
                        .orderBy("pemp_timestamp", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                Timestamp last_payment_timestamp = doc.getTimestamp("pemp_timestamp");
                            }
                        });

                db.collection("Attendances")
                        .whereEqualTo("attend_company_reference", db.document(comp_path))
                        .whereEqualTo("attend_emp_reference", db.document(emp_reference))
                        .orderBy("attend_date", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().size() > 0) {
                                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                    String latest_attendance = functions.getStringFromDate(doc.getTimestamp("attend_date").toDate(), "dd/MM/yyyy");
                                    String current_date = functions.getTodayDate("dd/MM/yyyy");
//                                String current_date= "08/04/2023";
                                    Log.e("firebase_timestamp", "The attendance date is : " + latest_attendance);
                                    if (!latest_attendance.equals(current_date)) {
                                        Log.e("firebase_error", "Validation Error !!");
                                        NoAttendanceDialog("You haven't taken today's attendances !! Please take today's attendances of the employees, before giving the payment.");
                                    } else {
                                        checkAttendancesAndProceed();
                                    }
                                } else {
                                    Log.e("firebase_error", "No Size !!");
                                    NoAttendanceDialog("You haven't taken today's attendances !! Please take today's attendances of the employees, before giving the payment.");
                                }
                            } else {
                                String error = task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error";
                                Log.e("firebase_error", "The firebase error is : " + error);
                                Snackbar.make(paymentrecords_mainlay, "Some Error Occured !!! Please try again !!", Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(this::finish, 1500);
                            }
                        });
            }
            else functions.no_internet_dialog(this, true);
        }
        else {
            Snackbar.make(paymentrecords_mainlay, "Some Error Occured !!! Please try again !!", Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(this::finish, 1500);
        }
    }

    public void NoAttendanceDialog(String msg) {
        Dialog msg_dialog = functions.createDialogBox(MakePaymentsActivity.this, R.layout.msg_dialog, false);
        TextView msg_content = msg_dialog.findViewById(R.id.msg_content);
        TextView msg_ok_btn = msg_dialog.findViewById(R.id.msg_ok_btn);
        TextView msg_cancel_btn = msg_dialog.findViewById(R.id.msg_cancel_btn);

        msg_cancel_btn.setVisibility(View.VISIBLE);
        msg_content.setText(msg);

        msg_ok_btn.setOnClickListener(v1 -> {
            msg_dialog.dismiss();
            startActivity(new Intent(getApplicationContext(), MarkAttendancesActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        });

        msg_cancel_btn.setOnClickListener(v1 -> {
            msg_dialog.cancel();
            finish();
        });

        msg_dialog.show();
    }

    public void checkAttendancesAndProceed() {
        if(functions.checkInternetConnection(this)){
            db.collection("Payments")
                    .whereEqualTo("pemp_company_reference", db.document(comp_path))
                    .whereEqualTo("pemp_emp_reference", db.document(emp_reference))
                    .orderBy("pemp_timestamp", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(task13 -> {
                        if (task13.isSuccessful()) {
                            if (task13.getResult().getDocuments().size() > 0) {
                                DocumentSnapshot lastest_payment = task13.getResult().getDocuments().get(0);
                                String latest_timestamp = functions.getStringFromDate(lastest_payment.getTimestamp("pemp_timestamp").toDate(), "dd/MM/yyyy");
                                String today_date = functions.getTodayDate("dd/MM/yyyy");
//                            String today_date= "08/04/2023";
                                if (!latest_timestamp.equals(today_date)) {
                                    MakePaymentActivityFunctions();
                                } else {
                                    Dialog msg_dialog = functions.createDialogBox(MakePaymentsActivity.this, R.layout.msg_dialog, false);
                                    TextView msg_content = msg_dialog.findViewById(R.id.msg_content);
                                    TextView msg_header = msg_dialog.findViewById(R.id.msg_header);
                                    TextView msg_ok_btn = msg_dialog.findViewById(R.id.msg_ok_btn);

                                    msg_header.setText("Can't Proceed !!");
                                    msg_content.setText("Either you have already paid the money till today to this employees; or this employee haven't started working yet !! \n\nCome back tommorrow or dates post that to pay.");

                                    msg_ok_btn.setOnClickListener(v1 -> {
                                        msg_dialog.dismiss();
                                        finish();
                                    });
                                    msg_dialog.show();
                                }
                            } else {
                                MakePaymentActivityFunctions();
                            }
                        } else {
                            String msg_error = task13 != null && task13.getException() != null &&
                                    task13.getException().getLocalizedMessage() != null ? task13.getException().getLocalizedMessage()
                                    : "No Error !!";
                            Log.e("firebase_error", "The error is : " + msg_error);
                            Snackbar.make(paymentrecords_mainlay, "Some Error Occurred !! Please try again", Snackbar.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
        else functions.no_internet_dialog(this, true);
    }

    public void MakePaymentActivityFunctions() {
        makepayments_nestlay.setVisibility(View.VISIBLE);
        makepayments_paymentbtnlay.setVisibility(View.VISIBLE);
        makepayments_empname.setText(emp_name);
        makepayments_empcontact.setText(emp_contact);
        loadDataForMakePayment();

        makepayments_addloanbtn.setOnClickListener(v -> {
            Dialog loading_dialog = functions.createDialogBox(MakePaymentsActivity.this, R.layout.loading_dialog, false);
            loading_dialog.show();

            if(functions.checkInternetConnection(this)){
                db.collection("Loans")
                        .whereEqualTo("loan_emp_reference", db.document(emp_reference))
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().size() > 0) {
                                    List<DocumentSnapshot> arr = task.getResult().getDocuments();
                                    predict_loan_paid = 0;
                                    predict_loan_taken = 0;
                                    total_loan_count = 0;
                                    total_loan_from_sal = 0;

                                    total_loan_count = arr.size();
                                    for (DocumentSnapshot doc : arr) {
                                        total_loan_from_sal += doc.getBoolean("loan_from_sal") ? 1 : 0;
                                        if (doc.getBoolean("loan_pay_status")) {
                                            predict_loan_paid += doc.getDouble("loan_amount");
                                        } else {
                                            predict_loan_taken += doc.getDouble("loan_amount");
                                        }
                                    }
                                    Log.e("loan_vals", "The Loan Taken is : " + predict_loan_taken + " & loan paid is : " + predict_loan_paid);

                                    RequestQueue queue = Volley.newRequestQueue(this);
                                    String url = "https://wetrackpredictions.pythonanywhere.com/LoanPredict";

                                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                                            response -> {
                                                try {
                                                    loading_dialog.dismiss();
                                                    response.getString("loan_predict_res");
                                                    String prediction_res = response.getString("loan_predict_res");
                                                    String loan_predict_status = response.getString("loan_predict_status");
                                                    Log.e("loan_predict_res", "The result is : " + prediction_res + " and the result status is : " + loan_predict_status);
                                                    if (Double.parseDouble(prediction_res) == 1) {
                                                        addNewLoan(loan_amount);
                                                    } else {
                                                        Dialog reject_dialog = functions.createDialogBox(MakePaymentsActivity.this, R.layout.msg_dialog, false);
                                                        reject_dialog.show();

                                                        TextView msg_header = reject_dialog.findViewById(R.id.msg_header);
                                                        TextView msg_content = reject_dialog.findViewById(R.id.msg_content);
                                                        TextView msg_cancel_btn = reject_dialog.findViewById(R.id.msg_cancel_btn);
                                                        TextView msg_ok_btn = reject_dialog.findViewById(R.id.msg_ok_btn);
                                                        msg_cancel_btn.setVisibility(View.VISIBLE);

                                                        msg_ok_btn.setTextSize(13);
                                                        msg_cancel_btn.setTextSize(13);

                                                        msg_header.setText("Loan Approval Failed !!");
                                                        msg_content.setText("Analysing the previously the loan approval and its payback the system has analsyed that this workers isn't efficient in paying the lend money back. \n\nSo, either can you reject his/her approval for loan, or ignore this message and pay him/her the required loan. \n\nBut be cautious !!");
                                                        msg_cancel_btn.setText("Give Loan Anyways");
                                                        msg_ok_btn.setText("Cancel");

                                                        msg_cancel_btn.setOnClickListener(v1 -> {
                                                            reject_dialog.cancel();
                                                            new Handler().postDelayed(() -> addNewLoan(loan_amount), 500);
                                                        });

                                                        msg_ok_btn.setOnClickListener(v1 -> reject_dialog.dismiss());
                                                    }
                                                } catch (JSONException e) {
                                                    loading_dialog.dismiss();
                                                    addNewLoan(loan_amount);
                                                    Log.e("volley_error", "Some Error Occurred while parsing json data !! Error : " + e.getLocalizedMessage());
                                                }
                                            },
                                            error -> {
                                                loading_dialog.dismiss();
                                                addNewLoan(loan_amount);
                                                Log.e("volley_error", "Some Error Occurred while retrieving json data !! Error : " + error.getLocalizedMessage());
                                            }) {
                                        @Override
                                        public byte[] getBody() {
                                            HashMap<String, Object> params = new HashMap<>();
                                            params.put("val1", predict_loan_taken);
                                            params.put("val2", predict_loan_paid);
                                            params.put("val3", total_loan_count);
                                            params.put("val4", total_loan_from_sal);
                                            String requestBody = new JSONObject(params).toString();
                                            return requestBody.getBytes();
                                        }

                                        @Override
                                        public @NotNull
                                        Map<String, String> getHeaders() {
                                            HashMap<String, String> headers = new HashMap<>();
                                            headers.put("User-Agent", "Mozilla/5.0");
                                            return headers;
                                        }

                                        @Override
                                        public String getBodyContentType() {
                                            return "application/json";
                                        }
                                    };
                                    queue.add(request);

                                } else {
                                    Snackbar.make(paymentrecords_mainlay, "No Loan Records !!", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                String msg = task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error !";
                                Log.e("firebase_error", "The error is : " + msg);
                            }
                        });
            }
            else functions.no_internet_dialog(this, false);
        });

        makepayments_submitbtn.setOnClickListener(v -> {

            updateSubmitButton(false);
            Dialog loading_dialog = functions.createDialogBox(MakePaymentsActivity.this, R.layout.loading_dialog, false);
            loading_dialog.show();

            String pemp_sal_paid_txt = makepayments_amtpending_giving.getEditText().getText().toString();
            String pemp_wage_paid_txt = makepayments_final_amount_giving.getEditText().getText().toString();
            String pemp_loan_paid_txt = makepayments_amtadvanceloan_giving.getEditText().getText().toString();

            Double pemp_sal_paid = !pemp_sal_paid_txt.equals("") ? Double.parseDouble(pemp_sal_paid_txt) : 0;
            Double pemp_wage_paid = !pemp_wage_paid_txt.equals("") ? Double.parseDouble(pemp_wage_paid_txt) : 0;
            Double pemp_loan_giving = !pemp_loan_paid_txt.equals("") ? Double.parseDouble(pemp_loan_paid_txt) : 0;
            Double final_arrears = (salary_amount - pemp_sal_paid) + (total_sal_giving - pemp_wage_paid);

            Timestamp time = Timestamp.now();
            Map<String, Object> pemp_data = new HashMap<>();
            pemp_data.put("pemp_emp_reference", db.document(emp_reference));
            pemp_data.put("pemp_company_reference", db.document(comp_path));
            pemp_data.put("pemp_timestamp", time);
            pemp_data.put("pemp_sal_arrears", final_arrears);
            pemp_data.put("pemp_sal_total", salary_amount);
            pemp_data.put("pemp_wage_total", total_sal_giving);
            pemp_data.put("pemp_loan_total", loan_amount);
            pemp_data.put("pemp_loan_balance", (loan_amount - pemp_loan_giving));
            pemp_data.put("pemp_sal_paid", pemp_sal_paid);
            pemp_data.put("pemp_wage_paid", pemp_wage_paid);
            pemp_data.put("pemp_loan_paid", pemp_loan_giving);
            pemp_data.put("pemp_base_rate", company_base_rate);
            pemp_data.put("pemp_start_date", newstart_payment_timestamp);

            Map<String, Object> update_emp_data = new HashMap<>();
            update_emp_data.put("emp_sal_arrears", final_arrears);
            update_emp_data.put("emp_sal_paid", pemp_wage_paid);
            update_emp_data.put("emp_sal_total", total_sal_giving);
            update_emp_data.put("emp_advance_loans", (loan_amount - pemp_loan_giving));

            Map<String, Object> sal_loan_add = new HashMap<>();
            sal_loan_add.put("loan_amount", pemp_loan_giving);
            sal_loan_add.put("loan_timestamp", time);
            sal_loan_add.put("loan_balance", (loan_amount - pemp_loan_giving));
            sal_loan_add.put("loan_emp_reference", db.document(emp_reference));
            sal_loan_add.put("loan_company_reference", db.document(comp_path));
            sal_loan_add.put("loan_from_sal", true);
            sal_loan_add.put("loan_pay_status", true);

            Log.e("final_data", "The final arrears is : " + final_arrears);
            Log.e("final_data", "The final total salary amount is : " + salary_amount);
            Log.e("final_data", "The final total wage amount is : " + total_sal_giving);
            Log.e("final_data", "The final total loan amount is : " + loan_amount);
            Log.e("final_data", "The final salary paid is : " + pemp_sal_paid);
            Log.e("final_data", "The final wages paid is : " + pemp_wage_paid);
            Log.e("final_data", "The final loan paid is : " + pemp_loan_giving);

            if(functions.checkInternetConnection(this)){
                db.collection("Payments")
                        .add(pemp_data)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                if (pemp_loan_giving > 0) {
                                    db.collection("Loans")
                                            .add(sal_loan_add)
                                            .addOnCompleteListener(task12 -> {
                                                if (task12.isSuccessful()) {
                                                    PaymentProcess(update_emp_data, loading_dialog);
                                                } else {
                                                    loading_dialog.cancel();
                                                    Snackbar.make(paymentrecords_mainlay, "Payment Success but some error occured !! Please try to check payment later...", Snackbar.LENGTH_SHORT).show();
                                                    new Handler().postDelayed(() -> {
                                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                        finish();
                                                    }, 1500);
                                                }
                                            });
                                } else {
                                    PaymentProcess(update_emp_data, loading_dialog);
                                }
                            } else {
                                loading_dialog.cancel();
                                Snackbar.make(paymentrecords_mainlay, "Error occured while payment record... Please try to add payment later...", Snackbar.LENGTH_SHORT).show();
                                new Handler().postDelayed(() -> {
                                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                    finish();
                                }, 1500);
                            }
                        });
            }
            else functions.no_internet_dialog(this, false);
        });

        makepayments_final_amount_giving.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String val = makepayments_final_amount_giving.getEditText().getText().toString();
                if (val.isEmpty()) {
                    makepayments_final_amount_giving.setErrorEnabled(true);
                    makepayments_final_amount_giving.setError("Please enter a number !!");
                    makepayments_final_amount_giving.requestFocus();
                    updateSubmitButton(false);
                } else {
                    makepayments_final_amount_giving.setError(null);
                    makepayments_final_amount_giving.setErrorEnabled(false);
                    total_amount_giving = Double.parseDouble(val);
                    makepayments_finalpaymentval.setText("Final Payment Amount : ₹ " + total_amount_giving);
                    updateSubmitButton(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void updateSubmitButton(Boolean check) {
        makepayments_submitbtn.setEnabled(check);
        int color = check ? R.color.my_green : R.color.light_grey;
        makepayments_submitbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), color)));
    }

    @SuppressLint("SetTextI18n")
    public void loadDataForMakePayment() {

        present_count = half_day_count = absent_count = 0;
        company_base_rate = salary_amount = 0.0;
        loan_payable_amount = total_amount = total_amount_giving = emp_sal_paid = emp_sal_total = new_loan_val = total_sal_giving = 0.0;

        if(functions.checkInternetConnection(this)){
            Dialog loading_dialog = functions.createDialogBox(MakePaymentsActivity.this, R.layout.loading_dialog, false);
            loading_dialog.show();

            db.collection("Employees")
                    .document(db.document(emp_reference).getId())
                    .get()
                    .addOnCompleteListener(task14 -> {
                        arrears_amount = task14.isSuccessful() ? task14.getResult().getDouble("emp_sal_arrears") : 0.0;
                        loan_amount = task14.isSuccessful() ? task14.getResult().getDouble("emp_advance_loans") : 0.0;
                        emp_sal_paid = task14.isSuccessful() ? task14.getResult().getDouble("emp_sal_paid") : 0.0;
                        emp_sal_total = task14.isSuccessful() ? task14.getResult().getDouble("emp_sal_total") : 0.0;

                        db.collection("Payments")
                                .whereEqualTo("pemp_emp_reference", db.document(emp_reference))
                                .orderBy("pemp_timestamp", Query.Direction.DESCENDING)
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                        Timestamp last_payment_timestamp = doc.getTimestamp("pemp_timestamp");
                                        Timestamp curr_timestamp = new Timestamp(functions.getDateFromString(functions.getTodayDate("dd-MM-yyyy"), "dd-MM-yyyy"));
//                                    Timestamp curr_timestamp = new Timestamp(functions.getDateFromString("08-04-2023", "dd-MM-yyyy"));

                                        newstart_payment_timestamp = task.getResult().getDocuments().size()==1 ? last_payment_timestamp :
                                                new Timestamp(functions.getModifiedDate(last_payment_timestamp.toDate(),
                                                        "dd/MM/yyyy",1,0,0));

                                        long payment_duration= setDateDurationText(newstart_payment_timestamp);
                                        makepayments_todaydate.setText("Date - " + functions.getTodayDate("dd/MM/yyyy"));
//                                    makepayments_todaydate.setText("Date - 08/04/2023");

                                        new Handler().postDelayed(() -> {
                                            db.collection("Attendances")
                                                    .whereEqualTo("attend_emp_reference", db.document(emp_reference))
                                                    .whereGreaterThan("attend_date", (Timestamp) last_payment_timestamp)
                                                    .whereLessThanOrEqualTo("attend_date", (Timestamp) curr_timestamp)
                                                    .orderBy("attend_date", Query.Direction.ASCENDING)
                                                    .get()
                                                    .addOnCompleteListener(task1 -> {
                                                        if (task1.isSuccessful()) {
                                                            if (task1.getResult().getDocuments().size() > 0) {
                                                                db.collection("Companies")
                                                                        .document(db.document(comp_path).getId())
                                                                        .get()
                                                                        .addOnCompleteListener(task2 -> {
                                                                            if(task2.isSuccessful()){
                                                                                ArrayList<Date> holiday_list = new ArrayList<>();
                                                                                for (Timestamp timestamp : (ArrayList<Timestamp>) task2.getResult().get("company_holidays")) {
                                                                                    if (timestamp.compareTo(last_payment_timestamp) > 0 && timestamp.compareTo(curr_timestamp) <= 0) {
                                                                                        holiday_list.add(timestamp.toDate());
                                                                                    }
                                                                                }
                                                                                List<DocumentSnapshot> attendance_arr = task1.getResult().getDocuments();
                                                                                ArrayList<Date> timestamp_arr= new ArrayList<>();
                                                                                for (DocumentSnapshot attendance : attendance_arr) {
                                                                                    timestamp_arr.add(functions.getDateFromString(
                                                                                            functions.getStringFromDate(attendance.getTimestamp(
                                                                                                            "attend_date").toDate(),
                                                                                                    "dd/MM/yyyy"),"dd/MM/yyyy"));

                                                                                    present_count += (attendance.getDouble("attend_status") == 1) ? 1 : 0;
                                                                                    half_day_count += (attendance.getDouble("attend_status") == 2) ? 1 : 0;
                                                                                    absent_count += (attendance.getDouble("attend_status") == 3) ? 1 : 0;
                                                                                }

                                                                                Date start_date= functions.getDateFromString(functions.getStringFromDate(
                                                                                        functions.modifiedDate(last_payment_timestamp.toDate(), 1),
                                                                                        "dd/MM/yyyy"),"dd/MM/yyyy");
                                                                                boolean check_dates= true;
                                                                                ArrayList<String> not_set_dates_arr= new ArrayList<>();
                                                                                for(int i=1;i<payment_duration;i++){
                                                                                    if(!timestamp_arr.contains(start_date) && !holiday_list.contains(start_date)){
                                                                                        not_set_dates_arr.add(functions.getStringFromDate(start_date,
                                                                                                "dd/MM/yyyy"));
                                                                                        check_dates= false;
                                                                                    }
                                                                                    start_date= functions.modifiedDate(start_date, 1);
                                                                                }
                                                                                if(check_dates){
                                                                                    makepayments_present.setText("" + present_count);
                                                                                    makepayments_halfday.setText("" + half_day_count);
                                                                                    makepayments_absent.setText("" + absent_count);
                                                                                    makepayments_holiday.setText("" + holiday_list.size());


                                                                                    long curr_fin_year = functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                                                                                            Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                                                                                            Long.parseLong(functions.getTodayDate("yyyy"));

                                                                                    if (comp_path != null) {
                                                                                        Log.e("companybaserate_error", "The company path is : " + comp_path);
                                                                                        db.collection("CompanyBaseRate")
                                                                                                .whereEqualTo("company_reference", db.document(comp_path))
                                                                                                .whereEqualTo("financial_year", curr_fin_year)
                                                                                                .get()
                                                                                                .addOnCompleteListener(task12 -> {
                                                                                                    if (task12.isSuccessful()) {
                                                                                                        if (task12.getResult().getDocuments().size() > 0) {
                                                                                                            String rate_field = functions.getTodayDate("MMM").toLowerCase();
//                                                                                    String rate_field= "apr";
                                                                                                            company_base_rate = task12.getResult().getDocuments().get(0).getDouble(rate_field + "_rate");
                                                                                                            makepayments_baserate.getEditText().setText("" + company_base_rate);

                                                                                                            salary_amount = (present_count * company_base_rate) + (half_day_count * (company_base_rate / 2));
                                                                                                            loan_payable_amount = Math.min(loan_amount, salary_amount);
                                                                                                            makepayments_arrears.setText("₹ " + arrears_amount);
                                                                                                            makepayments_amtadvanceloan.setText("₹ " + loan_amount);
                                                                                                            makepayments_amtadvanceloan_giving.getEditText().setFilters(new InputFilter[]{new InputFilterMinMax(0.0, loan_payable_amount)});
                                                                                                            makepayments_amtadvanceloan_giving.getEditText().setText(String.valueOf(0));
                                                                                                            makepayments_amtpending.setText("₹ " + salary_amount);
                                                                                                            makepayments_amtpending_giving.getEditText().setText("" + salary_amount);
                                                                                                            total_amount = salary_amount + arrears_amount;
                                                                                                            makepayments_amtpending_giving.getEditText().setFilters(new InputFilter[]{new InputFilterMinMax(0.0, salary_amount)});
                                                                                                            updateGivingTotalAmount(true);
                                                                                                            makepayments_total.setText("₹ " + total_amount);
                                                                                                            loading_dialog.dismiss();
                                                                                                        } else {
                                                                                                            Snackbar.make(paymentrecords_mainlay, "No CompanyBaseRate Found !!", Snackbar.LENGTH_SHORT).show();
                                                                                                        }
                                                                                                    } else {
                                                                                                        loading_dialog.dismiss();
                                                                                                        makepayments_baserate.getEditText().setText("" + 0);
                                                                                                        String msg = task12.getException() != null && task12.getException().getLocalizedMessage() != null ? task12.getException().getLocalizedMessage() : "No Error ...";
                                                                                                        Log.e("firebase_error", "Inner 2 Firebase Error : " + msg);
                                                                                                    }
                                                                                                });
                                                                                    }
                                                                                }
                                                                                else{
                                                                                    loading_dialog.cancel();
                                                                                    StringBuilder msg= new StringBuilder("Unable to take Attendances !!\nYou have left to take attendances of some days. Please take attendances" +
                                                                                            " of those days before the payment. The following are those dates : \n\n");
                                                                                    for(String date : not_set_dates_arr) msg.append("\u2022 ").append(date).append("\n");
                                                                                    msg.append("\n");
                                                                                    NoAttendanceDialog(String.valueOf(msg));
                                                                                }
                                                                            }
                                                                            else{

                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            loading_dialog.dismiss();
                                                            String msg = task1.getException() != null && task1.getException().getLocalizedMessage() != null ? task1.getException().getLocalizedMessage() : "No Error ...";
                                                            Log.e("firebase_error", "Inner 1 Firebase Error : " + msg);
                                                        }
                                                    });
                                        }, 10);
                                    } else {
                                        loading_dialog.dismiss();
                                        Log.e("firenbase_error", "Outmost Firebase Error : " + task.getException());
                                    }
                                });
                    });
        }
        else functions.no_internet_dialog(this, true);
    }

    @SuppressLint("SetTextI18n")
    public long setDateDurationText(@NonNull Timestamp timestamp) {
        Date timestampDate = timestamp.toDate();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy", Locale.getDefault());
        String startDateStr = df.format(timestampDate);
        String endDateStr = functions.getTodayDate("dd/MM/yyyy");
//        String endDateStr = "08/04/2023";
        long nodays = getNoOfDays(startDateStr, endDateStr);
        makepayments_getdatelist.setText(startDateStr + " - " + endDateStr + " (" + nodays + " days)");
        return nodays;
    }

    public long getNoOfDays(String start_date_str, String end_date_str) {
        long nodays = 0;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date endDate = sdf.parse(end_date_str);
            Date startDate = sdf.parse(start_date_str);
            long diff = endDate.getTime() - startDate.getTime();
            nodays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS)+1;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return nodays;
    }

    @SuppressLint("SetTextI18n")
    public void addNewLoan(Double curr_loan) {
        BottomSheetDialog addloan_btmdialog = new BottomSheetDialog(MakePaymentsActivity.this);
        addloan_btmdialog.setContentView(R.layout.update_loan_btmdialog);
        addloan_btmdialog.setCanceledOnTouchOutside(true);
        addloan_btmdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView addloan_currloan = addloan_btmdialog.findViewById(R.id.addloan_currloan);
        TextInputLayout addloan_val = addloan_btmdialog.findViewById(R.id.addloan_val);
        Button addloan_submitbtn = addloan_btmdialog.findViewById(R.id.addloan_submitbtn);
        addloan_submitbtn.setEnabled(false);
        addloan_submitbtn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.light_grey)));

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
            new_loan_val = loan_val;
            Map<String, Object> new_loan_data = new HashMap<>();
            new_loan_data.put("loan_amount", loan_val);
            new_loan_data.put("loan_timestamp", Timestamp.now());
            new_loan_data.put("loan_balance", (curr_loan + loan_val));
            new_loan_data.put("loan_emp_reference", db.document(emp_reference));
            new_loan_data.put("loan_company_reference", db.document(comp_path));
            new_loan_data.put("loan_from_sal", true);
            new_loan_data.put("loan_pay_status", false);

            if(functions.checkInternetConnection(this)){
                db.collection("Loans")
                        .add(new_loan_data)
                        .addOnCompleteListener(task12 -> {
                            if (task12.isSuccessful()) {
                                addloan_btmdialog.dismiss();
                                loan_amount = loan_amount + loan_val;
                                db.collection("Employees")
                                        .document(db.document(emp_reference).getId())
                                        .update("emp_advance_loans", loan_amount)
                                        .addOnCompleteListener(task2 -> {
                                            if (task2.isSuccessful()) {
                                                loadDataForMakePayment();
                                                Snackbar.make(paymentrecords_mainlay, "New Loan of Rs " + loan_val + " added successfully", Snackbar.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        });
            }
            else functions.no_internet_dialog(this, false);
        });

        addloan_btmdialog.show();
    }

    public void PaymentProcess(Map<String, Object> update_emp, Dialog dialog) {
        if(functions.checkInternetConnection(this)){
            db.collection("Employees")
                    .document(db.document(emp_reference).getId())
                    .update(update_emp)
                    .addOnCompleteListener(task1 -> {
                        String msg = (task1.isSuccessful()) ? "Payment record of " + functions.getTodayDate("dd/MM/yyyy") + " added successfully.." : "Payment Success but some error occured !! Please try to check payment later...";
//                    String msg = (task1.isSuccessful()) ? "Payment record of " + "08-04-2023" + " added successfully.." : "Payment Success but some error occured !! Please try to check payment later...";
                        Snackbar.make(paymentrecords_mainlay, "", Snackbar.LENGTH_SHORT).show();
                        dialog.dismiss();
                        Snackbar.make(paymentrecords_mainlay, msg, Snackbar.LENGTH_SHORT).show();
                        new Handler().postDelayed(() -> {
                        /*Intent pageintent = new Intent(getApplicationContext(), ViewPaymentsActivity.class);
                        pageintent.putExtra("emp_name", emp_name);
                        pageintent.putExtra("emp_contact", emp_contact);
                        pageintent.putExtra("emp_reference_id", emp_reference);
                        pageintent.putExtra("emp_advance_loans", loan_amount);
                        pageintent.putExtra("emp_sal_arrears", arrears_amount);
                        pageintent.putExtra("emp_sal_paid", pemp_wage_paid);
                        pageintent.putExtra("emp_sal_total", total_amount_giving);
                        startActivity(pageintent);*/
                            finish();
                        }, 1500);
                    });
        }
        else functions.no_internet_dialog(this, false);
    }

    public final TextWatcher loanTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence val, int i, int i1, int i2) {
            String loan_val = makepayments_amtadvanceloan_giving.getEditText().getText().toString();
            updateLoanWagesEditText(loan_val, makepayments_amtadvanceloan_giving);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public final TextWatcher wagesTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence val, int i, int i1, int i2) {
            String wage_val = makepayments_amtpending_giving.getEditText().getText().toString();
            loan_payable_amount= !wage_val.equals("") ? Double.parseDouble(wage_val) : 0;
            makepayments_amtadvanceloan_giving.getEditText().setFilters(new InputFilter[]{new InputFilterMinMax(0.0, loan_payable_amount)});
            makepayments_amtadvanceloan_giving.getEditText().setText(String.valueOf(0));
            updateLoanWagesEditText(wage_val, makepayments_amtpending_giving);
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void updateLoanWagesEditText(@NonNull String text, TextInputLayout edLayout) {
        if (text.equals("")) {
            edLayout.setErrorEnabled(true);
            edLayout.setError("Please enter a number !!");
            edLayout.requestFocus();
            updateSubmitButton(false);
        } else {
            edLayout.setError(null);
            edLayout.setErrorEnabled(false);
            updateSubmitButton(true);
            updateGivingTotalAmount(false);
        }
    }

    @SuppressLint("SetTextI18n")
    public void updateGivingTotalAmount(boolean check) {
        String pending_giving_str = makepayments_amtpending_giving.getEditText().getText().toString();
        Double sal_amt = Double.parseDouble((!pending_giving_str.equals("") ? pending_giving_str : "0.0"));
        String loan_str = makepayments_amtadvanceloan_giving.getEditText().getText().toString();
        Double sal_loan = Double.parseDouble(!loan_str.equals("") ? loan_str : "0.0");
        total_sal_giving = sal_amt + arrears_amount - sal_loan;
        makepayments_total.setText("₹ " + total_sal_giving);
        makepayments_final_amount_giving.getEditText().setFilters(new InputFilter[]{new InputFilterMinMax(0.0, total_sal_giving)});
        makepayments_final_amount_giving.getEditText().setText("" + total_sal_giving);
        if (check) {
            makepayments_amtpending_giving.getEditText().addTextChangedListener(wagesTextWatcher);
            makepayments_amtadvanceloan_giving.getEditText().addTextChangedListener(loanTextWatcher);
        }
    }

    public void makePaymentsInits() {
        makepayments_nestlay = findViewById(R.id.makepayments_nestlay);
        makepayments_paymentbtnlay = findViewById(R.id.makepayments_paymentbtnlay);
        makepayments_getdatelist = findViewById(R.id.makepayments_getdatelist);
        makepayments_present = findViewById(R.id.makepayments_present);
        makepayments_halfday = findViewById(R.id.makepayments_halfday);
        makepayments_holiday= findViewById(R.id.makepayments_holiday);
        makepayments_absent = findViewById(R.id.makepayments_absent);
        makepayments_amtpending = findViewById(R.id.makepayments_amtpending);
        makepayments_arrears = findViewById(R.id.makepayments_arrears);
        makepayments_total = findViewById(R.id.makepayments_total);
        makepayments_amtadvanceloan = findViewById(R.id.makepayments_amtadvanceloan);
        makepayments_amtpending_giving = findViewById(R.id.makepayments_amtpending_giving);
        makepayments_amtadvanceloan_giving = findViewById(R.id.makepayments_amtadvanceloan_giving);
        makepayments_todaydate = findViewById(R.id.makepayments_todaydate);
        makepayments_submitbtn = findViewById(R.id.makepayments_submitbtn);
        makepayments_addloanbtn = findViewById(R.id.makepayments_addloanbtn);
        makepayments_back_btn = findViewById(R.id.makepayments_back_btn);
        paymentrecords_mainlay = findViewById(R.id.paymentrecords_mainlay);
        makepayments_baserate = findViewById(R.id.makepayments_baserate);
        makepayments_finalpaymentval = findViewById(R.id.makepayments_finalpaymentval);
        makepayments_final_amount_giving = findViewById(R.id.makepayments_final_amount_giving);
        makepayments_empname = findViewById(R.id.makepayments_empname);
        makepayments_empcontact = findViewById(R.id.makepayments_empcontact);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        updateSubmitButton(false);
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
    }

    /*makepayments_editbaserate_btn.setOnClickListener(v -> {
        db.collection("CompanyBaseRate")
                .document(comp_path != null ? db.document(comp_path).getId() : "")
                .collection("rate_collection")
                .orderBy("company_rate_date", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                        Date company_latestrate_date = doc.getTimestamp("company_rate_date").toDate();
                        Date curr_date = functions.getDateFromString(functions.getTodayDate("dd-MM-yyyy"), "dd-MM-yyy");

                        Calendar cal1 = Calendar.getInstance();
                        cal1.setTime(company_latestrate_date);
                        int year1 = cal1.get(Calendar.YEAR);
                        int month1 = cal1.get(Calendar.MONTH);

                        Calendar cal2 = Calendar.getInstance();
                        cal2.setTime(curr_date);
                        int year2 = cal2.get(Calendar.YEAR);
                        int month2 = cal2.get(Calendar.MONTH);

                        int monthsDifference = (year2 - year1) * 12 + (month2 - month1);

                        if (monthsDifference >= 4) {
//                                Snackbar.make(paymentrecords_mainlay,"Edit Success",Snackbar.LENGTH_SHORT).show();
                            BottomSheetDialog updatebaserate_btmdialog = new BottomSheetDialog(MakePaymentsActivity.this, R.style.BottomSheetTheme);
                            updatebaserate_btmdialog.setContentView(R.layout.updatebaserate_btmdialog);
                            updatebaserate_btmdialog.setCanceledOnTouchOutside(true);
                            updatebaserate_btmdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            TextInputLayout editbaserate_val= updatebaserate_btmdialog.findViewById(R.id.editbaserate_val);
                            Button editbaserate_update_btn= updatebaserate_btmdialog.findViewById(R.id.editbaserate_update_btn);
                            editbaserate_update_btn.setEnabled(false);
                            editbaserate_update_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),R.color.light_grey)));
                            editbaserate_val.getEditText().addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                }

                                @Override
                                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                    String rate_num_txt= editbaserate_val.getEditText().getText().toString();
                                    long rate_num= !rate_num_txt.equals("") ? Long.parseLong(rate_num_txt) : 0;
                                    if(rate_num <= 0) {
                                        editbaserate_update_btn.setEnabled(false);
                                        editbaserate_update_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),R.color.light_grey)));
                                        editbaserate_val.setError("Please enter a value greater than zero !!");
                                        editbaserate_val.requestFocus();
                                    } else{
                                        editbaserate_update_btn.setEnabled(true);
                                        editbaserate_update_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(),R.color.maincolor)));
                                        editbaserate_val.setError(null);
                                        editbaserate_val.clearFocus();
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable editable) {

                                }
                            });

                            editbaserate_update_btn.setOnClickListener(v1->{
                                String rate_num_txt= editbaserate_val.getEditText().getText().toString();
                                long rate_num= !rate_num_txt.equals("") ? Long.parseLong(rate_num_txt) : 1;
                                Map<String,Object> new_rate= new HashMap<>();
                                new_rate.put("company_rate_date", Timestamp.now());
                                new_rate.put("company_rate_val", rate_num);
                                db.collection("CompanyBaseRate")
                                        .document(comp_path != null ? db.document(comp_path).getId() : "")
                                        .collection("rate_collection")
                                        .document()
                                        .set(new_rate)
                                        .addOnCompleteListener(task14 -> {
                                            if(task14.isSuccessful()){
                                                updatebaserate_btmdialog.dismiss();
                                            }
                                        });
                            });

                            updatebaserate_btmdialog.show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setTitle("Rate Updation Failed !!")
                                    .setMessage("You have recently updated the rate.\nPlease try to edit the rate later atleast after a month.")
                                    .setPositiveButton("Ok", (dialogInterface, i) -> dialogInterface.cancel());

                            AlertDialog dialog= builder.create();
                            dialog.show();
                        }
                    }
                });
    });*/
}