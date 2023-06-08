package com.rushikeshsantoshv.wetrack.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.rushikeshsantoshv.wetrack.DataModels.AnnualReportModel;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;

public class AnnualReportAdapter extends RecyclerView.Adapter<AnnualReportAdapter.ListViewHolder> {

    Context context;
    ArrayList<AnnualReportModel> arr= new ArrayList<>();

    public AnnualReportAdapter(Context context, ArrayList<AnnualReportModel> arr) {
        this.context = context;
        this.arr = arr;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.annual_report_item, null);
        return new ListViewHolder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {

        double comp_wages_amt= (arr.get(position).getCompBaseRate() * arr.get(position).getFullPresentCount()) +
                (((arr.get(position).getCompBaseRate()) / 2) * arr.get(position).getHalfPresentCount());

        double govt_wages_amt= (arr.get(position).getGovtBaseRate() * arr.get(position).getFullPresentCount()) +
                (((arr.get(position).getGovtBaseRate()) / 2) * arr.get(position).getHalfPresentCount());

        double net_balance= govt_wages_amt - comp_wages_amt;
        String net_val_sign= net_balance >= 0 ? "+" : "-";
        Drawable icon= net_balance >= 0 ? ContextCompat.getDrawable(context,R.drawable.ic_arrow_up) :
                ContextCompat.getDrawable(context,R.drawable.ic_arrow_down);
        ColorStateList icon_color= net_balance >= 0 ? ContextCompat.getColorStateList(context,R.color.my_green) :
                ContextCompat.getColorStateList(context,R.color.my_red);

        holder.annualreport_month_txt.setText(arr.get(position).getCurrMonth());
        holder.annualreport_total_daysworked.setText(""+arr.get(position).getDaysWorked()+" Days");
        holder.annualreport_present_count.setText(""+arr.get(position).getFullPresentCount()+" - Full Days");
        holder.annualreport_halfday_count.setText(""+arr.get(position).getHalfPresentCount()+" - Half Days");
        holder.annualreport_govtwages_subamt.setText("(₹ "+arr.get(position).getGovtBaseRate()+" x "+arr.get(position).getDaysWorked()+")");
        holder.annualreport_govtwages_amt.setText("+ ₹ "+govt_wages_amt);
        holder.annualreport_compwages_subamt.setText("(₹ "+arr.get(position).getCompBaseRate()+" x "+arr.get(position).getDaysWorked()+")");
        holder.annualreport_compwages_amt.setText("- ₹ "+comp_wages_amt);

        holder.annualreport_compwagespaid_subamt.setText("(₹ "+comp_wages_amt+" - ₹ "+
                arr.get(position).getWageAdvancePaid()+" - \n₹ "+arr.get(position).getAdditionalArrears()+")");
        holder.annualreport_compwagespaid_amt.setText("₹ "+arr.get(position).getCompWagesPaid());

        double prev_loan_balance= position==0 ? 0 : arr.get(position-1).getLoanBalance();
        arr.get(position).setPrevLoanBalance(prev_loan_balance);
        double loan_balance= arr.get(position).getPrevLoanBalance() - arr.get(position).getAdvancePaid() + arr.get(position).getAdvanceTaken();
        arr.get(position).setLoanBalance(loan_balance);
        holder.annualreport_loanbalance_subamt.setText("(₹ "+arr.get(position).getPrevLoanBalance()+" - ₹ "+arr.get(position).getAdvancePaid()+ " + \n₹ "+arr.get(position).getAdvanceTaken()+")");

        holder.annualreport_loanbalance_amt.setText("₹ "+arr.get(position).getLoanBalance());
        holder.annualreport_loan_taken.setText("₹ "+arr.get(position).getAdvanceTaken());
        holder.annualreport_wagearrears.setText(""+arr.get(position).getAdditionalArrears());
        holder.annualreport_finalamt_txt.setText(net_val_sign+" ₹ "+Math.abs(net_balance));
        holder.annualreport_finalamt_txt.setTextColor(icon_color);
        holder.annualreport_finalamt_status_icon.setImageDrawable(icon);
        holder.annualreport_finalamt_status_icon.setImageTintList(icon_color);

    }

    @Override
    public int getItemCount() {
        return arr.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView annualreport_month_txt, annualreport_total_daysworked, annualreport_present_count, annualreport_halfday_count;
        TextView annualreport_govtwages_subamt, annualreport_govtwages_amt;
        TextView annualreport_compwages_subamt, annualreport_compwages_amt;
        TextView annualreport_compwagespaid_subamt, annualreport_compwagespaid_amt;
        TextView annualreport_loan_paid, annualreport_loan_taken;
        TextView annualreport_loanbalance_subamt, annualreport_loanbalance_amt;
        TextView annualreport_wagearrears;
        TextView annualreport_finalamt_txt;
        ImageView annualreport_finalamt_status_icon;


        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            annualreport_month_txt= itemView.findViewById(R.id.annualreport_month_txt);
            annualreport_total_daysworked= itemView.findViewById(R.id.annualreport_total_daysworked);
            annualreport_present_count= itemView.findViewById(R.id.annualreport_present_count);
            annualreport_halfday_count= itemView.findViewById(R.id.annualreport_halfday_count);
            annualreport_govtwages_subamt= itemView.findViewById(R.id.annualreport_govtwages_subamt);
            annualreport_govtwages_amt= itemView.findViewById(R.id.annualreport_govtwages_amt);
            annualreport_compwages_subamt= itemView.findViewById(R.id.annualreport_compwages_subamt);
            annualreport_compwages_amt= itemView.findViewById(R.id.annualreport_compwages_amt);
            annualreport_compwagespaid_subamt= itemView.findViewById(R.id.annualreport_compwagespaid_subamt);
            annualreport_compwagespaid_amt= itemView.findViewById(R.id.annualreport_compwagespaid_amt);
            annualreport_loanbalance_subamt= itemView.findViewById(R.id.annualreport_loanbalance_subamt);
            annualreport_loanbalance_amt= itemView.findViewById(R.id.annualreport_loanbalance_amt);
            annualreport_loan_taken= itemView.findViewById(R.id.annualreport_loan_taken);
            annualreport_loan_paid= itemView.findViewById(R.id.annualreport_loan_paid);
            annualreport_wagearrears= itemView.findViewById(R.id.annualreport_wagearrears);
            annualreport_finalamt_txt= itemView.findViewById(R.id.annualreport_finalamt_txt);
            annualreport_finalamt_status_icon= itemView.findViewById(R.id.annualreport_finalamt_status_icon);
        }
    }
}
