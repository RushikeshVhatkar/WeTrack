package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rushikeshsantoshv.wetrack.Adapters.LoanDisplayAdapter;
import com.rushikeshsantoshv.wetrack.Adapters.PaymentDetailsAdapter;
import com.rushikeshsantoshv.wetrack.DataModels.EmployeeLoansModel;
import com.rushikeshsantoshv.wetrack.DataModels.EmployeePaymentsModel;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ViewPaymentsActivity extends AppCompatActivity {

    RelativeLayout viewpayments_mainlay;
    ImageButton viewpayments_month_perform_infobtn, viewpayments_back_btn, viewpayments_calender_btn;
    Spinner viewpayments_month_dropdown, viewpayments_year_dropdown;
    TextView viewpayments_present, viewpayments_halfday, viewpayments_absent, viewpayments_holiday, viewpayments_weekoff, viewpayments_notset;
    TextView viewpayments_addpayment_btn, payments_paymentempty_txt, payments_loanempty_txt;
    TextView viewpayments_empname, viewpayments_empcontact;

    RecyclerView paymentrecords_paymentrec, paymentrecords_loanrec;
    ArrayList<EmployeeLoansModel> emp_loan_arr = new ArrayList<>();
    ArrayList<EmployeePaymentsModel> emp_payment_arr = new ArrayList<>();
    PaymentDetailsAdapter paymentDetailsAdapter;
    LoanDisplayAdapter loanDisplayAdapter;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Intent emp_details_intent;
    String comp_path;
    Long curr_fin_year;
    Long min_fin_year;
    ArrayList<String> month_arr = new ArrayList<>();
    ArrayList<String> year_arr = new ArrayList<>();
    Functions functions = new Functions();

    String emp_reference_id, emp_name, emp_contact;
    Double emp_advance_loans, emp_sal_arrears, emp_sal_paid, emp_sal_total;

    @SuppressLint({"NotifyDataSetChanged", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_payments);
        functions.coloredStatusBarDarkTextDesign(ViewPaymentsActivity.this, R.color.maincolor_light, R.color.white);

        viewPaymentsInits();

        viewpayments_back_btn.setOnClickListener(v -> finish());

        viewpayments_month_perform_infobtn.setOnClickListener(v -> {
            Dialog dialog = functions.createDialogBox(ViewPaymentsActivity.this, R.layout.attendance_remark_info_dialog, true);
            ImageButton arid_closebtn = dialog.findViewById(R.id.arid_closebtn);
            arid_closebtn.setOnClickListener(v1 -> dialog.dismiss());
            dialog.show();
        });

        viewpayments_addpayment_btn.setOnClickListener(v -> {
            if (emp_details_intent != null) {
                Intent intent = new Intent(getApplicationContext(), MakePaymentsActivity.class);
                intent.putExtra("emp_name", emp_name);
                intent.putExtra("emp_contact", emp_contact);
                intent.putExtra("emp_reference_id", emp_reference_id);
                intent.putExtra("emp_advance_loans", emp_advance_loans);
                intent.putExtra("emp_sal_arrears", emp_sal_arrears);
                intent.putExtra("emp_sal_paid", emp_sal_paid);
                intent.putExtra("emp_sal_total", emp_sal_total);
                startActivity(intent);
            } else {
                Snackbar.make(viewpayments_mainlay, "Unable to go to payment page !! Please try again", Snackbar.LENGTH_SHORT).show();
            }
        });

        viewpayments_year_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (Integer.parseInt(year_arr.get(i)) == min_fin_year) {
                    int min_month = Integer.parseInt(functions.getStringFromDate(functions.getDateFromString(functions.getSharedPrefsValue(
                            getApplicationContext(), "user_data", "company_created", "string",
                            functions.getTodayDate("dd, MMM, yyyy")), "dd, MMM, yyyy"), "MM")) - 1;
                    month_arr = loadMonthItems(min_month, 12);
                } else if (Integer.parseInt(year_arr.get(i)) == Integer.parseInt(functions.getTodayDate("yyyy"))) {
                    month_arr = loadMonthItems(0, Integer.parseInt(functions.getTodayDate("MM")));
                } else {
                    month_arr = loadMonthItems(0, 12);
                }

                ArrayAdapter monthAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, month_arr);
                viewpayments_month_dropdown.setAdapter(monthAdapter);
                viewpayments_month_dropdown.setSelection(month_arr.size() - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        viewpayments_month_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int selected_month = functions.getThreeLetterMonths().indexOf(month_arr.get(i).toLowerCase().substring(0, 3));
                int selected_year = Integer.parseInt(viewpayments_year_dropdown.getSelectedItem().toString());
                loadPaymentAndLoanData(selected_month, selected_year);
                setMonthlyAttendancesData(month_arr.indexOf(viewpayments_month_dropdown.getSelectedItem().toString()),
                        Integer.parseInt(viewpayments_year_dropdown.getSelectedItem().toString()));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        if(functions.checkInternetConnection(this)){
            loadPaymentAndLoanData(Integer.parseInt(functions.getTodayDate("MM"))
                    , Integer.parseInt(functions.getTodayDate("yyyy")));

            setMonthlyAttendancesData(month_arr.indexOf(viewpayments_month_dropdown.getSelectedItem().toString()),
                    Integer.parseInt(viewpayments_year_dropdown.getSelectedItem().toString()));
        }
        else functions.no_internet_dialog(this, true);

    }

    @SuppressLint("SetTextI18n")
    private void setMonthlyAttendancesData(int selected_month, int selected_year) {
        if (emp_details_intent != null && comp_path != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(selected_year, selected_month, 1);
            calendar.set(Calendar.AM_PM, Calendar.AM);
            Timestamp start_timestamp = new Timestamp(functions.getDateFromString(functions.getStringFromDate(calendar.getTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));
            calendar.add(Calendar.MONTH, 1);
            Timestamp end_timestamp = new Timestamp(functions.getDateFromString(functions.getStringFromDate(calendar.getTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));

            Log.e("check_date", "The start is : " + start_timestamp.toDate() + " and end date is : " + end_timestamp.toDate());

            if(functions.checkInternetConnection(this)){
                db.collection("Attendances")
                        .whereEqualTo("attend_company_reference", db.document(comp_path))
                        .whereEqualTo("attend_emp_reference", db.document(emp_reference_id))
                        .whereGreaterThanOrEqualTo("attend_date", start_timestamp)
                        .whereLessThan("attend_date", end_timestamp)
                        .get()
                        .addOnCompleteListener(task -> {
                            int present_count = 0, halfday_count = 0, absent_count = 0;
                            final int[] holiday_count = {0};
                            final int[] not_set_count = {0};
                            if (task.isSuccessful()) {
                                if (task.getResult().size() > 0) {
                                    for (DocumentSnapshot emp : task.getResult().getDocuments()) {
                                        if (emp.getLong("attend_status") == 1) {
                                            present_count++;
                                        } else if (emp.getLong("attend_status") == 2) {
                                            halfday_count++;
                                        } else if (emp.getLong("attend_status") == 3) {
                                            absent_count++;
                                        }
                                    }
                                } else {
                                    Log.e("date_arr", "No Attendances Available !!");
                                }
                                viewpayments_present.setText("" + present_count);
                                viewpayments_absent.setText("" + absent_count);
                                viewpayments_halfday.setText("" + halfday_count);

                                int finalPresent_count = present_count;
                                int finalHalfday_count = halfday_count;
                                int finalAbsent_count = absent_count;
                                db.collection("Companies")
                                        .document(db.document(comp_path).getId())
                                        .get()
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful() && task1.getResult().exists() && task1.getResult().get("company_holidays") != null) {
                                                ArrayList<Timestamp> holiday_list = (ArrayList<Timestamp>) task1.getResult().get("company_holidays");

                                                for (Timestamp timestamp : holiday_list) {
                                                    if (timestamp.compareTo(start_timestamp) > 0 && timestamp.compareTo(end_timestamp) < 0) {
                                                        holiday_count[0]++;
                                                    }
                                                }

                                                viewpayments_holiday.setText(""+holiday_count[0]);
                                                int year= Integer.parseInt(functions.getStringFromDate(end_timestamp.toDate(), "yyyy"));
                                                int month= Integer.parseInt(functions.getStringFromDate(end_timestamp.toDate(), "MM"));
                                                not_set_count[0] += getTotalNumberOfDaysFromMonth(month, year) - (finalPresent_count + finalHalfday_count
                                                        + finalAbsent_count +holiday_count[0]);
                                                viewpayments_notset.setText(""+not_set_count[0]);
                                            }
                                        });

                            } else {
                                String error = task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error !!";
                                Log.e("firebase_error", "" + error);
                                viewpayments_present.setText("No data");
                                viewpayments_absent.setText("No data");
                                viewpayments_halfday.setText("No data");
                            }
                        });
            }
            else functions.no_internet_dialog(this, false);
        }
    }

    private int getTotalNumberOfDaysFromMonth(int month, int year){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    private void loadPaymentAndLoanData(int selected_month, int selected_year) {

        if (emp_loan_arr.size() > 0) emp_loan_arr.clear();
        if (emp_payment_arr.size() > 0) emp_payment_arr.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(selected_year, selected_month, 1);
        calendar.set(Calendar.AM_PM, Calendar.AM);
        Timestamp start_timestamp = new Timestamp(functions.getDateFromString(functions.getStringFromDate(calendar.getTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));
        calendar.add(Calendar.MONTH, 1);
        Timestamp end_timestamp = new Timestamp(functions.getDateFromString(functions.getStringFromDate(calendar.getTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));

        if(functions.checkInternetConnection(this)){
            db.collection("Payments")
                    .whereEqualTo("pemp_company_reference", db.document(comp_path))
                    .whereEqualTo("pemp_emp_reference", db.document(emp_reference_id))
                    .orderBy("pemp_start_date", Query.Direction.ASCENDING)
                    .whereGreaterThanOrEqualTo("pemp_start_date", start_timestamp)
                    .whereLessThan("pemp_start_date", end_timestamp)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                paymentrecords_paymentrec.setVisibility(View.VISIBLE);
                                payments_paymentempty_txt.setVisibility(View.GONE);
                                List<DocumentSnapshot> payment_arr = task.getResult().getDocuments();
                                if (emp_loan_arr.size() > 0) emp_loan_arr.clear();
                                if (emp_payment_arr.size() > 0) emp_payment_arr.clear();
                                for (int i = 0; i < payment_arr.size(); i++) {

                                    DocumentSnapshot doc = payment_arr.get(i);

                                    String check_comp_start = functions.getStringFromDate(functions.getDateFromString(functions.getSharedPrefsValue(
                                            getApplicationContext(), "user_data", "company_created", "string",
                                            functions.getTodayDate("dd, MMM, yyyy")), "dd, MMM, yyyy"), "dd/MM/yyyy");
                                    String curr_start = functions.getStringFromDate(doc.getTimestamp("pemp_start_date").toDate(), "dd/MM/yyyy");
                                    String curr_end = functions.getStringFromDate(doc.getTimestamp("pemp_timestamp").toDate(), "dd/MM/yyyy");

                                    if (curr_start.equals(curr_end) && check_comp_start.equals(curr_start))
                                        continue;

                                    emp_payment_arr.add(new EmployeePaymentsModel(
                                            doc.getDocumentReference("pemp_company_reference"),
                                            doc.getDocumentReference("pemp_emp_reference"),
                                            doc.getDouble("pemp_base_rate"),
                                            doc.getDouble("pemp_loan_paid"),
                                            doc.getDouble("pemp_loan_total"),
                                            doc.getDouble("pemp_sal_arrears"),
                                            doc.getDouble("pemp_sal_paid"),
                                            doc.getDouble("pemp_sal_total"),
                                            doc.getDouble("pemp_wage_paid"),
                                            doc.getDouble("pemp_wage_total"),
                                            doc.getDouble("pemp_loan_balance"),
                                            doc.getTimestamp("pemp_timestamp"),
                                            doc.getTimestamp("pemp_start_date")
                                    ));
                                }

                                paymentrecords_paymentrec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                paymentDetailsAdapter = new PaymentDetailsAdapter(getApplicationContext(), ViewPaymentsActivity.this, emp_payment_arr);
                                paymentrecords_paymentrec.setAdapter(paymentDetailsAdapter);
                                paymentDetailsAdapter.notifyDataSetChanged();
                            } else {
                                paymentrecords_paymentrec.setVisibility(View.GONE);
                                payments_paymentempty_txt.setVisibility(View.VISIBLE);
                            }
                        } else {
                            String error_msg = task != null && task.getException() != null && task.getException().getLocalizedMessage() != null
                                    ? task.getException().getLocalizedMessage() : "No Error !!";
                            Log.e("firebase_error", "The error : " + error_msg);
                        }
                    });

            db.collection("Loans")
                    .whereEqualTo("loan_company_reference", db.document(comp_path))
                    .whereEqualTo("loan_emp_reference", db.document(emp_reference_id))
                    .orderBy("loan_timestamp", Query.Direction.ASCENDING)
                    .whereGreaterThan("loan_timestamp", start_timestamp)
                    .whereLessThan("loan_timestamp", end_timestamp)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                payments_loanempty_txt.setVisibility(View.GONE);
                                paymentrecords_loanrec.setVisibility(View.VISIBLE);
                                List<DocumentSnapshot> loan_res = task.getResult().getDocuments();
                                for (DocumentSnapshot doc : loan_res) {
                                    emp_loan_arr.add(new EmployeeLoansModel(
                                            doc.getDouble("loan_amount"),
                                            doc.getDouble("loan_balance"),
                                            doc.getDocumentReference("loan_company_reference"),
                                            doc.getDocumentReference("loan_emp_reference"),
                                            doc.getTimestamp("loan_timestamp"),
                                            doc.getBoolean("loan_from_sal"),
                                            doc.getBoolean("loan_pay_status")
                                    ));
                                }
                                paymentrecords_loanrec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                loanDisplayAdapter = new LoanDisplayAdapter(getApplicationContext(), emp_loan_arr);
                                paymentrecords_loanrec.setAdapter(loanDisplayAdapter);
                                loanDisplayAdapter.notifyDataSetChanged();
                            } else {
                                payments_loanempty_txt.setVisibility(View.VISIBLE);
                                paymentrecords_loanrec.setVisibility(View.GONE);
                            }
                        } else {
                            String error_msg = task != null && task.getException() != null && task.getException().getLocalizedMessage() != null
                                    ? task.getException().getLocalizedMessage() : "No Error !!";
                            Log.e("firebase_error", "The error : " + error_msg);
                        }
                    });
        }
        else functions.no_internet_dialog(this, false);

    }

    private void viewPaymentsInits() {
        emp_details_intent = getIntent();
        viewpayments_mainlay = findViewById(R.id.viewpayments_mainlay);
        viewpayments_back_btn = findViewById(R.id.viewpayments_back_btn);
        viewpayments_calender_btn = findViewById(R.id.viewpayments_calender_btn);
        viewpayments_month_dropdown = findViewById(R.id.viewpayments_month_dropdown);
        viewpayments_year_dropdown = findViewById(R.id.viewpayments_year_dropdown);
        viewpayments_present = findViewById(R.id.viewpayments_present);
        viewpayments_halfday = findViewById(R.id.viewpayments_halfday);
        viewpayments_absent = findViewById(R.id.viewpayments_absent);
        viewpayments_holiday = findViewById(R.id.viewpayments_holiday);
        viewpayments_notset = findViewById(R.id.viewpayments_notset);
        viewpayments_month_perform_infobtn = findViewById(R.id.viewpayments_month_perform_infobtn);
        viewpayments_addpayment_btn = findViewById(R.id.viewpayments_addpayment_btn);
        paymentrecords_paymentrec = findViewById(R.id.paymentrecords_paymentrec);
        paymentrecords_loanrec = findViewById(R.id.paymentrecords_loanrec);
        payments_paymentempty_txt = findViewById(R.id.payments_paymentempty_txt);
        payments_loanempty_txt = findViewById(R.id.payments_loanempty_txt);
        viewpayments_empname = findViewById(R.id.viewpayments_empname);
        viewpayments_empcontact = findViewById(R.id.viewpayments_empcontact);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (emp_details_intent != null) {
            emp_name = emp_details_intent.getStringExtra("emp_name");
            emp_contact = emp_details_intent.getStringExtra("emp_contact");
            emp_reference_id = emp_details_intent.getStringExtra("emp_reference_id");
            emp_advance_loans = emp_details_intent.getDoubleExtra("emp_advance_loans", 0.0);
            emp_sal_arrears = emp_details_intent.getDoubleExtra("emp_sal_arrears", 0.0);
            emp_sal_paid = emp_details_intent.getDoubleExtra("emp_sal_paid", 0.0);
            emp_sal_total = emp_details_intent.getDoubleExtra("emp_sal_total", 0.0);
            viewpayments_empname.setText(emp_name);
            viewpayments_empcontact.setText(emp_contact);
        } else {
            viewpayments_empname.setText("Not Available");
            viewpayments_empcontact.setText("Not Available");
        }

        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);

        comp_path = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        curr_fin_year = functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                Long.parseLong(functions.getTodayDate("yyyy"));
        min_fin_year = Long.parseLong(functions.getStringFromDate(functions.getDateFromString(functions.getSharedPrefsValue(
                getApplicationContext(), "user_data", "company_created", "string",
                functions.getTodayDate("dd, MMM, yyyy")), "dd, MMM, yyyy"), "yyyy"));

        Log.e("ts_check", "The curr_fin_year is : "+curr_fin_year+" & min_fin_year is : "+min_fin_year);

        month_arr = loadMonthItems(0, Integer.parseInt(functions.getTodayDate("MM")));
        year_arr = loadYearItems(Math.toIntExact(min_fin_year), Integer.parseInt(functions.getTodayDate("yyyy")));

        ArrayAdapter monthAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, month_arr);
        ArrayAdapter yearAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, year_arr);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        viewpayments_month_dropdown.setAdapter(monthAdapter);
        viewpayments_year_dropdown.setAdapter(yearAdapter);

        viewpayments_month_dropdown.setSelection(month_arr.size() - 1);
        viewpayments_year_dropdown.setSelection(year_arr.size() - 1);

    }

    @NonNull
    public ArrayList<String> loadYearItems(int start, int end) {
        ArrayList<String> arr = new ArrayList<>();
        for (int i = start; i <= end; i++) arr.add(String.valueOf(i));
        return arr;
    }

    public ArrayList<String> loadMonthItems(int start, int end) {
        ArrayList<String> arr = new ArrayList<>();
        arr.add("January");
        arr.add("February");
        arr.add("March");
        arr.add("April");
        arr.add("May");
        arr.add("June");
        arr.add("July");
        arr.add("August");
        arr.add("September");
        arr.add("October");
        arr.add("November");
        arr.add("December");
        return new ArrayList<>(arr.subList(start, end));
    }
}