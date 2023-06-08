package com.rushikeshsantoshv.wetrack.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.Activities.CompanyDetailsActivity;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

public class UpdateOtpFragment extends Fragment {

    TextView uotpfrag_subheading_txt, uotpfrag_countdown_time, uotpfrag_resentuotpfrag_btn, uotpfrag_error_msg_txt;
    ProgressBar uotpfrag_progressbar;
    ImageButton uotpfrag_proceed_btn;
    OtpTextView uotpfrag_otpbox;
    RelativeLayout uotpfrag_mainlay;
    LinearLayout uotpfrag_resend_lay, uotpfrag_countdown_resenduotpfrag_lay;

    public FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser;
    public FirebaseFirestore db;
    public String mVerificationId;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    public boolean otpsend = false;
    Functions functions = new Functions();
    public String phonenum, ccode, comp_path;

    public long mTimeLeftInMillis = 30000;
    public final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    final String code = credential.getSmsCode();
                    if (code != null) {
                        verifyCode(code);
                    } else {
                        uotpfrag_progressbar.setVisibility(View.GONE);
                        uotpfrag_error_msg_txt.setVisibility(View.VISIBLE);
                        uotpfrag_countdown_resenduotpfrag_lay.setVisibility(View.GONE);
                        uotpfrag_resend_lay.setVisibility(View.VISIBLE);
                        Snackbar.make(uotpfrag_mainlay, "OTP not received !!", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    otpsend = false;
                    String error_msg = e.getLocalizedMessage() != null ? "Verification failed !! " + e.getLocalizedMessage() : "No Error...";
                    uotpfrag_error_msg_txt.setText(error_msg);
                    uotpfrag_progressbar.setVisibility(View.GONE);
                    uotpfrag_error_msg_txt.setVisibility(View.VISIBLE);
                    uotpfrag_resend_lay.setVisibility(View.VISIBLE);
                    uotpfrag_countdown_resenduotpfrag_lay.setVisibility(View.GONE);
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    otpsend = true;
                    super.onCodeSent(verificationId, token);
                    uotpfrag_resend_lay.setVisibility(View.GONE);
                    uotpfrag_progressbar.setVisibility(View.GONE);
                    uotpfrag_error_msg_txt.setVisibility(View.GONE);
                    uotpfrag_countdown_resenduotpfrag_lay.setVisibility(View.VISIBLE);
                    otpResendDelayCountDown();
                    Snackbar.make(uotpfrag_mainlay, "An OTP has been sent to your Mobile Number.", Snackbar.LENGTH_LONG).show();
                    mVerificationId = verificationId;
                    resendToken = token;

                }
            };

