package com.rushikeshsantoshv.wetrack.Fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;
import com.rushikeshsantoshv.wetrack.Activities.PrivacyPolicyTermsConditionsActivity;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.Objects;

public class SigninFragment extends Fragment {

    String selected_user;
    private RelativeLayout signinfrag_maincontainer;
    private LinearLayout signinfrag_contentcontainer;
    private TextInputLayout signinfrag_phonenum;
    private CheckBox signinfrag_termscheck;
    private ImageButton signinfrag_proceed_btn;
    private CountryCodePicker signinfrag_ccpicker;
    TextView signinfrag_tcondition_ppolicy_txt;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    Functions functions= new Functions();

    public SigninFragment(String selected_user) {
        this.selected_user = selected_user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_signin, container, false);

        signinfrag_maincontainer = v.findViewById(R.id.signinfrag_maincontainer);
        signinfrag_contentcontainer = v.findViewById(R.id.signinfrag_contentcontainer);
        signinfrag_phonenum = v.findViewById(R.id.signinfrag_phonenum);
        signinfrag_termscheck = v.findViewById(R.id.signinfrag_termscheck);
        signinfrag_proceed_btn = v.findViewById(R.id.signinfrag_proceed_btn);
        signinfrag_ccpicker = v.findViewById(R.id.signinfrag_ccpicker);
        signinfrag_tcondition_ppolicy_txt= v.findViewById(R.id.signinfrag_tcondition_ppolicy_txt);

        new Functions().putSharedPrefsValue(getContext(),"app_data","login_frag","int",1);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        signinfrag_proceed_btn.setEnabled(false);
        signinfrag_phonenum.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phonenum_txt = Objects.requireNonNull(signinfrag_phonenum.getEditText()).getText().toString();

                if (phonenum_txt.length() < 10) {
                    signinfrag_phonenum.setErrorEnabled(true);
                    signinfrag_phonenum.setError("Enter your phone number !!");
                    signinfrag_phonenum.requestFocus();
                    updateProceedBtnStyle("#DADADA", false);
                }
                else{
                    signinfrag_phonenum.setError(null);
                    signinfrag_phonenum.setErrorEnabled(false);
                }

                if (signinfrag_termscheck.isChecked() && phonenum_txt.length() == 10) {
                    updateProceedBtnStyle("#00A3FF", true);
                } else {
                    updateProceedBtnStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        signinfrag_termscheck.setOnCheckedChangeListener((compoundButton, b) -> {

            String phonenum_txt = Objects.requireNonNull(signinfrag_phonenum.getEditText()).getText().toString();
            if (!signinfrag_termscheck.isChecked()) {
                Snackbar.make(signinfrag_maincontainer, "Please read the terms and conditions and check to proceed !!", Snackbar.LENGTH_LONG).show();
                updateProceedBtnStyle("#DADADA", false);
            }

            if (phonenum_txt.length() == 10 && signinfrag_termscheck.isChecked())
                updateProceedBtnStyle("#00A3FF", true);
            else {
                updateProceedBtnStyle("#DADADA", false);
            }
        });

        signinfrag_proceed_btn.setOnClickListener(v1 -> {
            if(functions.checkInternetConnection(getActivity())){
                if (selected_user.equals("Employees")) {
                    checkPersonTable(false, true);
                } else if (selected_user.equals("Managers")) {
                    checkPersonTable(false, false);
                }
                else{
                    loadOtpPage(true);
                }
            }
            else{
                functions.no_internet_dialog(getActivity(), false);
            }
        });

        termsConditionsPrivacyPolicyLinkFunction();

        return v;
    }

    private void termsConditionsPrivacyPolicyLinkFunction() {
        SpannableString spannableString = new SpannableString(signinfrag_tcondition_ppolicy_txt.getText().toString());
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent= new Intent(getContext(), PrivacyPolicyTermsConditionsActivity.class);
                intent.putExtra("weblink","file:///android_asset/terms_and_conditions.html");
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        ClickableSpan clickableSpan2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent intent= new Intent(getContext(), PrivacyPolicyTermsConditionsActivity.class);
                intent.putExtra("weblink","https://www.freeprivacypolicy.com/live/c0bc722b-1e60-498c-8c82-338e90e8f19b");
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        };

        spannableString.setSpan(clickableSpan1, 11, 29, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan2, 34, 47, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        signinfrag_tcondition_ppolicy_txt.setText(spannableString);
        signinfrag_tcondition_ppolicy_txt.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // "[a-zA-Z]{3,20}"
    private void checkPersonTable(boolean isMain, boolean isEmployee) {

        String dbname= isEmployee ? "Employees" : "Managers";
        String contactnum_field= isEmployee ? "emp_contact" : "manager_contact";
        String contactnum= signinfrag_ccpicker.getSelectedCountryCodeWithPlus()+signinfrag_phonenum.getEditText().getText().toString();

        db.collection(dbname)
                .whereEqualTo(contactnum_field, contactnum)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().getDocuments().size() > 0)
                        loadOtpPage(isMain);
                    else{
                        if(!isMain)
                            checkPersonTable(true, isEmployee);
                        else
                            Snackbar.make(signinfrag_maincontainer,"Unable to find account !! Please try a valid number !!",Snackbar.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadOtpPage(boolean ismain) {
        String contactnum_withseperator= signinfrag_ccpicker.getSelectedCountryCodeWithPlus()+"-"+signinfrag_phonenum.getEditText().getText().toString();
        OtpPhoneFragment OtpPhoneFragment = new OtpPhoneFragment();
        Bundle args = new Bundle();
        args.putString("reg_phonenum", signinfrag_ccpicker.getSelectedCountryCodeWithPlus()+signinfrag_phonenum.getEditText().getText().toString());
        args.putString("reg_selecteduser", selected_user);
        args.putString("reg_phonecountrycode",contactnum_withseperator);
        args.putBoolean("reg_ismaintable", ismain);
        OtpPhoneFragment.setArguments(args);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.signin_maincontainer, OtpPhoneFragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void updateProceedBtnStyle(String s, boolean b) {
        signinfrag_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(s)));
        signinfrag_proceed_btn.setEnabled(b);
    }
}