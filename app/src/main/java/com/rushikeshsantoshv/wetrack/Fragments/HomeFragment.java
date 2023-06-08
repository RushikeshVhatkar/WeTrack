package com.rushikeshsantoshv.wetrack.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.Activities.AppSettingsActivity;
import com.rushikeshsantoshv.wetrack.Activities.CompanyDetailsActivity;
import com.rushikeshsantoshv.wetrack.Activities.EmpViewAttendancesActivity;
import com.rushikeshsantoshv.wetrack.Activities.EmployeesListActivity;
import com.rushikeshsantoshv.wetrack.Activities.FeedbackActivity;
import com.rushikeshsantoshv.wetrack.Activities.ManagersListActivity;
import com.rushikeshsantoshv.wetrack.Activities.MarkAttendancesActivity;
import com.rushikeshsantoshv.wetrack.Activities.PersonalInfoActivity;
import com.rushikeshsantoshv.wetrack.Activities.ViewPaymentsActivity;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

public class HomeFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Functions functions= new Functions();
    ActionBar actionBar;
    TextView title;
    LinearLayout homefrag_mainlay;
    LinearLayout homefrag_compdetails_btn, homefrag_employees_btn, homefrag_managers_btn, homefrag_attendances_btn;
    LinearLayout homefrag_makepayments_btn, homefrag_viewpayments_btn, homefrag_settings_btn, homefrag_profile_btn;
    LinearLayout homefrag_empdetails_lay, homefrag_emp_viewpayments_btn, homefrag_emp_viewattendances_btn;
    LinearLayout homefrag_employeeslay, homefrag_companymanagerlay, homefrag_paymentslay;
    LinearLayout homefrag_reportslay, homefrag_emp_analysis_btn, homefrag_annualreports_btn;
    LinearLayout homefrag_feedback_btn, homefrag_help_btn;

    public HomeFragment(ActionBar actionBar, TextView title) {
        this.actionBar= actionBar;
        this.title= title;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v= inflater.inflate(R.layout.fragment_home, container, false);

        homefrag_mainlay= v.findViewById(R.id.homefrag_mainlay);
        homefrag_employees_btn= v.findViewById(R.id.homefrag_employees_btn);
        homefrag_managers_btn= v.findViewById(R.id.homefrag_managers_btn);
        homefrag_attendances_btn= v.findViewById(R.id.homefrag_attendances_btn);
        homefrag_makepayments_btn= v.findViewById(R.id.homefrag_makepayments_btn);
        homefrag_viewpayments_btn= v.findViewById(R.id.homefrag_viewpayments_btn);
        homefrag_settings_btn= v.findViewById(R.id.homefrag_settings_btn);
        homefrag_profile_btn= v.findViewById(R.id.homefrag_profile_btn);
        homefrag_compdetails_btn= v.findViewById(R.id.homefrag_compdetails_btn);
        homefrag_annualreports_btn= v.findViewById(R.id.homefrag_annualreports_btn);

        homefrag_feedback_btn= v.findViewById(R.id.homefrag_feedback_btn);
        homefrag_help_btn= v.findViewById(R.id.homefrag_help_btn);

        homefrag_employeeslay= v.findViewById(R.id.homefrag_employeeslay);
        homefrag_companymanagerlay= v.findViewById(R.id.homefrag_companymanagerlay);
        homefrag_paymentslay= v.findViewById(R.id.homefrag_paymentslay);

        homefrag_empdetails_lay= v.findViewById(R.id.homefrag_empdetails_lay);
        homefrag_emp_viewpayments_btn= v.findViewById(R.id.homefrag_emp_viewpayments_btn);
        homefrag_emp_viewattendances_btn= v.findViewById(R.id.homefrag_emp_viewattendances_btn);

        homefrag_reportslay= v.findViewById(R.id.homefrag_reportslay);
        homefrag_emp_analysis_btn= v.findViewById(R.id.homefrag_emp_analysis_btn);
        homefrag_annualreports_btn= v.findViewById(R.id.homefrag_annualreports_btn);

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        db= FirebaseFirestore.getInstance();

        String ptype= functions.getSharedPrefsValue(getContext(), "user_data", "ptype", "string", null);
        if(ptype!=null && ptype.equals("Employees")){
            homefrag_employeeslay.setVisibility(View.GONE);
            homefrag_companymanagerlay.setVisibility(View.GONE);
            homefrag_paymentslay.setVisibility(View.GONE);
            homefrag_reportslay.setVisibility(View.GONE);
            homefrag_empdetails_lay.setVisibility(View.VISIBLE);
        }
        else if(ptype!=null && ptype.equals("Managers")){
            homefrag_employeeslay.setVisibility(View.VISIBLE);
            homefrag_companymanagerlay.setVisibility(View.GONE);
            homefrag_paymentslay.setVisibility(View.VISIBLE);
            homefrag_empdetails_lay.setVisibility(View.GONE);
            homefrag_reportslay.setVisibility(View.GONE);
            homefrag_makepayments_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#CDDEFF")));
            homefrag_viewpayments_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFEDEB")));
        }
        else if(ptype!=null){
            homefrag_employeeslay.setVisibility(View.VISIBLE);
            homefrag_companymanagerlay.setVisibility(View.VISIBLE);
            homefrag_paymentslay.setVisibility(View.VISIBLE);
            homefrag_reportslay.setVisibility(View.VISIBLE);
            homefrag_empdetails_lay.setVisibility(View.GONE);
        }


        homefrag_employees_btn.setOnClickListener(v1->{
            if(functions.checkInternetConnection(getActivity())){
                Intent intent= new Intent(getContext(), EmployeesListActivity.class);
                intent.putExtra("pagecheck",0);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        homefrag_managers_btn.setOnClickListener(v1->{
            if(functions.checkInternetConnection(getActivity())){
                startActivity(new Intent(getContext(), ManagersListActivity.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        homefrag_attendances_btn.setOnClickListener(v1->{
            if(functions.checkInternetConnection(getActivity())){
                startActivity(new Intent(getContext(), MarkAttendancesActivity.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        homefrag_emp_analysis_btn.setOnClickListener(v1->{
            if(functions.checkInternetConnection(getActivity())){
                Intent intent= new Intent(getContext(), EmployeesListActivity.class);
                intent.putExtra("pagecheck",3);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        homefrag_annualreports_btn.setOnClickListener(v1->{
            if(functions.checkInternetConnection(getActivity())){
                Intent intent= new Intent(getContext(), EmployeesListActivity.class);
                intent.putExtra("pagecheck",4);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        homefrag_profile_btn.setOnClickListener(v1->{
            startActivity(new Intent(getContext(), PersonalInfoActivity.class));
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        homefrag_settings_btn.setOnClickListener(v1->{
            startActivity(new Intent(getContext(), AppSettingsActivity.class));
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        homefrag_makepayments_btn.setOnClickListener(v1->{
            if(functions.checkInternetConnection(getActivity())){
                Intent intent= new Intent(getContext(), EmployeesListActivity.class);
                intent.putExtra("pagecheck",1);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        homefrag_compdetails_btn.setOnClickListener(v1->{
            if(functions.checkInternetConnection(getActivity())){
                startActivity(new Intent(getContext(), CompanyDetailsActivity.class));
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        homefrag_viewpayments_btn.setOnClickListener(v1->{
            if(functions.checkInternetConnection(getActivity())){
                Intent intent= new Intent(getContext(), EmployeesListActivity.class);
                intent.putExtra("pagecheck",2);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        homefrag_emp_viewpayments_btn.setOnClickListener(v1->{
            String user_path= functions.getSharedPrefsValue(getContext(), "user_data", "user_reference", "string", null);

            if(user_path!=null){
                db.collection("Employees")
                        .document(db.document(user_path).getId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Intent intent= new Intent(getContext(), ViewPaymentsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("emp_name", task.getResult().getString("emp_name"));
                                intent.putExtra("emp_contact", task.getResult().getString("emp_contact"));
                                intent.putExtra("emp_reference_id", task.getResult().getReference().getPath());
                                intent.putExtra("emp_advance_loans", task.getResult().getLong("emp_sal_arrears"));
                                intent.putExtra("emp_sal_arrears",task.getResult().getLong("emp_sal_arrears"));
                                intent.putExtra("emp_sal_paid",task.getResult().getLong("emp_sal_paid"));
                                intent.putExtra("emp_sal_total",task.getResult().getLong("emp_sal_total"));
                                startActivity(intent);
                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            else{
                                Snackbar.make(homefrag_mainlay, "Unable to load the payments !! Please try later", Snackbar.LENGTH_LONG).show();
                            }
                        });
            }
            else{
                Snackbar.make(homefrag_mainlay, "Unable to load the payments !! Please try later", Snackbar.LENGTH_LONG).show();
            }
        });

        homefrag_emp_viewattendances_btn.setOnClickListener(v1->{
            String user_path= functions.getSharedPrefsValue(getContext(), "user_data", "user_reference", "string", null);

            if(user_path!=null){
                db.collection("Employees")
                        .document(db.document(user_path).getId())
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Intent intent= new Intent(getContext(), EmpViewAttendancesActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.putExtra("emp_name", task.getResult().getString("emp_name"));
                                intent.putExtra("emp_contact", task.getResult().getString("emp_contact"));
                                intent.putExtra("emp_reference_id", task.getResult().getReference().getPath());
                                intent.putExtra("emp_advance_loans", task.getResult().getLong("emp_sal_arrears"));
                                intent.putExtra("emp_sal_arrears",task.getResult().getLong("emp_sal_arrears"));
                                intent.putExtra("emp_sal_paid",task.getResult().getLong("emp_sal_paid"));
                                intent.putExtra("emp_sal_total",task.getResult().getLong("emp_sal_total"));
                                startActivity(intent);
                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            }
                            else{
                                Snackbar.make(homefrag_mainlay, "Unable to load the attendances !! Please try later", Snackbar.LENGTH_LONG).show();
                            }
                        });
            }
            else{
                Snackbar.make(homefrag_mainlay, "Unable to load the attendances !! Please try later", Snackbar.LENGTH_LONG).show();
            }
        });

        homefrag_feedback_btn.setOnClickListener(v1->{
            startActivity(new Intent(getContext(), FeedbackActivity.class));
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        actionBar.setTitle("Home Page");
        title.setText("Home Page");
        return v;
    }

}