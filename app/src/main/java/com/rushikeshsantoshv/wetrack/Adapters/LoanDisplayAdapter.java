package com.rushikeshsantoshv.wetrack.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.rushikeshsantoshv.wetrack.DataModels.EmployeeLoansModel;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;

public class LoanDisplayAdapter extends RecyclerView.Adapter<LoanDisplayAdapter.ListViewHolder> {

    Context context;
    ArrayList<EmployeeLoansModel> loan_arr;

    public LoanDisplayAdapter(Context context, ArrayList<EmployeeLoansModel> loan_arr) {
        this.context = context;
        this.loan_arr = loan_arr;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.loan_display_item, null);
        return new ListViewHolder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        Timestamp timestamp= loan_arr.get(position).getLoan_timestamp();
        String day= new Functions().getStringFromDate(timestamp.toDate(),"dd");
        String month= new Functions().getStringFromDate(timestamp.toDate(),"MMM");
        String year= new Functions().getStringFromDate(timestamp.toDate(),"yyy");
        holder.loan_dis_day_txt.setText(day);
        holder.loan_dis_month_txt.setText(month);
        holder.loan_dis_year_txt.setText(year);

        holder.loan_dis_loan_balance.setText("Loan Balance - ₹ "+loan_arr.get(position).getLoan_balance());
        holder.loan_dis_loan_amt_txt.setText("₹ "+loan_arr.get(position).getLoan_amount());
        if(loan_arr.get(position).isLoan_pay_status()){
            holder.loan_dis_loanpaid_option.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check));
            holder.loan_dis_loanpaid_option.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.my_green)));
            holder.loan_dis_loantaken_option.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
            holder.loan_dis_loantaken_option.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.my_red)));
            holder.loan_dis_loan_amt_txt.setTextColor(ContextCompat.getColor(context, R.color.my_green));
            holder.loan_dis_loan_status_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_down));
            holder.loan_dis_loan_status_icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.my_green)));
        }
        else{
            holder.loan_dis_loantaken_option.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_check));
            holder.loan_dis_loantaken_option.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.my_green)));
            holder.loan_dis_loanpaid_option.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_close));
            holder.loan_dis_loanpaid_option.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.my_red)));
            holder.loan_dis_loan_amt_txt.setTextColor(ContextCompat.getColor(context, R.color.my_red));
            holder.loan_dis_loan_status_icon.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_arrow_up));
            holder.loan_dis_loan_status_icon.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.my_red)));
        }
    }

    @Override
    public int getItemCount() {
        return loan_arr.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView loan_dis_day_txt, loan_dis_month_txt, loan_dis_loan_balance, loan_dis_year_txt, loan_dis_loan_amt_txt;
        ImageView loan_dis_loanpaid_option, loan_dis_loantaken_option, loan_dis_loan_status_icon;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            loan_dis_day_txt= itemView.findViewById(R.id.loan_dis_day_txt);
            loan_dis_month_txt= itemView.findViewById(R.id.loan_dis_month_txt);
            loan_dis_loan_balance= itemView.findViewById(R.id.loan_dis_loan_balance);
            loan_dis_year_txt= itemView.findViewById(R.id.loan_dis_year_txt);
            loan_dis_loanpaid_option= itemView.findViewById(R.id.loan_dis_loanpaid_option);
            loan_dis_loantaken_option= itemView.findViewById(R.id.loan_dis_loantaken_option);
            loan_dis_loan_amt_txt= itemView.findViewById(R.id.loan_dis_loan_amt_txt);
            loan_dis_loan_status_icon= itemView.findViewById(R.id.loan_dis_loan_status_icon);
        }
    }
}
