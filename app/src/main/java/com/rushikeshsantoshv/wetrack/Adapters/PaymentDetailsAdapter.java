package com.rushikeshsantoshv.wetrack.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.DataModels.EmployeePaymentsModel;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentDetailsAdapter extends RecyclerView.Adapter<PaymentDetailsAdapter.ListViewHolder> {

    Context context;
    ArrayList<EmployeePaymentsModel> payment_arr;
    int check;
    Activity activity;
    Functions functions= new Functions();

    public PaymentDetailsAdapter(Context context, Activity activity, ArrayList<EmployeePaymentsModel> payment_arr) {
        this.context = context;
        this.activity= activity;
        this.payment_arr = payment_arr;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_display_item, null);
        return new ListViewHolder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {


        if(functions.checkInternetConnection(activity)){
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Timestamp timestamp = payment_arr.get(position).getPemp_timestamp();
            Timestamp start_date_timestamp = payment_arr.get(position).getPemp_start_date();

            Double prev_arrears = position > 0 ? payment_arr.get(position - 1).getPemp_sal_arrears() : 0;

            holder.payment_dis_day_txt.setText(new Functions().getStringFromDate(start_date_timestamp.toDate(), "dd"));
            holder.payment_dis_month_txt.setText(new Functions().getStringFromDate(start_date_timestamp.toDate(), "MMM"));

            holder.payment_dis_startday_txt.setText(new Functions().getStringFromDate(timestamp.toDate(), "dd"));
            holder.payment_dis_startmonth_txt.setText(new Functions().getStringFromDate(timestamp.toDate(), "MMM"));

            AtomicLong worked_days = new AtomicLong(new Functions().getNoOfDays(start_date_timestamp.toDate(), timestamp.toDate()));
            db.collection("Companies")
                    .document(payment_arr.get(position).getPemp_company_reference().getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            ArrayList<Timestamp> holidays= (ArrayList<Timestamp>) task.getResult().get("company_holidays");
                            Log.e("firebase_holidays_size","The size of holidays is : "+((holidays!=null && holidays.size() > 0) ? holidays.size() : 0));
                            for(Timestamp t : holidays){
                                if(t.compareTo(start_date_timestamp)>=0 && t.compareTo(timestamp)<=0){
                                    worked_days.getAndDecrement();
                                }
                            }
                            holder.payment_dis_weekly_duration_txt.setText("Total Worked Days - " + worked_days + " Days");
                        }
                    });

            db.collection("Attendances")
                    .whereEqualTo("attend_emp_reference", payment_arr.get(position).getPemp_emp_reference())
                    .whereGreaterThanOrEqualTo("attend_date", start_date_timestamp)
                    .whereLessThanOrEqualTo("attend_date", timestamp)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            if (task.getResult().getDocuments().size() > 0) {
                                List<DocumentSnapshot> list = task.getResult().getDocuments();
                                long present_count = 0, half_day_count = 0;
                                for (DocumentSnapshot doc : list) {
                                    if (doc.getLong("attend_status") == 1) {
                                        present_count++;
                                    } else if (doc.getLong("attend_status") == 2) {
                                        half_day_count++;
                                    }
                                }
                                holder.payment_dis_present_txt.setText(present_count+" Full Days | "+half_day_count+" Half Days");
                            } else {
                                String msg = task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error !!";
                                Log.e("firebase_error", "Empty Array Size..." + msg);
                            }
                        } else {
                            String msg = task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error !!";
                            Log.e("firebase_error", "The error is : " + msg);
                        }
                    });

            holder.payment_dis_baserate_txt.setText("Base Rate : ₹ " + payment_arr.get(position).getPemp_base_rate());
            holder.payment_dis_weekly_wage_txt.setText("Weekly Wage - ₹ " + (payment_arr.get(position).getPemp_sal_total() + prev_arrears));
            holder.payment_dis_arrears_pending_txt.setText("Arrears Pending - ₹ " + payment_arr.get(position).getPemp_sal_arrears());
            holder.payment_dis_loan_paid_txt.setText("Loan Paid - ₹ " + payment_arr.get(position).getPemp_loan_paid());
            holder.payment_dis_loan_pending_txt.setText("Loan Pending - ₹ " + payment_arr.get(position).getPemp_loan_balance());
            holder.payment_dis_wage_paid_txt.setText("₹ " + payment_arr.get(position).getPemp_wage_paid());
        }
        else functions.no_internet_dialog(activity, false);
    }

    @Override
    public int getItemCount() {
        return payment_arr.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView payment_dis_weekly_wage_txt, payment_dis_arrears_pending_txt;
        TextView payment_dis_loan_paid_txt, payment_dis_loan_pending_txt, payment_dis_wage_paid_txt;
        TextView payment_dis_startday_txt, payment_dis_startmonth_txt, payment_dis_day_txt, payment_dis_month_txt;
        TextView payment_dis_baserate_txt, payment_dis_weekly_duration_txt;

        TextView payment_dis_present_txt;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);

            payment_dis_day_txt = itemView.findViewById(R.id.payment_dis_day_txt);
            payment_dis_month_txt = itemView.findViewById(R.id.payment_dis_month_txt);

            payment_dis_startday_txt = itemView.findViewById(R.id.payment_dis_startday_txt);
            payment_dis_startmonth_txt = itemView.findViewById(R.id.payment_dis_startmonth_txt);

            payment_dis_weekly_duration_txt = itemView.findViewById(R.id.payment_dis_weekly_duration_txt);
            payment_dis_present_txt = itemView.findViewById(R.id.payment_dis_present_txt);

            payment_dis_weekly_wage_txt = itemView.findViewById(R.id.payment_dis_weekly_wage_txt);
            payment_dis_arrears_pending_txt = itemView.findViewById(R.id.payment_dis_arrears_pending_txt);
            payment_dis_loan_paid_txt = itemView.findViewById(R.id.payment_dis_loan_paid_txt);
            payment_dis_loan_pending_txt = itemView.findViewById(R.id.payment_dis_loan_pending_txt);
            payment_dis_wage_paid_txt = itemView.findViewById(R.id.payment_dis_wage_paid_txt);
            payment_dis_baserate_txt = itemView.findViewById(R.id.payment_dis_baserate_txt);
        }
    }
}
