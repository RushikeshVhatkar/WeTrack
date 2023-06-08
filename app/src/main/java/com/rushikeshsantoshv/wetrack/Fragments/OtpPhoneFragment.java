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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.rushikeshsantoshv.wetrack.Activities.HomeActivity;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;
import in.aabhasjindal.otptextview.OtpTextView;

@SuppressWarnings("all")
public class OtpPhoneFragment extends Fragment {

    public String phonenum, selecteduser;
    boolean reg_ismaintable;
    public RelativeLayout otp_main_container;
    public LinearLayout otp_resend_lay, otp_countdown_resendotp_lay;
    public TextView otp_resentotp_btn, otp_error_msg_txt;
    public ImageButton otp_proceed_btn, reg_passfrag_back_btn;
    public OtpTextView otp_otpbox;
    public ProgressBar otp_progressbar;
    public TextView otp_subheading_txt, otp_countdown_time;

    public FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser;
    public FirebaseFirestore db;
    public String mVerificationId;
    public PhoneAuthProvider.ForceResendingToken resendToken;
    public boolean otpsend = false;
    Functions functions = new Functions();

    public String reg_phonecountrycode;

    public long mTimeLeftInMillis = 30000;
    public final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                @Override
                public void onVerificationCompleted(PhoneAuthCredential credential) {
                    final String code = credential.getSmsCode();
                    if (code != null) {
                        verifyCode(code);
                    } else {
                        otp_progressbar.setVisibility(View.GONE);
                        otp_error_msg_txt.setVisibility(View.VISIBLE);
                        otp_countdown_resendotp_lay.setVisibility(View.GONE);
                        otp_resend_lay.setVisibility(View.VISIBLE);
                        Snackbar.make(otp_main_container, "OTP not received !!", Snackbar.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    otpsend = false;
                    String error_msg = e != null ? (e.getLocalizedMessage() != null ? "Verification failed !! " + e.getLocalizedMessage() : "No Error...") : "No Error...";
                    otp_error_msg_txt.setText(error_msg);
                    otp_progressbar.setVisibility(View.GONE);
                    otp_error_msg_txt.setVisibility(View.VISIBLE);
                    otp_resend_lay.setVisibility(View.VISIBLE);
                    otp_countdown_resendotp_lay.setVisibility(View.GONE);
                }

                @Override
                public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                    otpsend = true;
                    super.onCodeSent(verificationId, token);
                    otp_resend_lay.setVisibility(View.GONE);
                    otp_progressbar.setVisibility(View.GONE);
                    otp_error_msg_txt.setVisibility(View.GONE);
                    otp_countdown_resendotp_lay.setVisibility(View.VISIBLE);
                    otpResendDelayCountDown();
                    Snackbar.make(otp_main_container, "An OTP has been sent to your Mobile Number.", Snackbar.LENGTH_LONG).show();
                    mVerificationId = verificationId;
                    resendToken = token;

                }
            };

