package com.rushikeshsantoshv.wetrack.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rushikeshsantoshv.wetrack.Activities.AnnualReportActivity;
import com.rushikeshsantoshv.wetrack.Activities.EmployeeAnalysisActivity;
import com.rushikeshsantoshv.wetrack.Activities.EmployeeProfileActivity;
import com.rushikeshsantoshv.wetrack.Activities.MakePaymentsActivity;
import com.rushikeshsantoshv.wetrack.Activities.ViewPaymentsActivity;
import com.rushikeshsantoshv.wetrack.DataModels.EmployeeModel;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;

public class EmployeeListAdapter extends RecyclerView.Adapter<EmployeeListAdapter.ListViewHolder> {

    Context context;
    Activity activity;
    private ArrayList<EmployeeModel> employees_list;
    int pagecheck;

    public EmployeeListAdapter(Context context, ArrayList<EmployeeModel> employees_list, int pagecheck, Activity activity) {
        this.context = context;
        this.employees_list = employees_list;
        this.pagecheck = pagecheck;
        this.activity= activity;
    }

    @NonNull
    @Override
    public ListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.workers_item, null);
        return new ListViewHolder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ListViewHolder holder, int position) {
        holder.workeritem_srno.setText("" + (position + 1));
        holder.workeritem_wname.setText(employees_list.get(position).getEmp_name());
        holder.workeritem_wcontactno.setText(employees_list.get(position).getEmp_contact());

        holder.workeritem_mainlay.setOnClickListener(v -> {
            Class page= pagecheck == 1  ? MakePaymentsActivity.class :
                    (pagecheck==2 ? ViewPaymentsActivity.class :
                    (pagecheck == 3 ? EmployeeAnalysisActivity.class :
                    (pagecheck==4 ? AnnualReportActivity.class : EmployeeProfileActivity.class)));
            Intent intent= new Intent(context,page);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("emp_name", employees_list.get(position).getEmp_name());
            intent.putExtra("emp_contact", employees_list.get(position).getEmp_contact());
            intent.putExtra("emp_reference_id", employees_list.get(position).getEmp_reference());
            intent.putExtra("emp_advance_loans", employees_list.get(position).getEmp_advance_loans());
            intent.putExtra("emp_sal_arrears",employees_list.get(position).getEmp_sal_arrears());
            intent.putExtra("emp_sal_paid",employees_list.get(position).getEmp_sal_paid());
            intent.putExtra("emp_sal_total",employees_list.get(position).getEmp_sal_total());
            if(page==EmployeeProfileActivity.class){
                activity.finish();
            }
            context.startActivity(intent);
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//            activity.finish();

            /*FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("Payments")
                    .whereEqualTo("pemp_reference", (DocumentReference) db.document(employees_list.get(position).getEmp_reference()))
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                            db.collection("Payments")
                                    .document(task.getResult().getDocuments().get(0).getId())
                                    .collection("payment_reports")
                                    .orderBy("pemp_timestamp", Query.Direction.DESCENDING)
                                    .get()
                                    .addOnCompleteListener(task1 -> {
                                        if(task1.isSuccessful() && task1.getResult().getDocuments().size() > 0){
                                            DocumentSnapshot latest_data= task1.getResult().getDocuments().get(0);
                                            intent.putExtra("emp_sal_arrears", (Long) latest_data.getLong("pemp_sal_arrears"));
                                            intent.putExtra("emp_sal_paid",(Long) latest_data.getLong("pemp_sal_paid"));
                                            context.startActivity(intent);
                                        }
                                    });
                        }
                    });*/
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return employees_list.size();
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        TextView workeritem_wname, workeritem_wcontactno, workeritem_srno;
        RelativeLayout workeritem_mainlay;

        public ListViewHolder(@NonNull View itemView) {
            super(itemView);
            workeritem_wname = itemView.findViewById(R.id.workeritem_wname);
            workeritem_wcontactno = itemView.findViewById(R.id.workeritem_wcontactno);
            workeritem_mainlay = itemView.findViewById(R.id.workeritem_mainlay);
            workeritem_srno = itemView.findViewById(R.id.workeritem_srno);
        }
    }
}
