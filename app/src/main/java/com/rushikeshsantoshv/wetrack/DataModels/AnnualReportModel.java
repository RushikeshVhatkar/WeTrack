package com.rushikeshsantoshv.wetrack.DataModels;

public class AnnualReportModel {
    private String currMonth;
    private int daysWorked, fullPresentCount, halfPresentCount;
    private double govtBaseRate, compBaseRate, compWagesPaid, compWagesTotal, wageAdvancePaid;
    private double advanceTaken, advancePaid, prevLoanBalance, loanBalance, additionalArrears;

    public AnnualReportModel() {
    }

    public AnnualReportModel(String currMonth, int daysWorked, int fullPresentCount,
                             int halfPresentCount, double govtBaseRate,
                             double compBaseRate, double compWagesPaid,
                             double compWagesTotal, double wageAdvancePaid,
                             double advanceTaken, double advancePaid,
                             double prevLoanBalance, double loanBalance, double additionalArrears) {
        this.currMonth = currMonth;
        this.daysWorked = daysWorked;
        this.fullPresentCount = fullPresentCount;
        this.halfPresentCount = halfPresentCount;
        this.govtBaseRate = govtBaseRate;
        this.compBaseRate = compBaseRate;
        this.compWagesPaid = compWagesPaid;
        this.compWagesTotal= compWagesTotal;
        this.wageAdvancePaid= wageAdvancePaid;
        this.advanceTaken= advanceTaken;
        this.advancePaid = advancePaid;
        this.prevLoanBalance = prevLoanBalance;
        this.loanBalance= loanBalance;
        this.additionalArrears = additionalArrears;
    }

    public String getCurrMonth() {
        return currMonth;
    }

    public void setCurrMonth(String currMonth) {
        this.currMonth = currMonth;
    }

    public int getDaysWorked() {
        return daysWorked;
    }

    public void setDaysWorked(int daysWorked) {
        this.daysWorked = daysWorked;
    }

    public int getFullPresentCount() {
        return fullPresentCount;
    }

    public void setFullPresentCount(int fullPresentCount) {
        this.fullPresentCount = fullPresentCount;
    }

    public int getHalfPresentCount() {
        return halfPresentCount;
    }

    public void setHalfPresentCount(int halfPresentCount) {
        this.halfPresentCount = halfPresentCount;
    }

    public double getGovtBaseRate() {
        return govtBaseRate;
    }

    public void setGovtBaseRate(double govtBaseRate) {
        this.govtBaseRate = govtBaseRate;
    }

    public double getCompBaseRate() {
        return compBaseRate;
    }

    public void setCompBaseRate(double compBaseRate) {
        this.compBaseRate = compBaseRate;
    }

    public double getCompWagesPaid() {
        return compWagesPaid;
    }

    public void setCompWagesPaid(double compWagesPaid) {
        this.compWagesPaid = compWagesPaid;
    }

    public double getCompWagesTotal() {
        return compWagesTotal;
    }

    public void setCompWagesTotal(double compWagesTotal) {
        this.compWagesTotal = compWagesTotal;
    }

    public double getWageAdvancePaid() {
        return wageAdvancePaid;
    }

    public void setWageAdvancePaid(double wageAdvancePaid) {
        this.wageAdvancePaid = wageAdvancePaid;
    }

    public double getAdvanceTaken() {
        return advanceTaken;
    }

    public void setAdvanceTaken(double advanceTaken) {
        this.advanceTaken = advanceTaken;
    }

    public double getAdvancePaid() {
        return advancePaid;
    }

    public void setAdvancePaid(double advancePaid) {
        this.advancePaid = advancePaid;
    }

    public double getPrevLoanBalance() {
        return prevLoanBalance;
    }

    public void setPrevLoanBalance(double prevLoanBalance) {
        this.prevLoanBalance = prevLoanBalance;
    }

    public double getLoanBalance() {
        return loanBalance;
    }

    public void setLoanBalance(double loanBalance) {
        this.loanBalance = loanBalance;
    }

    public double getAdditionalArrears() {
        return additionalArrears;
    }

    public void setAdditionalArrears(double additionalArrears) {
        this.additionalArrears = additionalArrears;
    }
}
