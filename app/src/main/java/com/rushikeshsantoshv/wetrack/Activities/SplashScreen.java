package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        new Functions().coloredStatusBarDarkTextDesign(SplashScreen.this, R.color.white, R.color.splash_screen_navcolor);
        boolean checkUser = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "login_status", "boolean", false);
        new Handler().postDelayed(() -> {
            if (firebaseUser != null && checkUser) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            } else {
                startActivity(new Intent(getApplicationContext(), SelectUserActivity.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            }
        }, 2000);


        /*ComponentName componentName = new ComponentName(SplashScreen.this, ExampleJobService.class);
        JobInfo info = new JobInfo.Builder(123, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .setPeriodic(15 * 60 * 1000)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultcode = jobScheduler.schedule(info);
        if (resultcode == JobScheduler.RESULT_SUCCESS) {
            Log.e("ExampleJobService", "Job Scheduled..");
        } else {
            Log.e("ExampleJobService", "Job not Scheduled..");
        }*/

    }

    /*db.collection("Employees")
            .get()
                .addOnCompleteListener(task -> {
        if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
            List<DocumentSnapshot> emp_list= task.getResult().getDocuments();
            for(DocumentSnapshot emp_doc : emp_list){

                Map<String, Object> data = new HashMap<>();
                data.put("pemp_emp_reference", emp_doc.getReference());
                data.put("pemp_company_reference", emp_doc.getDocumentReference("company_path"));
                data.put("pemp_timestamp", Timestamp.now());
                data.put("pemp_sal_aries", 0);
                data.put("pemp_sal_paid", 0);
                data.put("pemp_sal_total", 0);
                data.put("pemp_wage_total", 0);
                data.put("pemp_loan_paid", 0);
                data.put("pemp_loan_total", 0);

                db.collection("Payments")
                        .add(data)
                        .addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Log.e("payment_add", "payment added.....");
                            }
                        });
            }
        }
    });*/

    /*db.collection("Employees")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0) {
                        List<DocumentSnapshot> emp_list= task.getResult().getDocuments();
                        for(DocumentSnapshot emp_doc : emp_list){

                            Map<String, Object> data = new HashMap<>();
                            data.put("pemp_timestamp", Timestamp.now());
                            data.put("pemp_sal_aries", 0);
                            data.put("pemp_sal_paid", 0);
                            data.put("pemp_sal_total", 0);
                            data.put("pemp_wage_total", 0);
                            data.put("pemp_loan_paid", 0);
                            data.put("pemp_loan_total", 0);

                            db.collection("Payments")
                                    .document(emp_doc.getId())
                                    .collection("payment_reports")
                                    .add(data)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Log.e("payment_add", "payment added.....");
                                        }
                                    });
                        }
                    }
                });*/

    /*db.collection("Employees")
            .get()
                .addOnCompleteListener(task -> {
        if(task.isSuccessful() && task.getResult().getDocuments().size() > 0){
            List<DocumentSnapshot> list= task.getResult().getDocuments();
            for(DocumentSnapshot doc : list){
                db.collection("Payments")
                        .document(doc.getId())
                        .collection("loan_reports")
                        .get()
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful() && task1.getResult().getDocuments().size() > 0){
                                List<DocumentSnapshot> inner_list= task1.getResult().getDocuments();
                                for(DocumentSnapshot inner_doc : inner_list){
                                    inner_doc.getReference().delete().addOnCompleteListener(task2 -> {
                                        if(task2.isSuccessful()){
                                            Log.e("delete_code","Deleted successfully......");
                                        }
                                    });
                                }
                            }
                        });

            }

        }
    });*/

    /*db.collection("Payments")
            .document("AXyg2PDXxFKvRzg64K4A")
                .collection("loan_reports")
                .orderBy("loan_timestamp",Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(task -> {
        if(task.isSuccessful() && task.getResult().getDocuments().size() > 0){
            List<DocumentSnapshot> pay_list= task.getResult().getDocuments();
            for(DocumentSnapshot doc : pay_list){

                Map<String, Object> data= new HashMap<>();
                data.put("loan_amount",doc.getLong("loan_val"));
                data.put("loan_balance", 1000);
                data.put("loan_company_reference", db.document("Companies/pUuIr6q3TyFVMS8MlEsw"));
                data.put("loan_emp_reference", db.document("Employees/AXyg2PDXxFKvRzg64K4A"));
                data.put("loan_from_sal", false);
                data.put("loan_pay_status", false);
                data.put("loan_timestamp", doc.getTimestamp("loan_timestamp"));

                db.collection("loan_collection")
                        .add(data)
                        .addOnCompleteListener(task1 -> {
                            Log.e("loan_add",doc.getLong("loan_val")+" added.....");
                        });
            }
        }
    });*/

    /*Date date= new Functions().getDateFromString("01-03-2023","dd-MM-yyyy");
        db.collection("Employees")
                .get()
                .addOnCompleteListener(task -> {
        if(task.isSuccessful() && task.getResult().getDocuments().size() > 0){
            List<DocumentSnapshot> emp_list= task.getResult().getDocuments();
            for(DocumentSnapshot emp: emp_list){
                Map<String, Object> loan_data = new HashMap<>();
                loan_data.put("loan_emp_reference", emp.getReference());
                loan_data.put("loan_company_reference",emp.getDocumentReference("company_path"));
                loan_data.put("loan_amount", emp.getLong("emp_advance_loans"));
                loan_data.put("loan_balance",emp.getLong("emp_advance_loans"));
                loan_data.put("loan_pay_status", false);
                loan_data.put("loan_from_sal", false);
                loan_data.put("loan_timestamp", new Timestamp(date));

                db.collection("loan_collection")
                        .add(loan_data)
                        .addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                Log.e("emp_loan_add",emp.getString("emp_name")+" added....");
                            }
                        });
            }
        }
    });*/
}