    public OtpPhoneFragment() {
    }


    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_otp_phone, container, false);

        otpPhoneFragInits(v);

        functions.putSharedPrefsValue(getContext(), "app_data", "login_frag", "int", 2);

        updateCountDownText();

        if (getArguments() != null) {
            phonenum = getArguments().getString("reg_phonenum");
            selecteduser = getArguments().getString("reg_selecteduser");
            reg_ismaintable = getArguments().getBoolean("reg_ismaintable");
            reg_phonecountrycode = getArguments().getString("reg_phonecountrycode");
            otp_subheading_txt.setText(Html.fromHtml("Please enter the veritication code send to <b>" + reg_phonecountrycode + "</b>"));
            if (phonenum == null) {
                returnBackSigninFrag();
            }
        } else {
            returnBackSigninFrag();
        }

        otpSendFunction();

        reg_passfrag_back_btn.setOnClickListener(v1 -> {
            getActivity().getSupportFragmentManager().popBackStack();
            getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        otp_otpbox.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                otp_proceed_btn.setEnabled(false);
                otp_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
            }

            @Override
            public void onOTPComplete(String otp) {
                if (otpsend) {
                    otp_proceed_btn.setEnabled(true);
                    otp_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00A3FF")));
                } else {
                    otp_proceed_btn.setEnabled(false);
                    otp_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
                }
            }
        });

        otp_resentotp_btn.setOnClickListener(v1 -> {
            if (functions.checkInternetConnection(getActivity()))
                resendVerificationCode(phonenum, resendToken);
            else functions.no_internet_dialog(getActivity(), false);
        });

        otp_proceed_btn.setOnClickListener(v1 -> {
            if (functions.checkInternetConnection(getActivity())) verifyCode(otp_otpbox.getOTP());
            else functions.no_internet_dialog(getActivity(), false);
        });

        return v;
    }

    private void otpPhoneFragInits(@NonNull View v) {
        otp_main_container = v.findViewById(R.id.otp_main_container);
        otp_resend_lay = v.findViewById(R.id.otp_resend_lay);
        otp_countdown_resendotp_lay = v.findViewById(R.id.otp_countdown_resendotp_lay);
        otp_countdown_time = v.findViewById(R.id.otp_countdown_time);
        otp_otpbox = v.findViewById(R.id.otp_otpbox);
        otp_progressbar = v.findViewById(R.id.otp_progressbar);
        otp_resentotp_btn = v.findViewById(R.id.otp_resentotp_btn);
        reg_passfrag_back_btn = v.findViewById(R.id.reg_passfrag_back_btn);
        otp_proceed_btn = v.findViewById(R.id.otp_proceed_btn);
        otp_subheading_txt = v.findViewById(R.id.otp_subheading_txt);
        otp_error_msg_txt = v.findViewById(R.id.otp_error_msg_txt);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    private void otpSendFunction() {
        if (functions.checkInternetConnection(getActivity())) {
            otp_resend_lay.setVisibility(View.GONE);
            otp_countdown_resendotp_lay.setVisibility(View.GONE);
            otp_progressbar.setVisibility(View.VISIBLE);

            PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                    .setPhoneNumber(phonenum)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(getActivity())
                    .setCallbacks(mCallbacks)
                    .build();
            PhoneAuthProvider.verifyPhoneNumber(options);
        } else functions.no_internet_dialog(getActivity(), true);
    }

    private void resendVerificationCode(String phoneNumber, PhoneAuthProvider.ForceResendingToken token) {
        if (functions.checkInternetConnection(getActivity())) {
            otp_resend_lay.setVisibility(View.GONE);
            otp_progressbar.setVisibility(View.VISIBLE);
            otp_error_msg_txt.setVisibility(View.GONE);
            otp_countdown_resendotp_lay.setVisibility(View.GONE);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber, 60, TimeUnit.SECONDS, getActivity(), mCallbacks, token);
        } else functions.no_internet_dialog(getActivity(), false);
    }

    @SuppressLint("SetTextI18n")
    private void verifyCode(String code) {
        if (functions.checkInternetConnection(getActivity())) {
            otp_otpbox.setOTP(code);
            otp_proceed_btn.setEnabled(false);
            otp_proceed_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
            FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
            firebaseAuth.signInWithCredential(credential)
                    .addOnCompleteListener(task1 -> {
                        otp_progressbar.setVisibility(View.GONE);
                        otp_error_msg_txt.setVisibility(View.GONE);
                        if (task1.isSuccessful()) {
                            Snackbar.make(otp_main_container, "OTP Verification successfully complete.", Snackbar.LENGTH_LONG).show();
                            firebaseUser = task1.getResult().getUser();

                            Dialog processUserAccDialog = functions.createDialogBox(getActivity(), R.layout.loading_dialog, false);
                            ImageView loading_verified_img = processUserAccDialog.findViewById(R.id.loading_verified_img);
                            ProgressBar loading_prog = processUserAccDialog.findViewById(R.id.loading_progbar);
                            TextView loading_txt = processUserAccDialog.findViewById(R.id.loading_txt);
                            processUserAccDialog.show();

                            new Handler().postDelayed(() -> {
                                Log.e("selecteduser", "The usertype is " + selecteduser);
                                if (selecteduser.equals("Owners")) {
                                    if (task1.getResult().getAdditionalUserInfo().isNewUser()) {
                                        addUserData();
                                        proceedForAddingDetails("Account Created Successfully.", processUserAccDialog, loading_prog, loading_verified_img, loading_txt);
                                    } else {
                                        processUserDataStatus(processUserAccDialog, loading_prog, loading_verified_img, loading_txt);
                                    }
                                } else {
                                    boolean isEmployee = (selecteduser.equals("Employees")) ? true : false;
                                    userVerifyDocument(isEmployee, processUserAccDialog, loading_prog, loading_verified_img, loading_txt);
                                }
                            }, 1000);
                        } else {
                            String error_msg = task1 != null && task1.getException() != null && task1.getException().getLocalizedMessage() != null ? task1.getException().getLocalizedMessage() : "No Error...";
                            otp_error_msg_txt.setText(error_msg);
                            otp_error_msg_txt.setVisibility(View.VISIBLE);
                            otp_resend_lay.setVisibility(View.VISIBLE);
                            otp_progressbar.setVisibility(View.GONE);
                            otp_countdown_resendotp_lay.setVisibility(View.GONE);
                            Snackbar.make(otp_main_container, "Verification failed !!", Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        String error_msg = e != null && e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error...";
                        otp_error_msg_txt.setText(error_msg);
                        otp_error_msg_txt.setVisibility(View.VISIBLE);
                        otp_resend_lay.setVisibility(View.VISIBLE);
                        otp_progressbar.setVisibility(View.GONE);
                        otp_countdown_resendotp_lay.setVisibility(View.GONE);
                    });
        } else functions.no_internet_dialog(getActivity(), false);
    }

    private void userVerifyDocument(boolean isEmployee, Dialog processUserAccDialog, ProgressBar loading_prog, ImageView loading_verified_img, TextView loading_txt) {
        if (functions.checkInternetConnection(getActivity())) {
            db.collection("UserLogin")
                    .whereEqualTo("user_phonenum", phonenum)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task2) {
                            if (task2.isSuccessful() && task2.getResult().getDocuments().size() > 0) {
                                Log.e("user_error", "In IF Condition");
                                userLoginToAcount(task2.getResult().getDocuments().get(0).getId(), isEmployee, processUserAccDialog, loading_prog, loading_verified_img, loading_txt);
                            } else {
                                String puser = isEmployee ? "emp_contact" : "manager_contact";
                                db.collection(selecteduser)
                                        .whereEqualTo(puser, phonenum)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task3) {
                                                if (task3.isSuccessful() && task3.getResult().getDocuments().size() > 0) {
                                                    DocumentSnapshot doc = task3.getResult().getDocuments().get(0);
                                                    Map<String, Object> userlogin_data = new HashMap<>();
                                                    userlogin_data.put("user_type", selecteduser);
                                                    userlogin_data.put("user_data_reference", doc.getReference());
                                                    userlogin_data.put("user_phonenum", phonenum);

                                                    db.collection("UserLogin")
                                                            .add(userlogin_data)
                                                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<DocumentReference> task4) {
                                                                    if (task4.isSuccessful()) {
                                                                        userLoginToAcount(task4.getResult().getId(), isEmployee, processUserAccDialog, loading_prog, loading_verified_img, loading_txt);
                                                                    } else {
                                                                        Snackbar.make(otp_main_container, "Some error occured !! Please try again...", Snackbar.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                } else {
                                                    String error = task3 != null && task3.getException() != null && task3.getException().getLocalizedMessage() != null ? task3.getException().getLocalizedMessage() : "No Error";
                                                    Toast.makeText(getContext(), "Some error occured : " + error, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        }
                    });
        } else functions.no_internet_dialog(getActivity(), false);
    }

    private void userLoginToAcount(String id, boolean isEmployee, Dialog processUserAccDialog, ProgressBar loading_prog, ImageView loading_verified_img, TextView loading_txt) {
        if (functions.checkInternetConnection(getActivity())) {
            db.collection("UserLogin")
                    .document(id)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                String pstatus_field = isEmployee ? "emp_status" : "manager_status";
                                String pname_field = isEmployee ? "emp_name" : "manager_name";
                                String pcontact_field = isEmployee ? "emp_contact" : "manager_contact";
                                String pemp_timestamp = isEmployee ? "emp_timestamp" : "manager_timestamp";
                                String dbname = isEmployee ? "Employees" : "Managers";

                                Log.e("user_id_error", "The user ID is : " + id);
                                task.getResult()
                                        .getDocumentReference("user_data_reference")
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task5) {
                                                if (task5.isSuccessful()) {
                                                    loading_prog.setVisibility(View.VISIBLE);
                                                    loading_verified_img.setVisibility(View.GONE);
                                                    loading_txt.setText("Login Successful.\nRedirecting to Home Page...");

                                                    DocumentSnapshot userdata = task5.getResult();
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "login_status", "boolean", true);
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "ptype", "string", selecteduser);
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "status", "boolean", userdata.getBoolean(pstatus_field));
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "user_reference", "string", userdata.getReference().getPath());
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "full_name", "string", userdata.getString(pname_field));
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "contact_num", "string", userdata.getString(pcontact_field));
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "company_path", "string", userdata.getDocumentReference("company_path").getPath());
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "timestamp", "string", userdata.getTimestamp(pemp_timestamp).toDate().toString());

                                                    functions.saveCompanyDetailsToPreferences(getContext(), db, userdata.getDocumentReference("company_path").getPath());

                                                    new Handler().postDelayed(() -> {
                                                        processUserAccDialog.dismiss();
                                                        startActivity(new Intent(getContext(), HomeActivity.class));
                                                        getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                                    }, 1000);
                                                } else {
                                                    String error = task5 != null && task5.getException() != null && task5.getException().getLocalizedMessage() != null ? task5.getException().getLocalizedMessage() : "No Error....";
                                                    Toast.makeText(getContext(), "Some Error Occured : " + error, Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            } else {
                                String error = task != null && task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error...";
                                Snackbar.make(otp_main_container, "Some Error Occured : " + error, Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
        } else functions.no_internet_dialog(getActivity(), false);
    }

    private void processUserDataStatus(Dialog processUserAccDialog, ProgressBar loading_prog, ImageView loading_verified_img, TextView loading_txt) {

        if (functions.checkInternetConnection(getActivity())) {
            db.collection(selecteduser)
                    .document(firebaseUser.getUid())
                    .get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            boolean isDetailsAdded = (task1.getResult().contains("isDetailsAdded") ? task1.getResult().getBoolean("isDetailsAdded") : false);
                            if (isDetailsAdded) {
                                loading_prog.setVisibility(View.VISIBLE);
                                loading_verified_img.setVisibility(View.GONE);
                                loading_txt.setText("Login Successful.\nRedirecting to Home Page...");

                                DocumentSnapshot userdata = task1.getResult();
                                functions.putSharedPrefsValue(getContext(), "user_data", "login_status", "boolean", true);
                                functions.putSharedPrefsValue(getContext(), "user_data", "ptype", "string", selecteduser);
                                functions.putSharedPrefsValue(getContext(), "user_data", "status", "boolean", userdata.getBoolean("isDetailsAdded"));
                                functions.putSharedPrefsValue(getContext(), "user_data", "full_name", "string", userdata.getString("full_name"));
                                functions.putSharedPrefsValue(getContext(), "user_data", "user_reference", "string", db.document("Owners/" + firebaseUser.getUid()).getPath());
                                functions.putSharedPrefsValue(getContext(), "user_data", "contact_num", "string", userdata.getString("contact_number"));
                                functions.putSharedPrefsValue(getContext(), "user_data", "company_path", "string", userdata.getDocumentReference("company_path").getPath());
                                functions.putSharedPrefsValue(getContext(), "user_data", "email_id", "string", userdata.getString("email_id"));

                                functions.saveCompanyDetailsToPreferences(getContext(), db, userdata.getDocumentReference("company_path").getPath());

                                new Handler().postDelayed(() -> {
                                    processUserAccDialog.dismiss();
                                    functions.putSharedPrefsValue(getContext(), "user_data", "login_status", "boolean", true);
                                    startActivity(new Intent(getContext(), HomeActivity.class));
                                    getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                                }, 1000);
                            } else {
                                proceedForAddingDetails("Login Successful.\nPlease add personal and other details before proceeding...", processUserAccDialog, loading_prog, loading_verified_img, loading_txt);
                            }
                        } else {
                            processUserDataStatus(processUserAccDialog, loading_prog, loading_verified_img, loading_txt);
                        }
                    });
        } else functions.no_internet_dialog(getActivity(), false);
    }

    private void proceedForAddingDetails(String loading_string, Dialog processUserAccDialog, @NonNull ProgressBar loading_prog, @NonNull ImageView loading_verified_img, @NonNull TextView loading_txt) {
        loading_prog.setVisibility(View.GONE);
        loading_verified_img.setVisibility(View.VISIBLE);
        loading_txt.setText(loading_string);
        AdditionalDetailsFragment additionalDetailsFragment = new AdditionalDetailsFragment();
        Bundle args = new Bundle();
        args.putString("reg_phonenum", phonenum);
        args.putString("reg_selecteduser", selecteduser);
        args.putString("reg_phonecountrycode", reg_phonecountrycode);
        additionalDetailsFragment.setArguments(args);

        new Handler().postDelayed(() -> {
            processUserAccDialog.dismiss();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.replace(R.id.signin_maincontainer, additionalDetailsFragment);
//            transaction.addToBackStack(null);
            transaction.commit();
        }, 1000);
    }

    private void addUserData() {
        Map<String, Object> userdata = new HashMap<>();
        userdata.put("owner_id", firebaseUser.getUid());
        userdata.put("isDetailsAdded", false);

        db.collection(selecteduser)
                .document(firebaseUser.getUid())
                .set(userdata)
                .addOnFailureListener(e -> {
                    addUserData();
                });
    }

    private void returnBackSigninFrag() {
        Snackbar.make(otp_main_container, "Unable to send OTP, Please try again !!", Snackbar.LENGTH_LONG).show();
        new Handler().postDelayed(() -> getActivity().getSupportFragmentManager().popBackStack(), 1000);
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
                otp_resend_lay.setVisibility(View.VISIBLE);
                otp_progressbar.setVisibility(View.GONE);
                otp_countdown_resendotp_lay.setVisibility(View.GONE);
                mTimeLeftInMillis = 30000;
            }
        }.start();
    }

    @SuppressLint("SetTextI18n")
    private void updateCountDownText() {
        int minutes = (int) (mTimeLeftInMillis / 1000) / 60;
        int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

        String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        String sec_txt = (seconds < 2) ? " sec" : " secs";
        otp_countdown_time.setText(timeLeftFormatted + sec_txt);
    }

}