package com.rushikeshsantoshv.wetrack.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.rushikeshsantoshv.wetrack.Adapters.AttendanceAdapter;
import com.rushikeshsantoshv.wetrack.DataModels.AttendanceModel;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.ArrayList;
import java.util.List;

public class EmpViewAttendancesActivity extends AppCompatActivity {

    LinearLayout empviewattendances_mainlay;
    RecyclerView empviewattendances_rec;
    ImageButton empviewattendances_back_btn;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    String comp_path;
    Functions functions= new Functions();
    Intent getData;
    ArrayList<AttendanceModel> attendance_arr = new ArrayList<>();
    AttendanceAdapter attendanceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emp_view_attendances);

        functions.coloredStatusBarDarkTextDesign(this, R.color.maincolor_light, R.color.white);
        empviewattendances_back_btn= findViewById(R.id.empviewattendances_back_btn);
        empviewattendances_rec= findViewById(R.id.empviewattendances_rec);
        empviewattendances_mainlay= findViewById(R.id.empviewattendances_mainlay);
        firebaseAuth= FirebaseAuth.getInstance();
        firebaseUser= firebaseAuth.getCurrentUser();
        db= FirebaseFirestore.getInstance();
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        getData= getIntent();

        empviewattendances_back_btn.setOnClickListener(v-> finish());

        if(functions.checkInternetConnection(this)){
            if(getData!=null){
                String emp_path= getData.getStringExtra("emp_reference_id");
                db.collection("Attendances")
                        .whereEqualTo("attend_emp_reference", db.document(emp_path))
                        .orderBy("attend_date", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                if(task.getResult().getDocuments().size()  > 0){
                                    List<DocumentSnapshot> doc_arr= task.getResult().getDocuments();
                                    for(DocumentSnapshot doc : doc_arr){
                                        attendance_arr.add(new AttendanceModel(". . . . .", ". . . . .",
                                                doc.getId(),
                                                doc.getDocumentReference("attend_emp_reference"),
                                                doc.getDocumentReference("attend_company_reference"),
                                                doc.getTimestamp("attend_date"),
                                                doc.getLong("attend_status")));
                                    }
                                    attendanceAdapter = new AttendanceAdapter(getApplicationContext(), this, attendance_arr, true, false, 1);
                                    empviewattendances_rec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    empviewattendances_rec.setAdapter(attendanceAdapter);
                                    attendanceAdapter.notifyDataSetChanged();
                                }
                            }
                        });
            }
            else{
                Snackbar.make(empviewattendances_mainlay, "Unable to load data duw to some issues !! Please try again later", Snackbar.LENGTH_LONG).show();
                new Handler().postDelayed(this::finish,2000);
            }
        }
        else functions.no_internet_dialog(this, true);
    }
}