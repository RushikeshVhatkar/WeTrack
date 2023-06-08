package com.rushikeshsantoshv.wetrack.DataModels;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rushikeshsantoshv.wetrack.R;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

// @SuppressWarnings("all")
public class Functions {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;

    public void checkTheme(@NotNull Context context) {

        SharedPreferences prefs_2 = context.getSharedPreferences("app_data", Activity.MODE_PRIVATE);
        int isCheck = prefs_2.getInt("get_theme", 0);

        if (isCheck == 0) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else if (isCheck == 1) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if (isCheck == 2) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void darkBackgroundStatusBarDesign(@NotNull Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(R.color.heading_no));
    }

    public void lightBackgroundStatusBarDesign(Activity activity) {
        int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        activity.getWindow().setStatusBarColor(Color.WHITE);
        activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity.getApplicationContext(), R.color.white));
        activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    public void coloredStatusBarDarkTextDesign(@NotNull Activity activity, int status_color_id, int navigation_color_id) {
        int flags = activity.getWindow().getDecorView().getSystemUiVisibility();
        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        activity.getWindow().getDecorView().setSystemUiVisibility(flags);
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(status_color_id));
        activity.getWindow().setNavigationBarColor(ContextCompat.getColor(activity.getApplicationContext(), navigation_color_id));
        activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
    }

    public void coloredStatusBarLightTextDesign(@NotNull Activity activity, int color_id) {
        activity.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        activity.getWindow().setStatusBarColor(activity.getResources().getColor(color_id));
    }

    public <Any> void putSharedPrefsValue(@NotNull Context context, String prefs_name, String prefs_objname, @NotNull String type, Any set_val) {

        SharedPreferences.Editor pref_edit = context.getSharedPreferences(prefs_name, MODE_PRIVATE).edit();
        switch (type) {
            case "string":
                pref_edit.putString(prefs_objname, (String) set_val);
                break;

            case "int":
                pref_edit.putInt(prefs_objname, (Integer) set_val);
                break;

            case "boolean":
                pref_edit.putBoolean(prefs_objname, (Boolean) set_val);
                break;

            case "float":
                pref_edit.putFloat(prefs_objname, (Float) set_val);
                break;

            case "long":
                pref_edit.putLong(prefs_objname, (Long) set_val);
                break;
        }
        pref_edit.apply();
    }

    public void clearSharedPreferences(Context context, String pref_name) {
        SharedPreferences pref = context.getSharedPreferences(pref_name, Context.MODE_PRIVATE);
        pref.edit().clear().commit();
    }

    public <Any> Any getSharedPrefsValue(@NotNull Context context, String prefs_name, String prefs_objname, @NotNull String type, Any default_val) {

        SharedPreferences pref = context.getSharedPreferences(prefs_name, MODE_PRIVATE);
        switch (type) {

            case "string":
                String stringval = pref.getString(prefs_objname, (String) default_val);
                return ((Any) (String) stringval);

            case "int":
                int intval = pref.getInt(prefs_objname, (Integer) default_val);
                return ((Any) (Integer) intval);

            case "boolean":
                Boolean boolval = pref.getBoolean(prefs_objname, (Boolean) default_val);
                return ((Any) (Boolean) boolval);

            case "float":
                Float floatval = pref.getFloat(prefs_objname, (Float) default_val);
                return ((Any) (Float) floatval);

            case "long":
                Long longval = pref.getLong(prefs_objname, (Long) default_val);
                return ((Any) (Long) longval);

            default:
                return null;
        }
    }

    public Dialog createDialogBox(Activity activity, int view_id, boolean isclose) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(view_id);
        dialog.setCanceledOnTouchOutside(isclose);
        dialog.setCancelable(isclose);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        return dialog;
    }

    public int getNetworkSignalStrength(@NonNull Activity activity){
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
        int val=-10000;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            if (networkCapabilities != null) {
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    val= networkCapabilities.getSignalStrength();
                } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    val= networkCapabilities.getSignalStrength();
                }
            }
        }
        return val;
    }

    public boolean checkInternetConnection(@NonNull Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void no_internet_dialog(Activity activity, boolean goback) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.no_net_dialog);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        Button no_net_dialog_oktbn = dialog.findViewById(R.id.no_net_dia_ok_btn);

        no_net_dialog_oktbn.setOnClickListener(v2 -> {
            if(goback){
                dialog.dismiss();
                new Handler().postDelayed(activity::finish,500);
            }
            else dialog.dismiss();
        });

        dialog.show();
    }

    public Date modifiedDate(Date date, int change) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, change);
        return cal.getTime();
    }

    @SuppressLint("SetTextI18n")
    public long getNoOfDays(@NonNull Date t1, @NonNull Date t2) {
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyy", Locale.getDefault());
        String startDateStr = df.format(t1);
        String endDateStr = df.format(t2);
        long nodays = 0;
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date endDate = sdf.parse(endDateStr);
            Date startDate = sdf.parse(startDateStr);
            long diff = endDate.getTime() - startDate.getTime();
            nodays = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return nodays;
    }

    @NonNull
    public ArrayList<String> getMonthList() {
        ArrayList<String> arr = new ArrayList<>();
        arr.add("apr");
        arr.add("may");
        arr.add("jun");
        arr.add("jul");
        arr.add("aug");
        arr.add("sep");
        arr.add("oct");
        arr.add("nov");
        arr.add("dec");
        arr.add("jan");
        arr.add("feb");
        arr.add("mar");
        return arr;
    }

    public String getTodayDate(String format) {

//        Log.e("today_date", "Today's date is : " + getTodayDate("dd-MM-yyyy"));
//        Log.e("today_date", "Today's date is : " + getTodayDate("dd-MMM-yyyy"));
//        Log.e("today_date", "Today's date is : " + getTodayDate("MMMM dd, yyyy"));
//        Log.e("today_date", "Today's date is : " + getTodayDate("dd/MM/yyyy"));
//        Log.e("today_date", "Today's date is : " + getTodayDate("yyyy-MM-dd"));
//        Log.e("today_date", "Today's date is : " + getTodayDate("yyyy/MM/dd HH:mm:ss"));
//        Log.e("today_date", "Today's date is : " + getTodayDate("EEE, MMM d, yyyy"));

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(c);
    }

    public Date getModifiedDate(Date date, String format, int v_day, int v_month, int v_year) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, v_day);
        calendar.add(Calendar.MONTH, v_month);
        calendar.add(Calendar.YEAR, v_year);
        return calendar.getTime();
    }

    public String getModifiedMonthTodayDate(String format, int variation) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, variation);
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(date);
    }

    public String getModifiedYearTodayDate(String format, int variation) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, variation);
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(date);
    }

    public String getModifiedDateTodayDate(String format, int variation) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, variation);
        Date date = calendar.getTime();
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(date);
    }

    public Date getDateFromString(String dateStr, String format) {
        Date date = Calendar.getInstance().getTime();
        try {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public int getTotalNoOfMonths(Calendar c1, Calendar c2) {
        int years = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        int months = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
        return (years * 12 + months);
    }

    public String getStringFromDate(Date date, String format) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    public String getDateFromDatePicker(DatePicker picker, String format) {
        Calendar calendar = Calendar.getInstance();
        int day = picker.getDayOfMonth();
        int month = picker.getMonth();
        int year = picker.getYear();
        calendar.set(year, month, day);
        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(calendar.getTime());
    }

    public String getDesiredDate(String sDate, int type, String format, int cal_type) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(cal_type, type);
        String day = dateFormat.format(calendar.getTime());
        return day;
    }

    @SuppressLint("SetTextI18n")
    public void whats_new_dialog(Activity activity, int pos) {
        final Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.help_dialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = android.R.style.Animation_Dialog;
        TextView content = dialog.findViewById(R.id.help_content);
        TextView app_rate_txt = dialog.findViewById(R.id.app_rate_text);
        TextView btn_great = dialog.findViewById(R.id.btn_ok);

        content.setText(R.string.des_content);
        app_rate_txt.setVisibility(View.VISIBLE);
        btn_great.setText("GREAT!");
        btn_great.setOnClickListener(v -> {

            if (pos == 2)
                new Functions().putSharedPrefsValue(activity, "app_data", "isFirstLogin", "boolean", false);
            dialog.dismiss();
        });

        app_rate_txt.setOnClickListener(v -> activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.lazygeniouz.saveit"))));
        dialog.show();
    }

    public void saveCompanyDetailsToPreferences(Context context, @NonNull FirebaseFirestore db, String comp_path) {
        db.document(comp_path)
                .get()
                .addOnCompleteListener(task1 -> {
                    SimpleDateFormat format = new SimpleDateFormat("dd, MMMM, yyyy");
                    if (task1.isSuccessful()) {
                        String cname = task1.getResult().getString("company_name");
                        String clocation = task1.getResult().getString("company_location");
                        String ccontact = task1.getResult().getString("company_contactnum");
                        Timestamp created_time = task1.getResult().getTimestamp("company_created");
                        Date created_date = created_time.toDate();

                        new Functions().putSharedPrefsValue(context, "user_data", "company_name", "string", cname);
                        new Functions().putSharedPrefsValue(context, "user_data", "company_location", "string", clocation);
                        new Functions().putSharedPrefsValue(context, "user_data", "company_contact", "string", ccontact);
                        new Functions().putSharedPrefsValue(context, "user_data", "company_created", "string", format.format(created_date));
                    } else {
                        new Functions().putSharedPrefsValue(context, "user_data", "company_name", "string", "No Data");
                        new Functions().putSharedPrefsValue(context, "user_data", "company_location", "string", "No Data");
                        new Functions().putSharedPrefsValue(context, "user_data", "company_contact", "string", "No Data");
                        new Functions().putSharedPrefsValue(context, "user_data", "company_created", "string", format.format(new Date()));
                    }
                });
    }

    public int getMonthFromString(String month_str) {
        ArrayList<String> montharr = new ArrayList<>();
        montharr.add("jan");
        montharr.add("feb");
        montharr.add("mar");
        montharr.add("apr");
        montharr.add("may");
        montharr.add("jun");
        montharr.add("jul");
        montharr.add("aug");
        montharr.add("sep");
        montharr.add("oct");
        montharr.add("nov");
        montharr.add("dec");
        return montharr.contains(month_str) ? montharr.indexOf(month_str) : 0;
    }

    public ArrayList<String> getThreeLetterMonths() {
        ArrayList<String> montharr = new ArrayList<>();
        montharr.add("jan");
        montharr.add("feb");
        montharr.add("mar");
        montharr.add("apr");
        montharr.add("may");
        montharr.add("jun");
        montharr.add("jul");
        montharr.add("aug");
        montharr.add("sep");
        montharr.add("oct");
        montharr.add("nov");
        montharr.add("dec");
        return montharr;
    }

    public String getMonthNames(int pos) {
        ArrayList<String> montharr = new ArrayList<>();
        montharr.add("April");
        montharr.add("May");
        montharr.add("June");
        montharr.add("July");
        montharr.add("August");
        montharr.add("September");
        montharr.add("October");
        montharr.add("November");
        montharr.add("December");
        montharr.add("January");
        montharr.add("February");
        montharr.add("March");
        return montharr.get(pos);
    }

    public String getMonthFullName(int pos) {
        ArrayList<String> montharr = new ArrayList<>();
        montharr.add("January");
        montharr.add("February");
        montharr.add("March");
        montharr.add("April");
        montharr.add("May");
        montharr.add("June");
        montharr.add("July");
        montharr.add("August");
        montharr.add("September");
        montharr.add("October");
        montharr.add("November");
        montharr.add("December");
        return montharr.get(pos);
    }

    public int dpToPx(@NonNull Context context, int dp) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void updateButtonStyle(String s, boolean b, @NonNull Button btn) {
        btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(s)));
        btn.setEnabled(b);
    }

    private void smsPermissionRequestResponse(Activity activity, String emp_phoneno, View lay) {
        Dexter.withContext(activity)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(emp_phoneno, null, "Hello, this is WeTrack App. The company, have added you as their manager. Please install WeTrack App and login to your account using your phone number.", null, null);
                        Snackbar.make(lay, "SMS notification has been send to the employee successfully.", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Snackbar.make(lay, "Unable to send SMS to the workers !! Please enable the SMS permission to enable this feature.", Snackbar.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

}
