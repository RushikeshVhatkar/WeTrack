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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeAnalysisActivity extends AppCompatActivity {

    LinearLayout emp_analysis_mainlay;
    RatingBar emp_analysis_monthly_ratebar, emp_analysis_yearly_ratebar;
    Spinner emp_analysis_month_dropdown, emp_analysis_year_dropdown;
    ImageButton emp_analysis_back_btn;
    TextView emp_analysis_empname, emp_analysis_empcontact, emp_analysis_loanbalance;
    PieChart emp_analysis_monthly_chart, emp_analysis_yearly_chart;

    String comp_path;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Functions functions = new Functions();
    ArrayList<String> month_arr= new ArrayList<>();
    ArrayList<String> year_arr= new ArrayList<>();

    Long curr_fin_year;
    Long min_fin_year;

    Intent employee_val;
    private String emp_name, emp_contact, emp_reference;
    long present_count = 0, half_day_count = 0, absent_count = 0;
    long y_present_count = 0, y_half_day_count = 0, y_absent_count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_analysis);

        new Functions().coloredStatusBarDarkTextDesign(this, R.color.maincolor_light, R.color.white);

        employeeAnalysisInits();
        emp_analysis_back_btn.setOnClickListener(v -> finish());

        if(functions.checkInternetConnection(this)){
            loadMonthlyEmployeesAnalysis(0,Integer.parseInt(functions.getTodayDate("yyyy")));
            loadYearlyEmployeesAnalysis(Integer.parseInt(functions.getTodayDate("yyyy")));
        }
        else functions.no_internet_dialog(this, true);

        emp_analysis_year_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(Integer.parseInt(year_arr.get(i))==min_fin_year){
                    int min_month = Integer.parseInt(functions.getStringFromDate(functions.getDateFromString(functions.getSharedPrefsValue(
                            getApplicationContext(), "user_data", "company_created", "string",
                            functions.getTodayDate("dd, MMM, yyyy")), "dd, MMM, yyyy"), "MM"))-1;
                    month_arr=  loadMonthItems(min_month,12);
                }
                else if(Integer.parseInt(year_arr.get(i))==Integer.parseInt(functions.getTodayDate("yyyy"))){
                    month_arr=  loadMonthItems(0,Integer.parseInt(functions.getTodayDate("MM")));
                }
                else{
                    month_arr=  loadMonthItems(0,12);
                }

                ArrayAdapter monthAdapter = new ArrayAdapter(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,month_arr);
                emp_analysis_month_dropdown.setAdapter(monthAdapter);
                emp_analysis_month_dropdown.setSelection(month_arr.size()-1);

                /*int selected_month= functions.getThreeLetterMonths().indexOf(emp_analysis_month_dropdown.getSelectedItem().toString().toLowerCase().substring(0,3))+1;
                int selected_year= Integer.parseInt(year_arr.get(i));
                loadMonthlyEmployeesAnalysis(selected_month,selected_year);*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        emp_analysis_month_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                int selected_month= functions.getThreeLetterMonths().indexOf(month_arr.get(i).toLowerCase().substring(0,3));
                int selected_year= Integer.parseInt(emp_analysis_year_dropdown.getSelectedItem().toString());
                if(functions.checkInternetConnection(EmployeeAnalysisActivity.this)){
                    loadMonthlyEmployeesAnalysis(selected_month,selected_year);
                    loadYearlyEmployeesAnalysis(selected_year);
                }
                else functions.no_internet_dialog(EmployeeAnalysisActivity.this, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void employeeAnalysisInits() {
        emp_analysis_mainlay = findViewById(R.id.emp_analysis_mainlay);
        emp_analysis_monthly_ratebar = findViewById(R.id.emp_analysis_monthly_ratebar);
        emp_analysis_yearly_ratebar = findViewById(R.id.emp_analysis_yearly_ratebar);
        emp_analysis_month_dropdown = findViewById(R.id.emp_analysis_month_dropdown);
        emp_analysis_year_dropdown = findViewById(R.id.emp_analysis_year_dropdown);
        emp_analysis_back_btn = findViewById(R.id.emp_analysis_back_btn);
        emp_analysis_empname = findViewById(R.id.emp_analysis_empname);
        emp_analysis_loanbalance= findViewById(R.id.emp_analysis_loanbalance);
        emp_analysis_empcontact = findViewById(R.id.emp_analysis_empcontact);
        emp_analysis_monthly_chart = findViewById(R.id.emp_analysis_monthly_chart);
        emp_analysis_yearly_chart = findViewById(R.id.emp_analysis_yearly_chart);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        comp_path = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        curr_fin_year = functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                Long.parseLong(functions.getTodayDate("yyyy"));
        min_fin_year = Long.parseLong(functions.getStringFromDate(functions.getDateFromString(functions.getSharedPrefsValue(
                getApplicationContext(), "user_data", "company_created", "string",
                functions.getTodayDate("dd, MMM, yyyy")), "dd, MMM, yyyy"), "yyyy"));
        employee_val = getIntent();

        month_arr=  loadMonthItems(0,Integer.parseInt(functions.getTodayDate("MM")));
        year_arr= loadYearItems(Math.toIntExact(min_fin_year), Integer.parseInt(functions.getTodayDate("yyyy")));

        ArrayAdapter monthAdapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item,month_arr);
        ArrayAdapter yearAdapter= new ArrayAdapter(this, android.R.layout.simple_spinner_item,year_arr);

        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        emp_analysis_month_dropdown.setAdapter(monthAdapter);
        emp_analysis_year_dropdown.setAdapter(yearAdapter);

        emp_analysis_month_dropdown.setSelection(month_arr.size()-1);
        emp_analysis_year_dropdown.setSelection(year_arr.size()-1);
    }

    @NonNull
    ArrayList<String> loadYearItems(int start, int end){
        ArrayList<String> arr= new ArrayList<>();
        for(int i=start;i<=end;i++) arr.add(String.valueOf(i));
        return arr;
    }

    ArrayList<String> loadMonthItems(int start, int end){
        ArrayList<String> arr= new ArrayList<>();
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

        int length= end-start;
        ArrayList<String> sorted_arr= new ArrayList<>();
        for(int i= start; i < end; i++){
            sorted_arr.add(arr.get(i));
        }
        return sorted_arr;
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadYearlyEmployeesAnalysis(int selected_year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(selected_year, 0, 1);
        Timestamp start_timestamp= new Timestamp(functions.getDateFromString(functions.getStringFromDate(calendar.getTime(),
                "dd/MM/yyyy"),"dd/MM/yyyy"));
        calendar.add(Calendar.YEAR, 1);
        Timestamp end_timestamp= new Timestamp(functions.getDateFromString(functions.getStringFromDate(calendar.getTime(),
                "dd/MM/yyyy"),"dd/MM/yyyy"));

        Log.e("date_check","The timestamps are : "+start_timestamp.toDate()+" & "+end_timestamp.toDate());

        if (employee_val != null) {
            emp_reference = employee_val.getStringExtra("emp_reference_id") != null ? employee_val.getStringExtra("emp_reference_id") : db.document("Employees/sampledoc").getPath();
            Dialog loading_dialog = functions.createDialogBox(this, R.layout.loading_dialog, false);
            loading_dialog.show();

            db.collection("Employees")
                    .document(db.document(emp_reference).getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            db.collection("Attendances")
                                    .whereEqualTo("attend_emp_reference", db.document(emp_reference))
                                    .whereGreaterThanOrEqualTo("attend_date",start_timestamp)
                                    .whereLessThanOrEqualTo("attend_date",end_timestamp)
                                    .orderBy("attend_date", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            y_present_count = 0;
                                            y_half_day_count = 0;
                                            y_absent_count = 0;
                                            List<DocumentSnapshot> attendance_arr = task1.getResult().getDocuments();
                                            Log.e("firebase_size","The size of the arr is : "+attendance_arr.size());
                                            for (DocumentSnapshot attendance : attendance_arr) {
                                                y_present_count += (attendance.getLong("attend_status") == 1) ? 1 : 0;
                                                y_half_day_count += (attendance.getLong("attend_status") == 2) ? 1 : 0;
                                                y_absent_count += (attendance.getLong("attend_status") == 3) ? 1 : 0;
                                            }

                                            Log.e("attend_count","Present - "+present_count+" & Absent - "+absent_count+" & Half Day - "+half_day_count);
                                            ArrayList<PieEntry> data = new ArrayList<>();
                                            data.add(new PieEntry(y_present_count, "Present"));
                                            data.add(new PieEntry(y_half_day_count, "Half Day"));
                                            data.add(new PieEntry(y_absent_count, "Absent"));

                                            PieDataSet pieDataSet = new PieDataSet(data, " - Counts");
                                            pieDataSet.setColors(new int[]{R.color.my_green, R.color.my_yellow, R.color.my_red, R.color.light_grey}, getApplicationContext());
                                            pieDataSet.setValueTextColor(android.R.color.darker_gray);
                                            pieDataSet.setValueTextSize(15f);
                                            PieData pieData = new PieData(pieDataSet);
                                            emp_analysis_yearly_chart.setData(pieData);
                                            emp_analysis_yearly_chart.setCenterText("Yearly Attendance Chart");
                                            emp_analysis_yearly_chart.setEntryLabelColor(android.R.color.darker_gray);
                                            emp_analysis_yearly_chart.getDescription().setEnabled(false);
                                            emp_analysis_yearly_chart.setEntryLabelTextSize(15);
                                            emp_analysis_yearly_chart.animate();

                                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                            String url = "https://wetrackpredictions.pythonanywhere.com/AttendanceAnalysis";

                                            long val1 = y_present_count;
                                            long val2 = y_half_day_count;
                                            long val3 = y_absent_count;

                                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                                                    response -> {
                                                        try {
                                                            loading_dialog.dismiss();
                                                            String prediction_res = response.getString("attendance_analysis_res");
                                                            String prediction_status = response.getString("attendance_analysis_status");

                                                            double result = Double.parseDouble(prediction_res);
                                                            Log.e("result","The result is : "+result);
                                                            emp_analysis_yearly_ratebar.setRating((float) result*5);

                                                        } catch (JSONException e) {
                                                            loading_dialog.cancel();
                                                            Snackbar.make(emp_analysis_mainlay, "Some Error Occurred while parsing json data !! Error : " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                                            Log.e("volley_error", "Some Error Occurred while parsing json data !! Error : " + e.getLocalizedMessage());
                                                        }
                                                    },
                                                    error -> {
                                                        loading_dialog.cancel();
                                                        Snackbar.make(emp_analysis_mainlay, "Some Error Occurred while retrieving json data !! Error : " + error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                                        Log.e("volley_error", "Some Error Occurred while retrieving json data !! Error : " + error.getLocalizedMessage());
                                                    }) {
                                                @Override
                                                public byte[] getBody() {
                                                    HashMap<String, Object> params = new HashMap<>();
                                                    params.put("val1", val1);
                                                    params.put("val2", val2);
                                                    params.put("val3", val3);
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
                                            String msg= task1.getException()!=null && task1.getException().getLocalizedMessage()!=null ? task1.getException().getLocalizedMessage() : "No Error !!!";
                                            Log.e("firebase_error","The error is : "+msg);
                                            Snackbar.make(emp_analysis_mainlay, "No Attendances taken for this month till yet !!", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            String msg= task.getException()!=null && task.getException().getLocalizedMessage()!=null ? task.getException().getLocalizedMessage() : "No Error !!!";
                            Log.e("firebase_error","The error is : "+msg);
                            Snackbar.make(emp_analysis_mainlay, "No Employees added yet !! Add Employees and try again", Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    void loadMonthlyEmployeesAnalysis(int selected_month, int selected_year) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(selected_year, selected_month, 1);
        Timestamp start_timestamp= new Timestamp(functions.getDateFromString(functions.getStringFromDate(calendar.getTime(),
                "dd/MM/yyyy"),"dd/MM/yyyy"));
        calendar.add(Calendar.MONTH, 1);
        Timestamp end_timestamp= new Timestamp(functions.getDateFromString(functions.getStringFromDate(calendar.getTime(),
                "dd/MM/yyyy"),"dd/MM/yyyy"));

        Log.e("date_check","The timestamps are : "+start_timestamp.toDate()+" & "+end_timestamp.toDate());

        if (employee_val != null) {
            emp_reference = employee_val.getStringExtra("emp_reference_id") != null ? employee_val.getStringExtra("emp_reference_id") : db.document("Employees/sampledoc").getPath();
            Dialog loading_dialog = functions.createDialogBox(this, R.layout.loading_dialog, false);
            loading_dialog.show();

            db.collection("Employees")
                    .document(db.document(emp_reference).getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            String emp_name = task.getResult().getString("emp_name");
                            String emp_contact = task.getResult().getString("emp_contact");
                            emp_analysis_loanbalance.setText("₹ "+task.getResult().getDouble("emp_advance_loans"));

                            emp_analysis_empname.setText(emp_name);
                            emp_analysis_empcontact.setText(emp_contact);

                            db.collection("Attendances")
                                    .whereEqualTo("attend_emp_reference", db.document(emp_reference))
                                    .whereGreaterThanOrEqualTo("attend_date",start_timestamp)
                                    .whereLessThanOrEqualTo("attend_date",end_timestamp)
                                    .orderBy("attend_date", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            present_count = 0;
                                            half_day_count = 0;
                                            absent_count = 0;
                                            List<DocumentSnapshot> attendance_arr = task1.getResult().getDocuments();
                                            Log.e("firebase_size","The size of the arr is : "+attendance_arr.size());
                                            for (DocumentSnapshot attendance : attendance_arr) {
                                                present_count += (attendance.getLong("attend_status") == 1) ? 1 : 0;
                                                half_day_count += (attendance.getLong("attend_status") == 2) ? 1 : 0;
                                                absent_count += (attendance.getLong("attend_status") == 3) ? 1 : 0;
                                            }

                                            Log.e("attend_count","Present - "+present_count+" & Absent - "+absent_count+" & Half Day - "+half_day_count);
                                            ArrayList<PieEntry> data = new ArrayList<>();
                                            data.add(new PieEntry(present_count, "Present"));
                                            data.add(new PieEntry(half_day_count, "Half Day"));
                                            data.add(new PieEntry(absent_count, "Absent"));

                                            PieDataSet pieDataSet = new PieDataSet(data, " - Counts");
                                            pieDataSet.setColors(new int[]{R.color.my_green, R.color.my_yellow, R.color.my_red, R.color.light_grey}, getApplicationContext());
                                            pieDataSet.setValueTextColor(android.R.color.darker_gray);
                                            pieDataSet.setValueTextSize(15f);
                                            PieData pieData = new PieData(pieDataSet);
                                            emp_analysis_monthly_chart.setData(pieData);
                                            emp_analysis_monthly_chart.setCenterText("Attendance Chart");
                                            emp_analysis_monthly_chart.setEntryLabelColor(android.R.color.darker_gray);
                                            emp_analysis_monthly_chart.getDescription().setEnabled(false);
                                            emp_analysis_monthly_chart.setEntryLabelTextSize(15);
                                            emp_analysis_monthly_chart.animate();

                                            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                            String url = "https://wetrackpredictions.pythonanywhere.com/AttendanceAnalysis";

                                            long val1 = present_count;
                                            long val2 = half_day_count;
                                            long val3 = absent_count;

                                            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, null,
                                                    response -> {
                                                        try {
                                                            loading_dialog.dismiss();
                                                            String prediction_res = response.getString("attendance_analysis_res");
                                                            String prediction_status = response.getString("attendance_analysis_status");

                                                            double result = Double.parseDouble(prediction_res);
                                                            Log.e("result","The result is : "+result);
                                                            float op= result >= 0.55 && result<=0.55 ? 2.5F : (result > 0.55 ? 4.5F : 1.5F);
                                                            Log.e("op_val","The OP value is : "+op);
                                                            emp_analysis_monthly_ratebar.setRating(op);

                                                        } catch (JSONException e) {
                                                            loading_dialog.cancel();
                                                            Snackbar.make(emp_analysis_mainlay, "Some Error Occurred while parsing json data !! Error : " + e.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                                            Log.e("volley_error", "Some Error Occurred while parsing json data !! Error : " + e.getLocalizedMessage());
                                                        }
                                                    },
                                                    error -> {
                                                        loading_dialog.cancel();
                                                        Snackbar.make(emp_analysis_mainlay, "Some Error Occurred while retrieving json data !! Error : " + error.getLocalizedMessage(), Snackbar.LENGTH_LONG).show();
                                                        Log.e("volley_error", "Some Error Occurred while retrieving json data !! Error : " + error.getLocalizedMessage());
                                                    }) {
                                                @Override
                                                public byte[] getBody() {
                                                    HashMap<String, Object> params = new HashMap<>();
                                                    params.put("val1", val1);
                                                    params.put("val2", val2);
                                                    params.put("val3", val3);
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
                                            String msg= task1.getException()!=null && task1.getException().getLocalizedMessage()!=null ? task1.getException().getLocalizedMessage() : "No Error !!!";
                                            Log.e("firebase_error","The error is : "+msg);
                                            Snackbar.make(emp_analysis_mainlay, "No Attendances taken for this month till yet !!", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            String msg= task.getException()!=null && task.getException().getLocalizedMessage()!=null ? task.getException().getLocalizedMessage() : "No Error !!!";
                            Log.e("firebase_error","The error is : "+msg);
                            Snackbar.make(emp_analysis_mainlay, "No Employees added yet !! Add Employees and try again", Snackbar.LENGTH_SHORT).show();
                        }
                    });
        }
    }

}