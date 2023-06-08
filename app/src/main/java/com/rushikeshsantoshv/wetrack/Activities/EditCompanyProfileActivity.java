package com.rushikeshsantoshv.wetrack.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import java.util.HashMap;
import java.util.Map;

public class EditCompanyProfileActivity extends AppCompatActivity {

    RelativeLayout editcompprofile_mainlay;
    ImageButton editcompprofile_back_btn;
    EditText editcompprofile_owner_fullname, editcompprofile_owner_emailid, editcompprofile_owner_phonenum, editcompprofile_owner_ccode;
    EditText editcompprofile_comp_name, editcompprofile_comp_location, editcompprofile_comp_contactnum;
    TextView editcompprofile_updatedetails_btn;

    Functions functions = new Functions();
    private final String email_regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    String comp_path;

    String owner_name, owner_fullcontact, owner_email;
    String comp_name, comp_contact, comp_location;
    String comp_id, emp_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_company_profile);
        functions.coloredStatusBarDarkTextDesign(EditCompanyProfileActivity.this, R.color.maincolor_light, R.color.white);

        editCompanyProfileInits();

        editcompprofile_back_btn.setOnClickListener(v -> finish());

        if(functions.checkInternetConnection(this)){
            Dialog loading_dialog = functions.createDialogBox(EditCompanyProfileActivity.this, R.layout.loading_dialog, false);
            loading_dialog.show();
            LoadCompanyData(new CompaniesCallBack<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot result1) {

                    comp_name = result1.getString("company_name");
                    comp_contact = result1.getString("company_contactnum");
                    comp_location = result1.getString("company_location");
                    comp_id = result1.getId();

                    editcompprofile_comp_name.setText(comp_name);
                    editcompprofile_comp_contactnum.setText(comp_contact);
                    editcompprofile_comp_location.setText(comp_location);

                    LoadOwnerData(new OwnersCallBack<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot result2) {
                            owner_name = result2.getString("full_name");
                            owner_email = result2.getString("email_id");
                            owner_fullcontact = result2.getString("contact_number");
                            emp_id = result2.getId();

                            String ccode = owner_fullcontact.split("-", 2)[0];
                            String contact_num = owner_fullcontact.split("-", 2)[1];

                            editcompprofile_owner_fullname.setText(owner_name);
                            editcompprofile_owner_emailid.setText(owner_email);
                            editcompprofile_owner_phonenum.setText(contact_num);
                            editcompprofile_owner_ccode.setText(ccode);
                            loading_dialog.dismiss();
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loading_dialog.cancel();
                            String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error";
                            Log.e("firebase_error", "The error is : " + msg);
                            Snackbar.make(editcompprofile_mainlay, msg, Snackbar.LENGTH_SHORT).show();
                            new Handler().postDelayed(EditCompanyProfileActivity.this::finish, 1500);
                        }
                    });
                }

                @Override
                public void onFailure(Exception e) {
                    loading_dialog.cancel();
                    String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error";
                    Log.e("firebase_error", "The error is : " + msg);
                    Snackbar.make(editcompprofile_mainlay, msg, Snackbar.LENGTH_SHORT).show();
                    new Handler().postDelayed(EditCompanyProfileActivity.this::finish, 1500);
                }
            });
        }
        else functions.no_internet_dialog(this, true);

        editcompprofile_updatedetails_btn.setOnClickListener(v -> {
            Dialog loading_dialog = functions.createDialogBox(EditCompanyProfileActivity.this, R.layout.loading_dialog, false);
            String fullname_txt = editcompprofile_owner_fullname.getText().toString();
            String email_txt = editcompprofile_owner_emailid.getText().toString();
            String companyname_txt = editcompprofile_comp_name.getText().toString();
            String clocation_txt = editcompprofile_comp_location.getText().toString();
            String ccontactnum = editcompprofile_comp_contactnum.getText().toString();

            loading_dialog.show();
            Map<String, Object> owner_data = new HashMap<>();
            Map<String, Object> company_data = new HashMap<>();

            if (!fullname_txt.equals(owner_name))
                owner_data.put("full_name", fullname_txt);
            if (!email_txt.equals(owner_email))
                owner_data.put("email_id", email_txt);
            if (!companyname_txt.equals(comp_name))
                company_data.put("company_name", companyname_txt);
            if (!clocation_txt.equals(comp_location))
                company_data.put("company_location", clocation_txt);
            if (!ccontactnum.equals(comp_contact))
                company_data.put("company_contactnum", ccontactnum);

            Log.e("owner_data","The owner data is : "+owner_data.toString());
            Log.e("company_data","The company data is : "+company_data.toString());

            if (owner_data.isEmpty() && company_data.isEmpty()) {
                Log.e("check","Submit - IF Condition Entered....");
                loading_dialog.cancel();
                Snackbar.make(editcompprofile_mainlay, "No details was altered !! Returning to profile page...", Snackbar.LENGTH_SHORT).show();
                new Handler().postDelayed(EditCompanyProfileActivity.this::finish, 1500);
            }
            else if(!owner_data.isEmpty() && !company_data.isEmpty()){
                if(functions.checkInternetConnection(this)){
                    Log.e("check","Submit - ELSE IF Condition Entered....");
                    UpdateCompanyData(company_data, comp_id, new UpdateCompaniesCallBack<Boolean>() {
                        @Override
                        public void onSuccess(boolean result) {
                            functions.putSharedPrefsValue(getApplicationContext(), "user_data", "company_name", "string", companyname_txt);
                            functions.putSharedPrefsValue(getApplicationContext(), "user_data", "company_location", "string", clocation_txt);
                            functions.putSharedPrefsValue(getApplicationContext(), "user_data", "company_contact", "string", ccontactnum);
                            if(functions.checkInternetConnection(EditCompanyProfileActivity.this)){
                                UpdateOwnerData(owner_data, emp_id, new UpdateOwnersCallBack<Boolean>() {
                                    @Override
                                    public void onSuccess(boolean result) {
                                        loading_dialog.dismiss();
                                        functions.putSharedPrefsValue(getApplicationContext(), "user_data", "full_name", "string", fullname_txt);
                                        functions.putSharedPrefsValue(getApplicationContext(), "user_data", "email_id", "string", email_txt);
                                        Snackbar.make(editcompprofile_mainlay, "Changes updated successfully.. Returning to profile page...", Snackbar.LENGTH_SHORT).show();
                                        new Handler().postDelayed(() -> {
                                            startActivity(new Intent(getApplicationContext(), PersonalInfoActivity.class));
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            finish();
                                        }, 1500);
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        loading_dialog.cancel();
                                        String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error";
                                        Log.e("firebase_error", "The error is : " + msg);
                                        Snackbar.make(editcompprofile_mainlay, "Unable to update the details !! Please try again", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else functions.no_internet_dialog(EditCompanyProfileActivity.this, false);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loading_dialog.cancel();
                            String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error";
                            Log.e("firebase_error", "The error is : " + msg);
                            Snackbar.make(editcompprofile_mainlay, "Unable to update the details !! Please try again", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                else functions.no_internet_dialog(this, false);
            }
            else{
                if(!owner_data.isEmpty()){
                    Log.e("check","Submit - ELSE - IF Condition Entered....");
                    Log.e("check","The owner ID is : "+emp_id);
                    UpdateOwnerData(owner_data, emp_id, new UpdateOwnersCallBack<Boolean>() {
                        @Override
                        public void onSuccess(boolean result) {
                            loading_dialog.dismiss();
                            functions.putSharedPrefsValue(getApplicationContext(), "user_data", "full_name", "string", fullname_txt);
                            functions.putSharedPrefsValue(getApplicationContext(), "user_data", "email_id", "string", email_txt);
                            Snackbar.make(editcompprofile_mainlay, "Changes updated successfully.. Returning to profile page...", Snackbar.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(getApplicationContext(), PersonalInfoActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }, 1500);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loading_dialog.cancel();
                            String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error";
                            Log.e("firebase_error", "The error is : " + msg);
                            Snackbar.make(editcompprofile_mainlay, "Unable to update the details !! Please try again", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    Log.e("check","Submit - ELSE - ELSE Condition Entered....");
                    Log.e("check","The company ID is : "+comp_id);
                    UpdateCompanyData(company_data, comp_id, new UpdateCompaniesCallBack<Boolean>() {
                        @Override
                        public void onSuccess(boolean result) {
                            loading_dialog.dismiss();
                            functions.putSharedPrefsValue(getApplicationContext(), "user_data", "company_name", "string", companyname_txt);
                            functions.putSharedPrefsValue(getApplicationContext(), "user_data", "company_location", "string", clocation_txt);
                            functions.putSharedPrefsValue(getApplicationContext(), "user_data", "company_contact", "string", ccontactnum);
                            Snackbar.make(editcompprofile_mainlay, "Changes updated successfully.. Returning to profile page...", Snackbar.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(getApplicationContext(), PersonalInfoActivity.class));
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                finish();
                            }, 1500);
                        }

                        @Override
                        public void onFailure(Exception e) {
                            loading_dialog.cancel();
                            String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error";
                            Log.e("firebase_error", "The error is : " + msg);
                            Snackbar.make(editcompprofile_mainlay, "Unable to update the details !! Please try again", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        editcompprofile_owner_fullname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fullname_txt = editcompprofile_owner_fullname.getText().toString();
                String email_txt = editcompprofile_owner_emailid.getText().toString();
                String companyname_txt = editcompprofile_comp_name.getText().toString();
                String clocation_txt = editcompprofile_comp_location.getText().toString();
                String ccontactnum = editcompprofile_comp_contactnum.getText().toString();

                if (fullname_txt.length() < 3) {
                    editcompprofile_owner_fullname.setError("Enter more than 4 characters !!");
                    editcompprofile_owner_fullname.requestFocus();
                    updateNextButtonStyle("#DADADA", false);
                } else
                    editcompprofile_owner_fullname.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5 &&
                        clocation_txt.length() >= 3 && ccontactnum.length() >= 10) {
                    updateNextButtonStyle("#0045AC", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editcompprofile_owner_emailid.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String fullname_txt = editcompprofile_owner_fullname.getText().toString();
                String email_txt = editcompprofile_owner_emailid.getText().toString();
                String companyname_txt = editcompprofile_comp_name.getText().toString();
                String clocation_txt = editcompprofile_comp_location.getText().toString();
                String ccontactnum = editcompprofile_comp_contactnum.getText().toString();

                if (email_txt.length() > 0 && !email_txt.matches(email_regex)) {
                    editcompprofile_owner_emailid.setError("Enter more than 4 characters !!");
                    editcompprofile_owner_emailid.requestFocus();
                    updateNextButtonStyle("#DADADA", false);
                } else
                    editcompprofile_owner_emailid.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5 &&
                        clocation_txt.length() >= 3 && ccontactnum.length() >= 10) {
                    updateNextButtonStyle("#0045AC", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editcompprofile_comp_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                String fullname_txt = editcompprofile_owner_fullname.getText().toString();
                String email_txt = editcompprofile_owner_emailid.getText().toString();
                String companyname_txt = editcompprofile_comp_name.getText().toString();
                String clocation_txt = editcompprofile_comp_location.getText().toString();
                String ccontactnum = editcompprofile_comp_contactnum.getText().toString();

                if (companyname_txt.length() < 5) {
                    editcompprofile_comp_name.setError("Enter more than 4 characters !!");
                    editcompprofile_comp_name.requestFocus();
                    updateNextButtonStyle("#DADADA", false);
                } else
                    editcompprofile_comp_name.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5
                        && clocation_txt.length() >= 3 && ccontactnum.length() >= 10) {
                    updateNextButtonStyle("#0045AC", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editcompprofile_comp_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fullname_txt = editcompprofile_owner_fullname.getText().toString();
                String email_txt = editcompprofile_owner_emailid.getText().toString();
                String companyname_txt = editcompprofile_comp_name.getText().toString();
                String clocation_txt = editcompprofile_comp_location.getText().toString();
                String ccontactnum = editcompprofile_comp_contactnum.getText().toString();

                if (clocation_txt.length() < 3) {
                    editcompprofile_comp_location.setError("Enter more than 2 characters !!");
                    editcompprofile_comp_location.requestFocus();
                    updateNextButtonStyle("#DADADA", false);

                } else
                    editcompprofile_comp_location.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5
                        && clocation_txt.length() >= 3 && ccontactnum.length() >= 10) {
                    updateNextButtonStyle("#0045AC", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editcompprofile_comp_contactnum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String fullname_txt = editcompprofile_owner_fullname.getText().toString();
                String email_txt = editcompprofile_owner_emailid.getText().toString();
                String companyname_txt = editcompprofile_comp_name.getText().toString();
                String clocation_txt = editcompprofile_comp_location.getText().toString();
                String ccontactnum = editcompprofile_comp_contactnum.getText().toString();

                if (ccontactnum.length() < 10) {
                    editcompprofile_comp_contactnum.setError("Enter valid contact number !!");
                    editcompprofile_comp_contactnum.requestFocus();
                    updateNextButtonStyle("#DADADA", false);
                } else
                    editcompprofile_comp_contactnum.setError(null);

                if (fullname_txt.length() >= 3 && email_txt.matches(email_regex) && companyname_txt.length() >= 5
                        && ccontactnum.length() == 10 && clocation_txt.length() >= 3) {
                    updateNextButtonStyle("#0045AC", true);
                } else {
                    updateNextButtonStyle("#DADADA", false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void editCompanyProfileInits() {
        editcompprofile_mainlay = findViewById(R.id.editcompprofile_mainlay);
        editcompprofile_back_btn = findViewById(R.id.editcompprofile_back_btn);
        editcompprofile_owner_fullname = findViewById(R.id.editcompprofile_owner_fullname);
        editcompprofile_owner_emailid = findViewById(R.id.editcompprofile_owner_emailid);
        editcompprofile_owner_phonenum = findViewById(R.id.editcompprofile_owner_phonenum);
        editcompprofile_owner_ccode = findViewById(R.id.editcompprofile_owner_ccode);
        editcompprofile_comp_name = findViewById(R.id.editcompprofile_comp_name);
        editcompprofile_comp_location = findViewById(R.id.editcompprofile_comp_location);
        editcompprofile_comp_contactnum = findViewById(R.id.editcompprofile_comp_contactnum);
        editcompprofile_updatedetails_btn = findViewById(R.id.editcompprofile_updatedetails_btn);
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
    }

    public void updateNextButtonStyle(String s, boolean b) {
        editcompprofile_updatedetails_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(s)));
        editcompprofile_updatedetails_btn.setEnabled(b);
    }

    private void UpdateCompanyData(Map<String, Object> company_data, String comp_id, UpdateCompaniesCallBack<Boolean> callBack) {
        db.collection("Companies")
                .document(comp_id)
                .update(company_data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(true);
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private void UpdateOwnerData(Map<String, Object> owner_data, String emp_id, UpdateOwnersCallBack<Boolean> callBack) {
        db.collection("Owners")
                .document(emp_id)
                .update(owner_data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(true);
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private void LoadCompanyData(CompaniesCallBack<DocumentSnapshot> callBack) {
        db.collection("Companies")
                .document(db.document(comp_path).getId())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(task.getResult());
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private void LoadOwnerData(OwnersCallBack<DocumentSnapshot> callBack) {
        db.collection("Owners")
                .whereEqualTo("company_path", db.document(comp_path))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().size() > 0) {
                            callBack.onSuccess(task.getResult().getDocuments().get(0));
                        } else {
                            callBack.onFailure(new Exception("Unable to retrieve data !! No such owner exists. Please try again"));
                        }
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private interface CompaniesCallBack<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }

    private interface OwnersCallBack<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }

    private interface UpdateCompaniesCallBack<T> {
        void onSuccess(boolean result);

        void onFailure(Exception e);
    }

    private interface UpdateOwnersCallBack<T> {
        void onSuccess(boolean result);

        void onFailure(Exception e);
    }

    /*db.collection("Companies")
            .document(db.document(comp_path).getId())
            .get()
                .addOnCompleteListener(task -> {
        Log.e("firebase_error", "Companies Entering.....");
        if (task.isSuccessful()) {
            DocumentSnapshot result1 = task.getResult();
            editcompprofile_comp_name.setText(result1.getString("company_name"));
            editcompprofile_comp_contactnum.setText(result1.getString("company_contactnum"));
            editcompprofile_comp_location.setText(result1.getString("company_location"));

            db.collection("Owners")
                    .whereEqualTo("company_path", db.document(comp_path))
                    .get()
                    .addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            if (task1.getResult().getDocuments().size() > 0) {
                                DocumentSnapshot doc = task1.getResult().getDocuments().get(0);
                                editcompprofile_owner_fullname.setText(doc.getString("full_name"));
                                editcompprofile_owner_emailid.setText(doc.getString("email_id"));
                                String full_contact = doc.getString("contact_number");
                                String ccode = full_contact.split("-", 2)[0];
                                String contact_num = full_contact.split("-", 2)[1];
                                editcompprofile_owner_phonenum.setText(contact_num);
                                editcompprofile_owner_ccode.setText(ccode);
                                loading_dialog.dismiss();
                            } else {
                                loading_dialog.cancel();
                                String errorMessage = "Unable to retrieve the data !! No such company exists";
                                Log.e("firebase_error", "The error is : " + errorMessage);
                            }
                        } else {
                            loading_dialog.cancel();
                            String msg = task1.getException().getLocalizedMessage() != null ? task1.getException().getLocalizedMessage() : "No Error";
                            Log.e("firebase_error", "The error is : " + msg);
                        }
                    });
        } else {
            loading_dialog.cancel();
            String msg = task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error";
            Log.e("firebase_error", "The error is : " + msg);
        }
    });*/
}