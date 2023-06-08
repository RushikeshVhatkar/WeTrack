package com.rushikeshsantoshv.wetrack.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.demogorgorn.monthpicker.MonthPickerDialog;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.rushikeshsantoshv.wetrack.Adapters.AnnualReportAdapter;
import com.rushikeshsantoshv.wetrack.DataModels.AnnualReportModel;
import com.rushikeshsantoshv.wetrack.DataModels.Functions;
import com.rushikeshsantoshv.wetrack.R;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnnualReportActivity extends AppCompatActivity {

    TextView annualreport_generatereport_btn;
    RecyclerView annualreport_finalrec;
    ImageButton annualreport_back_btn, annualreport_calender_btn, annualreport_calprev_year, annualreport_calnext_year;
    TextView annualreport_curr_year;
    RelativeLayout annualreport_mainlay;
    LinearLayout annualreport_yearly_rate_lay;
    TextInputLayout annualreport_avg_wages, annualreport_yearly_bonus, annualreport_additional_charges, annualreport_med_allowance;

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String comp_path;
    Long curr_fin_year;
    Long min_fin_year;
    Long max_fin_year;
    Timestamp comp_created_year;
    Long financial_year;
    int mon_size = 0;
    ArrayList<AnnualReportModel> annual_report_arr = new ArrayList<>();

    Intent getData;
    String emp_path, emp_name, emp_contact;

    AnnualReportAdapter annualReportAdapter;
    Functions functions = new Functions();
    File folderDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/WeTrack");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_annual_report);
        functions.coloredStatusBarDarkTextDesign(AnnualReportActivity.this, R.color.maincolor_light, R.color.white);

        annualReportInits();

        if (functions.checkInternetConnection(AnnualReportActivity.this)) {
            db.collection("Companies")
                    .document(db.document(comp_path).getId())
                    .get()
                    .addOnCompleteListener(task -> {
                        comp_created_year = (task.isSuccessful()
                                && task.getResult().getTimestamp("company_created") != null ?
                                task.getResult().getTimestamp("company_created") : Timestamp.now());

                        min_fin_year = Long.parseLong(functions.getStringFromDate(comp_created_year.toDate(), "yyyy"));
                        checkYear(false, annualreport_calprev_year);
                        checkYear(true, annualreport_calnext_year);

                        annualreport_calender_btn.setOnClickListener(v -> {
                            MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(AnnualReportActivity.this,
                                    (month, year) -> {
                                        curr_fin_year = (long) year;
                                        annualreport_curr_year.setText(String.valueOf(curr_fin_year));
                                        checkGovtBaseRateAndOtherProcess();
                                    }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH));

                            builder.setMinYear(Math.toIntExact(min_fin_year))
                                    .setOnYearChangedListener(i -> builder.setActivatedYear(i))
                                    .setMaxYear(Math.toIntExact(max_fin_year))
                                    .showYearOnly()
                                    .setTitle("Select Desired Financial Year")
                                    .build().show();
                        });
                    });

            checkGovtBaseRateAndOtherProcess();
        } else functions.no_internet_dialog(AnnualReportActivity.this, true);

        annualreport_calprev_year.setOnClickListener(v -> {
            annualreport_curr_year.setText(functions.getDesiredDate(annualreport_curr_year.getText().toString(), -1, "yyyy", Calendar.YEAR));
            checkYear(false, annualreport_calprev_year);
            checkYear(true, annualreport_calnext_year);
            checkGovtBaseRateAndOtherProcess();
        });

        annualreport_calnext_year.setOnClickListener(v -> {
            annualreport_curr_year.setText(functions.getDesiredDate(annualreport_curr_year.getText().toString(), 1, "yyyy", Calendar.YEAR));
            checkYear(false, annualreport_calprev_year);
            checkYear(true, annualreport_calnext_year);
            checkGovtBaseRateAndOtherProcess();
        });

        annualreport_back_btn.setOnClickListener(v-> finish());

        annualreport_generatereport_btn.setOnClickListener(v -> {
            Dexter.withContext(AnnualReportActivity.this)
                    .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                    .withListener(new MultiplePermissionsListener() {

                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                            try {
                                if (functions.checkInternetConnection(AnnualReportActivity.this)) {
                                    if (!folderDir.exists()) {
                                        folderDir.mkdirs();
                                    }

                                    financial_year = annualreport_curr_year.getText().equals(functions.getTodayDate("yyyy")) ?
                                            (functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                                                    Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                                                    Long.parseLong(functions.getTodayDate("yyyy"))) : Long.parseLong(annualreport_curr_year.getText().toString());

                                    String comp_name = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_name",
                                            "string", "");
                                    //File file = new File(folderDir, "Annual Report_" + comp_name + "_" + financial_year + "_" + emp_name + ".xls");

                                    File file= new File(folderDir, "sample_report_file.xls");

                                    Workbook workbook = new XSSFWorkbook();
                                    Sheet sheet = workbook.createSheet();

                                    // Employee Name Row
                                    CellStyle ename_style = workbook.createCellStyle();
                                    Font emp_font = workbook.createFont();
                                    emp_font.setBold(true);
                                    emp_font.setFontHeightInPoints((short) 14);
                                    ename_style.setFont(emp_font);

                                    Row emp_row = sheet.createRow(0);
                                    Cell emp_cell = emp_row.createCell(0);
                                    emp_cell.setCellValue(emp_name);
                                    emp_cell.setCellStyle(ename_style);

                                    // Heading Rows
                                    CellStyle heading_style = workbook.createCellStyle();
                                    heading_style.setAlignment(HorizontalAlignment.CENTER);
                                    heading_style.setVerticalAlignment(VerticalAlignment.CENTER);
                                    Font heading_font = workbook.createFont();
                                    heading_font.setBold(true);
                                    heading_font.setFontHeightInPoints((short) 12);
                                    heading_style.setFont(heading_font);

                                    Row heading_row = sheet.createRow(1);
                                    Cell heading_cell0 = heading_row.createCell(0);
                                    Cell heading_cell1 = heading_row.createCell(1);
                                    Cell heading_cell2 = heading_row.createCell(2);
                                    Cell heading_cell3 = heading_row.createCell(3);
                                    Cell heading_cell4 = heading_row.createCell(4);
                                    Cell heading_cell5 = heading_row.createCell(5);
                                    Cell heading_cell6 = heading_row.createCell(6);
                                    Cell heading_cell7 = heading_row.createCell(7);
                                    Cell heading_cell8 = heading_row.createCell(8);
                                    Cell heading_cell9 = heading_row.createCell(9);
                                    Cell heading_cell10 = heading_row.createCell(10);
                                    Cell heading_cell11 = heading_row.createCell(11);
                                    Cell heading_cell12 = heading_row.createCell(12);
                                    Cell heading_cell13 = heading_row.createCell(13);
                                    Cell heading_cell14 = heading_row.createCell(14);
                                    Cell heading_cell15 = heading_row.createCell(15);
                                    Cell heading_cell16 = heading_row.createCell(16);
                                    Cell heading_cell17 = heading_row.createCell(17);
                                    Cell heading_cell18 = heading_row.createCell(18);

                                    heading_cell0.setCellStyle(heading_style);
                                    heading_cell1.setCellStyle(heading_style);
                                    heading_cell2.setCellStyle(heading_style);
                                    heading_cell3.setCellStyle(heading_style);
                                    heading_cell4.setCellStyle(heading_style);
                                    heading_cell5.setCellStyle(heading_style);
                                    heading_cell6.setCellStyle(heading_style);
                                    heading_cell7.setCellStyle(heading_style);
                                    heading_cell8.setCellStyle(heading_style);
                                    heading_cell9.setCellStyle(heading_style);
                                    heading_cell10.setCellStyle(heading_style);
                                    heading_cell11.setCellStyle(heading_style);
                                    heading_cell12.setCellStyle(heading_style);
                                    heading_cell13.setCellStyle(heading_style);
                                    heading_cell14.setCellStyle(heading_style);
                                    heading_cell15.setCellStyle(heading_style);
                                    heading_cell16.setCellStyle(heading_style);
                                    heading_cell17.setCellStyle(heading_style);
                                    heading_cell18.setCellStyle(heading_style);

                                    heading_cell0.setCellValue("Month");
                                    heading_cell1.setCellValue("Worked \nDays");
                                    heading_cell2.setCellValue("Company \nBase Rate");
                                    heading_cell3.setCellValue("Monthly \nWages");
                                    heading_cell4.setCellValue("Adv. Taken");
                                    heading_cell5.setCellValue("Adv. Paid");
                                    heading_cell6.setCellValue("Adv. Bal");
                                    heading_cell7.setCellValue("Govt \nBase Rate");
                                    heading_cell8.setCellValue("Actual \nMonthly Wg.");
                                    heading_cell9.setCellValue("Balance");
                                    heading_cell10.setCellValue("Wage Arrs");
                                    heading_cell11.setCellValue("Wg. Arrs \nPaid");
                                    heading_cell12.setCellValue("Wg. Arrs \nBal.");
                                    heading_cell13.setCellValue("L/W\n20:1");
                                    heading_cell14.setCellValue("Bonus\n14%");
                                    heading_cell15.setCellValue("NA Fe 13");
                                    heading_cell16.setCellValue("Kambili");
                                    heading_cell17.setCellValue("MED-6");
                                    heading_cell18.setCellValue("Net \nBalance");

                                    // Content Rows
                                    CellStyle content_style = workbook.createCellStyle();
                                    content_style.setAlignment(HorizontalAlignment.CENTER);
                                    content_style.setVerticalAlignment(VerticalAlignment.CENTER);
                                    Font content_font = workbook.createFont();
                                    content_font.setFontHeightInPoints((short) 10);
                                    content_style.setFont(content_font);

                                    double govt_wag_arrears_val = 0;
                                    double govt_wag_arrears= 0;
                                    int total_working_days = 0;
                                    double total_comp_wages = 0;
                                    double final_adv_balance = 0;
                                    double total_actual_wages = 0;
                                    double total_govt_wages_bal = 0;

                                    for (int i = 0; i < annual_report_arr.size(); i++) {

                                        double comp_wages_amt = (annual_report_arr.get(i).getCompBaseRate() * annual_report_arr.get(i).getFullPresentCount()) +
                                                (((annual_report_arr.get(i).getCompBaseRate()) / 2) * annual_report_arr.get(i).getHalfPresentCount());

                                        double govt_wages_amt = (annual_report_arr.get(i).getGovtBaseRate() * annual_report_arr.get(i).getFullPresentCount()) +
                                                (((annual_report_arr.get(i).getGovtBaseRate()) / 2) * annual_report_arr.get(i).getHalfPresentCount());

                                        govt_wag_arrears_val += govt_wages_amt - comp_wages_amt;
                                        govt_wag_arrears_val = (i + 1) % 3 == 0 ? 0 : govt_wag_arrears_val;
                                        govt_wag_arrears = (i + 1) % 3 == 0 ? govt_wag_arrears_val : 0;
                                        total_working_days += annual_report_arr.get(i).getDaysWorked();
                                        total_comp_wages += comp_wages_amt;
                                        total_actual_wages += govt_wages_amt;
                                        final_adv_balance = annual_report_arr.get(i).getLoanBalance();
                                        total_govt_wages_bal += govt_wages_amt - comp_wages_amt;


                                        Row content_row = sheet.createRow(i + 2);
                                        Cell content_cell0 = content_row.createCell(0);
                                        Cell content_cell1 = content_row.createCell(1);
                                        Cell content_cell2 = content_row.createCell(2);
                                        Cell content_cell3 = content_row.createCell(3);
                                        Cell content_cell4 = content_row.createCell(4);
                                        Cell content_cell5 = content_row.createCell(5);
                                        Cell content_cell6 = content_row.createCell(6);
                                        Cell content_cell7 = content_row.createCell(7);
                                        Cell content_cell8 = content_row.createCell(8);
                                        Cell content_cell9 = content_row.createCell(9);
                                        Cell content_cell10 = content_row.createCell(10);
                                        Cell content_cell11 = content_row.createCell(11);
                                        Cell content_cell12 = content_row.createCell(12);
                                        Cell content_cell13 = content_row.createCell(13);
                                        Cell content_cell14 = content_row.createCell(14);
                                        Cell content_cell15 = content_row.createCell(15);
                                        Cell content_cell16 = content_row.createCell(16);
                                        Cell content_cell17 = content_row.createCell(17);
                                        Cell content_cell18 = content_row.createCell(18);

                                        content_cell0.setCellStyle(content_style);
                                        content_cell1.setCellStyle(content_style);
                                        content_cell2.setCellStyle(content_style);
                                        content_cell3.setCellStyle(content_style);
                                        content_cell4.setCellStyle(content_style);
                                        content_cell5.setCellStyle(content_style);
                                        content_cell6.setCellStyle(content_style);
                                        content_cell7.setCellStyle(content_style);
                                        content_cell8.setCellStyle(content_style);
                                        content_cell9.setCellStyle(content_style);
                                        content_cell10.setCellStyle(content_style);
                                        content_cell11.setCellStyle(content_style);
                                        content_cell12.setCellStyle(content_style);
                                        content_cell13.setCellStyle(content_style);
                                        content_cell14.setCellStyle(content_style);
                                        content_cell15.setCellStyle(content_style);
                                        content_cell16.setCellStyle(content_style);
                                        content_cell17.setCellStyle(content_style);
                                        content_cell18.setCellStyle(content_style);

                                        content_cell0.setCellValue(annual_report_arr.get(i).getCurrMonth());
                                        content_cell1.setCellValue(annual_report_arr.get(i).getDaysWorked());
                                        content_cell2.setCellValue(annual_report_arr.get(i).getCompBaseRate());
                                        content_cell3.setCellValue(comp_wages_amt);
                                        content_cell4.setCellValue(annual_report_arr.get(i).getAdvanceTaken());
                                        content_cell5.setCellValue(annual_report_arr.get(i).getAdvancePaid());
                                        content_cell6.setCellValue(annual_report_arr.get(i).getLoanBalance());
                                        content_cell7.setCellValue(annual_report_arr.get(i).getGovtBaseRate());
                                        content_cell8.setCellValue(govt_wages_amt);
                                        content_cell9.setCellValue(govt_wages_amt - comp_wages_amt);
                                        content_cell10.setCellValue(govt_wag_arrears);
                                        content_cell11.setCellValue(0);
                                        content_cell12.setCellValue(govt_wag_arrears);
                                        content_cell13.setCellValue("-");
                                        content_cell14.setCellValue("-");
                                        content_cell15.setCellValue("-");
                                        content_cell16.setCellValue("-");
                                        content_cell17.setCellValue("-");
                                        content_cell18.setCellValue("-");

                                    }

                                    Row total_row = sheet.createRow(annual_report_arr.size() + 2);
                                    Cell total_cell0 = total_row.createCell(0);
                                    Cell total_cell1 = total_row.createCell(1);
                                    Cell total_cell2 = total_row.createCell(2);
                                    Cell total_cell3 = total_row.createCell(3);
                                    Cell total_cell4 = total_row.createCell(4);
                                    Cell total_cell5 = total_row.createCell(5);
                                    Cell total_cell6 = total_row.createCell(6);
                                    Cell total_cell7 = total_row.createCell(7);
                                    Cell total_cell8 = total_row.createCell(8);
                                    Cell total_cell9 = total_row.createCell(9);
                                    Cell total_cell10 = total_row.createCell(10);
                                    Cell total_cell11 = total_row.createCell(11);
                                    Cell total_cell12 = total_row.createCell(12);
                                    Cell total_cell13 = total_row.createCell(13);
                                    Cell total_cell14 = total_row.createCell(14);
                                    Cell total_cell15 = total_row.createCell(15);
                                    Cell total_cell16 = total_row.createCell(16);
                                    Cell total_cell17 = total_row.createCell(17);
                                    Cell total_cell18 = total_row.createCell(18);

                                    total_cell0.setCellStyle(heading_style);
                                    total_cell1.setCellStyle(heading_style);
                                    total_cell2.setCellStyle(heading_style);
                                    total_cell3.setCellStyle(heading_style);
                                    total_cell4.setCellStyle(heading_style);
                                    total_cell5.setCellStyle(heading_style);
                                    total_cell6.setCellStyle(heading_style);
                                    total_cell7.setCellStyle(heading_style);
                                    total_cell8.setCellStyle(heading_style);
                                    total_cell9.setCellStyle(heading_style);
                                    total_cell10.setCellStyle(heading_style);
                                    total_cell11.setCellStyle(heading_style);
                                    total_cell12.setCellStyle(heading_style);
                                    total_cell13.setCellStyle(heading_style);
                                    total_cell14.setCellStyle(heading_style);
                                    total_cell15.setCellStyle(heading_style);
                                    total_cell16.setCellStyle(heading_style);
                                    total_cell17.setCellStyle(heading_style);
                                    total_cell18.setCellStyle(heading_style);

                                    total_cell0.setCellValue("");
                                    total_cell1.setCellValue(total_working_days);
                                    total_cell2.setCellValue("");
                                    total_cell3.setCellValue(total_comp_wages);
                                    total_cell4.setCellValue("");
                                    total_cell5.setCellValue("");
                                    total_cell6.setCellValue(final_adv_balance);
                                    total_cell7.setCellValue("");
                                    total_cell8.setCellValue(total_actual_wages);
                                    total_cell9.setCellValue(total_govt_wages_bal);
                                    total_cell10.setCellValue(total_govt_wages_bal);
                                    total_cell11.setCellValue(0);
                                    total_cell12.setCellValue(total_govt_wages_bal);
                                    total_cell13.setCellValue("");
                                    total_cell14.setCellValue("");
                                    total_cell15.setCellValue("");
                                    total_cell16.setCellValue("");
                                    total_cell17.setCellValue("");
                                    total_cell18.setCellValue("");

                                    for(int i=0; i < 18; i++){
                                        int rowcount=0;
                                        for (Row row : sheet) {
                                            Cell cell = row.getCell(i);
                                            int contentLength = 1;
                                            if (cell != null) {
                                                if (cell.getCellType() == CellType.STRING) {
                                                    contentLength = cell.getStringCellValue().length();
                                                } else if (cell.getCellType() == CellType.NUMERIC) {
                                                    double numericValue = cell.getNumericCellValue();
                                                    contentLength = String.valueOf(numericValue).length();
                                                }
                                                int currentWidth = sheet.getColumnWidth(i);
                                                int desiredWidth = (contentLength + 2) * 256;
                                                if (desiredWidth > currentWidth) {
                                                    sheet.setColumnWidth(i, desiredWidth);
                                                }
                                            }
                                            rowcount++;
                                        }
                                    }

                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                                    workbook.write(fileOutputStream);

                                    fileOutputStream.flush();
                                    fileOutputStream.close();
                                    Snackbar.make(annualreport_mainlay, "Annual Report of " + financial_year + " Financial Year Created Successfully.", Snackbar.LENGTH_SHORT).show();
                                } else
                                    functions.no_internet_dialog(AnnualReportActivity.this, false);
                            } catch (Exception e) {
                                Log.e("file_error", "The file error is : " + e);
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                            permissionToken.continuePermissionRequest();
                        }
                    }).check();
        });
    }

    private void getTheMonthsAndCallLoad() {

        financial_year = annualreport_curr_year.getText().equals(functions.getTodayDate("yyyy")) ?
                (functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                        Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                        Long.parseLong(functions.getTodayDate("yyyy"))) : Long.parseLong(annualreport_curr_year.getText().toString());

        // Long financial_year = 2022L;

        Dialog loading_dialog = functions.createDialogBox(AnnualReportActivity.this, R.layout.loading_dialog, false);
        loading_dialog.show();

        Log.e("user_reference", emp_path);

        Calendar start_calendar = Calendar.getInstance();
        Calendar end_calender = Calendar.getInstance();
        start_calendar.set(Calendar.YEAR, Math.toIntExact(financial_year));
        start_calendar.set(Calendar.MONTH, 3);
        start_calendar.set(Calendar.DAY_OF_MONTH, 1);

        if (financial_year == Long.parseLong(functions.getTodayDate("yyyy")) ||
                financial_year == Long.parseLong(functions.getModifiedYearTodayDate("yyyy", -1))) {
            end_calender.set(Calendar.YEAR, financial_year == Long.parseLong(functions.getTodayDate("yyyy")) ?
                    Math.toIntExact(financial_year) : (Math.toIntExact(financial_year) + 1));
            end_calender.set(Calendar.MONTH, financial_year == Long.parseLong(functions.getTodayDate("yyyy")) ?
                    (Integer.parseInt(functions.getTodayDate("MM")) - 2) : 2);
            end_calender.set(Calendar.DAY_OF_MONTH, end_calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        } else {
            end_calender.set(Calendar.YEAR, Math.toIntExact(financial_year) + 1);
            end_calender.set(Calendar.MONTH, 2);
            end_calender.set(Calendar.DAY_OF_MONTH, end_calender.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        int month_size = functions.getTotalNoOfMonths(start_calendar, end_calender);

        loadAnnualReport(loading_dialog, start_calendar, financial_year, month_size);
    }

    private void annualReportInits() {
        annualreport_mainlay = findViewById(R.id.annualreport_mainlay);
        annualreport_generatereport_btn = findViewById(R.id.annualreport_generatereport_btn);
        annualreport_finalrec = findViewById(R.id.annualreport_finalrec);
        annualreport_back_btn = findViewById(R.id.annualreport_back_btn);
        annualreport_calender_btn = findViewById(R.id.annualreport_calender_btn);
        annualreport_calprev_year = findViewById(R.id.annualreport_calprev_year);
        annualreport_calnext_year = findViewById(R.id.annualreport_calnext_year);
        annualreport_curr_year = findViewById(R.id.annualreport_curr_year);

        annualreport_yearly_rate_lay = findViewById(R.id.annualreport_yearly_rate_lay);
        annualreport_avg_wages = findViewById(R.id.annualreport_avg_wages);
        annualreport_yearly_bonus = findViewById(R.id.annualreport_yearly_bonus);
        annualreport_additional_charges = findViewById(R.id.annualreport_additional_charges);
        annualreport_med_allowance = findViewById(R.id.annualreport_med_allowance);

        getData = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        comp_path = functions.getSharedPrefsValue(getApplicationContext(), "user_data", "company_path", "string", null);
        curr_fin_year = functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                Long.parseLong(functions.getTodayDate("yyyy"));

        annualreport_curr_year.setText(String.valueOf(curr_fin_year));
        max_fin_year = curr_fin_year;

        emp_path = (getData != null && getData.getStringExtra("emp_reference_id") != null
                && !getData.getStringExtra("emp_reference_id").equals("")) ?
                getData.getStringExtra("emp_reference_id") : "Employees/sampleuser";

        emp_name = (getData != null && getData.getStringExtra("emp_name") != null
                && !getData.getStringExtra("emp_name").equals("")) ?
                getData.getStringExtra("emp_name") : "User";

        emp_contact = (getData != null && getData.getStringExtra("emp_contact") != null
                && !getData.getStringExtra("emp_contact").equals("")) ?
                getData.getStringExtra("emp_contact") : "+919876543210";

        financial_year = annualreport_curr_year.getText().equals(functions.getTodayDate("yyyy")) ?
                (functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                        Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                        Long.parseLong(functions.getTodayDate("yyyy"))) : Long.parseLong(annualreport_curr_year.getText().toString());

        annualreport_calprev_year.setEnabled(false);
        annualreport_calprev_year.setClickable(false);
        annualreport_calprev_year.setColorFilter(Color.parseColor("#CDCDCD"));

        annualreport_calnext_year.setEnabled(false);
        annualreport_calnext_year.setClickable(false);
        annualreport_calnext_year.setColorFilter(Color.parseColor("#CDCDCD"));

        annualreport_yearly_rate_lay.setVisibility(View.GONE);
        annualreport_generatereport_btn.setVisibility(View.GONE);
    }

    public void checkYear(boolean isNext, @NonNull ImageButton btn) {
        String curr_year = annualreport_curr_year.getText().toString();
        long curr_year_val = Long.parseLong(!curr_year.equals("") ? curr_year : functions.getTodayDate("yyyy"));
        boolean check = isNext ? curr_year_val < max_fin_year : curr_year_val > min_fin_year;
        btn.setEnabled(check);
        btn.setClickable(check);
        btn.setColorFilter(Color.parseColor(check ? "#494949" : "#CDCDCD"));
    }

    private void checkGovtBaseRateAndOtherProcess() {
        annualreport_finalrec.setVisibility(View.GONE);
        annualreport_generatereport_btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#DADADA")));
        annualreport_generatereport_btn.setEnabled(false);
        annualreport_generatereport_btn.setClickable(false);
        financial_year = annualreport_curr_year.getText().equals(functions.getTodayDate("yyyy")) ?
                (functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) <= 2 ?
                        Long.parseLong(functions.getModifiedMonthTodayDate("yyyy", -1)) :
                        Long.parseLong(functions.getTodayDate("yyyy"))) : Long.parseLong(annualreport_curr_year.getText().toString());

        // Long financial_year = 2022L;

        int curr_month = annualreport_curr_year.getText().equals(functions.getTodayDate("yyyy")) ?
                functions.getMonthList().indexOf(functions.getTodayDate("MMM").toLowerCase()) :
                functions.getMonthList().indexOf("mar");

        mon_size = curr_month;
        // int curr_month = functions.getMonthList().indexOf("mar");
        ArrayList<String> notadded_rates = new ArrayList<>();

        db.collection("GovtBaseRate")
                .whereEqualTo("company_reference", db.document(comp_path))
                .whereEqualTo("financial_year", financial_year)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().size() > 0) {
                            DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                            boolean check_rate = true;
                            for (int i = 0; i <= curr_month; i++) {
                                if (!doc.contains(functions.getMonthList().get(i) + "_rate")) {
                                    notadded_rates.add(functions.getMonthList().get(i));
                                    check_rate = false;
                                }
                            }

                            if (!check_rate) {
                                addGovtRateToDB(financial_year, curr_month, notadded_rates, doc, false);
                            } else {
                                getTheMonthsAndCallLoad();
                            }

                        } else {
                            Log.e("firebase_error", "The error is : no size");
                            for (int i = 0; i <= curr_month; i++) {
                                notadded_rates.add(functions.getMonthList().get(i));
                            }
                            for (String item : notadded_rates) {
                                Log.e("month_item", "The item is : " + item + " and current month is : " + curr_month);
                            }
                            addGovtRateToDB(financial_year, curr_month, notadded_rates, null, true);
                        }
                    } else {
                        String msg = task.getException() != null && task.getException().getLocalizedMessage() != null ? task.getException().getLocalizedMessage() : "No Error ...";
                        Log.e("firebase_error", "The error is : " + msg);
                    }
                });

    }

    private interface AttendancesCallBack<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }

    private interface PaymentsCallBack<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }

    private interface LoansCallBack<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }

    private interface CompanyRateBaseCallBack<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }

    private interface GovtRateBaseCallBack<T> {
        void onSuccess(T result);

        void onFailure(Exception e);
    }

    private interface LoopCallBack<T> {
        void onSuccess(T result);

        void onFail(T fail);
    }

    private void loadAnnualReport(Dialog loading_dialog, Calendar start_calendar, Long financial_year, int month_size) {
        if (annual_report_arr.size() > 0) annual_report_arr.clear();
        for (int i = 0; i <= month_size; i++) {
            annual_report_arr.add(i, new AnnualReportModel());
        }
        for (int i = 0; i <= month_size; i++) {
            int finalI = i;
            LoadLoopAndReadData(loading_dialog, start_calendar, financial_year, i, new LoopCallBack<Boolean>() {
                @Override
                public void onSuccess(Boolean result) {
                    if (finalI == month_size) {
                        annualReportAdapter = new AnnualReportAdapter(getApplicationContext(), annual_report_arr);
                        annualreport_finalrec.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        annualreport_finalrec.setAdapter(annualReportAdapter);
                        annualReportAdapter.notifyDataSetChanged();
                        annualreport_yearly_rate_lay.setVisibility(month_size >= 11 ? View.VISIBLE : View.GONE);
                        annualreport_generatereport_btn.setVisibility(month_size >= 11 ? View.VISIBLE : View.GONE);
                        annualreport_finalrec.setVisibility(View.VISIBLE);
                        annualreport_generatereport_btn.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getApplicationContext(), R.color.maincolor)));
                        annualreport_generatereport_btn.setEnabled(true);
                        annualreport_generatereport_btn.setClickable(true);
                        loading_dialog.dismiss();
                    }
                }

                @Override
                public void onFail(Boolean fail) {
                }
            });
        }
    }

    private void LoadLoopAndReadData(Dialog loading_dialog, Calendar start_calendar, Long financial_year, int i, LoopCallBack<Boolean> callBack) {
        int looped_year = (i >= 9) ? start_calendar.get(Calendar.YEAR) + 1 : start_calendar.get(Calendar.YEAR);
        Calendar c1 = Calendar.getInstance();
        c1.set(Calendar.DAY_OF_MONTH, 1);
        c1.set(Calendar.MONTH, functions.getThreeLetterMonths().indexOf(functions.getMonthList().get(i)));
        c1.set(Calendar.YEAR, looped_year);
        Timestamp t1 = new Timestamp(functions.getDateFromString(functions.getStringFromDate(c1.getTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));

        Calendar c2 = Calendar.getInstance();
        c2.set(Calendar.DAY_OF_MONTH, 1);
        c2.set(Calendar.MONTH, c1.get(Calendar.MONTH) + 1);
        c2.set(Calendar.YEAR, c1.get(Calendar.YEAR));
        Timestamp t2 = new Timestamp(functions.getDateFromString(functions.getStringFromDate(c2.getTime(), "dd/MM/yyyy"), "dd/MM/yyyy"));

        LoadAttendanceData(t1, t2, new AttendancesCallBack<List<DocumentSnapshot>>() {
            @Override
            public void onSuccess(List<DocumentSnapshot> result1) {
                if (result1.size() > 0) {
                    long p_count = 0, hd_count = 0, days_worked = 0;
                    String curr_month = functions.getMonthFullName(functions.getThreeLetterMonths().indexOf(functions.getMonthList().get(i))) + ", " + looped_year;
                    for (DocumentSnapshot attendance : result1) {
                        p_count += (attendance.getLong("attend_status") == 1) ? 1 : 0;
                        hd_count += (attendance.getLong("attend_status") == 2) ? 1 : 0;
                    }
                    days_worked = p_count + hd_count;

                    long finalP_count = p_count;
                    long finalHd_count = hd_count;
                    long finalDays_worked = days_worked;

                    LoadPaymentData(t1, t2, new PaymentsCallBack<List<DocumentSnapshot>>() {
                        @Override
                        public void onSuccess(List<DocumentSnapshot> result2) {
                            if (result2.size() > 0) {

                                double comp_wage_paid = 0;
                                double comp_loan_amt = 0;
                                double comp_wage_total = 0;

                                for (DocumentSnapshot doc : result2) {
                                    comp_loan_amt += doc.getDouble("pemp_loan_paid");
                                    comp_wage_total += doc.getDouble("pemp_wage_total");
                                    comp_wage_paid += doc.getDouble("pemp_wage_paid");
                                }
                                double finalComp_wage_paid = comp_wage_paid;
                                double finalComp_wage_total = comp_wage_total;
                                double finalComp_loan_amt = comp_loan_amt;

                                double add_arrears = result2.get(result2.size() - 1).getDouble("pemp_sal_arrears");

                               /* Log.e("val","The values are : \nThe Payment Size is : "+result2.size()+"\nThe i value is : "+i
                                        +"\nThe comp_loan_amt is : "+comp_loan_amt+
                                        " & \ncomp_wage_total is : "+comp_wage_total+" & \ncomp_wage_paid is : "+
                                        comp_wage_paid+" & \nadd_arrears is : "+add_arrears);*/

                                LoadLoanData(t1, t2, new LoansCallBack<List<DocumentSnapshot>>() {
                                    @Override
                                    public void onSuccess(List<DocumentSnapshot> result3) {
                                        double advance_taken = 0, advance_paid = 0;
                                        if (result3.size() > 0) {
                                            for (DocumentSnapshot doc : result3) {
                                                advance_taken += !doc.getBoolean("loan_pay_status") ? doc.getDouble("loan_amount") : 0;
                                                advance_paid += doc.getBoolean("loan_pay_status") ? doc.getDouble("loan_amount") : 0;
                                            }
                                        }
                                        else {
                                            advance_taken = 0;
                                            advance_paid = 0;
                                        }

                                        double finalAdvance_taken = advance_taken;
                                        double finalAdvance_paid = advance_paid;

                                        LoadGovtBaseRateData(financial_year, new GovtRateBaseCallBack<List<DocumentSnapshot>>() {
                                            @Override
                                            public void onSuccess(List<DocumentSnapshot> result4) {
                                                if (result4.size() > 0) {
                                                    String month_field_name = functions.getMonthList().get(i) + "_rate";
                                                    double govt_base_rate = result4.get(0).getDouble(month_field_name).doubleValue();

                                                    LoadCompanyBaseRateData(financial_year, new CompanyRateBaseCallBack<List<DocumentSnapshot>>() {
                                                        @Override
                                                        public void onSuccess(List<DocumentSnapshot> result5) {
                                                            if (result5.size() > 0) {
                                                                double comp_base_rate = result5.get(0).getDouble(month_field_name).doubleValue();
                                                                annual_report_arr.set(i, new AnnualReportModel(curr_month,
                                                                        Math.toIntExact(finalDays_worked), Math.toIntExact(finalP_count),
                                                                        Math.toIntExact(finalHd_count),
                                                                        govt_base_rate, comp_base_rate, finalComp_wage_paid,
                                                                        finalComp_wage_total, finalComp_loan_amt,
                                                                        finalAdvance_taken, finalAdvance_paid,
                                                                        0, 0, add_arrears));

                                                                callBack.onSuccess(true);
                                                            }
                                                            else {
                                                                Snackbar.make(annualreport_mainlay, "No Sufficient Data to analysis the annual report !! Please come back later after the data is enough", Snackbar.LENGTH_LONG).show();
                                                                Log.e("res_error", "05 Array No Size");
                                                                loading_dialog.cancel();
                                                                new Handler().postDelayed(() -> finish(),2000);

                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Exception e) {
                                                            String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error !!";
                                                            Log.e("firebase_error", "05 Error is : " + msg);
                                                            loading_dialog.cancel();
                                                        }
                                                    });
                                                }
                                                else {
                                                    Snackbar.make(annualreport_mainlay, "No Sufficient Data to analysis the annual report !! Please come back later after the data is enough", Snackbar.LENGTH_LONG).show();
                                                    Log.e("res_error", "04 Array No Size");
                                                    loading_dialog.cancel();
                                                    new Handler().postDelayed(() -> finish(),2000);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Exception e) {
                                                String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error !!";
                                                Log.e("firebase_error", "03 Error is : " + msg);
                                                loading_dialog.cancel();
                                            }
                                        });
                                    }

                                    @Override
                                    public void onFailure(Exception e) {
                                        String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error !!";
                                        Log.e("firebase_error", "04 Error is : " + msg);
                                        loading_dialog.cancel();
                                    }
                                });
                            }
                            else {
                                Snackbar.make(annualreport_mainlay, "No Sufficient Data to analysis the annual report !! Please come back later after the data is enough", Snackbar.LENGTH_LONG).show();
                                Log.e("res_error", "02 Array No Size");
                                loading_dialog.cancel();
                                new Handler().postDelayed(() -> finish(),2000);
                            }
                        }

                        @Override
                        public void onFailure(Exception e) {
                            String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error !!";
                            Log.e("firebase_error", "02 Error is : " + msg);
                            loading_dialog.cancel();
                        }
                    });
                }
                else {
                    Snackbar.make(annualreport_mainlay, "No Sufficient Data to analysis the annual report !! Please come back later after the data is enough", Snackbar.LENGTH_LONG).show();
                    Log.e("res_error", "01 Array No Size");
                    loading_dialog.cancel();
                    new Handler().postDelayed(() -> finish(),2000);
                }
            }

            @Override
            public void onFailure(Exception e) {
                String msg = e.getLocalizedMessage() != null ? e.getLocalizedMessage() : "No Error !!";
                Log.e("firebase_error", "01 Error is : " + msg);
                loading_dialog.cancel();
            }
        });
    }

    private void LoadCompanyBaseRateData(Long financial_year, CompanyRateBaseCallBack<List<DocumentSnapshot>> callBack) {
        Log.e("financial_year", "The financial year is : "+financial_year);
        db.collection("CompanyBaseRate")
                .whereEqualTo("company_reference", db.document(comp_path))
                .whereEqualTo("financial_year", financial_year)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(task.getResult().getDocuments());
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private void LoadAttendanceData(Timestamp t1, Timestamp t2, AttendancesCallBack<List<DocumentSnapshot>> callBack) {
        Log.e("ts_check", "Start - "+t1.toDate()+" & End - "+t2.toDate());
        db.collection("Attendances")
                .orderBy("attend_date", Query.Direction.DESCENDING)
                .whereEqualTo("attend_emp_reference", db.document(emp_path))
                .whereGreaterThanOrEqualTo("attend_date", t1)
                .whereLessThan("attend_date", t2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(task.getResult().getDocuments());
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private void LoadPaymentData(Timestamp t1, Timestamp t2, PaymentsCallBack<List<DocumentSnapshot>> callBack) {
        db.collection("Payments")
                .orderBy("pemp_timestamp", Query.Direction.ASCENDING)
                .whereEqualTo("pemp_emp_reference", db.document(emp_path))
                .whereGreaterThanOrEqualTo("pemp_timestamp", t1)
                .whereLessThan("pemp_timestamp", t2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(task.getResult().getDocuments());
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private void LoadLoanData(Timestamp t1, Timestamp t2, LoansCallBack<List<DocumentSnapshot>> callBack) {
        db.collection("Loans")
                .orderBy("loan_timestamp", Query.Direction.ASCENDING)
                .whereEqualTo("loan_emp_reference", db.document(emp_path))
                .whereGreaterThanOrEqualTo("loan_timestamp", t1)
                .whereLessThanOrEqualTo("loan_timestamp", t2)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(task.getResult().getDocuments());
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private void LoadGovtBaseRateData(Long financial_year, GovtRateBaseCallBack<List<DocumentSnapshot>> callBack) {
        db.collection("GovtBaseRate")
                .whereEqualTo("company_reference", db.document(comp_path))
                .whereEqualTo("financial_year", financial_year)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callBack.onSuccess(task.getResult().getDocuments());
                    } else {
                        callBack.onFailure(task.getException());
                    }
                });
    }

    private void addGovtRateToDB(Long financial_year, int curr_month, ArrayList<String> notadded_rates, DocumentSnapshot doc, boolean isadd) {
        BottomSheetDialog dialog = new BottomSheetDialog(AnnualReportActivity.this);
        dialog.setContentView(R.layout.add_company_rate_btmdialog);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        dialog.setOnCancelListener(dialogInterface -> finish());

        TextInputLayout acr_rate1 = dialog.findViewById(R.id.acr_rate1);
        TextInputLayout acr_rate2 = dialog.findViewById(R.id.acr_rate2);
        TextInputLayout acr_rate3 = dialog.findViewById(R.id.acr_rate3);
        TextInputLayout acr_rate4 = dialog.findViewById(R.id.acr_rate4);
        TextView acr_add_btn = dialog.findViewById(R.id.acr_add_btn);

        setVisibilityOfTextInputLayout("apr", "jul", acr_rate1, curr_month, notadded_rates);
        setVisibilityOfTextInputLayout("jul", "apr", acr_rate2, curr_month, notadded_rates);
        setVisibilityOfTextInputLayout("oct", "jan", acr_rate3, curr_month, notadded_rates);
        setVisibilityOfTextInputLayout("jan", "oct", acr_rate4, curr_month, notadded_rates);

        String empty_msg = "Enter a base rate !!";
        String nozero_msg = "Please enter a value greater than zero !!";
        acr_add_btn.setOnClickListener(v1 -> {
            String r1_txt = acr_rate1.getEditText().getText().toString();
            String r2_txt = acr_rate2.getEditText().getText().toString();
            String r3_txt = acr_rate3.getEditText().getText().toString();
            String r4_txt = acr_rate4.getEditText().getText().toString();

            if (acr_rate1.getVisibility() == View.VISIBLE && r1_txt.trim().length() <= 0) {
                TextInputLayout[] list = {acr_rate2, acr_rate3, acr_rate4};
                ArrayList<TextInputLayout> arr = new ArrayList<>();
                arr.addAll(Arrays.asList(list));
                updateCompanyRateEdittextErrorEnabled(acr_rate1, arr, empty_msg);
            } else if (acr_rate2.getVisibility() == View.VISIBLE && r2_txt.trim().length() <= 0) {
                TextInputLayout[] list = {acr_rate1, acr_rate3, acr_rate4};
                ArrayList<TextInputLayout> arr = new ArrayList<>();
                arr.addAll(Arrays.asList(list));
                updateCompanyRateEdittextErrorEnabled(acr_rate2, arr, empty_msg);
            } else if (acr_rate3.getVisibility() == View.VISIBLE && r3_txt.trim().length() <= 0) {
                TextInputLayout[] list = {acr_rate1, acr_rate2, acr_rate4};
                ArrayList<TextInputLayout> arr = new ArrayList<>();
                arr.addAll(Arrays.asList(list));
                updateCompanyRateEdittextErrorEnabled(acr_rate3, arr, empty_msg);
            } else if (acr_rate4.getVisibility() == View.VISIBLE && r4_txt.trim().length() <= 0) {
                TextInputLayout[] list = {acr_rate1, acr_rate2, acr_rate3};
                ArrayList<TextInputLayout> arr = new ArrayList<>();
                arr.addAll(Arrays.asList(list));
                updateCompanyRateEdittextErrorEnabled(acr_rate4, arr, empty_msg);
            } else if (acr_rate1.getVisibility() == View.VISIBLE && Double.parseDouble(r1_txt) <= 0.0) {
                TextInputLayout[] list = {acr_rate2, acr_rate3, acr_rate4};
                ArrayList<TextInputLayout> arr = new ArrayList<>();
                arr.addAll(Arrays.asList(list));
                updateCompanyRateEdittextErrorEnabled(acr_rate1, arr, nozero_msg);
            } else if (acr_rate2.getVisibility() == View.VISIBLE && Double.parseDouble(r2_txt) <= 0.0) {
                TextInputLayout[] list = {acr_rate1, acr_rate3, acr_rate4};
                ArrayList<TextInputLayout> arr = new ArrayList<>();
                arr.addAll(Arrays.asList(list));
                updateCompanyRateEdittextErrorEnabled(acr_rate2, arr, nozero_msg);
            } else if (acr_rate3.getVisibility() == View.VISIBLE && Double.parseDouble(r3_txt) <= 0.0) {
                TextInputLayout[] list = {acr_rate1, acr_rate2, acr_rate4};
                ArrayList<TextInputLayout> arr = new ArrayList<>();
                arr.addAll(Arrays.asList(list));
                updateCompanyRateEdittextErrorEnabled(acr_rate3, arr, nozero_msg);
            } else if (acr_rate4.getVisibility() == View.VISIBLE && Double.parseDouble(r4_txt) <= 0.0) {
                TextInputLayout[] list = {acr_rate1, acr_rate2, acr_rate3};
                ArrayList<TextInputLayout> arr = new ArrayList<>();
                arr.addAll(Arrays.asList(list));
                updateCompanyRateEdittextErrorEnabled(acr_rate4, arr, nozero_msg);
            } else {
                dialog.dismiss();
                Dialog dialog1 = functions.createDialogBox(AnnualReportActivity.this, R.layout.loading_dialog, false);
                dialog1.show();
                Map<String, Object> data = new HashMap<>();
                if (acr_rate1.getVisibility() == View.VISIBLE) {
                    data.put("apr_rate", Long.parseLong(r1_txt));
                    data.put("may_rate", Long.parseLong(r1_txt));
                    data.put("jun_rate", Long.parseLong(r1_txt));
                }
                if (acr_rate2.getVisibility() == View.VISIBLE) {
                    data.put("jul_rate", Long.parseLong(r2_txt));
                    data.put("aug_rate", Long.parseLong(r2_txt));
                    data.put("sep_rate", Long.parseLong(r2_txt));
                }
                if (acr_rate3.getVisibility() == View.VISIBLE) {
                    data.put("oct_rate", Long.parseLong(r3_txt));
                    data.put("nov_rate", Long.parseLong(r3_txt));
                    data.put("dec_rate", Long.parseLong(r3_txt));
                }
                if (acr_rate4.getVisibility() == View.VISIBLE) {
                    data.put("jan_rate", Long.parseLong(r4_txt));
                    data.put("feb_rate", Long.parseLong(r4_txt));
                    data.put("mar_rate", Long.parseLong(r4_txt));
                }
                if (isadd) {
                    data.put("company_reference", db.document(comp_path));
                    data.put("financial_year", financial_year);
                }

                Task<Void> addGovtBaseRateTask = isadd ? db.collection("GovtBaseRate").document().set(data) :
                        db.collection("GovtBaseRate").document(doc.getId()).update(data);

                addGovtBaseRateTask
                        .addOnCompleteListener(task1 -> {
                            Log.e("firebase_error", "Entering....");
                            if (task1.isSuccessful()) {
                                Snackbar.make(annualreport_mainlay, "Govornment Base Rate Added Successfully.", Snackbar.LENGTH_SHORT).show();
                                dialog1.dismiss();
                                getTheMonthsAndCallLoad();
                            } else {
                                dialog1.cancel();
                                String msg = task1.getException().getLocalizedMessage() != null ? task1.getException().getLocalizedMessage() : "No Error !!";
                                Log.e("firebase_error", msg);
                                Snackbar.make(annualreport_mainlay, "Unable to add the Govornment Base Rate !! Please try again", Snackbar.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }

    private void updateCompanyRateEdittextErrorEnabled(@NonNull TextInputLayout t, @NonNull ArrayList<TextInputLayout> arr, String msg) {
        t.setErrorEnabled(true);
        t.setError(msg);
        t.requestFocus();
        for (TextInputLayout item : arr) {
            item.setErrorEnabled(false);
            item.clearFocus();
            item.setError(null);
        }
    }

    private void setVisibilityOfTextInputLayout(String m1, String m2, TextInputLayout r, int curr_month, ArrayList<String> notadded_rates) {
        if (functions.getMonthList().indexOf(m1) <= curr_month && notadded_rates.contains(m1)) {
            r.setVisibility(View.VISIBLE);
            if (!(functions.getMonthList().indexOf(m2) <= curr_month && notadded_rates.contains(m2))) {
                r.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        } else r.setVisibility(View.GONE);
    }

}