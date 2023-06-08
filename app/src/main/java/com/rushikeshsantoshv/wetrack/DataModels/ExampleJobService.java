package com.rushikeshsantoshv.wetrack.DataModels;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

public class ExampleJobService extends JobService {

    private static final String TAG= "ExampleJobService";
    private boolean jobCancelled= false;

    FirebaseFirestore db;

    public ExampleJobService() {}

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        doBackgroundWork(jobParameters);
        return true;
    }

    private void doBackgroundWork(JobParameters params) {

        db= FirebaseFirestore.getInstance();
        if(jobCancelled){
            return;
        }
        db.collection("Trail")
                .document("SoIum9urUQEKj2O22AHZ")
                .update("trailTime", (Timestamp) Timestamp.now())
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
//                        Log.e("firebaseTime","firebaseTime Success");
                    }
                    else{
//                        Log.e("firebaseTime","firebaseTime Failed");
                    }
                });
//        Log.e(TAG,"Job finished...");
        jobFinished(params,false);
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
//        Log.e(TAG,"Job cancelled before completion...");
        jobCancelled= true;
        return true;
    }
}
