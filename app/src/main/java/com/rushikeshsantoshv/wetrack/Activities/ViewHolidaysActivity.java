package com.rushikeshsantoshv.wetrack.Activities;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import com.google.android.flexbox.FlexboxLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class ViewHolidaysActivity extends AppCompatActivity {

    RelativeLayout viewholidays_mainlay, viewholidays_holidaylay;
    ImageButton viewholidays_back_btn;
    FlexboxLayout viewholidays_holidayflexlay;
    TextView viewholidays_nodata_txt;
    NestedScrollView viewholidays_nestlay;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    String comp_path;
    ArrayList<Timestamp> holiday_list= new ArrayList<>();
    Functions functions= new Functions();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_holidays);

        functions.coloredStatusBarDarkTextDesign(ViewHolidaysActivity.this, R.color.maincolor_light, R.color.white);
        viewholidays_nestlay= findViewById(R.id.viewholidays_nestlay);
        viewholidays_mainlay= findViewById(R.id.viewholidays_mainlay);
        viewholidays_holidaylay= findViewById(R.id.viewholidays_holidaylay);
        viewholidays_nodata_txt= findViewById(R.id.viewholidays_nodata_txt);
        viewholidays_back_btn= findViewById(R.id.viewholidays_back_btn);
        viewholidays_holidayflexlay= findViewById(R.id.viewholidays_holidayflexlay);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        db= FirebaseFirestore.getInstance();
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);

        viewholidays_back_btn.setOnClickListener(v-> finish());

        if(functions.checkInternetConnection(this)){
            db.collection("Companies")
                    .document(db.document(comp_path).getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            holiday_list = (ArrayList<Timestamp>) task.getResult().get("company_holidays");
                            if (holiday_list != null && holiday_list.size() > 0) {
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
                                    viewholidays_holidayflexlay.addView(layout);
                                    viewholidays_nestlay.setVisibility(View.VISIBLE);
                                    viewholidays_nodata_txt.setVisibility(View.GONE);
                                }
                            }
                            else{
                                viewholidays_nestlay.setVisibility(View.GONE);
                                viewholidays_nodata_txt.setVisibility(View.VISIBLE);
                            }
                        }
                    });
        }
        else functions.no_internet_dialog(this, true);
    }
}