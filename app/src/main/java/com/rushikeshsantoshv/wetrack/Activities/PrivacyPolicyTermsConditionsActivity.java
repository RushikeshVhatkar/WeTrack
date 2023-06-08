package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

public class PrivacyPolicyTermsConditionsActivity extends AppCompatActivity {

    WebView pptc_webview;
    LinearLayout pptc_mainlay;
    ImageButton pptc_back_btn;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy_terms_conditions);
        new Functions().lightBackgroundStatusBarDesign(PrivacyPolicyTermsConditionsActivity.this);

        pptc_mainlay= findViewById(R.id.pptc_mainlay);
        pptc_webview= findViewById(R.id.pptc_webview);
        pptc_back_btn= findViewById(R.id.pptc_back_btn);

        pptc_back_btn.setOnClickListener(v-> finish());

        Intent intent= getIntent();
        if(intent!=null){
            String weblink= intent.getStringExtra("weblink");
            WebSettings webSettings = pptc_webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            pptc_webview.loadUrl(weblink);
        }
        else{
            Snackbar.make(pptc_mainlay, "Unable to load the link !! Please try again",Snackbar.LENGTH_SHORT).show();
            new Handler().postDelayed(this::finish,1500);
        }
    }
}