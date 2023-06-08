package com.rushikeshsantoshv.wetrack.Fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.Activities.HomeActivity;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.HashMap;
import java.util.Map;

public class AdditionalDetailsFragment extends Fragment {

    View adddetailsfrag_mainlay;
    TextInputLayout adddetailsfrag_pfullname, adddetailsfrag_pemailid;
    TextInputLayout adddetailsfrag_ccompanyname, adddetailsfrag_clocation, adddetailsfrag_ccontactnum;
    Button adddetailsfrag_nextbtn;

    private final String email_regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    private String phonenum, selecteduser, reg_phonecountrycode;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;
    Functions functions= new Functions();

    public AdditionalDetailsFragment() {
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_additional_details, container, false);

        viewInits(v);
        functions.putSharedPrefsValue(getContext(), "app_data", "login_frag", "int", 3);
        
        if (getArguments() != null) {
            phonenum = getArguments().getString("reg_phonenum");
            selecteduser = getArguments().getString("reg_selecteduser");
            reg_phonecountrycode= getArguments().getString("reg_phonecountrycode");
            if (phonenum == null || selecteduser == null) {
                returnBackSigninFrag();
            }
            else{
                String phone= reg_phonecountrycode.substring(reg_phonecountrycode.indexOf("-")+1);
                Log.e("reg_phonecountrycode","The contact number is : "+phone);
                adddetailsfrag_ccontactnum.getEditText().setText(phone);
            }
        } else {
            returnBackSigninFrag();
        }

