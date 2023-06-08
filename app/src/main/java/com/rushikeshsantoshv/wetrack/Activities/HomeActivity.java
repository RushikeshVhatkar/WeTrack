package com.rushikeshsantoshv.wetrack.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.Fragments.HomeFragment;
import com.rushikeshsantoshv.wetrack.R;

import java.util.HashMap;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    RelativeLayout home_fragcontainer;
    BottomNavigationView home_bottom_navbar;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    FirebaseFirestore db;
    private long backPressedTime;
    Functions functions = new Functions();

    CoordinatorLayout home_coordinatorlay;
    AppBarLayout home_appbarlayout;
    RelativeLayout home_collapsecontentlay;
    Toolbar home_toolbar;
    CollapsingToolbarLayout home_collapsetoolbarlay;
    DrawerLayout home_drawerlayout;
    NavigationView home_navview;
    FloatingActionButton home_sidenav;
    TextView home_customtitle;
    String comp_path;

    private ReviewInfo reviewInfo;
    private ReviewManager reviewManager;

    @SuppressLint({"UseCompatLoadingForDrawables", "NonConstantResourceId"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        new Functions().lightBackgroundStatusBarDesign(HomeActivity.this);
        home_fragcontainer = findViewById(R.id.home_fragcontainer);
        home_bottom_navbar = findViewById(R.id.home_bottom_navbar);
        home_drawerlayout = findViewById(R.id.home_drawerlayout);
        home_navview = findViewById(R.id.home_navview);
        home_sidenav = findViewById(R.id.home_sidenav);

        home_customtitle = findViewById(R.id.home_customtitle);

        home_coordinatorlay = findViewById(R.id.home_coordinatorlay);
        home_appbarlayout = findViewById(R.id.home_appbarlayout);
        home_collapsecontentlay = findViewById(R.id.home_collapsecontentlay);
        home_toolbar = findViewById(R.id.home_toolbar);
        home_collapsetoolbarlay = findViewById(R.id.home_collapsetoolbarlay);
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        setSupportActionBar(home_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        home_collapsetoolbarlay.setTitleEnabled(false);

        home_sidenav.setOnClickListener(v -> home_drawerlayout.openDrawer(GravityCompat.START));
        home_navview.setNavigationItemSelectedListener(HomeActivity.this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(HomeActivity.this, home_drawerlayout, home_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerIndicatorEnabled(false);
        home_drawerlayout.addDrawerListener(toggle);
        ActionBar actionBar = getSupportActionBar();
        toggle.syncState();
//        home_navview.setCheckedItem(R.id.nav_home);

        activateReviewInfo();

        long curr_fin_year = functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                Long.parseLong(functions.getTodayDate("yyyy"));
        if (comp_path != null) {
            if(functions.checkInternetConnection(this)){
                db.collection("CompanyBaseRate")
                        .whereEqualTo("company_reference", db.document(comp_path))
                        .whereEqualTo("financial_year",curr_fin_year)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                if (task.getResult().getDocuments().size() <= 0) {
                                    BottomSheetDialog dialog = new BottomSheetDialog(HomeActivity.this);
                                    dialog.setContentView(R.layout.add_company_rate_btmdialog);
                                    dialog.setCanceledOnTouchOutside(false);
                                    dialog.setCancelable(false);
                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialog.show();

                                    TextInputLayout acr_rate1 = dialog.findViewById(R.id.acr_rate1);
                                    TextInputLayout acr_rate2 = dialog.findViewById(R.id.acr_rate2);
                                    TextInputLayout acr_rate3 = dialog.findViewById(R.id.acr_rate3);
                                    TextInputLayout acr_rate4 = dialog.findViewById(R.id.acr_rate4);
                                    TextView acr_add_btn = dialog.findViewById(R.id.acr_add_btn);
                                    acr_add_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
                                    acr_add_btn.setEnabled(false);

                                    acr_rate1.getEditText().addTextChangedListener(new CompanyTextWatcher(getApplicationContext(), acr_rate1, acr_rate2, acr_rate3, acr_rate4, acr_add_btn));
                                    acr_rate2.getEditText().addTextChangedListener(new CompanyTextWatcher(getApplicationContext(), acr_rate2, acr_rate1, acr_rate3, acr_rate4, acr_add_btn));
                                    acr_rate3.getEditText().addTextChangedListener(new CompanyTextWatcher(getApplicationContext(), acr_rate3, acr_rate1, acr_rate2, acr_rate4, acr_add_btn));
                                    acr_rate4.getEditText().addTextChangedListener(new CompanyTextWatcher(getApplicationContext(), acr_rate4, acr_rate1, acr_rate2, acr_rate3, acr_add_btn));

                                    acr_add_btn.setOnClickListener(v1 -> {
                                        String acr_rate1_txt = acr_rate1.getEditText().getText().toString();
                                        String acr_rate2_txt = acr_rate2.getEditText().getText().toString();
                                        String acr_rate3_txt = acr_rate3.getEditText().getText().toString();
                                        String acr_rate4_txt = acr_rate4.getEditText().getText().toString();

                                        Double acr_rate1_int = Double.parseDouble(acr_rate1_txt);
                                        Double acr_rate2_int = Double.parseDouble(acr_rate2_txt);
                                        Double acr_rate3_int = Double.parseDouble(acr_rate3_txt);
                                        Double acr_rate4_int = Double.parseDouble(acr_rate4_txt);

                                        Map<String, Object> company_rate_data = new HashMap<>();
                                        company_rate_data.put("financial_year",Long.parseLong(functions.getTodayDate("yyyy")));
                                        company_rate_data.put("jan_rate", acr_rate4_int);
                                        company_rate_data.put("feb_rate", acr_rate4_int);
                                        company_rate_data.put("mar_rate", acr_rate4_int);
                                        company_rate_data.put("apr_rate", acr_rate1_int);
                                        company_rate_data.put("may_rate", acr_rate1_int);
                                        company_rate_data.put("jun_rate", acr_rate1_int);
                                        company_rate_data.put("jul_rate", acr_rate2_int);
                                        company_rate_data.put("aug_rate", acr_rate2_int);
                                        company_rate_data.put("sep_rate", acr_rate2_int);
                                        company_rate_data.put("oct_rate", acr_rate3_int);
                                        company_rate_data.put("nov_rate", acr_rate3_int);
                                        company_rate_data.put("dec_rate", acr_rate3_int);
                                        company_rate_data.put("company_reference", db.document(comp_path));

                                        db.collection("CompanyBaseRate")
                                                .document()
                                                .set(company_rate_data)
                                                .addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        dialog.dismiss();
                                                        Snackbar.make(home_drawerlayout, "Rates added successfully...", Snackbar.LENGTH_LONG).show();
                                                    } else {
                                                        Snackbar.make(home_drawerlayout, "Unable to add rates !! Please try again.", Snackbar.LENGTH_LONG).show();
                                                    }
                                                });
                                    });
                                }
                            } else {
                                String msg = task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error ...";
                                Log.e("firebase_error", "The error is : " + msg);
                            }
                        });

                db.collection("Managers")
                        .whereEqualTo("company_path", db.document(comp_path))
                        .get()
                        .addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                if (task2.getResult().getDocuments().size() <= 0) {
                                    BottomSheetDialog addmanager_btm_dialog = new BottomSheetDialog(this);
                                    addmanager_btm_dialog.setContentView(R.layout.addworkers_btm_dialog);
                                    addmanager_btm_dialog.setCanceledOnTouchOutside(false);
                                    addmanager_btm_dialog.setCancelable(false);
                                    addmanager_btm_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                                    TextInputLayout aw_btm_name = addmanager_btm_dialog.findViewById(R.id.aw_btm_name);
                                    TextInputLayout aw_btm_phone = addmanager_btm_dialog.findViewById(R.id.aw_btm_phone);
                                    TextView aw_btm_heading = addmanager_btm_dialog.findViewById(R.id.aw_btm_heading);
                                    TextInputLayout aw_btm_advancedloans = addmanager_btm_dialog.findViewById(R.id.aw_btm_advancedloans);
                                    SwitchCompat aw_btm_active_user_check = addmanager_btm_dialog.findViewById(R.id.aw_btm_active_user_check);
                                    Spinner aw_btm_managers_dropdown = addmanager_btm_dialog.findViewById(R.id.aw_btm_managers_dropdown);
                                    TextView aw_btm_managers_heading= addmanager_btm_dialog.findViewById(R.id.aw_btm_managers_heading);
                                    RadioGroup aw_btm_genders= addmanager_btm_dialog.findViewById(R.id.aw_btm_genders);
                                    aw_btm_advancedloans.setVisibility(View.GONE);
                                    Button aw_btm_add_btn = addmanager_btm_dialog.findViewById(R.id.aw_btm_add_btn);

                                    aw_btm_add_btn.setText("Add Manager");
                                    aw_btm_heading.setText("Add a new Manager");
                                    aw_btm_name.setHint("Manager Name");
                                    aw_btm_active_user_check.setText("Active Manager");
                                    aw_btm_managers_heading.setVisibility(View.GONE);
                                    aw_btm_managers_dropdown.setVisibility(View.GONE);

                                    aw_btm_add_btn.setEnabled(false);
                                    aw_btm_add_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));

                                    aw_btm_name.getEditText().addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                                            String emp_name = aw_btm_name.getEditText().getText().toString();
                                            String emp_contact = aw_btm_phone.getEditText().getText().toString();
                                            String fullname_regx = "^[a-zA-Z\\s]*$";

                                            if (emp_name.length() <= 2) {
                                                aw_btm_name.setError("Enter a name with more than 2 characters length !!");
                                                aw_btm_name.requestFocus();
                                                functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            } else if (!emp_name.matches(fullname_regx)) {
                                                aw_btm_name.setError("Please enter a valid name !!");
                                                aw_btm_name.requestFocus();
                                                functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            } else
                                                aw_btm_name.setError(null);

                                            if (emp_contact.length() < 10)
                                                functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else
                                                functions.updateButtonStyle("#00A3FF", true, aw_btm_add_btn);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                        }
                                    });

                                    aw_btm_phone.getEditText().addTextChangedListener(new TextWatcher() {
                                        @Override
                                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                        }

                                        @Override
                                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                                            String emp_name = aw_btm_name.getEditText().getText().toString();
                                            String emp_contact = aw_btm_phone.getEditText().getText().toString();
                                            String fullname_regx = "^[a-zA-Z\\s]*$";

                                            if (emp_contact.length() < 10) {
                                                aw_btm_phone.setError("Enter a valid contact number !!");
                                                aw_btm_phone.requestFocus();
                                                functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            } else
                                                aw_btm_phone.setError(null);

                                            if (emp_name.length() <= 2)
                                                functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else if (!emp_name.matches(fullname_regx))
                                                functions.updateButtonStyle("#DADADA", false, aw_btm_add_btn);
                                            else
                                                functions.updateButtonStyle("#00A3FF", true, aw_btm_add_btn);
                                        }

                                        @Override
                                        public void afterTextChanged(Editable editable) {
                                        }
                                    });

                                    aw_btm_add_btn.setOnClickListener(v2 -> {
                                        addmanager_btm_dialog.dismiss();
                                        String emp_name = aw_btm_name.getEditText().getText().toString();
                                        String emp_contact = aw_btm_phone.getEditText().getText().toString();
                                        String company_path_val = new Functions().getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
                                        String selected_gender=  aw_btm_genders.getCheckedRadioButtonId()==R.id.aw_btm_gender_male ? "Male" :
                                                (aw_btm_genders.getCheckedRadioButtonId()==R.id.aw_btm_gender_female ? "Female" : "Others");

                                        if (company_path_val != null) {
                                            DocumentReference company_path = db.document(company_path_val);
                                            Map<String, Object> worker_data = new HashMap<>();
                                            worker_data.put("manager_name", emp_name);
                                            worker_data.put("manager_contact", "+91" + emp_contact);
                                            worker_data.put("manager_timestamp", Timestamp.now());
                                            worker_data.put("manager_gender",selected_gender);
                                            worker_data.put("manager_status", aw_btm_active_user_check.isChecked());
                                            worker_data.put("company_path", company_path);

                                            if(functions.checkInternetConnection(this)){
                                                db.collection("Managers")
                                                        .add(worker_data)
                                                        .addOnCompleteListener(task -> {
                                                            if (task.isSuccessful()) {
                                                                Snackbar.make(home_fragcontainer, "New manager added successfully.", Snackbar.LENGTH_SHORT).show();
                                                            } else {
                                                                Snackbar.make(home_fragcontainer, "Unable to add an manager !! Please try again.", Snackbar.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                            else functions.no_internet_dialog(this, false);

                                        } else {
                                            Snackbar.make(home_fragcontainer, "Unable to add new manager for the time being. Please try later !!", Snackbar.LENGTH_SHORT).show();
                                        }
                                    });

                                    addmanager_btm_dialog.show();
                                }
                            } else {
                                String msg = task2.getException() != null && task2.getException().getLocalizedMessage() != null ? task2.getException().getLocalizedMessage() : "No Error ...";
                                Log.e("firebase_error", "The error is : " + msg);
                            }
                        });
            }
            else functions.no_internet_dialog(this, false);
        }
        else {
            Log.e("company_error", "The company path not found !!");
        }

        home_appbarlayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (Math.abs(verticalOffset) == home_appbarlayout.getTotalScrollRange()) {
                home_toolbar.setBackground(getApplicationContext().getDrawable(R.color.maincolor_light));
                getSupportActionBar().setDisplayShowTitleEnabled(true);
                toggle.setDrawerIndicatorEnabled(true);
                home_fragcontainer.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.color.white));
                home_fragcontainer.setElevation(0);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                functions.coloredStatusBarDarkTextDesign(HomeActivity.this, R.color.maincolor_light, R.color.white);
            } else {
                home_toolbar.setBackground(getApplicationContext().getDrawable(android.R.color.transparent));
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                home_fragcontainer.setElevation(20);
                toggle.setDrawerIndicatorEnabled(false);
                home_fragcontainer.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.top_curve_shape));
                functions.lightBackgroundStatusBarDesign(HomeActivity.this);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        getSupportFragmentManager().beginTransaction().add(R.id.home_fragcontainer, new HomeFragment(actionBar, home_customtitle)).commit();
        home_bottom_navbar.setSelectedItemId(R.id.menu_home);

        home_bottom_navbar.setOnItemSelectedListener(item -> {
            Fragment selectedfragment = null;

            switch (item.getItemId()) {

                case R.id.menu_home:
                    selectedfragment = new HomeFragment(actionBar, home_customtitle);
                    break;

                // TODO: Remove this comment after implementing the sticky notes
                /*case R.id.menu_stickynotes:
                    selectedfragment = new StickyNotesFragment(actionBar, home_customtitle);
                    break;*/

                default:
                    break;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.home_fragcontainer, selectedfragment).commit();
            item.setIconTintList(ContextCompat.getColorStateList(getApplicationContext(), R.color.maincolor));
            return true;
        });

    }

    private void verify() {
        FirebaseAuth.AuthStateListener mAuthStateListner = firebaseAuth -> {
            FirebaseUser mFirebaseuser = firebaseAuth.getCurrentUser();
            if (mFirebaseuser == null) {
                Toast.makeText(getApplicationContext(), "You have already logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), SelectUserActivity.class));
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finishAffinity();
        } else {
            Snackbar.make(home_drawerlayout, "Press back again to Exit", Snackbar.LENGTH_SHORT).show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Functions().clearSharedPreferences(getApplicationContext(), "attend_frag");
    }

    private void activateReviewInfo(){
        reviewManager= ReviewManagerFactory.create(this);
        Task<ReviewInfo> managerIntoTask= reviewManager.requestReviewFlow();
        managerIntoTask.addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                reviewInfo = task.getResult();
            }
            else{
                Snackbar.make(home_fragcontainer, "Review failed to start", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.drawer_aboutus: {
                new Handler().postDelayed(() -> {
                    home_drawerlayout.close();
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }, 500);
                }, 500);
                break;
            }

            case R.id.drawer_contactus: {
                new Handler().postDelayed(() -> {
                    home_drawerlayout.close();
                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(getApplicationContext(), ContactUsActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }, 500);
                }, 500);
                break;
            }

            case R.id.drawer_rate: {
                new Handler().postDelayed(() -> {
                    home_drawerlayout.close();
                    new Handler().postDelayed(() -> {
                        if(functions.checkInternetConnection(this)){
                            if(reviewInfo !=null){
                                Task<Void> flow= reviewManager.launchReviewFlow(this,reviewInfo);
                                flow.addOnCompleteListener(task -> Snackbar.make(home_fragcontainer, "Rating is completed", Snackbar.LENGTH_SHORT).show());
                            }
                        }
                        else functions.no_internet_dialog(this, false);
                    }, 500);
                }, 500);
                break;
            }

            case R.id.drawer_share_app: {
                new Handler().postDelayed(() -> {
                    home_drawerlayout.close();
                    new Handler().postDelayed(() -> {
                        Intent intent1 = new Intent(Intent.ACTION_SEND);
                        intent1.setType("text/plain");
                        String body = "Let me recommend you this application\n\nhttps://play.google.com/store/apps/details?id="+getPackageName();
                        intent1.putExtra(Intent.EXTRA_TEXT, body);
                        startActivity(Intent.createChooser(intent1, "Share using"));
                    }, 500);
                }, 500);
                break;
            }

            case R.id.drawer_privacy: {
                new Handler().postDelayed(() -> {
                    home_drawerlayout.close();
                    new Handler().postDelayed(() -> {
                        Intent intent= new Intent(getApplicationContext(), PrivacyPolicyTermsConditionsActivity.class);
                        intent.putExtra("weblink","https://www.freeprivacypolicy.com/live/c0bc722b-1e60-498c-8c82-338e90e8f19b");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }, 500);
                }, 500);
                break;
            }

            case R.id.drawer_terms_and_conditions: {
                new Handler().postDelayed(() -> {
                    home_drawerlayout.close();
                    new Handler().postDelayed(() -> {
                        Intent intent= new Intent(getApplicationContext(), PrivacyPolicyTermsConditionsActivity.class);
                        intent.putExtra("weblink","file:///android_asset/terms_and_conditions.html");
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }, 500);
                }, 500);
                break;
            }

            case R.id.drawer_logout: {
                new Functions().putSharedPrefsValue(getApplicationContext(), "user_data", "login_status", "boolean", false);
                functions.clearSharedPreferences(getApplicationContext(), "user_data");
                verify();
                firebaseAuth.signOut();
                Snackbar.make(home_drawerlayout, "Logout Successful. Please login to an account.", Snackbar.LENGTH_SHORT).show();
                new Handler().postDelayed(() -> {
                    startActivity(new Intent(getApplicationContext(), SelectUserActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }, 1000);
                break;
            }

            default:
                break;
        }
        return true;
    }

    private static class CompanyTextWatcher implements TextWatcher {

        Context context;
        private TextInputLayout textInputLayout;
        private TextInputLayout edtxt1, edtxt2, edtxt3;
        private TextView add_btn;

        public CompanyTextWatcher(Context context, TextInputLayout textInputLayout, TextInputLayout edtxt1, TextInputLayout edtxt2,
                                  TextInputLayout edtxt3, TextView add_btn) {
            this.context = context;
            this.textInputLayout = textInputLayout;
            this.edtxt1 = edtxt1;
            this.edtxt2 = edtxt2;
            this.edtxt3 = edtxt3;
            this.add_btn = add_btn;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String val_txt = textInputLayout.getEditText().getText().toString();
            String edtxt1_txt = edtxt1.getEditText().getText().toString();
            String edtxt2_txt = edtxt2.getEditText().getText().toString();
            String edtxt3_txt = edtxt3.getEditText().getText().toString();

            Double edtxt1_int = !edtxt1_txt.equals("") ? Double.parseDouble(edtxt1_txt) : 0.0;
            Double edtxt2_int = !edtxt2_txt.equals("") ? Double.parseDouble(edtxt2_txt) : 0.0;
            Double edtxt3_int = !edtxt3_txt.equals("") ? Double.parseDouble(edtxt3_txt) : 0.0;
            Double val_int = !val_txt.equals("") ? Double.parseDouble(val_txt) : 0.0;

            if (val_txt.equals("") || val_int < 1) {
                textInputLayout.setError("Please enter a number greater than 0 !!");
                textInputLayout.setErrorEnabled(true);
                textInputLayout.requestFocus();
            } else {
                textInputLayout.setErrorEnabled(false);
                textInputLayout.setError(null);
            }

            if (val_int > 0 && edtxt1_int > 0 && edtxt2_int > 0 && edtxt3_int > 0) {
                add_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0045AC")));
                add_btn.setEnabled(true);
            } else {
                add_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
                add_btn.setEnabled(false);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }
}