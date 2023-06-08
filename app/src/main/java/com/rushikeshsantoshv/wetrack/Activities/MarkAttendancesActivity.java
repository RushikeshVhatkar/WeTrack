package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.rushikeshsantoshv.wetrack.Adapters.AttendanceAdapter;
import com.rushikeshsantoshv.wetrack.DataModels.AttendanceModel;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MarkAttendancesActivity extends AppCompatActivity {

    RelativeLayout mark_atts_attendancesmainlay;
    LinearLayout mark_atts_emptylist_lay, mark_atts_mainlay, mark_atts_contentlay;
    Toolbar mark_atts_toolbar;
    ImageButton mark_atts_back_btn, mark_atts_searchemp_btn, mark_atts_calender_btn, mark_atts_calprev_date, mark_atts_calnext_date;
    TextView mark_atts_curr_date, mark_atts_submitbtn, mark_atts_empty_txt, mark_atts_editattendancebtn, mark_atts_mark_as_holiday_btn;
    ProgressBar mark_atts_progressbar;
    RecyclerView mark_atts_empsrec;
    NestedScrollView mark_atts_attendancedisplay_nestview;

    LinearLayout mark_atts_occasion_animlay;
    RelativeLayout mark_atts_actionbtn_lay;

    Functions functions = new Functions();
    String comp_path;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    ArrayList<AttendanceModel> attendance_arr = new ArrayList<>();
    AttendanceAdapter attendanceAdapter;
    String curr_user, curr_logged_user, user;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_attendances);

        functions.coloredStatusBarDarkTextDesign(MarkAttendancesActivity.this, R.color.maincolor_light, R.color.white);

        markAttendancesInits(); // Initialize the variables and wigets

        mark_atts_back_btn.setOnClickListener(v -> finish());

        // attendanceCheckFun(false);
        checkHolidaysAndSetDefault(false);

        mark_atts_mark_as_holiday_btn.setOnClickListener(v -> {
            if (comp_path != null) {
                if (functions.checkInternetConnection(this)) {
                    Timestamp today_timestamp = new Timestamp(functions.getDateFromString(mark_atts_curr_date.getText().toString(), "dd-MM-yyyy"));
                    DocumentReference comp = db.collection("Companies").document(db.document(comp_path).getId());
                    Map<String, Object> update = new HashMap<>();
                    update.put("company_holidays", FieldValue.arrayUnion(today_timestamp));
                    comp.update(update)
                            .addOnSuccessListener(aVoid -> {
                                checkHolidaysAndSetDefault(false);
                            })
                            .addOnFailureListener(e -> {
                                Snackbar.make(mark_atts_mainlay, "Failed to add as holday !! Please try again", Snackbar.LENGTH_LONG).show();
                            });
                } else functions.no_internet_dialog(this, false);
            } else {
                Snackbar.make(mark_atts_mainlay, "Unable to add holdays to your company due to some issues !! Please logout and try again", Snackbar.LENGTH_LONG).show();
            }
        });

        mark_atts_submitbtn.setOnClickListener(v -> {
            for (int i = 0; i < attendance_arr.size(); i++) {
                Map<String, Object> attendance_data = new HashMap<>();
                attendance_data.put("attend_emp_reference", attendance_arr.get(i).getAttend_emp_reference());
                attendance_data.put("attend_company_reference", attendance_arr.get(i).getAttend_company_reference());
                attendance_data.put("attend_date", (Timestamp) attendance_arr.get(i).getAttend_date());
                attendance_data.put("attend_status", attendance_arr.get(i).getAttend_status());

                if (mark_atts_submitbtn.getText().equals("Update Attendances")) {
                    if (functions.checkInternetConnection(this)) {
                        db.collection("Attendances")
                                .document(attendance_arr.get(i).getAttend_emp_doc())
                                .update(attendance_data)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.e("attendances_data", "Updated Successfully..");
                                    }
                                });
                    } else functions.no_internet_dialog(this, false);
                } else {
                    if (functions.checkInternetConnection(this)) {
                        db.collection("Attendances")
                                .document()
                                .set(attendance_data)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Log.e("attendances_data", "Submitted Successfully..");
                                    }
                                });
                    } else functions.no_internet_dialog(this, false);
                }
            }

            String txt = (mark_atts_submitbtn.getText().equals("Update Attendances")) ? "updated" : "marked";
            String day = functions.getTodayDate("dd-MM-yyy").equals(mark_atts_curr_date.getText().toString()) ?
                    "Today's attendance has been successfully " + txt + "." :
                    "Attendance of " + mark_atts_curr_date.getText().toString() + " has been successfully " + txt + ".";
            Snackbar.make(mark_atts_mainlay, day, Snackbar.LENGTH_SHORT).show();
            // attendanceCheckFun(false);
            checkHolidaysAndSetDefault(false);
        });

        mark_atts_calprev_date.setOnClickListener(v -> {
            mark_atts_curr_date.setText(functions.getDesiredDate(mark_atts_curr_date.getText().toString(), -1, "dd-MM-yyyy", Calendar.DATE));
            // attendanceCheckFun(false);
            checkHolidaysAndSetDefault(false);
            checkNextDate();
        });

        mark_atts_calnext_date.setOnClickListener(v -> {
            if (!checkNextDate()) {
                mark_atts_curr_date.setText(functions.getDesiredDate(mark_atts_curr_date.getText().toString(), 1, "dd-MM-yyyy", Calendar.DATE));
                // attendanceCheckFun(false);
                checkHolidaysAndSetDefault(false);
            }
            checkNextDate();
        });

        mark_atts_editattendancebtn.setOnClickListener(v -> {
            mark_atts_editattendancebtn.setVisibility(View.GONE);
            mark_atts_submitbtn.setVisibility(View.VISIBLE);
            mark_atts_submitbtn.setText("Update Attendances");
            // attendanceCheckFun(true);
            checkHolidaysAndSetDefault(true);
        });

        mark_atts_calender_btn.setOnClickListener(v -> {
            try {
                String curr_datestr = mark_atts_curr_date.getText().toString();
                SimpleDateFormat format1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                SimpleDateFormat inputFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH);
                Date curr_date = format1.parse(curr_datestr);
                Calendar cal1 = Calendar.getInstance();
                cal1.setTime(curr_date);
                DatePickerDialog datePickerDialog = new DatePickerDialog(MarkAttendancesActivity.this, (datePicker, i, i1, i2) -> {
                    mark_atts_curr_date.setText(functions.getDateFromDatePicker(datePicker, "dd-MM-yyyy"));
                    // attendanceCheckFun(false);
                    checkHolidaysAndSetDefault(false);
                    checkNextDate();
                }, cal1.get(Calendar.YEAR), cal1.get(Calendar.MONTH), cal1.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
                String comp_createddate = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_created", "string", null);
                if (comp_createddate != null) {
                    Date companydate = inputFormat.parse(comp_createddate);
                    datePickerDialog.getDatePicker().setMinDate(companydate.getTime());
                }

                datePickerDialog.show();
            } catch (ParseException e) {
                Log.e("time_error","The time is : "+e.getLocalizedMessage());
                Snackbar.make(mark_atts_mainlay, e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void markAttendancesInits() {
        mark_atts_emptylist_lay = findViewById(R.id.mark_atts_emptylist_lay);
        mark_atts_mainlay = findViewById(R.id.mark_atts_mainlay);
        mark_atts_contentlay = findViewById(R.id.mark_atts_contentlay);
        mark_atts_toolbar = findViewById(R.id.mark_atts_toolbar);
        mark_atts_back_btn = findViewById(R.id.mark_atts_back_btn);
        mark_atts_searchemp_btn = findViewById(R.id.mark_atts_searchemp_btn);
        mark_atts_calender_btn = findViewById(R.id.mark_atts_calender_btn);
        mark_atts_calprev_date = findViewById(R.id.mark_atts_calprev_date);
        mark_atts_calnext_date = findViewById(R.id.mark_atts_calnext_date);
        mark_atts_curr_date = findViewById(R.id.mark_atts_curr_date);
        mark_atts_submitbtn = findViewById(R.id.mark_atts_submitbtn);
        mark_atts_progressbar = findViewById(R.id.mark_atts_progressbar);
        mark_atts_empsrec = findViewById(R.id.mark_atts_empsrec);
        mark_atts_attendancesmainlay = findViewById(R.id.mark_atts_attendancesmainlay);
        mark_atts_empty_txt = findViewById(R.id.mark_atts_empty_txt);
        mark_atts_editattendancebtn = findViewById(R.id.mark_atts_editattendancebtn);
        mark_atts_mark_as_holiday_btn = findViewById(R.id.mark_atts_mark_as_holiday_btn);

        mark_atts_occasion_animlay = findViewById(R.id.mark_atts_occasion_animlay);
        mark_atts_actionbtn_lay = findViewById(R.id.mark_atts_actionbtn_lay);
        mark_atts_attendancedisplay_nestview = findViewById(R.id.mark_atts_attendancedisplay_nestview);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        mark_atts_curr_date.setText(functions.getTodayDate("dd-MM-yyyy"));
        checkNextDate();
        curr_user = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "ptype", "string", null);
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        user = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "ptype", "string", null);
        curr_logged_user = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "user_reference", "string", null);

        if (user != null && user.equals("Managers")) mark_atts_mark_as_holiday_btn.setVisibility(View.GONE);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void attendanceCheckFun(boolean isEdit) {

        if (attendance_arr.size() > 0) {
            attendance_arr.clear();
        }
        if (comp_path != null) {
            Timestamp attend_curr_timestamp = new Timestamp(functions.getDateFromString(mark_atts_curr_date.getText().toString(), "dd-MM-yyyy"));
            if (functions.checkInternetConnection(this)) {

                if (user != null && curr_logged_user != null && user.equals("Managers")) {
                    db.collection("Employees")
                            .whereEqualTo("company_path", db.document(comp_path))
                            .whereEqualTo("emp_manager_reference", db.document(curr_logged_user))
                            .orderBy(FieldPath.documentId(), Query.Direction.ASCENDING)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        ArrayList<DocumentReference> emp_ref_arr = new ArrayList<>();
                                        List<DocumentSnapshot> arrdoc = task.getResult().getDocuments();
                                        for (DocumentSnapshot doc : arrdoc)
                                            emp_ref_arr.add(doc.getReference());

                                        Task<QuerySnapshot> query = db.collection("Attendances")
                                                .whereEqualTo("attend_date", attend_curr_timestamp)
                                                .whereEqualTo("attend_company_reference", db.document(comp_path))
                                                .whereIn("attend_emp_reference", emp_ref_arr)
                                                .get();

                                        loadExistingEmpAttenAccToManagers(isEdit, query);
                                    }
                                }
                            });
                } else {
                    Task<QuerySnapshot> query = db.collection("Attendances")
                            .whereEqualTo("attend_date", attend_curr_timestamp)
                            .whereEqualTo("attend_company_reference", db.document(comp_path))
                            .orderBy("attend_emp_reference", Query.Direction.ASCENDING)
                            .get();

                    loadExistingEmpAttenAccToManagers(isEdit, query);
                }
            } else functions.no_internet_dialog(this, false);
        } else {
            Snackbar.make(mark_atts_mainlay, "Unable to retrieve company data !! Please try again 02", Snackbar.LENGTH_SHORT).show();
            mark_atts_progressbar.setVisibility(View.GONE);
            mark_atts_contentlay.setVisibility(View.GONE);
            mark_atts_emptylist_lay.setVisibility(View.VISIBLE);
        }
    }

    private void loadExistingEmpAttenAccToManagers(boolean isEdit, @NonNull Task<QuerySnapshot> query) {
        query.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int attendance_size = task.getResult().size();
                if (attendance_size > 0) {
                    mark_atts_mark_as_holiday_btn.setVisibility(View.GONE);
                    for (DocumentSnapshot emp : task.getResult().getDocuments()) {

                        attendance_arr.add(new AttendanceModel(". . . . .", ". . . . .",
                                emp.getId(),
                                emp.getDocumentReference("attend_emp_reference"),
                                emp.getDocumentReference("attend_company_reference"),
                                emp.getTimestamp("attend_date"),
                                emp.getLong("attend_status")));
                    }
                    attendanceAdapter = new AttendanceAdapter(getApplicationContext(), MarkAttendancesActivity.this, attendance_arr, true, isEdit, 0);
                    mark_atts_empsrec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    mark_atts_empsrec.setAdapter(attendanceAdapter);
                    attendanceAdapter.notifyDataSetChanged();
                    mark_atts_progressbar.setVisibility(View.GONE);
                    mark_atts_contentlay.setVisibility(View.VISIBLE);
                    mark_atts_emptylist_lay.setVisibility(View.GONE);
                    if (!isEdit) {
                        mark_atts_submitbtn.setVisibility(View.GONE);
                        mark_atts_editattendancebtn.setVisibility(View.VISIBLE);
                    }
                    if (!functions.getTodayDate("dd-MM-yyyy").equals(mark_atts_curr_date.getText().toString())) {
                        mark_atts_editattendancebtn.setVisibility(View.GONE);
                    }
                } else {
                    mark_atts_mark_as_holiday_btn.setVisibility(View.VISIBLE);
                    if (user != null && user.equals("Managers")) mark_atts_mark_as_holiday_btn.setVisibility(View.GONE);
//                                if (mark_atts_curr_date.getText().toString().equals(functions.getTodayDate("dd-MM-yyyy"))) {
//                                    setDefaultAttendanceArrayList(db.document(comp_path));
//                                } else {
//                                    mark_atts_progressbar.setVisibility(View.GONE);
//                                    mark_atts_contentlay.setVisibility(View.VISIBLE);
//                                    mark_atts_emptylist_lay.setVisibility(View.VISIBLE);
//                                    mark_atts_attendancesmainlay.setVisibility(View.GONE);
//                                    mark_atts_empty_txt.setText("Unable to retrieve attendances from this selected date !! Please try another date");
//                                }

                    Date date = functions.getDateFromString(mark_atts_curr_date.getText().toString(), "dd-MM-yyy");
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    cal.add(Calendar.DATE, 1);
                    Timestamp check_timestamp = new Timestamp(cal.getTime());
                    Timestamp curr_timestamp = new Timestamp(date);
                    // checkHolidaysAndSetDefault(db.document(comp_path), check_timestamp, curr_timestamp);
                    setDefaultAttendanceArrayList(db.document(comp_path), check_timestamp, curr_timestamp);
                }
            } else {
                String msg = task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error";
                Log.e("firebase_error", "The Error is : " + msg);
                Snackbar.make(mark_atts_mainlay, "Unable to retrieve the data !! Please try again 01", Snackbar.LENGTH_SHORT).show();
                mark_atts_progressbar.setVisibility(View.GONE);
                mark_atts_contentlay.setVisibility(View.GONE);
                mark_atts_emptylist_lay.setVisibility(View.VISIBLE);
            }
        });
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void checkHolidaysAndSetDefault(boolean isEdit) {

        if (functions.checkInternetConnection(this)) {
            mark_atts_progressbar.setVisibility(View.VISIBLE);
            mark_atts_contentlay.setVisibility(View.GONE);
            mark_atts_emptylist_lay.setVisibility(View.GONE);
            mark_atts_attendancesmainlay.setVisibility(View.VISIBLE);
            mark_atts_actionbtn_lay.setVisibility(View.VISIBLE);
            if (!isEdit) {
                mark_atts_submitbtn.setVisibility(View.VISIBLE);
                mark_atts_editattendancebtn.setVisibility(View.GONE);
            }

            Date date = functions.getDateFromString(mark_atts_curr_date.getText().toString(), "dd-MM-yyy");
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.DATE, 1);
            Timestamp curr_timestamp = new Timestamp(date);
            Log.e("time_error", "The current date is : " + functions.getStringFromDate(curr_timestamp.toDate(), "dd/MM/yyyy"));

            db.collection("Companies")
                    .document(db.document(comp_path).getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists() && task.getResult().get("company_holidays") != null) {
                            ArrayList<Timestamp> holiday_list = (ArrayList<Timestamp>) task.getResult().get("company_holidays");
                            if (holiday_list.size() > 0) {
                                boolean check_working = true;
                                for (Timestamp timestamp : holiday_list) {
                                    if (functions.getStringFromDate(timestamp.toDate(), "dd/MM/yyyy")
                                            .equals(functions.getStringFromDate(curr_timestamp.toDate(), "dd/MM/yyyy"))) {
                                        check_working = false;
                                        break;
                                    }
                                }

                                if (check_working) {
                                    mark_atts_progressbar.setVisibility(View.VISIBLE);
                                    mark_atts_contentlay.setVisibility(View.GONE);
                                    mark_atts_emptylist_lay.setVisibility(View.GONE);
                                    mark_atts_attendancesmainlay.setVisibility(View.VISIBLE);
                                    if (!isEdit) {
                                        mark_atts_submitbtn.setVisibility(View.VISIBLE);
                                        mark_atts_editattendancebtn.setVisibility(View.GONE);
                                    }
                                    mark_atts_attendancedisplay_nestview.setVisibility(View.VISIBLE);
                                    mark_atts_occasion_animlay.setVisibility(View.GONE);
                                    attendanceCheckFun(isEdit);
                                } else {

                                    mark_atts_progressbar.setVisibility(View.GONE);
                                    mark_atts_emptylist_lay.setVisibility(View.GONE);
                                    mark_atts_contentlay.setVisibility(View.VISIBLE);
                                    mark_atts_attendancesmainlay.setVisibility(View.VISIBLE);
                                    mark_atts_submitbtn.setVisibility(View.GONE);
                                    mark_atts_editattendancebtn.setVisibility(View.GONE);
                                    mark_atts_occasion_animlay.setVisibility(View.VISIBLE);
                                    mark_atts_attendancedisplay_nestview.setVisibility(View.GONE);
                                    mark_atts_actionbtn_lay.setVisibility(View.GONE);
                                    mark_atts_mark_as_holiday_btn.setVisibility(View.GONE);
                                }
                            } else {
                                mark_atts_progressbar.setVisibility(View.VISIBLE);
                                mark_atts_contentlay.setVisibility(View.GONE);
                                mark_atts_emptylist_lay.setVisibility(View.GONE);
                                mark_atts_attendancesmainlay.setVisibility(View.VISIBLE);
                                if (!isEdit) {
                                    mark_atts_submitbtn.setVisibility(View.VISIBLE);
                                    mark_atts_editattendancebtn.setVisibility(View.GONE);
                                }
                                mark_atts_attendancedisplay_nestview.setVisibility(View.VISIBLE);
                                mark_atts_occasion_animlay.setVisibility(View.GONE);
                                attendanceCheckFun(isEdit);
                            }
                        } else {
                            mark_atts_progressbar.setVisibility(View.VISIBLE);
                            mark_atts_contentlay.setVisibility(View.GONE);
                            mark_atts_emptylist_lay.setVisibility(View.GONE);
                            mark_atts_attendancesmainlay.setVisibility(View.VISIBLE);
                            if (!isEdit) {
                                mark_atts_submitbtn.setVisibility(View.VISIBLE);
                                mark_atts_editattendancebtn.setVisibility(View.GONE);
                            }
                            mark_atts_attendancedisplay_nestview.setVisibility(View.VISIBLE);
                            mark_atts_occasion_animlay.setVisibility(View.GONE);
                            attendanceCheckFun(isEdit);
                        }
                    });
        } else functions.no_internet_dialog(this, true);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDefaultAttendanceArrayList(DocumentReference comp_path, Timestamp check_timestamp, Timestamp date) {
        if (functions.checkInternetConnection(this)) {

            Task<QuerySnapshot> query = null;
            if(user != null && curr_logged_user != null && user.equals("Managers")){
                query= db.collection("Employees")
                        .orderBy("emp_timestamp", Query.Direction.ASCENDING)
                        .orderBy(FieldPath.documentId())
                        .whereEqualTo("company_path", comp_path)
                        .whereEqualTo("emp_manager_reference", db.document(curr_logged_user))
                        .whereLessThanOrEqualTo("emp_timestamp", check_timestamp)
                        .whereEqualTo("emp_status", true)
                        .get();
            }
            else{
                query= db.collection("Employees")
                        .orderBy("emp_timestamp", Query.Direction.ASCENDING)
                        .orderBy(FieldPath.documentId())
                        .whereEqualTo("company_path", comp_path)
                        .whereLessThanOrEqualTo("emp_timestamp", check_timestamp)
                        .whereEqualTo("emp_status", true)
                        .get();
            }

            query.addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()) {
                    if (task1.getResult().size() > 0) {
                        for (DocumentSnapshot emp : task1.getResult().getDocuments()) {
                            attendance_arr.add(new AttendanceModel(emp.getString("emp_name"), emp.getString("emp_contact"),
                                    emp.getId(), emp.getReference(), emp.getDocumentReference("company_path"),
                                    date, 1));
                        }
                        attendanceAdapter = new AttendanceAdapter(getApplicationContext(), MarkAttendancesActivity.this, attendance_arr, false, false, 0);
                        mark_atts_empsrec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        mark_atts_empsrec.setAdapter(attendanceAdapter);
                        attendanceAdapter.notifyDataSetChanged();
                        mark_atts_progressbar.setVisibility(View.GONE);
                        mark_atts_contentlay.setVisibility(View.VISIBLE);
                        mark_atts_emptylist_lay.setVisibility(View.GONE);
                    } else {
                        Snackbar.make(mark_atts_mainlay, "No Employees added yet !! Please add employees from Employees Tab", Snackbar.LENGTH_SHORT).show();
                        mark_atts_progressbar.setVisibility(View.GONE);
                        mark_atts_contentlay.setVisibility(View.GONE);
                        mark_atts_emptylist_lay.setVisibility(View.VISIBLE);
                    }
                } else {
                    Snackbar.make(mark_atts_mainlay, "Unable to retrieve the data !! Please try again.", Snackbar.LENGTH_SHORT).show();
                    String error = task1.getException() != null && task1.getException().getLocalizedMessage() != null ? task1.getException().getLocalizedMessage() : "No Error";
                    Log.e("get_emps", "The error is : " + error);
                    mark_atts_progressbar.setVisibility(View.GONE);
                    mark_atts_contentlay.setVisibility(View.GONE);
                    mark_atts_emptylist_lay.setVisibility(View.VISIBLE);
                }
            });
        } else functions.no_internet_dialog(this, false);
    }

    public boolean checkNextDate() {
        if (mark_atts_curr_date.getText().toString().equals(functions.getTodayDate("dd-MM-yyyy"))) {
            mark_atts_calnext_date.setEnabled(false);
            mark_atts_calnext_date.setColorFilter(Color.parseColor("#CDCDCD"));
            return true;
        } else {
            mark_atts_calnext_date.setEnabled(true);
            mark_atts_calnext_date.setColorFilter(Color.parseColor("#494949"));
            return false;
        }
    }
}