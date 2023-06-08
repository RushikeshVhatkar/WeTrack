package com.rushikeshsantoshv.wetrack.Fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;
import com.rushikeshsantoshv.wetrack.R;

import java.util.Objects;

public class UpdatePhoneFragment extends Fragment {

    LinearLayout upfrag_mainlay;
    CountryCodePicker upfrag_ccpicker;
    TextInputLayout upfrag_phonenum;
    ImageButton upfrag_proceed_btn;

    public UpdatePhoneFragment() {  }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View v= inflater.inflate(R.layout.fragment_update_phone, container, false);

        upfrag_mainlay= v.findViewById(R.id.upfrag_mainlay);
        upfrag_ccpicker= v.findViewById(R.id.upfrag_ccpicker);
        upfrag_phonenum= v.findViewById(R.id.upfrag_phonenum);
        upfrag_proceed_btn= v.findViewById(R.id.upfrag_proceed_btn);

        upfrag_proceed_btn.setOnClickListener(v1->{
            UpdateOtpFragment updateOtpFragment = new UpdateOtpFragment();
            Bundle args = new Bundle();
            args.putString("update_phonenum", upfrag_phonenum.getEditText().getText().toString());
            args.putString("update_ccode",upfrag_ccpicker.getSelectedCountryCodeWithPlus());
            updateOtpFragment.setArguments(args);

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.upn_fragcontainer, updateOtpFragment);
            transaction.commit();
        });

        upfrag_phonenum.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String phonenum_txt = Objects.requireNonNull(upfrag_phonenum.getEditText()).getText().toString();
                if (phonenum_txt.length() == 10) {
                    upfrag_phonenum.setError(null);
                    upfrag_phonenum.setErrorEnabled(false);
                    updateProceedBtnStyle("#00A3FF", true);
                } else {
                    upfrag_phonenum.setErrorEnabled(true);
                    upfrag_phonenum.setError("Enter your phone number !!");
                    upfrag_phonenum.requestFocus();
                    updateProceedBtnStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return v;
    }

    private void updateProceedBtnStyle(String s, boolean b) {
        upfrag_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(s)));
        upfrag_proceed_btn.setEnabled(b);
    }
}