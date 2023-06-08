package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.Collections;

public class AddHolidaysActivity extends AppCompatActivity {

    MaterialCalendarView add_holi_calenderview;
    TextView add_holi_cancel_btn, add_holi_adddates_btn;
    ArrayList<CalendarDay> selectedDates = new ArrayList<>();
    ArrayList<CalendarDay> removeselectedDates = new ArrayList<>();
    Intent getValueIntent;
    boolean remove_check= false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_holidays);

        getValueIntent= getIntent();
        add_holi_calenderview = findViewById(R.id.add_holi_calenderview);
        add_holi_cancel_btn = findViewById(R.id.add_holi_cancel_btn);
        add_holi_adddates_btn = findViewById(R.id.add_holi_adddates_btn);

        add_holi_calenderview.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        if(getValueIntent!=null && getValueIntent.getBooleanExtra("remove_check",false)){
            selectedDates= (ArrayList<CalendarDay>) getValueIntent.getSerializableExtra("selecteddates");
            remove_check= getValueIntent.getBooleanExtra("remove_check",true);
            Collections.sort(selectedDates, (t1, t2) -> {
                if (t1.isBefore(t2)) {
                    return -1;
                } else if (t1.isAfter(t2)) {
                    return 1;
                } else {
                    return 0;
                }
            });
            add_holi_calenderview.state().edit().setMinimumDate(selectedDates.get(0)).commit();
            add_holi_calenderview.state().edit().setMaximumDate(selectedDates.get(selectedDates.size()-1)).commit();

            add_holi_calenderview.addDecorator(new DayViewDecorator() {
                @Override
                public boolean shouldDecorate(CalendarDay day) {
                    return !selectedDates.contains(day);
                }

                @Override
                public void decorate(DayViewFacade view) {
                    view.setDaysDisabled(true);
                }
            });

            add_holi_calenderview.setDayFormatter(day -> {
                if (selectedDates.contains(day))
                    add_holi_calenderview.setDateSelected(day, true);
                return String.valueOf(day.getDay());
            });
        }
        else{
            add_holi_calenderview.state().edit().setMinimumDate(CalendarDay.today()).commit();
        }

        if(remove_check){
            add_holi_adddates_btn.setText("Remove Dates");
        }
        else{
            add_holi_adddates_btn.setText("Add Dates");
        }

        add_holi_calenderview.setOnDateChangedListener((widget, date, selected) -> {
            if(remove_check){
                if (!selected) {
                    selectedDates.remove(date);
                    removeselectedDates.add(date);

                } else {
                    selectedDates.add(date);
                    removeselectedDates.remove(date);
                }
            }
            else{
                if (!selected) {
                    selectedDates.remove(date);
                } else {
                    selectedDates.add(date);
                }
            }
        });

        add_holi_adddates_btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            if(remove_check){
                intent.putExtra("remove_array_size", removeselectedDates != null ? removeselectedDates.size() : 0);
                intent.putExtra("remove_selected_dates", removeselectedDates);
                setResult(RESULT_OK, intent);
            }
            else{
                intent.putExtra("array_size", selectedDates != null ? selectedDates.size() : 0);
                intent.putExtra("selected_dates", selectedDates);
                setResult(RESULT_OK, intent);
            }
            finish();
        });

        add_holi_cancel_btn.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        });

    }
}