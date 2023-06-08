package com.rushikeshsantoshv.wetrack.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rushikeshsantoshv.wetrack.Activities.ManagerProfileActivity;
import com.rushikeshsantoshv.wetrack.DataModels.ManagerModel;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;

public class AddManagersAdapter extends RecyclerView.Adapter<AddManagersAdapter.ListViewHolder> {

    Context context;
    Activity activity;
    private ArrayList<ManagerModel> manager_list;
    int pagecheck;

    public AddManagersAdapter(Context context, ArrayList<ManagerModel> manager_list, int pagecheck, Activity activity) {
        this.context = context;
        this.manager_list = manager_list;
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
        holder.workeritem_wname.setText(manager_list.get(position).getManager_name());
        holder.workeritem_wcontactno.setText(manager_list.get(position).getManager_contact());

        holder.workeritem_mainlay.setOnClickListener(v -> {
            Intent intent= new Intent(context, ManagerProfileActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e("manager_path","The manager path is : "+manager_list.get(position).getManger_path());
            intent.putExtra("manager_name", manager_list.get(position).getManager_name());
            intent.putExtra("manager_contact", manager_list.get(position).getManager_contact());
            intent.putExtra("manager_reference_id", manager_list.get(position).getManger_path());
            context.startActivity(intent);
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            activity.finish();
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
        return manager_list.size();
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
