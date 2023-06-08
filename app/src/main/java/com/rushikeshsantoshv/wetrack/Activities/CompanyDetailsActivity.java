package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.flexbox.FlexboxLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import org.threeten.bp.LocalDate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CompanyDetailsActivity extends AppCompatActivity {

    ImageButton compdetails_back_btn;
    LinearLayout compdetails_holidaylay, compdetails_mainlay;
    TextView compdetails_nodates_msg, compdetails_removedate_btn, compdetails_holiday_viewall_btn;
    ProgressBar compdetails_holiday_loading_prog;

    TextInputLayout compdetails_apr_jun_comprate, compdetails_jul_sep_comprate;
    TextInputLayout compdetails_oct_dec_comprate, compdetails_jan_mar_comprate;
    TextInputLayout compdetails_apr_jun_govtrate, compdetails_jul_sep_govtrate;
    TextInputLayout compdetails_oct_dec_govtrate, compdetails_jan_mar_govtrate;
    TextView compdetails_update_comp_rate_btn, compdetails_update_govt_rate_btn;
    LinearLayout compdetails_update_phonenum_lay, compdetails_deactivate_acc_lay;

    FlexboxLayout compdetails_holidayflexlay;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Functions functions = new Functions();
    TextView compdetails_addmoredate_btn;
    String comp_path;
    ArrayList<Timestamp> holiday_list = new ArrayList<>();
    Long financial_year;
    boolean check_month;
    String comp_base_rate_ref, govt_base_rate_ref;

    ArrayList<String> apr_jun= new ArrayList<>();
    ArrayList<String> jul_sep= new ArrayList<>();
    ArrayList<String> oct_dec= new ArrayList<>();
    ArrayList<String> jan_mar= new ArrayList<>();

    @SuppressLint({"NewApi", "LocalSuppress"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_details);
        functions.coloredStatusBarDarkTextDesign(CompanyDetailsActivity.this, R.color.maincolor_light, R.color.white);

        companyDetailsInits();

        if(functions.checkInternetConnection(CompanyDetailsActivity.this)){
            loadTheCompanyBaseRateDetails();

            loadTheGovtBaseRateDetails();

            displayHolidaysList();
        }
        else functions.no_internet_dialog(CompanyDetailsActivity.this, true);

        compdetails_back_btn.setOnClickListener(v -> finish());

        compdetails_update_phonenum_lay.setOnClickListener(v->{
            if(functions.checkInternetConnection(this)){
                startActivity(new Intent(getApplicationContext(), UpdatePhoneNumberActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(this, false);
        });

        compdetails_deactivate_acc_lay.setOnClickListener(v->{
            if(functions.checkInternetConnection(this)){

            }
            else functions.no_internet_dialog(this, false);
        });

        compdetails_addmoredate_btn.setOnClickListener(v -> {
            if(functions.checkInternetConnection(CompanyDetailsActivity.this)) startActivityForResult(new Intent(getApplicationContext(), AddHolidaysActivity.class), 10001);
            else functions.no_internet_dialog(CompanyDetailsActivity.this, false);
        });

        compdetails_removedate_btn.setOnClickListener(v -> {
            if(functions.checkInternetConnection(this)){
                ArrayList<CalendarDay> calendarDays = new ArrayList<>();
                if (holiday_list.size() > 0) {
                    for (int i = 0; i < holiday_list.size(); i++) {
                        Timestamp timestamp = holiday_list.get(i);
                        Timestamp check_timestamp = new Timestamp(functions.getDateFromString(functions.getStringFromDate(timestamp.toDate(),"dd/MM/yyyy"),"dd/MM/yyyy"));
                        Timestamp curr_timestamp= new Timestamp(functions.getDateFromString(functions.getTodayDate("dd/MM/yyyy"),"dd/MM/yyyy"));
                        if(check_timestamp.compareTo(curr_timestamp) > 0){
                            Date date = timestamp.toDate();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(date);

                            LocalDateTime localDateTime = timestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
                            LocalDate localDate = LocalDate.of(localDateTime.getYear(), localDateTime.getMonth().getValue(), localDateTime.getDayOfMonth());
                            CalendarDay calendarDay = CalendarDay.from(localDate);
                            calendarDays.add(calendarDay);
                        }
                    }

                    Intent intent = new Intent(getApplicationContext(), AddHolidaysActivity.class);
                    intent.putExtra("remove_check", true);
                    intent.putExtra("selecteddates", calendarDays);
                    startActivityForResult(intent, 10002);
                    Log.e("calenderday_size", "The size of the calenderdays is : " + (calendarDays.size() > 0 ? calendarDays.get(0) : 0));
                }
            }
            else functions.no_internet_dialog(this, false);
        });

        compdetails_update_comp_rate_btn.setOnClickListener(v-> {
            if(functions.checkInternetConnection(this)) updateRateFunction(true, "CompanyBaseRate");
            else functions.no_internet_dialog(this, true);
        });

        compdetails_update_govt_rate_btn.setOnClickListener(v-> {
            if(functions.checkInternetConnection(this)) updateRateFunction(false, "GovtBaseRate");
            else functions.no_internet_dialog(this, false);
        });
    }

    private void updateRateFunction(boolean isCompany, String db_name){
        BottomSheetDialog dialog = new BottomSheetDialog(CompanyDetailsActivity.this);
        dialog.setContentView(R.layout.add_company_rate_btmdialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        TextInputLayout acr_rate1 = dialog.findViewById(R.id.acr_rate1);
        TextInputLayout acr_rate2 = dialog.findViewById(R.id.acr_rate2);
        TextInputLayout acr_rate3 = dialog.findViewById(R.id.acr_rate3);
        TextInputLayout acr_rate4 = dialog.findViewById(R.id.acr_rate4);
        TextView acr_add_btn = dialog.findViewById(R.id.acr_add_btn);
        acr_add_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
        acr_add_btn.setEnabled(false);

        acr_rate1.getEditText().addTextChangedListener(new CompanyTextWatcher(getApplicationContext(), acr_rate1, acr_rate2, acr_rate3, acr_rate4, acr_add_btn));
        acr_rate2.getEditText().addTextChangedListener(new CompanyTextWatcher(getApplicationContext(), acr_rate2, acr_rate1, acr_rate3, acr_rate4, acr_add_btn));
        acr_rate3.getEditText().addTextChangedListener(new CompanyTextWatcher(getApplicationContext(), acr_rate3, acr_rate1, acr_rate2, acr_rate4, acr_add_btn));
        acr_rate4.getEditText().addTextChangedListener(new CompanyTextWatcher(getApplicationContext(), acr_rate4, acr_rate1, acr_rate2, acr_rate3, acr_add_btn));

        TextInputLayout apr_jun_rate= isCompany ? compdetails_apr_jun_comprate : compdetails_apr_jun_govtrate;
        TextInputLayout jul_sep_rate= isCompany ? compdetails_jul_sep_comprate : compdetails_jul_sep_govtrate;
        TextInputLayout oct_dec_rate= isCompany ? compdetails_oct_dec_comprate : compdetails_oct_dec_govtrate;
        TextInputLayout jan_mar_rate= isCompany ? compdetails_jan_mar_comprate : compdetails_jan_mar_govtrate;

        acr_rate1.getEditText().setText(apr_jun_rate.getEditText().getText().toString());
        acr_rate2.getEditText().setText(jul_sep_rate.getEditText().getText().toString());
        acr_rate3.getEditText().setText(oct_dec_rate.getEditText().getText().toString());
        acr_rate4.getEditText().setText(jan_mar_rate.getEditText().getText().toString());

        String curr_month= functions.getTodayDate("MMM").toLowerCase();
        if(apr_jun.contains(curr_month)) updateRateTextInputLayout(acr_rate1);
        if(jul_sep.contains(curr_month)) updateRateTextInputLayout(acr_rate2);
        if(oct_dec.contains(curr_month)) updateRateTextInputLayout(acr_rate3);
        if(jan_mar.contains(curr_month)) updateRateTextInputLayout(acr_rate4);

        acr_add_btn.setOnClickListener(v1 -> {
            String acr_rate1_txt = acr_rate1.getEditText().getText().toString();
            String acr_rate2_txt = acr_rate2.getEditText().getText().toString();
            String acr_rate3_txt = acr_rate3.getEditText().getText().toString();
            String acr_rate4_txt = acr_rate4.getEditText().getText().toString();

            Double acr_rate1_int = Double.parseDouble(acr_rate1_txt);
            Double acr_rate2_int = Double.parseDouble(acr_rate2_txt);
            Double acr_rate3_int = Double.parseDouble(acr_rate3_txt);
            Double acr_rate4_int = Double.parseDouble(acr_rate4_txt);

            Map<String, Object> company_rate_data = new HashMap<>();
            company_rate_data.put("financial_year",financial_year);
            company_rate_data.put("jan_rate", acr_rate4_int);
            company_rate_data.put("feb_rate", acr_rate4_int);
            company_rate_data.put("mar_rate", acr_rate4_int);
            company_rate_data.put("apr_rate", acr_rate1_int);
            company_rate_data.put("may_rate", acr_rate1_int);
            company_rate_data.put("jun_rate", acr_rate1_int);
            company_rate_data.put("jul_rate", acr_rate2_int);
            company_rate_data.put("aug_rate", acr_rate2_int);
            company_rate_data.put("sep_rate", acr_rate2_int);
            company_rate_data.put("oct_rate", acr_rate3_int);
            company_rate_data.put("nov_rate", acr_rate3_int);
            company_rate_data.put("dec_rate", acr_rate3_int);
            company_rate_data.put("company_reference", db.document(comp_path));

            db.collection(db_name)
                    .document(db.document(isCompany ? comp_base_rate_ref : govt_base_rate_ref).getId())
                    .update(company_rate_data)
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            dialog.dismiss();
                            Snackbar.make(compdetails_mainlay, "Rates updated successfully...", Snackbar.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(getApplicationContext(), CompanyDetailsActivity.class));
                                finish();
                            },1500);
                        } else {
                            Snackbar.make(compdetails_mainlay, "Unable to update rates !! Please try again.", Snackbar.LENGTH_LONG).show();
                        }
                    });
        });
    }

    private void updateRateTextInputLayout(@NonNull TextInputLayout rate_box){
        rate_box.setErrorEnabled(false);
        rate_box.setError(null);
        rate_box.setEnabled(false);
        rate_box.setClickable(false);
    }

    private void loadTheGovtBaseRateDetails() {
        if(functions.getTodayDate("MMM").equalsIgnoreCase("apr") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("may") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("jun")){
            compdetails_apr_jun_govtrate.setEnabled(false);
            compdetails_apr_jun_govtrate.setClickable(false);
        }
        else if(functions.getTodayDate("MMM").equalsIgnoreCase("jul") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("aug") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("sep")){
            compdetails_apr_jun_govtrate.setEnabled(false);
            compdetails_apr_jun_govtrate.setClickable(false);
            compdetails_jul_sep_govtrate.setEnabled(false);
            compdetails_jul_sep_govtrate.setClickable(false);
        }
        else if(functions.getTodayDate("MMM").equalsIgnoreCase("apr") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("may") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("jun")){
            compdetails_apr_jun_govtrate.setEnabled(false);
            compdetails_apr_jun_govtrate.setClickable(false);
            compdetails_jul_sep_govtrate.setEnabled(false);
            compdetails_jul_sep_govtrate.setClickable(false);
            compdetails_oct_dec_govtrate.setEnabled(false);
            compdetails_oct_dec_govtrate.setClickable(false);
        }
        else{
            compdetails_apr_jun_govtrate.setEnabled(false);
            compdetails_apr_jun_govtrate.setClickable(false);
            compdetails_jul_sep_govtrate.setEnabled(false);
            compdetails_jul_sep_govtrate.setClickable(false);
            compdetails_oct_dec_govtrate.setEnabled(false);
            compdetails_oct_dec_govtrate.setClickable(false);
            compdetails_jan_mar_govtrate.setEnabled(false);
            compdetails_jan_mar_govtrate.setClickable(false);
        }
        db.collection("GovtBaseRate")
                .whereEqualTo("financial_year", financial_year)
                .whereEqualTo("company_reference", db.document(comp_path))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(task.getResult().getDocuments().size() > 0){
                            compdetails_update_govt_rate_btn.setEnabled(true);
                            compdetails_update_govt_rate_btn.setClickable(true);
                            DocumentSnapshot doc= task.getResult().getDocuments().get(0);
                            govt_base_rate_ref= doc.getReference().getPath();
                            compdetails_apr_jun_govtrate.getEditText().setText(String.valueOf(doc.getDouble("apr_rate")!=null ? doc.getDouble("apr_rate") : 0.0));
                            compdetails_jul_sep_govtrate.getEditText().setText(String.valueOf(doc.getDouble("jul_rate")!=null ? doc.getDouble("jul_rate") : 0.0));
                            compdetails_oct_dec_govtrate.getEditText().setText(String.valueOf(doc.getDouble("oct_rate")!=null ? doc.getDouble("oct_rate") : 0.0));
                            compdetails_jan_mar_govtrate.getEditText().setText(String.valueOf(doc.getDouble("jan_rate")!=null ? doc.getDouble("jan_rate") : 0.0));
                        }
                        else{
                            compdetails_apr_jun_govtrate.getEditText().setText(String.valueOf(0.0));
                            compdetails_jul_sep_govtrate.getEditText().setText(String.valueOf(0.0));
                            compdetails_oct_dec_govtrate.getEditText().setText(String.valueOf(0.0));
                            compdetails_jan_mar_govtrate.getEditText().setText(String.valueOf(0.0));
                        }
                    }
                });
    }

    private void loadTheCompanyBaseRateDetails() {
        if(functions.getTodayDate("MMM").equalsIgnoreCase("apr") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("may") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("jun")){
            compdetails_apr_jun_comprate.setEnabled(false);
            compdetails_apr_jun_comprate.setClickable(false);
        }
        else if(functions.getTodayDate("MMM").equalsIgnoreCase("jul") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("aug") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("sep")){
            compdetails_apr_jun_comprate.setEnabled(false);
            compdetails_apr_jun_comprate.setClickable(false);
            compdetails_jul_sep_comprate.setEnabled(false);
            compdetails_jul_sep_comprate.setClickable(false);
        }
        else if(functions.getTodayDate("MMM").equalsIgnoreCase("apr") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("may") ||
                functions.getTodayDate("MMM").equalsIgnoreCase("jun")){
            compdetails_apr_jun_comprate.setEnabled(false);
            compdetails_apr_jun_comprate.setClickable(false);
            compdetails_jul_sep_comprate.setEnabled(false);
            compdetails_jul_sep_comprate.setClickable(false);
            compdetails_oct_dec_comprate.setEnabled(false);
            compdetails_oct_dec_comprate.setClickable(false);
        }
        else{
            compdetails_apr_jun_comprate.setEnabled(false);
            compdetails_apr_jun_comprate.setClickable(false);
            compdetails_jul_sep_comprate.setEnabled(false);
            compdetails_jul_sep_comprate.setClickable(false);
            compdetails_oct_dec_comprate.setEnabled(false);
            compdetails_oct_dec_comprate.setClickable(false);
            compdetails_jan_mar_comprate.setEnabled(false);
            compdetails_jan_mar_comprate.setClickable(false);
        }
        db.collection("CompanyBaseRate")
                .whereEqualTo("financial_year", financial_year)
                .whereEqualTo("company_reference", db.document(comp_path))
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        if(task.getResult().getDocuments().size() > 0){
                            compdetails_update_comp_rate_btn.setEnabled(true);
                            compdetails_update_comp_rate_btn.setClickable(true);
                            DocumentSnapshot doc= task.getResult().getDocuments().get(0);
                            comp_base_rate_ref= doc.getReference().getPath();
                            compdetails_apr_jun_comprate.getEditText().setText(String.valueOf(doc.getDouble("apr_rate")));
                            compdetails_jul_sep_comprate.getEditText().setText(String.valueOf(doc.getDouble("jul_rate")));
                            compdetails_oct_dec_comprate.getEditText().setText(String.valueOf(doc.getDouble("oct_rate")));
                            compdetails_jan_mar_comprate.getEditText().setText(String.valueOf(doc.getDouble("jan_rate")));
                        }
                        else{
                            compdetails_apr_jun_comprate.getEditText().setText(String.valueOf(0.0));
                            compdetails_jul_sep_comprate.getEditText().setText(String.valueOf(0.0));
                            compdetails_oct_dec_comprate.getEditText().setText(String.valueOf(0.0));
                            compdetails_jan_mar_comprate.getEditText().setText(String.valueOf(0.0));
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10001 && resultCode == RESULT_OK) {
            int arr_size = data.getIntExtra("array_size", 0);
            if (arr_size > 0) {
                ArrayList<CalendarDay> selectedDates_arr = (ArrayList<CalendarDay>) data.getSerializableExtra("selected_dates");
                addHolidays(selectedDates_arr);
            } else {
                Snackbar.make(compdetails_mainlay, "No dates selected !! Please select atleast one date !!", Snackbar.LENGTH_LONG).show();
            }
        } else if (requestCode == 10002 && resultCode == RESULT_OK) {
            int remove_arr_size = data.getIntExtra("remove_array_size", 0);
            if (remove_arr_size > 0) {
                ArrayList<CalendarDay> remove_selectedDates_arr = (ArrayList<CalendarDay>) data.getSerializableExtra("remove_selected_dates");
                removeHolidays(remove_selectedDates_arr);
            } else {
                Snackbar.make(compdetails_mainlay, "No dates selected !! Please select atleast one date !!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    public void removeHolidays(ArrayList<CalendarDay> remove_selectedDates_arr) {
        if (comp_path != null) {
            if (remove_selectedDates_arr.size() > 0) {

                ArrayList<String> holidaydates_str_list= new ArrayList<>();
                for(int i=0;i<holiday_list.size();i++){
                    holidaydates_str_list.add(functions.getStringFromDate(holiday_list.get(i).toDate(),"dd/MM/yyyy"));
                }

                for (int i = 0; i < remove_selectedDates_arr.size(); i++) {
                    DocumentReference comp = db.collection("Companies").document(db.document(comp_path).getId());
                    Map<String, Object> updates = new HashMap<>();
                    String remove_item= functions.getStringFromDate(convertCalenderDayToDate(remove_selectedDates_arr.get(i)),"dd/MM/yyyy");
                    if(holidaydates_str_list.contains(remove_item)){
                        updates.put("company_holidays", FieldValue.arrayRemove(holiday_list.get(holidaydates_str_list.indexOf(remove_item))));
                        int i1 = i;
                        comp.update(updates)
                                .addOnSuccessListener(aVoid -> {
                                    if (i1 == remove_selectedDates_arr.size() - 1) {
                                        Snackbar.make(compdetails_mainlay, "All selected dates added successfully", Snackbar.LENGTH_LONG).show();
                                        displayHolidaysList();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Snackbar.make(compdetails_mainlay, "Failed to add holdays !! Please try again", Snackbar.LENGTH_LONG).show();
                                });
                    }
                }
            }
        } else {
            Snackbar.make(compdetails_mainlay, "Unable to remove holdays to your company due to some issues !! Please logout and try again", Snackbar.LENGTH_LONG).show();
        }
    }

    private void companyDetailsInits() {
        compdetails_mainlay = findViewById(R.id.compdetails_mainlay);
        compdetails_back_btn = findViewById(R.id.compdetails_back_btn);
        compdetails_holidaylay = findViewById(R.id.compdetails_holidaylay);
        compdetails_holidayflexlay = findViewById(R.id.compdetails_holidayflexlay);
        compdetails_addmoredate_btn = findViewById(R.id.compdetails_addmoredate_btn);
        compdetails_removedate_btn = findViewById(R.id.compdetails_removedate_btn);
        compdetails_nodates_msg = findViewById(R.id.compdetails_nodates_msg);
        compdetails_holiday_viewall_btn= findViewById(R.id.compdetails_holiday_viewall_btn);
        compdetails_holiday_loading_prog= findViewById(R.id.compdetails_holiday_loading_prog);

        compdetails_update_phonenum_lay= findViewById(R.id.compdetails_update_phonenum_lay);
        compdetails_deactivate_acc_lay= findViewById(R.id.compdetails_deactivate_acc_lay);

        compdetails_update_comp_rate_btn= findViewById(R.id.compdetails_update_comp_rate_btn);
        compdetails_update_govt_rate_btn= findViewById(R.id.compdetails_update_govt_rate_btn);
        compdetails_apr_jun_comprate= findViewById(R.id.compdetails_apr_jun_comprate);
        compdetails_jul_sep_comprate= findViewById(R.id.compdetails_jul_sep_comprate);
        compdetails_oct_dec_comprate= findViewById(R.id.compdetails_oct_dec_comprate);
        compdetails_jan_mar_comprate= findViewById(R.id.compdetails_jan_mar_comprate);

        compdetails_apr_jun_govtrate= findViewById(R.id.compdetails_apr_jun_govtrate);
        compdetails_jul_sep_govtrate= findViewById(R.id.compdetails_jul_sep_govtrate);
        compdetails_oct_dec_govtrate= findViewById(R.id.compdetails_oct_dec_govtrate);
        compdetails_jan_mar_govtrate= findViewById(R.id.compdetails_jan_mar_govtrate);

        apr_jun.addAll(Arrays.asList("apr", "may", "jun"));
        jul_sep.addAll(Arrays.asList("jul", "aug", "sep"));
        oct_dec.addAll(Arrays.asList("oct", "nov", "dec"));
        jan_mar.addAll(Arrays.asList("jan", "feb", "mar"));

        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);

        /*check_month= functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2;
        financial_year = check_month ? Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                Long.parseLong(functions.getTodayDate("yyyy"));*/


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        compdetails_addmoredate_btn.setVisibility(View.GONE);
        compdetails_removedate_btn.setVisibility(View.GONE);
        compdetails_holiday_loading_prog= findViewById(R.id.compdetails_holiday_loading_prog);

        financial_year = functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                Long.parseLong(functions.getTodayDate("yyyy"));

        compdetails_update_comp_rate_btn.setEnabled(false);
        compdetails_update_comp_rate_btn.setClickable(false);
        compdetails_update_govt_rate_btn.setEnabled(false);
        compdetails_update_govt_rate_btn.setClickable(false);
    }

    public void addHolidays(ArrayList<CalendarDay> selectedDates) {
        if (comp_path != null) {
            if (selectedDates.size() > 0) {
                for (int i = 0; i < selectedDates.size(); i++) {
                    DocumentReference comp = db.collection("Companies").document(db.document(comp_path).getId());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("company_holidays", FieldValue.arrayUnion(new Timestamp(convertCalenderDayToDate(selectedDates.get(i)))));
                    int i1 = i;
                    comp.update(updates)
                            .addOnSuccessListener(aVoid -> {
                                if (i1 == selectedDates.size() - 1) {
                                    Snackbar.make(compdetails_mainlay, "All selected dates added successfully", Snackbar.LENGTH_LONG).show();
                                    displayHolidaysList();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Snackbar.make(compdetails_mainlay, "Failed to add holdays !! Please try again", Snackbar.LENGTH_LONG).show();
                            });
                }
            } else {
                Snackbar.make(compdetails_mainlay, "No dates selected !! Please select atleast one date !!", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(compdetails_mainlay, "Unable to add holdays to your company due to some issues !! Please logout and try again", Snackbar.LENGTH_LONG).show();
        }
    }

    @NonNull
    public Date convertCalenderDayToDate(CalendarDay calendarDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, calendarDay.getYear());
        calendar.set(Calendar.MONTH, calendarDay.getMonth() - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendarDay.getDay());
        return calendar.getTime();
    }

    @SuppressLint("NewApi")
    public void displayHolidaysList() {

        compdetails_addmoredate_btn.setVisibility(View.GONE);
        compdetails_removedate_btn.setVisibility(View.GONE);
        compdetails_holiday_loading_prog.setVisibility(View.VISIBLE);

        compdetails_holidayflexlay.removeAllViews();
        if (holiday_list!=null && holiday_list.size() > 0) holiday_list.clear();

        db.collection("Companies")
                .document(db.document(comp_path).getId())
                .get()
                .addOnCompleteListener(task -> {
                    compdetails_holiday_loading_prog.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        ArrayList<Timestamp> holi_data = (ArrayList<Timestamp>) task.getResult().get("company_holidays");
                        // holiday_list = (ArrayList<Timestamp>) task.getResult().get("company_holidays");
                        if (holi_data != null && holi_data.size() > 0) {
                            Calendar check_cal= Calendar.getInstance();
                            check_cal.set(Math.toIntExact(financial_year), 3, 1);
                            Timestamp start_ts= new Timestamp(functions.getDateFromString(functions.getStringFromDate(check_cal.getTime(),"dd/MM/yyyy"),"dd/MM/yyyy"));
                            check_cal.set(Math.toIntExact(financial_year)+1, 3, 1);
                            Timestamp end_ts= new Timestamp(functions.getDateFromString(functions.getStringFromDate(check_cal.getTime(),"dd/MM/yyyy"),"dd/MM/yyyy"));
                            for(Timestamp timestamp : holi_data){
                                timestamp= new Timestamp(functions.getDateFromString(functions.getStringFromDate(timestamp.toDate(),"dd/MM/yyyy"),"dd/MM/yyyy"));
                                if(timestamp.compareTo(start_ts) >= 0 && timestamp.compareTo(end_ts) < 0) {
                                    holiday_list.add(timestamp);
                                }
                            }
                            Collections.sort(holiday_list, Comparator.comparing(Timestamp::toDate));
                            for (int i = 0; i < holiday_list.size(); i++) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(holiday_list.get(i).toDate());
                                String month_txt = (String) DateFormat.format("MMM", holiday_list.get(i).toDate());
                                String date_day_txt = (String) DateFormat.format("dd", holiday_list.get(i).toDate());
                                String year_txt = (String) DateFormat.format("yy", holiday_list.get(i).toDate());

                                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                                LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.holiday_item, null, false);
                                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                        functions.dpToPx(getApplicationContext(), 50), ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.setMargins(functions.dpToPx(getApplicationContext(), 5),
                                        functions.dpToPx(getApplicationContext(), 10), 0, 0);
                                layout.setLayoutParams(params);

                                TextView holiday_date_txt = layout.findViewById(R.id.holiday_date_txt);
                                TextView holiday_month_txt = layout.findViewById(R.id.holiday_month_txt);
                                TextView holiday_year_txt= layout.findViewById(R.id.holiday_year_txt);
                                holiday_date_txt.setText(date_day_txt);
                                holiday_month_txt.setText(month_txt);
                                holiday_year_txt.setText(year_txt);
                                compdetails_holidayflexlay.addView(layout);
                            }

                            compdetails_addmoredate_btn.setVisibility(View.VISIBLE);
                            compdetails_holiday_viewall_btn.setVisibility(View.VISIBLE);
                            compdetails_addmoredate_btn.setText("Add More");
                            compdetails_removedate_btn.setVisibility(View.VISIBLE);
                            compdetails_holidaylay.setVisibility(View.VISIBLE);
                            compdetails_nodates_msg.setVisibility(View.GONE);

                            compdetails_holiday_viewall_btn.setOnClickListener(v->{
                                startActivity(new Intent(getApplicationContext(), ViewHolidaysActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            });

                        } else {
                            compdetails_holiday_viewall_btn.setVisibility(View.GONE);
                            compdetails_addmoredate_btn.setVisibility(View.VISIBLE);
                            compdetails_addmoredate_btn.setText("Add Date");
                            compdetails_removedate_btn.setVisibility(View.GONE);
                            compdetails_holidaylay.setVisibility(View.GONE);
                            compdetails_nodates_msg.setVisibility(View.VISIBLE);
                            Snackbar.make(compdetails_mainlay, "No dates added yet !!", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        compdetails_addmoredate_btn.setVisibility(View.GONE);
                        compdetails_removedate_btn.setVisibility(View.GONE);
                        compdetails_holiday_loading_prog.setVisibility(View.GONE);
                        compdetails_holidaylay.setVisibility(View.GONE);
                        compdetails_nodates_msg.setVisibility(View.VISIBLE);
                        Snackbar.make(compdetails_mainlay, "Some Error Occurred while loading holidays !! Please try again", Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private static class CompanyTextWatcher implements TextWatcher {

        Context context;
        private TextInputLayout textInputLayout;
        private TextInputLayout edtxt1, edtxt2, edtxt3;
        private TextView add_btn;

        public CompanyTextWatcher(Context context, TextInputLayout textInputLayout, TextInputLayout edtxt1, TextInputLayout edtxt2,
                                  TextInputLayout edtxt3, TextView add_btn) {
            this.context = context;
            this.textInputLayout = textInputLayout;
            this.edtxt1 = edtxt1;
            this.edtxt2 = edtxt2;
            this.edtxt3 = edtxt3;
            this.add_btn = add_btn;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String val_txt = textInputLayout.getEditText().getText().toString();
            String edtxt1_txt = edtxt1.getEditText().getText().toString();
            String edtxt2_txt = edtxt2.getEditText().getText().toString();
            String edtxt3_txt = edtxt3.getEditText().getText().toString();

            Double edtxt1_int = !edtxt1_txt.equals("") ? Double.parseDouble(edtxt1_txt) : 0;
            Double edtxt2_int = !edtxt2_txt.equals("") ? Double.parseDouble(edtxt2_txt) : 0;
            Double edtxt3_int = !edtxt3_txt.equals("") ? Double.parseDouble(edtxt3_txt) : 0;
            Double val_int = !val_txt.equals("") ? Double.parseDouble(val_txt) : 0;

            if (val_txt.equals("") || val_int < 1) {
                textInputLayout.setError("Please enter a number greater than 0 !!");
                textInputLayout.setErrorEnabled(true);
                textInputLayout.requestFocus();
            } else {
                textInputLayout.setErrorEnabled(false);
                textInputLayout.setError(null);
            }

            if (val_int > 0 && edtxt1_int > 0 && edtxt2_int > 0 && edtxt3_int > 0) {
                add_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0045AC")));
                add_btn.setEnabled(true);
            } else {
                add_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
                add_btn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    /*ArrayList<Calendar> selectedDates = new ArrayList<>();
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, null,
                    Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

            datePickerDialog.getDatePicker().init(
                    Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH),
                    Calendar.getInstance().get(Calendar.DAY_OF_MONTH), (datePicker, year, month, dayOfMonth) -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        selectedDates.add(calendar);
                        highlightSelectedDates(datePicker, selectedDates);
                    }
            );

            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", (dialogInterface, i) -> {
                // addHolidays(selectedDates);
                Toast.makeText(getApplicationContext(), "The selected dates : "+selectedDates.size(), Toast.LENGTH_SHORT).show();
            });
            datePickerDialog.show();*/
}