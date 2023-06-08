package com.rushikeshsantoshv.wetrack.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.divider.MaterialDivider;
import com.rushikeshsantoshv.wetrack.DataModels.AttendanceModel;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;

public class AttendanceAdapter extends RecyclerView.Adapter<AttendanceAdapter.ListViewHolder> {

    Context context;
    ArrayList<AttendanceModel> arr;
    Boolean isMarked;
    boolean isEdit;
    Functions functions= new Functions();
    Activity activity;
    int page;

    public AttendanceAdapter(Context context, Activity activity, ArrayList<AttendanceModel> arr, Boolean isMarked, boolean isEdit, int page) {
        this.context = context;
        this.activity= activity;
        this.arr = arr;
        this.isMarked = isMarked;
        this.isEdit= isEdit;
        this.page= page;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.emp_attendance_item, null);
        return new ListViewHolder(inflate);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        if(functions.checkInternetConnection(activity)){
            if (isMarked && !isEdit) {
                if(page!=1){
                    arr.get(position).getAttend_emp_reference()
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    holder.workeritem_wname.setText(task.getResult().getString("emp_name"));
                                    holder.workeritem_wcontactno.setText(task.getResult().getString("emp_contact"));
                                }
                            });
                }

                holder.empitem_completed_lay.setVisibility(View.VISIBLE);
                holder.emp_attendance_grouplay.setVisibility(View.GONE);

                long status = arr.get(position).getAttend_status();
                holder.emp_attendance_absent_tv.setVisibility(View.GONE);
                holder.emp_attendance_halfday_tv.setVisibility(View.GONE);
                holder.emp_attendance_present_tv.setVisibility(View.GONE);

                if (status == 1)
                    holder.emp_attendance_present_tv.setVisibility(View.VISIBLE);
                else if (status == 2)
                    holder.emp_attendance_halfday_tv.setVisibility(View.VISIBLE);
                else if (status == 3)
                    holder.emp_attendance_absent_tv.setVisibility(View.VISIBLE);
                else
                    holder.emp_attendance_present_tv.setVisibility(View.VISIBLE);

                if(page==1){
                    ViewGroup.LayoutParams layoutParams1= holder.emp_attendance_present_tv.getLayoutParams();
                    layoutParams1.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.emp_attendance_present_tv.setLayoutParams(layoutParams1);
                    holder.emp_attendance_present_tv.setPadding(20, 0, 20, 0);

                    ViewGroup.LayoutParams layoutParams2= holder.emp_attendance_absent_tv.getLayoutParams();
                    layoutParams2.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.emp_attendance_absent_tv.setLayoutParams(layoutParams2);
                    holder.emp_attendance_absent_tv.setPadding(20, 0, 20, 0);

                    ViewGroup.LayoutParams layoutParams3= holder.emp_attendance_halfday_tv.getLayoutParams();
                    layoutParams3.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    holder.emp_attendance_halfday_tv.setLayoutParams(layoutParams3);
                    holder.emp_attendance_halfday_tv.setPadding(20, 0, 20, 0);

                    holder.workeritem_wname.setText(""+functions.getStringFromDate(arr.get(position).getAttend_date().toDate(),"dd, MMMM, yyyy"));
                    holder.workeritem_wcontactno.setVisibility(View.GONE);
                    holder.emp_attendance_present_tv.setText("Present");
                    holder.emp_attendance_absent_tv.setText("Absent");
                    holder.emp_attendance_halfday_tv.setText("Half Day");

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.workeritem_divider.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.BELOW, holder.workeritem_datalay.getId());
                    holder.workeritem_divider.setLayoutParams(layoutParams);
                }
                else{
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.workeritem_divider.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.BELOW, holder.workeritem_textlay.getId());
                    holder.workeritem_divider.setLayoutParams(layoutParams);
                }
            }
            else {
                if(isEdit){
                    arr.get(position).getAttend_emp_reference()
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    holder.workeritem_wname.setText(task.getResult().getString("emp_name"));
                                    holder.workeritem_wcontactno.setText(task.getResult().getString("emp_contact"));
                                }
                            });
                }
                else{
                    holder.workeritem_wname.setText(arr.get(position).getEmp_name());
                    holder.workeritem_wcontactno.setText(arr.get(position).getEmp_contact());
                }

                holder.empitem_completed_lay.setVisibility(View.GONE);
                holder.emp_attendance_grouplay.setVisibility(View.VISIBLE);

                long status = arr.get(position).getAttend_status();
                int id = (status == 1) ? R.id.emp_attendance_present : ((status == 2) ? R.id.emp_attendance_half : ((status == 3) ? R.id.emp_attendance_absent : R.id.emp_attendance_present));
                holder.emp_attendance_radiogroup.check(id);

                holder.emp_attendance_radiogroup.setOnCheckedChangeListener((radioGroup, i) -> {
                    int val = (R.id.emp_attendance_present == i) ? 1 : ((R.id.emp_attendance_half == i) ? 2 : ((R.id.emp_attendance_absent == i) ? 3 : 1));
                    arr.get(position).setAttend_status(val);
                    this.notifyDataSetChanged();
                });
            }
        }
        else functions.no_internet_dialog(activity, false);
    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ListViewHolder extends RecyclerView.ViewHolder {

        TextView workeritem_wname, workeritem_wcontactno;
        RadioGroup emp_attendance_radiogroup;
        RadioButton emp_attendance_present, emp_attendance_half, emp_attendance_absent;
        RelativeLayout workeritem_mainlay, emp_attendance_grouplay;
        LinearLayout empitem_completed_lay;
        TextView emp_attendance_present_tv, emp_attendance_halfday_tv, emp_attendance_absent_tv;
        MaterialDivider workeritem_divider;
        RelativeLayout workeritem_datalay;
        LinearLayout workeritem_textlay;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            workeritem_wname = itemView.findViewById(R.id.workeritem_wname);
            workeritem_wcontactno = itemView.findViewById(R.id.workeritem_wcontactno);
            emp_attendance_radiogroup = itemView.findViewById(R.id.emp_attendance_radiogroup);
            emp_attendance_present = itemView.findViewById(R.id.emp_attendance_present);
            emp_attendance_half = itemView.findViewById(R.id.emp_attendance_half);
            emp_attendance_absent = itemView.findViewById(R.id.emp_attendance_absent);
            workeritem_mainlay= itemView.findViewById(R.id.workeritem_mainlay);

            emp_attendance_grouplay = itemView.findViewById(R.id.emp_attendance_grouplay);
            empitem_completed_lay = itemView.findViewById(R.id.empitem_completed_lay);
            emp_attendance_present_tv = itemView.findViewById(R.id.emp_attendance_present_tv);
            emp_attendance_halfday_tv = itemView.findViewById(R.id.emp_attendance_halfday_tv);
            emp_attendance_absent_tv = itemView.findViewById(R.id.emp_attendance_absent_tv);

            workeritem_divider= itemView.findViewById(R.id.workeritem_divider);
            workeritem_datalay= itemView.findViewById(R.id.workeritem_datalay);
            workeritem_textlay= itemView.findViewById(R.id.workeritem_textlay);

        }
    }
}