    public UpdateOtpFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_update_otp, container, false);

        updateOtpFragInits(v);
        uotpfrag_otpbox.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                uotpfrag_proceed_btn.setEnabled(false);
                uotpfrag_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
            }

            @Override
            public void onOTPComplete(String otp) {
                if (otpsend) {
                    uotpfrag_proceed_btn.setEnabled(true);
                    uotpfrag_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.my_green)));
                } else {
                    uotpfrag_proceed_btn.setEnabled(false);
                    uotpfrag_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.my_green)));
                }
            }
        });
        uotpfrag_resentuotpfrag_btn.setOnClickListener(v1 -> resendVerificationCode(phonenum, resendToken));
        uotpfrag_proceed_btn.setOnClickListener(v1 -> verifyCode(uotpfrag_otpbox.getOTP()));

        return v;
    }

    private void updateOtpFragInits(@NonNull View v) {
        uotpfrag_resend_lay = v.findViewById(R.id.uotpfrag_resend_lay);
        uotpfrag_countdown_resenduotpfrag_lay = v.findViewById(R.id.uotpfrag_countdown_resenduotpfrag_lay);
        uotpfrag_subheading_txt = v.findViewById(R.id.uotpfrag_subheading_txt);
        uotpfrag_countdown_time = v.findViewById(R.id.uotpfrag_countdown_time);
        uotpfrag_resentuotpfrag_btn = v.findViewById(R.id.uotpfrag_resentuotpfrag_btn);
        uotpfrag_progressbar = v.findViewById(R.id.uotpfrag_progressbar);
        uotpfrag_proceed_btn = v.findViewById(R.id.uotpfrag_proceed_btn);
        uotpfrag_otpbox = v.findViewById(R.id.uotpfrag_otpbox);
        uotpfrag_mainlay = v.findViewById(R.id.uotpfrag_mainlay);
        uotpfrag_error_msg_txt = v.findViewById(R.id.uotpfrag_error_msg_txt);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        comp_path = functions.getSharedPrefsValue(getContext(), "user_data", "company_path", "string", null);

        if (getArguments() != null) {
            phonenum = getArguments().getString("update_phonenum");
            ccode = getArguments().getString("update_ccode");
            uotpfrag_subheading_txt.setText(Html.fromHtml("Please enter the veritication code send to <b>" + ccode + "-" + phonenum + "</b>"));
            if (phonenum == null) {
                returnBackSigninFrag();
            }
        } else {
            returnBackSigninFrag();
        }

        updateCountDownText();
        otpSendFunction();
    }

    @SuppressLint("SetTextI18n")
    private void verifyCode(String code) {
        if(functions.checkInternetConnection(getActivity())){
            Dialog loading_dialog = functions.createDialogBox(getActivity(), R.layout.loading_dialog, false);
            loading_dialog.show();
            uotpfrag_otpbox.setOTP(code);
            uotpfrag_proceed_btn.setClickable(false);
            uotpfrag_proceed_btn.setEnabled(false);
            uotpfrag_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.my_green)));
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            firebaseUser.updatePhoneNumber(credential)
                    .addOnCompleteListener(task -> {
                        String full_phonenum = ccode + "-" + phonenum;
                        if (task.isSuccessful()) {
                            Map<String, Object> owner_data = new HashMap<>();
                            owner_data.put("contact_number", full_phonenum);
                            db.collection("Owners")
                                    .whereEqualTo("company_path", db.document(comp_path))
                                    .get()
                                    .addOnCompleteListener(task12 -> {
                                        if(task12.isSuccessful()){
                                            if(task12.getResult().getDocuments().size() > 0){
                                                DocumentReference doc= task12.getResult().getDocuments().get(0).getReference();
                                                db.collection("Owners")
                                                        .document(doc.getId())
                                                        .update(owner_data)
                                                        .addOnCompleteListener(task1 -> {
                                                            if (task1.isSuccessful()) {
                                                                functions.putSharedPrefsValue(getContext(), "user_data", "contact_num", "string", full_phonenum);
                                                                loading_dialog.dismiss();
                                                                Snackbar.make(uotpfrag_mainlay, "New Phone Number Updated Successfully...", Snackbar.LENGTH_LONG).show();
                                                                new Handler().postDelayed(() -> {
                                                                    getActivity().startActivity(new Intent(getContext(), CompanyDetailsActivity.class));
                                                                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                                    getActivity().finish();
                                                                }, 2000);
                                                            } else {
                                                                loading_dialog.cancel();
                                                                Snackbar.make(uotpfrag_mainlay, "Phone number updated but some issue occurred !! Please logout and login to account", Snackbar.LENGTH_LONG).show();
                                                                new Handler().postDelayed(() -> {
                                                                    getActivity().startActivity(new Intent(getContext(), CompanyDetailsActivity.class));
                                                                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                                    getActivity().finish();
                                                                }, 2000);
                                                            }
                                                        });
                                            }
                                            else{
                                                loading_dialog.cancel();
                                                Snackbar.make(uotpfrag_mainlay, "Phone number updated but some issue occurred !! Please logout and login to account", Snackbar.LENGTH_LONG).show();
                                                new Handler().postDelayed(() -> {
                                                    getActivity().startActivity(new Intent(getContext(), CompanyDetailsActivity.class));
                                                    getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                    getActivity().finish();
                                                }, 2000);
                                            }
                                        }
                                        else{
                                            loading_dialog.cancel();
                                            Snackbar.make(uotpfrag_mainlay, "Phone number updated but some issue occurred !! Please logout and login to account", Snackbar.LENGTH_LONG).show();
                                            new Handler().postDelayed(() -> {
                                                getActivity().startActivity(new Intent(getContext(), CompanyDetailsActivity.class));
                                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                                getActivity().finish();
                                            }, 2000);
                                        }
                                    });
                        } else {
                            loading_dialog.cancel();
                            Snackbar.make(uotpfrag_mainlay, "Unable to update your phone number !! Please try again", Snackbar.LENGTH_LONG).show();
                            new Handler().postDelayed(() -> {
                                getActivity().startActivity(new Intent(getContext(), CompanyDetailsActivity.class));
                                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                getActivity().finish();
                            }, 2000);
                        }
                    });
        }
        else functions.no_internet_dialog(getActivity(), false);
    }

    private void otpResendDelayCountDown() {
        new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                uotpfrag_resend_lay.setVisibility(View.VISIBLE);
                uotpfrag_progressbar.setVisibility(View.GONE);
                uotpfrag_countdown_resenduotpfrag_lay.setVisibility(View.GONE);
                mTimeLeftInMillis = 30000;
            }
        }.start();
    }

    private void returnBackSigninFrag() {
        Snackbar.make(uotpfrag_mainlay, "Unable to send OTP, Please try again !!", Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(() -> getActivity().getSupportFragmentManager().popBackStack(), 2000);
    }

    private void otpSendFunction() {
        if(functions.checkInternetConnection(getActivity())){
            uotpfrag_resend_lay.setVisibility(View.GONE);
            uotpfrag_countdown_resenduotpfrag_lay.setVisibility(View.GONE);
            uotpfrag_progressbar.setVisibility(View.VISIBLE);

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(ccode + phonenum)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(getActivity())
                    .setCallbacks(mCallbacks)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        }
        else functions.no_internet_dialog(getActivity(), false);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        if(functions.checkInternetConnection(getActivity())){
            uotpfrag_resend_lay.setVisibility(View.GONE);
            uotpfrag_progressbar.setVisibility(View.VISIBLE);
            uotpfrag_error_msg_txt.setVisibility(View.GONE);
            uotpfrag_countdown_resenduotpfrag_lay.setVisibility(View.GONE);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, getActivity(), mCallbacks, token);
        }
        else functions.no_internet_dialog(getActivity(), false);
    }

    @SuppressLint("SetTextI18n")
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        String sec_txt = (seconds < 2) ? " sec" : " secs";
        uotpfrag_countdown_time.setText(timeLeftFormatted + sec_txt);
    }
}