        adddetailsfrag_nextbtn.setOnClickListener(v1 -> {

            if(functions.checkInternetConnection(getActivity())){
                Dialog success_msgdialog = functions.createDialogBox(getActivity(), R.layout.loading_dialog, false);
                ImageView loading_verified_img = success_msgdialog.findViewById(R.id.loading_verified_img);
                ProgressBar loading_prog = success_msgdialog.findViewById(R.id.loading_progbar);
                TextView loading_txt = success_msgdialog.findViewById(R.id.loading_txt);
                loading_prog.setVisibility(View.VISIBLE);
                loading_verified_img.setVisibility(View.GONE);
                loading_txt.setText("Please wait...");
                success_msgdialog.show();

                Map<String, Object> company_details = new HashMap<>();
                Timestamp reg_time = Timestamp.now();
                company_details.put("company_name", adddetailsfrag_ccompanyname.getEditText().getText().toString());
                company_details.put("company_location", adddetailsfrag_clocation.getEditText().getText().toString());
                company_details.put("company_contactnum", adddetailsfrag_ccontactnum.getEditText().getText().toString());
                company_details.put("company_created", reg_time);
                if ("Owners".equals(selecteduser)) {
                    if(functions.checkInternetConnection(getActivity())){
                        db.collection("Companies")
                                .add(company_details)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {

                                        DocumentReference company_path = db.document(task.getResult().getPath());
                                        Map<String, Object> userdata = new HashMap<>();
                                        userdata.put("full_name", adddetailsfrag_pfullname.getEditText().getText().toString());
                                        userdata.put("contact_number", reg_phonecountrycode);
                                        userdata.put("reg_timestamp", reg_time);
                                        userdata.put("email_id", adddetailsfrag_pemailid.getEditText().getText().toString());
                                        userdata.put("company_path", company_path);
                                        userdata.put("isDetailsAdded", true);

                                        db.collection("Owners")
                                                .document(firebaseUser.getUid())
                                                .set(userdata)
                                                .addOnSuccessListener(unused -> {

                                                    functions.putSharedPrefsValue(getContext(), "user_data", "ptype", "string", selecteduser);
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "login_status", "boolean", true);
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "full_name", "string", adddetailsfrag_pfullname.getEditText().getText().toString());
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "contact_num", "string", phonenum);
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "email_id", "string", adddetailsfrag_pemailid.getEditText().getText().toString());
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "company_path", "string", company_path.getPath());
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "company_created", "string", reg_time.toDate().toString());
                                                    functions.putSharedPrefsValue(getContext(), "user_data", "isDetailsAdded", "boolean", true);
                                                    functions.saveCompanyDetailsToPreferences(getContext(), db, company_path.getPath());
                                                    loading_txt.setText("Data Entered Successfully. Going to home page.");

                                                    new Handler().postDelayed(() -> {
                                                        success_msgdialog.dismiss();
                                                        startActivity(new Intent(getContext(), HomeActivity.class));
                                                        getActivity().finish();
                                                    }, 2000);
                                                })
                                                .addOnFailureListener(e -> {
                                                    success_msgdialog.cancel();
                                                    String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error !!";
                                                    Snackbar.make(adddetailsfrag_mainlay, "Data Entry failed 02 !! Please try later.\nError : " + msg, Snackbar.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        success_msgdialog.cancel();
                                        String msg = task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error !!";
                                        Snackbar.make(adddetailsfrag_mainlay, "Data Entry failed 03 !! Please try later.\nError : " + msg, Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                    }
                    else functions.no_internet_dialog(getActivity(), false);
                }
            }
            else functions.no_internet_dialog(getActivity(), false);
        });

        adddetailsfrag_pfullname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fullname_txt = adddetailsfrag_pfullname.getEditText().getText().toString();
                String email_txt = adddetailsfrag_pemailid.getEditText().getText().toString();
                String companyname_txt = adddetailsfrag_ccompanyname.getEditText().getText().toString();
                String clocation_txt = adddetailsfrag_clocation.getEditText().getText().toString();
                String ccontactnum = adddetailsfrag_ccontactnum.getEditText().getText().toString();

                if (fullname_txt.length() < 3) {
                    adddetailsfrag_pfullname.setError("Enter more than 4 characters !!");
                    adddetailsfrag_pfullname.requestFocus();
                    updateNextButtonStyle("#DADADA", false);
                } else
                    adddetailsfrag_pfullname.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5 &&
                        clocation_txt.length() >= 3 && ccontactnum.length() >= 10) {
                    updateNextButtonStyle("#00A3FF", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        adddetailsfrag_pemailid.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String fullname_txt = adddetailsfrag_pfullname.getEditText().getText().toString();
                String email_txt = adddetailsfrag_pemailid.getEditText().getText().toString();

                String companyname_txt = adddetailsfrag_ccompanyname.getEditText().getText().toString();
                String clocation_txt = adddetailsfrag_clocation.getEditText().getText().toString();
                String ccontactnum = adddetailsfrag_ccontactnum.getEditText().getText().toString();

                if (email_txt.length() > 0 && !email_txt.matches(email_regex)) {
                    adddetailsfrag_pemailid.setError("Enter more than 4 characters !!");
                    adddetailsfrag_pemailid.requestFocus();
                    updateNextButtonStyle("#DADADA", false);
                } else
                    adddetailsfrag_pemailid.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5 &&
                        clocation_txt.length() >= 3 && ccontactnum.length() >= 10) {
                    updateNextButtonStyle("#00A3FF", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        adddetailsfrag_ccompanyname.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String fullname_txt = adddetailsfrag_pfullname.getEditText().getText().toString();
                String email_txt = adddetailsfrag_pemailid.getEditText().getText().toString();

                String companyname_txt = adddetailsfrag_ccompanyname.getEditText().getText().toString();
                String clocation_txt = adddetailsfrag_clocation.getEditText().getText().toString();
                String ccontactnum = adddetailsfrag_ccontactnum.getEditText().getText().toString();

                if (companyname_txt.length() < 5) {
                    adddetailsfrag_ccompanyname.setError("Enter more than 4 characters !!");
                    adddetailsfrag_ccompanyname.requestFocus();
                    updateNextButtonStyle("#DADADA", false);
                } else
                    adddetailsfrag_ccompanyname.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5
                        && clocation_txt.length() >= 3 && ccontactnum.length() >= 10) {
                    updateNextButtonStyle("#00A3FF", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        adddetailsfrag_clocation.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fullname_txt = adddetailsfrag_pfullname.getEditText().getText().toString();
                String email_txt = adddetailsfrag_pemailid.getEditText().getText().toString();

                String companyname_txt = adddetailsfrag_ccompanyname.getEditText().getText().toString();
                String clocation_txt = adddetailsfrag_clocation.getEditText().getText().toString();
                String ccontactnum = adddetailsfrag_ccontactnum.getEditText().getText().toString();

                if (clocation_txt.length() < 3) {
                    adddetailsfrag_clocation.setError("Enter more than 2 characters !!");
                    adddetailsfrag_clocation.requestFocus();
                    updateNextButtonStyle("#DADADA", false);

                } else
                    adddetailsfrag_clocation.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5
                        && clocation_txt.length() >= 3 && ccontactnum.length() >= 10) {
                    updateNextButtonStyle("#00A3FF", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        adddetailsfrag_ccontactnum.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fullname_txt = adddetailsfrag_pfullname.getEditText().getText().toString();
                String email_txt = adddetailsfrag_pemailid.getEditText().getText().toString();

                String companyname_txt = adddetailsfrag_ccompanyname.getEditText().getText().toString();
                String clocation_txt = adddetailsfrag_clocation.getEditText().getText().toString();
                String ccontactnum = adddetailsfrag_ccontactnum.getEditText().getText().toString();

                if (ccontactnum.length() < 10) {
                    adddetailsfrag_ccontactnum.setError("Enter valid contact number !!");
                    adddetailsfrag_ccontactnum.requestFocus();
                    updateNextButtonStyle("#DADADA", false);
                } else
                    adddetailsfrag_ccontactnum.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5
                        && ccontactnum.length() == 10 && clocation_txt.length() >= 3) {
                    updateNextButtonStyle("#00A3FF", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return v;
    }

    private void viewInits(@NonNull View v) {
        adddetailsfrag_mainlay = v.findViewById(R.id.adddetailsfrag_mainlay);
        adddetailsfrag_pfullname = v.findViewById(R.id.adddetailsfrag_pfullname);
        adddetailsfrag_pemailid = v.findViewById(R.id.adddetailsfrag_pemailid);
        adddetailsfrag_ccompanyname = v.findViewById(R.id.adddetailsfrag_ccompanyname);
        adddetailsfrag_clocation = v.findViewById(R.id.adddetailsfrag_clocation);
        adddetailsfrag_ccontactnum = v.findViewById(R.id.adddetailsfrag_ccontactnum);
        adddetailsfrag_nextbtn = v.findViewById(R.id.adddetailsfrag_nextbtn);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    public void updateNextButtonStyle(String s, boolean b) {
        adddetailsfrag_nextbtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(s)));
        adddetailsfrag_nextbtn.setEnabled(b);
    }

    public void returnBackSigninFrag() {
        Snackbar.make(adddetailsfrag_mainlay, "Unable to load the server, Please try again !!", Snackbar.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> getActivity().finish(), 1000);
    }
}

/*company_path.get()
    .addOnCompleteListener(task1 -> {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        if (task1.isSuccessful()) {
            String cname = task1.getResult().getString("company_name");
            String clocation = task1.getResult().getString("company_location");
            String cservices = task1.getResult().getString("company_services");
            String cexp = task1.getResult().getString("company_year_exp");
            String ccontact = task1.getResult().getString("company_contactnum");
            Timestamp created_time = task1.getResult().getTimestamp("company_created");
            Date created_date= created_time.toDate();

            functions.putSharedPrefsValue(getContext(), "user_data", "company_name", "string", cname);
            functions.putSharedPrefsValue(getContext(), "user_data", "company_location", "string", clocation);
            functions.putSharedPrefsValue(getContext(), "user_data", "company_services", "string", cservices);
            functions.putSharedPrefsValue(getContext(), "user_data", "company_contact", "string", ccontact);
            functions.putSharedPrefsValue(getContext(), "user_data", "company_exp", "string", cexp);
            functions.putSharedPrefsValue(getContext(), "user_data", "company_created", "string", format.format(created_date));
        } else {
            functions.putSharedPrefsValue(getContext(), "user_data", "company_name", "string", "No Data");
            functions.putSharedPrefsValue(getContext(), "user_data", "company_location", "string", "No Data");
            functions.putSharedPrefsValue(getContext(), "user_data", "company_services", "string", "No Data");
            functions.putSharedPrefsValue(getContext(), "user_data", "company_contact", "string", "No Data");
            functions.putSharedPrefsValue(getContext(), "user_data", "company_exp", "string", "No Data");
            functions.putSharedPrefsValue(getContext(), "user_data", "company_created", "string", format.format(new Date()));
        }
    });*/